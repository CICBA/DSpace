package ar.edu.unlp.sedici.dspace.ctask.general;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.content.authority.factory.ContentAuthorityServiceFactory;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.authority.service.MetadataAuthorityService;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import java.io.IOException;
import java.util.List;

public class MetadataAuthorityQualityControl extends AbstractCurationTask {

	private MetadataAuthorityService authService = ContentAuthorityServiceFactory.getInstance()
			.getMetadataAuthorityService();
	private ChoiceAuthorityService choiceAuthorityService = ContentAuthorityServiceFactory.getInstance()
			.getChoiceAuthorityService();

	/**
	 * Configuration property. If true, curation task fixes the metadata errors by
	 * itself. If false, only reports.
	 */
	private boolean fixmode = false;

	/**
	 * Configuration property, only works with fixmode in true. If true, curation
	 * task also fixes metadata value variants.
	 */
	private boolean fixvariants = false;

	/**
	 * Configuration property. List of metadata_fields which are authority
	 * controlled but we don't want the curation task process them
	 */
	private String[] metadataToSkip;

	@Override
	public void init(Curator curator, String taskId) throws IOException {
		super.init(curator, taskId);
		fixmode = taskBooleanProperty("fixmode", false);
		if (fixmode) {
			fixvariants = taskBooleanProperty("fixvariants", false);
		}
		metadataToSkip = this.taskArrayProperty("skipmetadata");
	}

	@Override
	public int perform(DSpaceObject dso) throws IOException {
		int status = Curator.CURATE_UNSET;
		StringBuilder reporter = new StringBuilder();
		if (dso instanceof Item) {
			Item item = (Item) dso;
			reporter.append("####################\n");
			reporter.append("Checking item with handle ").append(item.getHandle()).append(" and item id ")
					.append(item.getID()).append("\n");
			List<MetadataValue> mValues = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
			for (MetadataValue mv : mValues) {
				if (authService.isAuthorityControlled(mv.getMetadataField()) && dontSkip(mv.getMetadataField())) {
					checkMetadataAuthority(reporter, mv, item);
				}
			}
			reporter.append("####################");
			report(reporter.toString());
			status = Curator.CURATE_SUCCESS;
		} else {
			status = Curator.CURATE_SKIP;
		}
		setResult(reporter.toString());
		return status;
	}

	private void checkMetadataAuthority(StringBuilder reporter, MetadataValue mv, Item item) {

		if (mv.getAuthority() == null && mv.getValue() == null) {
			report(reporter, mv, "ERROR", "Null both authority key and text_value");
		} else if (mv.getAuthority() == null) {
			checkMetadataWithoutAuthorityKey(reporter, mv, item);
		} else {
			checkMetadataWithAuthorityKey(reporter, mv);
		}
	}

	/**
	 * Reconciles metadata value with authority
	 */
	private void checkMetadataWithoutAuthorityKey(StringBuilder reporter, MetadataValue mv, Item item) {

		Choices choices = choiceAuthorityService.getBestMatch(mv.getMetadataField().toString(), mv.getValue(),
				item.getOwningCollection(), mv.getLanguage());
		if (authService.isAuthorityRequired(mv.getMetadataField())) {
			report(reporter, mv, "ERROR", "Missing authority key for <<", mv.getValue(), ">>");
		} else {
			report(reporter, mv, "INFO", "Missing optional authority key for <<", mv.getValue(), ">>");
		}
		if (choices.values.length > 0) {
			String newAuthority = choices.values[0].authority;
			String value = choices.values[0].value;
			if (compare(value, mv.getValue())) {
				saveAuthorityKey(reporter, mv, newAuthority, choices);
			} else {
				// do not fix because can be either a false positive or variant
				report(reporter, mv, "INFO", "Recommended value <<", newAuthority, ">> with confidence ",
						String.valueOf(choices.confidence));
			}
		} else {
			assertConfidenceNotFound(reporter, mv);
		}

	}

	private void checkMetadataWithAuthorityKey(StringBuilder reporter, MetadataValue mv) {
		String value = choiceAuthorityService.getLabel(mv.getMetadataField().toString(), mv.getAuthority(),
				mv.getLanguage());
		if (value == null || value.isEmpty()) {
			// Authority not found
			report(reporter, mv, "ERROR", "Authority key <<", mv.getAuthority(), ">> not found");
			assertConfidenceNotFound(reporter, mv);
		} else if (compare(value, mv.getValue())) {
			// Authority found, value==text_value
			assertConfidenceUncertain(reporter, mv);
		} else {
			// Authority found, but value!=text_value
			saveVariant(reporter, mv, value);
		}
	}

	private void assertConfidenceUncertain(StringBuilder reporter, MetadataValue mv) {
		if (mv.getConfidence() < Choices.CF_UNCERTAIN) {
			report(reporter, mv, "ERROR", ": Invalid confidence ", String.valueOf(mv.getConfidence()), ", expected ",
					String.valueOf(Choices.CF_UNCERTAIN));
			if (fixmode) {
				mv.setConfidence(Choices.CF_UNCERTAIN);
				report(reporter, mv, "FIXED", "[CONFIDENCE] confidence replaced with value ",
						String.valueOf(Choices.CF_UNCERTAIN));
			}
		}
	}

	private void assertConfidenceNotFound(StringBuilder reporter, MetadataValue mv) {
		if (mv.getConfidence() > Choices.CF_NOTFOUND) {
			report(reporter, mv, "ERROR", "Invalid confidence ", String.valueOf(mv.getConfidence()), ", expected ",
					String.valueOf(Choices.CF_NOTFOUND));
			if (fixmode) {
				mv.setConfidence(Choices.CF_NOTFOUND);
				report(reporter, mv, "FIXED", "[CONFIDENCE] confidence replaced with value ",
						String.valueOf(Choices.CF_NOTFOUND));
			}
		}
	}

	private void saveVariant(StringBuilder reporter, MetadataValue mv, String value) {
		report(reporter, mv, "WARN", "text_value and value do not match for authority <<", mv.getAuthority(),
				">>. Metadata text_value <<", mv.getValue(), ">>. Authority value <<", value, ">>");
		if (fixvariants) {
			mv.setValue(value);
			mv.setConfidence(Choices.CF_UNCERTAIN);
			report(reporter, mv, "FIXED", "[VARIANT] text_value replaced with authority's value.");
		}

	}

	private void saveAuthorityKey(StringBuilder reporter, MetadataValue mv, String newAuthority, Choices choices) {
		report(reporter, mv, "INFO", " Found Authority <<", newAuthority, ">> for value <<", mv.getValue(), ">>.");
		if (fixmode && choices.confidence >= Choices.CF_UNCERTAIN) {
			mv.setAuthority(newAuthority);
			mv.setConfidence(choices.confidence);
			report(reporter, mv, "FIXED", "[AUTHORITY] Authority set with value <<", newAuthority, ">>");
		} else if (choices.confidence < Choices.CF_UNCERTAIN) {
			report(reporter, mv, "NOT FIXED", "[AMBIGUOUS AUTHORITY] Authority key <<", newAuthority,
					">> is ambiguous for text_value");
		}

	}

	private void report(StringBuilder reporter, MetadataValue value, String level, String... messages) {
		reporter.append("- ").append(level).append(" (").append(value.getID()).append(",")
				.append(value.getMetadataField()).append(") : ");
		for (String message : messages) {
			reporter.append(message);
		}
		reporter.append("\n");

	}

	/**
	 * Method used to compare 2 strings, considering several criteria
	 *
	 * @param value1
	 * @param value2
	 * @return true if value1.equals(value2) after some functions applied to both
	 *         strings
	 */
	private boolean compare(String value1, String value2) {
		String customValue1 = value1.trim();
		String customValue2 = value2.trim();
		return customValue1.equalsIgnoreCase(customValue2);
	}

	/**
	 * Check if current metadata must be processed or not.
	 *
	 * @param mf metadata field of the current metadata being processed
	 * @return true if metadataToSkip array doesn't contain mf
	 */
	private boolean dontSkip(MetadataField mf) {
		for (String dataToSkip : metadataToSkip) {
			if (mf.toString().equals(dataToSkip)) {
				return false;
			}
		}
		return true;
	}

}
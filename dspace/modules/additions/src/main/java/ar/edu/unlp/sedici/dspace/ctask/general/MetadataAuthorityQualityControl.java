package ar.edu.unlp.sedici.dspace.ctask.general;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
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

	private Item item;

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

	@Override
	public void init(Curator curator, String taskId) throws IOException {
		super.init(curator, taskId);
		fixmode = taskBooleanProperty("fixmode", false);
		if (fixmode) {
			fixvariants = taskBooleanProperty("fixvariants", false);
		}
	}

	@Override
	public int perform(DSpaceObject dso) throws IOException {
		int status = Curator.CURATE_UNSET;
		StringBuilder reporter = new StringBuilder();
		if (dso instanceof Item) {
			item = (Item) dso;
			reporter.append("####################\n");
			reporter.append("Checking item with handle ").append(item.getHandle()).append(" and item id ")
					.append(item.getID()).append("\n");
			List<MetadataValue> values = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
			for (MetadataValue value : values) {
				if (authService.isAuthorityControlled(value.getMetadataField())) {
					checkMetadataAuthority(reporter, value);
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

	private void checkMetadataAuthority(StringBuilder reporter, MetadataValue mv) {

		if (mv.getAuthority() == null && mv.getValue() == null) {
			report(reporter, mv, "ERROR", "Null both authority key and label");
		} else if (mv.getAuthority() == null) {
			checkMetadataWithoutAuthorityKey(reporter, mv);
		} else {
			checkMetadataWithAuthorityKey(reporter, mv);
		}
	}

	/**
	 * Reconciles metadata value with authority
	 */
	private void checkMetadataWithoutAuthorityKey(StringBuilder reporter, MetadataValue mv) {

		Choices choices = choiceAuthorityService.getBestMatch(mv.getMetadataField().toString(), mv.getValue(),
				item.getOwningCollection(), mv.getLanguage());
		if (authService.isAuthorityRequired(mv.getMetadataField())) {
			report(reporter, mv, "ERROR", "Missing authority key for <<", mv.getValue(), ">>");
		} else {
			report(reporter, mv, "INFO", "Missing optional authority key for <<", mv.getValue(), ">>");
		}
		if (choices.values.length > 0) {
			String newAuthority = choices.values[0].authority;
			String label = choices.values[0].label;
			//Should implement a better string.equals method here, with at least a trim to both strings
			if (label.equals(mv.getValue())) {
				saveAuthorityKey(reporter, mv, newAuthority, choices);
			} else {
				// do not fix because can be either a false positive or variant
				report(reporter, mv, "INFO", "Recommended value <<", newAuthority,
						">> with confidence ", String.valueOf(choices.confidence));
			}
		} else {
			assertConfidenceNotFound(reporter, mv);
		}

	}

	private void checkMetadataWithAuthorityKey(StringBuilder reporter, MetadataValue mv) {
		String label = choiceAuthorityService.getLabel(mv.getMetadataField().toString(), mv.getAuthority(),
				mv.getLanguage());
		if (label == null || label.isEmpty()) {
			// Authority not found
			report(reporter, mv, "ERROR", "Authority <<", mv.getAuthority(), ">> not found");
			assertConfidenceNotFound(reporter, mv);
			//Should implement a better string.equals method here, with at least a trim to both strings
		} else if (label.equals(mv.getValue())) {
			// Authority found, label==value
			assertConfidenceUncertain(reporter, mv);
		} else {
			// Authority found, but label!=value
			saveVariant(reporter, mv, label);
		}
	}

	private void assertConfidenceUncertain(StringBuilder reporter, MetadataValue mv) {
		if (mv.getConfidence() < Choices.CF_UNCERTAIN) {
			report(reporter, mv, "ERROR",
					": Wrong confidence: ", String.valueOf(mv.getConfidence()), ". Expected: ", String.valueOf(Choices.CF_UNCERTAIN));
			if (fixmode) {
				mv.setConfidence(Choices.CF_UNCERTAIN);
				reporter.append("FIXED \n");
			}
		}
	}

	private void assertConfidenceNotFound(StringBuilder reporter, MetadataValue mv) {
		if (mv.getConfidence() > Choices.CF_NOTFOUND) {
			report(reporter, mv, "ERROR", "Wrong confidence: ",
					String.valueOf(mv.getConfidence()), ". Expected: ", String.valueOf(Choices.CF_NOTFOUND));
			if (fixmode) {
				mv.setConfidence(Choices.CF_NOTFOUND);
				reporter.append("FIXED \n");
			}
		}
	}

	private void saveVariant(StringBuilder reporter, MetadataValue mv, String label) {
		report(reporter, mv, "WARN", "Variant, label and authority do not match. Metadata value <<", mv.getValue(),
				">>. Authority label <<", label, ">>");
		if (fixvariants) {
			mv.setValue(label);
			mv.setConfidence(Choices.CF_UNCERTAIN);
			reporter.append("VARIANT FIXED \n");
		}

	}

	private void saveAuthorityKey(StringBuilder reporter, MetadataValue mv, String newAuthority, Choices choices) {
		report(reporter, mv, "INFO", " Found Authority <<", newAuthority, ">> for value <<", mv.getValue(), ">>");
		if (fixmode && choices.confidence >= Choices.CF_UNCERTAIN) {
			mv.setAuthority(newAuthority);
			mv.setConfidence(choices.confidence);
			reporter.append("FIXED \n ");
		} else if (choices.confidence < Choices.CF_UNCERTAIN) {
			reporter.append("Ambiguous authority");
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

}
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
	private String metadataInfo;

	private MetadataAuthorityService metadataAuthorityService = ContentAuthorityServiceFactory.getInstance()
			.getMetadataAuthorityService();
	private ChoiceAuthorityService choiceAuthorityService = ContentAuthorityServiceFactory.getInstance()
			.getChoiceAuthorityService();

	/**
	 * Configuration property.
	 * If true, curation task fixes the metadata errors by itself.
	 * If false, only reports.
	 */
	private boolean fixmode = false;

	/**
	 * Configuration property, only works with fixmode in true.
	 * If true, curation task also fixes metadata value variants.
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
		StringBuilder resultReport = new StringBuilder();
		if (dso instanceof Item) {
			item = (Item) dso;
			resultReport.append("####################\n");
			resultReport.append("Checking item with handle ").append(item.getHandle()).append(" and item id ")
					.append(item.getID()).append(" \n");
			List<MetadataValue> values = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
			for (MetadataValue value : values) {
				if (metadataAuthorityService.isAuthorityControlled(value.getMetadataField())) {
					metadataInfo = "(" + value.getID() + "," + value.getMetadataField().toString() + ")";
					if (value.getAuthority() == null) {
						if (value.getValue() == null) {
							resultReport.append("- ERROR ").append(metadataInfo)
									.append(": Null both authority key and label \n");
						} else if (metadataAuthorityService.isAuthorityRequired(value.getMetadataField())) {
							checkMetadataAuthority(value, resultReport);
						} else {
							checkConfidenceWithoutAuthority(value, resultReport);
						}
					} else {
						checkMetadataValue(value, resultReport);
					}
				}
			}
			resultReport.append("####################\n");
			report(resultReport.toString());
			status = Curator.CURATE_SUCCESS;
		} else {
			status = Curator.CURATE_SKIP;
		}
		setResult(resultReport.toString());
		return status;
	}

	private void checkMetadataAuthority(MetadataValue value, StringBuilder resultReport) {
		Choices choices = choiceAuthorityService.getBestMatch(value.getMetadataField().toString(), value.getValue(),
				item.getOwningCollection(), null);
		resultReport.append("- ERROR ").append(metadataInfo).append(": Null and required Authority key.");
		if (choices.values.length > 0) {
			String newAuthority = choices.values[0].authority;
			String label = choices.values[0].label;
			if (label.equals(value.getValue())) {
				resultReport.append(" Expected <<").append(newAuthority).append(">>\n");
				if (fixmode) {
					value.setAuthority(newAuthority);
					value.setConfidence(choices.confidence);
					resultReport.append("FIXED \n ");
				}
			} else {
				resultReport.append("Recommended value <<").append(newAuthority).append(">>\n");
			}
		} else {
			resultReport.append(" No reasonable value found \n");
			if (value.getConfidence() != Choices.CF_NOTFOUND) {
				resultReport.append("- ERROR ").append(metadataInfo).append(": Wrong confidence: ")
						.append(value.getConfidence()).append(". Expected: ").append(Choices.CF_NOTFOUND).append("\n");
				if (fixmode) {
					value.setConfidence(Choices.CF_NOTFOUND);
					resultReport.append("FIXED \n");
				}
			}
		}
	}

	private void checkMetadataValue(MetadataValue value, StringBuilder resultReport) {
		String label = choiceAuthorityService.getLabel(value.getMetadataField().toString(), value.getAuthority(), null);
		if (label == null || label.isEmpty()) {
			resultReport.append("- ERROR ").append(metadataInfo).append(": Authority not found <<")
					.append(value.getAuthority()).append(">>\n");
			if (value.getConfidence() != Choices.CF_NOTFOUND) {
				resultReport.append("- ERROR ").append(metadataInfo).append(": Wrong confidence: ")
						.append(value.getConfidence()).append(". Expected: ").append(Choices.CF_NOTFOUND).append("\n");
				if (fixmode) {
					value.setConfidence(Choices.CF_NOTFOUND);
					resultReport.append("FIXED \n");
				}
			}
		} else if (!label.equals(value.getValue())) {
			resultReport.append("- WARN ").append(metadataInfo).append(": Variant, label and authority do not match.")
					.append(" Metadata value <<").append(value.getValue()).append(">>. Authority label <<")
					.append(label).append(">>\n");
			if (fixvariants) {
				value.setValue(label);
				value.setConfidence(Choices.CF_UNCERTAIN);
				resultReport.append("VARIANT FIXED \n");
			}
		} else if (value.getConfidence() < Choices.CF_UNCERTAIN) {
			resultReport.append("- ERROR ").append(metadataInfo).append(": Wrong confidence: ")
					.append(value.getConfidence()).append(". Expected: ").append(Choices.CF_UNCERTAIN).append("\n");
			if (fixmode) {
				value.setConfidence(Choices.CF_UNCERTAIN);
				resultReport.append("FIXED \n");
			}
		}
	}

	private void checkConfidenceWithoutAuthority(MetadataValue value, StringBuilder resultReport) {
		resultReport.append("- WARN ").append(metadataInfo).append(": Null but optional authority key \n");
		if (value.getConfidence() > Choices.CF_NOVALUE) {
			resultReport.append("- ERROR ").append(metadataInfo).append(": Wrong confidence: ")
					.append(value.getConfidence()).append(". Expected: ").append(Choices.CF_NOVALUE).append("\n");
			if (fixmode) {
				value.setConfidence(Choices.CF_NOVALUE);
				resultReport.append("FIXED \n");
			}
		}
	}
}
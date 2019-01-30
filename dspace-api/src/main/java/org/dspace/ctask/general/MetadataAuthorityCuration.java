	package org.dspace.ctask.general;

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

public class MetadataAuthorityCuration extends AbstractCurationTask
	{

	    protected int status = Curator.CURATE_UNSET;
	    private Item item;
	    protected String result = null;
        protected MetadataAuthorityService metadataAuthorityService = ContentAuthorityServiceFactory.getInstance().getMetadataAuthorityService();
        protected ChoiceAuthorityService choiceAuthorityService = ContentAuthorityServiceFactory.getInstance().getChoiceAuthorityService();

	    @Override
	    public int perform(DSpaceObject dso) throws IOException
	    {

	        if (dso instanceof Item){
	            item = (Item)dso;
	            List<MetadataValue> values = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
	            for (MetadataValue value : values) {
	                if(metadataAuthorityService.isAuthorityRequired(value.getMetadataField())){
	                    if (value.getAuthority() == null) {
	                        updateMetadataAuthority(value);
	                    }
	                    else {
	                        updateMetadataValue(value);
	                    }
	                }
	            }
	            status = Curator.CURATE_SUCCESS;
	            result = "#" + item.getHandle()+"#";
	            setResult(result);
	            report(result);
	        }
	        else
	            status = Curator.CURATE_SKIP;

	        return status;
	    }

	    private void updateMetadataAuthority(MetadataValue value) {
	        Choices choices = choiceAuthorityService.getBestMatch(value.getMetadataField().toString(), value.getValue(),item.getOwningCollection() ,null);
	        if (choices.total > 0) {
	            value.setAuthority(choices.values[0].authority);
	            if (value.getConfidence() != Choices.CF_ACCEPTED)
	                value.setConfidence(Choices.CF_ACCEPTED);
	        }
	        else {
	            if (value.getConfidence() > Choices.CF_NOVALUE)
	                value.setConfidence(Choices.CF_NOVALUE);
	        }
	    }

	    private void updateMetadataValue(MetadataValue value) {
	        String label = choiceAuthorityService.getLabel(value.getMetadataField().toString(), value.getAuthority(),null);
	        if (!label.isEmpty()) {
	            if (!label.equals(value.getValue())){
	                value.setValue(label);
	            }
	            if (value.getConfidence() != Choices.CF_ACCEPTED)
	                value.setConfidence(Choices.CF_ACCEPTED);
	        }
	    }

	}


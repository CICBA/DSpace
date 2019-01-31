	package org.dspace.ctask.general;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.content.authority.factory.ContentAuthorityServiceFactory;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.authority.service.MetadataAuthorityService;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import java.io.IOException;
import java.util.List;

public class MetadataAuthorityCuration extends AbstractCurationTask {

	    protected int status = Curator.CURATE_UNSET;
	    private Item item;
	    protected String result = null;
        protected MetadataAuthorityService metadataAuthorityService = ContentAuthorityServiceFactory.getInstance().getMetadataAuthorityService();
        protected ChoiceAuthorityService choiceAuthorityService = ContentAuthorityServiceFactory.getInstance().getChoiceAuthorityService();
        
        protected final String PLUGIN_PREFIX = "metadataauthority";
        protected boolean fixmode = false;
        protected boolean fixvariants = false;

        @Override
        public void init(Curator curator, String taskId) throws IOException
        {
            super.init(curator, taskId);
            fixmode = configurationService.getBooleanProperty(PLUGIN_PREFIX + ".fix");
            if (fixmode)
            	fixvariants = configurationService.getBooleanProperty(PLUGIN_PREFIX + ".fixvariants");
        }
        
	    @Override
	    public int perform(DSpaceObject dso) throws IOException
	    {

	        if (dso instanceof Item){
	            item = (Item)dso;
	            List<MetadataValue> values = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
	            for (MetadataValue value : values) {
	                if(metadataAuthorityService.isAuthorityControlled(value.getMetadataField())){
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
	        Choices choices = choiceAuthorityService.getBestMatch(value.getMetadataField().toString(),value.getValue(),item.getOwningCollection() ,null);
	        if (choices.total > 0) {
	        	String newAuthority = choices.values[0].authority;
		        String label = choices.values[0].label;
	        	if (label.equals(value.getValue())) {
	        		//reportar
	        		if (fixmode) {
	        			value.setAuthority(newAuthority);
	            		value.setConfidence(choices.confidence);
	        		}
	        	}
	        	//si el label es distinto -> reportar
	        }
	        else {
	            if (value.getConfidence() != Choices.CF_NOTFOUND)
	            	//reportar
	            	if (fixmode)
	            		value.setConfidence(Choices.CF_NOTFOUND);
	        }
	    }

	    private void updateMetadataValue(MetadataValue value) {
	        String label = choiceAuthorityService.getLabel(value.getMetadataField().toString(), value.getAuthority(),null);
	       	if (label == null || label.isEmpty()) {
	       		//reportar
	       		if (fixmode)
	       			value.setConfidence(Choices.CF_NOTFOUND);
	       	}
	       	else if (!label.equals(value.getValue())){
	       			//reportar
	        		if (fixvariants) {
	        		   value.setValue(label);
        			   value.setConfidence(Choices.CF_UNCERTAIN);
	        		}
	        }
	        else if (value.getConfidence() < Choices.CF_UNCERTAIN) {
	           		//reportar
	           		if (fixmode)
	        		   value.setConfidence(Choices.CF_UNCERTAIN);
	           	}
	    }
	        
}
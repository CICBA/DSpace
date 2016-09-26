package org.dspace.app.xmlui.aspect.ELProcessor;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

public class ExecutePreConfigQuery extends AbstractAction{

	private static final String propertyPrefix= "el.processor.query.";
	private static ConfigurationService configService=DSpaceServicesFactory.getInstance().getConfigurationService();
	private static MainProcessor mainProcessor;
	private Context context;
	
	@Override
	public Map act(Redirector redirector, SourceResolver resolver, Map objectModel,
            String source, Parameters parameters) throws Exception {
		
        this.setContext(ContextUtil.obtainContext(objectModel));

		Request request = ObjectModelHelper.getRequest(objectModel);
		String property=request.getParameter("propertyName");
		//as it's optional, handle might be null
		String handle=request.getParameter("handle");
		//TODO puede que no exista la property que se trata de levantar, esto devolveria null
		//hay que manearlo y mostrar mensaje de error
		String query=configService.getProperty(propertyPrefix+property);
		
		if(handle != "" & handle != null){
			query += "handle";
		}
		
		this.instanciateMainProcessor().process(query,handle,this.getContext());
		return null;
	}

	public  MainProcessor instanciateMainProcessor() {
		if(mainProcessor==null){
			mainProcessor=new MainProcessor();
		}
		return mainProcessor;
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}	

	
}

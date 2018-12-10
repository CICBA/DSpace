package ar.unlp.sedici.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ClearCacheAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.core.Context;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

import java.util.Map;

/**
 * Simple action which ensures the cache is cleared of all
 * cached results with security checks
 */

public class ClearCacheByGroupAction extends ClearCacheAction {

    public Map act(Redirector redirector,
                    SourceResolver resolver,
                    Map objectModel,
                    String src,
                    Parameters par
    ) throws Exception {
        Context context = ContextUtil.obtainContext(objectModel);
        GroupService groupService = EPersonServiceFactory.getInstance().getGroupService();
        AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
        ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
        String[] clearCacheGroups = configurationService.getArrayProperty("xmlui.clearCacheGroups");

        for (int i=0; i < clearCacheGroups.length; i++) {
            if (authorizeService.isAdmin(context) || groupService.isMember(context,clearCacheGroups[i]))
                return super.act(redirector, resolver, objectModel, src, par);
        }
        return null;
    }
}

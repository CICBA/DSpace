package org.dspace.xmlworkflow.state.actions.processingaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.xmlworkflow.factory.XmlWorkflowServiceFactory;
import org.dspace.xmlworkflow.state.Step;
import org.dspace.xmlworkflow.state.actions.ActionResult;
import org.dspace.xmlworkflow.storedcomponents.ClaimedTask;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;

public class ChangeCollectionAction extends ProcessingAction {

	@Override
	public void activate(Context c, XmlWorkflowItem wf) throws SQLException,
			IOException, AuthorizeException {
	}

	@Override
	public ActionResult execute(Context c, XmlWorkflowItem wfi, Step step,HttpServletRequest request) throws SQLException,AuthorizeException, IOException {
		if(request.getParameter("submit") != null){
            String collectionHandle = request.getParameter("collection_handle");
            if(collectionHandle == null || collectionHandle == "")
            	return new ActionResult(ActionResult.TYPE.TYPE_ERROR);
            
            DSpaceObject dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(c, collectionHandle);
            if(!(dso instanceof Collection))
            	return new ActionResult(ActionResult.TYPE.TYPE_ERROR);
            
            //first update item
            wfi.setCollection((Collection) dso);
            XmlWorkflowServiceFactory.getInstance().getWorkflowItemService().update(c,wfi);
            
            //then deletes claimed task
            ClaimedTask oldClaimedTask = XmlWorkflowServiceFactory.getInstance().getClaimedTaskService().findByWorkflowIdAndEPerson(c, wfi, c.getCurrentUser());
            XmlWorkflowServiceFactory.getInstance().getXmlWorkflowService().deleteClaimedTask(c, wfi, oldClaimedTask);
            return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
        } else {
            //We pressed the "cancel" button so return to our submissions page
            return new ActionResult(ActionResult.TYPE.TYPE_CANCEL);
        }
	}

	@Override
	public List<String> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}

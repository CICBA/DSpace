function confirm_workflow_delete (confirm_message){
	delete_button=$("#aspect_xmlworkflow_admin_WorkflowOverviewTransformer_field_submit_delete");
	delete_button.click(function() {
  		return confirm(confirm_message);
    });
	
};
package workflow;

import inventory.*;
import utility.ModuleControlConstant;
import utility.Utility;

import java.util.List;
import java.util.Map;

public class CreateWorkflowData {

    private void createTworkflow() {
        if (ModuleControlConstant.WORKFLOW_MIGRATION) {
            TicketWorkflow ticketWorkflow = new TicketWorkflow();
            List<Map<String, String>> workflowList = ticketWorkflow.readWorkflowList();
            ticketWorkflow.createTworkflow(workflowList);
        }
    }

    public void generateWorkflowData() {
        System.out.println("Started to generate Workflow Data ...!");
        Utility.printLog("execution.log", "WorkflowData", "Started Generting Workflow Data...!", "");

        String query = "delete from status where entitytype='Workflow Data'";
        //	DBConnect db = new DBConnect();
        //	db.executeQuery(query);

        createTworkflow();

        System.out.println("Ended to generate Workflow Data ...!");
        Utility.printLog("execution.log", "Workflow Data", "Ended Generting Workflow Data...!", "");
    }
}

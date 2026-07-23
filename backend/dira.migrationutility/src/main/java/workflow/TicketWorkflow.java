package workflow;

import api.ReadData;
import api.RestExecution;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.ProductUtility;
import utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketWorkflow extends RestExecution {

    private static String logFileName = "Staff.log";
    private static String logModuleName = "Team";

    private void createTworkflow(Map<String, String> workflowDetails) {

        String apiURL = getAPIURL("cpm/teamHierarchy/save");
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        String APIBody = getWorkflowJson(workflowDetails);
        Utility.printLog(logFileName, logModuleName, "Request Body", APIBody);

        JSONObject JSONResponseBody = httpPost(apiURL, APIBody);
        String response = JSONResponseBody.toString(4);
        Utility.printLog(logFileName, logModuleName, "Response", response);

        String teamName = workflowDetails.get("Ticket");
        ProductUtility.printResponse(JSONResponseBody, logModuleName, teamName);
    }

    public void createTworkflow(List<Map<String, String>> workflowMapList) {

        for (int i = 0; i < workflowMapList.size(); i++) {
            Map<String, String> map = workflowMapList.get(i);
            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
            createTworkflow(map);
        }
    }

    public List<Map<String, String>> readWorkflowList() {

        String sheetName = "Ticket";
        List<Map<String, String>> sheetMap = new ArrayList<>();
        ReadData readData = new ReadData();
        sheetMap = readData.getWorkflowDataSheet(sheetName);

        List<Map<String, String>> ticketMapList = new ArrayList<>();

        for (int i = 0; i < sheetMap.size(); i++) {
            Map<String, String> cellValue = sheetMap.get(i);
            String TworkflowName = safeTrim(cellValue.get("TworkflowName"));

            if (!"".equals(TworkflowName) && TworkflowName != null) {
                Map<String, String> valuemap = new HashMap<>();

                valuemap.put("RowIndex", safeTrim(cellValue.get("RowIndex")));
                valuemap.put("WorkflowName", safeTrim(cellValue.get("TworkflowName")));
                valuemap.put("event", safeTrim(cellValue.get("Event")));
                valuemap.put("Team", safeTrim(cellValue.get("Team")));
                valuemap.put("Condition", safeTrim(cellValue.get("Condition")));

                valuemap.put("queryField", safeTrim(cellValue.get("Field")));
                valuemap.put("queryOperator", safeTrim(cellValue.get("Operator")));
                valuemap.put("queryValue", safeTrim(cellValue.get("Value")));
                valuemap.put("queryCondition", safeTrim(cellValue.get("QueryCondition")));
                valuemap.put("isAutoAssign", safeTrim(cellValue.get("Assign")));
                valuemap.put("isAutoApprove", safeTrim(cellValue.get("Approve")));

                ticketMapList.add(valuemap);
            }
        }
        return ticketMapList;
    }

    // ------------------ Helpers ------------------
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    // ------------------ JSON Maker ------------------
    private String getWorkflowJson(Map<String, String> workflowDetails) {
        String jsonString;

        try {
            JSONObject mainJson = new JSONObject();

            mainJson.put("hierarchyName", workflowDetails.get("WorkflowName"));
            mainJson.put("eventName", workflowDetails.get("event"));
            mainJson.put("id", JSONObject.NULL);

            // Build teamHierarchyMappingList
            JSONArray teamArray = new JSONArray();
            JSONObject teamObj = new JSONObject();

            teamObj.put("teamId", workflowDetails.get("Team"));
            teamObj.put("teamAction", "");
            teamObj.put("teamcondition", workflowDetails.get("Condition"));

            // Build queryFieldList
            JSONArray queryArray = new JSONArray();
            JSONObject queryObj = new JSONObject();

            queryObj.put("id", JSONObject.NULL);
            queryObj.put("queryField", workflowDetails.get("queryField"));
            queryObj.put("queryOperator", workflowDetails.get("queryOperator"));
            queryObj.put("queryValue", workflowDetails.get("queryValue"));
            queryObj.put("queryCondition", workflowDetails.get("queryCondition"));
            queryObj.put("teamHirMappingId", workflowDetails.get("Team"));

            queryArray.put(queryObj);
            teamObj.put("queryFieldList", queryArray);

            teamObj.put("tat_id", "");
            teamObj.put("isAutoAssign", Boolean.parseBoolean(workflowDetails.get("isAutoAssign")));
            teamObj.put("isAutoApprove", Boolean.parseBoolean(workflowDetails.get("isAutoApprove")));

            teamArray.put(teamObj);

            mainJson.put("teamHierarchyMappingList", teamArray);
            mainJson.put("isAutoAssign", JSONObject.NULL);
            mainJson.put("isAutoApprove", JSONObject.NULL);
            mainJson.put("product", "BSS");

            jsonString = mainJson.toString();

        } catch (Exception e) {
            e.printStackTrace();
            jsonString = null;
        }

        return jsonString;
    }
}

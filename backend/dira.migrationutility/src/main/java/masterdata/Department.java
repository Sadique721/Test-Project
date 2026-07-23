package masterdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import api.ReadData;
import api.RestExecution;
import utility.ProductUtility;
import utility.Utility;

public class Department extends RestExecution {

    private static String logFileName = "masterdata.log";
    private static String logModuleName = "Department";

    public void createDep_Management(Map<String, String> map) {

        String apiURL = getAPIURL("SavbillCommonGateway/department/save");
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        String apiBody = getDepartmentJson(map);
        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

        JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
        String response = JSONResponseBody.toString(4);
        Utility.printLog(logFileName, logModuleName, "Response", response);

        String department = map.get("Department");
        ProductUtility.printResponse(JSONResponseBody, logModuleName, department);
    }

    public void createDep_Management(List<Map<String, String>> departmentMapList) {

        for (int i = 0; i < departmentMapList.size(); i++) {

            Map<String, String> map = new HashMap<String, String>();
            map = departmentMapList.get(i);
            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
            createDep_Management(map);
        }

    }

    public List<Map<String, String>> readDepartmentList() {

        String sheetName = "Department";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getDepartmentManagementDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();

        List<Map<String, String>> departmentMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String department = cellValue.get("Department");

            if ((!"".equals(department)) && (department != null)) {

                valuemap.put("RowIndex", cellValue.get("RowIndex"));
                valuemap.put("Department", cellValue.get("Department"));
                valuemap.put("Plans", cellValue.get("Plans"));
                valuemap.put("Status", cellValue.get("Status"));
                departmentMapList.add(valuemap);
            }
        }
        return departmentMapList;
    }



    private String getDepartmentJson(Map<String, String> department) {

        String jsonString = null;

        try {
           // CommonGetAPI commonGetAPI = new CommonGetAPI();



            JSONObject departmentJsonObject = new JSONObject();


            String departmentName = department.get("Department");
            String plans = department.get("Plans");
            String status = ProductUtility.getStatus(department.get("Status"));

            departmentJsonObject.put("name", departmentName);
            departmentJsonObject.put("plans", plans);
            departmentJsonObject.put("status", status);


            jsonString = departmentJsonObject.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }
        return jsonString;
    }


}
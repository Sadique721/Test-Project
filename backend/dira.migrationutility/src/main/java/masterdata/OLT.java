package masterdata;

import api.ReadData;
import api.RestExecution;
import org.json.JSONObject;
import utility.ProductUtility;
import utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OLT extends RestExecution {

    private static String logFileName = "masterdata.log";
    private static String logModuleName = "OLT";

    public void createOLT(Map<String, String> map) {

        String apiURL = getAPIURL("SavbillCommonGateway/city");
        Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

        String apiBody = getOLTJson(map);
        Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

        JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
        String response = JSONResponseBody.toString(4);
        Utility.printLog(logFileName, logModuleName, "Response", response);

        String OLT = map.get("OLT");
        ProductUtility.printResponse(JSONResponseBody, logModuleName, OLT);
    }

    public void createOLT(List<Map<String, String>> OLTMapList) {

        for (int i = 0; i < OLTMapList.size(); i++) {

            Map<String, String> map = new HashMap<String, String>();
            map = OLTMapList.get(i);
            Utility.printLog(logFileName, logModuleName, "Sheet Data", map.toString());
            createOLT(map);
        }

    }

    public List<Map<String, String>> readOLTList() {

        String sheetName = "OLT";
        List<Map<String, String>> sheetMap = new ArrayList<Map<String, String>>();
        ReadData readData = new ReadData();
        sheetMap = readData.getOLTDataSheet(sheetName);

        Map<String, String> cellValue = new HashMap<String, String>();

        List<Map<String, String>> OLTMapList = new ArrayList<Map<String, String>>();

        for (int i = 0; i < sheetMap.size(); i++) {

            Map<String, String> valuemap = new HashMap<String, String>();
            cellValue = sheetMap.get(i);

            String OLT = cellValue.get("OLT");

            if ((!"".equals(OLT)) && (OLT != null)) {

                valuemap.put("RowIndex", cellValue.get("RowIndex"));
                valuemap.put("BLD_NAME", cellValue.get("BLD_NAME"));
                valuemap.put("Plans", cellValue.get("Plans"));
                valuemap.put("Status", cellValue.get("Status"));
                OLTMapList.add(valuemap);
            }
        }
        return OLTMapList;
    }



    private String getOLTJson(Map<String, String> OLT) {

        String jsonString = null;

        try {
            // CommonGetAPI commonGetAPI = new CommonGetAPI();



            JSONObject OLTJsonObject = new JSONObject();


            String departmentName = OLT.get("Department");
            String plans = OLT.get("Plans");
            String status = ProductUtility.getStatus(OLT.get("Status"));

            OLTJsonObject.put("name", departmentName);
            OLTJsonObject.put("plans", plans);
            OLTJsonObject.put("status", status);


            jsonString = OLTJsonObject.toString();

        } catch (Exception e) {
            jsonString = null;
            e.printStackTrace();
        }
        return jsonString;
    }


}

package Act_Migration;

import org.json.JSONArray;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewApi {


        public static void main(String[] args) {
            // JSON Structure
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userName", "test_post2");
            jsonObject.put("password", "test_post2");
            jsonObject.put("serviceId", "LKNESP6M");

            // Create item array
            JSONArray itemArray = new JSONArray();

            // Item 1
            JSONObject item1 = new JSONObject();
            item1.put("key", "CUSTOMERSTATUS");
            item1.put("value", "Y");
            itemArray.put(item1);

            // Item 2
            JSONObject item2 = new JSONObject();
            item2.put("key", "CALLINGSTATIONID");
            item2.put("value", "54:82:f7:07:4f:c4");
            itemArray.put(item2);

            // Item 3
            JSONObject item3 = new JSONObject();
            item3.put("key", "PARAM1");
            item3.put("value", "1.1.1.2");
            itemArray.put(item3);

            // Add the items array to the main JSON object
            jsonObject.put("item", itemArray);

            // Print the final JSON structure as a formatted string
            String formattedJson = jsonObject.toString(4); // Pretty print with indentation
            System.out.println(formattedJson); // Print the formatted JSON

            // If you want the output in a single line as shown
            String singleLineJson = jsonObject.toString(); // Single line JSON output
            System.out.println(singleLineJson); // Print the single line JSON
        }
    }

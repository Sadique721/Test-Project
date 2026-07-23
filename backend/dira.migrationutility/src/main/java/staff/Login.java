package staff;

import org.json.JSONObject;

import api.RestExecution;
import utility.Constant;
import utility.Utility;

public class Login extends RestExecution {

	private void getLogin(String username, String password) throws Exception {

		String logFileName = "login.log";
		String logModuleName = "Login";

		String apiURL = getAPIURL("SavbillCommonGateway/login");
		Utility.printLog(logFileName, logModuleName, "Request URL", apiURL);

		// Initializing payload or API body
		String apiBody = getLoginJson(username, password);
		Utility.printLog(logFileName, logModuleName, "Request Body", apiBody);

		JSONObject JSONResponseBody = httpPost(apiURL, apiBody);
		String response = JSONResponseBody.toString(4);
		Utility.printLog(logFileName, logModuleName, "Response", response);

		int status = JSONResponseBody.getInt("status");

		if (status == 200) {
			System.out.println("Login successfully - " + username);
			String auth_bearer = JSONResponseBody.getString("accessToken");   //-->from here beare token is taken
			RestExecution.auth = auth_bearer;         //--> here also
		} else if (status == 401) {
			String msg = JSONResponseBody.getString("message");
			throw new Exception("Login Rejected  - " + msg);
		} else {
			if (JSONResponseBody.has("ERROR")) {
				String msg = "Error = " + JSONResponseBody.get("ERROR");
				System.out.println("Login Rejected  - " + msg);
				throw new Exception("Login Rejected  - " + msg);
			} else {
				String msg = "Error = " + JSONResponseBody.get("error");
				System.out.println("Login Rejected  - " + msg);
				throw new Exception("Login Rejected  - " + msg);
			}
		}
	}

	public void setAuthBearer() throws Exception {

		String userName = Constant.STAFF_USERNAME;
		String password = Constant.STAFF_PASSWORD;

		getLogin(userName, password);
	}

	@SuppressWarnings("unchecked")
	private String getLoginJson(String username, String password) {

		String jsonString = null;

		try {

			JSONObject loginJsonObject = new JSONObject();

			loginJsonObject.put("username", username);
			loginJsonObject.put("password", password);

			jsonString = loginJsonObject.toString();

		} catch (Exception e) {
			jsonString = null;
			e.printStackTrace();
		}

		return jsonString;
	}

}

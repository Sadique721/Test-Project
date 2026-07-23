package utility;

import org.json.JSONObject;

public class ProductUtility {

	public static String getStatus(String status) {

		String returnStatus = null;

		if ((!"".equals(status)) || (status != null)) {
			if (status.equalsIgnoreCase("Active")) {
				returnStatus = "Active";
			} else if (status.equalsIgnoreCase("Inactive")) {
				returnStatus = "Inactive";
			}
		}
		return returnStatus;
	}

	public static String getTimeUnit(String timeUnit) {

		String returnTimeUnit = null;

		if ((!"".equals(timeUnit)) || (timeUnit != null)) {
			if (timeUnit.equalsIgnoreCase("min")) {
				returnTimeUnit = "Min";
			} else if (timeUnit.equalsIgnoreCase("hour")) {
				returnTimeUnit = "Hour";
			} else if (timeUnit.equalsIgnoreCase("day")) {
				returnTimeUnit = "Day";
			}
		}
		return returnTimeUnit;
	}
	

	public static void printResponse(JSONObject jsonResponse, String moduleName, String entityName) {
        boolean statusFound = false;
        boolean responseCode = false;
        String message = "";

        // Detect status code
        int status = 0;
        if (jsonResponse.has("status")) {
            status = jsonResponse.optInt("status", 0);
            statusFound = true;
        } else if (jsonResponse.has("responseCode")) {
            status = jsonResponse.optInt("responseCode", 0);
            responseCode = true;
        }

        // Handle success
        if (status == 200) {
            message = String.format("New %s is added successfully - %s", moduleName, entityName);
            Utility.printLog("execution.log", moduleName, "Success", message);
        }
        // Handle already exists
        else if (status == 406) {
            String errorText = jsonResponse.optString("ERROR",
                    jsonResponse.optString("errorMesagge",
                            jsonResponse.optString("error",
                                    jsonResponse.optString("responseMessage", "Unknown error"))));
            message = String.format("%s - %s", errorText, entityName);
            Utility.printLog("execution.log", moduleName, "Already Exist", message);
        }
        // Handle forbidden
        else if (status == 403) {
            message = String.format("Access denied - You do not have permission to perform this operation. - %s", entityName);
            Utility.printLog("execution.log", moduleName, "Forbidden", message);
        }
        // Handle other errors
        else {
            String errorText = jsonResponse.optString("ERROR",
                    jsonResponse.optString("error",
                            jsonResponse.optString("message",
                                    jsonResponse.optString("responseMessage", "Unknown error"))));
            message = String.format("%s - %s", errorText, entityName);
            Utility.printLog("execution.log", moduleName, "ERROR", message);
        }

        System.out.println(message);
	}
	//this is comment by 5 feb developed by sarfo bhai
	/*
	public static void printResponse(JSONObject jsonResponse, String moduleName, String entityName) {
	    boolean statusFound = false;
	    boolean responseCode = false;
	    String message = "";

	    // Check if the "ERROR" key is not present
	    if (!jsonResponse.has("ERROR")) {
	        int status = 0;

	        // Check for "status" or "responseCode" keys
	        if (jsonResponse.has("status")) {
	            status = jsonResponse.getInt("status");
	            statusFound = true;
	        } else if (jsonResponse.has("responseCode")) {
	            status = jsonResponse.getInt("responseCode");
	            responseCode = true;
	        }

	        // Handle different statuses
	        if (status == 200) {
	            message = String.format("New %s is added successfully - %s", moduleName, entityName);
	            Utility.printLog("execution.log", moduleName, "Success", message);

	        } else if (status == 406) {
	            if (statusFound) {
	                // Check for "ERROR" or "errorMesagge" fields
	                if (jsonResponse.has("ERROR")) {
	                    message = String.format("%s - %s", jsonResponse.getString("ERROR"), entityName);
	                } else if (jsonResponse.has("errorMesagge")) {
	                    message = String.format("%s - %s", jsonResponse.getString("errorMesagge"), entityName);
	                }
	            } else if (responseCode) {
	                String responseMessage = jsonResponse.optString("responseMessage", "No response message available.");
	                message = String.format("%s - %s", responseMessage, entityName);
	            }

	            Utility.printLog("execution.log", moduleName, "Already Exist", message);

	        } else if (status == 403) {
	            // Handle 403 Forbidden status
	            message = String.format("Access denied - You do not have permission to perform this operation. - %s", entityName);
	            Utility.printLog("execution.log", moduleName, "Forbidden", message);
	            System.out.println(message);

	        } else {
	            // Handle other statuses, including error cases
	            if (statusFound) {
	                message = String.format("%s - %s", jsonResponse.optString("ERROR", "Unknown error"), entityName);
	            } else if (responseCode) {
	                String responseMessage = jsonResponse.optString("responseMessage", "No response message available.");
	                message = String.format("%s - %s", responseMessage, entityName);
	            }

	            Utility.printLog("execution.log", moduleName, "ERROR", message);
	        }

	    } else {
	        // If "ERROR" key exists in the response
	        message = String.format("%s - %s", jsonResponse.getString("ERROR"), entityName);
	        Utility.printLog("execution.log", moduleName, "ERROR", message);
	    }

	    System.out.println(message);
	}
/*
	
/*
	public static void stopExecution(String logFileName, String logModuleName, String message, String entityName) {

		ReadWriteExcelFile rw = new ReadWriteExcelFile();
	//	rw.setMultipleColumnInActiveSheet();    //comment file here 

		System.out.println(message + " : " + entityName);
		Utility.printLog(logFileName, logModuleName, message, entityName);

		String errorMessage = "ERROR | Migration Utility is interrupted due to above error";
		System.out.println(errorMessage);
		Utility.printLog("execution.log", "ERROR", errorMessage, entityName);
		rw.setMultipleColumnInActiveSheet(); 
		// throw new Exception(errorMessage);
		System.exit(0);
	}
*/
	
	
	public static void stopExecution(String logFileName, String logModuleName, String message, String entityName) {
	    ReadWriteExcelFile rw = new ReadWriteExcelFile();

	    try {
	        // Log the message
	        System.out.println(message + " : " + entityName);
	        Utility.printLog(logFileName, logModuleName, message, entityName);

	        // Log the error message
	        String errorMessage = "ERROR | Migration Utility is interrupted due to above error";
	        System.out.println(errorMessage);
	        Utility.printLog("execution.log", "ERROR", errorMessage, entityName);

	        // Attempt the file writing operation (which might throw an exception)
	         //  rw.setMultipleColumnInActiveSheetACT(); 
	        
	        // If you need to handle exceptions and continue, you can throw the exception or log it
	        // throw new Exception(errorMessage);  // Uncomment if you want to stop after error

	    } catch (Exception e) {
	        // Log the exception if it occurs, but do not stop the program.
	        System.err.println("Exception occurred: " + e.getMessage());
	        Utility.printLog("execution.log", "ERROR", "Exception: " + e.getMessage(), entityName);

	        // Here, we don't stop the execution with System.exit(0).
	        // Instead, you can log the error and continue execution.

	        // Continue with the migration or other steps.
	        System.out.println("Continuing execution after error.");
	    }
	    
	    // Proceed with other tasks after handling the error.
	    System.out.println("Further steps executed even after error.");
	    // Add more logic for subsequent steps here.
	}

	
	
	
	public static void stopExecutionNew(String logFileName, String logModuleName, String message, String entityName)
			throws Exception {

		System.out.println(message + " : " + entityName);
		Utility.printLog(logFileName, logModuleName, message, entityName);

		String errorMessage = "ERROR | Migration Utility is interrupted due to above error";
		System.out.println(errorMessage);
		Utility.printLog("execution.log", "ERROR", errorMessage, entityName);
		throw new Exception(errorMessage);
		// System.exit(0);
	}
}

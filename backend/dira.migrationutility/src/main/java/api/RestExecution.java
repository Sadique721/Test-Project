package api;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import utility.Constant;

public class RestExecution {

	public static String auth = "";
	

	public String getAPIURL(String apiName) {
		// Initializing Rest API's URL
		String apiURL = Constant.API_URL + apiName;
		return apiURL;
	}
	
	//------------Thread post request---------------------------->
	
	 // Method to make HTTP POST request asynchronously
    public JSONObject httpPostThread(String url, String body) {
        final JSONObject[] JSONResponseBody = new JSONObject[1]; // Use an array to hold the result in the thread
       // final String auth = "your_auth_token"; // Assuming the auth token is set elsewhere

        // Building request using requestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(body);
        builder.setContentType("application/json");
        builder.addHeader("Authorization", auth);  // Add auth bearer token
        builder.addHeader("X-Skip-Hash", "true");

        RequestSpecification requestSpec = builder.build();

        // Create a thread to execute the POST request asynchronously
        Thread thread = new Thread(() -> {
            try {
                StopWatch sw = new StopWatch();
                sw.start();
                Response response = RestAssured.given().spec(requestSpec).when().post(url);
                System.out.println("Taken Time = " + sw.getTime());
                JSONResponseBody[0] = new JSONObject(response.body().asString()); // Store result in the array
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start(); // Start the thread

        // Optional: Wait for the thread to complete before returning the result
        try {
            thread.join(); // Block until the thread completes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return JSONResponseBody[0]; // Return the response after the thread finishes execution
    }

// Thread work like jmeter---------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    public void executeSequentialHttpRequests(String url, String body,   int threadCount, long delayBetweenThreadsMs) {
        for (int i = 0; i < threadCount; i++) {
            System.out.println("Executing thread #" + (i + 1));
            try {
                // Build request using RequestSpecBuilder
                RequestSpecBuilder builder = new RequestSpecBuilder();
                builder.setBody(body);
                builder.setContentType("application/json");
                builder.addHeader("Authorization", auth); // Add auth bearer token

                RequestSpecification requestSpec = builder.build();

                // Measure the time taken for each request
                StopWatch sw = new StopWatch();
                sw.start();

                // Execute the POST request
                Response response = RestAssured.given().spec(requestSpec).when().post(url);
                sw.stop();

                // Print response and time taken
                System.out.println("Thread #" + (i + 1) + " Response: " + response.body().asString());
                System.out.println("Thread #" + (i + 1) + " Time Taken: " + sw.getTime() + "ms");

                // Parse response as JSONObject if needed
                JSONObject jsonResponse = new JSONObject(response.body().asString());
                System.out.println("Thread #" + (i + 1) + " JSON Response: " + jsonResponse.toString());

                // Introduce a delay between threads
                Thread.sleep(delayBetweenThreadsMs);

            } catch (Exception e) {
                System.err.println("Error in thread #" + (i + 1));
                e.printStackTrace();
            }
        }
    }
    //<<<<<<<<<<<<<<<<<<<<<<<--------------------------------------------------------------------------------
    
	//--------------------------------------------->
 // Asynchronous HTTP POST request using CompletableFuture
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);
    public CompletableFuture<JSONObject> httpPostAsync(String url, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch sw = new StopWatch();
                sw.start();

                // Build request
                RequestSpecBuilder builder = new RequestSpecBuilder();
                builder.setBody(body);
                builder.setContentType("application/json");
                builder.addHeader("Authorization", auth);
                builder.addHeader("X-Skip-Hash", "true");

                RequestSpecification requestSpec = builder.build();

                // Execute POST request
                Response response = RestAssured.given().spec(requestSpec).when().post(url);
                System.out.println("Taken Time = " + sw.getTime());

                return new JSONObject(response.body().asString());
            } catch (Exception e) {
                throw new RuntimeException("HTTP POST failed: " + e.getMessage(), e);
            }
        }, executor);
    }
    
	//<-----------------------------------------------
	
	public JSONObject httpPostNew(String url, String body) {
		JSONObject JSONResponseBody = null;

		HttpClient httpClient = HttpClientBuilder.create().build();

		// Create an instance of HttpPost with the desired URL
		// String postUrl = "http://example.com/api/endpoint";
		HttpPost httpPost = new HttpPost(url);

		// Add headers to the request
		httpPost.setHeader("Content-type", "application/json");
		httpPost.addHeader("Authorization", auth);

		try {
			// Set the request body
			// String request = "{\"name\":\"David\", \"age\":20}";
			StringEntity entity = new StringEntity(body);
			httpPost.setEntity(entity);

			// StopWatch sw = new StopWatch();sw.start();
			// Execute the request and obtain the response
			HttpResponse httpResponse = httpClient.execute(httpPost);
			// System.out.println("Taken Time = " + sw.getTime());
			// Extract the response's content
			HttpEntity responseEntity = httpResponse.getEntity();
			String response = EntityUtils.toString(responseEntity);
			// System.out.println("response = " + response);

			// JSONResponseBody = new JSONObject(responseEntity.getContent().toString());
			JSONResponseBody = new JSONObject(response);

			// Print the response

			// System.out.println("JSONResponseBody = " + JSONResponseBody.toString(4));

			// Response response = given().spec(requestSpec).when().post(url);

			// JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

//	public JSONObject httpPost(String url, String body) {
//		JSONObject JSONResponseBody = null;
//
//		// Building request using requestSpecBuilder
//		RequestSpecBuilder builder = new RequestSpecBuilder();
//
//		// Setting API's body
//		builder.setBody(body);
//
//		// Setting content type as application/json or application/xml
//
//		builder.setContentType("application/json");
//		builder.addHeader("Authorization", auth);    //--> add auth barear token
//        builder.addHeader("X-Skip-Hash", "true");
//
//		RequestSpecification requestSpec = builder.build();
//		try {
//			StopWatch sw = new StopWatch();
//			sw.start();
//			Response response = given().spec(requestSpec).when().post(url);
//			System.out.println("Taken Time = " + sw.getTime());
//			System.out.println("Response from API: " + response);
//			JSONResponseBody = new JSONObject(response.body().asString());  //i comment 18 dec
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return JSONResponseBody;
//	}

    public JSONObject httpPost(String url, String body) {
        JSONObject JSONResponseBody = null;

        // Build request using RequestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        // Set request body
        builder.setBody(body);

        // Set content type
        builder.setContentType("application/json");

        // ✅ Headers from collection (excluding Origin, Referer, X-HMAC-SIGNATURE, X-REQUEST-MILLISEC)
        builder.addHeader("Accept", "application/json, text/plain, */*");
        builder.addHeader("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
        builder.addHeader("Authorization", auth); // your dynamic token variable
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        builder.addHeader("requestFrom", "gui");
        builder.addHeader("X-Skip-Hash", "true");

        RequestSpecification requestSpec = builder.build();

        try {
            StopWatch sw = new StopWatch();
            sw.start();

            Response response = given().spec(requestSpec).when().post(url);

//            System.out.println("Status Code = " + response.statusCode());
//            System.out.println("Response Body = " + response.getBody().asString());


            sw.stop();
            System.out.println("Taken Time = " + sw.getTime());
//            System.out.println("Response from API: " + response);

            JSONResponseBody = new JSONObject(response.body().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    }

    //======================================================

    public JSONObject httpPostAI(String url, String body) {
        JSONObject JSONResponseBody = null;

        // Build request using RequestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        // Set request body
        builder.setBody(body);

        // Set content type
        builder.setContentType("application/json");

        // ✅ Headers from collection (excluding Origin, Referer, X-HMAC-SIGNATURE, X-REQUEST-MILLISEC)
        builder.addHeader("Accept", "application/json, text/plain, */*");
        builder.addHeader("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
        builder.addHeader("Authorization", auth); // your dynamic token variable
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36");
        builder.addHeader("requestFrom", "gui");
        builder.addHeader("X-Skip-Hash", "true");

        RequestSpecification requestSpec = builder.build();

        try {
            StopWatch sw = new StopWatch();
            sw.start();

            Response response = given().spec(requestSpec).when().post(url);

//            System.out.println("Status Code = " + response.statusCode());
//            System.out.println("Response Body = " + response.getBody().asString());


            sw.stop();
            System.out.println("Taken Time = " + sw.getTime());
//            System.out.println("Response from API: " + response);

            JSONResponseBody = new JSONObject(response.body().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    }


    // savanna payment
	public JSONObject httpPostS(String url, String body) {
        JSONObject JSONResponseBody = null;

        // Building request using requestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        // If the body is null, set it to an empty string or valid JSON object
        if (body == null) {
            body = "";
        }

        // Setting the body
        builder.setBody(body);

        // Setting content type as application/json
        builder.setContentType("application/json");

        // Adding Authorization header (bearer token)
        builder.addHeader("Authorization",  auth);  // Make sure `auth` is properly set

        // Build the request specification
        RequestSpecification requestSpec = builder.build();

        try {
            // Send the POST request and capture the response
            Response response = given().spec(requestSpec).when().post(url);

            // Log response body (for debugging)
            String responseBody = response.body().asString();
           // System.out.println("Response Body: " + responseBody);

            // Parse the response into a JSONObject
            JSONResponseBody = new JSONObject(responseBody);

           
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    
}
	
	public JSONObject httpPostFormData(String url, String body, String fileName) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();
		// Setting API's body
		// builder.setBody(body);

		// Setting content type as application/json or application/xml
		builder.setContentType("multipart/form-data");
		builder.addHeader("Authorization", auth);      //---> set barear token 

		builder.addMultiPart("spojo", body);

		if ((fileName != null) && (!"".equals(fileName))) {
			File file = new File(fileName);
			builder.addMultiPart("file", file);
		}

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().post(url);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

	public JSONObject httpPostFormData1(String url, String body, String fileName) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();
		// Setting API's body
		// builder.setBody(body);

		// Setting content type as application/json or application/xml
		builder.setContentType("multipart/form-data");
		builder.addHeader("Authorization", auth);

		builder.addMultiPart("docDetailsList", body);

		if ((fileName != null) && (!"".equals(fileName))) {
			File file = new File(fileName);
			builder.addMultiPart("file", file);
		}

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().post(url);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

	public JSONObject httpPostFormData2(String url, String body, String fileName) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();
		// Setting API's body
		// builder.setBody(body);

		// Setting content type as application/json or application/xml
		builder.setContentType("multipart/form-data");
		builder.addHeader("Authorization", auth);

		builder.addMultiPart("entityDTO", body);

		if ((fileName != null) && (!"".equals(fileName))) {
			File file = new File(fileName);
			builder.addMultiPart("file", file);
		}

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().post(url);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

    public JSONObject httpPostFormData3(String url, String jsonBody, String fileName) {
        JSONObject jsonResponse = null;

        try {
            // Build request
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder.setContentType("multipart/form-data");
            builder.addHeader("Authorization", auth);

            // Add JSON as form-data
            builder.addMultiPart("spojo", jsonBody);

            // Add file if provided and exists
            if (fileName != null && !fileName.isEmpty()) {
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    builder.addMultiPart("file", file);
                }
            }

            RequestSpecification requestSpec = builder.build();

            // Send POST request
            Response response = given()
                    .spec(requestSpec)
                    .when()
                    .post(url);

            // Convert response to JSONObject
            String responseBody = response.body().asString();
            jsonResponse = new JSONObject(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }


    public JSONObject httpPostFormData4(String url, String jsonBody, String fileName) {
        JSONObject jsonResponse = null;

        try {
            RequestSpecBuilder builder = new RequestSpecBuilder();

            builder.addHeader("Authorization", auth);
            builder.addHeader("requestFrom", "gui");
            builder.addHeader("Accept", "application/json, text/plain, */*");

            // Add EMPTY file part (important!)
            builder.addMultiPart("file", "");

            // Add JSON exactly like curl
            builder.addMultiPart("spojo", jsonBody);

            // Optional real file (only if actually required)
            if (fileName != null && !fileName.trim().isEmpty()) {
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    builder.addMultiPart("file", file);
                }
            }

            Response response = given()
                    .spec(builder.build())
                    .post(url);

            jsonResponse = new JSONObject(response.getBody().asString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }



    // this post requet for product of inventory
	public JSONObject httpPostFormDataP(String url, String body) {
	    JSONObject JSONResponseBody = null;

	    // Building request using requestSpecBuilder
	    RequestSpecBuilder builder = new RequestSpecBuilder();
	    
	    // Setting content type as multipart/form-data
	    builder.setContentType("multipart/form-data");
	    builder.addHeader("Authorization", auth);

	    // Adding multipart productDetailList with the body
	    builder.addMultiPart("productDetailList", body);

	    RequestSpecification requestSpec = builder.build();

	    try {
	        // Send the POST request and get the response
	        Response response = given().spec(requestSpec).when().post(url);
	        
	        // Log response body for debugging
	        String responseBody = response.body().asString();
	      //  System.out.println("Response Body: " + responseBody);

	        // Parse the response body into JSONObject
	        JSONResponseBody = new JSONObject(responseBody);

	    } catch (Exception e) {
	        // Print stack trace for debugging
	        e.printStackTrace();
	    }
	    
	    // Return the parsed JSON response
	    return JSONResponseBody;
	}
	
	// this post rewuest for building mangement of inventory
	public JSONObject httpPostFormDatabui(String url, String body) {
	    JSONObject JSONResponseBody = null;

	    try {
	        // Build the request
	        RequestSpecBuilder builder = new RequestSpecBuilder();
	        builder.setContentType("multipart/form-data");
	        builder.addHeader("Authorization", auth);
            builder.addHeader("X-Skip-Hash", "true");

	        // Add the JSON body as a multipart form-data field with the correct key
	        builder.addMultiPart("entityDTO", body, "application/json");

	        RequestSpecification requestSpec = builder.build();

	        // Send the POST request
	        Response response = given().spec(requestSpec).when().post(url);

	        // Log response body (optional)
	        String responseBody = response.getBody().asString();
	      //  System.out.println("Response Body: " + responseBody);

	        // Parse the response string to JSON
	        JSONResponseBody = new JSONObject(responseBody);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return JSONResponseBody;
	}






    public JSONObject httpGet(String url) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting content type as application/json or application/xml
		builder.setContentType("application/json");
		builder.addHeader("Authorization", auth);
        builder.addHeader("X-Skip-Hash", "true");

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().get(url);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

    public JSONObject httpGetcaf(String url) {
        JSONObject JSONResponseBody = null;

        try {
            RequestSpecBuilder builder = new RequestSpecBuilder();

            builder.setContentType("application/json");
            builder.addHeader("Accept", "application/json, text/plain, */*");
            builder.addHeader("Authorization", auth);  // e.g. "Bearer <token>"
            builder.addHeader("X-HMAC-SIGNATURE", "7F3Crg8umHwDchq/yrI0P/gbeN8eSGhwW5+ZN1qgKMk=");
            builder.addHeader("X-REQUEST-MILLISEC", String.valueOf(System.currentTimeMillis()));
            builder.addHeader("requestFrom", "gui");

            RequestSpecification requestSpec = builder.build();

            Response response = given().spec(requestSpec).when().get(url);

            System.out.println("GET URL: " + url);
            System.out.println("Response Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.body().asString());

            JSONResponseBody = new JSONObject(response.body().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    }


    // savan
	public JSONObject httpGet(String url,String custid) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting content type as application/json or application/xml
		builder.setContentType("application/json");
		builder.addHeader("Authorization", auth);
        builder.addHeader("X-Skip-Hash", "true");

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().get(url+custid);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}

	public JSONObject httpPut(String url, String body) {
		JSONObject JSONResponseBody = null;

		// Building request using requestSpecBuilder
		RequestSpecBuilder builder = new RequestSpecBuilder();

		// Setting API's body
		builder.setBody(body);

		// Setting content type as application/json or application/xml
		builder.setContentType("application/json");
		builder.addHeader("Authorization", auth);
        builder.addHeader("X-Skip-Hash", "true");

		RequestSpecification requestSpec = builder.build();
		try {

			Response response = given().spec(requestSpec).when().put(url);
			JSONResponseBody = new JSONObject(response.body().asString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONResponseBody;
	}



    public JSONObject httpPutcaf(String url, Map<String, Object> formData) {
        JSONObject JSONResponseBody = null;

        try {
            RequestSpecBuilder builder = new RequestSpecBuilder();

            // Add form-data fields
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                builder.addMultiPart(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            // Headers
            builder.setContentType("multipart/form-data");
            builder.addHeader("Authorization", auth);  // "Bearer <token>"
            builder.addHeader("Accept", "application/json, text/plain, */*");
            builder.addHeader("X-HMAC-SIGNATURE", "skutqGq3kgxoWdpXPeaCE+Up4Cbv+Vt+BKnGSL9z+EU=");
            builder.addHeader("X-REQUEST-MILLISEC", String.valueOf(System.currentTimeMillis()));
            builder.addHeader("requestFrom", "gui");

            RequestSpecification requestSpec = builder.build();
            Response response = given().spec(requestSpec).when().put(url);

//            System.out.println("Response Code: " + response.getStatusCode());
//            System.out.println("Response Body: " + response.body().asString());

            JSONResponseBody = new JSONObject(response.body().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    }


    public JSONObject httpPutLead(String url, Map<String, Object> formData) {
        JSONObject JSONResponseBody = null;

        try {
            RequestSpecBuilder builder = new RequestSpecBuilder();

            // Add form-data fields
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                builder.addMultiPart(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            // Headers
            builder.setContentType("multipart/form-data");
            builder.addHeader("Authorization", auth);  // "Bearer <token>"
            builder.addHeader("Accept", "application/json, text/plain, */*");
            builder.addHeader("X-HMAC-SIGNATURE", "skutqGq3kgxoWdpXPeaCE+Up4Cbv+Vt+BKnGSL9z+EU=");
            builder.addHeader("X-REQUEST-MILLISEC", String.valueOf(System.currentTimeMillis()));
            builder.addHeader("requestFrom", "gui");

            RequestSpecification requestSpec = builder.build();
            Response response = given().spec(requestSpec).when().put(url);

//            System.out.println("Response Code: " + response.getStatusCode());
//            System.out.println("Response Body: " + response.body().asString());

            JSONResponseBody = new JSONObject(response.body().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONResponseBody;
    }





}

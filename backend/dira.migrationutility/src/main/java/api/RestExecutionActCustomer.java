package api;

import static com.jayway.restassured.RestAssured.given;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONObject;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import utility.Constant;
public class RestExecutionActCustomer{
	
	    public static String auth = "";

	    // Thread pool for asynchronous execution
	    private final ExecutorService executorService;

	    public RestExecutionActCustomer(int threadCount) {
	        this.executorService = Executors.newFixedThreadPool(threadCount);
	    }

	    // Get API URL
	    public String getAPIURL(String apiName) {
	        return Constant.API_URL + apiName;
	    }

	    // Synchronous HTTP POST
	    public JSONObject httpPost(String url, String body) {
	        JSONObject JSONResponseBody = null;

	        // Building request using RequestSpecBuilder
	        RequestSpecBuilder builder = new RequestSpecBuilder();
	        builder.setBody(body);
	        builder.setContentType("application/json");
	        builder.addHeader("Authorization", auth);

	        RequestSpecification requestSpec = builder.build();

	        try {
	            StopWatch sw = new StopWatch();
	            sw.start();
	            Response response = given().spec(requestSpec).when().post(url);
	            System.out.println("Taken Time = " + sw.getTime() + " ms");
	            JSONResponseBody = new JSONObject(response.body().asString());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return JSONResponseBody;
	    }

	    // Asynchronous HTTP POST
	    public Future<JSONObject> httpPostAsync(String url, String body) {
	        return executorService.submit(() -> httpPost(url, body));
	    }

	    // Shutdown the thread pool
	    public void shutdown() {
	        executorService.shutdown();
	        try {
	            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
	                executorService.shutdownNow();
	            }
	        } catch (InterruptedException e) {
	            executorService.shutdownNow();
	        }
	    
	}

}

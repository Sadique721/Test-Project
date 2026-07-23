package api;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import utility.Constant;

public class ExetExecutionThread {

    public static String auth = ""; // Authorization token

    // API URL
    public String getAPIURL(String apiName) {
        String apiURL = Constant.API_URL + apiName;
        return apiURL;
    }

    // Standard POST request using Apache HttpClient with concurrency support
    public JSONObject httpPostNew(String url, String body) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        List<Callable<JSONObject>> tasks = new ArrayList<>();
        tasks.add(() -> {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.addHeader("Authorization", auth);

            try {
                StringEntity entity = new StringEntity(body);
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity responseEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(responseEntity);

                return new JSONObject(response); // Return the result from the callable task
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if error occurs
            }
        });

        List<Future<JSONObject>> results = executor.invokeAll(tasks);

        // Process the results (this will block until all tasks are completed)
        JSONObject JSONResponseBody = null;
        for (Future<JSONObject> result : results) {
            JSONResponseBody = result.get();  // Get the result from the task
        }

        executor.shutdown();
        return JSONResponseBody;  // Return the final result after all tasks are completed
    }

    // Standard POST request using Rest Assured with concurrency support
    public JSONObject httpPost(String url, String body) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        List<Callable<JSONObject>> tasks = new ArrayList<>();
        
        // Define the task
        tasks.add(() -> {
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder.setBody(body);
            builder.setContentType("application/json");
            builder.addHeader("Authorization", auth);

            RequestSpecification requestSpec = builder.build();
            try {
                StopWatch sw = new StopWatch();
                sw.start();
                Response response = given().spec(requestSpec).when().post(url);
                System.out.println("Taken Time = " + sw.getTime());

                // Log the raw response body for debugging
                String responseBody = response.body().asString();
                System.out.println("Raw Response Body: " + responseBody);

                // Check if the response body is not empty or null before creating JSONObject
                if (responseBody != null && !responseBody.isEmpty()) {
                    return new JSONObject(responseBody); // Return the result from the callable task
                } else {
                    System.err.println("Response body is empty or null.");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if error occurs
            }
        });

        // Execute all tasks concurrently
        List<Future<JSONObject>> results = executor.invokeAll(tasks);

        // Process the results (this will block until all tasks are completed)
        JSONObject finalResponse = null;
        for (Future<JSONObject> result : results) {
            finalResponse = result.get();  // Get the result from the task
        }

        executor.shutdown();
        return finalResponse;  // Return the final result after all tasks are completed
    }

    // POST request with file upload (multipart/form-data) with concurrency
    public JSONObject httpPostFormData(String url, String body, String fileName) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        List<Callable<JSONObject>> tasks = new ArrayList<>();
        tasks.add(() -> {
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder.setContentType("multipart/form-data");
            builder.addHeader("Authorization", auth);
            builder.addMultiPart("spojo", body);

            if (fileName != null && !fileName.isEmpty()) {
                File file = new File(fileName);
                builder.addMultiPart("file", file);
            }

            RequestSpecification requestSpec = builder.build();
            try {
                Response response = given().spec(requestSpec).when().post(url);
                return new JSONObject(response.body().asString()); // Return the result from the callable task
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if error occurs
            }
        });

        List<Future<JSONObject>> results = executor.invokeAll(tasks);

        // Process the results (this will block until all tasks are completed)
        JSONObject JSONResponseBody = null;
        for (Future<JSONObject> result : results) {
            JSONResponseBody = result.get();  // Get the result from the task
        }

        executor.shutdown();
        return JSONResponseBody;  // Return the final result after all tasks are completed
    }

    // Standard GET request with concurrency support
    public JSONObject httpGet(String url) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        List<Callable<JSONObject>> tasks = new ArrayList<>();
        tasks.add(() -> {
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder.setContentType("application/json");
            builder.addHeader("Authorization", auth);

            RequestSpecification requestSpec = builder.build();
            try {
                Response response = given().spec(requestSpec).when().get(url);
                return new JSONObject(response.body().asString()); // Return the result from the callable task
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if error occurs
            }
        });

        List<Future<JSONObject>> results = executor.invokeAll(tasks);

        // Process the results (this will block until all tasks are completed)
        JSONObject JSONResponseBody = null;
        for (Future<JSONObject> result : results) {
            JSONResponseBody = result.get();  // Get the result from the task
        }

        executor.shutdown();
        return JSONResponseBody;  // Return the final result after all tasks are completed
    }

    // Standard PUT request with concurrency support
    public JSONObject httpPut(String url, String body) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        List<Callable<JSONObject>> tasks = new ArrayList<>();
        tasks.add(() -> {
            RequestSpecBuilder builder = new RequestSpecBuilder();
            builder.setBody(body);
            builder.setContentType("application/json");
            builder.addHeader("Authorization", auth);

            RequestSpecification requestSpec = builder.build();
            try {
                Response response = given().spec(requestSpec).when().put(url);
                return new JSONObject(response.body().asString()); // Return the result from the callable task
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if error occurs
            }
        });

        List<Future<JSONObject>> results = executor.invokeAll(tasks);

        // Process the results (this will block until all tasks are completed)
        JSONObject JSONResponseBody = null;
        for (Future<JSONObject> result : results) {
            JSONResponseBody = result.get();  // Get the result from the task
        }

        executor.shutdown();
        return JSONResponseBody;  // Return the final result after all tasks are completed
    }

    // Retry mechanism for POST requests with concurrency
    private static final int DEFAULT_RETRY_LIMIT = 2;
    private static final long DEFAULT_RETRY_DELAY_MS = 2000;

    protected JSONObject executePostWithRetry(String url, String requestBody, int retryLimit, long retryDelay)
            throws InterruptedException, ExecutionException {

        int attempts = 0;
        ExecutorService executor = Executors.newFixedThreadPool(10); // Using thread pool for concurrency
        while (attempts < retryLimit) {
            attempts++;
            List<Callable<JSONObject>> tasks = new ArrayList<>();
            tasks.add(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setEntity(new StringEntity(requestBody));

                  //  System.out.println("Attempt " + attempts + " to call URL: " + url);
                    long startTime = System.currentTimeMillis();

                    try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                        long endTime = System.currentTimeMillis();
                        String responseBody = EntityUtils.toString(response.getEntity());

                        System.out.println("API Response [Time: " + (endTime - startTime) + "ms]: " + responseBody);
                        return new JSONObject(responseBody); // Return the result from the callable task
                    }
                } catch (IOException e) {
                  //  System.err.println("Error on attempt " + attempts + ": " + e.getMessage());
                    return null; // Return null if error occurs
                }
            });

            List<Future<JSONObject>> results = executor.invokeAll(tasks);
            JSONObject JSONResponseBody = null;

            // Process the results (this will block until all tasks are completed)
            for (Future<JSONObject> result : results) {
                JSONResponseBody = result.get();  // Get the result from the task
            }

            if (JSONResponseBody != null) {
                executor.shutdown();
                return JSONResponseBody;
            }

            // Exponential backoff delay
            Thread.sleep(retryDelay * (long) Math.pow(2, attempts - 1));
        }

        executor.shutdown();
        throw new RuntimeException("API call failed after " + retryLimit + " attempts.");
    }

    // Convenience method with default retry settings and concurrency
    protected JSONObject executePostWithRetry(String url, String requestBody) throws InterruptedException, ExecutionException {
        return executePostWithRetry(url, requestBody, DEFAULT_RETRY_LIMIT, DEFAULT_RETRY_DELAY_MS);
    }
}

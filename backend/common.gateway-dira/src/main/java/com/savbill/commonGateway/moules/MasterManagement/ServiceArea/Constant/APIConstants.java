package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.Constant;

import org.springframework.http.HttpStatus;

public class APIConstants {

    public static Integer SUCCESS = HttpStatus.OK.value();
    public static Integer FAIL = HttpStatus.BAD_REQUEST.value();
    public static Integer INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static Integer NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final String ERROR_TAG = "ERROR";
    public static final Integer EXPECTATION_FAILED=HttpStatus.EXPECTATION_FAILED.value();

    public interface API_Response_Message {
        public final String CREATED_SUCCESSFULLY = "Successfully Created";
        public final String UPDATED_SUCCESSFULLY = "Successfully Updated";
        public final String DELETED_SUCCESSFULLY = "Successfully Deleted";
        public final String FETCHED_SUCCESSFULLY = "Successfully Fetched";
        public final String NO_RECORDS_FOUND = "No Records Found!";
        public final String ALREADY_EXIST = " already exists!";
        public final String IN_USED = " in used!";
        public final String NOT_FOUND = " not found!";
        public final String IPV4_ADDRESS = "Please Check SourceIp Before proceeding values must be as per IPV4 standard(0-255)";
    }

    public interface LogConstant {
        public final String LOG_MESSAGE = "[{\"requestfrom\":\"{}\", \"requestby\":\"{}\", \"requestip\":\"{}\", \"type\":\"{}\", \"status\":\"{}\", \"statuscode\":\"{}\", \"message\":\"{}\"}]";
        public final String CREATE_TYPE = "CREATE";
        public final String UPDATE_TYPE = "UPDATE";
        public final String DELETE_TYPE = "DELETE";
        public final String FETCH_TYPE = "FETCH";
        public final String SUCCESS_STATUS = "SUCCESS";
        public final String FAIL_STATUS = "FAILURE";
        public final String REQUEST_FROM = "requestFrom";
        public final String AUTHORIZATION = "Authorization";
        public final String TRACE_ID = "traceId";
        public final String SPAN_ID = "spanId";
        public final String VALIDATE_TYPE = "VALIDATE";
    }

    public interface LogMessage {
        public final String MESSAGE_CREATE_STARTING = "Starting the create process with input: ";
        public final String MESSAGE_CREATE_ENDING = "Create process completed successfully for entity: ";

        public final String MESSAGE_UPDATE_STARTING = "Starting the update process with input: ";
        public final String MESSAGE_UPDATE_ENDING = "Update process completed successfully for entity: ";

        public final String MESSAGE_DELETE_STARTING = "Starting the delete process for entity with ID: ";
        public final String MESSAGE_DELETE_ENDING = "Delete process completed successfully for entity with ID: ";

        public final String MESSAGE_SEARCH_STARTING = "Starting the search operation.";
        public final String MESSAGE_SEARCH_ENDING = "Search operation completed successfully.";

        public final String MESSAGE_PAGINATION_STARTING = "Starting the data fetching operation with pagination.";
        public final String MESSAGE_PAGINATION_ENDING = "Data fetching with pagination completed successfully.";

        public final String MESSAGE_LIST_STARTING = "Starting the list fetching operation.";
        public final String MESSAGE_LIST_ENDING = "List fetching operation completed successfully.";

        public final String MESSAGE_FETCH_BY_STARTING = "Starting the fetch operation for: ";
        public final String MESSAGE_FETCH_BY_ENDING = "Fetch operation completed successfully for: ";

        public final String MESSAGE_FETCH_DATA_STARTING = "Starting the fetch operation";
        public final String MESSAGE_FETCH_DATA_ENDING = "Fetch operation completed successfully";

        public final String MESSAGE_VALIDATE_STARTING = "Starting the validate process with input: ";
        public final String MESSAGE_VALIDATE_ENDING = "Validate process completed successfully for entity: ";

        public final String MESSAGE_METHOD_STARTING = "Starting execution of method.";
        public final String MESSAGE_METHOD_ENDING = "Execution completed for method.";

        public static final String CREATE_OPERATION_FAILED = "Failed to create: {0} entity with entity name: {1} due to: {2}";
        public static final String UPDATE_OPERATION_FAILED = "Failed to update: {0} entity with entity name: {1} due to: {2}";
        public static final String DELETE_OPERATION_FAILED = "Failed to delete: {0} entity with entity id: {1} due to: {2}";
        public static final String FETCH_LIST_OPERATION_FAILED = "Failed to fetch: {0} list due to: {1}";
        public static final String FETCH_WITH_PAGINATION_OPERATION_FAILED = "Failed to fetch: {0} with pagination due to: {1}";
        public static final String FETCH_BY_ID_OPERATION_FAILED = "Failed to fetch: {0} entity with entity id: {1} due to: {2}";
        public static final String SEARCH_OPERATION_FAILED = "Failed to perform search operation for: {0} entity due to: {1}";
        public static final String VALIDATE_OPERATION_FAILED = "Failed to validate: {0} entity with field: {1} due to: {2}";
        public static final String FETCH_DATA_OPERATION_FAILED = "Failed to fetch: {0} data due to: {1}";
        public static final String METHOD_EXECUTION_FAILED = "Method execution failed due to: {0}";


        public static final String CREATE_OPERATION_SUCCESS = "Created: {0} entity with name: {1} successfully.";
        public static final String UPDATE_OPERATION_SUCCESS = "Updated: {0} entity with name: {1} successfully.";
        public static final String DELETE_OPERATION_SUCCESS = "Deleted: {0} entity with ID: {1} successfully.";
        public static final String FETCH_LIST_OPERATION_SUCCESS = "Fetched list of {0} successfully.";
        public static final String FETCH_WITH_PAGINATION_OPERATION_SUCCESS = "Fetched {0} with pagination successfully.";
        public static final String FETCH_BY_ID_OPERATION_SUCCESS = "Fetched {0} entity with ID: {1} successfully.";
        public static final String SEARCH_OPERATION_SUCCESS = "Search operation for {0} entity completed successfully.";
        public static final String VALIDATE_OPERATION_SUCCESS = "Validated {0} entity with field: {1} successfully.";
        public static final String FETCH_DATA_OPERATION_SUCCESS = "Fetched {0} data successfully.";
        public static final String METHOD_EXECUTION_SUCCESS = "Method executed successfully.";
    }
}

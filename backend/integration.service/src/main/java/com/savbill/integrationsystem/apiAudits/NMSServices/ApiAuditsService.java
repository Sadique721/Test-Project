package com.savbill.integrationsystem.apiAudits.NMSServices;

import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
import com.savbill.integrationsystem.apiAudits.entity.ApiAudits;
import com.savbill.integrationsystem.apiAudits.entity.QApiAudits;
import com.savbill.integrationsystem.apiAudits.mapper.ApiAuditsMapper;
import com.savbill.integrationsystem.apiAudits.model.ApiAuditsDTO;
import com.savbill.integrationsystem.apiAudits.repository.ApiAuditsRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.exceptions.DataNotFoundException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Api audits service.
 */
@Service
public class ApiAuditsService extends ExBaseAbstractService<ApiAuditsDTO, ApiAudits, Long> {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditsService.class);
    /**
     * The Api audits mapper.
     */
    @Autowired
    ApiAuditsMapper apiAuditsMapper;

    /**
     * The Repository.
     */
    @Autowired
    ApiAuditsRepository repository;

    /**
     * The Jwt util.
     */
    @Autowired
    JwtUtil jwtUtil;

    /**
     * The Environment info.
     */
    @Value("${environmentInfo}")
    private String environmentInfo;

    /**
     * The Server ip for audit.
     */
    @Value("${serverIpForAudit}")
    private String serverIpForAudit;

    /**
     * Instantiates a new Api audits service.
     *
     * @param repository the repository
     * @param mapper     the mapper
     */
    public ApiAuditsService(ApiAuditsRepository repository, ApiAuditsMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public void deleteEntity(ApiAuditsDTO entity) throws Exception {
        ApiAudits entityDomain = apiAuditsMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        //ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                throw new DataNotFoundException();
            }
            entityDomain.setIsDeleted(true);
            repository.save(entityDomain);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }


    /**
     * Extract data and save post api audits api audits dto.
     *
     * @param url                the url
     * @param request            the request
     * @param response           the response
     * @param httpPost           the http post
     * @param httpGet            the http get
     * @param responseTime       the response time
     * @param errorMessage       the error message
     * @param reqInitTime        the req init time
     * @param responseBo         the response bo
     * @param loggedInUser       the logged in user
     * @param loggedInUserMvnoId the logged in user mvno id
     * @param userNameForAudit   the user name for audit
     * @return the api audits dto
     */
    public ApiAuditsDTO extractDataAndSavePostApiAudits(String url,
                                                        HttpServletRequest request,
                                                        CloseableHttpResponse response,
                                                        HttpPost httpPost,
                                                        HttpGet httpGet,
                                                        Long responseTime,
                                                        String errorMessage,
                                                        LocalDateTime reqInitTime,
                                                        String responseBo,
                                                        String loggedInUser,
                                                        Integer loggedInUserMvnoId,
                                                        String userNameForAudit,
                                                        String referenceNumber) {
        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set request headers :
            JSONObject headersJson = new JSONObject();
            if (httpPost != null) {
                Header[] headerList = httpPost.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }

            //set request http method:
            if (httpPost.getMethod() != null) {
                apiAuditsDTO.setHttpMethod(httpPost.getMethod());
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }
            if(referenceNumber != null){
                apiAuditsDTO.setReferenceNumber(referenceNumber);
            }

            //set request paylaod:
            if (httpPost != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpPost.getURI());
                HttpEntity requestEntity = httpPost.getEntity();
                if (requestEntity != null && requestEntity.getContent().available() > 0) {
                    String requestBody = getContent(requestEntity.getContent());
                    requestJson.put("entity", requestBody);
                }
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpPost.getParams());
                requestJson.put("config", httpPost.getConfig());
                requestJson.put("version", httpPost.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }

            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.getEntity().toString());
                if (responseBo != null)
                    apiAuditsDTO.setResponsePayload(responseBo);
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :
            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
//                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }

            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }

            if (userNameForAudit != null) {
                apiAuditsDTO.setUsernameForAudit(userNameForAudit);
            }

            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());

            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /**
     * Extract data and save get api audits api audits dto.
     *
     * @param url                the url
     * @param request            the request
     * @param response           the response
     * @param httpGet            the http get
     * @param responseTime       the response time
     * @param errorMessage       the error message
     * @param reqInitTime        the req init time
     * @param responseBody       the response body
     * @param loggedInUser       the logged in user
     * @param loggedInUserMvnoId the logged in user mvno id
     * @return the api audits dto
     */
    public ApiAuditsDTO extractDataAndSaveGetApiAudits(String url, HttpServletRequest request, CloseableHttpResponse response, HttpGet httpGet, Long responseTime, String errorMessage, LocalDateTime reqInitTime, String responseBody, String loggedInUser, Integer loggedInUserMvnoId,String referenceNumber) {

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set headers :

            JSONObject headersJson = new JSONObject();
            if (httpGet != null) {
                Header[] headerList = httpGet.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }


            //set http method:
            if (httpGet != null) {
                apiAuditsDTO.setHttpMethod("GET");
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }


            //set request paylaod:
            if (httpGet != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpGet.getURI());
                //requestJson.put("entity",httpGet.getEntity());
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpGet.getParams());
                requestJson.put("config", httpGet.getConfig());
                requestJson.put("version", httpGet.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }


            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.getEntity().toString());
                if (responseBody != null)
                    apiAuditsDTO.setResponsePayload(responseBody);
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }

            if(referenceNumber != null){
                apiAuditsDTO.setReferenceNumber(referenceNumber);
            }

            //set AuthToken if any :


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :
            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
//                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set ratelimit Info :
//        if (response != null) {
//            String rateLimit = response.getFirstHeader("X-RateLimit-Limit").getValue();
//            if (rateLimit != null) {
//                Integer rateLimitLimit = Integer.parseInt(response.getFirstHeader("X-RateLimit-Limit").getValue());
//                if (rateLimitLimit != null) {
//                    apiAuditsDTO.setRateLimitInfo(String.valueOf(rateLimitLimit));
//                } else {
//                    apiAuditsDTO.setRateLimitInfo("NA");
//                }
//            }
//
//        }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }

            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }




            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());

            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Extract data and save post api audits api audits dto.
     *
     * @param url                the url
     * @param request            the request
     * @param response           the response
     * @param httpPut            the http put
     * @param httpGet            the http get
     * @param responseTime       the response time
     * @param errorMessage       the error message
     * @param reqInitTime        the req init time
     * @param responseBody       the response body
     * @param loggedInUser       the logged in user
     * @param loggedInUserMvnoId the logged in user mvno id
     * @param userNameForAudit   the user name for audit
     * @return the api audits dto
     */
    public ApiAuditsDTO extractDataAndSavePostApiAudits(String url, HttpServletRequest request, CloseableHttpResponse response, HttpPut httpPut, HttpGet httpGet, Long responseTime, String errorMessage, LocalDateTime reqInitTime, String responseBody, String loggedInUser, Integer loggedInUserMvnoId, String userNameForAudit,String referenceNumber) {

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set request headers :

            JSONObject headersJson = new JSONObject();
            if (httpPut != null) {
                Header[] headerList = httpPut.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }


            //set request http method:
            if (httpPut.getMethod() != null) {
                apiAuditsDTO.setHttpMethod(httpPut.getMethod());
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }

            if(referenceNumber != null){
              apiAuditsDTO.setReferenceNumber(referenceNumber);
            }


            //set request paylaod:
            if (httpPut != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpPut.getURI());
                requestJson.put("entity", httpPut.getEntity());
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpPut.getParams());
                requestJson.put("config", httpPut.getConfig());
                requestJson.put("version", httpPut.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }


            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(responseBody);
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }

            //set AuthToken if any :


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :
            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :

            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set ratelimit Info :
//        if (response != null) {
//            String rateLimit = response.getFirstHeader("X-RateLimit-Limit").getValue();
//            if (rateLimit != null) {
//                Integer rateLimitLimit = Integer.parseInt(response.getFirstHeader("X-RateLimit-Limit").getValue());
//                if (rateLimitLimit != null) {
//                    apiAuditsDTO.setRateLimitInfo(String.valueOf(rateLimitLimit));
//                } else {
//                    apiAuditsDTO.setRateLimitInfo("NA");
//                }
//            }
//
//        }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }


            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }
            apiAuditsDTO.setUsernameForAudit("NA");


            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());
            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /**
     * Extract data and save delete api audits api audits dto.
     *
     * @param url                the url
     * @param request            the request
     * @param response           the response
     * @param httpDelete         the http delete
     * @param responseTime       the response time
     * @param errorMessage       the error message
     * @param reqInitTime        the req init time
     * @param loggedInUser       the logged in user
     * @param loggedInUserMvnoId the logged in user mvno id
     * @param usernameForAudit   the username for audit
     * @return the api audits dto
     */
    public ApiAuditsDTO extractDataAndSaveDeleteApiAudits(String url, HttpServletRequest request, CloseableHttpResponse response, HttpDelete httpDelete, Long responseTime, String errorMessage, LocalDateTime reqInitTime, String loggedInUser, Integer loggedInUserMvnoId, String usernameForAudit) {

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set headers :

            JSONObject headersJson = new JSONObject();
            if (httpDelete != null) {
                Header[] headerList = httpDelete.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }


            //set http method:
            if (httpDelete != null) {
                apiAuditsDTO.setHttpMethod("DELETE");
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }


            //set request paylaod:
            if (httpDelete != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpDelete.getURI());
                //requestJson.put("entity",httpGet.getEntity());
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpDelete.getParams());
                requestJson.put("config", httpDelete.getConfig());
                requestJson.put("version", httpDelete.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }


            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.toString());
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }

            //set AuthToken if any :


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :

            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set ratelimit Info :
//        if (response != null) {
//            String rateLimit = response.getFirstHeader("X-RateLimit-Limit").getValue();
//            if (rateLimit != null) {
//                Integer rateLimitLimit = Integer.parseInt(response.getFirstHeader("X-RateLimit-Limit").getValue());
//                if (rateLimitLimit != null) {
//                    apiAuditsDTO.setRateLimitInfo(String.valueOf(rateLimitLimit));
//                } else {
//                    apiAuditsDTO.setRateLimitInfo("NA");
//                }
//            }
//
//        }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }

            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }
            if (usernameForAudit != null) {
                apiAuditsDTO.setUsernameForAudit(usernameForAudit);
            }

            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());
            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Measure response time long.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the long
     */
    public long measureResponseTime(LocalDateTime startTime, LocalDateTime endTime) {
        //this method will return diffrence of time
        Duration responseTime = Duration.between(startTime, endTime);
        return responseTime.toMillis();
    }


//    public static String getServerIPAddress() throws UnknownHostException {
//        return serverIpForAudit ;
//    }


    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        try {
            GenericDataDTO genericDataDTO = getListByPagination(generatePageRequest(page, size, sortBy, sortOrder), request);
            if (genericDataDTO.getDataList() != null) {
                List<ApiAuditsDTO> apiAuditsDTOList = genericDataDTO.getDataList();
                apiAuditsDTOList.forEach(apiAuditsDTO -> {
                    if (apiAuditsDTO.getRequestPayload() != null) {
                        if (apiAuditsDTO.getResponsePayload() != null) {
                            //call jsonchecker fucntion
                            apiAuditsDTO.setRequestPayloadInJson(jsoneCheckAndFormat(apiAuditsDTO.getRequestPayload()));
                            apiAuditsDTO.setResponsePayloadInJson(jsoneCheckAndFormat(apiAuditsDTO.getResponsePayload()));
                        }
                        //call jsonchecker function
                        apiAuditsDTO.setRequestPayloadInJson(jsoneCheckAndFormat(apiAuditsDTO.getRequestPayload()));
                    }
                });
                genericDataDTO.setDataList(apiAuditsDTOList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    /**
     * Jsone check and format object.
     *
     * @param data the data
     * @return the object
     */
    public Object jsoneCheckAndFormat(String data) {
        if (isValidJson(data)) {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject;
        } else {
            return data;
        }
    }


    /**
     * Is valid json boolean.
     *
     * @param jsonString the json string
     * @return the boolean
     */
    private static boolean isValidJson(String jsonString) {
        try {
            // Attempt to create a JSON object
            new JSONObject(jsonString);
            return true;
        } catch (Exception e) {
            // Parsing failed, not a valid JSON string
            return false;
        }
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, HttpServletRequest request) {
        try {
            QApiAudits qApiAudits = QApiAudits.apiAudits;
            PageRequest pageRequest = generatePageRequest(page, pageSize, "id", 1);
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            String authTokenHeader = request.getHeader("Authorization");
            BooleanExpression booleanExpression = qApiAudits.isNotNull();
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().contains("any")) {
                    if (!searchModel.getFilterValue().isEmpty()) {
                        booleanExpression = booleanExpression.and(qApiAudits.usernameForAudit.equalsIgnoreCase(searchModel.getFilterValue()).or(qApiAudits.referenceNumber.equalsIgnoreCase(searchModel.getFilterValue())));
                    }
                }
            }
            if (getMvnoId(authTokenHeader) != 1) {
                booleanExpression = booleanExpression.and(qApiAudits.mvnoId.in(getMvnoId(authTokenHeader),1));
            }
            Page<ApiAudits> apiAuditsPage = repository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(new ArrayList<>(apiAuditsPage.getContent()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(apiAuditsPage.getTotalElements());
            genericDataDTO.setPageRecords(apiAuditsPage.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(apiAuditsPage.getNumber() + 1);
            genericDataDTO.setTotalPages(apiAuditsPage.getTotalPages());
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    @Override
    public GenericDataDTO getListByPagination(PageRequest pageRequest, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long mvnoId = getLoggedInUser().getMvnoId().longValue();
        List<Long> mvnoIds = new ArrayList<>();
        mvnoIds.add(mvnoId);
        mvnoIds.add(1L);
        Page<ApiAudits> paginationList = repository.findAllByMvnoIdIn(mvnoIds, pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }


    /**
     * Gets content.
     *
     * @param in the in
     * @return the content
     */
    private String getContent(InputStream in) {
        BufferedReader reader = null;
        String result = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (reader != null)
                    reader.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * Save audit.
     *
     * @param message
     * @param basedUrl              the based url
     * @param response              the response
     * @param httpPost              the http post
     * @param requestInitiationTime the request initiation time
     * @param loggedInUserName      the logged in user name
     * @param mvnoId                the mvno id
     * @param entity                the entity
     * @param body                  the body
     * @param methodType            the methods type
     */
    public void saveAudit(String basedUrl,
                          String status,
                          HttpPost httpPost,
                          LocalDateTime requestInitiationTime,
                          String loggedInUserName,
                          Integer mvnoId,
                          String methodType, String errormessage, String response) {
        LocalDateTime requestCompletionTime = LocalDateTime.now();
        Long responseTime = measureResponseTime(requestInitiationTime, requestCompletionTime);
        extractDataAPIAudits(basedUrl,
                status,
                httpPost,
                responseTime,
                requestInitiationTime,
                loggedInUserName,
                mvnoId,
                methodType,
                errormessage,
                response);
    }

    public void saveAuthAudit(String basedUrl,
                              ResponseEntity<ONUResponseDTO> response,
                              HttpHeaders httpPost,
                              LocalDateTime requestInitiationTime,
                              String loggedInUserName,
                              Integer mvnoId,
                              Map<String, String> body,
                              String methodType, String errormessage) {
        try {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = measureResponseTime(requestInitiationTime, requestCompletionTime);
            extractAuthDataAPIAudits(basedUrl,
                    response,
                    httpPost,
                    responseTime,
                    requestInitiationTime,
                    loggedInUserName,
                    mvnoId,
                    methodType,
                    errormessage);
        } catch (Exception e) {
            logger.error("Exception while processing save audit with message: " + e.getMessage(), e);
        }
    }


    /**
     * Extract data and save post api audits.
     *
     * @param basedUrl              the based url
     * @param response              the response
     * @param httpPost              the http post
     * @param responseTime          the response time
     * @param requestInitiationTime the request initiation time
     * @param data                  the data
     * @param loggedInUserName      the logged in user name
     * @param mvnoId                the mvno id
     * @param methodType            the method type
     */
    public void extractDataAPIAudits(String basedUrl,
                                     String statusCode,
                                     HttpPost httpPost, Long responseTime,
                                     LocalDateTime requestInitiationTime,
                                     String loggedInUserName, Integer mvnoId,
                                     String methodType, String errorMessage, String response) {
        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();
            apiAuditsDTO.setApiUrl(basedUrl);
            JSONObject headersJson = new JSONObject();
            if (httpPost != null) {
                Header[] headerList = httpPost.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }
            if (httpPost != null) {
                apiAuditsDTO.setHttpMethod(methodType);
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response);
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }
            if (loggedInUserName != null) {
                apiAuditsDTO.setUserName(loggedInUserName);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }
            apiAuditsDTO.setIpAddress(serverIpForAudit);
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);
            apiAuditsDTO.setDependencies("NA");
            if (mvnoId != null) {
                apiAuditsDTO.setMvnoId(mvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }
            apiAuditsDTO.setTimeStamp(requestInitiationTime);
            if (statusCode != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(statusCode));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }
            if (httpPost != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpPost.getURI());
                HttpEntity requestEntity = httpPost.getEntity();
                if (requestEntity != null && requestEntity.getContent().available() > 0) {
                    String requestBody = getContent(requestEntity.getContent());
                    requestJson.put("entity", requestBody);
                }
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpPost.getParams());
                requestJson.put("config", httpPost.getConfig());
                requestJson.put("version", httpPost.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }
            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());
            repository.save(apiAudits);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void extractAuthDataAPIAudits(String basedUrl,
                                         ResponseEntity<ONUResponseDTO> response,
                                         HttpHeaders httpPost, Long responseTime,
                                         LocalDateTime requestInitiationTime,
                                         String loggedInUserName, Integer mvnoId,
                                         String methodType, String errorMessage) {
        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();
            apiAuditsDTO.setApiUrl(basedUrl);
            JSONObject headersJson;
            if (httpPost != null) {
                headersJson = new JSONObject(httpPost.toSingleValueMap());
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }
            if (httpPost != null) {
                apiAuditsDTO.setHttpMethod(methodType);
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.toString());
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }
            if (loggedInUserName != null) {
                apiAuditsDTO.setUserName(loggedInUserName);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }
            apiAuditsDTO.setIpAddress(serverIpForAudit);
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);
            apiAuditsDTO.setDependencies("NA");
            if (mvnoId != null) {
                apiAuditsDTO.setMvnoId(mvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }
            apiAuditsDTO.setTimeStamp(requestInitiationTime);
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }
            apiAuditsDTO.setRequestPayload("NA");
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }
            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());
            repository.save(apiAudits);
        } catch (Exception e) {
            logger.error("Exception while Extract Auth Date API Audits with message: " + e.getMessage(), e);
            System.out.println(e.getMessage());
        }
    }

    public void setAuditForCallback(String basedUrl,
                                            Object payload,
                                            ResponseEntity<?> response,
                                            HttpHeaders httpHeaders, Long responseTime,
                                            LocalDateTime requestInitiationTime,
                                            String loggedInUserName, Integer mvnoId,
                                            String methodType, String errorMessage,String usernameForAudit,String referenceNumber){

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();
            apiAuditsDTO.setApiUrl(basedUrl);
            if (httpHeaders != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonHeaders = objectMapper.writeValueAsString(httpHeaders.toSingleValueMap());
                    apiAuditsDTO.setHeaderDetails(jsonHeaders);
                } catch (JsonProcessingException e) {
                    logger.error("Error while parsing HTTP headers: " + e.getMessage(), e);
                    apiAuditsDTO.setHeaderDetails("Invalid JSON Headers");
                }
            } else {
                apiAuditsDTO.setHeaderDetails("NA");
            }
            if (httpHeaders != null) {
                apiAuditsDTO.setHttpMethod(methodType);
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }
            if (response != null && response.getBody() != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = objectMapper.writeValueAsString(response.getBody());
                    apiAuditsDTO.setResponsePayload(jsonResponse);
                } catch (JsonProcessingException e) {
                    logger.error("Error while parsing response body: " + e.getMessage(), e);
                    apiAuditsDTO.setResponsePayload("Invalid JSON Response");
                }
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }
            if (payload != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonPayload = objectMapper.writeValueAsString(payload);
                    apiAuditsDTO.setRequestPayload(jsonPayload);
                } catch (JsonProcessingException e) {
                    logger.error("Error while parsing request payload: " + e.getMessage(), e);
                    apiAuditsDTO.setRequestPayload("Invalid JSON Payload");
                }
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }
            if (loggedInUserName != null) {
                apiAuditsDTO.setUserName(loggedInUserName);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }
            apiAuditsDTO.setIpAddress(serverIpForAudit);
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);
            apiAuditsDTO.setDependencies("NA");
            if (mvnoId != null) {
                apiAuditsDTO.setMvnoId(mvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(1L);
            }
            apiAuditsDTO.setTimeStamp(requestInitiationTime);
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
            }
            if(usernameForAudit != null){
                apiAuditsDTO.setUsernameForAudit(usernameForAudit);
            }
            if(referenceNumber != null){
                apiAuditsDTO.setReferenceNumber(referenceNumber);
            }
            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());
            repository.save(apiAudits);
        } catch (Exception e) {
            logger.error("Exception while Extract Auth Date API Audits with message: " + e.getMessage(), e);
            System.out.println(e.getMessage());
        }
    }

    public ApiAuditsDTO extractDataAndSaveGetApiAuditsForAirtel(String url, HttpServletRequest request, CloseableHttpResponse response, HttpGet httpGet, Long responseTime, String errorMessage, LocalDateTime reqInitTime, String responseBody, String loggedInUser, Integer loggedInUserMvnoId,String referenceNumber) {

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set headers :

            JSONObject headersJson = new JSONObject();
            if (httpGet != null) {
                Header[] headerList = httpGet.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }


            //set http method:
            if (httpGet != null) {
                apiAuditsDTO.setHttpMethod("GET");
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }


            //set request paylaod:
            if (httpGet != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpGet.getURI());
                //requestJson.put("entity",httpGet.getEntity());
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpGet.getParams());
                requestJson.put("config", httpGet.getConfig());
                requestJson.put("version", httpGet.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }


            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.getEntity().toString());
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }

            apiAuditsDTO.setUsernameForAudit("Airtel");


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }

            if(referenceNumber != null){
                apiAuditsDTO.setReferenceNumber(referenceNumber);
            }

            //set AuthToken if any :


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :
            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set ratelimit Info :
//        if (response != null) {
//            String rateLimit = response.getFirstHeader("X-RateLimit-Limit").getValue();
//            if (rateLimit != null) {
//                Integer rateLimitLimit = Integer.parseInt(response.getFirstHeader("X-RateLimit-Limit").getValue());
//                if (rateLimitLimit != null) {
//                    apiAuditsDTO.setRateLimitInfo(String.valueOf(rateLimitLimit));
//                } else {
//                    apiAuditsDTO.setRateLimitInfo("NA");
//                }
//            }
//
//        }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(2L);
            }

            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }




            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());

            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public ApiAuditsDTO extractDataAndSaveGetApiAuditsForMomoPay(String url, HttpServletRequest request, CloseableHttpResponse response, HttpGet httpGet, Long responseTime, String errorMessage, LocalDateTime reqInitTime, String responseBody, String loggedInUser, Integer loggedInUserMvnoId,String referenceNumber) {

        try {
            ApiAuditsDTO apiAuditsDTO = new ApiAuditsDTO();

            //set url :
            apiAuditsDTO.setApiUrl(url);

            //set headers :

            JSONObject headersJson = new JSONObject();
            if (httpGet != null) {
                Header[] headerList = httpGet.getAllHeaders();
                for (Header header : headerList) {
                    headersJson.put(header.getName(), header.getValue());
                }
                apiAuditsDTO.setHeaderDetails(headersJson.toString());
            }


            //set http method:
            if (httpGet != null) {
                apiAuditsDTO.setHttpMethod("GET");
            } else {
                apiAuditsDTO.setHttpMethod("NA");
            }


            //set request paylaod:
            if (httpGet != null) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("uri", httpGet.getURI());
                //requestJson.put("entity",httpGet.getEntity());
                requestJson.put("headerGroup", headersJson.toString());
                requestJson.put("params", httpGet.getParams());
                requestJson.put("config", httpGet.getConfig());
                requestJson.put("version", httpGet.getProtocolVersion());
                apiAuditsDTO.setRequestPayload(requestJson.toString());
            } else {
                apiAuditsDTO.setRequestPayload("NA");
            }


            //set response payload:
            if (response != null) {
                apiAuditsDTO.setResponsePayload(response.getEntity().toString());
            } else {
                apiAuditsDTO.setResponsePayload("NA");
            }


            //set response status code
            if (responseTime != null) {
                apiAuditsDTO.setResponseTime(responseTime.toString() + " ms");
            } else {
                apiAuditsDTO.setResponseTime("NA");
            }

            if(referenceNumber != null){
                apiAuditsDTO.setReferenceNumber(referenceNumber);
            }

            //set AuthToken if any :


            //set username :
            if (loggedInUser != null) {
                apiAuditsDTO.setUserName(loggedInUser);
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setUserName(jwtUtil.getLoggedInUser().getUsername());
            } else {
                apiAuditsDTO.setUserName("");
            }


            //set remoteAddress :
            apiAuditsDTO.setIpAddress(serverIpForAudit);


            //set error message :
            if (errorMessage != null) {
                apiAuditsDTO.setErrorMessage(errorMessage);
                apiAuditsDTO.setResponsePayload(errorMessage);
            }


            //set ratelimit Info :
//        if (response != null) {
//            String rateLimit = response.getFirstHeader("X-RateLimit-Limit").getValue();
//            if (rateLimit != null) {
//                Integer rateLimitLimit = Integer.parseInt(response.getFirstHeader("X-RateLimit-Limit").getValue());
//                if (rateLimitLimit != null) {
//                    apiAuditsDTO.setRateLimitInfo(String.valueOf(rateLimitLimit));
//                } else {
//                    apiAuditsDTO.setRateLimitInfo("NA");
//                }
//            }
//
//        }


            //set environment Info :
            apiAuditsDTO.setEnvironmentInfo(environmentInfo);

            //set dependecies :
            apiAuditsDTO.setDependencies("NA");

            //set mvnoId
            if (loggedInUserMvnoId != null) {
                apiAuditsDTO.setMvnoId(loggedInUserMvnoId.longValue());
            } else if (jwtUtil.getLoggedInUser() != null) {
                apiAuditsDTO.setMvnoId(jwtUtil.getLoggedInUser().getMvnoId().longValue());
            } else {
                apiAuditsDTO.setMvnoId(1L);
            }

            //set TimeStamp
            apiAuditsDTO.setTimeStamp(reqInitTime);

            //set response code :
            if (response != null) {
                apiAuditsDTO.setHttpStatusCode(String.valueOf(response.getStatusLine().getStatusCode()));
            } else {
                apiAuditsDTO.setHttpStatusCode("NA");
            }

            apiAuditsDTO.setUsernameForAudit("MoMo Pay");




            ApiAudits apiAudits = apiAuditsMapper.dtoToDomain(apiAuditsDTO, new CycleAvoidingMappingContext());

            repository.save(apiAudits);
            return apiAuditsDTO;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

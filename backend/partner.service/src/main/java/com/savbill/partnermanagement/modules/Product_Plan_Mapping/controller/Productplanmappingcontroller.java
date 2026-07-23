package com.savbill.partnermanagement.modules.Product_Plan_Mapping.controller;

import com.savbill.partnermanagement.modules.PlanGroup.service.PlanGroupService;
import com.savbill.partnermanagement.modules.Product_Plan_Mapping.service.ProductplanmappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class Productplanmappingcontroller {
    private static String MODULE = " [Productplanmappingcontroller] ";
    private static final Logger logger = LoggerFactory.getLogger(Productplanmappingcontroller.class);
    @Autowired
    private ProductplanmappingService mappingService;
    @Autowired
    private PlanGroupService planGroupService;

//    @GetMapping("/getproductfromplan")
//    public List<Productplanmapping> getproductfromplan(@RequestParam("id") Long id) throws Exception {
//        List<Productplanmapping> list = new ArrayList<>();
//        list = mappingService.getallfromplan(id);
//        return list;
//    }
//    // Get Product Category Details By PlanId
//    @GetMapping("/getProductCategoryByPlanId")
//    public GenericDataDTO getProductCategoryByPlanId(@RequestParam("mappingId") Long mappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(mappingService.getProductCategoryByPlanId(mappingId));
//        } catch (Exception ex){
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    // Get Product By PlanId
//    @GetMapping("/getProductByPlanId")
//    public GenericDataDTO getProductByPlanId(@RequestParam("mappingId") Integer mappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(mappingService.getProductByPlanId(mappingId));
//        } catch (Exception ex){
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    //Delete Product Plan and Plan Group Mapping by Plan Group Id and Plan Id
//    @DeleteMapping("/deleteProductPlanGroupMapping")
//    public ResponseEntity<?> deleteProductPlanGroupMapping(@RequestParam(name = "planGroupId", required = true) Long planGroupId, @RequestParam (name = "planId", required = true) Long planId, HttpServletRequest request) {
//        MDC.put("type", "delete");
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            planGroupService.deleteProductPlanGroupMapping(planGroupId, planId);
//            Integer responseCode = APIConstants.SUCCESS;
//            response.put(APIConstants.MESSAGE, "DB Mapping has been deleted successfully.");
//            logger.info("deleting PlanGroupMappingById with name   :   request: { From : {}}; Response : {{}}", MODULE, responseCode, response);
//            MDC.remove("type");
//            return apiResponse(responseCode, response);
//        } catch (Exception e) {
//            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to deletePlanGroupMappingById with name :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, responseCode, response, e.getStackTrace());
//            MDC.remove("type");
//            return apiResponse(responseCode, response);
//        }
//    }

}

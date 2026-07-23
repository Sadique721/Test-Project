package com.savbill.integrationsystem.customizedselfcare;


//@RestController(value = "CustomizedSelfCareController")
//@RequestMapping()
//public class CustomizedSelfCareController {
//
//    String getModuleNameForLog() {
//        return "CustomizedSelfCareController[]";
//    }
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomizedSelfCareController.class);
//
//    @GetMapping(value = "/api/selfCare/category")
//    public Map<String, Object>  fetchAggregationReport() {
//        MDC.put("type", "Fetch");
//        try {
//    	    String responsePacket="{\"CategoryList\": [ { \"Id\": 10113,\"Name\": \"Technical Support Center\"},{\"Id\": 10132,\"Name\": \"Sales Call Center\"}]}";
//    	    JSONObject jsonObject = new JSONObject(responsePacket);
//    	    return jsonObject.toMap();
//		} catch (Exception e) {
//            logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//        }
//        MDC.remove("type");
//        return null;
//    }
//}

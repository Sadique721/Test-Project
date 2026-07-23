package com.savbill.commonGateway.moules.DemoGraphicMapping.controller;

import com.savbill.commonGateway.common.controller.BaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.moules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.savbill.commonGateway.moules.DemoGraphicMapping.model.DemoGraphicMappingDTO;
import com.savbill.commonGateway.moules.DemoGraphicMapping.service.DemoGraphicMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class DemoGraphicMappingController extends BaseController<DemoGraphicMappingDTO> {
    private static final String MODULE = " [DemoGraphicMappingController] ";

    @Autowired
    private DemoGraphicMappingService demoGraphicMappingService;

//    public DemoGraphicMappingController(DemoGraphicMappingService service) {
//        super(service);
//    }
private static final Logger logger = LoggerFactory.getLogger(DemoGraphicMappingController.class);
    @GetMapping("/getdemographicmapping")
   public ResponseEntity<?> getAll()  {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        List<DemoGraphicMappingTable> demoGraphicMappingTables  = demoGraphicMappingService.getAll();


            if (!demoGraphicMappingTables.isEmpty()) {

                response.put("demographicmappingtable", demoGraphicMappingTables);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("get Demographic mapping is Successfull  :  request: { From : {}}; Response : {{}}",MODULE ,RESP_CODE);

            } else {

                RESP_CODE = HttpStatus.NOT_FOUND.value();
                response.put(APIConstants.ERROR_TAG, "DATA NOT FOUND");

                logger.error("Unable to search :  request: { From : {}}; Response : {{}};Error :{} ;",MODULE,RESP_CODE,response);
            }
                    return apiResponse(RESP_CODE, response);
   }

}

package com.savbill.revenuemanagement.productmanagement.ServiceParameterMapping.Controller;/*
package com.savbill.apigw.modules.ServiceParameterMapping.Controller;

import com.savbill.apigw.constants.UrlConstants;
import com.savbill.apigw.core.dto.GenericDataDTO;
import com.savbill.apigw.modules.ServiceParameterMapping.Service.ServiceParamMappingService;
import com.savbill.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.savbill.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SERVICE_PARAM_MAPPING)
public class ServiceParamMappingController {


    @Autowired
    ServiceParamMappingService serviceParamMappingService;

    @GetMapping("/getParamMappingByServiceId")
    public GenericDataDTO getParamMappingByServiceId(@RequestParam Long serviceId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(serviceParamMappingService.getParamsByServiceId(serviceId));
        return genericDataDTO;
    }

    @PostMapping("/saveServiceParamValue")
    private GenericDataDTO saveServiceParamValue(@RequestBody ServiceParamMappingDTO serviceParamMappingDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        ServiceParamMapping serviceParamMapping = new ServiceParamMapping();
        serviceParamMapping = serviceParamMappingService.saveServiceParamValue(serviceParamMappingDTO);
        genericDataDTO.setData(serviceParamMapping);
        return genericDataDTO;
    }

    @PostMapping("/saveServiceParamValueList")
    private GenericDataDTO saveServiceParamValueList(@RequestBody List<ServiceParamMappingDTO> serviceParamMappingDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ServiceParamMapping> serviceParamMappingList = new ArrayList<>();
        serviceParamMappingList = serviceParamMappingService.saveServiceParamValueList(serviceParamMappingDTO);
        genericDataDTO.setDataList(serviceParamMappingList);
        return genericDataDTO;
    }
}
*/

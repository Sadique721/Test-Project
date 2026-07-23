package com.savbill.cpm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.CommonList.model.CommonListDTO;
import com.savbill.cpm.modules.CommonList.service.CommonListService;

import java.lang.reflect.Field;

@Service
public class StatusMapperUtil {

    @Autowired
    private CommonListService commonListService;

    public String getMappedStatus(Object obj, Field field, String value) throws NoSuchFieldException {
//        String returnValue = null;
//        if (obj instanceof Customers) {
//            String fieldName = field.getName();
//            switch (fieldName) {
//                case "status":
//                    List<CommonListDTO> commonList = commonListService.getCommonListByType("custStatus");
//                    if (null != commonList && 0 < commonList.size()) {
//                        commonList = commonList.stream().filter(dto -> null != value && value.equalsIgnoreCase(dto.getValue()))
//                                .collect(Collectors.toList());
//                        if (null != commonList && 0 < commonList.size())
//                            returnValue = commonList.get(0).getText();
//                    }
//            }
//        }
//        return returnValue;

//** change for improve perfomance

        if (obj instanceof Customers) {
            String fieldName = field.getName();
            if ("status".equalsIgnoreCase(fieldName)) {
                return commonListService.getCommonListByType("custStatus").stream()
                        .filter(dto -> value != null && value.equalsIgnoreCase(dto.getValue()))
                        .map(CommonListDTO::getText)
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }
}

package com.savbill.ticketmanagement.core.modules.tickets.service;


import com.savbill.ticketmanagement.core.modules.tickets.model.LiveUserServiceAreaWiseDetailsModel;
import com.savbill.ticketmanagement.core.modules.tickets.query.LiveUserNetworkDetailsQueryScript;
import com.savbill.ticketmanagement.core.repository.CustomRepository;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveCustomerNetworkDetailsService {

    public static String MODULE = " [LiveCustomerNetworkDetailsService] ";

    @Autowired
    private CustomRepository<LiveUserServiceAreaWiseDetailsModel> customRepository;

    public List<LiveUserServiceAreaWiseDetailsModel> getCustomerWiseNetworkDetailsFromLiveUser(Integer custId) {
        String SUBMODULE = MODULE + " [getCustomerWiseNetworkDetailsFromLiveUser()] ";
        try {
            List<LiveUserServiceAreaWiseDetailsModel> responseList = customRepository.getResultOfQuery(LiveUserNetworkDetailsQueryScript
                    .activeNetworkUserDetailsByCustomer(custId), LiveUserServiceAreaWiseDetailsModel.class);
            if (null != responseList && 0 < responseList.size()) {
                return responseList;
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
}

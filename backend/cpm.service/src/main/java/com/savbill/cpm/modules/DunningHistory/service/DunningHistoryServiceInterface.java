package com.savbill.cpm.modules.DunningHistory.service;

import com.savbill.cpm.modules.DunningHistory.domain.DunningHistory;

import java.util.List;

public interface DunningHistoryServiceInterface {

    List<DunningHistory> findAllCustomerDunningHistory();
}

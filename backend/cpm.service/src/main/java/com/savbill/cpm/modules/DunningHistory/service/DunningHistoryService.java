package com.savbill.cpm.modules.DunningHistory.service;

import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.DunningHistory.domain.DunningHistory;
import com.savbill.cpm.modules.DunningHistory.domain.QDunningHistory;
import com.savbill.cpm.modules.DunningHistory.repository.DunningHistoryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class DunningHistoryService{



//    public Integer  AGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
    @Autowired
    private DunningHistoryRepository dunningHistoryRepository;





    public Page<DunningHistory> findAllDunningHistory(PaginationRequestDTO requestDTO){
        QDunningHistory qDunningHistory = QDunningHistory.dunningHistory;
        BooleanExpression booleanExpression = qDunningHistory.isNotNull();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));;
        Page<DunningHistory> findAllDunningHistory = dunningHistoryRepository.findAll(booleanExpression , pageable);
        return findAllDunningHistory;
    }


    public Page<DunningHistory> findAllByPartnerOrCustomerDunningHistory(PaginationRequestDTO requestDTO){
        QDunningHistory qDunningHistory = QDunningHistory.dunningHistory;
        BooleanExpression booleanExpression = qDunningHistory.isNotNull();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("customer")){
            booleanExpression = booleanExpression.and(qDunningHistory.custid.eq(Integer.parseInt(requestDTO.getFilters().get(0).getFilterValue())));
        }
        if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("partner")){
            booleanExpression = booleanExpression.and(qDunningHistory.partnerid.eq(Long.parseLong(requestDTO.getFilters().get(0).getFilterValue())));
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<DunningHistory> findAllDunningHistory = dunningHistoryRepository.findAll(booleanExpression , pageable);
        return findAllDunningHistory;
    }








}

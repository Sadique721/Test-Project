package com.savbill.cpm.modules.dashboard;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.modules.tickets.domain.Case;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Integer> getAllCounts(String authHeader,Integer currentAssigneeId);

    Map<String, String> typeWiseCustomerCount(Long mvnoId);

    Map<String, String> getStatusWiseCount(Long mvnoId);

    Map<String, String> getNewlyActivatedCustomer(Long mvnoId);

    Map<String, Integer> getPlanWiseCustomer(Long mvnoId);

    Map<String, Double> getMonthWiseCollection(Long mvnoId, String year);

    Map<String, Double> pendingApprovalPayments(Long mvnoId);

    Map<String, Double> nextTenDaysReceivablePayment(Long mvnoId) throws ParseException;

    Map<String, Double> partnerWisePayment(Long mvnoId) throws ParseException;

    Map<String, Long> totalOpenTickets(Long mvnoId);

    Map<String, Map<String, Long>> monthWiseTicketCount(Long mvnoId, String year);

    Map<String, Map<String, String>> staffWiseTicketCount(Long mvnoId);

    Map<String, Map<String, String>> teamWiseTicketCount(Long mvnoId);

    List<Customers> nextTenDaysRenewableCustomer(Long mvnoId) throws ParseException;

    Map<String, Double> monthWiseVolumeUsages(Long mvnoId, Integer year);

    Map<String, Double> monthWiseTimeUsages(Long mvnoId, Integer year);

    Long connectedUser(Long mvnoId);

    List<Case> overDueTicketList(Long mvnoId) throws ParseException;

    Map<String, Double> monthWiseAGRPayable(Long mvnoId, String year);

    Map<String, Double> monthWiseTDSPayable(Long mvnoId, String year);

    Map<String, Double> partnerWiseTDSDetails(Long mvnoId, String year);

    Map<String, Map<String, Double>> monthWiseTotalDetails(Long mvnoId, String year);

    Map<String, Double> topFivePartnerCommissionWise(Long mvnoId, String year);

    List<Map<String, String>> availableInventoryProductWise(Long mvnoId);

    List<Map<String, String>> inventoryAlert(Long mvnoId);

    List<Map<String, String>> staffAndProductWiseInventories(Long mvnoId);

    List<Map<String, String>> wareHouseAndProductWiseInventories(Long mvnoId);
    GenericDataDTO getLeadApprovals( PaginationRequestDTO paginationRequestDTO);
    GenericDataDTO getCustomerDocApprovals ( PaginationRequestDTO paginationRequestDTO);
    GenericDataDTO getProductQtyByStaff ( PaginationRequestDTO paginationRequestDTO, Long mvnoId);
    GenericDataDTO getProductQtyByWarehouse (PaginationRequestDTO paginationRequestDTO, Long mvnoId);
}

package com.savbill.revenuemanagement.productmanagement.parentchildmapping;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;

@Service
public class ParentChildMappinService {
    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;



    private final Logger log = LoggerFactory.getLogger(ParentChildMappinService.class);
    @Transactional
    public void saveParentChildMapping(ParentChildMappingRel childMappingRel) {
        try {
            log.info("Attempting to save parent-child mapping: {}", childMappingRel);
            parentChildMappingRepo.saveAndFlush(childMappingRel);
            log.info("Parent-child mapping saved successfully with ID: {}", childMappingRel.getId());
        } catch (Exception e) {
            log.error("Error saving parent-child mapping: {}", childMappingRel, e);
            throw new RuntimeException("Failed to save parent-child mapping", e);
        }
    }

    public Double getWalletAmount(Integer childId) {
        Double walletAmount = customerLedgerDtlsRepository.findwalletAmountById(childId);
        if (walletAmount == null) {
            walletAmount = 0.00;
        }
        walletAmount = Math.floor(walletAmount * 100) / 100.0;
        return walletAmount;
    }

    public GenericDataDTO getCustLedgerByChild(Integer childId, PaginationRequestDTO paginationRequestDTO) {
        DecimalFormat df = new DecimalFormat("#.##");

        // Step 1: Fetch ALL records ordered by ID
        List<CustomerLedgerDtls> fullLedger = customerLedgerDtlsRepository
                .findAllLedgerByChildId(childId);

        // Step 2: Calculate running balance for full list
        double runningBalance = 0.0;
        for (CustomerLedgerDtls txn : fullLedger) {
            if (CommonConstants.TRANS_TYPE_CREDIT.equalsIgnoreCase(txn.getTranstype())) {
                runningBalance += txn.getAmount();
            } else if (CommonConstants.TRANS_TYPE_DEBIT.equalsIgnoreCase(txn.getTranstype())) {
                runningBalance -= txn.getAmount();
            }
            txn.setBalAmount(Double.parseDouble(df.format(runningBalance)));
        }

        // Step 3: Paginate manually in-memory
        int page = paginationRequestDTO.getPage() > 0 ? paginationRequestDTO.getPage() - 1 : 0;
        int pageSize = paginationRequestDTO.getPageSize();
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, fullLedger.size());

        List<CustomerLedgerDtls> pageContent = fullLedger.subList(fromIndex, toIndex);

        // Step 4: Wrap it in a Page-like object and return
        Page<CustomerLedgerDtls> pagedResult = new PageImpl<>(pageContent,
                PageRequest.of(page, pageSize), fullLedger.size());

        return convertPagableResponseToGenericDataDTO(pagedResult);
    }


    public <T> GenericDataDTO convertPagableResponseToGenericDataDTO(Page<? super T> paginationList){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }



}

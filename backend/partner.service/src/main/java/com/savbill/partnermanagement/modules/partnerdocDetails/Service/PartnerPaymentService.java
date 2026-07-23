package com.savbill.partnermanagement.modules.partnerdocDetails.Service;


import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.modules.partner.entity.PartnerPayment;
import com.savbill.partnermanagement.modules.partner.entity.QPartnerPayment;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.partnerdocDetails.mapper.PartnerPaymentMapper;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.*;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.*;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerCreditDocRepository;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerCreditDocumentRepository;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerPaymentRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerPaymentService extends ExBaseAbstractService<PartnerPaymentDTO, PartnerPayment, Long> {

    public PartnerPaymentService(PartnerPaymentRepository repository, PartnerPaymentMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private PartnerPaymentMapper partnerPaymentMapper;

    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerCreditDocumentRepository partnerCredRepo;
    @Autowired
    private PartnerCreditDocRepository partnerCreditDocRepository;

    @Autowired
    PartnerRepository partnerrepo;



    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public GenericDataDTO getAllPartnerPayment(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        ApplicationLogger.logger.info("get All Partner Payment called");
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPartnerPayment.isNotNull().and(qPartnerPayment.isDeleted.eq(false));
        if (getMvnoIdFromCurrentStaff() != 1)
            ApplicationLogger.logger.info("get Mvno Id From Current Staff() != 1");
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            ApplicationLogger.logger.info("get BU Ids From Current Staff() != null && getBUIdsFromCurrentStaff().size() > 0");
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(1).or(qPartnerPayment.partner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<PartnerPayment> paginationList = partnerPaymentRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                ApplicationLogger.logger.info("data: " + data);
                data.setPartnerName(data.getPartner().getName());
                return data;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public GenericDataDTO getAllPartnerCredit(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerCreditDocument qPartnerCreditDocument = QPartnerCreditDocument.partnerCreditDocument;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qPartnerCreditDocument.isNotNull().and(qPartnerCreditDocument.isDelete.eq(false));
        Page<PartnerCreditDocument> paginationList = partnerCredRepo.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }


    public GenericDataDTO getAllPartnerInvoice(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "billdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerDebitDocument qPartnerDebitDocument = QPartnerDebitDocument.partnerDebitDocument ;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qPartnerDebitDocument.isNotNull().and(qPartnerDebitDocument.isDelete.eq(false));
        Page<PartnerDebitDocument> paginationList = partnerCreditDocRepository.findAll(booleanExpression, pageRequest);
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

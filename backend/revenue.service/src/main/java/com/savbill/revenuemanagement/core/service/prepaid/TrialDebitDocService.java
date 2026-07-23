package com.savbill.revenuemanagement.core.service.prepaid;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.SearchTrialDebitDocsPojo;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.invoice.TrialDebitDocumentPojo;
import com.savbill.revenuemanagement.core.entity.Billrun.SearchTrialDebitDocs;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrailDebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrialDebitDocService extends AbstractService<TrialDebitDocument, TrialDebitDocumentPojo, Integer> {

    @Autowired
    private TrialDebitDocRepository entityRepository;

    @Autowired
    private SubscriberService customersService;

    @Autowired
    private CreditDocRepository creditDocRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Override
    protected JpaRepository<TrialDebitDocument, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocument> getAllEntities(Integer pageNumber, int pageSize) {
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocument> searchByBillRunId(String billRunId) {
        return entityRepository.findByBillrunid(Integer.valueOf(billRunId));
    }

    public List<TrialDebitDocument> searchCustomerId(Integer custId) {
        return entityRepository.findByCustomerId(Integer.valueOf(custId));
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '2')")
    public TrialDebitDocument getById(Integer id) {
        return entityRepository.getOne(id);
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '2')")
    public SearchTrialDebitDocs getSearchTrialDebitDocsForInvoice() {
        return new SearchTrialDebitDocs();
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '1')")
    public Page<TrialDebitDocumentPojo> searchTrialInvoice(SearchTrialDebitDocsPojo searchTrialDebitDocsPojo, PaginationRequestDTO paginationDTO, boolean isFromCustomerPortal, boolean isInvoiceVoid, boolean isOutstandingDue) throws Exception {
        pageRequest = generatePageRequest(paginationDTO.getPage(), paginationDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);

        LocalDate startDate = searchTrialDebitDocsPojo.getBillfromdate();
    	LocalDate endDate = searchTrialDebitDocsPojo.getBilltodate();

//        QTrialDebitDocument qTrialDebitDocument = QTrialDebitDocument.trialDebitDocument;
//        BooleanExpression exp = qTrialDebitDocument.isNotNull();

        String queryForDebitDocument = "SELECT deb FROM TrialDebitDocument deb  JOIN Customers cust ON deb.customer=cust.id WHERE deb.isDelete = false ";
        String countQueryDebitDocument = "SELECT count(*) FROM TrialDebitDocument deb  JOIN Customers cust ON deb.customer=cust.id WHERE deb.isDelete = false ";
        if (searchTrialDebitDocsPojo.getBillrunid() != null) {
           // exp = exp.and(qTrialDebitDocument.billrunid.eq(searchTrialDebitDocsPojo.getBillrunid()));
            queryForDebitDocument+=" AND (deb.billrunid="+searchTrialDebitDocsPojo.getBillrunid()+") ";
            countQueryDebitDocument+="AND (deb.billrunid="+searchTrialDebitDocsPojo.getBillrunid()+") ";
        }

        if (startDate != null) {
           // exp = exp.and(qTrialDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
            queryForDebitDocument+=" AND (deb.startdate>='"+startDate.atTime(00, 00, 00)+"') ";
            countQueryDebitDocument+="AND (deb.startdate>='"+startDate.atTime(00, 00, 00)+"') ";
        }
        if (endDate !=null) {
        	//exp = exp.and(qTrialDebitDocument.billdate.loe(endDate.atTime(23,59, 59)));
            queryForDebitDocument+=" AND (deb.endate>='"+endDate.atTime(00, 00, 00)+"') ";
            countQueryDebitDocument+="AND (deb.endate>='"+endDate.atTime(00, 00, 00)+"') ";
        }
        if (searchTrialDebitDocsPojo.getCustname() != null && !searchTrialDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
      //  	exp = exp.and(qTrialDebitDocument.customer.firstname.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustname()));

           //Pending
            queryForDebitDocument+=" AND (deb.endate>='"+endDate.atTime(00, 00, 00)+"') ";
            countQueryDebitDocument+="AND (deb.endate>='"+endDate.atTime(00, 00, 00)+"') ";
        }
        if (searchTrialDebitDocsPojo.getCustname() != null && !searchTrialDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
            queryForDebitDocument+=" AND (cust.username="+searchTrialDebitDocsPojo.getCustname()+") ";
            countQueryDebitDocument+="AND (cust.username="+searchTrialDebitDocsPojo.getCustname()+") ";
            //exp = exp.and(qTrialDebitDocument.customer.lastname.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustname()));
        }
        if (searchTrialDebitDocsPojo.getCustmobile() != null && !searchTrialDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
        	//exp = exp.and(qTrialDebitDocument.customer.mobile.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustmobile()));
            queryForDebitDocument+=" AND (cust.mobile = "+searchTrialDebitDocsPojo.getCustname()+") ";
            countQueryDebitDocument+="AND (cust.mobile ="+searchTrialDebitDocsPojo.getCustname()+") ";
        }

        if(searchTrialDebitDocsPojo.getCustomerid()!=null) {
//        	exp = exp.and(qTrialDebitDocument.customer.eq(customersService.get(searchTrialDebitDocsPojo.getCustomerid())));
            queryForDebitDocument+=" AND (cust.id ="+searchTrialDebitDocsPojo.getCustomerid()+") ";
            countQueryDebitDocument+="AND (cust.id ="+searchTrialDebitDocsPojo.getCustomerid()+") ";
        }

        if (searchTrialDebitDocsPojo.getDocnumber() != null && !searchTrialDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
        //	exp = exp.and(qTrialDebitDocument.docnumber.equalsIgnoreCase(searchTrialDebitDocsPojo.getDocnumber()));
            queryForDebitDocument+=" AND (deb.docnumber="+searchTrialDebitDocsPojo.getCustname()+") ";
            countQueryDebitDocument+="AND (deb.docnumber ="+searchTrialDebitDocsPojo.getCustname()+") ";
        }

        queryForDebitDocument+=" AND (deb.billrunstatus!='"+CommonConstants.DEBIT_DOC_STATUS.VOID+"') ";
        countQueryDebitDocument+="AND (deb.billrunstatus!='"+CommonConstants.DEBIT_DOC_STATUS.VOID+"') ";

        if (isOutstandingDue) {
            queryForDebitDocument += " AND (deb.totalamount - COALESCE(deb.adjustedAmount, 0) > 0) ";
            countQueryDebitDocument += " AND (deb.totalamount - COALESCE(deb.adjustedAmount, 0) > 0) ";
            // exclude cancelled payment status (case insensitive)
            queryForDebitDocument += " AND (LOWER(deb.paymentStatus) != 'cancelled') ";
            countQueryDebitDocument += " AND (LOWER(deb.paymentStatus) != 'cancelled') ";
        }

//        Page response = entityRepository.findAll(builder1, pageRequest);
//        List<TrialDebitDocumentPojo> trialDebitDocumentPojos = convertResponseModelIntoPojo(response.getContent());
        queryForDebitDocument += " order by deb.id DESC";
        Query q = entityManager.createQuery(queryForDebitDocument, TrialDebitDocument.class);
        List<TrialDebitDocument> debitdocList = q.getResultList();
        List<TrialDebitDocumentPojo> leadMasterPojoList = new ArrayList<TrialDebitDocumentPojo>();
        for(TrialDebitDocument debitDocument: debitdocList) {
            String planGroup = entityRepository.findPlanGroupByTrailDebitDocumentId(debitDocument.getId());
            boolean isBoosterplan = false;
            if(planGroup != null && (planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) || planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) || planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_DTV_ADDON))){
                isBoosterplan = true;
            }
            TrialDebitDocumentPojo debitDocSearchPojo = convertTrialDebitDocumentModelToTrialDebitDocumentPojo(debitDocument, isBoosterplan);
            leadMasterPojoList.add(debitDocSearchPojo);
        }
        if(isFromCustomerPortal) {
            leadMasterPojoList = leadMasterPojoList.stream().filter(a -> a.isBoosterplan() == false).collect(Collectors.toList());
        }
        Query queryTotal = entityManager.createQuery(countQueryDebitDocument);
        long countResult = (long) queryTotal.getSingleResult();

        int startIndex = pageRequest.getPageNumber() * pageRequest.getPageSize();

        int endIndex = Math.min(startIndex + pageRequest.getPageSize(), leadMasterPojoList.size());
        return new PageImpl<TrialDebitDocumentPojo>(leadMasterPojoList.subList(startIndex, endIndex), PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize()),
                countResult);
    }

    public TrialDebitDocumentPojo convertTrialDebitDocumentModelToTrialDebitDocumentPojo(TrialDebitDocument trialDebitDocument, boolean isBoosterplan) throws Exception {

        TrialDebitDocumentPojo pojo = null;
        if (trialDebitDocument != null) {
            pojo = new TrialDebitDocumentPojo();
            pojo.setId(trialDebitDocument.getId());
            pojo.setDocnumber(trialDebitDocument.getDocnumber());
            if (trialDebitDocument.getCustomer() != null) {
                pojo.setCustomerPojo(customersService.convertCustomersModelToCustomersPojo(trialDebitDocument.getCustomer()));
            }
            pojo.setBilldate(trialDebitDocument.getBilldate());
            pojo.setCreatedate(trialDebitDocument.getCreatedate());
            pojo.setStartdate(trialDebitDocument.getStartdate());
            pojo.setEndate(trialDebitDocument.getEndate());
            pojo.setDuedate(trialDebitDocument.getDuedate());
            pojo.setLatepaymentdate(trialDebitDocument.getLatepaymentdate());
            pojo.setSubtotal(trialDebitDocument.getSubtotal());
            pojo.setTax(trialDebitDocument.getTax());
            pojo.setDiscount(trialDebitDocument.getDiscount());
            pojo.setTotalamount(trialDebitDocument.getTotalamount());
            pojo.setPreviousbalance(trialDebitDocument.getPreviousbalance());
            pojo.setLatepaymentfee(trialDebitDocument.getLatepaymentfee());
            pojo.setCurrentcredit(trialDebitDocument.getCurrentcredit());
            pojo.setCurrentdebit(trialDebitDocument.getCurrentdebit());
            pojo.setTotaldue(trialDebitDocument.getTotaldue());
            pojo.setAmountinwords(trialDebitDocument.getAmountinwords());
            pojo.setDueinwords(trialDebitDocument.getDueinwords());
            pojo.setBillrunid(trialDebitDocument.getBillrunid());
            pojo.setBillrunstatus(trialDebitDocument.getBillrunstatus());
         //   pojo.setDocument(trialDebitDocument.getDocument());
            pojo.setCreatedByName(trialDebitDocument.getCreatedByName());
            pojo.setLastModifiedByName(trialDebitDocument.getLastModifiedByName());
            pojo.setBillableToName(trialDebitDocument.getBillableToName());
            if (trialDebitDocument.getCustomer() != null) {
                pojo.setCustid(trialDebitDocument.getCustomer().getId());
                pojo.setCustomerName(trialDebitDocument.getCustomer().getFullName(trialDebitDocument.getCustomer()));
                pojo.setCustType(trialDebitDocument.getCustomer().getCusttype());
            }
            pojo.setBoosterplan(isBoosterplan);

            pojo.setDebitDocDetails(trialDebitDocument.getTrialDebitDocumentDetails());
            if(trialDebitDocument.getAdjustedAmount() != null) {
                pojo.setAdjustedAmount((trialDebitDocument.getAdjustedAmount() * 100) / 100);
            }
            else{
                pojo.setAdjustedAmount(null);
            }

            pojo.setPaymentStatus((trialDebitDocument.getPaymentStatus()!=null) ? trialDebitDocument.getPaymentStatus():"Pending");

            pojo.setDebitDocDetails(trialDebitDocument.getTrialDebitDocumentDetails());

            List<CreditDocument> creditDocument = creditDocRepository.findAllByTrialDebitdocId(trialDebitDocument.getId());
            if (creditDocument!=null && !creditDocument.isEmpty() && creditDocument.get(0).getReferenceno()!=null) {
                pojo.setReferenceNo(creditDocument.get(0).getReferenceno());
            }

        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocumentPojo> convertResponseModelIntoPojo(List<TrialDebitDocument> trialDebitDocumentList) throws Exception {
        List<TrialDebitDocumentPojo> pojoListRes = new ArrayList<TrialDebitDocumentPojo>();
        if (trialDebitDocumentList != null && trialDebitDocumentList.size() > 0) {
            for (TrialDebitDocument trialDebitDocument : trialDebitDocumentList) {
                pojoListRes.add(convertTrialDebitDocumentModelToTrialDebitDocumentPojo(trialDebitDocument,false));
            }
        }
        return pojoListRes;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Trial Debit Doc");
        List<TrialDebitDocumentPojo> trialDebitDocumentPojos =  convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, TrialDebitDocumentPojo.class, trialDebitDocumentPojos, null);
    }

    @Transactional
    public void updateTrialDebitDocuments(List<TrailDebitDocumentDTOForAdjustment> debitDocs) {
        if (debitDocs.isEmpty()) return;

        Map<Integer, Double> idToAdjustedAmount = debitDocs.stream()
                .collect(Collectors.toMap(TrailDebitDocumentDTOForAdjustment::getId, TrailDebitDocumentDTOForAdjustment::getAdjustedAmount));

        entityManager.createQuery(
                        "UPDATE TrialDebitDocument d SET d.adjustedAmount = CASE d.id " +
                                idToAdjustedAmount.entrySet().stream()
                                        .map(entry -> "WHEN " + entry.getKey() + " THEN " + entry.getValue())
                                        .collect(Collectors.joining(" ")) +
                                " ELSE d.adjustedAmount END WHERE d.id IN (:ids)")
                .setParameter("ids", idToAdjustedAmount.keySet())
                .executeUpdate();
    }

//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<TrialDebitDocumentPojo> trialDebitDocumentPojos =  convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, TrialDebitDocumentPojo.class, trialDebitDocumentPojos, null);
//    }
}

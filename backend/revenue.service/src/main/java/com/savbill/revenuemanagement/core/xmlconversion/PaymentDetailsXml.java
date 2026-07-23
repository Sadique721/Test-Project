package com.savbill.revenuemanagement.core.xmlconversion;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.dto.invoice.xml.PlanInformation;
import com.savbill.revenuemanagement.core.entity.customers.CustomerAddress;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentTAXRel;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.customeraddress.CustomerAddressService;
import com.savbill.revenuemanagement.core.util.CurrencyUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PaymentDetailsXml {

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private DebitDocRepository debitDocRepository;

    public String getPaymentDetails(CreditDocument doc, String addressType, CustomerAddress address, DebitDocument docDebit) {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            CustomerAddressService custAddrService = SpringContext.getBean(CustomerAddressService.class);
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            String version="NEW";
            if (null == address)
                address = custAddrService.findByAddressTypeAndCustomer(addressType, doc.getCustomer(),version);

            //System.out.println("Doc is "+doc+":address:"+address.);

            if (null != doc && null != address) {
                String fullName = "-";
                if (null != doc.getCustomer()) {
                    if (null != doc.getCustomer().getTitle() && !doc.getCustomer().getTitle().isEmpty() && doc.getCustomer().getTitle()
                            .trim().length() > 0) {
                        fullName = doc.getCustomer().getTitle();
                    }
                    if (null != doc.getCustomer().getFirstname() && !doc.getCustomer().getFirstname().isEmpty()
                            && doc.getCustomer().getFirstname().trim().length() > 0) {
                        fullName += " " + doc.getCustomer().getFirstname();
                    }
                    if (null != doc.getCustomer().getLastname() && !doc.getCustomer().getLastname().isEmpty()
                            && doc.getCustomer().getLastname().trim().length() > 0) {
                        fullName += " " + doc.getCustomer().getLastname();
                    }
                }
                stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        " <receipt>" +
                        "     <id>" + doc.getId() + "</id>" +
                        "     <customerId>" + doc.getCustomer().getId() + "</customerId>" +
                        "     <customerName>" + fullName + "</customerName>" +
                        "     <number>" + doc.getId() + "</number>" +
                        "     <createDate>" + doc.getCreatedate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</createDate>" +
                        "     <payment>" + doc.getAmount() + "</payment>" +
                        "     <totalAmountInWords>" + CurrencyUtil.convert(Math.round(doc.getAmount())) + "</totalAmountInWords>");
                if (null != doc.getPaydetails1()) {
                    stringBuilder.append("<paymentdetails1>" + doc.getPaydetails1() + "</paymentdetails1>");
                } else {
                    stringBuilder.append("<paymentdetails1>" + "-" + "</paymentdetails1>");
                }
                if (null != doc.getPaydetails2()) {
                    stringBuilder.append("<paymentdetails2>" + doc.getPaydetails2() + "</paymentdetails2>");
                } else {
                    stringBuilder.append("<paymentdetails2>" + "-" + "</paymentdetails2>");
                }
                if (null != doc.getPaydetails3()) {
                    stringBuilder.append("<paymentdetails3>" + doc.getPaydetails3() + "</paymentdetails3>");
                } else {
                    stringBuilder.append("<paymentdetails3>" + "-" + "</paymentdetails3>");
                }
                if (null != doc.getPaydetails4()) {
                    stringBuilder.append("<paymentdetails4>" + doc.getPaydetails4() + "</paymentdetails4>");
                } else {
                    stringBuilder.append("<paymentdetails4>" + "-" + "</paymentdetails4>");
                }
                if (null != doc.getPaymentdate()) {
                    stringBuilder.append("<paymentDate>" + doc.getPaymentdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</paymentDate>");
                } else {
                    stringBuilder.append("<paymentDate>" + "-" + "</paymentDate>");
                }
                if(docDebit!=null) {
                    stringBuilder.append("<invoiceid>" + docDebit.getDocnumber() + "</invoiceid>");
                    stringBuilder.append("<invoicevalue>" + docDebit.getTotalamount() + "</invoicevalue>");
                    stringBuilder.append("<invoicedue>" + docDebit.getDuedate() + "</invoicedue>");
                    stringBuilder.append("<invoicedate>" + docDebit.getBilldate() + "</invoicedate>");
                }
                else {
                    stringBuilder.append("<invoiceid>-</invoiceid>");
                    stringBuilder.append("<invoicevalue>-</invoicevalue>");
                    stringBuilder.append("<invoicedue>-</invoicedue>");
                    stringBuilder.append("<invoicedate>-</invoicedate>");
                }
                if (null != doc.getCreatedById()) {
                    StaffUser staffUser =null; //staffUserRepository.findById(doc.getCreatedById()).orElse(null);
                    if (null != staffUser)
                        stringBuilder.append("<createBy>" + staffUser.getFullName() + "</createBy>");
                    else
                        stringBuilder.append("<createBy>" + "-" + "</createBy>");
                } else {
                    stringBuilder.append("<createBy>" + "-" + "</createBy>");
                }
                if (null != doc.getPaymode()) {
                    stringBuilder.append("<payMode>" + doc.getPaymode() + "</payMode>");
                } else {
                    stringBuilder.append("<payMode>" + "-" + "</payMode>");
                }
                stringBuilder.append("<referenceno>" + doc.getReferenceno() + "</referenceno>" +
                        "<customerInformation>" +
                        "         <accountnumber xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <accounttype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <authorizationpolicyname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <balance xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <birthdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <brand xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <country xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <createdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <cui xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <customertype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <email>" + doc.getCustomer().getEmail() + "</email>" +
                        "         <encryptiontype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <expirydate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <failureattempt xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <firstlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <firstname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <gatewayaddress xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <gender xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <hotspotname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <imei xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <imsi xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastlogouttime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastmodifieddate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <laststatuschangedate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <location xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <msisdn xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <outstandingbalance>" + doc.getCustomer().getOutstanding() + "</outstandingbalance>" +
                        "         <phone>" + doc.getCustomer().getPhone() + "</phone>" +
                        "         <qos xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <status xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <subscriberpackage xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "     </customerInformation>" +
                        "     <addressDetail>");
                if (null != address.getAddress1()) {
                    stringBuilder.append("<address1>" + address.getAddress1() + "</address1>");
                } else {
                    stringBuilder.append("<address1>" + "-" + "</address1>");
                }
                if (null != address.getAddress2()) {
                    stringBuilder.append("<address2>" + address.getAddress2() + "</address2>");
                } else {
                    stringBuilder.append("<address2>" + "-" + "</address2>");
                }
                if (null != address.getArea()) {
                    stringBuilder.append("<area>" + address.getArea().getName() + "</area>");
                } else {
                    stringBuilder.append("<area>" + "-" + "</area>");
                }
                if (null != address.getLandmark()) {
                    stringBuilder.append("<landmark>" + address.getLandmark() + "</landmark>");
                } else {
                    stringBuilder.append("<landmark>" + "-" + "</landmark>");
                }
                if (null != address.getAddressType()) {
                    stringBuilder.append("<addresstype>" + address.getAddressType() + "</addresstype>");
                } else {
                    stringBuilder.append("<addresstype>" + "-" + "</addresstype>");
                }
                if (doc.getCreditdocumentno() != null) {
                    stringBuilder.append("<creditdocumentno>").append(doc.getCreditdocumentno()).append("</creditdocumentno>");
                }
                if (doc.getReciptNo() != null) {
                    stringBuilder.append("<reciptNo>").append(doc.getReciptNo()).append("</reciptNo>");
                }
                if (doc.getPaymode() != null) {
                    stringBuilder.append("<paymode>").append(doc.getPaymode()).append("</paymode>");
                }
                if (docDebit != null && docDebit.getDocnumber() != null) {
                    stringBuilder.append("<invoiceNumber>").append(docDebit.getDocnumber()).append("</invoiceNumber>");
                }
                if (docDebit != null && docDebit.getTotalamount() != null) {
                    stringBuilder.append("<invoiceAmount>").append(docDebit.getTotalamount()).append("</invoiceAmount>");
                }
                if (doc.getCustomer() != null && doc.getCustomer().getMobile() != null) {
                    stringBuilder.append("<phoneNumber>").append(doc.getCustomer().getMobile()).append("</phoneNumber>");
                }
                if (doc.getCustomer() != null && doc.getCustomer().getAcctno() != null) {
                    stringBuilder.append("<accountNumber>").append(doc.getCustomer().getAcctno()).append("</accountNumber>");
                }
                stringBuilder.append("<city>" + (null != address.getCity() ? address.getCity().getName() : "-") + "</city>" +
                        "         <pincode>" + (null != address.getPincode() ? address.getPincode().getPincode() : "-") + "</pincode>" +
                        "         <state>" + (null != address.getState() ? address.getState().getName() : "-") + "</state>" +
                        "         <country>" + (null != address.getCountry() ? address.getCountry().getName() : "-") + "</country>" +
                        "         <subscriberid>" + doc.getCustomer().getId() + "</subscriberid>" +
                        "     </addressDetail>" +
                  /*  "     <planInformation>" +
                    "         <description>{PLAN_DESC}</description>" +
                    "         <displayname>{PLAN_DISP_NAME}</displayname>" +
                    "         <name>{PLAN_NAME}</name>" +
                    "         <postpaidplanid>{PLAN_ID}</postpaidplanid>" +
                    "     </planInformation>" +*/
                        "     <email>" + doc.getCustomer().getEmail() + "</email>" +
                        "     <phone>" + doc.getCustomer().getPhone() + "</phone>" );
                ArrayList<PlanInformation> planInformations = getPlanInformationFromDebitDoc(docDebit);

                    for (PlanInformation plan : planInformations) {
                        stringBuilder.append("<planInformation>");
                        stringBuilder.append("<createdate>").append(formatDateForXml(plan.getCreatedate())).append("</createdate>");
                        stringBuilder.append("<description>").append(plan.getDescription()).append("</description>");
                        stringBuilder.append("<displayname>").append(plan.getDisplayname()).append("</displayname>");
                        stringBuilder.append("<enddate>").append(formatDateForXml(plan.getEnddate())).append("</enddate>");
                        stringBuilder.append("<name>").append(plan.getName()).append("</name>");
                        stringBuilder.append("<planGroupName>").append(plan.getPlanGroupName()).append("</planGroupName>");
                        stringBuilder.append("<postpaidplanid>").append(plan.getPostpaidplanid()).append("</postpaidplanid>");
                        stringBuilder.append("<status>").append(plan.getStatus()).append("</status>");
                        stringBuilder.append("</planInformation>");
                    }

                ArrayList<TaxDto> taxList = getTaxListFromDebitDoc(docDebit);
                    stringBuilder.append("<taxInformation>");
                    for (TaxDto tax : taxList) {
                        stringBuilder.append("<taxList>");
                        stringBuilder.append("<absoluteAmount>").append(tax.getAbsoluteAmount()).append("</absoluteAmount>");
                        stringBuilder.append("<beforetax>").append(tax.isBeforetax()).append("</beforetax>");
                        stringBuilder.append("<chargeid>").append(tax.getChargeid()).append("</chargeid>");
                        stringBuilder.append("<description>").append(tax.getDescription()).append("</description>");
                        stringBuilder.append("<endDate>").append(formatDateForXml(tax.getEndDate())).append("</endDate>");
                        stringBuilder.append("<invoiceId>").append(tax.getInvoiceId()).append("</invoiceId>");
                        stringBuilder.append("<level>").append(tax.getLevel()).append("</level>");
                        stringBuilder.append("<name>").append(tax.getName()).append("</name>");
                        stringBuilder.append("<percentage>").append(tax.getPercentage()).append("</percentage>");
                        stringBuilder.append("<rangefrom>").append(tax.getRangefrom()).append("</rangefrom>");
                        stringBuilder.append("<rangeupto>").append(tax.getRangeupto()).append("</rangeupto>");
                        stringBuilder.append("<startDate>").append(formatDateForXml(tax.getStartDate())).append("</startDate>");
                        stringBuilder.append("<taxAmount>").append(tax.getTaxAmount()).append("</taxAmount>");
                        stringBuilder.append("</taxList>");
                    }
                    stringBuilder.append("</taxInformation>");
                stringBuilder.append("<PAN>").append(doc.getCustomer().getTinNo()).append("</PAN>");

                stringBuilder.append(" </receipt>");
            }
            return stringBuilder.toString();
        }catch (Exception ex) {
            //ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, ex.getStackTrace());
            ApplicationLogger.logger.error("Kafka receive Error receivePrepaidCustomerInvoiceChargesDetail()" + ex.getMessage(), APIConstants.FAIL,ex);
        }
        return null;
    }

    public ArrayList<PlanInformation> getPlanInformationFromDebitDoc(DebitDocument debitDocument) {
        ArrayList<PlanInformation> planInformations = new ArrayList<>();
        if (debitDocument == null || debitDocument.getId() == null) {
            System.out.println("DebitDocument is NULL, returning empty plan info");
            return planInformations;
        }
        List<DebitDocDetails> docDetailsList = debitDocRepository.debitDocDetailsByDebitDocId(debitDocument.getId());
        if (docDetailsList == null || docDetailsList.isEmpty()) {
            System.out.println("No DebitDocDetails found for debitDocId : " + debitDocument.getId());
            return planInformations;
        }
        Set<String> planIds = docDetailsList.stream().filter(docDetails -> docDetails.getPlanId() != null).map(DebitDocDetails::getPlanId).collect(Collectors.toSet());
        if (planIds.isEmpty()) {
            System.out.println("No planId available in DebitDocDetails for debitDocId: " + debitDocument.getId());
            return planInformations;
        }

        for (String planId : planIds) {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(Integer.valueOf(planId));
            PlanInformation planInformation = new PlanInformation();
            planInformation.setCreatedate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
            planInformation.setEnddate(getDatefromLocalDateTime(debitDocument.getEndate()));
            planInformation.setDescription(postpaidPlan.get().getDesc());
            planInformation.setDisplayname(postpaidPlan.get().getDisplayName());
            planInformation.setName(postpaidPlan.get().getName());
            planInformation.setPlanGroupName(postpaidPlan.get().getPlanGroup());
            planInformation.setStatus(postpaidPlan.get().getStatus());
            planInformations.add(planInformation);
        }
        return planInformations;
    }

    //28/11/2025: It is old now. added new code below.
   /* public ArrayList<TaxDto> getTaxListFromDebitDoc(DebitDocument debitDocument) {
        List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelRepository.findAllByDebitdocumentid(debitDocument.getId());//debitDocument.getDebitDocumentTAXRels();
        Set<String> taxnames = debitDocumentTAXRels.stream().map(DebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
        ArrayList<TaxDto> taxes = new ArrayList<>();
        for (String taxname : taxnames) {
            DebitDocumentTAXRel documentTAXRel = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
            Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(DebitDocumentTAXRel::getAmount).sum();
            TaxDto taxDto = new TaxDto();
            taxDto.setInvoiceId(String.valueOf(debitDocument.getId()));
            taxDto.setName(taxname);
            taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setEndDate(getDatefromLocalDateTime(documentTAXRel.getEnddate()));
            taxDto.setLevel(documentTAXRel.getTaxlevel().intValue());
            taxDto.setTaxAmount(documentTAXRel.getAmount());
            taxDto.setStartDate(getDatefromLocalDateTime(documentTAXRel.getStartdate()));
            taxDto.setPercentage(documentTAXRel.getPercentage());
            taxDto.setDescription(documentTAXRel.getDescription());
            taxes.add(taxDto);
        }
        return taxes;
    }*/

    public ArrayList<TaxDto> getTaxListFromDebitDoc(DebitDocument debitDocument) {
        ArrayList<TaxDto> taxes = new ArrayList<>();
        if (debitDocument == null || debitDocument.getId() == null) {
            return taxes;
        }
        List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelRepository.findAllByDebitdocumentid(debitDocument.getId());//debitDocument.getDebitDocumentTAXRels();
        if (debitDocumentTAXRels == null || debitDocumentTAXRels.isEmpty()) {
            return taxes;
        }
        Set<String> taxnames = debitDocumentTAXRels.stream().map(DebitDocumentTAXRel::getTaxname).filter(Objects::nonNull).collect(Collectors.toSet());

        for (String taxname : taxnames) {
            Optional<DebitDocumentTAXRel> taxRelOpt = debitDocumentTAXRels.stream().filter(t -> taxname.equalsIgnoreCase(t.getTaxname())).findFirst();
            if (!taxRelOpt.isPresent()) {
                continue;
            }
            DebitDocumentTAXRel documentTAXRel = taxRelOpt.get();
            //Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(DebitDocumentTAXRel::getAmount).sum();
            TaxDto taxDto = new TaxDto();
            taxDto.setInvoiceId(String.valueOf(debitDocument.getId()));
            taxDto.setName(taxname);
            //taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setChargeid(documentTAXRel.getChargeid() != null ? String.valueOf(documentTAXRel.getChargeid()) : null);
            //taxDto.setEndDate(getDatefromLocalDateTime(documentTAXRel.getEnddate()));
            taxDto.setEndDate(documentTAXRel.getEnddate() != null ? getDatefromLocalDateTime(documentTAXRel.getEnddate()) : null);
            //taxDto.setLevel(documentTAXRel.getTaxlevel().intValue());
            taxDto.setLevel(documentTAXRel.getTaxlevel() != null ? documentTAXRel.getTaxlevel().intValue() : 0);
            taxDto.setTaxAmount(documentTAXRel.getAmount());
            //taxDto.setStartDate(getDatefromLocalDateTime(documentTAXRel.getStartdate()));
            taxDto.setStartDate(documentTAXRel.getStartdate() != null ? getDatefromLocalDateTime(documentTAXRel.getStartdate()) : null);
            taxDto.setPercentage(documentTAXRel.getPercentage());
            taxDto.setDescription(documentTAXRel.getDescription());
            taxes.add(taxDto);
        }
        return taxes;
    }

    public Date getDatefromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return new Date();
    }
    public String formatDateForXml(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

}

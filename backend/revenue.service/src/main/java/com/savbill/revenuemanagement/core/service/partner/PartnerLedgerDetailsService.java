package com.savbill.revenuemanagement.core.service.partner;


import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.dto.invoice.ServiceLevelCommission;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.exceptions.DataNotFoundException;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.partner.PartnerLedgerDetailMapper;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerLedgerDetailsRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.core.service.prepaid.PartnerCommissionService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMapping;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PartnerLedgerDetailsService extends ExBaseAbstractService<PartnerLedgerDetailsDTO, PartnerLedgerDetails, Long> {
    public PartnerLedgerDetailsService(PartnerLedgerDetailsRepository repository, PartnerLedgerDetailMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private PartnerLedgerDetailMapper partnerLedgerDetailMapper;
    @Autowired
    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PlanGroupRepository planGroupRepository;

    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    private TaxRepository taxRepository;


    public PartnerLedgerDetails setCreditBalance(Integer custId, Double offerprice, Double agr, Double tds, Integer partnerId, Double commission, String category, String desc, Double tax) throws Exception {
        try {
            PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
            PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
            partnerLedgerDetailsDTO.setCustid(custId);
            partnerLedgerDetailsDTO.setOfferprice(offerprice);
            partnerLedgerDetailsDTO.setTax(tax);
            partnerLedgerDetailsDTO.setAgr_amount(agr);
            partnerLedgerDetailsDTO.setTds_amount(tds);
            partnerLedgerDetailsDTO.setCommission(commission);
            partnerLedgerDetailsDTO.setAmount(0.00);
            partnerLedgerDetailsDTO.setPartnerId(partnerId);
            partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            if (category == null)
                partnerLedgerDetailsDTO.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_BALANCE);
            else
                partnerLedgerDetailsDTO.setTranscategory(category);
            if (desc == null)
                partnerLedgerDetailsDTO.setDescription("Partner Create");
            else
                partnerLedgerDetailsDTO.setDescription(desc);
            partnerLedgerDetailsDTO.setCreateDate(LocalDateTime.now());
            partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
            //return partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            return null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" SetCreditBalance " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void addBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        try {
            Integer id = dto.getPartner_id();
            if (partnerRepository.getOne(id) != null) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
                partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.####").format(dto.getAmount())));
                partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                partnerLedgerDetailsDTO.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_BALANCE);
                partnerLedgerDetailsDTO.setDescription(dto.getDescription());
                partnerLedgerDetailsDTO.setPartnerId(dto.getPartner_id());
                if (dto.getPaymentdate() != null) {
                    partnerLedgerDetailsDTO.setCreateDate(dto.getPaymentdate().atStartOfDay());
                }
                partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
                partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            } else {
                throw new DataNotFoundException("Partner Not Found");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" addBalance() " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void reverseBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        //reverseBalance(dto, null, null);
    }

    public void reverseBalance(Integer custId,Double offerPrice,Double balanceAmount,Integer partnerId, String category, String desc) throws Exception {
        try {
            if (partnerRepository.getOne(partnerId) != null) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                PartnerLedgerDetailsDTO partnerLedgerDetailsDTO = new PartnerLedgerDetailsDTO();
                partnerLedgerDetailsDTO.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
                if(balanceAmount>0)
                    partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                else
                    partnerLedgerDetailsDTO.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                if(balanceAmount<0)
                    balanceAmount=-(balanceAmount);
                if(offerPrice>0)
                    partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(offerPrice)));
                else
                    partnerLedgerDetailsDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(balanceAmount)));
                partnerLedgerDetailsDTO.setTranscategory(category);
                partnerLedgerDetailsDTO.setDescription(desc);
                partnerLedgerDetailsDTO.setPartnerId(partnerId);
                partnerLedgerDetailsDTO.setCreateDate(LocalDateTime.now());
                partnerLedgerDetailsDTO.setTds_amount(0.0);
                partnerLedgerDetailsDTO.setAgr_amount(0.0);
                partnerLedgerDetailsDTO.setTax(0.0);
                partnerLedgerDetailsDTO.setCustid(custId);
                partnerLedgerDetailsDTO.setOfferprice(offerPrice);
                partnerLedgerDetails = partnerLedgerDetailMapper.dtoToDomain(partnerLedgerDetailsDTO, new CycleAvoidingMappingContext());
                partnerLedgerDetailsRepository.save(partnerLedgerDetails);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" reverseBalance() " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PartnerLedgerDetailsDTO> convertResponseModelIntoPojo(List<PartnerLedgerDetails> partnerLedgerDetails) {
        return partnerLedgerDetails.stream().map(data -> partnerLedgerDetailMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public PartnerLedgerInfoPojo getByTime(PartnerLedgerGetDTO pojo) throws Exception
    {
        PartnerLedgerInfoPojo infoPojo = new PartnerLedgerInfoPojo();
        Double OpeningAmount = 0.0;
        if (pojo.getSTART_DATE() != null) {
            OpeningAmount = partnerLedgerDetailsRepository.findOpeningAmount(pojo.getPartner_id(), pojo.getSTART_DATE());
        }
        if (OpeningAmount != null) {
            infoPojo.setOpeningAmount(Double.parseDouble(new DecimalFormat("##.####").format(OpeningAmount)));
        }else{
            infoPojo.setOpeningAmount(0.0);
            OpeningAmount = 0.0;
        }
        Double bal = 0.0;
        List<PartnerLedgerDetails> partnerLedgerDetailsList = new ArrayList<>();
        if (pojo.getSTART_DATE() != null && pojo.getEND_DATE() != null)
            partnerLedgerDetailsList = partnerLedgerDetailsRepository.findAllByStartDateAndEndDateAndPartnerId(pojo.getSTART_DATE(), pojo.getEND_DATE(), pojo.getPartner_id());

        if (pojo.getSTART_DATE() == null && pojo.getEND_DATE() == null)
            partnerLedgerDetailsList = partnerLedgerDetailsRepository.findAllByPartner_IdOrderByCreateDateAsc(pojo.getPartner_id());

        if(partnerLedgerDetailsList!=null && !partnerLedgerDetailsList.isEmpty())
        {
            partnerLedgerDetailsList.stream().forEach(x->{
                if(x.getTranscategory()!=null && x.getTranscategory().equalsIgnoreCase("CommissionTrasnfer"))
                    x.setTranscategory("TransferCommission");
                if(x.getTranscategory()!=null && x.getTranscategory().equalsIgnoreCase("BalanceTranfer"))
                    x.setTranscategory("TransferBalance");
            });
        }

        if (partnerLedgerDetailsList != null && 0 < partnerLedgerDetailsList.size())
        {
            String plan = "";
            for (int i = 0; i < partnerLedgerDetailsList.size(); i++)
            {
                List<String> creditNoteList = new ArrayList<>();
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Withdraw")){
                    bal=bal-partnerLedgerDetailsList.get(i).getAmount();
                }else
                {
                    if (partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT)) {
                        if (partnerLedgerDetailsList.get(i).getAmount() > 0.0d)
                            bal += OpeningAmount + partnerLedgerDetailsList.get(i).getAmount();
                        if (partnerLedgerDetailsList.get(i).getCommission() != null)
                            if (partnerLedgerDetailsList.get(i).getCommission() > 0.0d)
                                bal += OpeningAmount + partnerLedgerDetailsList.get(i).getCommission();
                    }
                    if (partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
                        if (partnerLedgerDetailsList.get(i).getAmount() > 0.0d)
                            bal += OpeningAmount - partnerLedgerDetailsList.get(i).getAmount();
                        if (partnerLedgerDetailsList.get(i).getCommission() != null)
                            if (partnerLedgerDetailsList.get(i).getCommission() > 0.0d)

                                bal += OpeningAmount - partnerLedgerDetailsList.get(i).getCommission();
                    }
                }
                partnerLedgerDetailsList.get(i).setBalAmount(Double.parseDouble(new DecimalFormat("##.####").format(bal)));

                if(partnerLedgerDetailsList.get(i).getPlanid() != null){
                    String[] parts = partnerLedgerDetailsList.get(i).getDescription().split(" PlanID");
                    String before = parts[0];
                    //String after = parts[1];
                    String planName="";
                    if(partnerLedgerDetailsList.get(i).getPlanid()!=null && !partnerLedgerDetailsList.get(i).getPlanid().equalsIgnoreCase(""))
                        planName = postpaidPlanRepo.findNameById(Integer.parseInt(partnerLedgerDetailsList.get(i).getPlanid()));
                    String desc = before + " PlanName = " + planName;
                    partnerLedgerDetailsList.get(i).setDescription(desc);
                    partnerLedgerDetailsList.get(i).setPlanname(planName);
                } else if (partnerLedgerDetailsList.get(i).getDebitDocId() != null) {
                    DebitDocument debitDec = debitDocRepository.findById(partnerLedgerDetailsList.get(i).getDebitDocId().intValue()).orElse(null);
                    if(debitDec != null && debitDec.getPostpaidPlan() != null){
                       plan = postpaidPlanRepo.findNameById(debitDec.getPostpaidPlan().getId());
                    }
                }

                if (partnerLedgerDetailsList.get(i).getDebitDocId() != null) {
                    DebitDocument debitDoc = debitDocRepository.findById(partnerLedgerDetailsList.get(i).getDebitDocId().intValue()).orElse(null);
                    if (debitDoc != null) {
                        partnerLedgerDetailsList.get(i).setInvoiceNo(debitDoc.getDocnumber());
                    }
                    List<CreditDebitDocMapping> creditDebitMapping = creditDebtMappingRepository.findBydebtDocId(partnerLedgerDetailsList.get(i).getDebitDocId().intValue());

                    List<Integer> creditDocIdList = new ArrayList<>();
                    if (creditDebitMapping != null && creditDebitMapping.size() > 0) {
                        creditDocIdList = creditDebitMapping.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
                    }

                    if (creditDocIdList != null && creditDocIdList.size() > 0) {
                        creditNoteList = creditDocRepository.findAllByIdInAndTypeCreditNote(creditDocIdList);
                    }
                }

                if(partnerLedgerDetailsList.get(i).getCustid()!=null) {
                    partnerLedgerDetailsList.get(i).setCustomer_name(customersRepository.findCustomerName(partnerLedgerDetailsList.get(i).getCustid()));
                    partnerLedgerDetailsList.get(i).setCustomer_username(customersRepository.findUsernameById(partnerLedgerDetailsList.get(i).getCustid()));
                }
                if(partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_CREDIT) &&
                        partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.WALLET_BALANCE_TOPUP);
                }
                if(partnerLedgerDetailsList.get(i).getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT) &&
                        partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_CUST_CREATE)){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE1);
                    if(partnerLedgerDetailsList.get(i).getPlanid()!=null && !partnerLedgerDetailsList.get(i).getPlanid().equalsIgnoreCase(""))
                    {
                        String planName = postpaidPlanRepo.findNameById(Integer.parseInt(partnerLedgerDetailsList.get(i).getPlanid()));
                        partnerLedgerDetailsList.get(i).setPlanname(planName);

                    }
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getOfferprice());
                }
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Revert Balance")){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.TRANS_CREDIT_NOTE1);
                    partnerLedgerDetailsList.get(i).setCreditDocNo(creditNoteList);
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getOfferprice());
                }
                if(partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Revert Commission")){
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.CREDIT_NOTE_COMMISSION);
                    partnerLedgerDetailsList.get(i).setCreditDocNo(creditNoteList);
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getOfferprice());

                }
                if (partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase("Withdraw")) {
                    partnerLedgerDetailsList.get(i).setTranscategory(CommonConstants.WALLET_BALANCE_PAYOUT);
                }
                if (partnerLedgerDetailsList.get(i).getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)) {
                    partnerLedgerDetailsList.get(i).setOfferprice(partnerLedgerDetailsList.get(i).getOfferprice());
                }
            }
        }
        infoPojo.setDebitCreditDetail(convertResponseModelIntoPojo(partnerLedgerDetailsList));
        infoPojo.setClosingBalance(Double.parseDouble(new DecimalFormat("##.####").format(bal)));
        return infoPojo;
    }

    public PartnerLedgerAllInfoPojo partInfoByTime(Integer partnerId, PartnerLedgerInfoPojo pojo) {
        PartnerLedgerAllInfoPojo partpojo = new PartnerLedgerAllInfoPojo();
        Partner partner = partnerRepository.getOne(partnerId);
        partpojo.setPartnerId(partner.getId());
        partpojo.setPartnername(partner.getName());
        partpojo.setCommissionType(partner.getPriceBookId().getCommission_on());
        partpojo.setAddress(partner.getAddress1());
        partpojo.setStatus(partner.getStatus());
        partpojo.setPartnerLedgerInfoPojo(pojo);
        return partpojo;
    }

    public void setLedgerDetailsForCustomerCreation(Integer custId, Double offerPrice, Double balanceAmount, Customers customers, Integer partner) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [setLedgerDetailsForCustomerCreation()] ";
        try {
            Partner franchise = partnerRepository.findById(partner).orElse(null);
            if (null != franchise)
            {
                reverseBalance(custId,offerPrice,balanceAmount, partner, CommonConstants.TRANS_CATEGORY_CUST_CREATE, "Debit Against Customer Creation = " + customers.getFirstname());
                franchise.setBalance(franchise.getBalance()-offerPrice);
                //partnerRepository.save(franchise);
            }
            else
                throw new DataNotFoundException("Partner Not Found");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerLedgerDetailsService]";
    }


    public List<PartnerLedgerDetailsPlanLevelDTO> convertIntoPlanLevelDTO(PartnerLedgerInfoPojo infoPojo) {
        List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS=new ArrayList<>();

        if(infoPojo.getDebitCreditDetail()!=null && !infoPojo.getDebitCreditDetail().isEmpty())
        {
            List<PartnerLedgerDetailsDTO> walletRechargeList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Wallet Balance Topup")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> invoiceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Invoice")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> planCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Commision") && record.getPlanGroupId()==null).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> planGroupCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Commision") && record.getPlanGroupId()!=null).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> revertCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Credit Note Commission")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> revertBalanceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Credit Note")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> withdrawCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Wallet Balance Payout")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> transferCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && (record.getTranscategory().equalsIgnoreCase("TransferCommission") || record.getTranscategory().equalsIgnoreCase("CommissionTrasnfer"))).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> transferBalanceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && (record.getTranscategory().equalsIgnoreCase("TransferBalance") || record.getTranscategory().equalsIgnoreCase("BalanceTranfer"))).collect(Collectors.toList());


            List<Integer> planIds=planCommissionList.stream().filter(x -> x.getPlanid() != null).map(x->Integer.parseInt(x.getPlanid())).distinct().collect(Collectors.toList());
            List<Integer> planGroupIds=planGroupCommissionList.stream().map(x->x.getPlanGroupId()).distinct().collect(Collectors.toList());
            addWalletRechargeList(planLevelDTOS,walletRechargeList);
            addInvoiceList(planLevelDTOS,invoiceList);
            addWithdrawList(planLevelDTOS,withdrawCommissionList);
            planIds.forEach(planId->{addPlanCommissionList(planLevelDTOS,planCommissionList.stream().filter(record->record.getPlanid()!=null && Integer.parseInt(record.getPlanid())==planId.intValue()).collect(Collectors.toList()));});
            planGroupIds.forEach(planGroupId->{addPlanGroupCommissionList(planLevelDTOS,planGroupCommissionList.stream().filter(record->record.getPlanGroupId()!=null && record.getPlanGroupId().intValue()==planGroupId.intValue()).collect(Collectors.toList()));});
            addRevertBalanceList(planLevelDTOS,revertBalanceList);
            addRevertCommissionList(planLevelDTOS,revertCommissionList);
            addTransferCommissionList(planLevelDTOS,transferCommissionList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("CR")).collect(Collectors.toList()));
            addTransferCommissionList(planLevelDTOS,transferCommissionList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("DR")).collect(Collectors.toList()));
            addTransferBalanceList(planLevelDTOS,transferBalanceList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("CR")).collect(Collectors.toList()));
            addTransferBalanceList(planLevelDTOS,transferBalanceList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("DR")).collect(Collectors.toList()));
        }
        return planLevelDTOS;
    }

    private void addRevertBalanceList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> revertBalanceList) {

        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(revertBalanceList!=null && !revertBalanceList.isEmpty())
        {
            dto.setSerialNo(planLevelDTOS.size()+1);
            dto.setTranscategory(revertBalanceList.get(0).getTranscategory());
            dto.setTransType(revertBalanceList.get(0).getTranstype());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(revertBalanceList.stream().mapToDouble(x->x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(revertBalanceList.stream().mapToDouble(x->x.getAmount()).sum())));
            dto.setCustomerCount(revertBalanceList.stream().filter(x->x.getCustid()!=null).map(x->x.getCustid()).distinct().count());
            dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() + revertBalanceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            planLevelDTOS.add(dto);
        }
    }


    private void addWalletRechargeList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> walletRechargeList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(walletRechargeList!=null && !walletRechargeList.isEmpty()) {
            dto.setSerialNo(planLevelDTOS.size() + 1);
            dto.setTranscategory(walletRechargeList.get(0).getTranscategory());
            dto.setTransType(walletRechargeList.get(0).getTranstype());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setCustomerCount(0L);
            planLevelDTOS.add(dto);
        }
    }

    private void addInvoiceList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> invoiceList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(invoiceList!=null && !invoiceList.isEmpty())
        {
            dto.setSerialNo(planLevelDTOS.size()+1);
            dto.setTranscategory(invoiceList.get(0).getTranscategory());
            dto.setTransType(invoiceList.get(0).getTranstype());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setCustomerCount(invoiceList.stream().filter(x->x.getCustid()!=null).mapToInt(x->x.getCustid()).distinct().count());
            if(planLevelDTOS!=null && !planLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() - invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            planLevelDTOS.add(dto);
        }
    }

    private void addWithdrawList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> withdrawCommissionList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(withdrawCommissionList!=null && !withdrawCommissionList.isEmpty()) {
            dto.setSerialNo(planLevelDTOS.size() + 1);
            dto.setTranscategory(withdrawCommissionList.get(0).getTranscategory());
            dto.setTransType(withdrawCommissionList.get(0).getTranstype());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            if(planLevelDTOS!=null && !planLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() - withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));

            dto.setCustomerCount(0L);
            planLevelDTOS.add(dto);
        }
    }
    private void addPlanCommissionList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> planCommissionList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(planCommissionList!=null && !planCommissionList.isEmpty())
        {
            PlanCommissionDetailList detailList=getDetail(new PlanCommissionPojo(Integer.parseInt(planCommissionList.get(0).getPlanid()),false,planCommissionList.get(0).getAgrPercentage(),planCommissionList.get(0).getRevenueSharePercentage(),planCommissionList.get(0).getPartnerTaxId().intValue(),planCommissionList.get(0).getTdsPercentage()));
            PlanCommissionDetail detail=detailList.getPlanCommissionDetailList().get(0);

            dto.setSerialNo(planLevelDTOS.size()+1);
            dto.setPlanOrPlanGroupName(planCommissionList.get(0).getPlanname());
            dto.setPlanOrPlanGroupId(Integer.parseInt(planCommissionList.get(0).getPlanid()));
            dto.setIsPlanGroup(false);

            dto.setPlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(detail.getOfferPrice())));
            dto.setBasePlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(detail.getBaseOfferPriceExcludeAgr())));
            dto.setCustomerCount(planCommissionList.stream().map(x->x.getCustid()).distinct().count());
            dto.setTotalSale(Double.parseDouble(new DecimalFormat("##.##").format(dto.getCustomerCount()*dto.getPlanOrPlanGroupPrice())));
            dto.setNetCommission(Double.parseDouble(new DecimalFormat("##.##").format(detail.getNetCommission())));
            dto.setTotalCommission(Double.parseDouble(new DecimalFormat("##.##").format(detail.getPayableCommission())));
            dto.setTotalPlanCommission(Double.parseDouble(new DecimalFormat("##.##").format(planCommissionList.stream().mapToDouble(x->x.getCommission()).sum())));
            dto.setTranscategory(planCommissionList.get(0).getTranscategory());
            dto.setTransType(planCommissionList.get(0).getTranstype());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(planCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(planCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            if(planLevelDTOS!=null && !planLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() + planCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));

            dto.setCommissionSharePercentage(planCommissionList.get(0).getRevenueSharePercentage());
            dto.setPartnerTaxId(planCommissionList.get(0).getPartnerTaxId());
            dto.setAgrPercentage(planCommissionList.get(0).getAgrPercentage());
            dto.setTdsPercentage(planCommissionList.get(0).getTdsPercentage());
            dto.setServiceId(planCommissionList.get(0).getServiceId());
            dto.setServiceName(planCommissionList.get(0).getServiceName());
            planLevelDTOS.add(dto);
        }
    }

    private void addPlanGroupCommissionList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> planGroupCommissionList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(planGroupCommissionList!=null && !planGroupCommissionList.isEmpty())
        {
            PlanCommissionDetailList detailList=getDetail(new PlanCommissionPojo(planGroupCommissionList.get(0).getPlanGroupId(),true,planGroupCommissionList.get(0).getAgrPercentage(),planGroupCommissionList.get(0).getRevenueSharePercentage(),planGroupCommissionList.get(0).getPartnerTaxId().intValue(),planGroupCommissionList.get(0).getTdsPercentage()));
            List<PlanCommissionDetail> details=detailList.getPlanCommissionDetailList();

            dto.setSerialNo(planLevelDTOS.size()+1);
            dto.setPlanOrPlanGroupName(planGroupCommissionList.get(0).getPlanGroupName());
            dto.setPlanOrPlanGroupId(planGroupCommissionList.get(0).getPlanGroupId());
            dto.setIsPlanGroup(true);
            dto.setCustomerCount(planGroupCommissionList.stream().map(x->x.getCustid()).distinct().count());

            dto.setPlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(details.stream().mapToDouble(x->x.getOfferPrice()).sum())));
            dto.setBasePlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(details.stream().mapToDouble(x->x.getBaseOfferPriceExcludeAgr()).sum())));
            dto.setNetCommission(Double.parseDouble(new DecimalFormat("##.##").format(details.stream().mapToDouble(x->x.getNetCommission()).sum())));
            dto.setTotalCommission(Double.parseDouble(new DecimalFormat("##.##").format(details.stream().mapToDouble(x->x.getNetCommission()+x.getPartnerTaxAmount()-x.getTdsAmount()).sum())));
            dto.setTotalPlanCommission(Double.parseDouble(new DecimalFormat("##.##").format(planGroupCommissionList.stream().mapToDouble(x->x.getCommission()).sum())));
            dto.setTranscategory(planGroupCommissionList.get(0).getTranscategory());
            dto.setTransType(planGroupCommissionList.get(0).getTranstype());
            dto.setTotalSale(Double.parseDouble(new DecimalFormat("##.##").format(dto.getCustomerCount()*dto.getPlanOrPlanGroupPrice())));
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(planGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(planGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            if(planLevelDTOS!=null && !planLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() + planGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format( planGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));

            dto.setCommissionSharePercentage(planGroupCommissionList.get(0).getRevenueSharePercentage());
            dto.setPartnerTaxId(planGroupCommissionList.get(0).getPartnerTaxId());
            dto.setAgrPercentage(planGroupCommissionList.get(0).getAgrPercentage());
            dto.setTdsPercentage(planGroupCommissionList.get(0).getTdsPercentage());
            dto.setServiceId(planGroupCommissionList.get(0).getServiceId());
            dto.setServiceName(planGroupCommissionList.get(0).getServiceName());
            planLevelDTOS.add(dto);
        }
    }

    private void addRevertCommissionList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> revertCommissionList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(revertCommissionList!=null && !revertCommissionList.isEmpty())
        {
            dto.setSerialNo(planLevelDTOS.size()+1);
            dto.setTranscategory(revertCommissionList.get(0).getTranscategory());
            dto.setTransType(revertCommissionList.get(0).getTranstype());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setCustomerCount(revertCommissionList.stream().filter(x->x.getCustid()!=null).map(x->x.getCustid()).distinct().count());
            if(planLevelDTOS!=null && !planLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() - revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            planLevelDTOS.add(dto);
        }
    }

    private void addTransferBalanceList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> transferBalanceList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(transferBalanceList!=null && !transferBalanceList.isEmpty()) {
            dto.setSerialNo(planLevelDTOS.size() + 1);
            dto.setTranscategory(transferBalanceList.get(0).getTranscategory());

            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setTransType("DR");
            else
                dto.setTransType("CR");

            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() - transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() + transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));

            dto.setCustomerCount(0L);
            planLevelDTOS.add(dto);
        }
    }

    private void addTransferCommissionList(List<PartnerLedgerDetailsPlanLevelDTO> planLevelDTOS, List<PartnerLedgerDetailsDTO> transferCommissionList) {
        PartnerLedgerDetailsPlanLevelDTO dto=new PartnerLedgerDetailsPlanLevelDTO();
        if(transferCommissionList!=null && !transferCommissionList.isEmpty()) {
            dto.setSerialNo(planLevelDTOS.size() + 1);
            dto.setTranscategory(transferCommissionList.get(0).getTranscategory());

            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setTransType("DR");
            else
                dto.setTransType("CR");

            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() - transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planLevelDTOS.get(planLevelDTOS.size()-1).getBalAmount() + transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setCustomerCount(0L);
            planLevelDTOS.add(dto);
        }
    }

    public List<PartnerLedgerDetailsServiceLevelDTO> convertIntoServiceLevelDTO(PartnerLedgerInfoPojo infoPojo) {
        List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS=new ArrayList<>();
        if(infoPojo.getDebitCreditDetail()!=null && !infoPojo.getDebitCreditDetail().isEmpty())
        {
            List<PartnerLedgerDetailsDTO> walletRechargeList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Wallet Balance Topup")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> invoiceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Invoice")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> planAndPlanGroupCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Commision")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> revertCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Credit Note Commission")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> revertBalanceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Credit Note")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> withdrawCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && record.getTranscategory().equalsIgnoreCase("Wallet Balance Payout")).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> transferCommissionList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && (record.getTranscategory().equalsIgnoreCase("TransferCommission") || record.getTranscategory().equalsIgnoreCase("CommissionTrasnfer"))).collect(Collectors.toList());
            List<PartnerLedgerDetailsDTO> transferBalanceList=infoPojo.getDebitCreditDetail().stream().filter(record->record.getTranscategory()!=null && (record.getTranscategory().equalsIgnoreCase("TransferBalance") || record.getTranscategory().equalsIgnoreCase("BalanceTranfer"))).collect(Collectors.toList());

            List<Integer> serviceIds=planAndPlanGroupCommissionList.stream().filter(x->x.getServiceId()!=null).map(x->x.getServiceId()).distinct().collect(Collectors.toList());
            addWalletRechargeList1(serviceLevelDTOS,walletRechargeList);
            addInvoiceList1(serviceLevelDTOS,invoiceList);
            addWithdrawList1(serviceLevelDTOS,withdrawCommissionList);
            serviceIds.forEach(serviceId->{addServiceCommissionList(serviceLevelDTOS,planAndPlanGroupCommissionList.stream().filter(record->record.getServiceId()!=null && record.getServiceId().intValue()==serviceId.intValue()).collect(Collectors.toList()));});
            addRevertBalanceList1(serviceLevelDTOS,revertBalanceList);
            addRevertCommissionList1(serviceLevelDTOS,revertCommissionList);
            addTransferCommissionList1(serviceLevelDTOS,transferCommissionList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("CR")).collect(Collectors.toList()));
            addTransferCommissionList1(serviceLevelDTOS,transferCommissionList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("DR")).collect(Collectors.toList()));
            addTransferBalanceList1(serviceLevelDTOS,transferBalanceList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("CR")).collect(Collectors.toList()));
            addTransferBalanceList1(serviceLevelDTOS,transferBalanceList.stream().filter(x->x.getTranstype()!=null && x.getTranstype().equalsIgnoreCase("DR")).collect(Collectors.toList()));

        }
        return serviceLevelDTOS;
    }


    private void addRevertBalanceList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> revertBalanceList) {

        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(revertBalanceList!=null && !revertBalanceList.isEmpty())
        {
            dto.setSerialNo(serviceLevelDTOS.size()+1);
            dto.setTranscategory(revertBalanceList.get(0).getTranscategory());
            dto.setTransType(revertBalanceList.get(0).getTranstype());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(revertBalanceList.stream().mapToDouble(x->x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(revertBalanceList.stream().mapToDouble(x->x.getAmount()).sum())));
            dto.setCustomerCount(revertBalanceList.stream().filter(x->x.getCustid()!=null).map(x->x.getCustid()).distinct().count());
            dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() + revertBalanceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            serviceLevelDTOS.add(dto);
        }
    }


    private void addWalletRechargeList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> walletRechargeList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(walletRechargeList!=null && !walletRechargeList.isEmpty()) {
            dto.setSerialNo(serviceLevelDTOS.size() + 1);
            dto.setTranscategory(walletRechargeList.get(0).getTranscategory());
            dto.setTransType(walletRechargeList.get(0).getTranstype());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(walletRechargeList.stream().mapToDouble(x -> x.getAmount()).sum())));
            dto.setCustomerCount(0L);
            serviceLevelDTOS.add(dto);
        }
    }

    private void addInvoiceList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> invoiceList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(invoiceList!=null && !invoiceList.isEmpty())
        {
            dto.setSerialNo(serviceLevelDTOS.size()+1);
            dto.setTranscategory(invoiceList.get(0).getTranscategory());
            dto.setTransType(invoiceList.get(0).getTranstype());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setCustomerCount(invoiceList.stream().filter(x->x.getCustid()!=null).mapToInt(x->x.getCustid()).distinct().count());
            if(serviceLevelDTOS!=null && !serviceLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() - invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format( invoiceList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            serviceLevelDTOS.add(dto);
        }
    }

    private void addWithdrawList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> withdrawCommissionList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(withdrawCommissionList!=null && !withdrawCommissionList.isEmpty()) {
            dto.setSerialNo(serviceLevelDTOS.size() + 1);
            dto.setTransType(withdrawCommissionList.get(0).getTranstype());
            dto.setTranscategory(withdrawCommissionList.get(0).getTranscategory());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            if(serviceLevelDTOS!=null && !serviceLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() - withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(withdrawCommissionList.stream().mapToDouble(x -> x.getAmount() + x.getCommission()).sum())));
            dto.setCustomerCount(0L);
            serviceLevelDTOS.add(dto);
        }
    }
    private void addServiceCommissionList(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> planAndPlanGroupCommissionList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        PlanCommissionDetailList commissionDetailList=new PlanCommissionDetailList();
        if(planAndPlanGroupCommissionList!=null && !planAndPlanGroupCommissionList.isEmpty())
        {
            AtomicReference<Double> totalSale= new AtomicReference<>(0.0);
            List<Integer> planIds=planAndPlanGroupCommissionList.stream().filter(x->x.getPlanid()!=null).map(x->Integer.parseInt(x.getPlanid())).distinct().collect(Collectors.toList());

            planIds.stream().forEach(planId->{
                List<PartnerLedgerDetailsDTO> planCommissionList=planAndPlanGroupCommissionList.stream().filter(x->x.getPlanid()!=null  && x.getPlanid().equalsIgnoreCase(planId.toString())).collect(Collectors.toList());
                PlanCommissionDetailList detailList=getDetail(new PlanCommissionPojo(planId,false,planCommissionList.get(0).getAgrPercentage(),planCommissionList.get(0).getRevenueSharePercentage(),planCommissionList.get(0).getPartnerTaxId().intValue(),planCommissionList.get(0).getTdsPercentage()));
                Long count=planCommissionList.stream().filter(x->x.getCustid()!=null).mapToInt(x->x.getCustid()).distinct().count();
                detailList.getPlanCommissionDetailList().get(0).setCustomerCount(count);
                detailList.getPlanCommissionDetailList().stream().forEach(detail->{
                    if(commissionDetailList.getPlanCommissionDetailList()!=null)
                        commissionDetailList.getPlanCommissionDetailList().add(detail);
                    else
                    {
                        commissionDetailList.setPlanCommissionDetailList(new ArrayList<>());
                        commissionDetailList.getPlanCommissionDetailList().add(detail);
                    }
                    totalSale.updateAndGet(v -> v + (count * detail.getOfferPrice()));
                });
            });

            dto.setSerialNo(serviceLevelDTOS.size()+1);
            dto.setTransType(planAndPlanGroupCommissionList.get(0).getTranstype());
            dto.setPlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(commissionDetailList.getPlanCommissionDetailList().stream().mapToDouble(x-> x.getOfferPrice()).sum())));
            dto.setBasePlanOrPlanGroupPrice(Double.parseDouble(new DecimalFormat("##.##").format(commissionDetailList.getPlanCommissionDetailList().stream().mapToDouble(x->x.getCustomerCount() * x.getBaseOfferPriceExcludeAgr()).sum())));
            dto.setCustomerCount(planAndPlanGroupCommissionList.stream().filter(x->x.getCustid()!=null).map(x->x.getCustid()).distinct().count());
            dto.setTotalSale(Double.parseDouble(new DecimalFormat("##.##").format(totalSale.get())));
            dto.setNetCommission(Double.parseDouble(new DecimalFormat("##.##").format(commissionDetailList.getPlanCommissionDetailList().stream().mapToDouble(x->x.getCustomerCount() * x.getNetCommission()).sum())));
            dto.setTotalCommission(Double.parseDouble(new DecimalFormat("##.##").format(planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getCommission()-x.getPartnerTax()+x.getTds_amount()+x.getRoyalty()).sum())));
            dto.setTotalPlanCommission(Double.parseDouble(new DecimalFormat("##.##").format(planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getCommission()).sum())));
            dto.setTranscategory(planAndPlanGroupCommissionList.get(0).getTranscategory());
            dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            if(serviceLevelDTOS!=null && !serviceLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() + planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(planAndPlanGroupCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));

            dto.setCommissionSharePercentage(planAndPlanGroupCommissionList.get(0).getRevenueSharePercentage());
            dto.setPartnerTaxId(planAndPlanGroupCommissionList.get(0).getPartnerTaxId());
            dto.setAgrPercentage(planAndPlanGroupCommissionList.get(0).getAgrPercentage());
            dto.setTdsPercentage(planAndPlanGroupCommissionList.get(0).getTdsPercentage());
            dto.setServiceId(planAndPlanGroupCommissionList.get(0).getServiceId());
            dto.setServiceName(planAndPlanGroupCommissionList.get(0).getServiceName());
            dto.setCommissionDetailList(commissionDetailList);
            Map<String, List<PartnerLedgerDetailsDTO>> collect = planAndPlanGroupCommissionList.stream().collect(groupingBy(PartnerLedgerDetailsDTO::getPlanid));
            List<ServiceLevelCommission> serviceLevelCommissions=new ArrayList<>();
            Iterator<Map.Entry<String,List<PartnerLedgerDetailsDTO>>> iterator = collect.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String,List<PartnerLedgerDetailsDTO>> entry = iterator.next();
                ServiceLevelCommission commission=new ServiceLevelCommission();
                commission.setPlanName(entry.getValue().get(0).getPlanname());
                commission.setPartnerCommission(entry.getValue().stream().filter(x->x.getRoyalty()!=null).mapToDouble(x->x.getCommission()+x.getRoyalty()-x.getPartnerTax()+x.getTds_amount()).sum());
                commission.setRoyalty(entry.getValue().stream().filter(x->x.getRoyalty()!=null).mapToDouble(x->x.getRoyalty()).sum());
                commission.setNetCommission(commission.getPartnerCommission()-commission.getRoyalty());
                commission.setPartnerTax(entry.getValue().stream().mapToDouble(x->x.getPartnerTax()).sum());
                commission.setTds(entry.getValue().stream().mapToDouble(x->x.getTds_amount()).sum());
                commission.setPayableCommission(entry.getValue().stream().mapToDouble(x->x.getCommission()).sum());
                serviceLevelCommissions.add(commission);
            }
            dto.setServiceLevelCommissions(serviceLevelCommissions);
            serviceLevelDTOS.add(dto);
        }
    }

    private void addRevertCommissionList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> revertCommissionList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(revertCommissionList!=null && !revertCommissionList.isEmpty())
        {
            dto.setSerialNo(serviceLevelDTOS.size()+1);
            dto.setTransType(revertCommissionList.get(0).getTranstype());
            dto.setTranscategory(revertCommissionList.get(0).getTranscategory());
            dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            dto.setCustomerCount(revertCommissionList.stream().filter(x->x.getCustid()!=null).map(x->x.getCustid()).distinct().count());
            if(serviceLevelDTOS!=null && !serviceLevelDTOS.isEmpty())
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() - revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format( revertCommissionList.stream().mapToDouble(x->x.getAmount() + x.getCommission()).sum())));

            serviceLevelDTOS.add(dto);
        }
    }

    private void addTransferBalanceList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> transferBalanceList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(transferBalanceList!=null && !transferBalanceList.isEmpty()) {
            dto.setSerialNo(serviceLevelDTOS.size() + 1);
            dto.setTranscategory("TransferBalance");
            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setTransType("DR");
            else
                dto.setTransType("CR");

            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));

            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));

            if(transferBalanceList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() - transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() + transferBalanceList.stream().mapToDouble(x -> x.getAmount()).sum())));

            dto.setCustomerCount(0L);
            serviceLevelDTOS.add(dto);
        }
    }

    private void addTransferCommissionList1(List<PartnerLedgerDetailsServiceLevelDTO> serviceLevelDTOS, List<PartnerLedgerDetailsDTO> transferCommissionList) {
        PartnerLedgerDetailsServiceLevelDTO dto=new PartnerLedgerDetailsServiceLevelDTO();
        if(transferCommissionList!=null && !transferCommissionList.isEmpty()) {
            dto.setSerialNo(serviceLevelDTOS.size() + 1);
            dto.setTranscategory("TransferCommission");

            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setTransType("DR");
            else
                dto.setTransType("CR");

            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setDebit(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setCredit(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));

            dto.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));

            if(transferCommissionList.get(0).getTranstype().equalsIgnoreCase("DR"))
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() - transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));
            else
                dto.setBalAmount(Double.parseDouble(new DecimalFormat("##.##").format(serviceLevelDTOS.get(serviceLevelDTOS.size()-1).getBalAmount() + transferCommissionList.stream().mapToDouble(x -> x.getAmount()).sum())));

            dto.setCustomerCount(0L);
            serviceLevelDTOS.add(dto);
        }
    }

    public PlanCommissionDetailList getDetail(PlanCommissionPojo pojo) {
        PlanCommissionDetailList detailList=new PlanCommissionDetailList();
        List<PlanCommissionDetail> list=new ArrayList<>();
        if(pojo!=null && pojo.getIsPlanGroup()!=null && pojo.getIsPlanGroup() && pojo.getId()!=null)
        {
            PlanGroup planGroup=planGroupRepository.findById(pojo.getId()).orElse(null);
            if(planGroup!=null)
            {
                List<PlanGroupMapping> mappings=planGroup.getPlanMappingList();
                if(mappings!=null && !mappings.isEmpty())
                {
                    mappings.stream().forEach(mapping->{
                        PostpaidPlan postpaidPlan=mapping.getPlan();
                        if(postpaidPlan!=null)
                        {
                            PlanCommissionDetail commissionDetail=new PlanCommissionDetail();
                            commissionDetail.setPlanGroupId(planGroup.getPlanGroupId());
                            commissionDetail.setPlanGroupName(planGroup.getPlanGroupName());
                            commissionDetail.setPlanId(postpaidPlan.getId());
                            commissionDetail.setPlanName(postpaidPlan.getName());
                            List<Charge> charges=postpaidPlan.getChargeList().stream().map(charge->charge.getCharge()).collect(Collectors.toList());
                            charges.stream().forEach(charge->{
                                charge.setTaxamount(partnerCommissionService.getTaxAmount(charge.getTax(), charge.getPrice()));
                            });
                            Double grossOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->x.getTaxamount()!=null).mapToDouble(x->x.getPrice()+x.getTaxamount()).sum()));
                            Double grossTaxAmount=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->x.getTaxamount()!=null).mapToDouble(x->x.getTaxamount()).sum()));
                            Double grossBaseOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(grossOfferPrice-grossTaxAmount));
                            Double offerPrice=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->(x.getChargetype().equalsIgnoreCase("ADVANCE") || x.getChargetype().equalsIgnoreCase("RECURRING")) && x.getTaxamount()!=null).mapToDouble(x->x.getPrice() + x.getTaxamount()).sum()));
                            Double taxAmount=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->(x.getChargetype().equalsIgnoreCase("ADVANCE") || x.getChargetype().equalsIgnoreCase("RECURRING")) && x.getTaxamount()!=null).mapToDouble(x->x.getTaxamount()).sum()));
                            Double baseOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(offerPrice-taxAmount));
                            Double agrAmount=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPrice*(pojo.getAgrPercentage()/100.0d)));
                            Double baseOfferPriceExcludeAgr=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPrice-agrAmount));
                            Double netPartnerCommission=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPriceExcludeAgr*(pojo.getRevenueSharePercentage()/100.0d)));
                            Tax tax=taxRepository.getOne(pojo.getPartnerTaxId());
                            Double partnerTaxAmount=Double.parseDouble(new DecimalFormat("##.##").format(partnerCommissionService.getTaxAmount(tax,netPartnerCommission)));
                            Double tdsAmount=Double.parseDouble(new DecimalFormat("##.##").format(netPartnerCommission*(pojo.getTdsPercentage()/100.0d)));
                            Double payableCommission=Double.parseDouble(new DecimalFormat("##.##").format(netPartnerCommission+partnerTaxAmount-tdsAmount));
                            commissionDetail.setGrossOfferPrice(grossOfferPrice);
                            commissionDetail.setNetCommission(netPartnerCommission);
                            commissionDetail.setPayableCommission(payableCommission);
                            commissionDetail.setAgrAmount(agrAmount);
                            commissionDetail.setTaxAmount(taxAmount);
                            commissionDetail.setOfferPrice(offerPrice);
                            commissionDetail.setPartnerTaxAmount(partnerTaxAmount);
                            commissionDetail.setBaseOfferPriceExcludeAgr(baseOfferPriceExcludeAgr);
                            commissionDetail.setGrossTaxAmount(grossTaxAmount);
                            commissionDetail.setGrossBaseOfferPrice(grossBaseOfferPrice);
                            commissionDetail.setTdsAmount(tdsAmount);

                            list.add(commissionDetail);
                        }
                    });
                }
                detailList.setPlanCommissionDetailList(list);
            }
        }
        else if(pojo!=null && pojo.getIsPlanGroup()!=null && !pojo.getIsPlanGroup() && pojo.getId()!=null)
        {
            PostpaidPlan postpaidPlan=postpaidPlanRepo.findById(pojo.getId()).orElse(null);
            if(postpaidPlan!=null)
            {
                PlanCommissionDetail commissionDetail=new PlanCommissionDetail();
                commissionDetail.setPlanId(postpaidPlan.getId());
                commissionDetail.setPlanName(postpaidPlan.getName());
                List<Charge> charges=postpaidPlan.getChargeList().stream().map(charge->charge.getCharge()).collect(Collectors.toList());
                charges.stream().forEach(charge->{
                    charge.setTaxamount(partnerCommissionService.getTaxAmount(charge.getTax(), charge.getPrice()));
                });
                Double grossOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->x.getTaxamount()!=null).mapToDouble(x->x.getPrice()+x.getTaxamount()).sum()));
                Double grossTaxAmount=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->x.getTaxamount()!=null).mapToDouble(x->x.getTaxamount()).sum()));
                Double grossBaseOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(grossOfferPrice-grossTaxAmount));
                Double offerPrice=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->(x.getChargetype().equalsIgnoreCase("ADVANCE") || x.getChargetype().equalsIgnoreCase("RECURRING")) && x.getTaxamount()!=null).mapToDouble(x->x.getPrice() + x.getTaxamount()).sum()));
                Double taxAmount=Double.parseDouble(new DecimalFormat("##.##").format(charges.stream().filter(x->(x.getChargetype().equalsIgnoreCase("ADVANCE") || x.getChargetype().equalsIgnoreCase("RECURRING")) && x.getTaxamount()!=null).mapToDouble(x->x.getTaxamount()).sum()));
                Double baseOfferPrice=Double.parseDouble(new DecimalFormat("##.##").format(offerPrice-taxAmount));
                Double agrAmount=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPrice*(pojo.getAgrPercentage()/100.0d)));
                Double baseOfferPriceExcludeAgr=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPrice-agrAmount));
                Double netPartnerCommission=Double.parseDouble(new DecimalFormat("##.##").format(baseOfferPriceExcludeAgr*(pojo.getRevenueSharePercentage()/100.0d)));
                Tax tax=taxRepository.getOne(pojo.getPartnerTaxId());
                Double partnerTaxAmount=Double.parseDouble(new DecimalFormat("##.##").format(partnerCommissionService.getTaxAmount(tax,netPartnerCommission)));
                Double tdsAmount=Double.parseDouble(new DecimalFormat("##.##").format(netPartnerCommission*(pojo.getTdsPercentage()/100.0d)));
                Double payableCommission=Double.parseDouble(new DecimalFormat("##.##").format(netPartnerCommission+partnerTaxAmount-tdsAmount));
                commissionDetail.setGrossOfferPrice(grossOfferPrice);
                commissionDetail.setNetCommission(netPartnerCommission);
                commissionDetail.setPayableCommission(payableCommission);
                commissionDetail.setAgrAmount(agrAmount);
                commissionDetail.setTaxAmount(taxAmount);
                commissionDetail.setOfferPrice(offerPrice);
                commissionDetail.setPartnerTaxAmount(partnerTaxAmount);
                commissionDetail.setBaseOfferPriceExcludeAgr(baseOfferPriceExcludeAgr);
                commissionDetail.setGrossTaxAmount(grossTaxAmount);
                commissionDetail.setGrossBaseOfferPrice(grossBaseOfferPrice);
                commissionDetail.setTdsAmount(tdsAmount);

                list.add(commissionDetail);
                detailList.setPlanCommissionDetailList(list);
            }
        }
        return detailList;
    }
}

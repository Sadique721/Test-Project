package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.pojo.CustMilestoneDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.QuickInvoicePojo;

import java.util.List;

public interface QuickInvoiceService {

//    public void saveLeadMasterWithMilestones(QuickInvoicePojo quickInvoicePojo);

    public List<CustMilestoneDetailsPojo> saveCustomerMileStoneWithLead(QuickInvoicePojo quickInvoiceCreationPojo) throws Exception;
    public CustMilestoneDetailsPojo updateCustMilestoneDetails(CustMilestoneDetailsPojo milestonePojo);
    public CustMilestoneDetailsPojo getMilestoneById(Long id);
    public List<CustMilestoneDetailsPojo> getAllMilestones(Long leadId);
}
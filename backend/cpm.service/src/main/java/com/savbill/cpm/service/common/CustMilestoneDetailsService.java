package com.savbill.cpm.service.common;

import com.savbill.cpm.model.common.CustMilestoneDetails;
import com.savbill.cpm.model.lead.LeadMaster;
import com.savbill.cpm.pojo.CustMilestoneDetailsPojo;
import com.savbill.cpm.pojo.QuickInvoiceCreationPojo;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.repository.LeadMasterRepository;
import com.savbill.cpm.repository.common.CustMilestoneDetailsRepository;
import com.savbill.cpm.service.LeadMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustMilestoneDetailsService {

    @Autowired
    private CustMilestoneDetailsRepository custMilestoneDetailsRepository;

    @Autowired
    private LeadMasterService leadMasterService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    public List<CustMilestoneDetailsPojo> saveCustomerMileStoneWithLead(QuickInvoiceCreationPojo quickInvoiceCreationPojo) throws Exception {
        List<CustMilestoneDetailsPojo> custMileStoneList = new ArrayList<>();

            if(quickInvoiceCreationPojo.getCustMileStoneDetailsList()!= null && quickInvoiceCreationPojo.getCustMileStoneDetailsList().size()>0){
                for(CustMilestoneDetailsPojo mileStonePojo: quickInvoiceCreationPojo.getCustMileStoneDetailsList()){
                    if(mileStonePojo.getLeadId()!= null) {
                        CustMilestoneDetails custMilestoneDetails = new CustMilestoneDetails(mileStonePojo);
                        Optional<LeadMaster> leadMaster = leadMasterRepository.findById(mileStonePojo.getLeadId());
                        if (leadMaster.isPresent())
                            custMilestoneDetails.setLeadMaster(leadMaster.get());

                        custMilestoneDetails = custMilestoneDetailsRepository.save(custMilestoneDetails);
                        CustMilestoneDetailsPojo custMilestonePojo = new CustMilestoneDetailsPojo(custMilestoneDetails);
                        custMileStoneList.add(custMilestonePojo);
                    }
                }
            }
        return custMileStoneList;
    }

    public CustMilestoneDetailsPojo updateCustMilestoneDetails(CustMilestoneDetailsPojo milestonePojo){
        CustMilestoneDetails customerMilestoneDetailInstance = new CustMilestoneDetails(milestonePojo);
        CustMilestoneDetails existingMilestoneObj = new CustMilestoneDetails();
        if(milestonePojo.getId()!= null)
            existingMilestoneObj = custMilestoneDetailsRepository.findById(milestonePojo.getId()).get();
        if(existingMilestoneObj!= null) {

            customerMilestoneDetailInstance.setLeadMaster(existingMilestoneObj.getLeadMaster());
            customerMilestoneDetailInstance = custMilestoneDetailsRepository.save(customerMilestoneDetailInstance);
            CustMilestoneDetailsPojo pojo= new CustMilestoneDetailsPojo(customerMilestoneDetailInstance);
            return pojo;
        }
        return null;
    }

    public CustMilestoneDetailsPojo getMilestoneById(Long id){
        CustMilestoneDetails existingMilestoneObj = new CustMilestoneDetails();
        if(id!= null)
            existingMilestoneObj= custMilestoneDetailsRepository.findById(id).get();
        if(existingMilestoneObj!= null) {
            CustMilestoneDetailsPojo pojo = new CustMilestoneDetailsPojo(existingMilestoneObj);
//        CustomersPojo customerPojo = customerMapper.domainToDTO(existingMilestoneObj.getCustomers(), new CycleAvoidingMappingContext());
            pojo.setLeadId(Long.parseLong(String.valueOf(existingMilestoneObj.getLeadMaster().getId())));
            return pojo;
        }
        return null;
    }

    public List<CustMilestoneDetailsPojo> getAllMilestones(Long leadId){
        List<CustMilestoneDetails> mileStoneList = new ArrayList<>();
        List<CustMilestoneDetailsPojo> mileStonePojoList = new ArrayList<>();
        if(leadId!= null)
            mileStoneList = custMilestoneDetailsRepository.findAllByLeadMaster_id(leadId);
        else
            mileStoneList = custMilestoneDetailsRepository.findAll();
        if(mileStoneList!= null && mileStoneList.size()>0){
            mileStoneList.forEach(item ->{
                CustMilestoneDetailsPojo pojo = new CustMilestoneDetailsPojo(item);
                if(item.getLeadMaster()!= null && item.getLeadMaster().getId() != null)
                    pojo.setLeadId(Long.parseLong(String.valueOf(item.getLeadMaster().getId())));
                mileStonePojoList.add(pojo);
            });
            return mileStonePojoList;
        }
        return null;
    }
}

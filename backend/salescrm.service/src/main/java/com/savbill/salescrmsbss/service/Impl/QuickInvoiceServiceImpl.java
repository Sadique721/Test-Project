package com.savbill.salescrmsbss.service.Impl;

import com.savbill.salescrmsbss.entity.CustMilestoneDetails;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.pojo.CustMilestoneDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.QuickInvoicePojo;
import com.savbill.salescrmsbss.kafka.KafkaMessageData;
import com.savbill.salescrmsbss.kafka.KafkaMessageSender;
//import com.savbill.salescrmsbss.rabbitMq.MessageSender;
import com.savbill.salescrmsbss.rabbitMq.message.QuickInvoicePojoMessage;
import com.savbill.salescrmsbss.repository.CustMileStoneRepository;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.service.LeadMasterService;
import com.savbill.salescrmsbss.service.QuickInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuickInvoiceServiceImpl implements QuickInvoiceService {

    private final Logger logger = LoggerFactory.getLogger(QuickInvoiceServiceImpl.class);
    @Autowired
    private CustMileStoneRepository custMilestoneDetailsRepository;

    @Autowired
    private LeadMasterService leadMasterService;

//    @Autowired
//    private MessageSender messageSender;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public List<CustMilestoneDetailsPojo> saveCustomerMileStoneWithLead(QuickInvoicePojo quickInvoiceCreationPojo) throws Exception {
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
        try{
            if(custMileStoneList!= null && custMileStoneList.size()>0) {
                QuickInvoicePojoMessage quickInvoicePojoMessage = new QuickInvoicePojoMessage(quickInvoiceCreationPojo);
//                messageSender.send(quickInvoicePojoMessage, RabbitMqConstants.QUEUE_APIGW_LEAD_MILESTONES_MAPPING);
                kafkaMessageSender.send(new KafkaMessageData(quickInvoicePojoMessage, QuickInvoicePojoMessage.class.getSimpleName()));
            }
        }catch(Exception e){
            logger.error("Error While send Lead Message : ", e.getMessage());
        }
        logger.info("Lead has been created successfully with appropriate milestones.");
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

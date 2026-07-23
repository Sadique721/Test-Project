package com.savbill.revenuemanagement.productmanagement.PlanGroup.service;


import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.repository.partner.PlanGroupMappingChargeRelRepo;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.mastermanagement.Branch.repository.BranchRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.service.ServiceAreaService;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Plan.service.PostPaidPlanService;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMapping;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMappingChargeRel;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.ServiceAreaPlanGroupMapping;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.dto.PlanGroupDTO;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupMappingRepository;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.ServiceAreaPlangroupMappingRepo;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Group_Mapping.ProductPlanGroupMappingRepository;


import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SavePlanGroupSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdatePlanGroupSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanGroupService extends AbstractService<PlanGroup, PlanGroupDTO, Integer> {

//    @Autowired
//    private PriceBookPlanDtlRepository priceBookPlanDtlRepository;
//    @Autowired
//    private PartnerService partnerService;
    @Autowired
    private PlanGroupRepository entityRepository;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private PostPaidPlanService postpaidPlanService;

    @Autowired
    private PlanGroupMappingService planGroupMappingService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

//    @Autowired
//    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

//    @Autowired
//    WorkflowAuditService workflowAuditService;

//    @Autowired
//    HierarchyService hierarchyService;

//    @Autowired
//    StaffUserService staffUserService;

//    @Autowired
//    private TatUtils tatUtils;

//    @Autowired
//    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    @PersistenceContext
    private EntityManager entityManager;

//    @Autowired
//    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    private static String MODULE = " [PlanGroupService] ";
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private ProductCategoryRepository productCategoryRepository;
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PlanGroupMappingChargeRelRepo chargerelrepo;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private PartnerRepository partnerRepository;

//    @Autowired
//    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private  PlanGroupRepository planGroupRepository;

    //@Autowired
   // private MessageSender messageSender;

//    @Autowired
//    CreateDataSharedService createDataSharedService;
    @Override
    protected JpaRepository<PlanGroup, Integer> getRepository() {
        return entityRepository;
    }
    
    public void savePlanGroupData(SavePlanGroupSharedDataMessage planGroupSharedDataMessage){
        PlanGroup planGroup=new PlanGroup();
        planGroup.setPlanGroupId(planGroupSharedDataMessage.getPlanGroupId());
        planGroup.setPlanGroupName(planGroupSharedDataMessage.getPlanGroupName());
        planGroup.setStatus(planGroupSharedDataMessage.getStatus());
        planGroup.setMvnoId(planGroupSharedDataMessage.getMvnoId());
        planGroup.setPlantype(planGroupSharedDataMessage.getPlantype());
        planGroup.setPlanMode(planGroupSharedDataMessage.getPlanMode());
        planGroup.setIsDelete(planGroupSharedDataMessage.getIsDelete());
        planGroup.setDbr(planGroupSharedDataMessage.getDbr());
        planGroup.setPlanGroupType(planGroupSharedDataMessage.getPlanGroupType());
        planGroup.setCategory(planGroupSharedDataMessage.getCategory());
        planGroup.setNextTeamHierarchyMappingId(planGroupSharedDataMessage.getNextTeamHierarchyMappingId());
        planGroup.setNextStaff(planGroupSharedDataMessage.getNextStaff());
        planGroup.setAccessibility(planGroupSharedDataMessage.getAccessibility());
        planGroup.setAllowDiscount(planGroupSharedDataMessage.getInvoiceToOrg());
        planGroup.setOfferprice(planGroupSharedDataMessage.getOfferprice());
        planGroup.setProductPlanGroupMappingList(planGroupSharedDataMessage.getProductPlanGroupMappingList());
        planGroup.setTemplateId(planGroupSharedDataMessage.getTemplateId());
        planGroup.setInvoiceToOrg(planGroupSharedDataMessage.getInvoiceToOrg());
        planGroup.setRequiredApproval(planGroupSharedDataMessage.getRequiredApproval());
        planGroup.setCreatedById(planGroupSharedDataMessage.getCreatedById());
        planGroup.setLastModifiedById(planGroupSharedDataMessage.getLastModifiedById());
        planGroupRepository.save(planGroup);


        List<PlanGroupMapping> planGroupMappings = planGroupSharedDataMessage.getPlanMappingList();
        List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
        for (PlanGroupMapping data : planGroupMappings){
            PostpaidPlan plan = postpaidPlanRepo.findById(data.getPlanId().intValue()).get();
            PlanGroupMapping planGroupMapping = new PlanGroupMapping(data,plan, planGroupRepository.findById(planGroup.getPlanGroupId()).get());
            planGroupMappingList.add(planGroupMapping);
        }
        planGroupMappingRepository.saveAll(planGroupMappingList);

        List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = planGroupSharedDataMessage.getPlanGroupMappingChargeRelsList();
        List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList = new ArrayList<>();
        for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
            PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(data.getPlanGroupMappingId()).get();
            Charge charge = chargeRepository.findById(data.getChargeid()).get();
            PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel(data,charge,planGroupMapping);
            planGroupMappingChargeRelList.add(planGroupMappingChargeRel);
        }
        chargerelrepo.saveAll(planGroupMappingChargeRelList);

        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = planGroupSharedDataMessage.getServiceAreaPlanGroupMappingList();
        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList = new ArrayList<>();
        for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
            ServiceArea serviceArea = serviceAreaRepository.findById(data.getServiceAreaId()).orElse(null);
            ServiceAreaPlanGroupMapping serviceAreaPlanGroupMapping = new ServiceAreaPlanGroupMapping(data,planGroup,serviceArea);
            serviceAreaPlanGroupMappingList.add(serviceAreaPlanGroupMapping);
        }
        serviceAreaPlangroupMappingRepo.saveAll(serviceAreaPlanGroupMappingList);
        
    }


    public void updatePlanGroupData(UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage) {
        PlanGroup planGroup=planGroupRepository.findById(updatePlanGroupSharedDataMessage.getPlanGroupId()).orElse(null);
        if(planGroup!=null){
            planGroup.setPlanGroupId(updatePlanGroupSharedDataMessage.getPlanGroupId());
            planGroup.setPlanGroupName(updatePlanGroupSharedDataMessage.getPlanGroupName());
            planGroup.setStatus(updatePlanGroupSharedDataMessage.getStatus());
            planGroup.setMvnoId(updatePlanGroupSharedDataMessage.getMvnoId());
            planGroup.setPlantype(updatePlanGroupSharedDataMessage.getPlantype());
            planGroup.setPlanMode(updatePlanGroupSharedDataMessage.getPlanMode());
            planGroup.setIsDelete(updatePlanGroupSharedDataMessage.getIsDelete());
//            planGroup.setPlanMappingList(updatePlanGroupSharedDataMessage.getPlanMappingList());
            planGroup.setDbr(updatePlanGroupSharedDataMessage.getDbr());
            planGroup.setPlanGroupType(updatePlanGroupSharedDataMessage.getPlanGroupType());
            planGroup.setCategory(updatePlanGroupSharedDataMessage.getCategory());
            planGroup.setNextTeamHierarchyMappingId(updatePlanGroupSharedDataMessage.getNextTeamHierarchyMappingId());
            planGroup.setNextStaff(updatePlanGroupSharedDataMessage.getNextStaff());
            planGroup.setAccessibility(updatePlanGroupSharedDataMessage.getAccessibility());
            planGroup.setAllowDiscount(updatePlanGroupSharedDataMessage.getInvoiceToOrg());
            planGroup.setOfferprice(updatePlanGroupSharedDataMessage.getOfferprice());
            planGroup.setServicearea(null);
            planGroup.setProductPlanGroupMappingList(updatePlanGroupSharedDataMessage.getProductPlanGroupMappingList());
            planGroup.setTemplateId(updatePlanGroupSharedDataMessage.getTemplateId());
            planGroup.setInvoiceToOrg(updatePlanGroupSharedDataMessage.getInvoiceToOrg());
            planGroup.setRequiredApproval(updatePlanGroupSharedDataMessage.getRequiredApproval());
            planGroup.setCreatedById(updatePlanGroupSharedDataMessage.getCreatedById());
            planGroup.setLastModifiedById(updatePlanGroupSharedDataMessage.getLastModifiedById());
            planGroupRepository.save(planGroup);



            List<PlanGroupMapping> planGroupMappings = planGroup.getPlanMappingList();
            List<PlanGroupMapping> existingPlangroupMapping =  planGroupMappingRepository.findByPlanGroupMappingIdIn(planGroupMappings.stream().map(PlanGroupMapping::getPlanGroupMappingId).collect(Collectors.toList()));
            planGroupMappingRepository.deleteAll(existingPlangroupMapping);
            List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
            PostpaidPlan plan=null;
            for (PlanGroupMapping data : planGroupMappings){
                if (data.getPlanId()!=null) {
                     plan = postpaidPlanRepo.findById(data.getPlanId().intValue()).get();
                }else {
                    plan = data.getPlan();

                }
                PlanGroupMapping planGroupMapping = new PlanGroupMapping(data,plan, planGroupRepository.findById(planGroup.getPlanGroupId()).get());
                planGroupMappingList.add(planGroupMapping);
            }
            planGroupMappingRepository.saveAll(planGroupMappingList);

            List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = updatePlanGroupSharedDataMessage.getPlanGroupMappingChargeRelsList();
            List<Long> ids = planGroupMappingChargeRels.stream().map(PlanGroupMappingChargeRel::getId).collect(Collectors.toList());
            List<PlanGroupMappingChargeRel> existingPlanGrpchargeRel = chargerelrepo.findByIdIn(ids);
            chargerelrepo.deleteAll(existingPlanGrpchargeRel);
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList = new ArrayList<>();
            for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
                PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(data.getPlanGroupMappingId()).orElse(null);
                Charge charge = chargeRepository.findById(data.getChargeid()).get();
                PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel(data,charge,planGroupMapping);
                planGroupMappingChargeRelList.add(planGroupMappingChargeRel);
            }
            chargerelrepo.saveAll(planGroupMappingChargeRelList);

            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = updatePlanGroupSharedDataMessage.getServiceAreaPlanGroupMappingList();
            List<Long> saMappingIds = serviceAreaPlanGroupMappings.stream().map(ServiceAreaPlanGroupMapping::getId).collect(Collectors.toList());
            List<ServiceAreaPlanGroupMapping> existingList = serviceAreaPlangroupMappingRepo.findByIdIn(saMappingIds);
            serviceAreaPlangroupMappingRepo.deleteAll(existingList);

            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList = new ArrayList<>();
            for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
                ServiceArea serviceArea = serviceAreaRepository.findById(data.getServiceAreaId()).get();
                ServiceAreaPlanGroupMapping serviceAreaPlanGroupMapping = new ServiceAreaPlanGroupMapping(data,planGroup,serviceArea);
                serviceAreaPlanGroupMappingList.add(serviceAreaPlanGroupMapping);
            }
            serviceAreaPlangroupMappingRepo.saveAll(serviceAreaPlanGroupMappingList);

        }
    }
}

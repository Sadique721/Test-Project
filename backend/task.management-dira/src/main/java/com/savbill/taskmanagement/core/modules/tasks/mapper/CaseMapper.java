package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.NetworkDevices.service.NetworkDeviceService;
import com.savbill.taskmanagement.core.modules.NetworkDevices.service.OLTSlotService;
import com.savbill.taskmanagement.core.modules.NetworkDevices.service.OltPortService;
import com.savbill.taskmanagement.core.modules.Partner.domain.Partner;
import com.savbill.taskmanagement.core.modules.Partner.repository.PartnerRepository;
import com.savbill.taskmanagement.core.modules.Partner.service.PartnerService;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.mapper.ResolutionReasonsMapper;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.service.ResolutionReasonsService;
import com.savbill.taskmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdate;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseUpdateDTO;

import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCategoryRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseDocDetailsRepository;

import com.savbill.taskmanagement.core.modules.tasks.service.LiveCustomerNetworkDetailsService;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = CaseAssignmentMapper.class)
public abstract class CaseMapper implements IBaseMapper<CaseDTO, Case> {

    private String MODULE = " [CaseMapper] ";

    @Override
    @Mappings({
            @Mapping(target = "customers", source = "dto.customersId"),
            @Mapping(target = "currentAssignee", source = "dto.currentAssigneeId"),
            @Mapping(target = "finalResolution", source = "dto.finalResolutionId"),
            @Mapping(target = "finalResolvedBy", source = "dto.finalResolvedById"),
            @Mapping(target = "finalClosedBy", source = "dto.finalClosedById"),
            @Mapping(target = "partner", source = "dto.partnerid")
    })
    public abstract Case dtoToDomain(CaseDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mappings({
            @Mapping(source = "domain.customers", target = "customersId"),
            @Mapping(source = "domain.currentAssignee", target = "currentAssigneeId"),
            @Mapping(source = "domain.finalResolution", target = "finalResolutionId"),
            @Mapping(source = "domain.finalResolvedBy", target = "finalResolvedById"),
            @Mapping(source = "domain.currentAssignee.username", target = "currentAssigneeName"),
            @Mapping(source = "domain.finalClosedBy", target = "finalClosedById"),
            @Mapping(source = "finalClosedDate", target = "finalClosedByDateString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "finalResolutionDate", target = "finalResolutionDateString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "caseStartedOn", target = "caseStartedOnString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "firstAssignedOn", target = "firstAssignedOnString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "domain.partner", target = "partnerid")
    })
    public abstract CaseDTO domainToDTO(Case domain, @Context CycleAvoidingMappingContext context);

    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private CustomersService customersService;
//    @Autowired
//    private CaseReasonService caseReasonService;
//    @Autowired
//    private CaseReasonMapper caseReasonMapper;
    @Autowired
    private ResolutionReasonsService resolutionReasonsService;
    @Autowired
    private ResolutionReasonsMapper resolutionReasonsMapper;
    @Autowired
    private NetworkDeviceService networkDeviceService;
    @Autowired
    private OLTSlotService oltSlotService;
    @Autowired
    private OltPortService oltPortService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private LiveCustomerNetworkDetailsService liveCustomerNetworkDetailsService;
    @Autowired
    private PartnerService partnerService;

//    @Autowired
//    TicketReasonCategoryService ticketReasonCategoryService;

    @Autowired
    CaseCategoryRepository caseCategoryRepository;




    @Autowired
    CaseDocDetailsRepository caseDocDetailsRepository;


    @Autowired
    PartnerRepository partnerRepository;

    Integer fromPartnerToId(Partner partner) {
        return null != partner ? partner.getId() : null;
    }

    Partner fromIdToPartner(Integer id) {
        if (null == id) return null;
        Partner entity = null;
        try {
            entity = partnerService.get(id);
            entity.setId(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromStaffUserToId(StaffUser staffUser) {
        return null != staffUser ? staffUser.getId() : null;
    }

    StaffUser fromIdToStaffUser(Integer id) {
        if (null == id) return null;
        StaffUser entity = null;
        try {
            entity = staffUserService.get(id);
            entity.setId(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            entity = null;
        }
        return entity;
    }




//    Long fromCaseReasonToId(CaseReason caseReason) {
//        return null != caseReason ? caseReason.getReasonId() : null;
//    }

//    CaseReason fromIdToCaseReason(Long id) {
//        if (null == id) return null;
//        CaseReason entity = null;
//        try {
//            CaseReasonDTO dto = caseReasonService.getEntityById(id);
//            entity = caseReasonMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
//            entity.setReasonId(dto.getReasonId());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

    Integer fromCustomerToId(Customers customer) {
        return null != customer ? customer.getId() : null;
    }

    Customers fromIdToCustomer(Integer id) {
        if (null == id) return null;
        Customers entity = null;
        try {
            entity = customersService.get(id);
            entity.setId(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Long fromReasonToId(ResolutionReasons resolutionReasons) {
        return null != resolutionReasons ? resolutionReasons.getId() : null;
    }

    ResolutionReasons fromIdToReason(Long id) {
        if (null == id) return null;
        ResolutionReasons entity = null;
        try {
            ResolutionReasonsDTO dto = resolutionReasonsService.getEntityById(id);
            entity = resolutionReasonsMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
            entity.setId(dto.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @Mappings({@Mapping(source = "caseUpdate.ticket", target = "ticketId"),
            @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd-MM-yyyy hh:mm a"),
            @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd-MM-yyyy hh:mm a")})
    public abstract CaseUpdateDTO updateToUpdateDTO(CaseUpdate caseUpdate, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "dtoData.ticketId", target = "ticket")
    public abstract CaseUpdate updateDTOToUpdate(CaseUpdateDTO dtoData, @Context CycleAvoidingMappingContext context);

//    @AfterMapping
//    void afterMapping(@MappingTarget CaseDTO caseDTO, Case caseDomain) {
//        try {
//            if (null != caseDomain && null != caseDomain.get) {
//                StaffUser staffUsers = staffUserService.get(caseDomain.getStaffUser().getId());
//                if (null != staffUsers) {
//
////                    if (null != staffUsers.getNetworkdevicesId()) {
////                        try {
////                            NetworkDeviceDTO oltDTO = networkDeviceService.getEntityById(Long.valueOf(staffUsers.getNetworkdevicesId()));
////                            caseDTO.setOltName(null != oltDTO ? oltDTO.getName() : "-");
////                        } catch (DataNotFoundException dnfe) {
////                            ApplicationLogger.logger.error(MODULE + " [Network Devices] " + dnfe.getMessage(), dnfe);
////                            caseDTO.setOltName("-");
////                        }
////                    }
//                    //else
//                        caseDTO.setOltName("-");
//
////                    if (null != staffUsers.getServiceAreaId()) {
////                        try {
////                            ServiceAreaDTO serviceArea = serviceAreaService.getEntityById(customers.getServiceAreaId().longValue());
////                            caseDTO.setServiceAreaName(null != serviceArea ? serviceArea.getName() : "-");
////                            caseDTO.setServiceAreaId(null != serviceArea ? serviceArea.getId() : null);
////                        } catch (DataNotFoundException dnfe) {
////                            ApplicationLogger.logger.error(MODULE + " [Service Area] " + dnfe.getMessage(), dnfe);
////                            caseDTO.setServiceAreaName("-");
////                            caseDTO.setServiceAreaId(null);
////                        }
////                    } else
//                    caseDTO.setServiceAreaName("-");
//
////                    if(caseDomain.getCaseReason().getPrimaryKey()!=null) {
////                        caseDTO.setCaseReasonName(caseDomain.getCaseReason().getName());
////                        caseDTO.setCaseReasonTimeUnit(caseDomain.getCaseReason().getTimeUnit());
////                        caseDTO.setCaseReasonTime(caseDomain.getCaseReason().getTime());
////                        caseDTO.setTatConsideration(caseDomain.getCaseReason().getTatConsideration());
////                    }else {
////                    	caseDTO.setCaseReasonName("-");
////                    }
////                    if (null != customers.getOltslotid()) {
////                        try {
////                            OLTSlotDetailDTO slotDetailDTO = oltSlotService.getEntityById(customers.getOltslotid());
////                            caseDTO.setSlotName(null != slotDetailDTO ? slotDetailDTO.getName() : "-");
////                        } catch (DataNotFoundException dnfe) {
////                            ApplicationLogger.logger.error(MODULE + " [Slot] " + dnfe.getMessage(), dnfe);
////                            caseDTO.setSlotName("-");
////                        }
////                    }
////                    else
//                        caseDTO.setSlotName("-");
//
////                    if (null != customers.getOltportid()) {
////                        try {
////                            OLTPortDTO portDTO = oltPortService.getEntityById(customers.getOltportid());
////                            caseDTO.setPortName(null != portDTO ? portDTO.getName() : "-");
////                        } catch (DataNotFoundException dnfe) {
////                            ApplicationLogger.logger.error(MODULE + " [Port] " + dnfe.getMessage(), dnfe);
////                            caseDTO.setPortName("-");
////                        }
////                    } else
//                    caseDTO.setPortName("-");
//
//
////                    List<LiveUserServiceAreaWiseDetailsModel> list = liveCustomerNetworkDetailsService
////                            .getCustomerWiseNetworkDetailsFromLiveUser(customers.getId());
//
////                    if (null != list && 0 < list.size()) {
////                        caseDTO.setLiveUserServiceAreaDetails(list.get(0));
////                    }
//                    //caseDTO.setStaffName(staffUsers.getFullName());
//
//                    if (null != caseDTO.getCurrentAssigneeId()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getCurrentAssigneeId());
//                        if (null != staffUser) {
//                            caseDTO.setCurrentAssigneeName(staffUser.getFullName());
//                        } else caseDTO.setCurrentAssigneeName("-");
//                    } else caseDTO.setCurrentAssigneeName("-");
//
//                    if (null != caseDTO.getFinalClosedById()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getFinalClosedById());
//                        if (null != staffUser) {
//                            caseDTO.setFinalClosedByName(staffUser.getFullName());
//                        } else caseDTO.setFinalClosedByName("-");
//                    } else caseDTO.setFinalClosedByName("-");
//
//
//                    if (null != caseDTO.getFinalResolvedById()) {
//                        StaffUser staffUser = staffUserService.get(caseDTO.getFinalResolvedById());
//                        if (null != staffUser) {
//                            caseDTO.setFinalResolvedByName(staffUser.getFullName());
//                        } else caseDTO.setFinalResolvedByName("-");
//                    } else caseDTO.setFinalResolvedByName("-");
//
//
//                    if (null != caseDTO.getFinalResolutionId()) {
//                        try {
//                            ResolutionReasonsDTO reasonsDTO = resolutionReasonsService.getEntityById(caseDTO.getFinalResolutionId().longValue());
//                            caseDTO.setFinalResolutionName(null != reasonsDTO ? reasonsDTO.getName() : "-");
//                        } catch (DataNotFoundException dnfe) {
//                            ApplicationLogger.logger.error(MODULE + " [Final Resolution] " + dnfe.getMessage(), dnfe);
//                            caseDTO.setFinalResolutionName("-");
//                        }
//                    } else caseDTO.setFinalResolutionName("-");
//
//
////                    if (null != caseDomain.getCaseReason()) {
////                        caseDTO.setReason(caseDomain.getCaseReason().getName());
////                    } else
////                        caseDTO.setReason("-");
//
//                    caseDTO.setUserName(staffUsers.getUsername());
//                    caseDTO.setMobile(staffUsers.getPhone());
//                    caseDTO.setEmail(staffUsers.getEmail());
//
////                    if (null != customers.getParnterId()) {
////                        Partner partner = partnerRepository.findByIdAndIsDeleteIsFalse(customers.getParnterId());
////                        if(partner!=null){
////                            caseDTO.setPartnerName(null != partner.getName() ? partner.getName() : "-");
////                        }
////
////                    }
//                    //else {
//                        caseDTO.setPartnerName("-");
//                    //}
//
//                    caseDTO.setCaseTitle(null != caseDomain.getCaseTitle() ? caseDomain.getCaseTitle() : "-");
//
//                    if (Objects.nonNull(caseDTO.getCaseCategoryId())) {
//                        CaseCategory caseCategory =  caseCategoryRepository.findById(caseDTO.getCaseCategoryId()).orElse(null);
//                        //caseDTO.setCaseCategory(caseCategory.getCategoryName());
//                        //TicketReasonCategory ticketReasonCategory = ticketReasonCategoryRepo.findById(caseDTO.getTicketReasonCategoryId()).orElse(null);
//                       // caseDTO.setCaseReasonCategory(ticketReasonCategory.getCategoryName());
////                        if (caseDTO.getReasonSubCategoryId() != null) {
////                            TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO = ticketReasonSubCategoryService.getEntityById(caseDTO.getReasonSubCategoryId());
////                            if(ticketReasonSubCategoryDTO!=null) {
////                                caseDTO.setCaseReasonSubCategory(ticketReasonSubCategoryDTO.getSubCategoryName());
////                            }else{
////                                caseDTO.setCaseReasonCategory("-");
////                            }
////                                if (caseDTO.getGroupReasonId() != null) {
////                                List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMapping = ticketReasonSubCategoryDTO.getTicketSubCategoryGroupReasonMappingList().stream().filter(t -> t.getId().equals(caseDTO.getGroupReasonId())).collect(Collectors.toList());
////                                if (ticketSubCategoryGroupReasonMapping.size() > 0) {
////                                    caseDTO.setCaseReason(ticketSubCategoryGroupReasonMapping.get(0).getReason());
////                                }
////                            } else {
////                                caseDTO.setCaseReason("-");
////                            }
////                        } else {
////                            caseDTO.setCaseReasonSubCategory("-");
////                        }
//
//
//                    } else {
//                        //caseDTO.setCaseCategory("-");
//                        System.out.println("e");
//                    }
//                }
//                if (caseDomain.getCaseId() != null) {
//                    List<CaseDocDetails> caseDocDetails = caseDocDetailsRepository.findAllByTicketId(caseDomain.getCaseId());
//                    if (caseDocDetails.size() > 0) {
//                        caseDTO.setCaseDocDetails(caseDocDetails);
//                    } else {
//                        caseDTO.setCaseDocDetails(new ArrayList<>());
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }

}

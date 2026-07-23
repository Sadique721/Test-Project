package com.savbill.partnermanagement.modules.PriceGroup.service;

import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService2;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.PlanGroup.mapper.PlangroupMapper;
import com.savbill.partnermanagement.modules.PlanGroup.service.PlanGroupService;
import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBook;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.QPriceBook;
import com.savbill.partnermanagement.modules.PriceGroup.mapper.PriceBookMapper;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookDTO;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookRepository;
//import com.savbill.partnermanagement.modules.partner.entity.QPartner;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceBookService extends ExBaseAbstractService2<PriceBookDTO, PriceBook, Long> {

    @Autowired
    PriceBookRepository priceBookRepository;
    @Autowired
    PriceBookMapper priceBookMapper;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    private PlanGroupService planGroupService;
    @Autowired
    private PlangroupMapper mapper;


    public PriceBookService(PriceBookRepository repository, PriceBookMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBook Service]";
    }

    public List<PriceBookDTO> getAllActive() {
        ApplicationLogger.logger.info("get All Active ");
        List<PriceBook> priceBooks = priceBookRepository.getAllByStatus().stream().filter(priceBook -> (priceBook.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || priceBook.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (priceBook.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(priceBook.getBuId()))).collect(Collectors.toList());
        List<PriceBookDTO> priceBookDTOList = priceBooks.stream().map(data -> priceBookMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        ApplicationLogger.logger.info("priceBookDTOList: " + priceBookDTOList);
        return priceBookDTOList;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
       ApplicationLogger.logger.info("duplicate Verify At Save called");
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = priceBookRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    count = priceBookRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = priceBookRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }

            if (count == 0) {
                flag = true;
            }
        }
        ApplicationLogger.logger.info("flag: " + flag);
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
       ApplicationLogger.logger.info("duplicate Verify At Edit called");
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = priceBookRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    count = priceBookRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = priceBookRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            ApplicationLogger.logger.info("count: " + count);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = priceBookRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = priceBookRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = priceBookRepository.duplicateVerifyAtEdit2(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        ApplicationLogger.logger.info("flag: " + flag);
        return flag;
    }

//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            QPriceBook qPriceBook = QPriceBook.priceBook;
//            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
//            BooleanExpression booleanExpression = qPriceBook.isNotNull().and(qPriceBook.isDeleted.eq(false));
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//            if (filterList.size() > 0) {
//                for (GenericSearchModel genericSearchModel : filterList) {
//                    booleanExpression = booleanExpression.and(qPriceBook.bookname.containsIgnoreCase(genericSearchModel.getFilterValue()));
//
//                }
//            }
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//                booleanExpression = booleanExpression.and(qPriceBook.mvnoId.eq(1).or(qPriceBook.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPriceBook.buId.in(getBUIdsFromCurrentStaff()))));
//            }
//            return makeGenericResponse(genericDataDTO, priceBookRepository.findAll(booleanExpression,pageRequest));
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("PriceBook");
        ApplicationLogger.logger.info("sheet created: " + sheet);
        //createExcel(workbook, sheet, PriceBookDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                PriceBookDTO.class.getDeclaredField("id"),
                PriceBookDTO.class.getDeclaredField("bookname"),
                PriceBookDTO.class.getDeclaredField("description"),
                PriceBookDTO.class.getDeclaredField("validfrom"),
                PriceBookDTO.class.getDeclaredField("validto"),
                PriceBookDTO.class.getDeclaredField("status"),
                PriceBookDTO.class.getDeclaredField("agrPercentage"),
                PriceBookDTO.class.getDeclaredField("tdsPercentage"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        //createPDF(doc, PriceBookDTO.class, getFields());
    }

    public GenericDataDTO getBookByName(String name, PageRequest pageRequest) {
        ApplicationLogger.logger.info("get Book By Name : " + name);
        String SUBMODULE = getModuleNameForLog() + " [getBookByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<PriceBook> priceBookList = null;
            if (getMvnoIdFromCurrentStaff() == 1)
                priceBookList = priceBookRepository.findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);

            else if (getBUIdsFromCurrentStaff().size() == 0)
                priceBookList = priceBookRepository.findAllByBooknameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            else
                priceBookList = priceBookRepository.findAllByNameAndIsDeleteIsFalse(name, pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());

ApplicationLogger.logger.info("priceBookList: " + priceBookList);
            if (null != priceBookList && 0 < priceBookList.getSize()) {
                makeGenericResponse(genericDataDTO, priceBookList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        ApplicationLogger.logger.info("get Book By Name : " + name);
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PriceBook> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = priceBookRepository.findAll(pageRequest);
        else if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                paginationList = priceBookRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
            else
                paginationList = priceBookRepository.findAll(pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());


        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public void validateSaveOrUpdateData(PriceBookDTO entityDTO) {

        if(entityDTO.getRevenueType()!=null && entityDTO.getRevenueType().equalsIgnoreCase("Percentage"))
        {
            if(entityDTO.getCommission_on().equalsIgnoreCase("Plan level"))
            {
                if(entityDTO.getPriceBookPlanDetailList()!=null && !entityDTO.getPriceBookPlanDetailList().isEmpty())
                {
                    for(PriceBookPlanDetailDTO dto:entityDTO.getPriceBookPlanDetailList())
                    {
                        if(dto.getRevenueSharePercentage()==null)
                            throw new RuntimeException("Revenue Share Percentage is mandatory, Please add valid value.");

                    }
                }
            }
        }
        if (entityDTO.getRevenueType() == null || entityDTO.getRevenueType().isEmpty()) {
            throw new RuntimeException("Revenue type is mandatory, Please add valid value.");
        } else if (!(entityDTO.getRevenueType().equalsIgnoreCase("Percentage") || entityDTO.getRevenueType().equalsIgnoreCase("Slab"))) {
            throw new RuntimeException("Please add valid revenue type, Percentage or Slab.");
        } else if (entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
            if (entityDTO.getPriceBookSlabDetailsList() != null && !entityDTO.getPriceBookSlabDetailsList().isEmpty()) {
                throw new RuntimeException("Percentage revenue share cannot have Slab Details.");
            }
        } else if (entityDTO.getRevenueType().equalsIgnoreCase("Slab")) {
            if (entityDTO.getPriceBookPlanDetailList() != null || !entityDTO.getPriceBookPlanDetailList().isEmpty()) {
                entityDTO.getPriceBookPlanDetailList().forEach(x -> {
                    if (x.getRevenueSharePercentage() != null && !x.getRevenueSharePercentage().equalsIgnoreCase("0"))
                        throw new RuntimeException("Slab Revenue cannot have Revenue Share Percentage.");
                });
            }
        }
    }

//    public PriceBookDTO checkAndUpdateAllPlanSelected(PriceBookDTO entityDTO) throws Exception {
//        if(entityDTO!=null && entityDTO.getIsAllPlanSelected()!=null && entityDTO.getIsAllPlanSelected())
//        {
//            PostPaidPlanService postpaidPlanService = SpringContext.getBean(PostPaidPlanService.class);
//            List<PostpaidPlan> postpaidPlanList = postpaidPlanService.getAllActiveEntities("NORMAL", Constants.PLAN_GROUP_ALL);
//            List<PriceBookPlanDetailDTO> bookPlanDetailDTOS=entityDTO.getPriceBookPlanDetailList();
//            if(bookPlanDetailDTOS==null)
//                bookPlanDetailDTOS=new ArrayList<>();
//
//            if(entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_SERVICE))
//            {
//                List<Long> serviceIdList=entityDTO.getServiceCommissionList().stream().map(x->x.getServiceId()).collect(Collectors.toList());
//                if(serviceIdList!=null && serviceIdList.size()>0)
//                    postpaidPlanList=postpaidPlanList.stream().filter(x->serviceIdList.contains(x.getServiceId().longValue())).collect(Collectors.toList());
//            }
//
//            for(PostpaidPlan plan:postpaidPlanList)
//            {
//                PriceBookPlanDetailDTO dto=new PriceBookPlanDetailDTO();
//                dto.setPriceBook(entityDTO);
//                PostpaidPlanPojo pojo=new PostpaidPlanPojo();
//                pojo.setId(plan.getId());
//                dto.setPostpaidPlan(pojo);
//                dto.setPartnerofficeprice(0d);
//                if(entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
//                    if (entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN))
//                        dto.setRevenueSharePercentage(entityDTO.getRevenueSharePercentage().toString());
//                    else
//                        dto.setRevenueSharePercentage(null);
//                }
//                else
//                    dto.setRevenueSharePercentage(null);
//                dto.setOfferprice(plan.getOfferprice());
//                dto.setRegistration("NO");
//                dto.setRenewal("NO");
//                dto.setRevsharen("YES");
//                bookPlanDetailDTOS.add(dto);
//            }
//            entityDTO.setPriceBookPlanDetailList(bookPlanDetailDTOS);
//        }
//        return entityDTO;
//    }


//    public PriceBookDTO checkAndUpdateAllPlangroupSelected(PriceBookDTO entityDTO) {
//        if(entityDTO!=null && entityDTO.getIsAllPlanGroupSelected()!=null && entityDTO.getIsAllPlanGroupSelected()) {
//
//            List<PlanGroup> planGroupList = planGroupService.findAllPlanGroupList(getMvnoIdFromCurrentStaff(), "NORMAL", null, null, null);
//            planGroupList.stream().map(x -> mapper.domainToDTO(x, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//            List<PriceBookPlanDetailDTO> bookPlanDetailDTOS = entityDTO.getPriceBookPlanDetailList();
//            if (bookPlanDetailDTOS == null)
//                bookPlanDetailDTOS = new ArrayList<>();
//
//            for (PlanGroup plan : planGroupList) {
//                PriceBookPlanDetailDTO dto = new PriceBookPlanDetailDTO();
//                dto.setPriceBook(entityDTO);
//                PlanGroupDTO pojo = new PlanGroupDTO();
//                pojo.setPlanGroupId(plan.getPlanGroupId());
//                dto.setPlanGroup(pojo);
//                dto.setPartnerofficeprice(0d);
//                if (entityDTO.getRevenueType().equalsIgnoreCase("Percentage")) {
//                    if (entityDTO.getCommission_on() != null && entityDTO.getCommission_on().equalsIgnoreCase(CommonConstants.COMMISSION_ON_PLAN) && entityDTO.getRevenueSharePercentage() != null)
//                        dto.setRevenueSharePercentage(entityDTO.getRevenueSharePercentage().toString());
//                    else
//                        dto.setRevenueSharePercentage(null);
//                } else
//                    dto.setRevenueSharePercentage(null);
//                dto.setRegistration("NO");
//                dto.setRenewal("NO");
//                dto.setRevsharen("YES");
//                bookPlanDetailDTOS.add(dto);
//            }
//            entityDTO.setPriceBookPlanDetailList(bookPlanDetailDTOS);
//        }
//        return entityDTO;
//    }

//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        QPartner qPartner = QPartner.partner;
//        QPriceBook qPriceBook = QPriceBook.priceBook;
//        BooleanExpression booleanExpression1 = qPriceBook.isDeleted.isNotNull().and(qPriceBook.isDeleted.eq(false));
//        booleanExpression1=booleanExpression1.and(qPriceBook.id.eq(id.longValue()));
//        List<PriceBook> priceBook = IterableUtils.toList(priceBookRepository.findAll(booleanExpression1));
//        BooleanExpression expression = qPartner.isNotNull().and(qPartner.priceBookId.id.eq(priceBook.get(0).getId()));
//        List<Partner> partners = IterableUtils.toList(partnerRepository.findAll(expression));
//        if (partners.size() == 0) {
//            flag = true;
//        }
//        return flag;
//    }
}

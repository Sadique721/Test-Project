package com.savbill.revenuemanagement.productmanagement.Discount.service;


import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.Discount;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountMappingPojo;
import com.savbill.revenuemanagement.productmanagement.Discount.repocitory.DiscountMappingRepository;
import com.savbill.revenuemanagement.productmanagement.Discount.repocitory.DiscountPlanMappingRepo;
import com.savbill.revenuemanagement.productmanagement.Discount.repocitory.DiscountRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SaveDiscountSharedMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdateDiscountSharedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiscountService extends AbstractService<Discount, DiscountMappingPojo, Integer> {

//    @Autowired
//    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private DiscountRepository entityRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private DiscountMappingRepository discountMappingRepository;

    @Autowired
    private DiscountPlanMappingRepo discountPlanMappingRepo;
    @Autowired
    DiscountRepository discountRepository;

    public DiscountService() {
        sortColMap.put("id","discountid");
    }

    public static final String MODULE = "[DiscountService]";

    @Override
    protected JpaRepository<Discount, Integer> getRepository() {
        return entityRepository;
    }

    public void saveDiscountData(SaveDiscountSharedMessage savePlanSharedDataMessage) {
        Discount discount=new Discount();
        discount.setId(savePlanSharedDataMessage.getId());
        discount.setName(savePlanSharedDataMessage.getName());
        discount.setStatus(savePlanSharedDataMessage.getStatus());
        discount.setMvnoId(savePlanSharedDataMessage.getMvnoId());
        //discount.setDiscMappingList(savePlanSharedDataMessage.getDiscMappingList());
        List<DiscountMapping> discountMapping=new ArrayList<>();
        for(DiscountMapping list: savePlanSharedDataMessage.getDiscMappingList()){
            list.setValidFrom(LocalDate.parse(list.getValidFromString()));
            list.setValidUPTO(LocalDate.parse(list.getValidUPTOString()));
            list.setDiscount(discount);
            discountMapping.add(list);

        }
        discount.setDiscMappingList(discountMapping);

        discount.setPlanMappingList(savePlanSharedDataMessage.getPlanMappingList());
        discount.setBuId(savePlanSharedDataMessage.getBuId());
        discountRepository.save(discount);
    }

    public void updateDiscountData(UpdateDiscountSharedMessage updateDiscountSharedMessage) {
        Discount discount=discountRepository.findById(updateDiscountSharedMessage.getId()).orElse(null);
        if(discount!=null){
        discount.setId(updateDiscountSharedMessage.getId());
        discount.setName(updateDiscountSharedMessage.getName());
        discount.setStatus(updateDiscountSharedMessage.getStatus());
        discount.setMvnoId(updateDiscountSharedMessage.getMvnoId());
        List<DiscountMapping> discountMapping=new ArrayList<>();
        for(DiscountMapping list: updateDiscountSharedMessage.getDiscMappingList()){
            list.setValidFrom(LocalDate.parse(list.getValidFromString()));
            list.setValidUPTO(LocalDate.parse(list.getValidUPTOString()));
            discountMapping.add(list);
        }
        discount.setDiscMappingList(discountMapping);

        discount.setPlanMappingList(updateDiscountSharedMessage.getPlanMappingList());
        discount.setBuId(updateDiscountSharedMessage.getBuId());
        discountRepository.save(discount);
        }

    }
//
//    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.Discount', '1')")
//    public Page<Discount> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff());
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public List<Discount> getAllActiveEntities() {
//        return entityRepository.findByStatus(CommonConstants.YES_STATUS).stream().filter(discount -> discount.getMvnoId() == getMvnoIdFromCurrentStaff() || discount.getMvnoId() == null).collect(Collectors.toList());
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public List<Discount> getAllEntities() {
//        return entityRepository.findAll().stream().filter(discount -> (discount.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || discount.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1)
//            && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))).collect(Collectors.toList());
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
//    public void deleteDiscount(Integer id) throws Exception {
//        Discount discount = entityRepository.getOne(id);
//        discount.setIsDelete(true);
//        entityRepository.save(discount);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public Discount getDiscountForAdd() {
//        return new Discount();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public Discount getDiscountForEdit(Integer id) throws Exception {
//        return entityRepository.getOne(id);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public DiscountMapping getDiscountMapping() {
//        return new DiscountMapping();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public List<DiscountMapping> getDiscountMappingList() {
//        return new ArrayList<>();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
//    public Discount deleteSlab(Discount discount, int index) {
//        discount.getDiscMappingList().remove(index);
//        return discount;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public DiscountPlanMapping getDiscountPlanMapping() {
//        return new DiscountPlanMapping();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public List<DiscountPlanMapping> getDiscountPlanMappingList() {
//        return new ArrayList<>();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_DELETE + "\")")
//    public Discount deleteTier(Discount discount, int index) {
//        discount.getPlanMappingList().remove(index);
//        return discount;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_ADD + "\")")
//    public Discount saveDiscount(Discount discount) throws Exception {
//        String SUBMODULE = MODULE + "[saveDiscount()]";
//        try {
//            for (DiscountMapping item : discount.getDiscMappingList()) {
//                item.setDiscount(discount);
//            }
//            for (DiscountPlanMapping item : discount.getPlanMappingList()) {
//                item.setDiscount(discount);
//            }
//            if(getMvnoIdFromCurrentStaff() != null) {
//            	discount.setMvnoId(getMvnoIdFromCurrentStaff());
//        	}
//            Discount save = entityRepository.save(discount);
//            return save;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public DiscountPojo save(DiscountPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + "[save()]";
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//            Discount obj = covertDiscountPojoToDiscountModel(pojo);
//            if(getBUIdsFromCurrentStaff().size() == 1)
//                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//            obj = saveDiscount(obj);
//            pojo = covertDiscountModelToDiscountPojo(obj);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    public Discount covertDiscountPojoToDiscountModel(DiscountPojo discountPojo) throws Exception {
//        String SUBMODULE = MODULE + "[covertDiscountPojoToDiscountModel()]";
//        Discount discount = null;
//        try {
//            if (discountPojo != null) {
//                discount = new Discount();
//                if (discountPojo.getId() != null) {
//                    discount.setId(discountPojo.getId());
//                }
//                discount.setName(discountPojo.getName());
//                discount.setDesc(discountPojo.getDesc());
//                if(discountPojo.getMvnoId() != null) {
//                	discount.setMvnoId(discountPojo.getMvnoId());
//            	}
//                discount.setStatus(discountPojo.getStatus());
//
//                if (discountPojo.getDiscoundMappingList() != null && discountPojo.getDiscoundMappingList().size() > 0) {
//                    List<DiscountMapping> discountMappingsList = new ArrayList<DiscountMapping>();
//                    DiscountMapping discountMapping = null;
//                    List<DiscountMapping> oldDiscountMappingList=discountMappingRepository.findByDiscountId(discountPojo.getId());
//                    List<DiscountMappingPojo> newDiscountMappingList=discountPojo.getDiscoundMappingList().stream().filter(discountMappingPojo -> Objects.isNull(discountMappingPojo.getId())).collect(Collectors.toList());
//                    List<DiscountPlanMapping> exstingPlan=discountPlanMappingRepo.findByDiscountId(discountPojo.getId());
//                    List<DiscountPlanMappingPojo> discountPlanMappings=discountPojo.getDiscoundPlanMappingList().stream().filter(discountPlanMappingPojo -> Objects.isNull(discountPlanMappingPojo.getId())).collect(Collectors.toList());
//
//                    for(DiscountMapping oldMapping : oldDiscountMappingList){
//                        for(DiscountMappingPojo newDisc: newDiscountMappingList){
//
//                            if(oldMapping.getDiscountType().equals(newDisc.getDiscountType()) && oldMapping.getAmount().equals(newDisc.getAmount()) && oldMapping.getValidFrom().equals(newDisc.getValidFrom()) && oldMapping.getValidUPTO().equals(newDisc.getValidUpto())){
//                                throw new IllegalArgumentException("Unable save, Found duplicate mapping entry under Discount Mapping.");
//                            }
//
//                        }
//                    }
//
//                    for(DiscountPlanMapping discountPlanMapping: exstingPlan){
//                        for(DiscountPlanMappingPojo discountPlanMapping1:discountPlanMappings){
//                            if(discountPlanMapping.getPlanId()==discountPlanMapping1.getPlanId()){
//                                throw new IllegalArgumentException("Unable Save, Found duplicate mapping entry under Discount Plan Mapping.");
//                            }
//                        }
//                    }
//
//                    for (DiscountMappingPojo element : discountPojo.getDiscoundMappingList()) {
//                        discountMapping = new DiscountMapping(element, discount);
//                        discountMappingsList.add(discountMapping);
//                    }
//                    discount.setDiscMappingList(discountMappingsList);
//                }
//
//                if (discountPojo.getDiscoundPlanMappingList() != null && discountPojo.getDiscoundPlanMappingList().size() > 0) {
//                    List<DiscountPlanMapping> discountPlanMappingsList = new ArrayList<DiscountPlanMapping>();
//                    DiscountPlanMapping discountPlanMapping = null;
//                    for (DiscountPlanMappingPojo element : discountPojo.getDiscoundPlanMappingList()) {
//                        discountPlanMapping = new DiscountPlanMapping(element, discount);
//                        discountPlanMappingsList.add(discountPlanMapping);
//                    }
//                    discount.setPlanMappingList(discountPlanMappingsList);
//                }
//
//                return discount;
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return discount;
//    }
//
//    public DiscountPojo covertDiscountModelToDiscountPojo(Discount discount) throws Exception {
//
//        String SUBMODULE = MODULE + "[covertDiscountModelToDiscountPojo()]";
//
//        DiscountPojo pojo = null;
//        try {
//            if (discount != null) {
//                pojo = new DiscountPojo();
//                pojo.setId(discount.getId());
//                pojo.setName(discount.getName());
//                pojo.setDesc(discount.getDesc());
//                pojo.setStatus(discount.getStatus());
//                pojo.setCreatedById(discount.getCreatedById());
//                pojo.setCreatedate(discount.getCreatedate());
//                pojo.setCreatedByName(discount.getCreatedByName());
//                pojo.setLastModifiedById(discount.getLastModifiedById());
//                pojo.setUpdatedate(discount.getUpdatedate());
//                pojo.setLastModifiedByName(discount.getLastModifiedByName());
//                if(discount.getMvnoId() != null) {
//                	pojo.setMvnoId(discount.getMvnoId());
//            	}
//                if (discount.getDiscMappingList() != null && discount.getDiscMappingList().size() > 0) {
//                    List<DiscountMappingPojo> discoundMappingList = new ArrayList<>();
//                    for (DiscountMapping discountMapping : discount.getDiscMappingList()) {
//                        DiscountMappingPojo discountMappingPojo = new DiscountMappingPojo(discountMapping);
//                        discountMappingPojo.setId(discountMapping.getId());
//                        discoundMappingList.add(discountMappingPojo);
//                    }
//                    pojo.setDiscoundMappingList(discoundMappingList);
//                }
//
//                if (discount.getPlanMappingList() != null && discount.getPlanMappingList().size() > 0) {
//                    List<DiscountPlanMappingPojo> discoundPlanMappingList = new ArrayList<>();
//                    for (DiscountPlanMapping discountPlanMapping : discount.getPlanMappingList()) {
//                        DiscountPlanMappingPojo discountPlanMappingPojo = new DiscountPlanMappingPojo(discountPlanMapping);
//                        discountPlanMappingPojo.setId(discountPlanMapping.getId());
//                        discoundPlanMappingList.add(discountPlanMappingPojo);
//                    }
//                    pojo.setDiscoundPlanMappingList(discoundPlanMappingList);
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojo;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DISCOUNT_ALL + "\",\"" + AclConstants.OPERATION_DISCOUNT_VIEW + "\")")
//    public List<DiscountPojo> convertResponseModelIntoPojo(List<Discount> discountList) throws Exception {
//        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
//        List<DiscountPojo> pojoListRes = new ArrayList<>();
//        try {
//            if (discountList != null && discountList.size() > 0) {
//                for (Discount discount : discountList) {
//                    pojoListRes.add(covertDiscountModelToDiscountPojo(discount));
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return pojoListRes;
//    }
//
//    public void validateRequest(DiscountPojo pojo, Integer operation) {
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null)
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.YES_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.NO_STATUS))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE))
//                && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Discount");
//        List<DiscountPojo> discountPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createExcel(workbook, sheet, DiscountPojo.class, discountPojos, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                DiscountPojo.class.getDeclaredField("id"),
//                DiscountPojo.class.getDeclaredField("name"),
//                DiscountPojo.class.getDeclaredField("status"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<DiscountPojo> discountPojos = convertResponseModelIntoPojo(entityRepository.findAll());
//        createPDF(doc, DiscountPojo.class, discountPojos, getFields());
//    }
//
//    @Override
//    public Page<Discount> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = MODULE + " [search()] ";
//        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//        try {
//            for (GenericSearchModel searchModel : filterList) {
//                if (null != searchModel.getFilterColumn()) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getDiscountByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                } else
//                    throw new RuntimeException("Please Provide Search Column!");
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return null;
//    }
//
//    public Page<Discount> getDiscountByName(String s1, PageRequest pageRequest) {
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest);
//        if(getBUIdsFromCurrentStaff().size() == 0)
//            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        else
//            return entityRepository.findAllByNameAndIsDeleteIsFalse(s1, pageRequest, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//    }
//
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff().size() == 0)
//                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        Integer mvnoId = getMvnoIdFromCurrentStaff();
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff().size() == 0)
//                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//            }
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
//                else {
//                    if(getBUIdsFromCurrentStaff().size() == 0)
//                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                    else
//                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//                }
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public Page<Discount> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
//                                List<GenericSearchModel> filterList) {
//        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            return entityRepository.findAll(pageRequest);
//        if (null == filterList || 0 == filterList.size())
//            return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
//        else
//            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
//    }
//
//    @Override
//    public Discount get(Integer id) {
//        Discount discount = super.get(id);
//        if (getMvnoIdFromCurrentStaff() == 1 || ((discount.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || discount.getMvnoId() == 1) && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))))
//            return discount;
//        return null;
//    }
//
//    public Discount getEntityForUpdateAndDelete(Integer id) {
//        Discount discount = get(id);
//        if(discount == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == discount.getMvnoId().intValue()) && (discount.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(discount.getBuId()))))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        return discount;
//    }
}

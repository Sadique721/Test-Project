package com.savbill.cpm.modules.partnerdocDetails.Service;

import brave.Tracer;
import com.savbill.cpm.constants.ClientServiceConstant;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.DocumentConstants;
import com.savbill.cpm.constants.SubscriberConstants;
import com.savbill.cpm.core.exceptions.DataNotFoundException;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.fileUtillity.FileUtility;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.partnerdocDetails.domain.PartnerdocDetails;
import com.savbill.cpm.modules.partnerdocDetails.mapper.PartnerDocDetailsMapper;
import com.savbill.cpm.modules.partnerdocDetails.model.PartnerdocDTO;
import com.savbill.cpm.modules.partnerdocDetails.repository.PartnerDocdetailsRepository;
import com.savbill.cpm.repository.postpaid.PartnerRepository;
import com.savbill.cpm.service.common.ClientServiceSrv;
import com.savbill.cpm.service.postpaid.PartnerService;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerDocDetailsService extends ExBaseAbstractService<PartnerdocDTO, PartnerdocDetails,Long> {
    @Autowired
    private PartnerDocDetailsMapper mapper;

    public PartnerDocDetailsService(JpaRepository<PartnerdocDetails, Long> repository, IBaseMapper<PartnerdocDTO, PartnerdocDetails> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PartnerDocDetailsService]";
    }
    @Autowired
    PartnerDocdetailsRepository partnerDocdetailsRepository;
    @Autowired
    PartnerRepository partnerRepository;
    
    @Autowired
    PartnerDocDetailsMapper partnerDocDetailsMapper;
    
    @Autowired
    PartnerService partnerService;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(PartnerService.class);


    private String PATH;

    public String deleteDocument(List<Long> docIdList, Integer partnerId) throws Exception {

        String SUBMODULE = getModuleNameForLog() + " [deleteDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PARTNER_DOC_PATH).get(0).getValue();
        try {
            Partner partner = partnerService.get(partnerId);
            if (null != partner) {
                String subFolderName = partner.getName().trim() + "/";
                String path = PATH + subFolderName;
                for (Long id : docIdList) {
                    PartnerdocDTO dbDTO = getEntityById(id);
                    if (null != dbDTO) {
                        fileUtility.removeFileAtServer(dbDTO.getUniquename(), path);
                        super.deleteEntity(dbDTO);
                    } else throw new DataNotFoundException("Document Not Found with id = " + id);
                }
                return SubscriberConstants.DELETED_SUCCESSFULLY;
            } else throw new DataNotFoundException("Customer Not Found!");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List uploadDocument(List<PartnerdocDTO> partnerdocDTOList, MultipartFile[] file) throws Exception {

        String SUBMODULE = " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PARTNER_DOC_PATH).get(0).getValue();
        ApplicationLogger.logger.info(SUBMODULE + " :Partner-Doc-Path:" + PATH);
        List<PartnerdocDTO> finalResponseList = new ArrayList<>();
        try {
            for (PartnerdocDTO partnerdoc : partnerdocDTOList) {
                if (null != partnerdoc.getPartnerId()) {
                    Partner partner = partnerService.get(partnerdoc.getPartnerId());
                    if (null != partner) {
                        partnerdoc.setMode(DocumentConstants.OFFLINE);
                        String subFolderName = File.separator + partner.getName().trim() + File.separator;
                        ApplicationLogger.logger.info(SUBMODULE + " :PartnerFile-Doc-Sub-Path:" + subFolderName);
                        String path = PATH +subFolderName;
                        ApplicationLogger.logger.info(SUBMODULE + " :PartnerFile-Doc-Full-Path:" + path);
                        if (null == partnerdoc.getDocId()) {
                            if (null != partnerdoc.getFilename()) {
                                MultipartFile file1 = fileUtility.getFileFromArray(partnerdoc.getFilename(), file);
                                if (null != file1) {
                                    partnerdoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                    partnerdoc = super.saveEntity(partnerdoc);
                                    finalResponseList.add(partnerdoc);
                                }
                            }
                        } else {
                            PartnerdocDTO partnerdocDTO = getEntityById(partnerdoc.getDocId());
                            if (null != partnerdoc) {
                                if (null != partnerdoc.getFilename()
                                        && null != partnerdoc.getFilename()
                                        && !partnerdoc.getFilename().equalsIgnoreCase(partnerdoc.getFilename())) {
                                    fileUtility.removeFileAtServer(partnerdoc.getUniquename(), path);
                                }
                                MultipartFile file1 = fileUtility.getFileFromArray(partnerdoc.getFilename(), file);
                                if (null != file1) {
                                    partnerdoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                }
                                partnerdoc = super.updateEntity(partnerdoc);
                            }
                            finalResponseList.add(partnerdoc);
                        }
                    } else
                        throw new DataNotFoundException("Partner Not Found!");
                } else
                    throw new RuntimeException("Please Provide Partner");
            }
            return finalResponseList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        
    }

    public List<PartnerdocDTO> findDocsByPartnerId(Integer partnerId) {
        String SUBMODULE = getModuleNameForLog() + " [findDocsByPartnerId()] ";
        try {
            Partner partner = partnerService.get(partnerId);
            List<PartnerdocDTO> partnerdocDTOList = new ArrayList<>();
            List<PartnerdocDetails> docList = partnerDocdetailsRepository.findAllByPartner_idAndIsDeleteIsFalse(partnerId);
            if (null != docList && 0 < docList.size()) {
                partnerdocDTOList = docList.stream().map(data -> partnerDocDetailsMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                        .collect(Collectors.toList());
            }
            for(PartnerdocDTO partnerdocDTO : partnerdocDTOList){
                if(partnerdocDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD) || partnerdocDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                    partnerdocDTO.setDocumentNumber(getMaskedDocuments(DocumentConstants.PAN_CARD, partner.getPanName()));
            }
            return partnerdocDTOList.stream().sorted(Comparator.comparing(PartnerdocDTO :: getDocId).reversed()).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    private String getMaskedDocuments(String documentType, String documentNumber){
        if(documentType.equalsIgnoreCase(DocumentConstants.PAN_CARD))
            return DocumentConstants.PAN_STAR_PATTERN + documentNumber.substring(6);
        return "";
    }
    public Partner getById(Integer id) {
        if (getMvnoIdFromCurrentStaff() == 1) return partnerRepository.findByIdAndIsDeleteIsFalse(id);
        return partnerRepository.findByIdAndIsDeleteIsFalseAndMvnoIdIn(id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @Override
    public void deleteEntity(PartnerdocDTO entity) throws Exception {
        try{
            Partner partner = getById(entity.getPartnerId());
            if(entity.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                partner.setPanName(null);
            entity.setIsDelete(true);
            partnerDocdetailsRepository.save(partnerDocDetailsMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
        }
        catch (Exception ex){
            throw ex;
        }
    }



    @Override
    public boolean deleteVerification(Integer partnerId) throws Exception {
        boolean flag = false;
        Integer count = partnerDocdetailsRepository.deleteVerify(partnerId);
        if(count>0){
            flag=true;
        }
        return flag;
    }

    public PartnerdocDTO getEntityForUpdateAndDelete(Long id) throws Exception {
        Partner partner = getById(id.intValue());

        PartnerdocDTO entityDTO = mapper.domainToDTO(partnerDocdetailsRepository.findById(id).get(), new CycleAvoidingMappingContext());

        if(getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        if(entityDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entityDTO.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return entityDTO;
    }


    public  PartnerdocDTO updateEntity(PartnerdocDTO entityDTO) throws Exception {
        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        PartnerdocDetails partnerdocDetails = partnerDocDetailsMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
       Partner old1=null;
       if(entityDTO.getPartnerId()!=null)
       {
           old1 = getById(entityDTO.getPartnerId());
       }
        try {

            if(entityDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entityDTO.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            if (old1 != null) {
                log.info("Partner update details: " + UpdateDiffFinder.getUpdatedDiff(old1, partnerdocDetails));
            }
            return partnerDocDetailsMapper.domainToDTO(partnerDocdetailsRepository.save(partnerdocDetails), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            throw ex;
        }
    }


    public PartnerdocDTO getEntityById(Long id){
        Optional<PartnerdocDetails> partnerdocDetails = partnerDocdetailsRepository.findById(id);
        return partnerDocDetailsMapper.domainToDTO(partnerdocDetails.get(), new CycleAvoidingMappingContext());

    }


    public Partner getEntityForUpdateAndDelete(Integer id) {
        Partner partner = partnerRepository.findById(id).orElse(null);
        if(partner == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == partner.getMvnoId().intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return partner;
    }



}

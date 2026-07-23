package com.savbill.partnermanagement.modules.partnerdocDetails.Service;


import com.savbill.partnermanagement.constants.APIConstants;
import com.savbill.partnermanagement.constants.ClientServiceConstant;
import com.savbill.partnermanagement.constants.DocumentConstants;
import com.savbill.partnermanagement.constants.SubscriberConstants;
import com.savbill.partnermanagement.core.constants.Constants;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.exceptions.DataNotFoundException;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.partnerdocDetails.domain.PartnerdocDetails;
import com.savbill.partnermanagement.modules.partnerdocDetails.mapper.PartnerDocDetailsMapper;
import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerdocDTO;
import com.savbill.partnermanagement.modules.partnerdocDetails.repository.PartnerDocdetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PartnerDocDetailsService extends ExBaseAbstractService<PartnerdocDTO, PartnerdocDetails, Long> {
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
    private String PATH;

    public String deleteDocument(List<Long> docIdList, Integer partnerId) throws Exception {
        ApplicationLogger.logger.info("delete Document called");
        String SUBMODULE = getModuleNameForLog() + " [deleteDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PARTNER_DOC_PATH).get(0).getValue();
        ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + PATH);
        try {
            Partner partner = partnerService.get(partnerId);
            ApplicationLogger.logger.info("partner: " + partner);
            if (null != partner) {
                ApplicationLogger.logger.info("partner: " + partner);
                String subFolderName = partner.getName().trim() + "/";
                String path = PATH + subFolderName;
                for (Long id : docIdList) {
                    PartnerdocDTO dbDTO = getEntityById(id);
                    if (null != dbDTO) {
                        fileUtility.removeFileAtServer(dbDTO.getUniquename(), path);
                        super.deleteEntity(dbDTO);
                    } else throw new DataNotFoundException("Document Not Found with id = " + id);
                }
                ApplicationLogger.logger.info("Document Deleted Successfully");
                return SubscriberConstants.DELETED_SUCCESSFULLY;
            } else throw new DataNotFoundException("Customer Not Found!");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List uploadDocument(List<PartnerdocDTO> partnerdocDTOList, MultipartFile[] file) throws Exception {
        ApplicationLogger.logger.info("upload Document called");
        String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PARTNER_DOC_PATH).get(0).getValue();
        List<PartnerdocDTO> finalResponseList = new ArrayList<>();
        try {
            for (PartnerdocDTO partnerdoc : partnerdocDTOList) {
                if (null != partnerdoc.getPartnerId()) {
                    ApplicationLogger.logger.info("partner ID: " + partnerdoc.getPartnerId());
                    Partner partner = partnerService.get(partnerdoc.getPartnerId());
                    if (null != partner) {
                        ApplicationLogger.logger.info("partner: " + partner);
                        partnerdoc.setMode(DocumentConstants.OFFLINE);
                        partnerdoc.setCreatedById(super.getMvnoIdFromCurrentStaff());
                        partnerdoc.setLastModifiedById(super.getMvnoIdFromCurrentStaff());
                        String subFolderName = File.separator + partner.getName().trim() + File.separator;
                        String path = PATH + subFolderName;
                        ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
                        if (null == partnerdoc.getDocId()) {
                            ApplicationLogger.logger.info("partnerdoc: " + partnerdoc);
                            if (null != partnerdoc.getFilename()) {
                                ApplicationLogger.logger.info("partnerdoc.getFilename(): " + partnerdoc.getFilename());
                                MultipartFile file1 = fileUtility.getFileFromArray(partnerdoc.getFilename(), file);
                                if (null != file1) {
                                    ApplicationLogger.logger.info("file: " + file1);
                                    partnerdoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                    partnerdoc = super.saveEntity(partnerdoc);
                                    finalResponseList.add(partnerdoc);
                                    ApplicationLogger.logger.info("partnerdoc: " + partnerdoc);
                                }
                            }
                        } else {
                            PartnerdocDTO partnerdocDTO = getEntityById(partnerdoc.getDocId());
                            ApplicationLogger.logger.info("partner doc DTO: " + partnerdocDTO);
                            if (null != partnerdoc) {
                                ApplicationLogger.logger.info("partner doc: " + partnerdoc);
                                if (null != partnerdoc.getFilename()
                                        && null != partnerdoc.getFilename()
                                        && !partnerdoc.getFilename().equalsIgnoreCase(partnerdoc.getFilename())) {
                                    fileUtility.removeFileAtServer(partnerdoc.getUniquename(), path);
                                }
                                MultipartFile file1 = fileUtility.getFileFromArray(partnerdoc.getFilename(), file);
                                if (null != file1) {
                                    ApplicationLogger.logger.info("file: " + file1);
                                    partnerdoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                }
                                partnerdoc = super.updateEntity(partnerdoc);
                            }
                            finalResponseList.add(partnerdoc);
                        }
                    } else
                        ApplicationLogger.logger.info("partner: " + partner);
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
        ApplicationLogger.logger.debug(SUBMODULE + ":partnerId:" + partnerId);
        try {
            Partner partner = partnerService.get(partnerId);
            List<PartnerdocDTO> partnerdocDTOList = new ArrayList<>();
            List<PartnerdocDetails> docList = partnerDocdetailsRepository.findAllByPartner_idAndIsDeleteIsFalse(partnerId);
            ApplicationLogger.logger.info("Partner Id: " + partnerId + " docList: " + docList);
            if (null != docList && 0 < docList.size()) {
                ApplicationLogger.logger.info("docList: " + docList);
                partnerdocDTOList = docList.stream().map(data -> partnerDocDetailsMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                        .collect(Collectors.toList());
            }
            for (PartnerdocDTO partnerdocDTO : partnerdocDTOList) {
                if (partnerdocDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD) || partnerdocDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                    ApplicationLogger.logger.info("partnerdocDTO: " + partnerdocDTO);
                partnerdocDTO.setDocumentNumber(getMaskedDocuments(DocumentConstants.PAN_CARD, partner.getPanName()));
            }
            return partnerdocDTOList.stream().sorted(Comparator.comparing(PartnerdocDTO::getDocId).reversed()).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    private String getMaskedDocuments(String documentType, String documentNumber) {
        if (documentType.equalsIgnoreCase(DocumentConstants.PAN_CARD))
            return DocumentConstants.PAN_STAR_PATTERN + documentNumber.substring(6);
        ApplicationLogger.logger.info("documentNumber: " + documentNumber);
        return "";
    }

    public Partner getById(Integer id) {
        if (getMvnoIdFromCurrentStaff() == 1) return partnerRepository.findByIdAndIsDeleteIsFalse(id);
        ApplicationLogger.logger.info("getMvnoIdFromCurrentStaff: " + getMvnoIdFromCurrentStaff());
        return partnerRepository.findByIdAndIsDeleteIsFalseAndMvnoIdIn(id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @Override
    public void deleteEntity(PartnerdocDTO entity) throws Exception {
        ApplicationLogger.logger.info("delete Entity called");
        try {
            Partner partner = getById(entity.getPartnerId());
            ApplicationLogger.logger.info("partner: " + partner);
            if (entity.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                partner.setPanName(null);
            entity.setIsDelete(true);
            entity.setLastModifiedById(super.getMvnoIdFromCurrentStaff());
            entity.setCreatedById(super.getMvnoIdFromCurrentStaff());
            partnerDocdetailsRepository.save(partnerDocDetailsMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
            ApplicationLogger.logger.info("Document Deleted Successfully");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean deleteVerification(Integer partnerId) throws Exception {
        ApplicationLogger.logger.info("delete Verification called");
        boolean flag = false;
        Integer count = partnerDocdetailsRepository.deleteVerify(partnerId);
        ApplicationLogger.logger.info("count: " + count);
        if (count > 0) {
            flag = true;
        }
        ApplicationLogger.logger.info("flag: " + flag);
        return flag;
    }

    public PartnerdocDTO getEntityForUpdateAndDelete(Long id) throws Exception {
        ApplicationLogger.logger.info("get Entity For Update And Delete called");
        Partner partner = getById(id.intValue());

        PartnerdocDTO entityDTO = mapper.domainToDTO(partnerDocdetailsRepository.findById(id).get(), new CycleAvoidingMappingContext());
        ApplicationLogger.logger.info("entityDTO: " + entityDTO);
        if (getMvnoIdFromCurrentStaff() != null) {
            ApplicationLogger.logger.info("getMvnoIdFromCurrentStaff: " + getMvnoIdFromCurrentStaff());
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        if (entityDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entityDTO.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        ApplicationLogger.logger.info("get Mvno Id From Current Staff: " + getMvnoIdFromCurrentStaff());
        return entityDTO;
    }

    public PartnerdocDTO updateEntity(PartnerdocDTO entityDTO) throws Exception {
        ApplicationLogger.logger.info("update Entity called");
        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        PartnerdocDetails partnerdocDetails = partnerDocDetailsMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        ApplicationLogger.logger.info("partner doc Details: " + partnerdocDetails);
        try {
            if (entityDTO == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entityDTO.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            ApplicationLogger.logger.error("MVNO_DELETE_UPDATE_ERROR_MSG: " + Constants.MVNO_DELETE_UPDATE_ERROR_MSG);

            return partnerDocDetailsMapper.domainToDTO(partnerDocdetailsRepository.save(partnerdocDetails), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    public PartnerdocDTO getEntityById(Long id) {
        ApplicationLogger.logger.info("get Entity By Id called :" + id);
        Optional<PartnerdocDetails> partnerdocDetails = partnerDocdetailsRepository.findById(id);
        ApplicationLogger.logger.info("partner doc Details: " + partnerdocDetails);
        return partnerDocDetailsMapper.domainToDTO(partnerdocDetails.get(), new CycleAvoidingMappingContext());

    }
}
package com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.service.impl;


import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.constants.SubscriberConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.fileUtillity.FileUtility;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDTO;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.domain.MvnoDocDetails;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.mapper.MvnoDocDetailsMapper;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.repository.MvnoDocDetailsRepository;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.service.DocDetailsService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.MvnoMessage;
import com.savbill.commonGateway.spring.LoggedInUserService;
import com.itextpdf.text.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocDetailsServiceImpl implements DocDetailsService {


    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private LoggedInUserService loggedInUserService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private MvnoDocDetailsRepository docDetailsRepository;

    @Autowired
    private MvnoDocDetailsMapper docDetailsMapper;


    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    @Override
    public List<MvnoDocDetailsDTO> uploadDocument(List<MvnoDocDetailsDTO> mvnoDocDetailsList, Long mvnoId, MultipartFile[] files) {

        String SUBMODULE = "[DocDetailsServiceImpl]" + " [uploadDocument()] ";
        // PATH="D:/";
        String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.MVNO_DOC_PATH).get(0).getValue();
        List<MvnoDocDetailsDTO> finalResponseList = new ArrayList<>();
        Integer loggedInStaffMvnoId = loggedInUserService.getMvnoIdFromCurrentStaff();
        try {
            MvnoDTO mvno = mvnoService.getEntityById(mvnoId);
            for (MvnoDocDetailsDTO mvnoDoc : mvnoDocDetailsList) {
                mvnoDoc.setCreatedById(clientServiceSrv.getLoggedInUserId());
                mvnoDoc.setLastModifiedById(clientServiceSrv.getLoggedInUserId());
                mvnoDoc.setMvnoId(mvno.getId().intValue());
                mvnoDoc.setDocStatus(mvnoDoc.getDocStatus());
                if (null != mvnoDoc.getMvnoId()) {
                    if (mvnoDoc.getStartDate() != null || mvnoDoc.getEndDate() != null) {
                        if (mvnoDoc.getStartDate().isAfter(mvnoDoc.getEndDate())) {
                            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "End date must be greater than start date", null);
                        }
                    }
                    if (null != mvno) {
                        String subFolderName = mvno.getUsername().trim() + "_" + mvno.getId() + File.separator;
                        String path = PATH + subFolderName;
                        ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
                        if (null == mvnoDoc.getDocId()) {
                            if (null != mvnoDoc.getFilename()) {
                                MultipartFile file1 = fileUtility.getFileFromArray(mvnoDoc.getFilename(), files);
                                if (null != file1) {
                                    mvnoDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                    mvnoDoc.setDocumentNumber(mvnoDoc.getDocumentNumber());
                                    MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDoc, new CycleAvoidingMappingContext());
                                    mvnoDocDetails = docDetailsRepository.save(mvnoDocDetails);
                                    mvnoDoc.setDocId(mvnoDocDetails.getDocId());
                                    finalResponseList.add(mvnoDoc);
                                }
                            }
                        } else {
                            MvnoDocDetailsDTO mvnoDocDTO = getEntityById(mvnoDoc.getDocId());
                            if (null != mvnoDocDTO) {
                                if (null != mvnoDocDTO.getFilename()
                                        && null != mvnoDoc.getFilename()
                                        && !mvnoDocDTO.getFilename().equalsIgnoreCase(mvnoDoc.getFilename())) {
                                    fileUtility.removeFileAtServer(mvnoDocDTO.getUniquename(), path);
                                }
                                MultipartFile file1 = fileUtility.getFileFromArray(mvnoDoc.getFilename(), files);
                                if (null != file1) {
                                    mvnoDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                }
                                mvnoDoc.setDocumentNumber(mvnoDoc.getDocumentNumber());
                                mvnoDoc = updateEntity(mvnoDoc);
                                finalResponseList.add(mvnoDoc);
                            } else {
                                throw new DataNotFoundException("Mvno Doc Details Not Found!");
                            }
                        }
                        if (mvnoDoc.getNextTeamHierarchyMappingId() == null) {
                            if (mvnoDoc.getDocStatus() != null && !"".equals(mvnoDoc.getDocStatus())) {
                                if (mvnoDoc.getDocStatus().equalsIgnoreCase("pending")) {

                                        StaffUser currentStaff = staffUserService.get(loggedInUserService.getLoggedInUser().getStaffId());
                                        mvnoDoc.setNextTeamHierarchyMappingId(null);
                                        mvnoDoc.setNextStaff(currentStaff.getId());

                                }
                                MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDoc, new CycleAvoidingMappingContext());
                                mvnoDocDetails.setMvno(mvnoService.getMapper().dtoToDomain(mvno, new CycleAvoidingMappingContext()));
                                docDetailsRepository.save(mvnoDocDetails);
                            }
                        }
                    } else
                        throw new DataNotFoundException("Mvno Not Found!");
                } else
                    throw new RuntimeException("Please Provide Mvno Details");
            }
            return finalResponseList;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
    }

    @Override
    public MvnoDocDetailsDTO updateEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO){
        MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDocDetailsDTO, new CycleAvoidingMappingContext());
        docDetailsRepository.save(mvnoDocDetails);
        return docDetailsMapper.domainToDTO(mvnoDocDetails, new CycleAvoidingMappingContext());
    }
    @Override
    public MvnoDocDetailsDTO getEntityById(Long docId){
        Optional<MvnoDocDetails> mvnoDocDetails = docDetailsRepository.findById(docId);
        if(mvnoDocDetails.isPresent()) {
            return docDetailsMapper.domainToDTO(mvnoDocDetails.get(), new CycleAvoidingMappingContext());
        }
        return null;
    }

    @Override
    public List<MvnoDocDetailsDTO> findDocsByEntityId(Long mvnoId) {
        try {
            List<MvnoDocDetailsDTO> mvnoDocDetailsDTOS = docDetailsRepository.findAllByMvnoIdAndIsDeleteFalse(mvnoId);
            if(!CollectionUtils.isEmpty(mvnoDocDetailsDTOS)) {
                return mvnoDocDetailsDTOS;//docDetailsMapper.domainToDTO(mvnoDocDetailsDTOS, new CycleAvoidingMappingContext());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean isDocPending(Long mvnoId) {
        return false;
    }

    @Override
    public String deleteDocument(List<Long> docIdList, Long mvnoId) {
        return null;
    }

    @Override
    public void pdfGenerate(Document doc) {

    }

    @Override
    public GenericDataDTO getDocApprovals(Long docId, Boolean isApproveRequest, String remarks) {
        try {
            if(loggedInUserService.getLoggedInUser().getMvnoId() != 1) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Only SuperAdmin staff can approve/reject Mvno Documents..!!",null);
            }
            Optional<MvnoDocDetails> mvnoDocDetails = docDetailsRepository.findById(docId);
            if(!mvnoDocDetails.isPresent()) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Given Document Not Available..!!",null);
            }
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            String approvedByName = "Administrator";
            if (isApproveRequest) {
                mvnoDocDetails.get().setDocStatus(SubscriberConstants.VERIFIED);
                Mvno mvno = mvnoRepository.getOne(mvnoDocDetails.get().getMvno().getId());
                mvno.setStatus(SubscriberConstants.ACTIVE);
                mvnoRepository.save(mvno);
                MvnoMessage mvnoMessage = new MvnoMessage(mvno.getId(), mvno.getName(), mvno.getUsername(), mvno.getPassword(), mvno.getSuffix(), mvno.getDescription(),
                        mvno.getEmail(), mvno.getPhone(), mvno.getStatus(), mvno.getLogfile(), mvno.getMvnoHeader(), mvno.getMvnoFooter() ,mvno.getAddress(), mvno.getFullName(),false,mvno.getProfileImage(), mvno.getLogo_file_name());
                kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,mvnoMessage.getClass().getSimpleName()));
                genericDataDTO.setResponseMessage("Mvno Doc Approved successfully..!");
            } else {
                mvnoDocDetails.get().setDocStatus(SubscriberConstants.REJECT);
                genericDataDTO.setResponseMessage("Mvno Doc Rejected successfully..!");
            }
            mvnoDocDetails.get().setNextTeamHierarchyMappingId(null);
            mvnoDocDetails.get().setNextStaff(null);
            docDetailsRepository.save(mvnoDocDetails.get());

            MvnoDocDetailsDTO mvnoDocDetailsDTO = docDetailsMapper.domainToDTO(mvnoDocDetails.get(), new CycleAvoidingMappingContext());
           mvnoDocDetailsDTO.setMvnoId(mvnoDocDetails.get().getMvno().getId().intValue());
            mvnoDocDetailsDTO.setDocStatus(SubscriberConstants.VERIFIED);
            //messageSender.send(new MvnoDocDetailsDTO(mvnoDocDetailsDTO), RabbitMqConstants.QUEUE_MVNO_DOC_SAVE_FROM_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(new MvnoDocDetailsDTO(mvnoDocDetailsDTO),new MvnoDocDetailsDTO(mvnoDocDetailsDTO).getClass().getSimpleName()));

            genericDataDTO.setData(mvnoDocDetailsDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), e);
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
}

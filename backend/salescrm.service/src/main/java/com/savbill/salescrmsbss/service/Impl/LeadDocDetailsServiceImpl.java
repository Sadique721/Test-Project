package com.savbill.salescrmsbss.service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.LeadDocDetails;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.exceptions.DataNotFoundException;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.repository.LeadDocDetailsRepository;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.service.AbstractService;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.LeadDocDetailsService;
import com.savbill.salescrmsbss.utils.ClientServiceConstant;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.DocumentConstants;
import com.savbill.salescrmsbss.utils.FileUtility;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;
import com.savbill.salescrmsbss.utils.ValidateCrudTransactionData;

@Service
public class LeadDocDetailsServiceImpl extends AbstractService<LeadDocDetails, Long> implements LeadDocDetailsService{
	
	private final Logger log = LoggerFactory.getLogger(LeadDocDetailsServiceImpl.class);
	
	private String PATH;
	
	@Autowired
	private LeadDocDetailsRepository leadDocDetailsRepository;
	
	@Autowired
	private LeadMasterRepository leadMasterRepository;
	
	@Autowired
    private ClientServiceSrv clientServiceSrv;
	
	@Autowired
    private FileUtility fileUtility;
	
    public String getModuleNameForLog() {
        return "[LeadDocDetailsServiceImpl]";
    }

	@Override
	public void validateRequest(LeadDocDetailsDTO dto, Integer operation) {
		if (dto == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Required object is not set", null);
		}
		if (dto != null && operation.equals(CommonConstants.OPERATION_ADD)) {
			if (dto.getDocId() != null)
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id should not be present in the JSON body.",
						null);
		}
		if (dto != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
				|| operation.equals(CommonConstants.OPERATION_DELETE)) && dto.getDocId() == null) {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Id can not be set to null.", null);
		}
	}

	@Override
	public LeadDocDetails save(LeadDocDetailsDTO leadMasterPojo, MultipartFile files) throws IOException {
		try {
			String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
			    PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.LEAD_DOC_PATH).get(0).getValue();
			    //PATH = "E:\\Users\\savbill\\leaddoc\\";
				LeadDocDetails leadDocDetails = new LeadDocDetails(leadMasterPojo);
				leadDocDetails.setDocStatus(leadMasterPojo.getDocStatus());
				if(leadMasterPojo.getLeadMasterId() != null) {
					Optional<LeadMaster> leadMasterOp = leadMasterRepository.findById(leadMasterPojo.getLeadMasterId());
					if(leadMasterOp.isPresent()) {
						LeadMaster leadMaster = leadMasterOp.get();
						leadDocDetails.setLeadMaster(leadMaster);
						leadDocDetails.setMode(DocumentConstants.OFFLINE);
		                String subFolderName = leadMaster.getId() + "/";
		                String path = PATH +subFolderName;
		                log.debug(SUBMODULE + ":File Path:" + path);
		                if (null == leadDocDetails.getDocId()) {
		                    if (null != leadDocDetails.getFilename()) {
		                        MultipartFile file1 = fileUtility.getFileFromArray(leadDocDetails.getFilename(), files);
		                        if (null != file1) {
		                        	leadDocDetails.setUniquename(fileUtility.saveFileToServer(file1, path));
		                        	leadDocDetails = leadDocDetailsRepository.save(leadDocDetails);
		                        }
		                    }
		                } else {
		                	LeadDocDetails leadDoc = findById(leadDocDetails.getDocId());
		                    if (null != leadDoc) {
		                    	leadDoc.setDocStatus(leadMasterPojo.getDocStatus());
		                    	leadDocDetails.setDocId(leadDoc.getDocId());
		                    	leadDocDetails.setLeadMaster(leadMaster);
		                        if (null != leadDoc.getFilename() && null != leadDocDetails.getFilename()
		                                && !leadDoc.getFilename().equalsIgnoreCase(leadDocDetails.getFilename())) {
		                            fileUtility.removeFileAtServer(leadDoc.getUniquename(), path);
		                        }
		                        if(files != null && !files.isEmpty()) {
			                        MultipartFile file1 = fileUtility.getFileFromArray(leadDocDetails.getFilename(), files);
			                        if (null != file1) {
			                        	leadDocDetails.setUniquename(fileUtility.saveFileToServer(file1, path));
			                        }
		                        }
		                    	leadDocDetails = leadDocDetailsRepository.save(leadDocDetails);
		                    }
		                }
					}else {
						throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Lead Master is required  for document.", null);
					}
				}
				return leadDocDetails;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public LeadDocDetails findById(Long leadMasterId) {
		Optional<LeadDocDetails> leadDocDetailsOp =  leadDocDetailsRepository.findById(leadMasterId);
		if(leadDocDetailsOp.isPresent()) {
			return leadDocDetailsOp.get();
		}else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "Lead doc details is not found.", null);
		}
	}
	
	@Override
	public Page<LeadDocDetails> findAll(PaginationRequestDTO paginationRequestDTO,Long id) {
		PageRequest pageRequest = super.generatePageRequest(paginationRequestDTO.getPage(),
				paginationRequestDTO.getPageSize(), "docId",
				paginationRequestDTO.getSortOrder());
		LeadDocDetails leadDocDetails = new LeadDocDetails();
		leadDocDetails.setIsDelete(false);
		LeadMaster leadMasterObj = null;
		Optional<LeadMaster> leadMasterOp = leadMasterRepository.findById(id);
		if(leadMasterOp.isPresent())
			leadMasterObj = leadMasterOp.get();
		leadDocDetails.setLeadMaster(leadMasterOp.get()); 
		Page<LeadDocDetails> pageList = this.leadDocDetailsRepository.findAll(Example.of(leadDocDetails), pageRequest);
		List<LeadDocDetails> list = pageList.getContent();
		if(list != null && list.size() > 0 && leadMasterObj != null) {
			for (LeadDocDetails leadDocDetails2 : list) {
	            if(leadDocDetails2.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || leadDocDetails2.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
	            	leadDocDetails2.setDocumentNumber(leadDocDetails2.getDocumentNumber());
	            else if(leadDocDetails2.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
	            	leadDocDetails2.setDocumentNumber(leadDocDetails2.getDocumentNumber());
	            else if(leadDocDetails2.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
	            	leadDocDetails2.setDocumentNumber(leadDocDetails2.getDocumentNumber());
			}
		}
		return this.leadDocDetailsRepository.findAll(Example.of(leadDocDetails), pageRequest);
	}

	@Override
	public void deleteLeadDocDetails(Long leadDocId) {
		LeadDocDetails leadDocDetails = this.leadDocDetailsRepository.findById(leadDocId).get();
		if (Objects.nonNull(leadDocDetails)) {
			leadDocDetails.setIsDelete(true);					
			this.leadDocDetailsRepository.save(leadDocDetails);
			log.info("LeadDocDetails has been deleted successfully");
		}
	}
	
	@Transactional
    public LeadDocDetails uploadDocumentOnline(LeadDocDetailsDTO customerDocDetails, Boolean isUpdate) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [uploadDocumentOnline()] ";
        LeadDocDetails saveEntity = null;
        try {
            if (null != customerDocDetails.getLeadMasterId()) {
        		LeadMaster leadMaster = leadMasterRepository.findById(customerDocDetails.getLeadMasterId()).get();
//				if(customerDocDetails.getDocumentNumber() != null){
//					List<LeadDocDetails> existingDoc = leadDocDetailsRepository
//							.findByDocumentNumberAndDocSubTypeAndIsDeleteFalse(customerDocDetails.getDocumentNumber(), customerDocDetails.getDocSubType());
//
//					boolean isDuplicate = existingDoc.stream().anyMatch(doc ->
//							customerDocDetails.getDocId() == null || !doc.getDocId().equals(customerDocDetails.getDocId())
//					);
//					if (isDuplicate) {
//						throw new RuntimeException(customerDocDetails.getDocSubType() + " already exists! Please delete or update the same entity");
//					}
//				}

                if (null != leadMaster) {
                    if(customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {
						if (String.valueOf(customerDocDetails.getDocumentNumber()).length() != 12) {
							throw new RuntimeException("Adhar card number should be 12 digit long");
						} else {
							if (customerDocDetails.getDocumentNumber() != null && leadMaster.getPan() != null) {
								if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(leadMaster.getAadhar())) {
									if (leadMaster.getAadhar().equalsIgnoreCase(customerDocDetails.getDocumentNumber()))
											throw new RuntimeException("Adhar Number already exists! Please delete or update the same entity");
								}
								leadMaster.setAadhar(updatedAadhar(leadMaster.getId(), customerDocDetails.getDocumentNumber()));
							}
						}
					}
                    else if(customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) {
						if (String.valueOf(customerDocDetails.getDocumentNumber()).length() != 10) {
							throw new RuntimeException("Pan card number should be 10 digit long");
						} else {
							if (customerDocDetails.getDocumentNumber() != null && leadMaster.getPan() != null) {
								if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(leadMaster.getPan())) {
									if (leadMaster.getPan().equalsIgnoreCase(customerDocDetails.getDocumentNumber()))
											throw new RuntimeException("PAN Number already exists! Please delete or update the same entity");
								}
								leadMaster.setPan(updatedPan(leadMaster.getId(), customerDocDetails.getDocumentNumber()));
							}
						}
					}
                    else if(customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER)) {
						if (String.valueOf(customerDocDetails.getDocumentNumber()).length() < 3 ||  String.valueOf(customerDocDetails.getDocumentNumber()).length() > 15 ){
							throw new RuntimeException("GST number Should be between 3 to 15 digits long");
						}else {
							if(customerDocDetails.getDocumentNumber() != null && leadMaster.getGst() != null) {
								if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(leadMaster.getGst())) {
									if (leadMaster.getGst().equalsIgnoreCase(customerDocDetails.getDocumentNumber()))
											throw new RuntimeException("GST Number already exists! Please delete or update the same entity");
								}
								leadMaster.setGst(updatedGst(leadMaster.getId(), customerDocDetails.getDocumentNumber()));
							}
//							if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(leadMaster.getGst()))
//								throw new RuntimeException("GST Number already exists! Please delete or update the same entity");
//							leadMaster.setGst(updatedGst(leadMaster.getId(), customerDocDetails.getDocumentNumber()));
						}
					}
                    leadMasterRepository.save(leadMaster);
                    customerDocDetails.setFilename(null);
                    customerDocDetails.setMode(DocumentConstants.ONLINE);
                    if(!ValidateCrudTransactionData.validateStringTypeFieldValue(customerDocDetails.getRemark()))
                        customerDocDetails.setRemark(null);
                    if(!ValidateCrudTransactionData.validateStringTypeFieldValue(customerDocDetails.getUniquename()))
                        customerDocDetails.setUniquename(null);

                    LeadDocDetails saveObj = new LeadDocDetails(customerDocDetails);
                    saveObj.setDocStatus(customerDocDetails.getDocStatus());
                    saveObj.setLeadMaster(leadMaster);
					saveObj.setDocumentNumber(customerDocDetails.getDocumentNumber());
                    if (null == customerDocDetails.getDocId())
                        saveEntity = leadDocDetailsRepository.save(saveObj);
                    else
                        saveEntity = leadDocDetailsRepository.save(saveObj);
                    saveEntity.setDocumentNumber(customerDocDetails.getDocumentNumber());
                } else
                    throw new DataNotFoundException("LeadMaster Not Found!");
            } else
                throw new RuntimeException("Please Provide LeadMaster");
            return saveEntity;
        } catch (Exception ex) {
            log.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
	
	@Override
	public List<LeadDocDetails> findDocsByLeadId(Long leadId) {
        String SUBMODULE = getModuleNameForLog() + " [findDocsByLeadId()] ";
        List<LeadDocDetails> docList = new ArrayList<LeadDocDetails>();
        try {
    		LeadMaster leadMaster = leadMasterRepository.findById(leadId).get();
    		if(leadMaster != null) {
    			docList = leadDocDetailsRepository.findAllByLeadMasterAndIsDeleteIsFalse(leadMaster);
	            if(docList != null && docList.size() > 0) {
	            	for(LeadDocDetails customerDocDetailsDTO : docList){
		                if(customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
		                    customerDocDetailsDTO.setDocumentNumber(leadMaster.getAadhar());
		                else if(customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
		                    customerDocDetailsDTO.setDocumentNumber(leadMaster.getPan());
		                else if(customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
		                    customerDocDetailsDTO.setDocumentNumber(leadMaster.getGst());
		            }
	    	        return docList;
	            }
    		}else {
                throw new DataNotFoundException("LeadMaster Not Found!");
    		}
        } catch (Exception ex) {
            log.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
		return docList;
    }
	
	public boolean isLeadDocPending(Long leadid) {
    	String SUBMODULE = getModuleNameForLog() + " [isLeadDocPending()] ";
    	boolean isCustDocStatusPending = false ;
        try {
    		LeadMaster leadMaster = leadMasterRepository.findById(leadid).get();
            List<LeadDocDetails> docList = leadDocDetailsRepository.findAllByLeadMasterAndIsDeleteIsFalse(leadMaster);
            if (null != docList && 0 < docList.size()) {
            	List<LeadDocDetails> docListTemp = docList.stream().filter(doc -> doc.getDocStatus().equalsIgnoreCase("pending")).collect(Collectors.toList());
                if (null != docListTemp && 0 < docListTemp.size()) {
                	isCustDocStatusPending =  true;
                }else {
                	isCustDocStatusPending = false;
                }
            }else {
            	isCustDocStatusPending =  false;
            }
        } catch (Exception ex) {
        	log.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
		return isCustDocStatusPending;
    }
	
	public String deleteDocument(List<Long> docIdList, Long leadMasterId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.LEAD_DOC_PATH).get(0).getValue();
        try {
    		LeadMaster leadMaster = leadMasterRepository.findById(leadMasterId).get();
            if (null != leadMaster) {
                String subFolderName = leadMaster.getUsername().trim() + "/";
                String path = PATH + subFolderName;
                for (Long id : docIdList) {
                	LeadDocDetails db = leadDocDetailsRepository.findById(leadMasterId).get();
                    if (null != db) {
                        fileUtility.removeFileAtServer(db.getUniquename(), path);
                        this.deleteEntity(db,leadMasterId);
                    } else throw new DataNotFoundException("Document Not Found with id = " + id);
                }
                return "Delete successfully";
            } else throw new DataNotFoundException("LeadMaster Not Found!");
        } catch (Exception ex) {
        	log.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }
	
    public void deleteEntity(LeadDocDetails entity,Long leadId) throws Exception {
        try{
    		LeadMaster leadMaster = leadMasterRepository.findById(leadId).get();
            if(entity.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || entity.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
            	leadMaster.setAadhar(null);
            else if(entity.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
            	leadMaster.setPan(null);
            else if(entity.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
            	leadMaster.setGst(null);
                entity.setIsDelete(true);
                leadDocDetailsRepository.save(entity);
        }
        catch (Exception ex){
            throw ex;
        }
    }
	
    @Override
	public LeadDocDetails approveLeadDocDetails(Long docId, String status) {
		LeadDocDetails doc = leadDocDetailsRepository.findById(docId).get();
		doc.setDocStatus(status);
		return leadDocDetailsRepository.save(doc);
	}
	
//	private String getMaskedDocuments(String documentType, String documentNumber){
//        if(documentType.equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || documentType.equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
//            return DocumentConstants.AADHAR_STAR_PATTERN + documentNumber.substring(8);
//        if(documentType.equalsIgnoreCase(DocumentConstants.PAN_CARD))
//            return DocumentConstants.PAN_STAR_PATTERN + documentNumber.substring(6);
//        if(documentType.equalsIgnoreCase(DocumentConstants.GST_NUMBER))
//            return DocumentConstants.GST_STAR_PATTERN + documentNumber.substring(10);
//        return "";
//    }
	
	private String updatedAadhar(Long leadMasterId, String aadharNumber){
		LeadMaster leadMaster = leadMasterRepository.findById(leadMasterId).get();
        if(!aadharNumber.contains("*"))
            return aadharNumber;
        return leadMaster.getAadhar();
    }

    private String updatedPan(Long leadMasterId, String panNumber){
		LeadMaster leadMaster = leadMasterRepository.findById(leadMasterId).get();
        if(!panNumber.contains("*"))
            return panNumber;
        return leadMaster.getPan();
    }

    private String updatedGst(Long leadMasterId, String gstNumber){
		LeadMaster leadMaster = leadMasterRepository.findById(leadMasterId).get();
        if(!gstNumber.contains("*"))
            return gstNumber;
        return leadMaster.getGst();
    }
}

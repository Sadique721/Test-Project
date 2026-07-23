package com.savbill.salescrmsbss.service.Impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.entity.QuotationPODoc;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.repository.QuotationDetailsRepository;
import com.savbill.salescrmsbss.repository.QuotationPODocRepository;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.QuotationPODocService;
import com.savbill.salescrmsbss.utils.ClientServiceConstant;
import com.savbill.salescrmsbss.utils.FileUtility;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;

@Service
public class QuotationPODocServiceImpl implements QuotationPODocService{
	
	private final Logger log = LoggerFactory.getLogger(LeadQuotationServiceImpl.class);

	private String PATH;
	
	@Autowired
	private QuotationPODocRepository quotationPODocRepository;

	@Autowired
	private QuotationDetailsRepository quotationDetailsRepository;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private FileUtility fileUtility;
	
	 public String getModuleNameForLog() {
	        return "[QuotationPODocServiceImpl]";
	    }

	@Override
	public QuotationPODoc save(Long quotationId, String poNumber,MultipartFile files) throws IOException {
		String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
	    PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.QUOTATION_PO_DOC_PATH).get(0).getValue();
	    if(quotationId != null) {
	    	QuotationPODoc quotationPODoc = new QuotationPODoc();
	    	Optional<QuotationDetails> op = quotationDetailsRepository.findById(quotationId);
	    	if(op.isPresent()) {
	    		String subFolderName = op.get().getId() + "/";
                String path = PATH +subFolderName;
                log.debug(SUBMODULE + ":File Path:" + path);
                if(files != null && !files.isEmpty()) {
                	quotationPODoc.setQuotationDetailId(quotationId);
                	quotationPODoc.setPoNumber(poNumber);
                	quotationPODoc.setStatus("Active");
                	quotationPODoc.setUniquename(fileUtility.saveFileToServer(files, path));
                	return quotationPODocRepository.save(quotationPODoc);              
                }else {
        			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "File is required for upload quotation po.", null);
                }
	    	}else {
				throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "quotation detail is not found with id."+quotationId, null);
	    	}
	    }else {
			throw new CustomValidationException(SalesCrmsConstants.INTERNAL_SERVER_ERROR, "quotationId is required for upload quotation po.", null);
	    }
	}

	@Override
	public List<QuotationPODoc> findDocsByQuotationId(Long quotationId) {
		return quotationPODocRepository.findAllByQuotationDetailId(quotationId);
	}

}

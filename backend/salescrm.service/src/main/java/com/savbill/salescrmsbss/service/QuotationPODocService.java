package com.savbill.salescrmsbss.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.QuotationPODoc;

public interface QuotationPODocService {

	QuotationPODoc save(Long quotationId,String poNumber, MultipartFile files) throws IOException;
				
		
	List<QuotationPODoc> findDocsByQuotationId(Long quotationId);
}

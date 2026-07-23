package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.model.postpaid.DebitDocumentTAXRel;
import com.savbill.cpm.repository.postpaid.DebitDocumentTAXRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebitDocumentTAXRelService {

    @Autowired
    DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    public List<DebitDocumentTAXRel> getTotalTaxByType(Integer debitDocumentID) {
        return debitDocumentTAXRelRepository.getTotalTaxByType(debitDocumentID);
    }
}

package com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.service;

import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.moules.SettingsManagement.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.itextpdf.text.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface DocDetailsService {

    List<MvnoDocDetailsDTO> uploadDocument(List<MvnoDocDetailsDTO> mvnoDocDetailsList, Long mvnoId, MultipartFile[] files);

    List<MvnoDocDetailsDTO> findDocsByEntityId(Long mvnoId);

    boolean isDocPending(Long mvnoId);

    String deleteDocument(List<Long> docIdList, Long mvnoId);

    void pdfGenerate(Document doc);

    GenericDataDTO getDocApprovals(Long docId, Boolean isApproveRequest, String remarks);

    MvnoDocDetailsDTO updateEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO);

    MvnoDocDetailsDTO getEntityById(Long docId);
}

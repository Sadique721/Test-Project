package com.savbill.taskmanagement.core.modules.MailDocument.service;

import com.savbill.taskmanagement.core.constants.ClientServiceConstant;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.MailDocument.domain.MailDocument;
import com.savbill.taskmanagement.core.modules.MailDocument.repository.MailDocumentRepository;
import com.savbill.taskmanagement.core.service.FileSystemService;
import com.savbill.taskmanagement.core.utillity.fileUtillity.FileUtility;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MailDocumentService {

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private MailDocumentRepository mailDocumentRepository;

    @Autowired
    private FileSystemService fileSystemService;


    public void saveMultipartfileinMailDocument(List<MultipartFile> files , String mailId) throws IOException {
        if (files != null && files.size() > 0) {
            for (MultipartFile multipartFile : files) {
                String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAIL_DOC_PATH).get(0).getValue();
                String path = PATH;
                MailDocument mailDocument = new MailDocument();
                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(multipartFile);
                if (file1 != null) {
                    mailDocument.setUniquename(fileUtility.saveFileToServerForTicket(file1, path)); /**file actually save here**/
                    mailDocument.setFilename(file1.getOriginalFilename());
                    mailDocument.setMailId(mailId);
                    mailDocument.setDocStatus("Active");
                    mailDocumentRepository.save(mailDocument);
                }
            }


        }

    }

    public List<MultipartFile> getMultipartfilelistfromMessageId(String messageId) throws IOException {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        List<MailDocument> mailDocumentList = mailDocumentRepository.findAllByMailId(messageId);
        for(MailDocument maildocument: mailDocumentList){
          Resource resource =  fileSystemService.getMailDoc(maildocument.getUniquename());
          File file = resource.getFile();
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "text/plain", IOUtils.toByteArray(input));
            multipartFileList.add(multipartFile);
        }
        return multipartFileList;
    }

}

package com.savbill.commonGateway.common.service;

import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class FileSystemService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    private Path mvnoDocDir;

    private Path barterDocDir;

    private Path itemDocDir;
    private Path mvnoDocPath;

    private Path barterDocPath;


    @Autowired
    ClientServiceSrv clientServiceSrv;



    public Resource getMvnoDoc(MvnoDTO mvno, String file) {
        ApplicationLogger.logger.info("In getMvnoDoc");
        String path = clientServiceSrv.getClientSrvByName(ClientServiceConstant.MVNO_DOC_PATH_READ).get(0).getValue();
        List<String> paths = Arrays.asList(path.split(","));
        Resource resource = null;
        try {
            for (String pathString : paths) {
                mvnoDocDir = Paths.get(pathString.trim());
                String subFolderName = mvno.getUsername().trim() + "_" + mvno.getId() + File.separator;
//                + "_" + mvno.getId() + File.separator
//                String subFolderName = mvnoDocDir + File.separator + mvno.getUsername().trim() + "_" + mvno.getId() + File.separator;
                                                                    //mvno.getUsername().trim() + "_" + mvno.getId() + File.separator;
                this.mvnoDocPath = Paths.get(mvnoDocDir+subFolderName);

                Path filePath = this.mvnoDocPath.resolve(file).normalize();
                ApplicationLogger.logger.info("MvnoDoc PATH:" + filePath.toString());
                resource = new UrlResource(filePath.toUri());
                if (resource.exists()){
                    return resource;
                }
            }
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }

    public Resource getBarterDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getCustDoc");
        barterDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = barterDocDir + "/" +  userName.trim() + "/";
            this.barterDocPath = Paths.get(subFolderName);

            Path filePath = this.barterDocPath.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            String fileUrl = filePath.toUri().toString();
            resource = new UrlResource(new URI(fileUrl));
//            resource = new UrlResource(filePath.toUri());
            if (resource==null) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return resource;
    }

    public Resource getItemDoc(String userName, String file) {
        ApplicationLogger.logger.info("In getItemDoc");
        itemDocDir = Paths.get(clientServiceSrv.getClientSrvByName(ClientServiceConstant.ITEM_COMPLAIN).get(0).getValue());
        Resource resource = null;
        try {
            String subFolderName = itemDocDir + userName.trim() + "/";
            this.itemDocDir = Paths.get(subFolderName);

            Path filePath = this.itemDocDir.resolve(file).normalize();
            ApplicationLogger.logger.info("CustDoc PATH:" + filePath.toString());
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                ApplicationLogger.logger.info("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }





}

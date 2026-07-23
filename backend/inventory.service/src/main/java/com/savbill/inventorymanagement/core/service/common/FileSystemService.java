package com.savbill.inventorymanagement.core.service.common;


import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemService {

    private Path itemDocDir;

    @Autowired
    ClientServiceService clientServiceSrv;

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

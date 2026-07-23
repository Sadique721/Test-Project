package com.savbill.radius.utils;

import com.savbill.radius.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUtility {

    private static final String MODULE = " [File Utility ] ";
    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);

    public String saveFileToServer(MultipartFile argFile, String path,Integer mvnoId) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

        int allowedFileSize = clientService.getByName(RadiusConstants.ALLOWED_DOCUMENT_SIZE,mvnoId) != null ? Integer.parseInt(clientService.getByName(RadiusConstants.ALLOWED_DOCUMENT_SIZE,mvnoId).getValue()) : 10;
        if (argFile.getSize() > ((long) allowedFileSize * 1024 * 1024))
            throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");

        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);
        String filePath = path + System.currentTimeMillis() + "_" + fileName;
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new RuntimeException("Failed to save file: ");
            }
            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (IOException e) {
//            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw new RuntimeException("Failed to save file: " + e.getMessage());        }
    }


    public boolean removeFileAtServer(String argFile, String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path + argFile);
            if (null == file) {
//                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public boolean removeFileAtServer(String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path);
            if (null == file) {
//                ApplicationLogger.logger.debug(SUBMODULE + "File not found with name" + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public MultipartFile getFileFromArray(String fileName, MultipartFile[] files,Integer mvnoId) {
        boolean isAvailable = false;
        try {
            Integer allowedFileSize = clientService.getByName(RadiusConstants.ALLOWED_DOCUMENT_SIZE,mvnoId) != null ? Integer.parseInt(clientService.getByName(RadiusConstants.ALLOWED_DOCUMENT_SIZE,mvnoId).getValue()) : 2;
            for (MultipartFile file : files) {
                if (file.getSize() > (allowedFileSize * 1024 * 1024))
                    throw new RuntimeException("File size limit exceeds. Please provide document within " + allowedFileSize + "MB");
                if (fileName.equalsIgnoreCase(file.getOriginalFilename())) {
                    isAvailable = true;
                    return file;
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }


}

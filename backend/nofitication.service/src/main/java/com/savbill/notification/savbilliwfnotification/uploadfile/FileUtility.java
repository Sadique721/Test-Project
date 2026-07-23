package com.savbill.notification.savbilliwfnotification.uploadfile;

import com.savbill.notification.kafka.KafkaMessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUtility {

    private static final String MODULE = " [File Utility ] ";

    //private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageReceiver.class);

    public String saveFileToServer(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";

        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new RuntimeException("File not created");
            }
            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String saveFileToServerForTicket(MultipartFile argFile, String path) throws IOException {
        String SUBMODULE = MODULE + " [saveFileToServerForTicket()] ";
        Assert.notNull(path, "Path should not be empty");
        String fileName = "Test";
        if (null != argFile) {
            fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim() : fileName;
        }
//        path="D:\\";
        File file = new File(path + System.currentTimeMillis() + "_" + fileName);

        log.info("===================== Absolute Path :-" + file.getAbsolutePath() + " File Details : -  " + file.toString() + "=====================");
        File directory = new File(path);
        try {
            if (!directory.exists()) {
                directory.mkdir();
            }
            log.info("=====================Directory Path :- " + directory.getPath() + "=====================");
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new RuntimeException("File not created");
            }

            if (null != argFile) {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(argFile.getBytes());
                fout.close();
            }
            return file.getName();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public boolean removeFileAtServer(String argFile, String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path + argFile);
            if (null == file) {
                throw new RuntimeException(SUBMODULE + "File not found with name " + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public boolean removeFileAtServer(String path) {
        boolean isFileDeleted = false;
        String SUBMODULE = MODULE + " [saveFileToServer()] ";
        Assert.notNull(path, "Path should not be empty");
        try {
            File file = new File(path);
            if (null == file) {
                throw new RuntimeException(SUBMODULE + "File not found with name " + file.getName());
            }
            if (null != file && file.delete()) {
                isFileDeleted = true;
            }
            return isFileDeleted;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public MultipartFile getFileFromArray(String fileName, MultipartFile files) {
        boolean isAvailable = false;
        try {
                if (fileName.equalsIgnoreCase(files.getOriginalFilename())) {
                    isAvailable = true;
                    return files;
                }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

}

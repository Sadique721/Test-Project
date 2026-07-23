package com.savbill.salescrmsbss.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.service.ClientServiceSrv;

@Component
public class FileUtility {

	private final Logger log = LoggerFactory.getLogger(FileUtility.class);

	private static final String MODULE = " [File Utility ] ";

	private Path leadDocPath;

	private Path leadDocDir;
	
	private Path poDocPath;

	private Path poDocDir;

	@Autowired
	private ClientServiceSrv clientService;

	public String saveFileToServer(MultipartFile argFile, String path) throws IOException {
		String SUBMODULE = MODULE + " [saveFileToServer()] ";
		Assert.notNull(path, "Path should not be empty");
		String fileName = "Test";

		Integer allowedFileSize = clientService.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE, getLoggedInMvnoId().longValue()) != null
				? Integer.parseInt(clientService.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE,getLoggedInMvnoId().longValue()).getValue())
				: 2;
		if (argFile.getSize() > (allowedFileSize * 1024 * 1024))
			throw new RuntimeException(
					"File size limit exceeds. Please provide document within " + allowedFileSize + "MB");

		if (null != argFile) {
			fileName = (null != argFile.getOriginalFilename()) ? argFile.getOriginalFilename().replace("/", "_").trim()
					: fileName;
		}
		File file = new File(path + System.currentTimeMillis() + "_" + fileName);
		File directory = new File(path);
		try {
			if (!directory.exists()) {
				directory.mkdir();
			}
			boolean isCreated = file.createNewFile();
			if (!isCreated) {
				throw new FileNotCreatedException();
			}
			if (null != argFile) {
				FileOutputStream fout = new FileOutputStream(file);
				fout.write(argFile.getBytes());
				fout.close();
			}
			return file.getName();
		} catch (IOException e) {
			log.error(SUBMODULE + e.getMessage(), e);
			throw new FileNotCreatedException();
		}
	}

	public boolean removeFileAtServer(String argFile, String path) {
		boolean isFileDeleted = false;
		String SUBMODULE = MODULE + " [saveFileToServer()] ";
		Assert.notNull(path, "Path should not be empty");
		try {
			File file = new File(path + argFile);

			if (null != file && file.delete()) {
				isFileDeleted = true;
			} else {
				log.debug(SUBMODULE + "File not found with name" + file.getName());
			}
			return isFileDeleted;
		} catch (Exception ex) {
			log.error(SUBMODULE + ex.getMessage(), ex);
			throw ex;
		}
	}

	public MultipartFile getFileFromArray(String fileName, MultipartFile file) {
		try {
			Integer allowedFileSize = clientService.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE, getLoggedInMvnoId().longValue()) != null
					? Integer.parseInt(clientService.getByNameAndMvnoId(DocumentConstants.ALLOWED_DOCUMENT_SIZE, getLoggedInMvnoId().longValue()).getValue())
					: 2;
			if (file.getSize() > (allowedFileSize * 1024 * 1024))
				throw new RuntimeException(
						"File size limit exceeds. Please provide document within " + allowedFileSize + "MB");
			if (fileName.equalsIgnoreCase(file.getOriginalFilename())) {
				return file;
			}

		} catch (Exception ex) {
			throw ex;
		}
		return null;
	}

	public Resource getLeadDoc(Long id, String file) {
		log.info("In getLeadDoc");
		leadDocDir = Paths.get(clientService.getClientSrvByName(ClientServiceConstant.LEAD_DOC_PATH).get(0).getValue());
		// leadDocDir = Paths.get("E:\\Users\\savbill\\leaddoc\\");
		Resource resource = null;
		try {
			String subFolderName = leadDocDir + "/" + id + "/";
			this.leadDocPath = Paths.get(subFolderName);

			Path filePath = this.leadDocPath.resolve(file).normalize();
			log.info("leadDoc PATH:" + filePath.toString());
			resource = new UrlResource(filePath.toUri());
			if (!resource.exists()) {
				log.info("File not found " + file);
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			resource = null;
		}
		return resource;
	}
	
	public Resource getPoDoc(Long id, String file) {
		log.info("In getLeadDoc");
		poDocDir = Paths.get(clientService.getClientSrvByName(ClientServiceConstant.QUOTATION_PO_DOC_PATH).get(0).getValue());
		//poDocDir = Paths.get("E:\\Users\\savbill\\leaddoc\\");
		Resource resource = null;
		try {
			String subFolderName = poDocDir + "/" + id + "/";
			this.poDocPath = Paths.get(subFolderName);

			Path filePath = this.poDocPath.resolve(file).normalize();
			log.info("poDoc PATH:" + filePath.toString());
			resource = new UrlResource(filePath.toUri());
			if (!resource.exists()) {
				log.info("File not found " + file);
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			resource = null;
		}
		return resource;
	}

	public Integer getLoggedInMvnoId() {
		int loggedInMvnoId = -1;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
			}
		} catch (Exception e) {
			loggedInMvnoId = -1;
		}
		return loggedInMvnoId;
	}

}

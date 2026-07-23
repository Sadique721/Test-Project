package com.savbill.cpm.controller.api;

import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.repository.common.ClientServiceRepository;
import com.savbill.cpm.spring.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class MigrationController extends ApiBaseController {

    @Autowired
    ClientServiceRepository clientServiceRepository;

    private static String MODULE = " [MigrationController] ";

    private static String MIGRATION_FILE_LOCATION = "MIGRATION_FILE_LOCATION";
    private final Logger log = LoggerFactory.getLogger(APIController.class);


    //	private static final String OTP = "otp";
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId(MIGRATION_FILE_LOCATION, getLoggedInUser().getMvnoId());
            if (clientService != null) {
                Path file = Paths.get(clientService.getValue()).resolve(filename).normalize();
                Resource resource = new UrlResource(file.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
        return  null;
    }


    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

}

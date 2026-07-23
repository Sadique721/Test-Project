package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The type Async service.
 */
@Service
public class AsyncService {

    /**
     * The Upload bulk management service.
     */
    @Autowired
    private BulkUploadServiceArea bulkUploadServiceArea;

    @Async
    public void doAsync(Workbook workbook, Integer mvnoId, Integer loggedInUserId, String loggedInUserName) throws Exception {
        bulkUploadServiceArea.saveDataToSourceMasterInBulk(workbook, mvnoId, loggedInUserId, loggedInUserName);
    }
}

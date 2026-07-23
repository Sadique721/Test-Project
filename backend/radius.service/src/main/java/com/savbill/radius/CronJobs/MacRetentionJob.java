package com.savbill.radius.CronJobs;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.SchedularAudit;
import com.savbill.radius.kafka.message.CustMacMessage;
import com.savbill.radius.repository.ClientRepository;
import com.savbill.radius.repository.FaultyMACKRepocitory;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.repository.MacAddressMappingRepository;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.services.SchedularAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MacRetentionJob {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Autowired
    private LiveUserService liverUserService;

    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;

    @Autowired
    private FaultyMACKRepocitory faultyMACKRepocitory;

    @Autowired
    private SchedularAuditService schedularAuditService;
    private Integer counter = 0;

    private static final Logger log = LoggerFactory.getLogger(MacRetentionJob.class);

    @Scheduled(cron = "${session.mac.retention.schedule}")
    public void cronJobForMacRetention() {
        SchedularAudit schedularAudit  = new SchedularAudit();
        try {
            schedularAudit.setStartTime(LocalDateTime.now());
            schedularAudit.setSchedularName(AAAConstant.SCHEDULAR_MAC_RETENTION_NAME);
            log.info(String.format("Cron job run for Mac Retention started  at: %s no of times %s", LocalDateTime.now(), ++counter));
            List<MacAddressMapping> macAddressMappingList = macAddressMappingRepository.findMappingsNotInLiveUserWithPastRetentionDate();
            List<String> macList = macAddressMappingList.stream().map(MacAddressMapping::getMacAddress).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(macAddressMappingList)) {
                log.info("Number of Mac found for Retention: " + macAddressMappingList.size());
                try {
                    CustMacMessage message = new CustMacMessage();
                    message.setMacAddressMappings(macAddressMappingList);
                    message.setBulkDelete(true);
                    RadiusUtility radiusUtility = new RadiusUtility();
                    radiusUtility.SendCustMacInfo(message);
                } catch (Exception e) {
                    log.error("Exception to send Deleted MAC data to CMS: " + e.getMessage());
                }
//                macAddressMappingRepository.deleteInBatch(macAddressMappingList);
//                macAddressMappingRepository.deleteAll(macAddressMappingList);
                log.info(String.format("start Time take for delete: %s no of times %s", LocalDateTime.now(), ++counter));
                List<Long> custIdList = new ArrayList<>();
                List<String> macIdList = new ArrayList<>();
                for (MacAddressMapping macAddressMapping : macAddressMappingList) {
                    custIdList.add(macAddressMapping.getCustomerId());
                    macIdList.add(macAddressMapping.getMacAddress());
                }

                try {
                    macAddressMappingRepository.deleteByCustomerIdAndMacIn(custIdList, macIdList);
                } catch (Exception ex) {
                    log.error("Error while deleting MAC addresses from Radius: " + ex.getMessage());
                }
                log.info(String.format("end Time take for delete: %s no of times %s", LocalDateTime.now(), ++counter));
                faultyMACKRepocitory.deleteByMacIn(macList);

            } else {
                log.debug("There is no MAC for Retention..!");
            }
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setTotalCount(macAddressMappingList.size());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_SUCCESS);
            schedularAudit.setDescription("Mac Retention Done Successfully");
            log.debug(String.format("Cron job run for Mac Retention completed at: %s no of times %s", LocalDateTime.now(), counter));
        }
        catch (Exception e){
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_FAILURE);
            schedularAudit.setDescription(e.getMessage());
        }
        finally {
            schedularAuditService.saveEntity(schedularAudit);
        }
    }
}

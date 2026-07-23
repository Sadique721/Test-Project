package com.savbill.cpm.service.tacacs;

import com.savbill.cpm.exception.AccessLevelGroupNotFound;
import com.savbill.cpm.model.tacacs.AccessLevelGroupTacacs;
import com.savbill.cpm.rabbitMq.message.ReceiveAccessLevelGroupTacacsMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AccessLevelGroupService {
    Page<AccessLevelGroupTacacs> getAllAccessLevelGroup(int page, int pageSize) throws AccessLevelGroupNotFound;

    AccessLevelGroupTacacs addAccessLevelGroup(ReceiveAccessLevelGroupTacacsMessage receiveAccessLevelGroupTacacsMessage);

    Map<String,Boolean> deleteAccessLevelGroupById(Long accessLvlId) throws AccessLevelGroupNotFound;

    AccessLevelGroupTacacs updateAccessLevelGroup(Long accessLvlId, ReceiveAccessLevelGroupTacacsMessage accessLevelGroupDto) throws AccessLevelGroupNotFound;
}

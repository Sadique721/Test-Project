package com.diameter.service;


import com.diameter.dto.RatePackageGroupDTOMessage;
import com.diameter.model.RatePackageGroup;
import com.diameter.repository.RatePackageGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class RatePackageGroupService {

    @Autowired
    private RatePackageGroupRepository RatePackageGroupRepository;

    @Transactional
    public void saveGroup(RatePackageGroupDTOMessage message) {
        RatePackageGroup group = RatePackageGroup
                .builder()
                .groupId(message.getGroupId())
                .groupName(message.getGroupName())
                .description(message.getDescription())
                .createdDate(message.getCreatedDate())
                .modifiedDate(message.getModifiedDate())
                .isDeleted(false)
                .build();
        RatePackageGroupRepository.save(group);
        log.info("[OCS] RatePackageGroup SAVED | ID: {} | Name: {}",
                group.getGroupId(), group.getGroupName());
    }

    @Transactional
    public void updateGroup(RatePackageGroupDTOMessage message) {
        RatePackageGroup group = RatePackageGroupRepository.findByGroupId((message.getGroupId()));
        group.setGroupId(message.getGroupId());
        group.setGroupName(message.getGroupName());
        group.setDescription(message.getDescription());
        group.setCreatedDate(message.getCreatedDate());
        group.setModifiedDate(message.getModifiedDate());
        group.setIsDeleted(message.getIsDeleted() != null ? message.getIsDeleted() : false);

        RatePackageGroupRepository.save(group);
        log.info("[ocs] RatePackageGroup UPDATED | ID: {} | Name: {}",
                group.getGroupId(), group.getGroupName());
    }

    @Transactional
    public void deleteGroup(RatePackageGroupDTOMessage message) {
        RatePackageGroup group = RatePackageGroupRepository.findByGroupId((message.getGroupId()));
        group.setIsDeleted(true);
        RatePackageGroupRepository.save(group);
        log.info("[ocs] RatePackageGroup DELETED | ID: {}", message.getGroupId());
    }

    public RatePackageGroup
    getGroup(Long groupId) {
        return RatePackageGroupRepository.findById(groupId).orElse(null);
    }

    public Iterable<RatePackageGroup
            > getAllGroups() {
        return RatePackageGroupRepository.findAll();
    }
}

package com.savbill.ticketmanagement.core.modules.Region.service;


import com.savbill.ticketmanagement.core.modules.Region.Mapper.RegionMapper;
import com.savbill.ticketmanagement.core.modules.Region.domain.Region;
import com.savbill.ticketmanagement.core.modules.Region.model.RegionDTO;
import com.savbill.ticketmanagement.core.modules.Region.repository.RegionBranchRepository;
import com.savbill.ticketmanagement.core.modules.Region.repository.RegionRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.SaveRegionSharedDataMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdateRegionSharedDataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RegionService  extends ExBaseAbstractService<RegionDTO, Region, Long> {

    public RegionService(RegionRepository repository, RegionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RegionServiceService]";
    }
    private static Log log = LogFactory.getLog(RegionService.class);

    @Autowired
    RegionRepository repository;

    @Autowired
    RegionBranchRepository regionBranchRepository;

    @Autowired
    RegionMapper regionMapper;

    @Override
    public boolean duplicateVerifyAtSave(String rname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (rname != null) {
            rname = rname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public Region getById(Long id) {
        return repository.findById(id).get();
    }

    public boolean duplicateVerifyAtEdit(String rname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (rname != null) {
            rname = rname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = repository.duplicateVerifyAtEdit(rname, id);
                else countEdit = repository.duplicateVerifyAtEdit(rname, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }



    public void saveRegion(SaveRegionSharedDataMessage message) {
        // Create a new Region object
        try {
            Region region = new Region();

            // Set values from the message
            region.setId(message.getId());
            region.setRname(message.getRname());
            region.setBranchidList(message.getBranchidList());
            region.setStatus(message.getStatus());
            region.setIsDeleted(message.getIsDeleted());
            region.setMvnoId(message.getMvnoId());

            // Save the region using the repository
            repository.save(region);
        }catch (Exception e){
            log.error("Unable to create Region "+e.getMessage());
            e.getMessage();
        }

    }

    public void updateRegion(UpdateRegionSharedDataMessage message) {
        try {
            // Create a new Region object
            Region region = new Region();

            // Find the existing object by id using the repository and assign it to the created object
            Region existingRegion = repository.findById(message.getId()).orElse(null);

            // Set other values except id
            existingRegion.setRname(message.getRname());
            existingRegion.setBranchidList(message.getBranchidList());
            existingRegion.setStatus(message.getStatus());
            existingRegion.setIsDeleted(message.getIsDeleted());
            existingRegion.setMvnoId(message.getMvnoId());

            // Save the region using the repository
            repository.save(existingRegion);
        }catch (Exception e){
            log.error("Unable to Update Region "+e.getMessage());
        }
    }


    public void saveRegionData(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recordInJson = (String) records.value();
        try {
            SaveRegionSharedDataMessage message = objectMapper.readValue(recordInJson , SaveRegionSharedDataMessage.class);
            Region region = new Region();
            region.setId(message.getId());
            region.setRname(message.getRname());
            region.setBranchidList(message.getBranchidList());
            region.setStatus(message.getStatus());
            region.setIsDeleted(message.getIsDeleted());
            region.setMvnoId(message.getMvnoId());
            repository.save(region);

        }catch (Exception e){
            log.error("Error message : "+e.getMessage());
        }
    }

    public void updateRegionData(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recoredInJson = (String) records.value();
        try {
            UpdateRegionSharedDataMessage message = objectMapper.readValue(recoredInJson, UpdateRegionSharedDataMessage.class);
                Region region = repository.findById(message.getId()).orElse(null);
                if(region != null){
                    region.setRname(message.getRname());
                    region.setBranchidList(message.getBranchidList());
                    region.setStatus(message.getStatus());
                    region.setIsDeleted(message.getIsDeleted());
                    region.setMvnoId(message.getMvnoId());

                    // Save the region using the repository
                    repository.save(region);
                }else {
                    log.info("Data not found ");
                }
        }catch (Exception e){
            log.error("Error message : "+e.getMessage());
        }
    }
}


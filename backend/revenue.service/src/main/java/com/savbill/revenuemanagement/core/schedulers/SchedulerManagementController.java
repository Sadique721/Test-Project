package com.savbill.revenuemanagement.core.schedulers;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SCHEDULER_MANAGEMENT)
public class SchedulerManagementController {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerManagementController.class);

    @Autowired
    SchedulerManagementService schedulerManagementService;

    @PostMapping(value = "/save")
    public GenericDataDTO saveSchedulerManagement(@Valid @RequestBody SchedulerManagementDTO schedulerManagementDTO){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            if(Objects.isNull(schedulerManagementDTO)){
                dataDTO.setData(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_OBJECT_IS_EMPTY);
                dataDTO.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SUCCESS);
                return dataDTO;
            }
            schedulerManagementService.save(schedulerManagementDTO);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_SAVE);
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::Operation Perform for Save Scheduler Ended :::::::::");
        }
        return dataDTO;
    }

    @PutMapping(value = "/update/{id}")
    public GenericDataDTO updateSchedulerManagement(@Valid @RequestBody SchedulerManagementDTO schedulerManagementDTO, @PathVariable Long id){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            schedulerManagementService.update(schedulerManagementDTO,id);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_UPDATE);
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::Operation Perform for Update Scheduler Ended :::::::::");
        }
        return dataDTO;
    }

    @DeleteMapping(value = "/delete/{id}")
    public GenericDataDTO deleteSchedulerManagement(@PathVariable Long id){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            schedulerManagementService.delete(id);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_DELETE);
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::Operation Perform for Delete Scheduler Ended :::::::::");
        }
        return dataDTO;
    }

    @GetMapping(value = "/getScheduler/{id}")
    public GenericDataDTO getSchedulerById(@PathVariable Long id){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            SchedulerManagement schedulerManagementDTO = schedulerManagementService.getById(id);
            dataDTO.setData(schedulerManagementDTO);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_FETCH);
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::Operation Perform for Delete Scheduler Ended :::::::::");
        }
        return dataDTO;
    }

    @PostMapping("/search")
    public GenericDataDTO getAllSchedulersWithPagination(@RequestBody PaginationRequestDTO paginationRequestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (Objects.isNull(paginationRequestDTO)) {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_OBJECT_IS_EMPTY);
                genericDataDTO.setTotalRecords(0);
                logger.error(":::::::::::::::SchedulerManagement Details fetch failed — reason: missing Payload for Pagination in the request.::::::::::::::");
                return genericDataDTO;
            }
            Page<SchedulerManagement> schedulerManagements = schedulerManagementService.getAllScedulersWithPagination(paginationRequestDTO);
            List<SchedulerManagement> schedulerManagementList = schedulerManagements.getContent().stream().collect(Collectors.toList());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SCHEDULER_FETCH);
            genericDataDTO.setDataList(schedulerManagementList);
            genericDataDTO.setTotalRecords(schedulerManagements.getTotalElements());
            logger.info(":::::::::::::::::::::SchedulerManagement Details fetch Successfully:::::::::::::::::::::");
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Exception While Fetch Details For KnowledgeBaseDocs.");
            genericDataDTO.setTotalRecords(0);
            logger.error(e.getMessage());
        }
        return genericDataDTO;
    }


}

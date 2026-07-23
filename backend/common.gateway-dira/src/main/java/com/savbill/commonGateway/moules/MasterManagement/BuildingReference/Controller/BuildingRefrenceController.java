package com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Controller;

import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity.BuildingRefrence;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Service.BuildingReferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUILDING_REFRENCE)
public class BuildingRefrenceController {
    @Autowired
    private BuildingReferenceService buildingReferenceService;

    private static final Logger log = LoggerFactory.getLogger(BuildingRefrenceController.class);

    @PostMapping("/save")
    public GenericDataDTO saveBuilding(@RequestBody BuildingRefrence buildingRefrence, HttpServletRequest request,HttpServletResponse res){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            Boolean isValid= buildingReferenceService.isValid(buildingRefrence);
            if(isValid){
                genericDataDTO.setData(buildingReferenceService.createBuilding(buildingRefrence));
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Success");
            }else{
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                genericDataDTO.setResponseMessage("Building Reference Already Exists With name");
                throw new RuntimeException("Building Reference Already Exists With name");
            }

        }catch (Exception e){
            log.error("Building Reference Already Exists With name" + buildingRefrence.getName(), e);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return  genericDataDTO;
    }

    @PutMapping("/update")
    public GenericDataDTO UpdateBuilding(@RequestBody BuildingRefrence buildingRefrence, @RequestParam("id") Long id, HttpServletRequest request,HttpServletResponse res){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            Optional<BuildingRefrence> building1= buildingReferenceService.findById(id);
            if(building1.isPresent()){
                genericDataDTO.setData(buildingReferenceService.updateBuilding(buildingRefrence,id));
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Building Refrence Successfully Updated");
            }else{
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                genericDataDTO.setResponseMessage("Error While Updateing Building Refrence");
                throw new RuntimeException("No Building Refrence Found with id"+id);
            }
        }catch (Exception e){
        e.getMessage();
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return  genericDataDTO;
    }

    @DeleteMapping("/delete")
    public GenericDataDTO deleteBuilding(@RequestParam("id") Long id, HttpServletRequest request,HttpServletResponse res){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            Optional<BuildingRefrence> building1= buildingReferenceService.findById(id);
            if(building1.isPresent()){
                genericDataDTO.setData(buildingReferenceService.deleteBuilding(id));
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Building Refrence Successfully Deleted");
            }else{
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                genericDataDTO.setResponseMessage("Error While Deleting Building Refrence");
                throw new RuntimeException("No Building Found with id"+id);
            }

        }catch (Exception e){
            e.getMessage();
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return  genericDataDTO;
    }
    @GetMapping("/findById/{id}")
    public GenericDataDTO findById(@PathVariable Long id){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try {
                genericDataDTO.setData(buildingReferenceService.findById(id).get());
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Building Refrence Successfully Deleted");

        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage("Error While Deleting Building Refrence");
            e.getMessage();
        }
        return  genericDataDTO;
    }
    @GetMapping("/all")
    public GenericDataDTO fetchBuilding(HttpServletRequest req, HttpServletResponse res){
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try {
                genericDataDTO.setDataList(buildingReferenceService.findAll());
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Building Refrence Successfully Fetched");

        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage("Error While Fetching Building Refrence");
            e.getMessage();
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return  genericDataDTO;
    }
}

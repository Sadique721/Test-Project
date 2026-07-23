package com.savbill.commonGateway.moules.StaffSales.Controller;

import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;

import com.savbill.commonGateway.moules.StaffSales.Service.StaffSalseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL+"/staff-sales")
public class StaffSalseController{


    @Autowired
    private StaffUserRepository staffSalseRepo;

    @Autowired
    private StaffUserService staffUserService;


    @Autowired
    private StaffSalseService staffSalseService;

    @GetMapping("/created-by/{staffId}")
    public List<Map<String, Object>> getSalesByStaff(@PathVariable("staffId")Integer staffId, @RequestParam(required = true,value = "startdate") String startdate,@RequestParam(required = true,value = "enddate")String enddate) {
        try{
            if(staffId!=null){
                return staffSalseService.getPlansCreatedByStaff(staffId,startdate,enddate);
            }else{
                return staffSalseService.getPlansCreatedByStaff(staffUserService.getLoggedInUserId(),startdate,enddate);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

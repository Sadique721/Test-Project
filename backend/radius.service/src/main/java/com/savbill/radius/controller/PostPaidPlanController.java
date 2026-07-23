package com.savbill.radius.controller;

import com.savbill.radius.entity.PostpaidPlan;
import com.savbill.radius.services.impl.PostpaidPlanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(path = "SavbillRadius/postpaidPlan")
public class PostPaidPlanController {
    @Autowired
    PostpaidPlanServiceImpl planService;

    @GetMapping("/findByPlanName")
    public Boolean findByPlanName(@RequestParam( name= "planName") String planName){
        try{
        PostpaidPlan plan=planService.findByPlanName(planName);
            if(Objects.isNull(plan)){
                return false;
            }
        }catch (Exception e){
        e.getMessage();
        }
        return true;
    }
}

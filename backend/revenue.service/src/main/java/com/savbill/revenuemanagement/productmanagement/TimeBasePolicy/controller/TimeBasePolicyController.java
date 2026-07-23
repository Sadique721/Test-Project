package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.controller;


import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.service.TimeBasePolicyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TIME_BASE_POLICY)
public class TimeBasePolicyController  {



    private static final Logger logger = LoggerFactory.getLogger(TimeBasePolicyController.class);
    @Autowired
    TimeBasePolicyService timeBasePolicyService;

    //@Autowired
   // private MessageSender messageSender;





}

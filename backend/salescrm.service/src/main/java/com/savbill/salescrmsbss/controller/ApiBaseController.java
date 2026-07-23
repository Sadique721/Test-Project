package com.savbill.salescrmsbss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiBaseController {
    private static final Logger logger = LoggerFactory.getLogger(ApiBaseController.class);
    private String MODULE = "[ApiBaseController]";


    //	private static final String OTP = "otp";
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;



}

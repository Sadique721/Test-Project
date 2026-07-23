package com.savbill.radius.helper;

import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;

@Data
public class AcctCdrSearchDTO {

    private Integer custId ;

    private String startDate;

    private String endDate;

    private String timeFrame;

    private String searchDate;

    private  Integer page;

    private Integer pageSize;


}

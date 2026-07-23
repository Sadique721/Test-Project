package com.savbill.cpm.modules.FlutterWaveHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FlutterWaveAuthentication {
    /** This Service is for FlutterwaveAuthetication Do not add any other authetication**/

    @Autowired
    private FlutterWaveConstant flutterWaveConstant;

    protected  String getKey(){
        return flutterWaveConstant.getKEY();
    }

    protected String getURL(){
        return flutterWaveConstant.getVERIFY_URL();
    }





}

package com.savbill.radius.services;

import java.text.ParseException;
import java.util.Map;

public interface DashBoardService {
    Map<String,Integer> getDailyConsumeDataOfLastSevenDays(Integer mvnoId) throws ParseException;
    Long connectedUser(Integer mvnoId);
    Map<String,Integer> getAvgSessionTimeByDate(Integer mvnoId) throws ParseException;
    Map<String,Integer> getAuthFailureData(Integer mvnoId) throws ParseException;
    Map<String,Integer> getDailyConsumeDataOfLastSevenDaysForPWSC(Integer mvnoId,Long locationId) throws ParseException;
    Long connectedUserForPWSC(Integer mvnoId,Long locationId);
    Map<String,Integer> getAvgSessionTimeByDateForPWSC(Integer mvnoId,Long locationId) throws ParseException;
    Map<String,Integer> getAuthFailureDataForPWSC(Integer mvnoId,Long locationId) throws ParseException;


}

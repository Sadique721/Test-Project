package com.savbill.commonGateway.moules.StaffSales.Service;

import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaffSalseService {

    @Autowired
    private StaffUserRepository staffSalseRepo;

    public List<Map<String, Object>> getPlansCreatedByStaff(Integer staffId,String startdate, String enddate) {

        Timestamp startDateTime = parseToTimestamp(startdate);
        Timestamp endDateTime = parseToTimestamp(enddate);
        List<Object[]> rawResults = staffSalseRepo.getTotalSoldPlanCount(staffId,startDateTime,endDateTime);

        List<Map<String, Object>> mappedResults = new ArrayList<>();

        for (Object[] row : rawResults) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("planName", row[0]);
            map.put("customerCount", row[1]);
            map.put("cafCount",row[2]);
            map.put("planPrice", row[3]);
            mappedResults.add(map);
        }

        return mappedResults;
    }

    private Timestamp parseToTimestamp(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            // Case 1: Standard format yyyy-MM-dd HH:mm:ss
            return Timestamp.valueOf(date);
        } catch (Exception ignore) {}

        try {
            // Case 2: ISO format yyyy-MM-ddTHH:mm:ss
            Instant instant = Instant.parse(date);
            return Timestamp.from(instant);
        } catch (Exception ignore) {}

        try {
            // Case 3: Only date yyyy-MM-dd — convert to start of day
            LocalDate d = LocalDate.parse(date);
            return Timestamp.valueOf(d.atStartOfDay());
        } catch (Exception ignore) {}

        throw new IllegalArgumentException("Invalid date format: " + date);
    }



}

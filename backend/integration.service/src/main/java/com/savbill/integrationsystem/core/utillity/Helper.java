package com.savbill.integrationsystem.core.utillity;

//import com.savbill.NepaliCalendar.dto.NepaliDateDTO;
//import com.savbill.NepaliCalendar.service.DateConverterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class Helper {
    @Autowired
    JdbcTemplate jdbc;

    public String getBillGenBatchNumber(String batchName) {
        String currinvoiceNo = null;
        String newInvoiceNo = null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();
//            JdbcTemplate jdbcTemplate1 = (JdbcTemplate) ApplicationContextUtils.getApplicationContext().getBean("jdbcTemplate2");
            String query = "SELECT nextval('billgenbatchno')";
            currinvoiceNo = jdbc.queryForObject(query, String.class).trim();
            StringBuilder sb = new StringBuilder();
            sb.append(batchName);
            sb.append(current_Year);
            while (sb.length() < 12 - currinvoiceNo.length()) {
                sb.append('0');
            }
            sb.append(currinvoiceNo);
            newInvoiceNo = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }


//    public static NepaliDateDTO getNepaliDate(LocalDateTime englishDate) {
//        DateConverterService dateConverterService = new DateConverterService();
//        String englishDateAndTime = englishDate.getDayOfMonth() + "-" + englishDate.getMonthValue() + "-" + englishDate.getYear() + " " + englishDate.getHour() + ":" + englishDate.getMinute() + ":" + englishDate.getSecond();
//        return dateConverterService.getNepaliDateFromEnglishDate(englishDateAndTime);
//    }
//    NepaliDateDTO localBillDate = CommonUtil.getNepaliDate(invoice.getBillDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
//    NepaliDateDTO localStartDate = CommonUtil.getNepaliDate(invoice.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
//    NepaliDateDTO localEndDate = CommonUtil.getNepaliDate(invoice.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
//                invoice.setLocalbilldate(localBillDate.getGatey() + " " + localBillDate.getMahinaInWords() + " " + localBillDate.getSaal());
//                invoice.setLocalstartdate(localStartDate.getGatey() + " " + localStartDate.getMahinaInWords() + " " + localStartDate.getSaal());
//                invoice.setLocalenddate(localEndDate.getGatey() + " " + localEndDate.getMahinaInWords() + " " + localEndDate.getSaal());


    private static String getTwoDigitValue(int value) {
        String twoDigit = "";
        if (value < 10) {
            twoDigit = "0" + value;
        } else {
            twoDigit = String.valueOf(value);
        }
        return twoDigit;
    }


}

package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.time.LocalDate;

@Component
public class NumberSequenceUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public synchronized String getInvoiceNumber(Boolean isLCO, Integer partnerId, Integer mvnoId) {
        String currinvoiceNo=null;
        String newInvoiceNo=null;
        try {
            String query = "SELECT nextval('invoiceno_"+mvnoId+"')";
            if(isLCO)
                query = "SELECT nextval('invoiceno_"+mvnoId+"_"+partnerId+"')";

            currinvoiceNo= jdbcTemplate.queryForObject(query, String.class).trim();
            newInvoiceNo = currinvoiceNo;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }

    public String getInvoiceNumberForTrial(Boolean isLCO, Integer partnerId, Integer mvnoId) {
        String currinvoiceNo=null;
        String newInvoiceNo=null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();

            String query = "SELECT nextval('invoicenotrail_"+mvnoId+"')";
            if(isLCO)
                query = "SELECT nextval('invoicenotrail_"+mvnoId+"_"+partnerId+"')";

            synchronized (InvoiceUtil.class){
                currinvoiceNo= jdbcTemplate.queryForObject(query, String.class).trim();}

//            StringBuilder sb = new StringBuilder();
//            sb.append(mvnoId);
//            sb.append("-");
//            sb.append(current_Year);
//            sb.append("-");
//            while (sb.length() < 12 - currinvoiceNo.length()) {
//                sb.append('0');
//            }
//            sb.append(currinvoiceNo);
            newInvoiceNo=currinvoiceNo;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }

    @Transient
    public void createInvoiceFunctionForMVNO(Mvno pojo) {
        //Invoice number
        try {
            String queryForInsertSequence = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoiceno_"+pojo.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertSequence);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //Trial Invoice number
        try {
            String queryForInsertSequenceTrial = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoicenotrail_"+pojo.getId()+"', 1, 1,9999999,1) ON DUPLICATE KEY UPDATE name = name;";
            jdbcTemplate.execute(queryForInsertSequenceTrial);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //Payment number
        try {
            String queryForInsertPayment = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('paymentno_"+pojo.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //CN number
        try {
            String queryForInsertPayment = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('creditnoteno_"+pojo.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {

            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
    }

    @Transient
    public void createInvoiceFunctionForPartner(Partner partner) {

        //Invoice number
        try {
            String queryForInsertSequence = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoiceno_"+partner.getMvnoId()+"_"+partner.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertSequence);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //Trial Invoice number
        try {
            String queryForInsertSequenceTrial = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoicenotrail_"+partner.getMvnoId()+"_"+partner.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertSequenceTrial);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //Payment number
        try {
            String queryForInsertPayment = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('paymentno_"+partner.getMvnoId()+"_"+partner.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
        //CN number
        try {
            String queryForInsertPayment = "INSERT INTO sequence"+
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('creditnoteno_"+partner.getMvnoId()+"_"+partner.getId()+"', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function"+ex.getMessage());
        }
    }
}

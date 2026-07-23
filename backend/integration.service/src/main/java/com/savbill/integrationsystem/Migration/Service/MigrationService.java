package com.savbill.integrationsystem.Migration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.Migration.Controller.MigrationController;
import com.savbill.integrationsystem.PostpaidPlan.CustomersMigrationPojo;
import com.savbill.integrationsystem.PostpaidPlan.PostpaidPlanMigrationPojo;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import feign.Response;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MigrationService {
    Logger logger = LoggerFactory.getLogger(MigrationController.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    CMSClient cmsClient;
    public GenericDataDTO planCreateFromXLS(MultipartFile file, HttpServletRequest req) throws IOException {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        List<PostpaidPlanMigrationPojo> plans = new ArrayList<>();

        Workbook workbook =null;
        try(InputStream fis = file.getInputStream()){

            workbook = new XSSFWorkbook(fis);

            logger.info("Number of sheets: ");
            workbook.forEach(sheet -> {
                logger.info(" => " + sheet.getSheetName());
                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for(Row row : sheet) {

                    if(index++ == 0) continue;
                    PostpaidPlanMigrationPojo plan = new PostpaidPlanMigrationPojo();
                    plan.setName(dataFormatter.formatCellValue(row.getCell(1)));
                    plan.setDisplayName(dataFormatter.formatCellValue(row.getCell(2)));
                    plan.setCode(dataFormatter.formatCellValue(row.getCell(3)));
                    plan.setPlantype(dataFormatter.formatCellValue(row.getCell(4)));
                    plan.setCategory(dataFormatter.formatCellValue(row.getCell(5)));
                    plan.setMode(dataFormatter.formatCellValue(row.getCell(6)));
                    plan.setPlanGroup(dataFormatter.formatCellValue(row.getCell(7)));
                    plan.setServiceNames(new ArrayList<>(Arrays.asList(dataFormatter.formatCellValue(row.getCell(8)))));
                    plan.setServiceAreas( new ArrayList<>(Arrays.asList(dataFormatter.formatCellValue(row.getCell(9)))));
                    plan.setAccessibility(dataFormatter.formatCellValue(row.getCell(10)));
                    plan.setStartDateString(parseDate(dataFormatter.formatCellValue(row.getCell(11))).toString());
                    plan.setEndDateString(parseDate(dataFormatter.formatCellValue(row.getCell(12))).toString());
                    plan.setValidity(parseDouble(dataFormatter.formatCellValue(row.getCell(13))));
                    plan.setUnitsOfValidity(dataFormatter.formatCellValue(row.getCell(14)));
                    plan.setAllowdiscount(parseBoolean(dataFormatter.formatCellValue(row.getCell(15))));
                    plan.setPlanStatus(dataFormatter.formatCellValue(row.getCell(16)));
                    plan.setInvoiceToOrg(parseBoolean(dataFormatter.formatCellValue(row.getCell(17))));
                    plan.setRequiredApproval(parseBoolean(dataFormatter.formatCellValue(row.getCell(18))));
                    plan.setAllowOverUsage(parseBoolean(dataFormatter.formatCellValue(row.getCell(19))));
                    plan.setMaxconcurrentsession(dataFormatter.formatCellValue(row.getCell(20)));
                    plan.setDesc(dataFormatter.formatCellValue(row.getCell(21)));
                    plan.setQuotatype(dataFormatter.formatCellValue(row.getCell(22)));
                    plan.setQuotatime(parseDouble(dataFormatter.formatCellValue(row.getCell(23))));
                    plan.setQuotaunittime(dataFormatter.formatCellValue(row.getCell(24)));
                    plan.setQuota(parseLong(dataFormatter.formatCellValue(row.getCell(25))));
                    plan.setQuotaUnit(dataFormatter.formatCellValue(row.getCell(26)));
                    plan.setQuotaResetInterval(dataFormatter.formatCellValue(row.getCell(27)));
                    plan.setSaccode(dataFormatter.formatCellValue(row.getCell(28)));
                    plan.setQospolicyName(dataFormatter.formatCellValue(row.getCell(29)));
                    plan.setTimebasepolicyName(dataFormatter.formatCellValue(row.getCell(30)));
                    plan.setParam1(dataFormatter.formatCellValue(row.getCell(31)));
                    plan.setParam2(dataFormatter.formatCellValue(row.getCell(32)));
                    plan.setParam3(dataFormatter.formatCellValue(row.getCell(33)));
                    plan.setChargenameList(new ArrayList<>(Arrays.asList(dataFormatter.formatCellValue(row.getCell(34)))));
                    plan.setNewOfferPrice(parseDouble(dataFormatter.formatCellValue(row.getCell(35))));
                    plan.setBillCycle(parseInteger(dataFormatter.formatCellValue(row.getCell(36))));
//                   plan.setOfferprice(parseDouble(dataFormatter.formatCellValue(row.getCell(37))));
                    plans.add(plan);
                }
            });
            genericDataDTO.setResponseCode( migratePostPaidPlandata(plans, req.getHeader("Authorization")).getResponseCode());
            genericDataDTO.setData( migratePostPaidPlandata(plans, req.getHeader("Authorization")).getData());
        } catch (EncryptedDocumentException | IOException e) {
            logger.error(e.getMessage(), e);
        }finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
        return  genericDataDTO;
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Long parseLong(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy", Locale.ENGLISH);
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            LocalDate date = LocalDate.parse(value, formatter);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public GenericDataDTO customerCreateFromXLS(MultipartFile file,HttpServletRequest request) throws IOException {

        GenericDataDTO genericDataDTO=new GenericDataDTO();
        List<CustomersMigrationPojo> customersMigrationPojos = new ArrayList<>();

        Workbook workbook =null;
        try(InputStream fis = file.getInputStream()){

            workbook = new XSSFWorkbook(fis);

            logger.info("Number of sheets: ");
            workbook.forEach(sheet -> {
                logger.info(" => " + sheet.getSheetName());
                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for(Row row : sheet) {

                    if (index++ == 0) continue;
                    CustomersMigrationPojo customersMigrationPojo=new CustomersMigrationPojo();
                    if(!dataFormatter.formatCellValue(row.getCell(1)).equals("")) {
                        customersMigrationPojo.setCusttype(dataFormatter.formatCellValue(row.getCell(1)));
                        customersMigrationPojo.setTitle(dataFormatter.formatCellValue(row.getCell(2)));
                        customersMigrationPojo.setFirstname(dataFormatter.formatCellValue(row.getCell(3)));
                        customersMigrationPojo.setLastname(dataFormatter.formatCellValue(row.getCell(4)));
                        customersMigrationPojo.setUsername(dataFormatter.formatCellValue(row.getCell(5)));
                        customersMigrationPojo.setPassword(dataFormatter.formatCellValue(row.getCell(6)));
                        customersMigrationPojo.setCountryCode(dataFormatter.formatCellValue(row.getCell(7)));
                        customersMigrationPojo.setPrimaryMobile(dataFormatter.formatCellValue(row.getCell(8)));
                        customersMigrationPojo.setSecondaryMobile(dataFormatter.formatCellValue(row.getCell(9)));
                        customersMigrationPojo.setTelephone(dataFormatter.formatCellValue(row.getCell(10)));
                        customersMigrationPojo.setFax(dataFormatter.formatCellValue(row.getCell(11)));
                        customersMigrationPojo.setEmail(dataFormatter.formatCellValue(row.getCell(12)));
                        customersMigrationPojo.setPan(dataFormatter.formatCellValue(row.getCell(13)));
                        customersMigrationPojo.setContactperson(dataFormatter.formatCellValue(row.getCell(14)));
                        customersMigrationPojo.setCalendarType(dataFormatter.formatCellValue(row.getCell(15)));
                        customersMigrationPojo.setCustomerCategory(dataFormatter.formatCellValue(row.getCell(16)));
                        customersMigrationPojo.setCDCustomerType(dataFormatter.formatCellValue(row.getCell(17)));
                        customersMigrationPojo.setCDCustomerSubType(dataFormatter.formatCellValue(row.getCell(18)));
                        customersMigrationPojo.setCustomerSector(dataFormatter.formatCellValue(row.getCell(19)));
                        customersMigrationPojo.setCustomerSectorType(dataFormatter.formatCellValue(row.getCell(20)));
                        customersMigrationPojo.setCAFNumber(dataFormatter.formatCellValue(row.getCell(21)));
                        customersMigrationPojo.setDOB(dataFormatter.formatCellValue(row.getCell(22)));
                        customersMigrationPojo.setBillday(parseInteger(dataFormatter.formatCellValue(row.getCell(23))));
                        customersMigrationPojo.setStatus(dataFormatter.formatCellValue(row.getCell(24)));
                        customersMigrationPojo.setDedicatedStaffUserName(dataFormatter.formatCellValue(row.getCell(25)));
                        customersMigrationPojo.setParentCustomer(dataFormatter.formatCellValue(row.getCell(26)));
                        customersMigrationPojo.setCustomerType(dataFormatter.formatCellValue(row.getCell(27)));
                        customersMigrationPojo.setSalesMark(dataFormatter.formatCellValue(row.getCell(28)));
                        customersMigrationPojo.setParentExperience(dataFormatter.formatCellValue(row.getCell(29)));
                        customersMigrationPojo.setServiceArea(dataFormatter.formatCellValue(row.getCell(30)));
                        customersMigrationPojo.setBranchName(dataFormatter.formatCellValue(row.getCell(31)));
                        customersMigrationPojo.setPartner(dataFormatter.formatCellValue(row.getCell(32)));
                        customersMigrationPojo.setAddress(dataFormatter.formatCellValue(row.getCell(32)));
                        customersMigrationPojo.setMunicipality(dataFormatter.formatCellValue(row.getCell(33)));
                        customersMigrationPojo.setWard(dataFormatter.formatCellValue(row.getCell(34)));
                        customersMigrationPojo.setLandmark(dataFormatter.formatCellValue(row.getCell(35)));
                        customersMigrationPojo.setValleyType(dataFormatter.formatCellValue(row.getCell(36)));
                        customersMigrationPojo.setLatitude(dataFormatter.formatCellValue(row.getCell(38)));
                        customersMigrationPojo.setLongitude(dataFormatter.formatCellValue(row.getCell(39)));
                        customersMigrationPojo.setPOP(dataFormatter.formatCellValue(row.getCell(40)));
                        customersMigrationPojo.setOLT(dataFormatter.formatCellValue(row.getCell(41)));
                        customersMigrationPojo.setMasterDB(dataFormatter.formatCellValue(row.getCell(42)));
                        customersMigrationPojo.setSplitterDB(dataFormatter.formatCellValue(row.getCell(43)));
                        customersMigrationPojo.setStaticIP(dataFormatter.formatCellValue(row.getCell(44)));
                        customersMigrationPojo.setNASIP(dataFormatter.formatCellValue(row.getCell(45)));
                        customersMigrationPojo.setNASPortValidate(dataFormatter.formatCellValue(row.getCell(46)));
//                    customersMigrationPojo.setIPPoolNameBind(dataFormatter.formatCellValue(row.getCell(47)));
                        customersMigrationPojo.setPlanCategory(dataFormatter.formatCellValue(row.getCell(48)));
                        customersMigrationPojo.setPlanGroupName(dataFormatter.formatCellValue(row.getCell(50)));
                        customersMigrationPojo.setInvoiceType(dataFormatter.formatCellValue(row.getCell(52)));
                        customersMigrationPojo.setBillTo(dataFormatter.formatCellValue(row.getCell(53)));
                        customersMigrationPojo.setInvoiceToOrganization(dataFormatter.formatCellValue(row.getCell(54)));
                        customersMigrationPojo.setBillableTo(dataFormatter.formatCellValue(row.getCell(55)));
                        customersMigrationPojo.setDiscountType(dataFormatter.formatCellValue(row.getCell(56)));
                        customersMigrationPojo.setDiscountPercentage(dataFormatter.formatCellValue(row.getCell(57)));
                        customersMigrationPojo.setDExpiryDate(dataFormatter.formatCellValue(row.getCell(58)));
//                    customersMigrationPojo.setPlanName(dataFormatter.formatCellValue(row.getCell(51)));
                        customersMigrationPojo.setNewPriceWithDiscount(dataFormatter.formatCellValue(row.getCell(59)));
                        customersMigrationPojo.setServiceName(dataFormatter.formatCellValue(row.getCell(63)));
                        customersMigrationPojo.setPlannameList(new ArrayList<>(Arrays.asList(dataFormatter.formatCellValue(row.getCell(64)))));
                        customersMigrationPojo.setAreaName(dataFormatter.formatCellValue(row.getCell(65)));
                        customersMigrationPojo.setEarlybillday(Integer.valueOf(dataFormatter.formatCellValue(row.getCell(66))));
                        customersMigrationPojos.add(customersMigrationPojo);
                    }

                }
            });
            genericDataDTO=migrateCustomerdata(customersMigrationPojos,request.getHeader("Authorization"));
    }
        return genericDataDTO;

}


    public GenericDataDTO migratePostPaidPlandata( List<PostpaidPlanMigrationPojo> pojos,String token) {
        return cmsClient.migratePostPaidPlan(pojos,token);
    }
    public GenericDataDTO migrateCustomerdata(List<CustomersMigrationPojo> pojos, String token) {
        return cmsClient.migrateCustomer(pojos,token);
    }

    public ResponseEntity<Object> migrateUpdatePlan(MultipartFile file, String token) {
        return cmsClient.planUpdateBySheet(file,token);
    }

    public Response migrateDownloadPlan(String token) {
        return cmsClient.downloadPlanSheet(token);
    }

    public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, HashMap<String, Object> response,Object object){
        return  apiResponse(responseCode,response,object);
    }



}


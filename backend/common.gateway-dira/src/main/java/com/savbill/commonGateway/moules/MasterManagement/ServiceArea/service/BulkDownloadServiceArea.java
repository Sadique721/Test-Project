package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;

import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.repository.CityRepository;
import com.savbill.commonGateway.moules.MasterManagement.LocationMaster.LocationMasterRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.Constant.BulkManagementConstant;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LocationMaster;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Bulk download service area.
 */
@Slf4j
@Service
public class BulkDownloadServiceArea {
    private final EntityManager entityManager;
    private JdbcTemplate jdbcTemplate;

    private final String serviceAreaTableName = "tblmservicearea";

    @Autowired
    private PincodeRepository pinRepo;

    @Autowired
    private LocationMasterRepository locationMasterRepo;

    @Autowired
    private CityRepository cityRepo;

    @Autowired
    private ServiceAreaRepository serviceAreaRepo;

    @Autowired
    private LocationMasterRepository locationRepository;

    /**
     * Instantiates a new Bulk download service area.
     * @param entityManager the entity manager
     * @param jdbcTemplate the jdbc template
     */
    public BulkDownloadServiceArea(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets service area header.
     * @param tableName the table name
     * @return the service area header
     * @throws SQLException the sql exception
     */
    public LinkedHashSet<String> getServiceAreaHeader(String tableName) throws SQLException {
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) {
            throw new SQLException("DataSource is not available.");
        }
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getColumns(null, null, tableName, null)) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    headers.add(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headers;
    }

    /**
     * Gets table header.
     * @param serviceAreaTableName the service area table name
     * @return the table header
     * @throws Exception the exception
     */
    public List<String> getTableHeader(String serviceAreaTableName) throws Exception {
        List<String> headerList = new ArrayList<>();
        try {
            LinkedHashSet<String> tableSourceMaster = getServiceAreaHeader(serviceAreaTableName);
            LinkedHashSet<String> commonHeader = new LinkedHashSet<>(Arrays.asList(
                    "service_area_id", "createdate", "lastmodifieddate", "is_deleted", "MVNOID", "createdbystaffid",
                    "lastmodifiedbystaffid", "createbyname", "updatebyname", "mvno_lists", "areaid", "is_bind_with_plan"
            ));
            LinkedHashSet<String> addOtherHeader = new LinkedHashSet<>(Arrays.asList(BulkManagementConstant.SourceMasterColumn.LOCATION_ID, BulkManagementConstant.SourceMasterColumn.PINCODE_ID));
            tableSourceMaster.removeAll(commonHeader);
            tableSourceMaster.addAll(addOtherHeader);
            headerList = new ArrayList<>(tableSourceMaster);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerList;
    }

    /**
     * Write excel sheet with headers list.
     * @param header the header
     * @return the list
     */
    public List<Object> writeExcelSheetWithHeaders(List<String> header) {
        List<Object> objects = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
                CellStyle headerCellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                headerCellStyle.setFont(font);
                cell.setCellStyle(headerCellStyle);
            }
            objects.add(workbook);
            objects.add(sheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * Create excel report workbook.
     * @return the workbook
     * @throws Exception the exception
     */
    public Workbook createExcelReport(Integer mvnoId) throws Exception {
        try {
            List<String> tableHeader = getTableHeader(serviceAreaTableName);
            List<Object> objects = writeExcelSheetWithHeaders(tableHeader);
            Workbook workbook = (Workbook) objects.get(0);
            Sheet sheet = (Sheet) objects.get(1);
            /** Validate On Master Sheet*/
            addMasterSheetValidation(objects, sheet);
            if (mvnoId != 1) {
                /** Create Site Data Sheet */
                createSiteDataSheet(mvnoId, workbook, sheet);
            }
            /** create City Data Sheet */
            createCityDataSheet(mvnoId, workbook, sheet);
            /** create Location Data Sheet */
            createLocationDataSheet(mvnoId, workbook, sheet);
            /** create Pincode Data Sheet */
            createPincodeDataSheet(mvnoId, workbook, sheet);
            /** Create Dependend Sheet */
            createPincodeCityDependSheet(workbook, sheet);
            /** Code for Hide Sheet Method */
            configureSheetVisibility(workbook, mvnoId);
            return workbook;
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void configureSheetVisibility(Workbook workbook, Integer mvnoId) {
        try {
            List<String> sheetsToHide = new ArrayList<>();
            if (mvnoId == 1) {
                sheetsToHide.add(BulkManagementConstant.SheetNames.CITY_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.PINCODE_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.CITY_PINCODE_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.LOCATION_SHEET);
            } else {
                sheetsToHide.add(BulkManagementConstant.SheetNames.CITY_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.PINCODE_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.SITE_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.CITY_PINCODE_SHEET);
                sheetsToHide.add(BulkManagementConstant.SheetNames.LOCATION_SHEET);
            }
            /** Hide the specified sheets */
            for (String sheetName : sheetsToHide) {
                int sheetIndex = workbook.getSheetIndex(sheetName);
                if (sheetIndex != -1) {
                    workbook.setSheetHidden(sheetIndex, true);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets mvno id from current staff.
     * @return the mvno id from current staff
     */
    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return mvnoId;
    }

    private void createPincodeCityDependSheet(Workbook workbook, Sheet sheet) {
        try {
            Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
            Sheet pincodeSheet = workbook.getSheet(BulkManagementConstant.SheetNames.PINCODE_SHEET);
            addCityPincodeSheets(workbook, masterSheet, pincodeSheet, 1, 10000, 4, 10);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addCityPincodeSheets(Workbook workbook, Sheet masterSheet, Sheet pincodeSheet, int firstRow,
                                      int lastRow, int cityCol, int pincodeCol) {
        try {
            /** Create a map to store city and pincode relationships */
            Map<String, List<String>> cityPincodeMap = new HashMap<>();
            Set<String> allCities = new HashSet<>();
            /** Populate cities set and city-pincode map */
            for (int rowIndex = 1; rowIndex <= pincodeSheet.getLastRowNum(); rowIndex++) {
                Row pincodeRow = pincodeSheet.getRow(rowIndex);
                if (pincodeRow != null) {
                    Cell cityCell = pincodeRow.getCell(2);
                    Cell pincodeCell = pincodeRow.getCell(1);
                    if (cityCell != null && pincodeCell != null) {
                        String cityName = cityCell.getStringCellValue().trim();
                        String pincode = pincodeCell.getStringCellValue().trim();

                        if (!cityName.isEmpty() && !pincode.isEmpty()) {
                            /** Add city to the set of all cities */
                            allCities.add(cityName);
                            /** Add pincode to the list of pincodes for this city */
                            cityPincodeMap.computeIfAbsent(cityName, k -> new ArrayList<>()).add(pincode);
                        }
                    }
                }
            }
            /** Create a dropdown sheet to store city and pincode data */
            Sheet dropdownSheet = workbook.createSheet(BulkManagementConstant.SheetNames.CITY_PINCODE_SHEET);
            int rowIndex = 0;
            int maxPincodes = 0;
            /** Populate dropdown sheet with cities and their pincodes */
            for (String city : allCities) {
                Row row = dropdownSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(city);
                List<String> pincodes = cityPincodeMap.get(city);
                if (pincodes == null || pincodes.isEmpty()) {
                    row.createCell(1).setCellValue(" ");
                    maxPincodes = Math.max(maxPincodes, 1);
                } else {
                    for (int i = 0; i < pincodes.size(); i++) {
                        row.createCell(i + 1).setCellValue(pincodes.get(i));
                    }
                    maxPincodes = Math.max(maxPincodes, pincodes.size());
                }
            }
            /** Create named range for cities */
            Name cityNameRange = workbook.createName();
            cityNameRange.setNameName("CityNames");
            cityNameRange.setRefersToFormula("CityPincodeSheet!$A$1:$A$" + rowIndex);
            /** Create city dropdown validation */
            DataValidationHelper validationHelper = masterSheet.getDataValidationHelper();
            DataValidationConstraint cityConstraint = validationHelper.createFormulaListConstraint("CityNames");
            CellRangeAddressList cityAddressList = new CellRangeAddressList(firstRow, lastRow, cityCol, cityCol);
            DataValidation cityValidation = validationHelper.createValidation(cityConstraint, cityAddressList);
            cityValidation.setShowErrorBox(true);
            cityValidation.createErrorBox("Invalid City", "Must select from dropdown");
            masterSheet.addValidationData(cityValidation);
            /** Create dependent pincode dropdown validation */
            String pincodeFormula =
                    "INDIRECT(\"CityPincodeSheet!B\"&MATCH($" + (char) ('A' + cityCol) + "2,CityPincodeSheet!$A$1:$A$" + rowIndex + ",0)&\":\"&CHAR(66+" + (maxPincodes - 1) + ")" +
                            "&MATCH($" + (char) ('A' + cityCol) + "2,CityPincodeSheet!$A$1:$A$" + rowIndex + ",0))";
            DataValidationConstraint pincodeConstraint = validationHelper.createFormulaListConstraint(pincodeFormula);
            CellRangeAddressList pincodeAddressList = new CellRangeAddressList(firstRow, lastRow, pincodeCol, pincodeCol);
            DataValidation pincodeValidation = validationHelper.createValidation(pincodeConstraint, pincodeAddressList);
            pincodeValidation.setShowErrorBox(true);
            pincodeValidation.createErrorBox("Invalid Pincode", "Must select from dropdown");
            masterSheet.addValidationData(pincodeValidation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating city-pincode dropdowns: " + e.getMessage(), e);
        }
    }

    private void addMasterSheetValidation(List<Object> objects, Sheet sheet) {
        try {
            /** Validate Name Validate */
            addServiceAreaNameValidate(sheet, 0, 1000);
            /** Validate Numerical Validate for Radius */
            addNumericalValueValidation(sheet, 5, 1000);
            /** Validate Dropdown Validate for Status */
            addStatusDropdown(sheet, 1, 1, 10000);
            /** Validate Dropdown Validate for Service Area Type */
            addServiceAreaTypeDropdown(sheet, 7, 1, 10000);
            /** Validate BlockNo based on Service Area Type */
            addBlockNoValidation(sheet, 8, 7, 1, 10000);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addServiceAreaTypeDropdown(Sheet sheet, int columnIndex, int startRow, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            String[] options = {BulkManagementConstant.DropdownStatus.PUBLIC,
                    BulkManagementConstant.DropdownStatus.PRIVATE};
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, lastRow, columnIndex, columnIndex);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Values", "Values must select from Dropdown");
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addBlockNoValidation(Sheet sheet, int blockNoColumnIndex, int serviceAreaTypeColumnIndex, int startRow, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            /**
             * Combined validation formula:
             * 1. Only allow input if Service Area Type is "Private"
             * 2. Ensure only whole numbers are entered
             * 3. Prevent decimal numbers and non-numeric inputs
             */
            String formula = "AND(" +
                    // Check if Service Area Type is "Private"
                    "IF(" + getColumnLetter(serviceAreaTypeColumnIndex) + "2=\"Private\", TRUE, FALSE)," +
                    // Check if the input is a whole number (integer)
                    "AND(" +
                    "ISNUMBER(" + getColumnLetter(blockNoColumnIndex) + "2)," +
                    // Ensure it's an integer (no decimals)
                    getColumnLetter(blockNoColumnIndex) + "2=INT(" + getColumnLetter(blockNoColumnIndex) + "2)" +
                    ")" +
                    ")";
            /** Apply the formula to the BlockNo column */
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, lastRow, blockNoColumnIndex, blockNoColumnIndex);
            DataValidationConstraint constraint = validationHelper.createCustomConstraint(formula);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            /** Error box for invalid input */
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Input", "BlockNo is required only for 'Private' Service Area Type and must be a valid number (e.g., 1, 23, 100).");
            /** Add the validation to the sheet */
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    private void addStatusDropdown(Sheet sheet, int columnIndex, int startRow, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            String[] options = {BulkManagementConstant.DropdownStatus.ACTIVE,
                    BulkManagementConstant.DropdownStatus.INACTIVE,
                    BulkManagementConstant.DropdownStatus.UNDER_DEVELOPMENT};
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
            CellRangeAddressList addressList = new CellRangeAddressList(startRow, lastRow, columnIndex, columnIndex);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Values", "Values must select from Dropdown");
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addServiceAreaNameValidate(Sheet sheet, int colIndex, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            /** Custom formula for validating names: Starts with a letter, may contain numbers (but not at the start), no trailing space, and no duplicates */
            String nameValidationFormula = "AND(" +
                    "ISTEXT(LEFT(" + getColumnLetter(colIndex) + "2,1)), " +
                    "ISNUMBER(SEARCH(LEFT(" + getColumnLetter(colIndex) + "2,1),\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\")), " +
                    "ISNUMBER(SEARCH(" + getColumnLetter(colIndex) + "2," + getColumnLetter(colIndex) + "2)), " +
                    "COUNTIF(" + getColumnLetter(colIndex) + "$2:" + getColumnLetter(colIndex) + "$" + lastRow + "," + getColumnLetter(colIndex) + "2)=1, " +
                    "RIGHT(" + getColumnLetter(colIndex) + "2,1)<>\" \"" +
                    ")";
            /** Set the region where the validation should appear (from row 2 to the last row, excluding the header) */
            CellRangeAddressList addressList = new CellRangeAddressList(1, lastRow, colIndex, colIndex);
            /** Apply the custom validation formula */
            DataValidationConstraint constraint = validationHelper.createCustomConstraint(nameValidationFormula);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            /** Ensure the validation only allows valid names (no duplicates, no trailing space) */
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Name", "The name must start with a letter, may contain numbers, must be unique, and cannot have trailing spaces.");
            /** Add the validation to the sheet */
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addNumericalValueValidation(Sheet sheet, int colIndex, int lastRow) {
        try {
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            /**
             * Custom formula for validating numerical values:
             * Must be a number (integer or decimal) and allow duplicates.
             */
            String numericalValidationFormula = "ISNUMBER(" + getColumnLetter(colIndex) + "2)";
            /**
             * Set the region where the validation should appear (from row 2 to the last row, excluding the header)
             */
            CellRangeAddressList addressList = new CellRangeAddressList(1, lastRow, colIndex, colIndex);
            /**
             * Apply the custom validation formula
             */
            DataValidationConstraint constraint = validationHelper.createCustomConstraint(numericalValidationFormula);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            /**
             * Ensure the validation only allows valid numbers (integer or decimal).
             */
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Value", "The value must be a valid number, such as 1, 23.00040, 100, or 1.098.");
            /**
             * Add the validation to the sheet
             */
            sheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getColumnLetter(int columnIndex) {
        try {
            return CellReference.convertNumToColString(columnIndex);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createLocationDataSheet(Integer mvnoId, Workbook workbook, Sheet sheet) {
        try {
            final String locationSheetName = BulkManagementConstant.SheetNames.LOCATION_SHEET;
            List<Map<String, String>> locationData = getLocationData(mvnoId);
            addDataToSheet(workbook, locationData, locationSheetName, BulkManagementConstant.MapData.LOCATION_NAME);
            addDropDownToMasterSheet(sheet, workbook, locationSheetName, 9, 10000);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createPincodeDataSheet(Integer mvnoId, Workbook workbook, Sheet sheet) {
        try {
            final String pincodeSheet = BulkManagementConstant.SheetNames.PINCODE_SHEET;
            List<Map<String, String>> pincodeData = getPincodeData(mvnoId);
            addDataToSheet(workbook, pincodeData, pincodeSheet, BulkManagementConstant.MapData.PINCODE);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createCityDataSheet(Integer mvnoId, Workbook workbook, Sheet sheet) {
        try {
            final String citySheetName = BulkManagementConstant.SheetNames.CITY_SHEET;
            List<Map<String, String>> cityData = getCityData(mvnoId);
            addDataToSheet(workbook, cityData, citySheetName, BulkManagementConstant.MapData.CITY_NAME);
            addDropDownToMasterSheet(sheet, workbook, citySheetName, 4, 10000);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createSiteDataSheet(Integer mvnoId, Workbook workbook, Sheet sheet) {
        try {
            final String siteSheetName = BulkManagementConstant.SheetNames.SITE_SHEET;
            List<Map<String, String>> siteData = getSiteData(mvnoId);
            addDataToSheet(workbook, siteData, siteSheetName, BulkManagementConstant.MapData.SITE_NAME);
            addDropDownToMasterSheet(sheet, workbook, siteSheetName, 6, 10000);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Map<String, String>> getSiteData(Integer mvnoId) {
        try {
            List<String> filteredSiteName = serviceAreaRepo.findsiteNameBymvnoId(mvnoId);
            return filteredSiteName.stream().map(siteName -> {
                Map<String, String> siteNameMap = new LinkedHashMap<>();
                siteNameMap.put(BulkManagementConstant.MapData.SITE_NAME, siteName);
                return siteNameMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addDropDownToMasterSheet(Sheet masterSheet, Workbook workbook, String dataSheetName,
                                          int colIndex, int lastRow) {
        try {
            Sheet dataSheet = workbook.getSheet(dataSheetName);
            int lastDataRow = dataSheet.getLastRowNum();
            Name namedRange = workbook.createName();
            String reference;
            if (dataSheetName == BulkManagementConstant.SheetNames.SITE_SHEET) {
                reference = dataSheetName + "!$A$2:$A$" + (lastDataRow + 1);
            } else {
                reference = dataSheetName + "!$B$2:$B$" + (lastDataRow + 1);
            }
            namedRange.setRefersToFormula(reference);
            namedRange.setNameName(dataSheetName + "NameRange");
            DataValidationHelper validationHelper = masterSheet.getDataValidationHelper();
            DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(dataSheetName + "NameRange");
            CellRangeAddressList addressList = new CellRangeAddressList(1, lastRow, colIndex, colIndex);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.createErrorBox("Invalid Value", "Please select a value from the dropdown that matches the exact case.");
            masterSheet.addValidationData(dataValidation);
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets city data.
     * @param mvnoId the mvno id
     * @return the city data
     */
    public List<Map<String, String>> getCityData(Integer mvnoId) {
        try {
            List<City> filteredCities;
            if (mvnoId != null && mvnoId != 1) {
                filteredCities = cityRepo.findAll().stream()
                        .filter(city -> (city.getMvnoId().equals(mvnoId) ||
                                city.getMvnoId().equals(1)) &&
                                !city.getIsDelete())
                        .collect(Collectors.toList());
            } else {
                filteredCities = cityRepo.findAll();
            }
            return filteredCities.stream().map(city -> {
                Map<String, String> cityDataMap = new LinkedHashMap<>();
                cityDataMap.put(BulkManagementConstant.MapData.CITYID, String.valueOf(city.getId()));
                cityDataMap.put(BulkManagementConstant.MapData.CITY_NAME, city.getName());
                return cityDataMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets pincode data.
     * @param mvnoId the mvno id
     * @return the pincode data
     */
    public List<Map<String, String>> getPincodeData(Integer mvnoId) {
        try {
            List<Pincode> filteredPincodeList;
            if (mvnoId != null && mvnoId != 1) {
                filteredPincodeList = pinRepo.findAll().stream()
                        .filter(pincode -> (pincode.getMvnoId().equals(mvnoId) ||
                                pincode.getMvnoId().equals(1)) &&
                                !pincode.getIsDeleted())
                        .collect(Collectors.toList());
            } else {
                filteredPincodeList = pinRepo.findAll();
            }
            return filteredPincodeList.stream().map(pincode -> {
                Map<String, String> pincodeDataMap = new LinkedHashMap<>();
                pincodeDataMap.put(BulkManagementConstant.MapData.PINCODE_ID, String.valueOf(pincode.getId()));
                pincodeDataMap.put(BulkManagementConstant.MapData.PINCODE, pincode.getPincode());
                Optional<City> city = cityRepo.findById(pincode.getCityId());
                pincodeDataMap.put(BulkManagementConstant.MapData.CITY_NAME, city.get().getName());
                return pincodeDataMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets location data.
     * @param mvnoId the mvno id
     * @return the location data
     */
    public List<Map<String, String>> getLocationData(Integer mvnoId) {
        try {
            List<LocationMaster> filteredLocationList;
            if (mvnoId != null && mvnoId != 1) {
                filteredLocationList = locationMasterRepo.findAll().stream()
                        .filter(locationMaster -> locationMaster.getMvnoId().equals(mvnoId.longValue()) ||
                                locationMaster.getMvnoId().equals(1L))
                        .collect(Collectors.toList());
            } else {
                filteredLocationList = locationMasterRepo.findAll();
            }
            return filteredLocationList.stream().map(locationMaster -> {
                Map<String, String> pincodeDataMap = new LinkedHashMap<>();
                pincodeDataMap.put(BulkManagementConstant.MapData.LOCATION_ID, String.valueOf(locationMaster.getLocationMasterId()));
                pincodeDataMap.put(BulkManagementConstant.MapData.LOCATION_NAME, locationMaster.getName());
                return pincodeDataMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Add data to sheet.
     * @param workbook the workbook
     * @param data the data
     * @param sheetName the sheet name
     */
    public void addDataToSheet(Workbook workbook, List<Map<String, String>> data, String sheetName, String emptyHeaderName) {
        try {
            /** If data is null or empty, create an empty entry dynamically */
            if (data == null || data.isEmpty()) {
                data = new ArrayList<>();
                Map<String, String> emptydata = new LinkedHashMap<>();
                /** If no data is provided, add an empty map with dynamic placeholder keys (header only) */
                emptydata.put(emptyHeaderName, "");
                data.add(emptydata);
            }
            Sheet sheet = workbook.createSheet(sheetName);
            /** Add header row for better clarity */
            Set<String> keys = data.get(0).keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < array.length; i++) {
                headerRow.createCell(i).setCellValue(array[i]);
            }
            /** Add data to the hidden sheet */
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> actualData = data.get(i);
                int cellIndex = 0;
                /** Iterate over the entries in the map */
                for (Map.Entry<String, String> entry : actualData.entrySet()) {
                    Cell cell = row.createCell(cellIndex++);
                    cell.setCellValue(entry.getValue());
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}

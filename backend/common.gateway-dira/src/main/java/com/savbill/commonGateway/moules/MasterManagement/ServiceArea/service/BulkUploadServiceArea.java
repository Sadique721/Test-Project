package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;


import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveServiceAreaSharedDataMessge;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.repository.CityRepository;
import com.savbill.commonGateway.moules.MasterManagement.LocationMaster.LocationMasterRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.Constant.APIConstants;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.Constant.BulkManagementConstant;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LocationMaster;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions.AlreadyExistException;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions.GenericException;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions.NoRecordFoundException;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.PolyGoneRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.rabbitmq.messages.ServiceAreaMesseage;
import com.savbill.commonGateway.rabbitmq.messages.ServiceareaMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Bulk upload service area.
 */
@Service
@Slf4j
@EnableAsync
public class BulkUploadServiceArea {

    /**
     * The Location repository.
     */
    @Autowired
    private LocationMasterRepository locationRepository;

    /**
     * The City repository.
     */
    @Autowired
    private CityRepository cityRepository;

    /**
     * The Pincode repository.
     */
    @Autowired
    private PincodeRepository pincodeRepository;

    /**
     * The Service area repository.
     */
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    /**
     * The Async service.
     */
    @Autowired
    private AsyncService asyncService;

    /**
     * The Staff user repository.
     */
    @Autowired
    private StaffUserRepository staffUserRepository;

    /**
     * The Service area service.
     */
    @Autowired
    private ServiceAreaService serviceAreaService;

    /**
     * The Kafka message sender.
     */
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * The Create data shared service.
     */
    @Autowired
    private CreateDataSharedService createDataSharedService;


    @Autowired
    private PolyGoneRepository polyGoneRepository;

    /**
     * The constant COLUMN_MAPPINGS.
     */
    private static final Map<Integer, String> COLUMN_MAPPINGS = new HashMap<>();

    static {
        COLUMN_MAPPINGS.put(0, BulkManagementConstant.SourceMasterColumn.NAME);
        COLUMN_MAPPINGS.put(1, BulkManagementConstant.SourceMasterColumn.STATUS);
        COLUMN_MAPPINGS.put(2, BulkManagementConstant.SourceMasterColumn.LATITUDE);
        COLUMN_MAPPINGS.put(3, BulkManagementConstant.SourceMasterColumn.LONGITUDE);
        COLUMN_MAPPINGS.put(4, BulkManagementConstant.SourceMasterColumn.CITYID);
        COLUMN_MAPPINGS.put(5, BulkManagementConstant.SourceMasterColumn.RADIUS);
        COLUMN_MAPPINGS.put(6, BulkManagementConstant.SourceMasterColumn.SITE_NAME);
        COLUMN_MAPPINGS.put(7, BulkManagementConstant.SourceMasterColumn.SERVICEAREA_TYPE);
        COLUMN_MAPPINGS.put(8, BulkManagementConstant.SourceMasterColumn.PINCODE);
        COLUMN_MAPPINGS.put(9, BulkManagementConstant.SourceMasterColumn.LOCATION);
        COLUMN_MAPPINGS.put(10, BulkManagementConstant.SourceMasterColumn.UNIT_NUMBER);
    }




    /**
     * Upload bulk data string.
     * @param file the file
     * @param mvnoId the mvno id
     * @param loggedInUserId the logged in user id
     * @param loggedInUserName the logged in user name
     * @return the string
     * @throws Exception the exception
     * @throws NoRecordFoundException the no record found exception
     * @throws AlreadyExistException the already exist exception
     * @throws GenericException the generic exception
     */
    public String uploadBulkData(MultipartFile file, Integer mvnoId, Integer loggedInUserId, String loggedInUserName) throws Exception, NoRecordFoundException, AlreadyExistException, GenericException {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            validateExcelData(workbook);
            validateDuplicateServiceName(workbook, mvnoId);
            if (mvnoId != 1) {
                validateCityMaster(workbook, mvnoId);
                validateLocationMaster(workbook, mvnoId);
                validatePincodeMaster(workbook, mvnoId);
            } else {
                validateSiteName(workbook);
            }
            validateMasterSheet(workbook, mvnoId);
            String successMessage = "Validations successful. Data is being processed in the background.";
            asyncService.doAsync(workbook, mvnoId, loggedInUserId, loggedInUserName);
            return successMessage;
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (Exception e) {
            HttpStatus status = (e instanceof Exception) ? HttpStatus.NO_CONTENT :
                    (e instanceof Exception) ? HttpStatus.CONFLICT :
                            HttpStatus.NOT_ACCEPTABLE;
            log.error(APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    status.value(),
                    e.getMessage()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Validate site name.
     * @param workbook the workbook
     */
    private void validateSiteName(Workbook workbook) {
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                String siteName = getExcelCellValue(row.getCell(6));
                if (siteName.trim().isEmpty()) {
                    log.error("Site Name cannot be empty in row {}!", rowIndex);
                    throw new RuntimeException("Site Name cannot be empty in row " + rowIndex);
                }
            }
        }
    }

    /**
     * Validate duplicate service name.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @throws Exception the exception
     */
    private void validateDuplicateServiceName(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                String serviceName = getExcelCellValue(row.getCell(0));
                boolean isDuplicate = duplicateVerifyAtSave(serviceName, mvnoId);
                if (!isDuplicate) {
                    log.error("ServiceArea Name '{}' is already exists!", serviceName);
                    throw new RuntimeException("Service Area Name '" + serviceName + "' is already exists!");
                }
            }
        }
    }

    /**
     * Duplicate verify at save boolean.
     * @param name the name
     * @param mvnoId the mvno id
     * @return the boolean
     * @throws Exception the exception
     */
    public boolean duplicateVerifyAtSave(String name, Integer mvnoId) throws Exception {
        name = name.trim();
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByNameAndIsDeletedIsFalse(name);
        if (serviceAreaList.isEmpty()) {
            return true;
        }
        for (ServiceArea serviceArea : serviceAreaList) {
            if ((mvnoId == 1 && serviceArea.getMvnoId() == 1) ||
                    (mvnoId != 1 && serviceArea.getMvnoId() != 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate pincode master.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @throws Exception the exception
     */
    private void validatePincodeMaster(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        List<Pincode> allPincodes = pincodeRepository.findAll(); // Fetch all pincodes once
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                String pincodeName = getExcelCellValue(row.getCell(10));
                if (pincodeName != null && !pincodeName.isEmpty()) {
                    List<Pincode> filteredPincode = allPincodes.stream()
                            .filter(pincode ->
                            (pincode.getMvnoId().equals(mvnoId) ||
                                    pincode.getMvnoId().equals(1)) &&
                                    pincode.getPincode().equals(pincodeName) &&
                                    !pincode.getIsDeleted())
                            .collect(Collectors.toList());

                    if (filteredPincode.isEmpty()) {
                        log.error("Pincode {} data is not matched with this mvno", pincodeName);
                        throw new RuntimeException("Pincode " + pincodeName + " data is not matched with this mvno");
                    }
                }
            }
        }
    }

    /**
     * Validate location master.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @throws Exception the exception
     */
    private void validateLocationMaster(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        List<LocationMaster> locationMasters = locationRepository.findAll(); // Fetch all pincodes once
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                String locationName = getExcelCellValue(row.getCell(9));
                if (locationName != null && !locationName.isEmpty()) {
                    List<LocationMaster> filteredLocation = locationMasters.stream()
                            .filter(locationMaster ->
                            (locationMaster.getMvnoId().equals(mvnoId.longValue()) ||
                                    locationMaster.getMvnoId().equals(1L)) &&
                                    locationMaster.getName().equals(locationName))
                            .collect(Collectors.toList());

                    if (filteredLocation.isEmpty()) {
                        log.error("Location Master {} data is not matched with this mvno", locationName);
                        throw new RuntimeException("Location Master " + locationName + " data is not matched with this mvno");
                    }
                }
            }
        }
    }


    /**
     * Validate city master.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @throws Exception the exception
     */
    private void validateCityMaster(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        List<City> cityList = cityRepository.findAll(); // Fetch all pincodes once
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                String cityName = getExcelCellValue(row.getCell(4));
                if (cityName != null && !cityName.isEmpty()) {
                    List<City> filteredCity = cityList.stream()
                            .filter(city ->
                            (city.getMvnoId().equals(mvnoId) ||
                                    city.getMvnoId().equals(1)) &&
                                    city.getName().equals(cityName) &&
                                    !city.getIsDelete())
                            .collect(Collectors.toList());

                    if (filteredCity.isEmpty()) {
                        log.error("City {} data is not matched with this mvno", cityName);
                        throw new RuntimeException("City " + cityName + " data is not matched with this mvno");
                    }
                }
            }
        }
    }

    /**
     * Validate excel data.
     * @param workbook the workbook
     * @throws NoRecordFoundException the no record found exception
     * @throws Exception the exception
     */
    private void validateExcelData(Workbook workbook) throws NoRecordFoundException, Exception {
        try {
            Map<Long, City> cityMap = getCityMap();
            Map<Long, LocationMaster> locationMasterMap = getLocationMasterMap();
            Map<Long, Pincode> pincodeMap = getPincodeMap();
            validateSheet(workbook.getSheet(BulkManagementConstant.SheetNames.CITY_SHEET),
                    cityMap, BulkManagementConstant.EntityName.CITY);
            validateSheet(workbook.getSheet(BulkManagementConstant.SheetNames.PINCODE_SHEET),
                    pincodeMap, BulkManagementConstant.EntityName.PINCODE);
            workbook.close();
        } catch (Exception e) {
            HttpStatus status = (e instanceof Exception) ? HttpStatus.NO_CONTENT :
                    (e instanceof Exception) ? HttpStatus.CONFLICT :
                            HttpStatus.NOT_ACCEPTABLE;
            log.error(APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    status.value(),
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Validate sheet.
     * @param <T> the type parameter
     * @param sheet the sheet
     * @param dbDataMap the db data map
     * @param sheetType the sheet type
     * @throws NoRecordFoundException the no record found exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    private <T> void validateSheet(Sheet sheet, Map<Long, T> dbDataMap, String sheetType) throws NoRecordFoundException, IllegalArgumentException {
        String SUBMODULE = getModuleNameForLog() + " [validateSheet()] ";
        try {
            if (sheet == null) {
                log.error(SUBMODULE + "Sheet " + sheetType + " is missing from the Excel file.");
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Sheet " + sheetType + " is missing from the Excel file.", null);
            }
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.getCellType() != CellType.STRING) {
                    log.error(SUBMODULE + "Invalid ID in " + sheetType + " sheet at row " + (row.getRowNum() + 1));
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid ID in " + sheetType + " sheet at row " + (row.getRowNum() + 1), null);
                }
                long id = Long.parseLong(idCell.getStringCellValue());
                if (!dbDataMap.containsKey(id)) {
                    log.error(SUBMODULE + sheetType + " ID " + id + " does not exist in the database.");
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), sheetType + " ID " + id + " does not exist in the database.", null);
                }
                switch (sheetType) {
                    case BulkManagementConstant.EntityName.CITY:
                        validateCityData(row, (City) dbDataMap.get(id));
                        break;
                    case BulkManagementConstant.EntityName.LOCATION:
                        validateLocationData(row, (LocationMaster) dbDataMap.get(id));
                        break;
                    case BulkManagementConstant.EntityName.PINCODE:
                        validatePincodeData(row, (Pincode) dbDataMap.get(id));
                        break;
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (NoRecordFoundException | IllegalArgumentException e) {
            HttpStatus status = (e instanceof NoRecordFoundException) ? HttpStatus.NO_CONTENT :
                    HttpStatus.EXPECTATION_FAILED;
            log.error(
                    APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    status.value(),
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Validate pincode data.
     * @param row the row
     * @param pincode the pincode
     * @throws NoRecordFoundException the no record found exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    private void validatePincodeData(Row row, Pincode pincode) throws NoRecordFoundException, IllegalArgumentException {
        String SUBMODULE = getModuleNameForLog() + " [validatePincodeData()] ";
        try {
            Cell nameCell = row.getCell(1);
            if (nameCell == null || nameCell.getCellType() != CellType.STRING) {
                log.error(SUBMODULE + "Invalid name in Pincode sheet at row " + (row.getRowNum() + 1));
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid name in Pincode sheet at row " + (row.getRowNum() + 1), null);
            }
            String name = nameCell.getStringCellValue().trim();
            if (!name.equals(pincode.getPincode())) {
                log.error(SUBMODULE + "Pincode mismatch found for:: " + pincode.getPincode());
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Pincode mismatch found for:: " + pincode.getPincode(), null);
            }
        } catch (IllegalArgumentException e) {
            log.error(
                    APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Validate location data.
     * @param row the row
     * @param locationMaster the location master
     * @throws NoRecordFoundException the no record found exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    private void validateLocationData(Row row, LocationMaster locationMaster) throws NoRecordFoundException, IllegalArgumentException {
        String SUBMODULE = getModuleNameForLog() + " [validateLocationData()] ";
        try {
            Cell nameCell = row.getCell(1);
            if (nameCell == null || nameCell.getCellType() != CellType.STRING) {
                log.error(SUBMODULE + "Invalid name in Location Master sheet at row " + (row.getRowNum() + 1));
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid name in Location Master sheet at row " + (row.getRowNum() + 1), null);
            }
            String name = nameCell.getStringCellValue().trim();
            if (!name.equals(locationMaster.getName())) {
                log.error(SUBMODULE + "Location Master name mismatch found for:: " + locationMaster.getName());
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Location Master name mismatch found for:: " + locationMaster.getName(), null);
            }
        } catch (IllegalArgumentException e) {
            log.error(
                    APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Validate city data.
     * @param row the row
     * @param city the city
     * @throws NoRecordFoundException the no record found exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    private void validateCityData(Row row, City city) throws NoRecordFoundException, IllegalArgumentException {
        String SUBMODULE = getModuleNameForLog() + " [validateCityData()] ";
        try {
            Cell nameCell = row.getCell(1);
            if (nameCell == null || nameCell.getCellType() != CellType.STRING) {
                log.error(SUBMODULE + "Invalid name in City sheet at row " + (row.getRowNum() + 1));
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid name in City sheet at row " + (row.getRowNum() + 1), null);
            }
            String name = nameCell.getStringCellValue().trim();
            if (!name.equals(city.getName())) {
                log.error(SUBMODULE + "City name mismatch found for:: " + city.getName());
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "City name mismatch found for:: " + city.getName(), null);
            }
        } catch (IllegalArgumentException e) {
            log.error(
                    APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw e;
        }
    }

    /**
     * Gets pincode map.
     * @return the pincode map
     */
    private Map<Long, Pincode> getPincodeMap() {
        return pincodeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        pincode -> pincode.getId(),
                        pincode -> pincode
                ));
    }

    /**
     * Gets location master map.
     * @return the location master map
     */
    private Map<Long, LocationMaster> getLocationMasterMap() {
        return locationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        locationMaster -> locationMaster.getLocationMasterId(),
                        locationMaster -> locationMaster
                ));
    }

    /**
     * Gets city map.
     * @return the city map
     */
    private Map<Long, City> getCityMap() {
        return cityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        city -> city.getId().longValue(),
                        city -> city
                ));
    }

    /**
     * Validate master sheet.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @throws IllegalArgumentException the illegal argument exception
     */
    private void validateMasterSheet(Workbook workbook, Integer mvnoId) throws IllegalArgumentException {
        String SUBMODULE = getModuleNameForLog() + " [validateMasterSheet()] ";
        try {
            Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
            if (masterSheet == null) {
                log.error(SUBMODULE + "ServiceAreaSheet not found in the uploaded file.");
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ServiceAreaSheet not found in the uploaded file.", null);
            }
            Map<String, Integer> headerMap = getHeaderMap(masterSheet);
            Map<String, Set<String>> validValuesMap = new HashMap<>();
            validValuesMap.put(BulkManagementConstant.ColumnName.CITYID, getValidValuesFromSubsheet(workbook, BulkManagementConstant.SheetNames.CITY_SHEET, BulkManagementConstant.MapData.CITY_NAME));
            validValuesMap.put(BulkManagementConstant.ColumnName.LOCATION, getValidValuesFromSubsheet(workbook, BulkManagementConstant.SheetNames.LOCATION_SHEET, BulkManagementConstant.MapData.LOCATION_NAME));
            validValuesMap.put(BulkManagementConstant.ColumnName.PINCODE, getValidValuesFromSubsheet(workbook, BulkManagementConstant.SheetNames.PINCODE_SHEET, BulkManagementConstant.MapData.PINCODE));
            if (mvnoId != 1) {
                validValuesMap.put(BulkManagementConstant.ColumnName.SITENAME, getValidValuesFromSubsheet(workbook, BulkManagementConstant.SheetNames.SITE_SHEET, BulkManagementConstant.MapData.SITE_NAME));
            }
            for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
                Row row = masterSheet.getRow(rowIndex);
                if (row != null) {
                    Map<String, City> cityMap = loadCityData(workbook.getSheet(BulkManagementConstant.SheetNames.CITY_SHEET));
                    Map<String, Pincode> pincodeMap = loadPinCode(workbook.getSheet(BulkManagementConstant.SheetNames.PINCODE_SHEET));
                    validateCityPincodeData(row, cityMap, pincodeMap);
                    validateRow(row, rowIndex, headerMap, validValuesMap, mvnoId);
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (IllegalArgumentException e) {
            log.error(APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error(APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Gets valid values from subsheet.
     * @param workbook the workbook
     * @param sheetName the sheet name
     * @param columnName the column name
     * @return the valid values from subsheet
     */
    private Set<String> getValidValuesFromSubsheet(Workbook workbook, String sheetName, String columnName) {
        String SUBMODULE = getModuleNameForLog() + " [getValidValuesFromSubsheet()] ";
        Set<String> validValues = new HashSet<>();
        Sheet subsheet = workbook.getSheet(sheetName);
        if (subsheet == null) {
            log.error(SUBMODULE + "Subsheet " + sheetName + " not found.");
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Subsheet " + sheetName + " not found", null);
        }
        Row headerRow = subsheet.getRow(0);
        int columnIndex = -1;
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equals(columnName)) {
                columnIndex = cell.getColumnIndex();
                break;
            }
        }
        if (columnIndex == -1) {
            log.error(SUBMODULE + "Column " + columnName + " not found in " + sheetName);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Column " + columnName + " not found in " + sheetName, null);
        }
        for (int i = 1; i <= subsheet.getLastRowNum(); i++) {
            Row row = subsheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    validValues.add(cell.getStringCellValue().trim());
                }
            }
        }
        return validValues;
    }


    /**
     * Gets header map.
     * @param masterSheet the master sheet
     * @return the header map
     */
    private Map<String, Integer> getHeaderMap(Sheet masterSheet) {
        Row headerRow = masterSheet.getRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }
        return headerMap;
    }


    /**
     * Validate row.
     * @param row the row
     * @param rowIndex the row index
     * @param headerMap the header map
     * @param validValuesMap the valid values map
     * @param mvnoId the mvno id
     */
    private void validateRow(Row row, int rowIndex, Map<String, Integer> headerMap, Map<String, Set<String>> validValuesMap, Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [validateRow()] ";
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            String header = entry.getKey();
            int colIndex = entry.getValue();
            Cell cell = row.getCell(colIndex);
            String cellValue = getCellValueForValidateRow(row, colIndex);
            if (header.equals(BulkManagementConstant.SourceMasterColumn.NAME)
                    || header.equals(BulkManagementConstant.SourceMasterColumn.CITYID)
                    || header.equals(BulkManagementConstant.SourceMasterColumn.PINCODE_ID)
                    || header.equals(BulkManagementConstant.SourceMasterColumn.STATUS)) {
                if (isCellEmpty(cell)) {
                    log.error(APIConstants.LogConstant.FETCH_TYPE,
                            APIConstants.LogConstant.FAIL_STATUS,
                            HttpStatus.EXPECTATION_FAILED.value(),
                            "Empty cell found at row " + (rowIndex + 1) +
                                    ", column " + header
                    );
                    log.error(SUBMODULE + "Empty cell found at row " + (rowIndex + 1) + ", column " + header);
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Empty cell found at row " + (rowIndex + 1) + ", column " + header, null);
                }
            }
            switch (header) {
                case BulkManagementConstant.ColumnName.CITYID:
                case BulkManagementConstant.ColumnName.LOCATION:
                case BulkManagementConstant.ColumnName.PINCODE:
                    validateDropdownValue(
                            cellValue,
                            validValuesMap.get(header),
                            getColumnName(colIndex),
                            rowIndex,
                            header,
                            BulkManagementConstant.ColumnName.SERVICEAREA_TYPE
                    );
                    break;
                case BulkManagementConstant.ColumnName.SITENAME:
                    if (mvnoId != 1) {
                        validateDropdownValue(cellValue, validValuesMap.get(header), getColumnName(colIndex), rowIndex, header, header);
                    }
                    break;
                case BulkManagementConstant.ColumnName.STATUS:
                    validateActiveInactive(cellValue, getColumnName(colIndex), rowIndex);
                    break;
                case BulkManagementConstant.ColumnName.SERVICEAREA_TYPE:
                    validatePrivatePublic(cellValue, getColumnName(colIndex), rowIndex);
                    break;
                case BulkManagementConstant.ColumnName.RADIUS:
                    validateRadius(row, headerMap.get("radius"), headerMap.get("latitude"), headerMap.get("longitude"), headerMap.get("name"));
                    break;
                case BulkManagementConstant.ColumnName.BLOCK_NUMBER:
                    validateBlockNumber(row, headerMap.get("service_area_type"), headerMap.get("blockno"), headerMap.get("name"));
                    break;
            }
        }
    }

    /**
     * Validate dropdown value.
     * @param value the value
     * @param validValues the valid values
     * @param column the column
     * @param rowIndex the row index
     * @param dropdownName the dropdown name
     * @param serviceAreaColumn the service area column
     */
    private void validateDropdownValue(String value, Set<String> validValues, String column,
                                       int rowIndex, String dropdownName, String serviceAreaColumn) {
        String SUBMODULE = getModuleNameForLog() + " [validateDropdownValue()] ";
        if (!validValues.contains(value) && value != null && !value.trim().isEmpty()) {
            log.error(SUBMODULE + "Invalid value in column " + column + " at row " + (rowIndex + 1) +
                    ". Must be a value Selected from " + dropdownName + " Drop-Down and it's Case Sensitive");
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid value in column '" + column + "' at row " + (rowIndex + 1) + ". Select a case-sensitive value from the '" + dropdownName + "' dropdown.", null);
        }
    }

    /**
     * Is cell empty boolean.
     * @param cell the cell
     * @return the boolean
     */
    public boolean isCellEmpty(Cell cell) {
        if (cell == null) {
            return true;
        }
        return cell.getCellType() == CellType.BLANK ||
                (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty());
    }

    /**
     * Gets column name.
     * @param colIndex the col index
     * @return the column name
     */
    public String getColumnName(int colIndex) {
        return CellReference.convertNumToColString(colIndex);
    }


    /**
     * Gets cell value for validate row.
     * @param row the row
     * @param colIndex the col index
     * @return the cell value for validate row
     */
    private String getCellValueForValidateRow(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                default:
                    return "";
            }
        }
        return "";
    }

    /**
     * Validate active inactive.
     * @param value the value
     * @param column the column
     * @param rowIndex the row index
     */
    private void validateActiveInactive(String value, String column, int rowIndex) {
        String SUBMODULE = getModuleNameForLog() + " [validateActiveInactive()] ";
        if (!value.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.ACTIVE) &&
                !value.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.INACTIVE) &&
                !value.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.UNDER_DEVELOPMENT) &&
                value != null && !value.trim().isEmpty()) {
            log.error(SUBMODULE + "Invalid value in column " + column + " at row " + (rowIndex + 1) +
                    ". Must be either 'Active' or 'Inactive'");
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid value in column " + column + " at row " + (rowIndex + 1) +
                    ". Must be either 'Active' or 'Inactive'", null);
        }
    }

    /**
     * Validate private public.
     * @param value the value
     * @param column the column
     * @param rowIndex the row index
     */
    private void validatePrivatePublic(String value, String column, int rowIndex) {
        String SUBMODULE = getModuleNameForLog() + " [validatePrivatePublic()] ";
        if (!value.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.PUBLIC) &&
                !value.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.PRIVATE) &&
                value != null && !value.trim().isEmpty()) {
            log.error(SUBMODULE + "Invalid value in column " + column + " at row " + (rowIndex + 1) +
                    ". Must be either 'Public' or 'Private'");
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Invalid value in column " + column + " at row " + (rowIndex + 1) +
                    ". Must be either 'Public' or 'Private'", null);
        }
    }

    /**
     * Save data to source master in bulk.
     * @param workbook the workbook
     * @param mvnoId the mvno id
     * @param loggedInUserId the logged in user id
     * @param loggedInUserName the logged in user name
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDataToSourceMasterInBulk(Workbook workbook, Integer mvnoId, Integer loggedInUserId, String loggedInUserName) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveDataToSourceMasterInBulk()] ";
        Map<String, City> cityMap = loadCityData(workbook.getSheet(BulkManagementConstant.SheetNames.CITY_SHEET));
        Map<String, LocationMaster> locationMAp = loadLocationData1(workbook.getSheet(BulkManagementConstant.SheetNames.LOCATION_SHEET));
        Map<String, Pincode> pincodeMap = loadPinCode(workbook.getSheet(BulkManagementConstant.SheetNames.PINCODE_SHEET));
        Map<String, String> siteNameData = loadSiteName(workbook.getSheet(BulkManagementConstant.SheetNames.SITE_SHEET), mvnoId);
        List<ServiceArea> serviceAreaList = processSourceMasters(workbook, cityMap, locationMAp,
                pincodeMap, siteNameData, mvnoId,
                loggedInUserId, loggedInUserName);

        try {
            for (ServiceArea serviceArea : serviceAreaList) {
                /** Check for existing ServiceArea with the same name and mvnoId */
                Optional<ServiceArea> existingServiceAreaOpt = serviceAreaRepository.findAllByNameAndMvnoIdAndIsDeletedIsFalse(
                        serviceArea.getName(), serviceArea.getMvnoId()
                );
                ServiceArea savedServiceArea;
                if (Objects.isNull(serviceArea.getSiteName()) || serviceArea.getSiteName().equalsIgnoreCase("")) {
                    serviceArea.setSiteName(serviceArea.getName());
                }
                if (existingServiceAreaOpt.isPresent()) {
                    /** Update existing Service Area */
                    ServiceArea updatedServiceArea = updateServiceArea(existingServiceAreaOpt.get(), serviceArea);
                    savedServiceArea = saveEntity(updatedServiceArea);
                } else {
                    /** Save new Service Area */
                    savedServiceArea = saveEntity(serviceArea);
                }
                /** Convert Entity to DTO */
                ServiceAreaDTO dto = convertEntityToDto(serviceArea);
                /** Set Data For Service Area Excluding Polygone */
                ServiceAreaDTO serviceAreaDTO = serviceAreaService.setDataforServicAreaExcludingPolygone(dto);
                ServiceAreaDTO savedServiceAreaDto = convertSavedEntityToDto(savedServiceArea);

                /** Handle polygon data */
                if (dto.getSiteName() != null && dto.getPolyGoneList() != null && dto.getPolyGoneList().get(0).getPolygoneName()!=null) {
                    boolean polygonExists = serviceAreaRepository.existsBySiteNameAndMvnoId(dto.getSiteName(), mvnoId);
                    boolean sameNamePolygoneExist = polyGoneRepository.existsByPolygoneNameAndMvnoidAndServiceAreaId(dto.getPolyGoneList().get(0).getPolygoneName(),dto.getMvnoId(),dto.getId().intValue());
                    if (!polygonExists && !sameNamePolygoneExist) {
                        serviceAreaService.savePoliGonList(dto, savedServiceAreaDto);
                    }
                }
                boolean staffServiceMap = false;
                /** Save Staff User Service Area Mapping */
                staffServiceMap = serviceAreaService.saveStaffUserServiceAreaMappingForBulk(savedServiceAreaDto, staffServiceMap);
                /** Send Service Area To Other Microservice */
                sendServiceAreaToOtherMicroserviceWhenSave(savedServiceAreaDto, savedServiceArea, staffServiceMap);
                /** Save MVNOIDs List */
                serviceAreaService.saveMVNOIdsList(savedServiceAreaDto, staffServiceMap);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error(APIConstants.LogConstant.FETCH_TYPE,
                    APIConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update service area service area.
     * @param existing the existing
     * @param updatedData the updated data
     * @return the service area
     */
    private ServiceArea updateServiceArea(ServiceArea existing, ServiceArea updatedData) {
        ServiceArea serviceArea = new ServiceArea();
        serviceArea.setId(existing.getId());
        serviceArea.setName(updatedData.getName());
        serviceArea.setServiceAreaType(updatedData.getServiceAreaType());
        serviceArea.setBlockNo(updatedData.getBlockNo());
        serviceArea.setAreaId(existing.getAreaId());
        serviceArea.setRadius(updatedData.getRadius());
        serviceArea.setLatitude(updatedData.getLatitude());
        serviceArea.setLongitude(updatedData.getLongitude());
        serviceArea.setLocations(updatedData.getLocations());
        serviceArea.setStatus(updatedData.getStatus());
        serviceArea.setCityid(updatedData.getCityid());
        serviceArea.setPincodeList(updatedData.getPincodeList());
        serviceArea.setSiteName(updatedData.getSiteName());
        serviceArea.setCreatedById(existing.getCreatedById());
        serviceArea.setLastModifiedById(existing.getLastModifiedById());
        serviceArea.setCreatedByName(existing.getCreatedByName());
        serviceArea.setLastModifiedByName(existing.getLastModifiedByName());
        serviceArea.setMvnoId(existing.getMvnoId());
        return serviceArea;
    }

    /**
     * Convert saved entity to dto service area dto.
     * @param savedServiceArea the saved service area
     * @return the service area dto
     */
    private ServiceAreaDTO convertSavedEntityToDto(ServiceArea savedServiceArea) {
        String SUBMODULE = getModuleNameForLog() + " [convertSavedEntityToDto()] ";
        try {
            ServiceAreaDTO serviceAreaDTO = convertEntityToDto(savedServiceArea);
            ServiceAreaMesseage serviceAreaMesseage = new ServiceAreaMesseage(serviceAreaDTO);
            kafkaMessageSender.send(new KafkaMessageData(serviceAreaMesseage, serviceAreaMesseage.getClass().getSimpleName()));
            return serviceAreaDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Send service area to other microservice when save.
     * @param savedServiceAreaDto the saved service area dto
     * @param serviceArea the service area
     * @param staffServiceMap the staff service map
     */
    private void sendServiceAreaToOtherMicroserviceWhenSave(ServiceAreaDTO savedServiceAreaDto, ServiceArea serviceArea, boolean staffServiceMap) {
        ServiceareaMessage serviceareaMessage = new ServiceareaMessage();
        serviceareaMessage.setId(savedServiceAreaDto.getId());
        serviceareaMessage.setName(savedServiceAreaDto.getName());
        serviceareaMessage.setStatus(savedServiceAreaDto.getStatus());
        serviceareaMessage.setIsDeleted(savedServiceAreaDto.getIsDeleted());
        serviceareaMessage.setMvnoId(savedServiceAreaDto.getMvnoId());
        serviceareaMessage.setLatitude(savedServiceAreaDto.getLatitude());
        serviceareaMessage.setLongitude(savedServiceAreaDto.getLongitude());
        serviceareaMessage.setAreaId(savedServiceAreaDto.getAreaid());
        serviceareaMessage.setSiteName(savedServiceAreaDto.getSiteName());
        kafkaMessageSender.send(new KafkaMessageData(serviceareaMessage, serviceareaMessage.getClass().getSimpleName()));
        ServiceArea serviceAreaEntity = serviceArea;
        List<Pincode> pincodeList = new ArrayList<>();
        for (Integer pincode : savedServiceAreaDto.getPincodes()) {
            Optional<Pincode> optionalPincode = pincodeRepository.findById(Long.valueOf(pincode));
            if (optionalPincode.isPresent()) {
                Pincode pincodeEntity = optionalPincode.get();
                pincodeEntity.setCreatedate(LocalDateTime.now());
                pincodeEntity.setUpdatedate(LocalDateTime.now());
                pincodeList.add(pincodeEntity);
            }
        }
        serviceAreaEntity.setPincodeList(pincodeList);
        serviceAreaEntity.setLocationIdList(savedServiceAreaDto.getLocationIds());
        sendCreatedServiceAreaData(serviceAreaEntity, staffServiceMap);
    }

    /**
     * Send created service area data.
     * @param object the object
     * @param staffSAMap the staff sa map
     */
    public void sendCreatedServiceAreaData(Object object, boolean staffSAMap) {
        if (Objects.nonNull(object)) {
            SaveServiceAreaSharedDataMessge saveServiceAreaSharedDataMessge = new SaveServiceAreaSharedDataMessge();
            saveServiceAreaSharedDataMessge.setId(((ServiceArea) object).getId());
            saveServiceAreaSharedDataMessge.setAreaId(((ServiceArea) object).getAreaId());
            saveServiceAreaSharedDataMessge.setCityid(((ServiceArea) object).getCityid());
            saveServiceAreaSharedDataMessge.setLongitude(((ServiceArea) object).getLongitude());
            saveServiceAreaSharedDataMessge.setLatitude(((ServiceArea) object).getLatitude());
            saveServiceAreaSharedDataMessge.setName(((ServiceArea) object).getName());
            saveServiceAreaSharedDataMessge.setIsDeleted(((ServiceArea) object).getIsDeleted());
            saveServiceAreaSharedDataMessge.setPincodeList(((ServiceArea) object).getPincodeList());
            saveServiceAreaSharedDataMessge.setLocationIdList(((ServiceArea) object).getLocationIdList());
            saveServiceAreaSharedDataMessge.setMvnoId(((ServiceArea) object).getMvnoId());
            saveServiceAreaSharedDataMessge.setStatus(((ServiceArea) object).getStatus());
            saveServiceAreaSharedDataMessge.setCreatedById(((ServiceArea) object).getCreatedById());
            saveServiceAreaSharedDataMessge.setStaffSAMap(staffSAMap);
            saveServiceAreaSharedDataMessge.setCreatedById(((ServiceArea) object).getCreatedById());
            saveServiceAreaSharedDataMessge.setCreatedByName(((ServiceArea) object).getCreatedByName());
            saveServiceAreaSharedDataMessge.setLastModifiedByName(((ServiceArea) object).getLastModifiedByName());
            saveServiceAreaSharedDataMessge.setSiteName(((ServiceArea) object).getSiteName());

            if (saveServiceAreaSharedDataMessge.getPincodeList() != null && !saveServiceAreaSharedDataMessge.getPincodeList().isEmpty()) {
                saveServiceAreaSharedDataMessge.getPincodeList().stream().forEach(x -> {
                    x.setCreatedate(null);
                    x.setUpdatedate(null);
                    x.getAreaList().stream().forEach(area -> {
                        area.setCreatedate(null);
                        area.setUpdatedate(null);
                    });
                });
            }
            kafkaMessageSender.send(new KafkaMessageData(saveServiceAreaSharedDataMessge, saveServiceAreaSharedDataMessge.getClass().getSimpleName()));
        }
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    public String getModuleNameForLog() {
        return "[BulkUploadServiceArea]";
    }

    /**
     * Save entity service area.
     * @param serviceArea the service area
     * @return the service area
     */
    private ServiceArea saveEntity(ServiceArea serviceArea) {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            ServiceArea savedServiceArea = serviceAreaRepository.save(serviceArea);
            return savedServiceArea;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Convert entity to dto service area dto.
     * @param serviceArea the service area
     * @return the service area dto
     */
    private ServiceAreaDTO convertEntityToDto(ServiceArea serviceArea) {
        ServiceAreaDTO serviceAreaDTO = new ServiceAreaDTO();
        serviceAreaDTO.setId(serviceArea.getId());
        serviceAreaDTO.setName(serviceArea.getName());
        serviceAreaDTO.setStatus(serviceArea.getStatus());
        serviceAreaDTO.setIsDeleted(false);
        serviceAreaDTO.setCreatedById(serviceArea.getCreatedById());
        serviceAreaDTO.setLastModifiedById(serviceArea.getLastModifiedById());
        serviceAreaDTO.setCityid(serviceArea.getCityid());
        if (!serviceArea.getLocations().isEmpty()) {
            Set<LocationMaster> locationMaster = serviceArea.getLocations();
            List<Long> locationIds = new ArrayList<>();
            if (!locationMaster.isEmpty() && locationMaster.size() > 0) {
                for (LocationMaster master : locationMaster) {
                    locationIds.add(master.getLocationMasterId());
                }
            }
            serviceAreaDTO.setLocationIds(locationIds);
        }
        if (!serviceArea.getPincodeList().isEmpty()) {
            List<Pincode> pincodeList = serviceArea.getPincodeList();
            List<Integer> pincodeIds = new ArrayList<>();
            if (!pincodeList.isEmpty()) {
                for (Pincode pincode : pincodeList) {
                    pincodeIds.add(pincode.getId().intValue());
                }
            }
            serviceAreaDTO.setPincodes(pincodeIds);
        }
        serviceAreaDTO.setRadius(serviceArea.getRadius());
        serviceAreaDTO.setSiteName(serviceArea.getSiteName());
        serviceAreaDTO.setServiceAreaType(serviceArea.getServiceAreaType());
        serviceAreaDTO.setBlockNo(serviceArea.getBlockNo());
        serviceAreaDTO.setMvnoId(serviceArea.getMvnoId());
        return serviceAreaDTO;
    }

    /**
     * Load city data map.
     * @param sheet the sheet
     * @return the map
     */
    private Map<String, City> loadCityData(Sheet sheet) {
        Map<String, City> cityMap = new HashMap<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                String cityId = getExcelCellValue(row.getCell(0));
                String cityname = getExcelCellValue(row.getCell(1));
                if (cityId != null &&
                        !cityId.trim().isEmpty() &&
                        cityname != null &&
                        !cityname.trim().isEmpty()) {
                    Optional<City> city = cityRepository.findAllByIdAndName(Integer.valueOf(cityId), cityname);
                    cityMap.put(cityname, city.get());
                }
            }
        }
        return cityMap;
    }

    /**
     * Load location data 1 map.
     * @param sheet the sheet
     * @return the map
     */
    private Map<String, LocationMaster> loadLocationData1(Sheet sheet) {
        Map<String, LocationMaster> locationnMap = new HashMap<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                String locationId = getExcelCellValue(row.getCell(0));
                String locationName = getExcelCellValue(row.getCell(1));
                if (locationId != null &&
                        !locationId.trim().isEmpty() &&
                        locationName != null &&
                        !locationName.trim().isEmpty()) {
                    Optional<LocationMaster> location = locationRepository.findAllByLocationMasterIdAndName(Long.parseLong(locationId), locationName);
                    locationnMap.put(locationName, location.get());
                }
            }
        }
        return locationnMap;
    }

    /**
     * Load pin code map.
     * @param sheet the sheet
     * @return the map
     */
    private Map<String, Pincode> loadPinCode(Sheet sheet) {
        Map<String, Pincode> pincodeMap = new HashMap<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                String pincodeId = getExcelCellValue(row.getCell(0));
                String pincodeName = getExcelCellValue(row.getCell(1));
                if (pincodeId != null &&
                        !pincodeId.trim().isEmpty() &&
                        pincodeName != null &&
                        !pincodeName.trim().isEmpty()) {
                    Optional<Pincode> pincode = pincodeRepository.findAllByIdAndPincode(Long.valueOf(pincodeId), pincodeName);
                    pincodeMap.put(pincodeName, pincode.get());
                }
            }
        }
        return pincodeMap;
    }

    /**
     * Load site name map.
     * @param sheet the sheet
     * @param mvnoId the mvno id
     * @return the map
     */
    private Map<String, String> loadSiteName(Sheet sheet, Integer mvnoId) {
        Map<String, String> siteMap = new HashMap<>();
        if (mvnoId != 1) {
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    String siteName = getExcelCellValue(row.getCell(0));
                    siteMap.put(siteName, siteName);
                }
            }
        }
        return siteMap;
    }

    /**
     * Gets excel cell value.
     * @param cell the cell
     * @return the excel cell value
     */
    private String getExcelCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getCellType() == CellType.NUMERIC ?
                String.valueOf((int) cell.getNumericCellValue()) :
                cell.getStringCellValue();
    }

    /**
     * Gets excel cell value double.
     * @param cell the cell
     * @return the excel cell value double
     */
    private String getExcelCellValueDouble(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getCellType() == CellType.NUMERIC ?
                String.valueOf((double) cell.getNumericCellValue()) :
                cell.getStringCellValue();
    }

    /**
     * Process source masters list.
     * @param workbook the workbook
     * @param cityMap the city map
     * @param locationMasterMap the location master map
     * @param pincodeMap the pincode map
     * @param siteNameData the site name data
     * @param mvnoId the mvno id
     * @param loggedInUserId the logged in user id
     * @param loggedInUserName the logged in user name
     * @return the list
     * @throws Exception the exception
     */
    private List<ServiceArea> processSourceMasters(Workbook workbook, Map<String, City> cityMap,
                                                   Map<String, LocationMaster> locationMasterMap, Map<String, Pincode> pincodeMap,
                                                   Map<String, String> siteNameData, Integer mvnoId, Integer loggedInUserId, String loggedInUserName) throws Exception {
        List<ServiceArea> sourceMasters = new ArrayList<>();
        Sheet masterSheet = workbook.getSheet(BulkManagementConstant.SheetNames.SERVICE_AREA_SHEET);
        for (int rowIndex = 1; rowIndex <= masterSheet.getLastRowNum(); rowIndex++) {
            Row row = masterSheet.getRow(rowIndex);
            if (row != null) {
                ServiceArea sourceMaster = createSourceMaster(row, cityMap, locationMasterMap,
                        locationMasterMap, pincodeMap, siteNameData, mvnoId, loggedInUserId, loggedInUserName);
                sourceMasters.add(sourceMaster);
            }
        }
        return sourceMasters;
    }

    /**
     * Validate city pincode data.
     * @param row the row
     * @param cityMap the city map
     * @param pincodeMap the pincode map
     * @throws Exception the exception
     */
    private void validateCityPincodeData(Row row, Map<String, City> cityMap, Map<String, Pincode> pincodeMap) throws Exception {
        final String SUBMODULE = getModuleNameForLog() + " [validateCityPincodeData()] ";
        /** Extract city name and pincode from the Excel row */
        String cityName = getExcelCellValue(row.getCell(4));
        String pincodeName = getExcelCellValue(row.getCell(10));
        /** Validate presence of city in the map */
        City city = cityMap.get(cityName);
        /** Validate presence of pincode in the map */
        Pincode pincode = pincodeMap.get(pincodeName);
        if (pincode != null) {
            /** Fetch city details using city ID from the pincode */
            Optional<City> cityOptional = cityRepository.findById(pincode.getCityId());
            /** Validate if the pincode belongs to the city */
            City fetchedCity = cityOptional.get();
            if (!fetchedCity.getName().trim().equals(cityName.trim())) {
                log.error(SUBMODULE + "Invalid mapping: Pincode '" + pincodeName + "' does not match city '" + cityName
                        + "' at row " + (row.getRowNum() + 1));
                throw new CustomValidationException(
                        HttpStatus.EXPECTATION_FAILED.value(),
                        "Invalid mapping: Pincode '" + pincodeName + "' does not match city '" + cityName
                                + "' at row " + (row.getRowNum() + 1),
                        null
                );
            }
        } else {
            log.error(SUBMODULE + "Missing Pincode: Please enter a valid pincode at row " + (row.getRowNum() + 1));
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Missing Pincode: Please enter a valid pincode at row " + (row.getRowNum() + 1),
                    null
            );
        }
    }

    /**
     * Create source master service area.
     * @param row the row
     * @param cityMap the city map
     * @param locationMasterMap the location master map
     * @param masterMap the master map
     * @param pincodeMap the pincode map
     * @param siteNameData the site name data
     * @param mvnoId the mvno id
     * @param loggedInUserId the logged in user id
     * @param loggedInUserName the logged in user name
     * @return the service area
     */
    private ServiceArea createSourceMaster(Row row, Map<String, City> cityMap,
                                           Map<String, LocationMaster> locationMasterMap,
                                           Map<String, LocationMaster> masterMap,
                                           Map<String, Pincode> pincodeMap,
                                           Map<String, String> siteNameData, Integer mvnoId, Integer loggedInUserId,
                                           String loggedInUserName) {
        ServiceArea sourceMaster = new ServiceArea();
        /** Set Service Area Name */
        sourceMaster.setName(getExcelCellValue(row.getCell(0)));
        /** Set Status */
        sourceMaster.setStatus(getExcelCellValue(row.getCell(1)));
        /** Set Latitude */
        sourceMaster.setLatitude(getExcelCellValue(row.getCell(2)));
        /** Set Longitude */
        sourceMaster.setLongitude(getExcelCellValue(row.getCell(3)));
        /** Set City */
        setCityDetails(sourceMaster, row, cityMap);
        /** Set Radius */
        sourceMaster.setRadius(getExcelCellValueDouble(row.getCell(5)));
        /** Set Site Name */
        if (mvnoId != 1) {
            SetSiteNameDetails(sourceMaster, row, siteNameData);
        } else {
            sourceMaster.setSiteName(getExcelCellValue(row.getCell(6)));
        }
        /** Set Service Area Type */
        String serviceAreaType = getExcelCellValue(row.getCell(7));
        sourceMaster.setServiceAreaType(serviceAreaType);
        if (serviceAreaType.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.PRIVATE)) {
            /** Set Block Number */
            sourceMaster.setBlockNo(getExcelCellValue(row.getCell(8)));
        }
        /** Set Location */
        setLocationDetails(sourceMaster, row, locationMasterMap);
        /** Set Pincode */
        SetPincodeDetails(sourceMaster, row, pincodeMap);
        /** Set MvnoId */
        sourceMaster.setMvnoId(Math.toIntExact(mvnoId));
        /** Set Created By Id */
        sourceMaster.setCreatedById(loggedInUserId);
        /** Set Created By Nmae */
        sourceMaster.setCreatedByName(loggedInUserName);
        /** Set Updated By Id */
        sourceMaster.setLastModifiedById(loggedInUserId);
        /** Set Updated By Name */
        sourceMaster.setLastModifiedByName(loggedInUserName);
        return sourceMaster;
    }

    /**
     * Set site name details.
     * @param sourceMaster the source master
     * @param row the row
     * @param siteNameData the site name data
     */
    private void SetSiteNameDetails(ServiceArea sourceMaster, Row row, Map<String, String> siteNameData) {
        String siteName = getExcelCellValue(row.getCell(6));
        sourceMaster.setSiteName(siteNameData.get(siteName));
    }

    /**
     * Sets city details.
     * @param sourceMaster the source master
     * @param row the row
     * @param cityMap the city map
     */
    private void setCityDetails(ServiceArea sourceMaster, Row row, Map<String, City> cityMap) {
        String cityName = getExcelCellValue(row.getCell(4));
        City city = cityMap.get(cityName);
        sourceMaster.setCityid(Long.valueOf(city.getId()));
    }

    /**
     * Sets location details.
     * @param sourceMaster the source master
     * @param row the row
     * @param locationMasterMap the location master map
     */
    private void setLocationDetails(ServiceArea sourceMaster, Row row,
                                    Map<String, LocationMaster> locationMasterMap) {
        String locationName = getExcelCellValue(row.getCell(9));
        LocationMaster location = locationMasterMap.get(locationName);
        if (location != null) {
            sourceMaster.setLocations(Collections.singleton(location));
        }
    }

    /**
     * Set pincode details.
     * @param sourceMaster the source master
     * @param row the row
     * @param pincodeMap the pincode map
     */
    private void SetPincodeDetails(ServiceArea sourceMaster, Row row, Map<String, Pincode> pincodeMap) {
        String pincodeName = getExcelCellValue(row.getCell(10));
        Pincode pincode = pincodeMap.get(pincodeName);
        sourceMaster.setPincodeList(Collections.singletonList(pincode));
    }

    /**
     * Gets mvno id from current staff.
     * @return the mvno id from current staff
     */
    public Integer getMvnoIdFromCurrentStaff() {
        String SUBMODULE = getModuleNameForLog() + " [getMvnoIdFromCurrentStaff()] ";
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            e.getStackTrace();
            log.error(SUBMODULE + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return mvnoId;
    }

    /**
     * Gets logged in user id.
     * @return the logged in user id
     */
    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    /**
     * Gets logged in user name.
     * @param loggedInUserId the logged in user id
     * @return the logged in user name
     */
    public String getLoggedInUserName(Integer loggedInUserId) {
        String loggedInUserName = null;
        try {
            Optional<StaffUser> staffUser = staffUserRepository.findById(loggedInUserId);
            loggedInUserName = staffUser.get().getUsername();
        } catch (Exception e) {
            loggedInUserName = null;
        }
        return loggedInUserName;
    }

    /**
     * This will be validate service area data by given condition
     * @param serviceArea the service area
     */
    private void validateSheetServiceArea(ServiceArea serviceArea) {
        if (Objects.nonNull(serviceArea.getRadius())) {
            if ((Objects.isNull(serviceArea.getLatitude()) || serviceArea.getLatitude().equalsIgnoreCase(" ") || serviceArea.getLatitude().isEmpty())
                    || (Objects.isNull(serviceArea.getLongitude()) || serviceArea.getLongitude().equalsIgnoreCase(" ") || serviceArea.getLongitude().isEmpty())
            )
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ServiceArea: " + serviceArea.getName() + " requires longitude and latitude for its radius.", null);
        }
        if (serviceArea.getServiceAreaType().equalsIgnoreCase(BulkManagementConstant.DropdownStatus.PRIVATE)
                && (Objects.isNull(serviceArea.getBlockNo()) || serviceArea.getBlockNo().equalsIgnoreCase(" ") || serviceArea.getBlockNo().isEmpty())) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ServiceArea: " + serviceArea.getName() + " requires block number as it is private.", null);
        }

    }

    /**
     * Validate radius.
     * @param row the row
     * @param radius the radius
     * @param latitude the latitude
     * @param longitude the longitude
     * @param servicearea the servicearea
     */
    private void validateRadius(Row row, int radius, int latitude, int longitude, int servicearea) {
        String radiusValue = getCellValueForValidateRow(row, radius);
        String latitudeValue = getCellValueForValidateRow(row, latitude);
        String longitudeValue = getCellValueForValidateRow(row, longitude);
        String serviceAreaValue = getCellValueForValidateRow(row, servicearea);

        if ((radiusValue != null && !radiusValue.isEmpty())
                && ((latitudeValue == null || latitudeValue.isEmpty())
                || (longitudeValue == null || longitudeValue.isEmpty()))
        ) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ServiceArea: " + serviceAreaValue + " requires longitude and latitude for its radius.", null);
        }
    }

    /**
     * Validate block number.
     * @param row the row
     * @param serviceAreaType the service area type
     * @param blockNo the block no
     * @param servicearea the servicearea
     */
    private void validateBlockNumber(Row row, int serviceAreaType, int blockNo, int servicearea) {
        String serviceAreaTypeValue = getCellValueForValidateRow(row, serviceAreaType);
        String blockNoValue = getCellValueForValidateRow(row, blockNo);
        String serviceAreaValue = getCellValueForValidateRow(row, servicearea);

        if (serviceAreaTypeValue.equalsIgnoreCase(BulkManagementConstant.DropdownStatus.PRIVATE) && (blockNoValue == null || blockNoValue.isEmpty())) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "ServiceArea: " + serviceAreaValue + " requires block no for because it is private.", null);
        }
    }
}

   







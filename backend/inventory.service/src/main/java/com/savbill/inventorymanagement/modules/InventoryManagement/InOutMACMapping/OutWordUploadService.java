package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import com.opencsv.CSVReader;
import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.GetRemarksDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemSkipped;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemSkippedRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import io.jsonwebtoken.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OutWordUploadService {

    @Autowired
    @Qualifier("outwardUploadExecutor")
    private Executor outwardUploadExecutor;

    @Autowired
    private ItemSkippedRepository itemSkippedRepository;

    @Autowired
    private OutwardRepository outwardRepository;
    @Autowired
    private InwardRepository inwardRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InOutWardMACService inOutWardMACService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClientServiceService clientServiceSrv;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;


    public Integer MAX_PAGE_SIZE;

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    private static final int CHUNK_SIZE = 1000;

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

//        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
        pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
//        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

    public GenericDataDTO getRemarksByOutwardId(Long outwardId, PaginationRequestDTO paginationRequestDTO) {

        PageRequest pageRequest = generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", paginationRequestDTO.getSortOrder());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<GetRemarksDTO> remarks = itemSkippedRepository.findRemarksByOutwardId(outwardId, Long.valueOf(getLoggedInUser().getMvnoId()), pageRequest);
        if (remarks == null || remarks.isEmpty()) {
            throw new CustomValidationException(
                    HttpStatus.OK.value(),
                    "Remarks not found",
                    null
            );
        }


        genericDataDTO.setTotalPages(remarks.getTotalPages());
        genericDataDTO.setPageRecords(remarks.getNumberOfElements());
        genericDataDTO.setTotalRecords(remarks.getTotalElements());
        genericDataDTO.setCurrentPageNumber(pageRequest.getPageNumber());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Remarks fetched successfully");
        genericDataDTO.setDataList(remarks.getContent());
        return genericDataDTO;
    }

    private Map<String, Function<ItemSkipped, String>> getFieldMapper() {

        Map<String, Function<ItemSkipped, String>> map = new HashMap<>();

        map.put("macAddress", ItemSkipped::getMac);
        map.put("serialNumber", ItemSkipped::getSerial);

        map.put("reason", ItemSkipped::getReason);

        return map;
    }

    public ByteArrayInputStream generateDynamicExcel(Long id,String type) {

        List<String> headers = new ArrayList<>();
        List<ItemSkipped> items = new ArrayList<>();
//        if(CommonConstants.INWARD.equalsIgnoreCase(type)){
//            headers = findHeaderValidatin(id);
//            items = itemSkippedRepository.findByInwardId(id);
//        } else
        if (CommonConstants.OUTWARD.equalsIgnoreCase(type)) {
            headers = findHeaderValidatin(id);
            items = itemSkippedRepository.findByOutwardId(id);
        }

        if (!headers.contains("reason")) {
            headers.add("reason");
        }
        if (!headers.contains("itemId")) {
            headers.add(0, "itemId");
        }

        if (headers.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No headers configured for Id: " + id,null);
        }

        if (items.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(),"No Failed Record Found",null);
        }

        Map<String, Function<ItemSkipped, String>> fieldMapper = getFieldMapper();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Skipped Items");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
//            headerFont.setBold(true);
//            headerStyle.setFont(headerFont);

            CellStyle textStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            textStyle.setDataFormat(format.getFormat("@"));
            textStyle.setWrapText(true);

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.size(); i++) {
                String headerKey = headers.get(i).trim();

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerKey);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;

            for (ItemSkipped item : items) {
                Row row = sheet.createRow(rowIndex++);

                for (int col = 0; col < headers.size(); col++) {
                    String headerKey = headers.get(col).trim();
                    Cell cell = row.createCell(col);

                    String value = "";

                    if ("itemId".equals(headerKey)) {
                        value = String.valueOf(rowIndex - 1);
                    } else {
                        Function<ItemSkipped, String> mapper = fieldMapper.get(headerKey);
                        if (mapper != null) {
                            String rawValue = mapper.apply(item);
                            value = rawValue != null ? rawValue.trim() : "";
                        }
                    }

                    cell.setCellStyle(textStyle);
                    cell.setCellValue(value);
                }
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1000, 15000));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException | java.io.IOException e) {
            throw new RuntimeException("Failed to generate Excel file for id: " + id, e);
        }
    }

    public void validateExcelHeadersOnly(MultipartFile file, Long id, String type) throws IOException {

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            DataFormatter formatter = new DataFormatter();

            List<String> actualHeaders = new ArrayList<>();

            for (Cell cell : headerRow) {
                String header = formatter.formatCellValue(cell);
                if (header != null && !header.trim().isEmpty()) {
                    actualHeaders.add(header.trim().toLowerCase());
                }
            }

            Set<String> uniqueHeaders = new HashSet<>(actualHeaders);
            if (uniqueHeaders.size() != actualHeaders.size()) {
                throw new IllegalArgumentException("Duplicate columns found in Excel header");
            }
            List<String> expectedHeaders;
            if (CommonConstants.OUTWARD.equalsIgnoreCase(type)) {
                expectedHeaders = findHeaderValidatin(id)
                        .stream()
                        .map(h -> h.trim().toLowerCase())
                        .collect(Collectors.toList());
            } else {
                expectedHeaders = new ArrayList<>();
            }
            expectedHeaders.add("itemid");

            List<String> missingHeaders = expectedHeaders.stream()
                    .filter(expected -> !actualHeaders.contains(expected))
                    .collect(Collectors.toList());

            if (!missingHeaders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Missing columns: " + missingHeaders
                );
            }

            List<String> extraHeaders = actualHeaders.stream()
                    .filter(actual -> !expectedHeaders.contains(actual))
                    .collect(Collectors.toList());

            if (!extraHeaders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Unexpected columns found: " + extraHeaders
                );
            }

            if (sheet.getLastRowNum() == 0) {
                throw new IllegalArgumentException("Excel file contains header but no data rows");
            }


        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Excel header validation failed: " + e.getMessage(), e);
        }
    }

    public void validateHeadersOnly(MultipartFile file, Long id, String type) throws IOException {

        try (
                InputStream is = file.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                CSVReader reader = new CSVReader(isr)
        ) {

            String[] headers = reader.readNext();

            if (headers == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            List<String> actualHeaders = Arrays.stream(headers)
                    .map(h -> h.trim().toLowerCase())
                    .collect(Collectors.toList());

            Set<String> uniqueHeaders = new HashSet<>(actualHeaders);
            if (uniqueHeaders.size() != actualHeaders.size()) {
                throw new IllegalArgumentException("Duplicate columns found in CSV header");
            }

            List<String> expectedHeaders;
            if (CommonConstants.OUTWARD.equalsIgnoreCase(type)) {
                expectedHeaders = findHeaderValidatin(id)
                        .stream()
                        .map(h -> h.trim().toLowerCase())
                        .collect(Collectors.toList());
            } else {
                expectedHeaders = new ArrayList<>();
            }
            expectedHeaders.add("itemid");

            List<String> missingHeaders = expectedHeaders.stream()
                    .filter(expected -> !actualHeaders.contains(expected))
                    .collect(Collectors.toList());

            if (!missingHeaders.isEmpty()) {
                throw new RuntimeException("Missing columns: " + missingHeaders);
            }

            List<String> extraHeaders = actualHeaders.stream()
                    .filter(actual -> !expectedHeaders.contains(actual))
                    .collect(Collectors.toList());

            if (!extraHeaders.isEmpty()) {
                throw new IllegalArgumentException("Unexpected columns found: " + extraHeaders);
            }

            String[] firstDataRow = reader.readNext();
            if (firstDataRow == null) {
                throw new IllegalArgumentException("CSV file contains header but no data rows");
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Header validation failed: " + e.getMessage(), e);
        }
    }

    public GenericDataDTO uploadOutwardFile(
            MultipartFile file,
            Long outwardId,
            Long productId,
            Long ownerId,
            String ownerType
    ) {

        GenericDataDTO response = new GenericDataDTO();
        response.setResponseMessage("File upload accepted. Processing in background.");
        response.setResponseCode(HttpStatus.OK.value());

        itemSkippedRepository.deleteByOutwardId(outwardId);

        Integer mvnoId = getLoggedInUser().getMvnoId();
        Integer userId = getLoggedInUser().getUserId();
        String fullName = getLoggedInUser().getFullName();

        try {

            byte[] fileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();

            outwardUploadExecutor.execute(() -> {
                try {
                    processOutwardFile(
                            fileBytes,
                            fileName,
                            outwardId,
                            mvnoId,
                            userId,
                            productId,
                            ownerId,
                            ownerType
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        return response;
    }

    private Set<Long> getAllowedItemIds(
            Long productId,
            Long ownerId,
            String ownerType,
            Integer mvnoId) {

        Integer currentMvnoId = mvnoId;
        boolean isSuperAdmin = currentMvnoId == 1;

        List<Integer> mvnoIds = isSuperAdmin
                ? Collections.emptyList()
                : Arrays.asList(currentMvnoId, 1);

        return new HashSet<>(
                itemRepository.findItemIdsForOutwardSim(
                        productId,
                        ownerId,
                        ownerType,
//                        "Approve",
                        mvnoIds,
                        isSuperAdmin
                )
        );
    }

    private Map<String, Long> buildIdentifierItemMap(Set<String> identifiers) {

        List<Object[]> results =
                itemRepository.findItemByImsiOrMsisdnIn(identifiers);

        Map<String, Long> map = new HashMap<>();

        for (Object[] row : results) {
            Long id = (Long) row[0];
            String mac = (String) row[1];
            if (mac != null && !mac.isEmpty())
                map.put(mac, id);
        }

        return map;
    }


    @Transactional
    public void processOutwardFile(
            byte[] fileBytes,
            String fileName,
            Long outwardId,
            Integer mvnoId,
            Integer userId,
            Long productId,
            Long ownerId,
            String ownerType) throws Exception {

        final int CHUNK_SIZE = 1000;

        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        Outward outward = outwardRepository.findById(outwardId)
                .orElseThrow(() -> new RuntimeException("Outward not found"));

        Long remainingQty = outward.getInTransitQty();

        Set<Long> allowedItemIds =
                getAllowedItemIds(productId, ownerId, ownerType, mvnoId);

        ProductCategory pc = outward.getProductId().getProductCategory();

        boolean hasMac = pc.isHasMac();
        boolean hasSerial = pc.isHasSerial();

        List<Map<String, String>> rows =
                parseFile(inputStream, fileName, outwardId);

        Set<String> identifiers = rows.stream()
                .map(r -> {

                    String mac = r.get("macaddress");

                    if (mac != null && !mac.trim().isEmpty())
                        return normalizeIfNumeric(mac);

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Long> identifierItemMap =
                buildIdentifierItemMap(identifiers);

        Set<Long> usedItemIds = new HashSet<>();

        List<InOutWardMACMapping> validChunk = new ArrayList<>();
        List<ItemSkipped> rejectedChunk = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {

            Map<String, String> row = rows.get(i);
            int rowNum = i + 2;

            try {

                OutwardFileValidationDto dto =
                        mapToDTO(row, rowNum, mvnoId,
                                allowedItemIds,
                                identifierItemMap,
                                usedItemIds);

                dto.setOutwardId(outwardId);

                if (!dto.getRemarkData().isEmpty()) {

                    rejectedChunk.add(
                            convertToRejectedEntity(dto, userId, mvnoId));

                } else {

                    if (remainingQty > 0) {

                        validChunk.add(convertDtoToEntity(dto));
                        remainingQty--;

                    } else {

                        ItemSkipped rejected =
                                convertToRejectedEntity(dto, userId, mvnoId);

                        rejected.setReason(
                                "Quantity exceeded. Outward limit reached.");

                        rejectedChunk.add(rejected);
                    }
                }

                if (validChunk.size() >= CHUNK_SIZE) {

                    processAndSaveChunk(validChunk,
                            outward,
                            hasMac,
                            hasSerial,
                            mvnoId,
                            rejectedChunk);

                    validChunk.clear();
                }

                if (rejectedChunk.size() >= CHUNK_SIZE) {
                    itemSkippedRepository.saveAll(rejectedChunk);
                    rejectedChunk.clear();
                }

            } catch (Exception e) {

                ItemSkipped rejected = new ItemSkipped();
                rejected.setOutwardId(outwardId);
                rejected.setMvnoId(Long.valueOf(mvnoId));
                rejected.setType("OUTWARD");
                rejected.setReason("Row " + rowNum + " : " + e.getMessage());

                rejectedChunk.add(rejected);
            }
        }

        if (!validChunk.isEmpty()) {

            processAndSaveChunk(validChunk,
                    outward,
                    hasMac,
                    hasSerial,
                    mvnoId,
                    rejectedChunk);
        }

        if (!rejectedChunk.isEmpty()) {
            itemSkippedRepository.saveAll(rejectedChunk);
        }
    }

    private void processAndSaveChunk(
            List<InOutWardMACMapping> chunk,
            Outward outward,
            boolean hasMac,
            boolean hasSerial,
            Integer mvnoId,
            List<ItemSkipped> rejectedEntities) throws Exception {

        if (hasMac || hasSerial) {

            inOutWardMACService.validateUpdateMacMappingListNEW(
                    chunk,
                    outward,
                    hasMac,
                    hasSerial,
                    mvnoId,
                    rejectedEntities);

            try {
                inOutWardMACService.checkItemsForInwardOfOutward(chunk);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

//        if (hasSim) {
//
//            inOutWardMACService.validateUpdateMacMappingListSimNEW(
//                    chunk,
//                    outward,
//                    mvnoId,
//                    rejectedEntities);
//
//            inOutWardMACService.checkItemsForInwardOfOutwardSim(chunk);
//        }

//        inOutWardMACRepository.saveAll(chunk);
    }
    private ItemSkipped convertToRejectedEntity(
            OutwardFileValidationDto dto,
            Integer userId,
            Integer mvnoId) {

        ItemSkipped entity = new ItemSkipped();

        entity.setOutwardId(dto.getOutwardId());
        entity.setMvnoId(Long.valueOf(mvnoId));
        entity.setType("OUTWARD");
        entity.setImsi(dto.getImsi());
        entity.setIccid(dto.getIccid());
        entity.setPin1(dto.getPin1());
        entity.setPuk1(dto.getPuk1());
        entity.setPin2(dto.getPin2());
        entity.setPuk2(dto.getPuk2());
        entity.setKiEncrypted(dto.getKiEncrypted());
        entity.setAcc(dto.getAcc());
        entity.setAdm(dto.getAdm());
        entity.setKic(dto.getKic());
        entity.setKid(dto.getKid());
        entity.setKik(dto.getKik());
        entity.setMac(dto.getMacAddress());
        entity.setSerial(dto.getSerialNumber());
        entity.setPort(dto.getPort());
        entity.setTrackable(dto.getTrackable());
        entity.setMsisdn(dto.getMsisdn());
        entity.setReason(String.join(" | ", dto.getRemarkData()));
        entity.setCreatedate(Timestamp.valueOf(LocalDateTime.now()));

        return entity;
    }



    private OutwardFileValidationDto convertEntityToDto(
            InOutWardMACMapping entity) {

        OutwardFileValidationDto dto = new OutwardFileValidationDto();

        dto.setMacAddress(entity.getMacAddress());
        dto.setSerialNumber(entity.getSerialNumber());
//        dto.setImsi(entity.getImsi());
//        dto.setIccid(entity.getIccid());
//        dto.setMsisdn(entity.getMsisdn());
        dto.setOutwardId(entity.getOutwardId());
        dto.setRemarkData(new ArrayList<>());
        return dto;
    }


    private InOutWardMACMapping convertDtoToEntity(
            OutwardFileValidationDto dto) {

        InOutWardMACMapping entity = new InOutWardMACMapping();

        entity.setOutwardId(dto.getOutwardId());
        entity.setId(dto.getItemId());
        entity.setMacAddress(dto.getMacAddress());
        entity.setSerialNumber(dto.getSerialNumber());
//        entity.setImsi(dto.getImsi());
//        entity.setIccid(dto.getIccid());
//        entity.setMsisdn(dto.getMsisdn());
        entity.setMvnoId(dto.getMvnoId());
        return entity;
    }

    public OutwardFileValidationDto mapToDTO(Map<String, String> row, int rowNum, Integer mvnoId,
                                             Set<Long> allowedItemIds,
                                             Map<String, Long> identifierItemMap,
                                             Set<Long> usedItemIds) {

        OutwardFileValidationDto dto = new OutwardFileValidationDto();

        for (Map.Entry<String, String> e : row.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            applyColumnValue(dto, key, value.trim(), rowNum, mvnoId);
        }
        String identifier = null;

        if (dto.getImsi() != null && !dto.getImsi().isEmpty())
            identifier = dto.getImsi();
        else if (dto.getMsisdn() != null && !dto.getMsisdn().isEmpty())
            identifier = dto.getMsisdn();
        else if (dto.getMacAddress() != null && !dto.getMacAddress().isEmpty())
            identifier = dto.getMacAddress();
        else {
            dto.getRemarkData().add(" MAC must be provided");
            return dto;
        }

        //  Fetch itemId
        Long itemId = identifierItemMap.get(identifier);

        if (itemId == null) {
            dto.getRemarkData().add("Item not found for: " + identifier);
            return dto;
        }

        // Allowed check
        if (!allowedItemIds.contains(itemId)) {
            dto.getRemarkData().add("Item not allowed for this outward");
            return dto;
        }

        // Duplicate check inside file
        if (usedItemIds.contains(itemId)) {
            dto.getRemarkData().add("Duplicate identifier in file: " + identifier);
            return dto;
        }

        usedItemIds.add(itemId);
        dto.setItemId(itemId);

        return dto;

    }

    private String normalizeIfNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        value = value.trim();

        try {
            return new BigDecimal(value)
                    .stripTrailingZeros()
                    .toPlainString();
        } catch (NumberFormatException e) {
            return value;
        }
    }
    private void applyColumnValue(
            OutwardFileValidationDto dto,
            String column,
            String value,
            int rowNum,
            Integer mvnoId) {

        value = (value == null) ? "" : value.trim();

        switch (column) {

            case "macaddress":
                if (value.isEmpty()) {
                    dto.getRemarkData().add("MAC is missing");
                }
                value = normalizeIfNumeric(value);
                dto.setMacAddress(value);
                break;

            case "serialnumber":
                if (value.isEmpty()) {
                    dto.getRemarkData().add("Serial is missing");
                }
                value = normalizeIfNumeric(value);
                dto.setSerialNumber(value);
                break;




            default:
                break;
        }
    }


    public void saveValidData(OutwardFileValidationDto dto,
                              Integer userId,
                              String userName) {

        InOutWardMACMapping entity = new InOutWardMACMapping();

        entity.setMacAddress(dto.getMacAddress());
        entity.setSerialNumber(dto.getSerialNumber());
//        entity.setImsi(dto.getImsi());
//        entity.setIccid(dto.getIccid());
//        entity.setMsisdn(dto.getMsisdn());

//        entity.setStatus(CommonConstants.ACTIVE_STATUS);
        entity.setIsDeleted(false);

        entity.setCreatedById(userId);
        entity.setCreatedByName(userName);

//        inOutWardMACMappingRepository.save(entity);
    }

    public void saveRejectedData(OutwardFileValidationDto dto,
                                 Integer userId,
                                 String userName) {

        ItemSkipped entity = new ItemSkipped();

        entity.setOutwardId(dto.getOutwardId());
        entity.setMvnoId(Long.valueOf(dto.getMvnoId()));
        entity.setType("OUTWARD");
        entity.setImsi(dto.getImsi());
        entity.setIccid(dto.getIccid());
        entity.setPin1(dto.getPin1());
        entity.setPuk1(dto.getPuk1());
        entity.setPin2(dto.getPin2());
        entity.setPuk2(dto.getPuk2());
        entity.setKiEncrypted(dto.getKiEncrypted());
        entity.setAcc(dto.getAcc());
        entity.setAdm(dto.getAdm());
        entity.setKic(dto.getKic());
        entity.setKid(dto.getKid());
        entity.setKik(dto.getKik());
        entity.setReason(String.join(" | ", dto.getRemarkData()));

        itemSkippedRepository.save(entity);
    }

    private static boolean isCsvRowEmpty(String[] row) {
        if (row == null) return true;

        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<String> findHeaderValidatin(Long outwardId) {

        Long productId = outwardRepository.findProductIdByOutwardId(outwardId);
        Long pcId = productRepository.findProductCategoryIdByProductId(productId);

        ProductCategory pc = productCategoryRepository.findById(pcId)
                .orElseThrow(() ->
                        new RuntimeException("Product Category not found"));

        List<String> requiredHeaders = new ArrayList<>();

        if (pc.isHasMac()) requiredHeaders.add("macAddress");
        if (pc.isHasSerial()) requiredHeaders.add("serialNumber");

        return requiredHeaders;
    }


    private static String normalizeHeader(String header) {
        if (header == null) return "";
        return header.replace("*", "")
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9_ ]", "")
                .replace(" ", "_");
    }

    private List<Map<String, String>> parseFile(InputStream inputStream,
                                                String fileName,
                                                Long outwardId) throws Exception {

        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        String lower = fileName.toLowerCase();

        if (lower.endsWith(".csv")) {

            return parseCSV(inputStream, outwardId);

        } else if (lower.endsWith(".xls") || lower.endsWith(".xlsx")
                || lower.endsWith(".xlsm") || lower.endsWith(".xltx")
                || lower.endsWith(".xltm") || lower.endsWith(".xlam")
                || lower.endsWith(".xla")) {

            return parseExcel(inputStream, outwardId);

        } else {

            throw new IllegalArgumentException(
                    "Only CSV or Excel files are allowed");
        }
    }

    private List<Map<String, String>> parseCSV(InputStream inputStream,
                                               Long outwardId) throws Exception {

        List<Map<String, String>> list = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String[] headers = reader.readNext();

            if (headers == null || headers.length == 0) {
                throw new IllegalArgumentException("CSV file is empty or header row missing");
            }

            List<String> actualHeaders = Arrays.stream(headers)
                    .map(OutWordUploadService::normalizeHeader)
                    .filter(h -> h != null && !h.isEmpty())
                    .collect(Collectors.toList());

            Set<String> uniqueHeaders = new HashSet<>(actualHeaders);
            if (uniqueHeaders.size() != actualHeaders.size()) {
                throw new IllegalArgumentException("Duplicate columns found in CSV header");
            }

            List<String> expectedHeaders = findHeaderValidatin(outwardId)
                    .stream()
                    .map(OutWordUploadService::normalizeHeader)
                    .collect(Collectors.toList());
            expectedHeaders.add("itemid");
            validateHeadersStrict(actualHeaders, expectedHeaders);

            String[] currentRow;
            int rowNumber = 1;

            while ((currentRow = reader.readNext()) != null) {
                rowNumber++;

                if (isCsvRowEmpty(currentRow)) {
                    continue;
                }

                Map<String, String> row = new LinkedHashMap<>();

                for (int j = 0; j < actualHeaders.size(); j++) {

                    String value = j < currentRow.length
                            ? currentRow[j].trim()
                            : "";

                    row.put(actualHeaders.get(j), value);
                }

                list.add(row);
            }

            if (list.isEmpty()) {
                throw new IllegalArgumentException(
                        "No Outward records found in uploaded file. At least one record is required."
                );
            }
        }

        return list;
    }

    private void validateHeadersStrict(List<String> actualHeaders,
                                       List<String> expectedHeaders) {

        List<String> missingHeaders = expectedHeaders.stream()
                .filter(expected -> !actualHeaders.contains(expected))
                .collect(Collectors.toList());

        if (!missingHeaders.isEmpty()) {
            throw new IllegalArgumentException("Missing columns: " + missingHeaders);
        }

        List<String> extraHeaders = actualHeaders.stream()
                .filter(actual -> !expectedHeaders.contains(actual))
                .collect(Collectors.toList());

        if (!extraHeaders.isEmpty()) {
            throw new IllegalArgumentException("Unexpected columns found: " + extraHeaders);
        }
    }

    private boolean isExcelRowEmpty(Row row) {

        if (row == null || row.getLastCellNum() == -1) {
            return true;
        }

        DataFormatter formatter = new DataFormatter();

        for (int i = 0; i < row.getLastCellNum(); i++) {

            Cell cell = row.getCell(i);

            if (cell != null &&
                    !formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private List<Map<String, String>> parseExcel(InputStream inputStream,
                                                 Long outwardId) throws Exception {

        List<Map<String, String>> list = new ArrayList<>();

        try (Workbook wb = WorkbookFactory.create(inputStream)) {

            Sheet sheet = wb.getSheetAt(0);

            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            DataFormatter formatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row missing in Excel file");
            }

            List<String> actualHeaders = new ArrayList<>();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {

                Cell cell = headerRow.getCell(i);

                String headerValue = getCellValue(cell, formatter);

                String normalized = normalizeHeader(headerValue);

                if (normalized == null || normalized.isEmpty()) {
                    throw new IllegalArgumentException("Blank header found at column index: " + i);
                }

                actualHeaders.add(normalized);
            }

            Set<String> uniqueHeaders = new HashSet<>(actualHeaders);
            if (uniqueHeaders.size() != actualHeaders.size()) {
                throw new IllegalArgumentException("Duplicate columns found in Excel header");
            }

            List<String> expectedHeaders = findHeaderValidatin(outwardId)
                    .stream()
                    .map(OutWordUploadService::normalizeHeader)
                    .collect(Collectors.toList());
            expectedHeaders.add("itemid");
            validateHeadersStrict(actualHeaders, expectedHeaders);

            int rowNumber = 1;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {

                Row currentRow = sheet.getRow(r);
                rowNumber++;

                if (currentRow == null || isExcelRowEmpty(currentRow)) {
                    continue;
                }

                Map<String, String> rowMap = new LinkedHashMap<>();

                for (int i = 0; i < actualHeaders.size(); i++) {

                    Cell cell = currentRow.getCell(i);

                    String value = cell != null
                            ? formatter.formatCellValue(cell)
                            : "";

                    rowMap.put(actualHeaders.get(i), value.trim());
                }

                list.add(rowMap);
            }

            if (list.isEmpty()) {
                throw new IllegalArgumentException(
                        "No Outward records found in uploaded Excel file. At least one record is required."
                );
            }
        }

        return list;
    }

    private String getCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return "";

        switch (cell.getCellType()) {

            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return formatter.formatCellValue(cell);
                }

                return BigDecimal.valueOf(cell.getNumericCellValue())
                        .stripTrailingZeros()
                        .toPlainString();

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                return formatter.formatCellValue(cell);

            case BLANK:
                return "";

            default:
                return formatter.formatCellValue(cell);
        }
    }
}

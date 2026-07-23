package com.savbill.radius.services.impl;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.FaultyMAC;
import com.savbill.radius.repository.FaultyMACKRepocitory;
import com.savbill.radius.services.FaultyMACKService;
import com.savbill.radius.utils.CustomValidationException;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FaultyMACKServiceImpl implements FaultyMACKService {
    @Autowired
    FaultyMACKRepocitory faultyMACKRepocitory;

    @Override
    public FaultyMAC saveMack(FaultyMAC mac) {
        Optional<FaultyMAC> macdata = faultyMACKRepocitory.findFaultyMACSByMackIdEqualsAndIsDeletedFalseAndMvnoIdEquals(mac.getMackId(), mac.getMvnoId());
        if (!macdata.isPresent()) {
            return faultyMACKRepocitory.save(mac);
        } else {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"MACK with id " + mac.getMackId() + " ALREADY EXISTS",null);
        }
    }

    @Override
    public FaultyMAC updateMack(FaultyMAC mac) {
        Optional<FaultyMAC> macdata = faultyMACKRepocitory.findById(mac.getId());
        Optional<FaultyMAC> data = faultyMACKRepocitory.findFaultyMACSByMackIdEqualsAndIsDeletedFalseAndMvnoIdEquals(mac.getMackId(), mac.getMvnoId());

        if (macdata.isPresent()) {
            if (data.isPresent() && macdata.get().equals(data.get())) {
                macdata.get().setMackId(mac.getMackId());
                macdata.get().setIsActive(mac.getIsActive());
                macdata.get().setIsDeleted(mac.getIsDeleted());
                return faultyMACKRepocitory.save(macdata.get());
            } else if (!data.isPresent()) {

                macdata.get().setMackId(mac.getMackId());
                macdata.get().setIsActive(mac.getIsActive());
                macdata.get().setIsDeleted(mac.getIsDeleted());
                return faultyMACKRepocitory.save(macdata.get());
            } else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
                        "Data mismatch for mac " + mac.getMackId(), null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
                    "No data found for mac " + mac.getMackId(), null);
        }

    }

    @Override
    public void deleteMack(String mac, Integer mvnoId) {

        Optional<FaultyMAC> macdata = faultyMACKRepocitory.findById((Long.parseLong(mac)));
        if (macdata.isPresent()) {
            macdata.get().setIsDeleted(true);
            faultyMACKRepocitory.save(macdata.get());
        } else {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"No data found for mac " + mac,null);
        }
    }

    @Override
    public FaultyMAC findByMacId(String macId, Integer mvnoId) {
        Optional<FaultyMAC> macdata = faultyMACKRepocitory.findFaultyMACSByMackIdEqualsAndIsDeletedFalseAndMvnoIdEquals(macId, mvnoId);
        if (macdata.isPresent()) {
            return macdata.get();
        } else {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"No data found for mac " + macId,null);
        }
    }

    @Override
    public FaultyMAC findById(Long macId) {
        Optional<FaultyMAC> macdata = faultyMACKRepocitory.findById(macId);
        if (macdata.isPresent()) {
            return macdata.get();
        } else {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"No data found for mac " + macId,null);
        }
    }

    @Override
    public Page<FaultyMAC> getAll(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request) {
        QFaultyMAC qFaultyMAC = QFaultyMAC.faultyMAC;
        BooleanExpression exp = qFaultyMAC.isNotNull().and(qFaultyMAC.isDeleted.eq(false));
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        if (mvnoId != null && mvnoId == 1) {
            if (paginationDTO.getPage() != 0 && paginationDTO.getSize() != 0) {
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }

            }
            return faultyMACKRepocitory.findAll(pageable);
        } else {
            exp = exp.and(qFaultyMAC.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            Predicate builder = exp;
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable1 = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
            return faultyMACKRepocitory.findAll(builder, pageable1);
        }
    }

    @Override
    public void uploadXl(MultipartFile file, Integer mvnoId) {
        Set<FaultyMAC> macs = new HashSet<>();
        Workbook workbook = null;

        try (InputStream fis = file.getInputStream()) {
            workbook = new XSSFWorkbook(fis);
            DataFormatter dataFormatter = new DataFormatter();
            List<FaultyMAC> existingMacs = faultyMACKRepocitory.findAllByMvnoIdAndIsDeletedFalse(mvnoId);
            Set<String> existingMackIds = existingMacs.stream()
                    .map(FaultyMAC::getMackId)
                    .collect(Collectors.toSet());

            workbook.forEach(sheet -> {
                int index = 0;
                for (Row row : sheet) {
                    if (index++ == 0) continue;
                    String mackId = dataFormatter.formatCellValue(row.getCell(0));
                    if (mackId != null && !mackId.isEmpty() && !existingMackIds.contains(mackId)) {
                        FaultyMAC faultyMAC = new FaultyMAC();
                        faultyMAC.setMackId(mackId);
                        faultyMAC.setMvnoId(mvnoId);
                        macs.add(faultyMAC);
                    }
                }
            });

            if (!macs.isEmpty()) {
                faultyMACKRepocitory.saveAll(macs);
                System.out.println("Saved " + macs.size() + " new Faulty MAC entries.");
            } else {
                System.out.println("No new entries to save.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                  e.getMessage();
                }
            }
        }
    }
}

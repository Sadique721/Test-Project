package com.savbill.cpm.modules.planUpdate.service;

import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.planUpdate.domain.QuotaDtls;
import com.savbill.cpm.modules.planUpdate.mapper.QuotaDtlsMapper;
import com.savbill.cpm.modules.planUpdate.model.QuotaDtlsDTO;
import com.savbill.cpm.modules.planUpdate.repository.QuotaDtlsRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuotaDtlsService extends ExBaseAbstractService<QuotaDtlsDTO, QuotaDtls, Long> {

    @Autowired
    private QuotaDtlsMapper quotaDtlsMapper;
    @Autowired
    private QuotaDtlsRepository quotaDtlsRepository;

    public QuotaDtlsService(QuotaDtlsRepository repository, QuotaDtlsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "QuotaDtlsService";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    public List<QuotaDtlsDTO> findAllByCustomersId(Integer id) {
        return quotaDtlsRepository.findAllByCustomersId(id)
                .stream().map(domain -> quotaDtlsMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    public static Double quotaUnitConvertToKBUnit(Double quota, String quotaType) throws Exception {
        Double quotaKb = 0.0;
        if (quota != null) {
            if (quotaType != null) {
                if (quotaType.equalsIgnoreCase(Constants.GB)) {
                    quotaKb = quota * 1048576;
                }
                if (quotaType.equalsIgnoreCase(Constants.MB)) {
                    quotaKb = quota * 1024;
                }
                if (quotaType.equalsIgnoreCase(Constants.KB)) {
                    quotaKb = quota;
                }
            }
        }
        return quotaKb;
    }

    public static Double quotaUnitConvertKbToUnit(Double quota, String quotaType) throws Exception {
        Double quotaUnit = 0.0;
        if (quota != null) {
            if (quotaType != null) {
                if (quotaType.equalsIgnoreCase(Constants.GB)) {
                    quotaUnit = (quota / 1048576);
                }
                if (quotaType.equalsIgnoreCase(Constants.MB)) {
                    quotaUnit = (quota / 1024);
                }
                if (quotaType.equalsIgnoreCase(Constants.KB)) {
                    quotaUnit = quota;
                }
            }
        }
        return quotaUnit;
    }

    public static Double quotaUnitConvertToSecond(Double quota, String quotaType) throws Exception {
        Double quotaSecond = 0.0;
        if (quota != null) {
            if (quotaType != null) {
                if (quotaType.equalsIgnoreCase(Constants.HOUR)) {
                    quotaSecond = quota * 3600;
                }
                if (quotaType.equalsIgnoreCase(Constants.MINUTE)) {
                    quotaSecond = quota * 60;
                }
                if (quotaType.equalsIgnoreCase(Constants.SECOND)) {
                    quotaSecond = quota;
                }
            }
        }
        return quotaSecond;
    }

    public static Double quotaUnitSecondConvertToUnit(Double quota, String quotaType) throws Exception {
        Double quotaUnitTime = 0.0;
        if (quota != null) {
            if (quotaType != null) {
                if (quotaType.equalsIgnoreCase(Constants.HOUR)) {
                    quotaUnitTime = (quota / 3600);
                }
                if (quotaType.equalsIgnoreCase(Constants.MINUTE)) {
                    quotaUnitTime = (quota / 60);
                }
                if (quotaType.equalsIgnoreCase(Constants.SECOND)) {
                    quotaUnitTime = quota;
                }
            }
        }
        return quotaUnitTime;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("QuotaDtls");
        createExcel(workbook, sheet, QuotaDtlsDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, QuotaDtlsDTO.class, null);
    }
}

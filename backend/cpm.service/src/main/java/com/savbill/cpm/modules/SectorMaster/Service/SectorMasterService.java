package com.savbill.cpm.modules.SectorMaster.Service;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.cpm.constants.LogConstants;
import com.savbill.cpm.constants.SearchConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.SectorMaster.Domain.QSectorMaster;
import com.savbill.cpm.modules.SectorMaster.Domain.SectorMaster;
import com.savbill.cpm.modules.SectorMaster.Mapper.SectorMasterMapper;
import com.savbill.cpm.modules.SectorMaster.Model.SectorMasterDTO;
import com.savbill.cpm.modules.SectorMaster.Repository.SectorMasterRepository;
import com.savbill.cpm.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Service
public class SectorMasterService extends ExBaseAbstractService<SectorMasterDTO, SectorMaster, Long> {
    public SectorMasterService(SectorMasterRepository repository, SectorMasterMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private Tracer tracer;

    @Override
    public String getModuleNameForLog() {
        return "[SectorMasterService]";
    }

    @Autowired
    SectorMasterRepository repository;
    private final Logger logger = LoggerFactory.getLogger(SectorMasterService.class);

    @Override
    public boolean duplicateVerifyAtSave(String sname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (sname != null) {
            sname = sname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(sname);
            else count = repository.duplicateVerifyAtSave(sname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public SectorMaster getById(Long id) {
        return repository.findById(id).get();

    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else count = repository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = repository.duplicateVerifyAtEdit(name, id);
                else countEdit = repository.duplicateVerifyAtEdit(name, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<SectorMaster> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = repository.findAll(pageRequest);
        else
            paginationList = repository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Active CasMaster by keyword : " + filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                        return getSectorByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Active Master By Key :  " + filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }
        return null;
    }

    public GenericDataDTO getSectorByName(String rname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QSectorMaster qSectorMaster = QSectorMaster.sectorMaster;
            BooleanExpression exp = qSectorMaster.isNotNull();
            exp = exp.and(qSectorMaster.sname.containsIgnoreCase(rname)).and(qSectorMaster.isDeleted.eq(false));
            Page<SectorMaster> regionList = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                regionList = repository.findAll(exp, pageRequest);
            } else {
                exp = exp.and(qSectorMaster.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                regionList = repository.findAll(exp, pageRequest);
            }
            if (null != regionList && 0 < regionList.getSize()) {
                makeGenericResponse(genericDataDTO, regionList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
}

package com.savbill.integrationsystem.core.service;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ExBaseService<T, K> {
    List<T> getAllEntities() throws Exception;

    T getEntityById(K id,Long mvnoId) throws Exception;

    T getEntityById(K id, boolean flag) throws Exception;

    T getEntityForUpdateAndDelete(K id,Long mvnoId) throws Exception;

    T saveEntity(T entity) throws Exception;

    T updateEntity(T entity) throws Exception;

    void deleteEntity(T entity) throws Exception;

    GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, HttpServletRequest request) throws Exception;

    GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, HttpServletRequest request);

//    void excelGenerate(Workbook workbook) throws Exception;
//
//    void pdfGenerate(Document doc) throws Exception;

    boolean deleteVerification(Integer id) throws Exception;
}

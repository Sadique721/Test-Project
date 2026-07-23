package com.savbill.ticketmanagement.core.service;

import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;

import java.util.List;

public interface ExBaseService<T, K> {
    List<T> getAllEntities() throws Exception;

    T getEntityById(K id) throws Exception;

    T getEntityById(K id, boolean flag) throws Exception;

    T getEntityForUpdateAndDelete(K id) throws Exception;

    T saveEntity(T entity) throws Exception;

    T updateEntity(T entity) throws Exception;

    void deleteEntity(T entity) throws Exception;

    GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) throws Exception;

    GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder);

//    void excelGenerate(Workbook workbook) throws Exception;
//
//    void pdfGenerate(Document doc) throws Exception;

    boolean deleteVerification(Integer id)throws Exception;
}

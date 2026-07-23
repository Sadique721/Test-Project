package com.savbill.salescrmsbss.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.savbill.salescrmsbss.utils.ClientServiceConstant;
import com.savbill.salescrmsbss.utils.CommonConstants;



public class AbstractService<T, Long> {

	public Integer MAX_PAGE_SIZE;

	public Map<String, String> sortColMap = new HashMap<>();

	public PageRequest pageRequest = null;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
		this.MAX_PAGE_SIZE = Integer
				.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
		if (pageSize > MAX_PAGE_SIZE)
			pageSize = MAX_PAGE_SIZE;

		if (null != sortColMap && 0 < sortColMap.size()) {
			if (sortColMap.containsKey(sortBy)) {
				sortBy = sortColMap.get(sortBy);
			}
		}

		if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
			pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
		else
			pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
		return pageRequest;
	}
}

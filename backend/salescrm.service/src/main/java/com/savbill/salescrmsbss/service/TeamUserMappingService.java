package com.savbill.salescrmsbss.service;

import java.util.Set;

public interface TeamUserMappingService {

	public Set<Long> findByStaffIds(Integer staffId);

}

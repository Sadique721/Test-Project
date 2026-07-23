package com.savbill.radius.services;

import com.savbill.radius.entity.CoaDMProfileAttribute;
import com.savbill.radius.helper.CoaDMProfileAttributeDto;

import java.util.List;

public interface CoaDMProfileAttributeService {
    List<CoaDMProfileAttribute> findCoaDMProfileAttributeByCoaDMProfileId(Long coaDMProfileId, Integer mvnoId);
    List<CoaDMProfileAttribute> findAllCoaDMProfileAttributes(Integer mvnoId);
    void deleteCoaDMProfileAttributeById(Long id, Integer mvnoid);
    CoaDMProfileAttribute saveCoaDMProfileAttribute(CoaDMProfileAttributeDto CoaDMProfileAttributeDto, Integer mvnoId);
    List<CoaDMProfileAttribute> updateCoaDMProfileAttribute(List<CoaDMProfileAttribute> CoaDMProfileAttribute, Integer mvnoid, Long coaDMId);
	void deleteCoaDMProfileAttributeByCoaDmProfileId(Long coaDMProfileId, Integer mvnoId);
}

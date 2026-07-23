package com.savbill.radius.services;

import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.CoaDMProfile;
import com.savbill.radius.helper.CoaDMProfileDto;

import javax.servlet.http.HttpServletRequest;

public interface CoaDMProfileService {
    CoaDMProfile findCoaDMProfileById(Long id, Integer mvnoId);
    List<CoaDMProfile> findAllCoaDMProfiles(Integer mvnoId);
    void deleteCoaDMProfileById(Long id, Integer mvnoId);
    CoaDMProfile saveCoaDMProfile(CoaDMProfileDto CoaDMProfileDto, Integer mvnoId);
    CoaDMProfile updateCoaDMProfile(CoaDMProfile coaDMProfile, Integer mvnoId, HttpServletRequest request);
    Optional<CoaDMProfile> findCoaDMProfileByName(String name, Integer mvnoId);
    CoaDMProfile validateCoaDMProfileByName(String name, Integer mvnoId);
    List<CoaDMProfile> findByType(String type, Integer mvnoId);
    List<CoaDMProfile> searchCoaDMProfile(String name, String type, Integer mvnoId);
    List<CoaDMProfile> findCoaProfiles(Integer mvnoId);
}

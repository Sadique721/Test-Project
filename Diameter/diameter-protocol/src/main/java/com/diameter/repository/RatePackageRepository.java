package com.diameter.repository;

import com.diameter.model.RatePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatePackageRepository extends JpaRepository<RatePackage, Long> {
    RatePackage findByPackageName(String packageName);

    List<RatePackage> findByPulseId(Long pulseId);

    void deleteByPackageName(String packageName);
}
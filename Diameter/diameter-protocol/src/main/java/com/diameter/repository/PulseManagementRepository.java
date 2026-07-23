package com.diameter.repository;

import com.diameter.model.PulseManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PulseManagementRepository extends JpaRepository<PulseManagement, Long> {
    PulseManagement findByPulseName(String pulseName);

    void deleteByPulseName(String pulseName);

    PulseManagement findByid(Long id);
}
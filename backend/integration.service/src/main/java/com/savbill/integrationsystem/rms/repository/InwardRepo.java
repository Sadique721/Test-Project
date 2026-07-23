package com.savbill.integrationsystem.rms.repository;

import com.savbill.integrationsystem.rms.entity.Inward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InwardRepo extends JpaRepository<Inward,Long> {
    Inward findTopByOrderByIdDesc();
}

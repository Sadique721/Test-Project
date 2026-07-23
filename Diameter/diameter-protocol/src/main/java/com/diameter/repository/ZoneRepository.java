package com.diameter.repository;

import com.diameter.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByZoneName(String zoneName);
}
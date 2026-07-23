package com.diameter.repository;

import com.diameter.model.ServiceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long> {
}

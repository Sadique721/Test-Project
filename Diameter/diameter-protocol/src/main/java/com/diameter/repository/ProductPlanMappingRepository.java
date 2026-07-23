package com.diameter.repository;

import com.diameter.model.Productplanmapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPlanMappingRepository extends JpaRepository<Productplanmapping, Long> {

}

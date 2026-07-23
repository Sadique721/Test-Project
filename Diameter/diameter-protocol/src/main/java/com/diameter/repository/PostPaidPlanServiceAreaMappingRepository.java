package com.diameter.repository;

import com.diameter.model.PostPaidPlanServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostPaidPlanServiceAreaMappingRepository extends JpaRepository<PostPaidPlanServiceAreaMapping, Long> {

}


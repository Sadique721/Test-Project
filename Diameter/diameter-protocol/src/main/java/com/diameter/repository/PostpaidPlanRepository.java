package com.diameter.repository;

import com.diameter.model.PostpaidPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostpaidPlanRepository extends JpaRepository<PostpaidPlan, Integer> {


}


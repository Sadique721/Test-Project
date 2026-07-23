package com.diameter.repository;

import com.diameter.model.PostpaidPlanCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostpaidPlanChargeRepository  extends JpaRepository<PostpaidPlanCharge, Integer>  {

}


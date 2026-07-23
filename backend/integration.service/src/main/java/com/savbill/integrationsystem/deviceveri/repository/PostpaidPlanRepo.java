package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.PostpaidPlanData;

@Repository
public interface PostpaidPlanRepo extends JpaRepository<PostpaidPlanData, Long>
{
	List<PostpaidPlanData> findByPostpaidplanidAndIsDeleted(Long postpaidplanid, Integer isDeleted);
}

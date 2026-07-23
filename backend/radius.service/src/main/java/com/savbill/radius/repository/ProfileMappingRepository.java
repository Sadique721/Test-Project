package com.savbill.radius.repository;

import com.savbill.radius.entity.ProfileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileMappingRepository extends JpaRepository<ProfileMapping, Long>, QuerydslPredicateExecutor<ProfileMapping> {
     List<ProfileMapping> findByProfileId(Long profileId);

}

package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer> {
}

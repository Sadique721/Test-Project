package com.savbill.taskmanagement.core.modules.NetworkDevices.repository;


import com.savbill.taskmanagement.core.modules.NetworkDevices.domain.OLTPortDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OltPortRepository extends JpaRepository<OLTPortDetails,Long> {
}

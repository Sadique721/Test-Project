package com.savbill.cpm.modules.NetworkDevices.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.NetworkDevices.domain.OLTPortDetails;

public interface OltPortRepository extends JpaRepository<OLTPortDetails,Long> {
}

package com.savbill.integrationsystem.deviceveri.repository;

import com.savbill.integrationsystem.deviceveri.domain.SubscriberaddressrelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberaddressrelRepo extends JpaRepository<SubscriberaddressrelData, Long> {
}

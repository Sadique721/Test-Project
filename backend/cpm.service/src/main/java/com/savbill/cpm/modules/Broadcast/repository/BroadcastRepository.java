package com.savbill.cpm.modules.Broadcast.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.Broadcast.domain.Broadcast;

public interface BroadcastRepository extends JpaRepository<Broadcast,Long> {
}

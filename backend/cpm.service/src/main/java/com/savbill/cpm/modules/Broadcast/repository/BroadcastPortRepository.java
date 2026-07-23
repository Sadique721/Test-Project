package com.savbill.cpm.modules.Broadcast.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.Broadcast.domain.BroadcastPorts;

@Repository
public interface BroadcastPortRepository  extends JpaRepository<BroadcastPorts,Long> {
}

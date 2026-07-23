package com.savbill.cpm.modules.DisconnectSubscriber.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.DisconnectSubscriber.domain.UserDisconnect;

public interface UserDisconnectRepository extends JpaRepository<UserDisconnect,Long> {
}

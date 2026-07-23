package com.savbill.integrationsystem.Case;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepo extends JpaRepository<Case,Long> {

    List<Case> findAllByUserName(String username);

}

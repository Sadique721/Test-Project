package com.savbill.cpm.repository.tacacs;

import com.savbill.cpm.model.tacacs.AccessLevelGroupTacacs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccessLevelGroupTacacsRepository extends JpaRepository<AccessLevelGroupTacacs, Long> {
}

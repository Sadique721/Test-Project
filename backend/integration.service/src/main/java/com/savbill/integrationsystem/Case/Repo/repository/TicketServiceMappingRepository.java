package com.savbill.integrationsystem.Case.Repo.repository;

import com.savbill.integrationsystem.Case.TicketServicemapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketServiceMappingRepository extends JpaRepository<TicketServicemapping, Long> {

}

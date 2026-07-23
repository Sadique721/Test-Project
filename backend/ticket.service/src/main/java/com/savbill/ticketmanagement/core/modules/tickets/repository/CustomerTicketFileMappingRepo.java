package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.CustomerTicketFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerTicketFileMappingRepo extends JpaRepository<CustomerTicketFileMapping , Long> {

    @Query(value = "SELECT * FROM tbltcustomer_ticket_file_mapping WHERE customer_ticket_mapping_id = :customerTicketMappingId",
            nativeQuery = true)
    List<CustomerTicketFileMapping> findByCustomerTicketMappingId(@Param("customerTicketMappingId") Long customerTicketMappingId);

    @Query(value = "SELECT * FROM tbltcustomer_ticket_file_mapping WHERE uniquename = :uniquename",
            nativeQuery = true)
    CustomerTicketFileMapping findByCustomerTicketByUniqueName(@Param("uniquename") String uniquename);

    Optional<CustomerTicketFileMapping> findByCustomerTicketMappingAndSection(Long customerTicketMappingId, String section);


}

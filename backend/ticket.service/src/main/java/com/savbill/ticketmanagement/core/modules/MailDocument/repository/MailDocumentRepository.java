package com.savbill.ticketmanagement.core.modules.MailDocument.repository;

import com.savbill.ticketmanagement.core.modules.MailDocument.domain.MailDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailDocumentRepository extends JpaRepository<MailDocument,Long> {

    List<MailDocument> findAllByMailId(@Param("mailId") String mailId);

}

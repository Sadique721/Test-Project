package com.savbill.commonGateway.moules.knowledgeBaseDocs;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface KnowledgeBaseDocRepo extends JpaRepository<KnowledgeBaseDocuments,Long> , QuerydslPredicateExecutor<KnowledgeBaseDocuments> {

    @Query(value = "select new KnowledgeBaseDocuments(t.eventName, t.docType) from KnowledgeBaseDocuments t where t.id = :id ")
    KnowledgeBaseDocuments findDocumentTypeById(Long id);

    boolean existsByEventNameAndDocumentForAndDocTypeAndMvnoId(String eventName, String documentFor, String docType, Long mvnoId);

}

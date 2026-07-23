package com.savbill.cpm.core.dto;



import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.Projections;
import com.savbill.cpm.model.postpaid.QCreditDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CreditDocRepositoryCustomImpl implements CreditDocRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    @Autowired
    public CreditDocRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<CreditDocumentProjectionDTO> findAllProjected(BooleanExpression predicate, Pageable pageable) {
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;

        List<CreditDocumentProjectionDTO> content = queryFactory
                .select(Projections.constructor(CreditDocumentProjectionDTO.class,
                        qCreditDocument.id,
                        qCreditDocument.paymode,
                        qCreditDocument.reciptNo,
                        qCreditDocument.amount,
                        qCreditDocument.paymentdate,
                        qCreditDocument.createdByName,
                        qCreditDocument.status,
                        qCreditDocument.createdate))
                .from(qCreditDocument)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(qCreditDocument.count())
                .from(qCreditDocument)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}

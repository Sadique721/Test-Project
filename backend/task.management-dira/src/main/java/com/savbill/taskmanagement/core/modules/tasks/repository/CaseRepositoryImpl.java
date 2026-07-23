package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.savbill.taskmanagement.core.modules.Mvno.domain.QMvno;
import com.savbill.taskmanagement.core.modules.staffuser.domain.QStaffUser;
import com.savbill.taskmanagement.core.modules.tasks.domain.QCase;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CaseRepositoryImpl implements CaseRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CaseListDTO> findCaseList(BooleanExpression predicate, Pageable pageable) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        QCase c = QCase.case$;
        QStaffUser assignee = QStaffUser.staffUser;
        QMvno mvno = QMvno.mvno;

        List<CaseListDTO> content = queryFactory
                .select(Projections.constructor(
                        CaseListDTO.class,
                        c.caseId,
                        c.caseTitle,
                        c.caseNumber,
                        c.caseType,
                        c.caseStatus,
                        c.createdate,
                        c.updatedate,
                        c.nextFollowupDate,
                        c.nextFollowupTime,
                        assignee.id,
                        assignee.username,
                        mvno.name,
                        c.mvnoId,
                        assignee.parentStaffId
                ))
                .from(c)
                .leftJoin(c.currentAssignee, assignee)
                .leftJoin(mvno).on(mvno.id.eq(c.mvnoId.longValue()))
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(c.count())
                .from(c)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
package com.savbill.taskmanagement.core.utillity.CaseUtility;

import com.savbill.taskmanagement.core.dto.CalanderCasePojo;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
@Service
public class CaseSpecification {
    @PersistenceContext
    private  EntityManager entityManager;
    public  List<CaseDTO>  getCasesByCriteria(CalanderCasePojo calanderCasePojo) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CaseDTO> query = cb.createQuery(CaseDTO.class);
        Root<Case> root = query.from(Case.class);
        Root<Teams> teamRoot = query.from(Teams.class);

        // Build Predicates
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("teamId"), teamRoot.get("id")));

        if (calanderCasePojo.getCaseTitle() != null && !calanderCasePojo.getCaseTitle().isEmpty()) {
            predicates.add(cb.like(root.get("caseTitle"), "%" + calanderCasePojo.getCaseTitle() + "%"));
        }

        if (calanderCasePojo.getCaseType() != null && !calanderCasePojo.getCaseType().isEmpty()) {
            predicates.add(cb.equal(root.get("caseType"), calanderCasePojo.getCaseType()));
        }

        if (calanderCasePojo.getCaseStatus() != null && !calanderCasePojo.getCaseStatus().isEmpty()) {
            predicates.add(cb.equal(root.get("caseStatus"), calanderCasePojo.getCaseStatus()));
        }

        if (calanderCasePojo.getCasePriority() != null && !calanderCasePojo.getCasePriority().isEmpty()) {
            predicates.add(cb.equal(root.get("priority"), calanderCasePojo.getCasePriority()));
        }

        if (calanderCasePojo.getTeamId() != null) {
            predicates.add(cb.equal(root.get("teamId"), calanderCasePojo.getTeamId()));
        }

        if (calanderCasePojo.getCurrentAssigneeId() != null) {
            Join<Object, Object> assigneeJoin = root.join("currentAssignee");
            predicates.add(cb.equal(assigneeJoin.get("id"), calanderCasePojo.getCurrentAssigneeId()));
        }

        if (calanderCasePojo.getCustomerId() != null) {
            Join<Object, Object> customerJoin = root.join("customers");
            predicates.add(cb.equal(customerJoin.get("id"), calanderCasePojo.getCustomerId()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Define DTO Projection
        query.select(cb.construct(
                CaseDTO.class,
                root.get("caseId"),
                root.get("caseTitle"),
                root.get("caseType"),
                root.get("caseNumber"),
                root.get("caseFor"),
                root.get("caseOrigin"),
                root.get("caseStatus"),
                root.get("priority"),
                root.get("startDate"),
                root.get("endDate"),
                root.get("customers").get("id"),
                root.get("teamId"),
                teamRoot.get("name"),
                cb.concat(root.join("currentAssignee").get("firstname"), cb.concat(" ", root.join("currentAssignee").get("lastname"))),
                cb.concat(root.join("customers").get("title"), cb.concat(" ", cb.concat(root.join("customers").get("firstname"), root.join("customers").get("lastname")))),
                root.get("currentAssignee").get("id"),
                root.get("isFromCalender"),
                root.get("firstRemark")
        ));

        return entityManager.createQuery(query).getResultList();
    }
}

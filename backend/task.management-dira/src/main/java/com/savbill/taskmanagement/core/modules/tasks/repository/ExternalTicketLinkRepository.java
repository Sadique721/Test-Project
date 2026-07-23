package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.ExternalTicketLinkMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalTicketLinkRepository extends JpaRepository<ExternalTicketLinkMapping,Long> {

    @Query(value = "select t.linked_ticket_id from tbltexternalticketlink t where t.task_id=:taskId",nativeQuery = true)
    List<Integer> findAllByTaskId(Integer taskId);


}

package com.savbill.radius.repository;

import com.savbill.radius.entity.CustReplyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustReplyItemRepository extends JpaRepository<CustReplyItem, Integer> {
}

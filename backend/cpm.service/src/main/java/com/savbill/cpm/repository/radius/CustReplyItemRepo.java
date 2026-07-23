package com.savbill.cpm.repository.radius;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.radius.CustReplyItem;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustReplyItemRepo extends JpaRepository<CustReplyItem, Integer> {

    List<CustReplyItem> findBycustid(Integer custid);
}

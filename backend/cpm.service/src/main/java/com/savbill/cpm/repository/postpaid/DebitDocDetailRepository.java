package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.DebitDocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DebitDocDetailRepository  extends JpaRepository<DebitDocDetails, Integer>, QuerydslPredicateExecutor<DebitDocDetails> {

    List<DebitDocDetails> findAllByDebitdocumentid(Integer debitdocumentid);
    List<DebitDocDetails> findAllByDebitdocdetailidIn(List<Integer> debitdocDetailids);


}

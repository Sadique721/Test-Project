package com.savbill.commonGateway.moules.MvnoFeedBackConfig.Repository;

import com.savbill.commonGateway.moules.MvnoFeedBackConfig.Domain.FeedbackConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackConfigRepository extends JpaRepository<FeedbackConfig, Long>, QuerydslPredicateExecutor<FeedbackConfig> {
    // Add custom query methods here if needed


    List<FeedbackConfig> findAllByMvnoid(Integer mvnoid);

     List<FeedbackConfig> findAllByEventAndMvnoid(String event, Integer mvnoId);
}

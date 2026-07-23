package com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long>, QuerydslPredicateExecutor<PasswordHistory> {

    @Query("SELECT MAX(p.passwordAttemptNumber) FROM PasswordHistory p WHERE p.staffId = :staffId")
    Long findMaxPasswordAttemptNumberByStaffId(@Param("staffId") Integer staffId);

    @Query("SELECT p FROM PasswordHistory p WHERE p.staffId = :staffId ORDER BY p.passwordAttemptNumber DESC")
    List<PasswordHistory> findByStaffIdOrderByPasswordAttemptNumberDesc(
            @Param("staffId") Integer staffId, Pageable pageable);
}

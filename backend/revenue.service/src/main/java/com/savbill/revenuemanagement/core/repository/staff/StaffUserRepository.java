package com.savbill.revenuemanagement.core.repository.staff;



import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer> {
    Optional<StaffUser> findByUsername(String username);

    String findNameById(Integer staffId);
}

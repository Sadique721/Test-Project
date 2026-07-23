package com.savbill.partnermanagement.core.repository;

import com.savbill.partnermanagement.modules.StaffUser.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffUser, Long> {

    Optional<StaffUser> findByUsername(String userName);
//
//    @Query(value = "select * from tblmstaff s where s.username LIKE %:userName%",nativeQuery = true)
//    List<StaffUser> searchByUserName(@Param("userName") String userName);
//
//    @Query("select s from StaffUser s where s.username = :username and s.mvnoId = :mvnoId")
//    StaffUser findByUserNameAndMvnoId(@Param("username") String username, @Param("mvnoId") Long mvnoId);
}

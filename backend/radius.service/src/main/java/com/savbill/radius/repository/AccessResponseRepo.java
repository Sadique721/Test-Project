package com.savbill.radius.repository;

import com.savbill.radius.entity.AccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessResponseRepo extends JpaRepository<AccessResponse, Long> {
    @Query(value = "select count(*) from tblmaccessresponse c where c.name=:name and c.isdelete=false ", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    Page<AccessResponse> findAllByIsDeleteFalse(Pageable pageable);

    List<AccessResponse> findAllByIsDeleteFalse();
}

package com.savbill.taskmanagement.core.modules.Mail.repository;

import com.savbill.taskmanagement.core.modules.Mail.domain.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail,Long>, QuerydslPredicateExecutor<Mail> {

    List<Mail> findAll();

    List<Mail> findAllById(Long id);

    @Query(value = "select count(*) from tblissuetype where id =:id and is_delete=false" ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query(value = "select count(*) from tblissuetype m where m.name=:name and m.is_delete=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);
    @Query(value = "select count(*) from tblissuetype m where m.name=:name and m.is_delete=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblissuetype t where t.id =:id and t.name =:name and t.is_delete =false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblissuetype t where t.id =:id and  t.name =:name and t.is_delete =false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

    Page<Mail> findAllByFolderNotAndMvnoIdAndIsDelete(String folder,Long mvnoId, Boolean isdelete, Pageable pageable);

    Page<Mail> findAllByFolderNotAndMvnoIdAndBuIdAndIsDelete(String folder,Long mvnoId, Long buId, Boolean isdelete, Pageable pageable);

}

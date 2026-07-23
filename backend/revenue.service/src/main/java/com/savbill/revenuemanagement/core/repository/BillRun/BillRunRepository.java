package com.savbill.revenuemanagement.core.repository.BillRun;

import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRunRepository extends JpaRepository<BillRun, Integer>{


    List<BillRun> findByStatusAndMvnoIdIn(String y, List<Integer> list);

    List<BillRun> findByStatus(String y);

    @Query("select t from BillRun t where t.isDelete=false")
    List<BillRun> findAll();

//    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false and t.MVNOID in :mvnoIds and t.BUID in :buIds", nativeQuery = true)
//    List<BillRun> findAll(@Param("mvnoIds")List mvnoIds, @Param("buIds")List buIds);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    List<BillRun> findAll(@Param("mvnoIds")List mvnoIds);

    @Query(value = "select * from  TBLMBILLRUN t where t.is_delete=false AND t.lcoid IS NULL", nativeQuery = true)
    Page<BillRun> findAll(PageRequest pageRequest);

    @Query(value = "select * from  TBLMBILLRUN t where t.is_delete=false AND t.lcoid=:lcoId", nativeQuery = true)
    Page<BillRun> findAll(Pageable pageRequest, @Param("lcoId") Integer lcoId);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid=:lcoId"
            ,nativeQuery = true
            ,countQuery = "select count(*) from TBLMBILLRUN t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid=:lcoId")
    Page<BillRun> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds) AND t.lcoid IS NULL) AND t.lcoid IS NULL"
            ,nativeQuery = true
            ,countQuery = "select count(*) from TBLMBILLRUN t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid IS NULL")
    Page<BillRun> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid=:lcoId"
            ,nativeQuery = true
            ,countQuery = "select count(*) from TBLMBILLRUN t where t.is_delete=false AND (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)) AND t.lcoid=:lcoId")
    Page<BillRun> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from TBLMBILLRUN t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid IS NULL"
            ,nativeQuery = true
            ,countQuery = "select count(*) from TBLMBILLRUN t where t.is_delete=false AND t.MVNOID in :mvnoIds AND t.lcoid IS NULL")
    Page<BillRun> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);
}

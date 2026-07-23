package com.savbill.cpm.modules.Voucher.repository;
import com.savbill.cpm.modules.Voucher.domain.Voucher;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long>,QuerydslPredicateExecutor<Voucher> {

	boolean existsByCode(String code);

	@Query("SELECT e.code FROM Voucher e WHERE e.code IN (:code)")
	List<String> findByCodeIn(@Param("code") List<String> code);

	@Query(value = "select voucher_batch_id from tbltvoucher where CODE =:code" ,nativeQuery = true)
	Long getVoucherBatchById(@Param("code") String code);
	@Query(value = "select v from Voucher v where v.voucherBatch.voucherBatchId =:voucherBatchId  ORDER BY v.id DESC" )
	List<Voucher> getVoucherBatch(@Param("voucherBatchId") Long voucherBatchId);



}

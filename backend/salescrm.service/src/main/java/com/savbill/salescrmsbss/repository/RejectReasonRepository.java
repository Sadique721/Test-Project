package com.savbill.salescrmsbss.repository;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.RejectReason;

@JaversSpringDataAuditable
@Repository
public interface RejectReasonRepository extends JpaRepository<RejectReason, Long>{

	Optional<RejectReason> findByNameAndIsDelete(String name,Boolean isDelete);
	
	List<RejectReason> findByNameAndMvnoIdAndIsDelete(String name,Long mvnoId,Boolean isDelete);
	
	Page<RejectReason> findByNameContainingAndIsDelete(String name,Boolean isDelete,Pageable pageable);
	
	Page<RejectReason> findByNameContainingAndMvnoIdAndIsDelete(String name,Long mvnoId,Boolean isDelete,Pageable pageable);

	@Query(value = "select * from savbillsalesscrms.tblmrejectreason where status = 'Active' and is_delete = false\n",nativeQuery = true)
	List<RejectReason> findAllRejectedReasonsList();
 @Query(value = "select count(*) from savbillsalesscrms.tblmrejectreason where lower(name) = :reasonName", nativeQuery = true)
    Integer findByNameEqualsIgnoreCase(@Param("reasonName") String reasonName);
	@Query(value = "select count(*) from tblmrejectreason t where t.name=:name and t.is_delete=false", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name);
	@Query(value = "select count(*) from tblmrejectreason t where t.name=:name and t.is_delete=false and t.mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);
	@Query(value = "select count(*) from tblmrejectreason t where t.name=:name and t.COUNTRYID =:id and t.is_delete=false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Long id);
	@Query(value = "select count(*) from tblmrejectreason t where t.name=:name and t.reject_reason_id =:id and t.is_delete=false and t.mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Long id, @Param("mvnoIds")List mvnoIds);

}

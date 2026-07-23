package com.savbill.salescrmsbss.repository;

import com.savbill.salescrmsbss.entity.LeadSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadSourceRepository extends JpaRepository<LeadSource, Long> {

	@Query(value = "select count(*) from LeadSource l where l.leadSourceName=:name and (l.mvnoId =:mvnoId or l.mvnoId=null) and  l.isDelete=false and l.id!=:id")
	Integer findByLeadSourceNameAndMvnoIdAndIsDeleteFalseAndIdIsNot(@Param(value = "name") String name, @Param(value = "mvnoId") Long mvnoId, @Param(value = "id") Long id);

	Optional<LeadSource> findByLeadSourceNameAndIsDeleteFalse(String name);

	List<LeadSource> findByLeadSourceNameContaining(String name);

	List<LeadSource> findByIsDeleteFalse();

	List<LeadSource> findByMvnoIdAndIsDeleteFalse(Long mvnoId);

	Page<LeadSource> findByMvnoIdAndIsDeleteFalse(Long mvnoId, Pageable pageable);

	Page<LeadSource> findByLeadSourceNameContainingAndIsDelete(String name, boolean deleted, Pageable pageable);

	Page<LeadSource> findByLeadSourceNameContainingAndMvnoIdAndIsDelete(String name, Long mvnoId, boolean deleted, Pageable pageable);

	@Query(name = "select * from TBLMLEADSOURCE where is_delete=false AND (mvno_id is NULL or mvno_id=:mvnoId) AND (bu_id is NULL or bu_id=:buId)", nativeQuery = true)
	List<LeadSource> findByMvnoIdAndBuId(Long mvnoId, Long buId);

	@Query(value = "select count(*) from LeadSource l where l.leadSourceName=:name and (l.mvnoId =:mvnoId or l.mvnoId=null) and  l.isDelete=false")
	Integer countForAdd(@Param(value = "name") String name, @Param(value = "mvnoId") Long mvnoId);

}

package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Dictionary;


@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long>,QuerydslPredicateExecutor<Dictionary>  {
	List<Dictionary> findByVendorContaining(String vendor);
	
	List<Dictionary> findByVendorIdContaining(String vendorId);
	
	//Search by vendorType
	@Query(value = "select * from tblmdictionary d where d.vendorType =:vendorType",nativeQuery = true)
	List<Dictionary> findByVendorType(@Param("vendorType") String vendorType);
	
	//Search by vendor and vendorId
	List<Dictionary> findByVendorContainingAndVendorIdContaining(String vendor, String vendorId);
	
	//Search by vendor , vendorId and vendor type
	@Query(value = "select * from tblmdictionary d where d.vendor LIKE %:vendor% AND d.vendorId LIKE %:vendorId% AND d.vendorType =:vendorType",nativeQuery = true)
	List<Dictionary> findByVendorContainingAndVendorIdContainingAndVendorType(@Param("vendor") String vendor,@Param("vendorId") String vendorId,@Param("vendorType") String vendorType);
	
	//Search by vendor and vendor type
	@Query(value = "select * from tblmdictionary d where d.vendor LIKE %:vendor% AND d.vendorType =:vendorType",nativeQuery = true)
	List<Dictionary> findByVendorContainingAndVendorType(@Param("vendor") String vendor,@Param("vendorType") String vendorType);
	
	//Search by vendorId and vendor type
	@Query(value = "select * from tblmdictionary d where d.vendorId LIKE %:vendorId% AND d.vendorType =:vendorType",nativeQuery = true)
	List<Dictionary> findByVendorIdContainingAndVendorType(@Param("vendorId") String vendorId,@Param("vendorType") String vendorType);
	
	Optional<Dictionary> findByVendor(String vendor);
	
	@Query(value = "select * from tblmdictionary d where d.dictionaryid !=:dictionaryId AND d.vendor =:vendor",nativeQuery = true)
	Optional<Dictionary> findByVendorOnUpdate(@Param("dictionaryId") Long dictionaryId, @Param("vendor") String vendor);
}

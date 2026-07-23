package com.savbill.salescrmsbss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.StaffUser;

//@JaversSpringDataAuditable
@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer>{

	@Query(name = "select * from tblstaffuser sf inner join tblstaffbusinessunitrel t on sf.staffid = t.staffid where sf.is_delete = false and t.businessunitid =:buId and sf.MVNOID =:mvnoId",nativeQuery = true)
	List<StaffUser> findByMvnoIdAndBusinessUnitNameList(@Param("mvnoId") Long mvnoId,@Param("buId") Long buId);

	@Query(name = "select * from tblstaffuser sf inner join tblstaffbusinessunitrel t on sf.staffid = t.staffid where sf.is_delete = false and t.businessunitid =:buId",nativeQuery = true)
	List<StaffUser> findByBusinessUnitNameList(@Param("buId") Long buId);

	@Query(name = "select * from tblstaffuser where is_delete = false and MVNOID =:mvnoId",nativeQuery = true)
	List<StaffUser> findByMvnoId(@Param("mvnoId") Long mvnoId);

	@Query(name = "select * from tblstaffuser where partnerid =:partnerId",nativeQuery = true)
	List<StaffUser> findAllByPartnerid(@Param("partnerId") Integer partnerId);


//	@Query(name = "select * from tblstaffuser where is_delete = false and username =:name",nativeQuery = true)
//	List<StaffUser> findByUsername(String name);
	Optional<StaffUser> findByUsername(String username);

	@Query(value = "SELECT staffid FROM savbillsalesscrms.tblstaffuser t WHERE t.parent_staff_id = :parentStaffId", nativeQuery = true)
	List<Integer> findAllByParentStaffId(@Param("parentStaffId") Integer parentStaffId);

	@Query("SELECT s.id, s.firstname, s.lastname FROM StaffUser s " + "WHERE s.partnerid = :partnerId AND s.isDelete = false AND (s.mvnoId = 1 OR s.mvnoId = :m_Id)")
	List<Object[]> findMinimalStaffUsers(@Param("partnerId") Integer partnerId, @Param("m_Id") Integer m_Id);

	@Query("SELECT s.id, b.id FROM StaffUser s JOIN s.businessUnitNameList b WHERE s.id IN :staffUserIds")
	List<Object[]> findBusinessUnitIdsForStaffUsers(@Param("staffUserIds") List<Integer> staffUserIds);
}

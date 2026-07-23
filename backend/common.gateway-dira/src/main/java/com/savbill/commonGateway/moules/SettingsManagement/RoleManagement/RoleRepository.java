package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {

    @Query(value = "select * from tblmroles where lower(rolename) like '%' || :search || '%' order by roleid",
            countQuery = "select count(*) from tblmroles where lower(rolename) like '%' || :search ",
            nativeQuery = true)
    Page<Role> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<Role> findByStatus(String status);

    List<Role> findByStatusAndIdIn(String status, List<Long> roleIds);

    @Query("select t from Role t where t.isDelete=false")
    List<Role> findAll();

    @Query("select t from Role t where t.product=:productType")
    List<Role> finadAllByProduct(String productType);

    @Query("select t from Role t where t.isDelete=false")
    Page<Role> findAll(Pageable page);

    @Query("select t from Role t where t.isDelete=false and t.mvnoId in :mvnoIds")
    Page<Role> findAll(Pageable page, @Param("mvnoIds")List mvnoIds);

    void deleteById(Long id);

    @Query(value = "select count(*) from tblmroles where rolename=:name and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmroles where rolename=:name and roleid =:id and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmroles where rolename=:name and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmroles where rolename=:name and roleid =:id and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    List<Role> findAllByRolename(String name);

    @Query(value = "select t from Role t where t.id IN (:roleIds)")
    List<Role> findRolenameByrolrids(List<Long> roleIds);

    @Query(value = "select new Role(t.id, t.rolename) from Role t where t.id IN (:roleIds)")
    List<Role> findRoleIdAndNameByrolrids(List<Long> roleIds);

    @Query(value = "select * from tblmroles t where t.roleid in (:roleIds) and t.rstatus = 'Active' and t.product =:productType ORDER BY t.roleid desc",nativeQuery = true)
    List<Role> findRolesByRoleIds(@Param("roleIds") List<Long> roleIds, @Param("productType") String productType);

    @Query(value = "select t.rolename from tblmroles t where t.roleid in (:roleIds) and t.rstatus = 'Active'",nativeQuery = true)
    List<String> findRoleNameByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Query(value = "select * from tblmroles t where t.roleid in (:roleIds)  and t.lcoid =:lcoid and t.rstatus = 'Active' and t.product =:productType ORDER BY t.roleid desc",nativeQuery = true)
    List<Role> findRolesByRoleIdsAndLcoId(@Param("roleIds") List<Long> roleIds, @Param("lcoid")Integer lcoid, @Param("productType") String productType );

    @Query(value = "SELECT * FROM tblmroles t WHERE t.roleid IN (:roleIds) AND t.lcoid = :lcoid AND t.rstatus = 'Active' AND t.product = :productType ORDER BY t.roleid desc",
            nativeQuery = true)
    Page<Role> findRolesByRoleIdsAndLcoIdPaginationWise(Pageable page, @Param("roleIds") List<Long> roleIds, @Param("lcoid")Integer lcoid, @Param("productType") String productType);

    @Query(value = "SELECT * FROM tblmroles t WHERE t.roleid IN (:roleIds) AND t.rstatus = 'Active' AND t.product = :productType ORDER BY t.roleid desc",
            nativeQuery = true)
    Page<Role> findRolesByRoleIdsPaginationWise(Pageable page, @Param("roleIds") List<Long> roleIds, @Param("productType") String productType);
    @Query(value = "select r.rolename from Role r where r.id= :roleId")
    String findRolenameByRoleId(Long roleId );
    @Query(
            value = "SELECT t.* FROM tblmroles t " +
                    "JOIN tblmstaffuser s ON t.CREATEDBYSTAFFID = s.staffid " +
                    "WHERE t.is_delete = false AND s.department = :department " +
                    "ORDER BY t.roleid DESC",

            countQuery = "SELECT COUNT(*) FROM tblmroles t " +
            "JOIN tblmstaffuser s ON t.CREATEDBYSTAFFID = s.staffid " +
            "WHERE t.is_delete = false AND s.department = :department",

            nativeQuery = true
    )
    Page<Role> findByDepartmentId(@Param("department") Integer department, Pageable pageRequest);

    @Query(
            value = "SELECT t.* FROM tblmroles t " +
                    "JOIN tblmstaffuser s ON t.CREATEDBYSTAFFID = s.staffid " +
                    "WHERE t.is_delete = false AND s.department = :department " +
                    "ORDER BY t.roleid DESC", nativeQuery = true
    )
    List<Role> findByDepartmentIdWithoutPagination(@Param("department") Integer department);

//    Page<Role> findAllByRolenameContainingAndMvnoIdAndIsDeleteIsFalse(String name, Integer mvnoId, Pageable pageable);
}

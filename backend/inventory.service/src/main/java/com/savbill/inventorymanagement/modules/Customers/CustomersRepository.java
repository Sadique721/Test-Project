package com.savbill.inventorymanagement.modules.Customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer>, QuerydslPredicateExecutor<Customers> {
    List<Customers> findByUsername(String username);
    Customers findByIdAndIsDeletedIsFalse(Integer id);
    Customers findByIdAndIsDeletedIsFalseAndMvnoIdIn(Integer id, Collection<Integer> mvnoId);
//    @Query(value = "select * from tblmcustomers t where t.servicearea_id IN :serviceAreaIds", nativeQuery = true)
//    List<Integer> findByServiceAreaIds(@Param("serviceAreaIds") List<Integer> serviceAreaIds);

    List<Customers> findAllByIsDeletedIsFalseAndStatusAndServiceareaIdIn(String status, List<Long> servicearea_id);

    Long countByPopidAndIsDeletedIsFalse(Long popId);
    Integer countByParentCustId(Integer parentCustId);
    Page<Customers> findAllByIsDeletedIsFalseAndStatusAndServiceareaId(String status, Long serviceAreaId,Pageable pageable);
    Page<Customers> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndServiceareaId(String status, List<Integer> mvnoId, Long serviceAreaId, Pageable pageable);

    List<Customers> findAllByIsDeletedIsFalseAndStatusAndServiceareaId(String status, Long serviceAreaId);
    List<Customers> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndServiceareaId(String status, List<Integer> mvnoId, Long serviceAreaId);
    List<Customers> findAllByServiceareaIdInAndMvnoIdInAndIsDeletedIsFalse(List<Long> servicearea_id, List<Integer> mvnoId);
    List<Customers> findAllByMvnoIdInAndIsDeletedIsFalse(List<Integer> mvnoId);

    @Query("SELECT new Customers( " +
            "c.id, c.username, c.mvnoId, c.status, c.custtype, c.buId, c.password) " +
            "FROM Customers c " +
            "WHERE c.isDeleted = false " +
            "AND c.servicearea.id IN :serviceareaIds " +
            "AND c.mvnoId IN :mvnoIds")
    List<Customers> findAllLightCustomerByServiceareaIdInAndMvnoIdInAndIsDeletedIsFalse(
            @Param("serviceareaIds") List<Long> serviceareaIds,
            @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new Customers( " +
            "c.id, c.username, c.mvnoId, c.status, c.custtype, c.buId, c.password) " +
            "FROM Customers c " +
            "WHERE c.isDeleted = false " +
            "AND c.id =:id")
    Customers findAllLightCustomerById(
            @Param("id") Integer id);

    @Query(value = "SELECT c.custid FROM tblmcustomers c WHERE c.is_deleted = false " +
            "AND (c.parent_experience IS NULL OR LOWER(c.parent_experience) = LOWER(:parentExperienceActual)) " +
            "AND (:checkServiceAreas = false OR c.servicearea_id IN (:serviceAreaIds)) " +
            "AND (:checkMvno = false OR c.MVNOID IN (:mvnoIds)) " +
            "AND (:checkBu = false OR c.BUID IN (:buIds)) " +
            "AND (:checkPartner = false OR c.partnerid = :partnerId)",
            nativeQuery = true)
    List<Integer> getCustomerIdsByNativeQuery(
            @Param("parentExperienceActual") String parentExperienceActual,
            @Param("checkServiceAreas") boolean checkServiceAreas,
            @Param("serviceAreaIds") List<Long> serviceAreaIds,
            @Param("checkMvno") boolean checkMvno,
            @Param("mvnoIds") List<Long> mvnoIds,
            @Param("checkBu") boolean checkBu,
            @Param("buIds") List<Long> buIds,
            @Param("checkPartner") boolean checkPartner,
            @Param("partnerId") int partnerId);

    @Query("SELECT c.username FROM Customers c WHERE c.isDeleted = false AND c.id =:id")
    String findCustomerUserNameBYId(@Param("id") Integer id);

}

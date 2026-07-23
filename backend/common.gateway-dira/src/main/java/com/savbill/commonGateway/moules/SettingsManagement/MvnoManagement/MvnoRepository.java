package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MvnoRepository extends JpaRepository<Mvno, Long>, QuerydslPredicateExecutor<Mvno> {
    Page<Mvno> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(String name, Pageable pageable);
    Page<Mvno> findAll(Pageable pageable);

    @Query("select t.name from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);

    Integer countByNameAndIsDeleteIsFalse(String name);
    Integer countByUsernameAndIsDeleteIsFalse(String username);
    Integer countByNameAndIsDeleteIsFalseAndId(String name, Long id);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDTO(m.id, m.name, m.username, m.password,m.status,m.isDelete) FROM Mvno m WHERE (:mvnoIds IS NULL OR m.id IN :mvnoIds) and m.isDelete = false and m.status = 'Active'")
    List<MvnoDTO> findAllByLatitudeAndLongitude(@Param("mvnoIds") List<Long> mvnoIds);

    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void UpdateMvnoidISP(@Param("oldMvnoid") Integer oldMvnoid, @Param("newMvnoid") Integer newMvnoid);


    @Query("select new com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoNameAndIdDTO(m.id, m.name) from Mvno m where m.status ='active'")
    List<MvnoNameAndIdDTO> findMvnoNameAndIdsForListing();

    @Query("SELECT m FROM Mvno m WHERE m.id = :id AND m.status = 'active'")
    Optional<Mvno> findActiveById(@Param("id") Long id);

    @Query("SELECT m.passwordPolicyId FROM Mvno m WHERE m.id = :mvnoId AND m.isDelete = false")
    Optional<Long> findPasswordPolicyIdByMvnoId(@Param("mvnoId") Long mvnoId);

    boolean existsByPasswordPolicyId(Long passwordPolicyId);

    boolean existsByCustAccountProfileId(Long custAccountProfileId);

    List<Mvno> findAllByCustAccountProfileId(Long custAccountProfileId);

    @Query("select new com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno(m.eventName, m.eventId) from Mvno m where m.id = :mvnoId")
    Optional<Mvno> findEventNameAndEventIdByMvnoId(@Param("mvnoId") Long mvnoId);

}

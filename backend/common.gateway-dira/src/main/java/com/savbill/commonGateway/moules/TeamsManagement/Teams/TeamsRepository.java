package com.savbill.commonGateway.moules.TeamsManagement.Teams;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface TeamsRepository extends JpaRepository<Teams, Long>, QuerydslPredicateExecutor<Teams> {
    List<Teams> findAllByIdIn(List<Long> ids);


    @Query(value = "SELECT * FROM tblmteams t where t.is_deleted = false AND lcoid IS NULL"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblmteams t where t.is_deleted = false AND lcoid IS NULL")
    Page<Teams> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM tblmteams t where t.is_deleted = false AND lcoid=:lcoId"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblmteams t where t.is_deleted = false AND lcoid=:lcoId")
    Page<Teams> findAll(Pageable pageable, @Param("lcoId") Integer lcoId);

    @Query(value = "SELECT * FROM tblmteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid IS NULL"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblmteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid IS NULL")
    Page<Teams> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "SELECT * FROM tblmteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid=:lcoId"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblmteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid=:lcoId")
    Page<Teams> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("lcoId") Integer lcoId);

    List<Teams> findAllByIdInAndIsDeletedIsFalse(List<Long> id);

    @Query(value = "select * from tblmteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tblmteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid IS NULL")
    Page<Teams> findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(@Param("s1") Integer partnerid, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblmteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tblmteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid=:lcoId")
    Page<Teams> findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(@Param("s1") Integer partnerid, Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid IS NULL", countQuery = "select count(*) from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid IS NULL", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    @Query(value = "select * from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid=:lcoId", countQuery = "select count(*) from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid=:lcoId", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid=:lcoId", countQuery = "select count(*) from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid=:lcoId", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid IS NULL", countQuery = "select count(*) from tblmteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid IS NULL", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds") List mvnoIds);


    @Query(value = "SELECT count(*) FROM tblmteams t where t.parentteamid = :parentTeamId", nativeQuery = true)
    Long checkTeamIsAlreadyParentTeam(@Param("parentTeamId") Long parentTeamId);

    Teams findByParentTeams(Teams teams);

    @Query(value = "select count(*) from tblmteams where team_name=:name and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmteams where team_name=:name and team_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmteams where team_name=:name and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmteams where team_name=:name and team_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    List<Teams> findAllByNameContainingIgnoreCase(String name);

    @Query(value = "select team_id from tbltteamusermapping where staffid =:staffid", nativeQuery = true)
    List<Long> findAllByStaff(@Param("staffid") Integer staffid);

    @Query(value = "select t.name from Teams t where t.id IN (:teamids)")
    List<String> findRolenameByrolrids(List<Long> teamids);

    List<Teams> findAllByIsDeletedIsFalseAndStatus(String status);

    List<Teams> findAllByIsDeletedIsFalseAndStatusAndMvnoId(String status, Integer mvnoId);

    List<Teams> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndProduct(String status, List<Integer> mvnoId, String product);

    List<Teams> findAllByIsDeletedIsFalseAndStatusAndPartner_Id(String status, Integer partner_id);

    List<Teams> findAllByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdIn(String status, Integer partner_id, List<Integer> mvnoId);

    List<Teams> findAllByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdInAndLcoId(String status, Integer partner_id, List<Integer> mvnoId, Integer lcoId);

    Page<Teams> findAllByIsDeletedIsFalse(Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndMvnoId(Integer mvnoId, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_Id(Integer partner_id, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_IdAndMvnoId(Integer partner_id, Integer mvnoId, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndLcoId(Integer partner_id, Integer mvnoId, Integer lcoId, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_IdAndNameContainingIgnoreCase(Integer partner_id, String name, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndNameContainingIgnoreCase(Integer partner_id, Integer mvnoId, String name, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndPartner_IdAndMvnoIdAndLcoIdAndNameContainingIgnoreCase(Integer partner_id, Integer mvnoId, Integer lcoId, String name, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndNameContainsIgnoreCase(String name, Pageable pageable);

    Page<Teams> findAllByIsDeletedIsFalseAndMvnoIdAndNameContainingIgnoreCase(Integer mvnoId, String name, Pageable pageable);

    @Query(value = "select * from tblmteams where product=:productType and is_deleted=false", nativeQuery = true)
    Page<Teams> findAllByProduct(String productType, Pageable pageable);

    //@Query(value = "select * from tblmteams t where t.product=:productType and t.is_deleted=false and t.MVNOID in mvnoId", nativeQuery = true)
    Page<Teams> findAllByProductAndIsDeletedFalseAndMvnoIdIn(String productType, Pageable pageable, List<Integer> mvnoId);

    @Query(value = "select * from tblmteams where product=:productType and is_deleted=false and team_status =:status", nativeQuery = true)
    List<Teams> findByProduct(String productType, String status);

    @Query(value = "SELECT team_id AS id, team_name AS name, team_status AS status, MVNOID AS mvnoId, partnerid AS partnerid, is_deleted AS isDeleted " +
            "FROM tblmteams WHERE team_status = :status AND is_deleted = FALSE",
            nativeQuery = true)
    List<TeamsMinimalDTO> findAllMinimalByStatus(@Param("status") String status);

    @Query(value = "SELECT team_id AS id, team_name AS name, team_status AS status, MVNOID AS mvnoId, partnerid AS partnerid, is_deleted AS isDeleted " +
            "FROM tblmteams WHERE team_status = :status AND partnerid = :partnerId AND is_deleted = FALSE",
            nativeQuery = true)
    List<TeamsMinimalDTO> findAllMinimalByStatusAndPartnerId(@Param("status") String status, @Param("partnerId") Long partnerId);

    @Query(value = "SELECT team_id AS id, team_name AS name, team_status AS status, MVNOID AS mvnoId, partnerid AS partnerid, is_deleted AS isDeleted " +
            "FROM tblmteams WHERE team_status = :status AND MVNOID IN (:mvnoIds) AND is_deleted = FALSE",
            nativeQuery = true)
    List<TeamsMinimalDTO> findAllMinimalByStatusAndMvno(
            @Param("status") String status,
            @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT team_id AS id, team_name AS name, team_status AS status, MVNOID AS mvnoId, partnerid AS partnerid, is_deleted AS isDeleted " +
            "FROM tblmteams WHERE team_status = :status AND partnerid = :partnerId AND MVNOID = :mvnoId AND is_deleted = FALSE",
            nativeQuery = true)
    List<TeamsMinimalDTO> findAllMinimalByStatusAndPartnerIdAndMvno(@Param("status") String status, @Param("partnerId") Long partnerId, @Param("mvnoId") Integer mvnoId);

    @Query(value = "select new Teams (t.id,t.name,t.parentTeams.id)from Teams t where t.status= :status and t.mvnoId in (:mvnoId) and t.product= :product and t.isDeleted=false ")
    List<Teams> findAllByStatusAndMvnoIdInAndProduct(String status, List<Integer> mvnoId, String product);

    @Query(value = "select new Teams (t.id,t.name,t.parentTeams.id) from Teams t where t.status= :status and t.mvnoId in (:mvnoId) and t.product= :product and t.partner.id= :partner_id and t.isDeleted=false ")
    List<Teams> findAllByStatusAndPartner_IdAndMvnoIdIn(String status, Integer partner_id, List<Integer> mvnoId);

    @Query(value = "select new Teams (t.id,t.name,t.parentTeams.id) from Teams t where t.status= :status and t.isDeleted=false ")
    List<Teams> findByIsDeletedIsFalseAndStatus(String status);

    @Query(value = "select new Teams (t.id,t.name,t.parentTeams.id) from Teams t where t.status= :status and t.mvnoId in (:mvnoId) and t.product= :product and t.partner.id= :partner_id and t.lcoId= :lcoId and t.isDeleted=false ")
    List<Teams> findByIsDeletedIsFalseAndStatusAndPartner_IdAndMvnoIdInAndLcoId(String status, Integer partner_id, List<Integer> mvnoId, Integer lcoId);

    @Query(value = "select new Teams (t.id,t.name,t.parentTeams.id)from Teams t where t.status= :status and t.partner.id= :partner_id and t.isDeleted=false ")
    List<Teams> findAllByStatusAndPartenerId(String status, Integer partner_id);

    @Query(value = "SELECT t.name from Teams t where t.id in (:teamids)")
    List<String> getTeamNameListByTeamId(Set<Long> teamids);

    @Query(value = "SELECT * FROM tblmteams t join tblmstaffuser s on t.CREATEDBYSTAFFID=s.staffid  where t.is_deleted = false and s.department= :departmentid "
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblmteams t join tblmstaffuser s on t.CREATEDBYSTAFFID=s.staffid  where t.is_deleted = false and s.department= :departmentid")
    Page<Teams> findByDepartmentId(Integer departmentid,Pageable pageable);

    @Query(
            value = "SELECT t.team_id AS id, " +
                    "       t.team_name AS name, " +
                    "       t.team_status AS status, " +
                    "       t.mvnoid AS mvnoId, " +
                    "       t.partnerid AS partnerid, " +
                    "       t.is_deleted AS isDeleted " +
                    "FROM tblmteams t " +
                    "JOIN tblmstaffuser s ON t.CREATEDBYSTAFFID = s.staffid " +
                    "WHERE t.team_status = :status " +
                    "AND t.partnerid = :partnerId " +
                    "AND t.is_deleted = false " +
                    "AND s.department = :departmentid",
            nativeQuery = true
    )
    List<TeamsMinimalDTO> findByDepartmentIdWithoutPagination(
            @Param("status") String status,
            @Param("partnerId") Long partnerId,
            @Param("departmentid") Integer departmentId
    );
}

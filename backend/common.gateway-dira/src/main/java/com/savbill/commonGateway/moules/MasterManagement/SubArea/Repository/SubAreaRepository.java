package com.savbill.commonGateway.moules.MasterManagement.SubArea.Repository;

import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SubAreaRepository extends JpaRepository<SubArea,Long>, QuerydslPredicateExecutor<SubArea> {
    @Query("SELECT s FROM SubArea s WHERE s.isDeleted = false  order by s.id desc")
    Page<SubArea> findAllActiveSubAreas(Pageable pageable);


    @Query(value = "select subareaid from tblmsubarea where name=:name and is_deleted=false", nativeQuery = true)
    Integer findIdByName(String name);

    @Query(value = "select areaid from tblmsubarea where subareaid=:subareaid and is_deleted=false", nativeQuery = true)
    Integer findAreaIdBySubAreaId(Long subareaid);

    @Query(value = "SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea(s.id, s.name, s.status, s.mvnoId) from SubArea s where s.area.id= :areaId and s.status = 'Active'")
    List<SubArea> findSubAreaFromAreaID(@Param("areaId") Long areaId);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea(a.id, a.name, a.status, a.mvnoId) " +
            "FROM SubArea a WHERE a.isDeleted = false AND a.mvnoId IN (:mvnoIds)")
    Page<SubArea> findAllByMvnoIds(@Param("mvnoIds") List mvnoIds, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea(a.id, a.name, a.status, a.mvnoId) " +
            "FROM SubArea a WHERE a.isDeleted = false")
    Page<SubArea> findAll(Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea(a.id, a.name, a.status, a.countryId, a.cityId, a.stateId, a.area.id, a.mvnoId,a.filename,a.uniquename) " +
            "FROM SubArea a WHERE a.id = :id")
    SubArea findSubAreaById(Long id);

    @Query(value = "select s.id from SubArea s where s.name=:name and s.isDeleted=false")
    List<Integer> findIdsByName( @Param("name")String name);


    @Modifying
    @Transactional
    @Query("update SubArea s set s.isDeleted=true where s.id=:id")
    void deleteById(@Param("id") Long id);

//    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO(" +
//            "a.id, a.name, a.status, a.mvnoId) " +
//            "FROM SubArea a WHERE a.isDeleted = false AND a.mvnoId IN (:mvnoIds)")
//    Page<SubAreaDTO> findAllSubAreaByMvnoIds(@Param("mvnoIds") List<Integer> mvnoIds, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO(" +
            "sa.id, sa.name, sa.status, sa.mvnoId, a.id, a.name, sa.cityId, sa.stateId, sa.countryId, sa.isDeleted, " +
            "p.id, p.pincode, c.name, s.name, ci.name) " +
            "FROM SubArea sa " +
            "LEFT JOIN sa.area a " +
            "LEFT JOIN a.pincode p " +
            "LEFT JOIN Country c ON c.id = sa.countryId " +
            "LEFT JOIN State s ON s.id = sa.stateId " +
            "LEFT JOIN City ci ON ci.id = sa.cityId " +
            "WHERE sa.isDeleted = false " +
            "AND sa.mvnoId IN :mvnoIds " +
            "ORDER BY sa.id DESC")
    Page<SubAreaDTO> findAllSubAreaByMvnoIds(@Param("mvnoIds") List<Integer> mvnoIds, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO(" +
            "a.id, a.name, a.status, a.mvnoId) " +
            "FROM SubArea a WHERE a.isDeleted = false")
    Page<SubAreaDTO> findAllProjected(Pageable pageable);

    Long countByNameAndIsDeletedIsFalseAndCityIdAndStateId(String name, Integer cityId, Integer stateId);

    Long countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndMvnoIdIn(String name, Integer cityId, Integer stateId, List<Integer> mvnoIds);

    Long countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndId(String name, Integer cityId, Integer stateId, Long id);

    Long countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndMvnoIdInAndId(String name, Integer cityId, Integer stateId, List<Integer> mvnoIds, Long id);

    @Query("SELECT COUNT(s) " +
                    "FROM SubArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.name = :name " +
                    "AND s.cityId = :cityId " +
                    "AND s.stateId = :stateId " +
                    "AND s.area.id = :areaId " +
                    "AND s.area.pincode.id = :pincodeId"
    )
    Long countDuplicate(@Param("name") String name, @Param("cityId") Integer cityId, @Param("stateId") Integer stateId, @Param("areaId") Long areaId, @Param("pincodeId") Long pincodeId);

    @Query("SELECT COUNT(s) " +
                    "FROM SubArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.name = :name " +
                    "AND s.cityId = :cityId " +
                    "AND s.stateId = :stateId " +
                    "AND s.area.id = :areaId " +
                    "AND s.area.pincode.id = :pincodeId " +
                    "AND s.mvnoId IN :mvnoIds"
    )
    Long countDuplicateWithMvno(@Param("name") String name, @Param("cityId") Integer cityId, @Param("stateId") Integer stateId, @Param("areaId") Long areaId, @Param("pincodeId") Long pincodeId, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT COUNT(s) " +
                    "FROM SubArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.name = :name " +
                    "AND s.cityId = :cityId " +
                    "AND s.stateId = :stateId " +
                    "AND s.area.id = :areaId " +
                    "AND s.area.pincode.id = :pincodeId " +
                    "AND s.id <> :id"
    )
    Long countDuplicateExcludingSelf(@Param("name") String name, @Param("cityId") Integer cityId, @Param("stateId") Integer stateId, @Param("areaId") Long areaId, @Param("pincodeId") Long pincodeId, @Param("id") Long id);

    @Query("SELECT COUNT(s) " +
                    "FROM SubArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.name = :name " +
                    "AND s.cityId = :cityId " +
                    "AND s.stateId = :stateId " +
                    "AND s.area.id = :areaId " +
                    "AND s.area.pincode.id = :pincodeId " +
                    "AND s.mvnoId IN :mvnoIds " +
                    "AND s.id <> :id"
    )
    Long countDuplicateExcludingSelfWithMvno(@Param("name") String name, @Param("cityId") Integer cityId, @Param("stateId") Integer stateId, @Param("areaId") Long areaId, @Param("pincodeId") Long pincodeId, @Param("mvnoIds") List<Integer> mvnoIds, @Param("id") Long id);

    boolean existsByArea(Area area);
}

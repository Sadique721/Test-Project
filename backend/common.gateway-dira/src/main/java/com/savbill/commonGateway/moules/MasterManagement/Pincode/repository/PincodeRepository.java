package com.savbill.commonGateway.moules.MasterManagement.Pincode.repository;


import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDTO;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeMvnoDto;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeRespDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface PincodeRepository extends JpaRepository<Pincode, Long>, QuerydslPredicateExecutor<Pincode> {
    Pincode findByPincodeAndIsDeletedIsFalse(String pincode);

    Page<Pincode> findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalse(String pincode, Pageable pageable);

    Page<Pincode> findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String pincode, Pageable pageable, List mvnoIds);

    @Query("SELECT t from Pincode t WHERE t.isDeleted = false")
    Page<Pincode> findAll(Pageable pageable);

    List<Pincode> findAllByPincodeStartingWithAndIsDeletedIsFalse(String s1);

    @Query(value = "select sum(tbl.tab) from(\n" +
            /*
            "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.PINCODEID =:id and t4.is_delete =false \n" +
            "union all \n" + */
            "select count(*) as tab from tblmarea t5  where t5.pincodeid =:id and t5.is_deleted =false \n" +
            ")tbl", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("pincode") String pincode, @Param("mvnoIds") List mvnoIds);


    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and pincodeid =:id and cityId=:cityId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("pincode") String pincode, @Param("id") Long id, @Param("cityId") Integer cityid, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("pincode") String pincode);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and pincodeid =:id and cityId=:cityId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("pincode") String pincode, @Param("id") Long id, @Param("cityId") Integer cityid);

    @Query(value = "select t from Pincode t where t.isDeleted=false and mvnoId in :mvnoIds")
    Page<Pincode> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and cityId=:cityId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSaveWithPincodeAndCityID(@Param("pincode") String pincode, @Param("cityId") Integer cityId, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmpincode where pincode=:pincode and cityId=:cityId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSaveWithPincodeAndCityID(@Param("pincode") String pincode, @Param("cityId") Integer cityId);

    @Query(value = "SELECT pincode from tblmpincode where pincodeid=:id and is_deleted=false", nativeQuery = true)
    String getPincode(@Param("id") Long id);

    @Query(value = "SELECT pincode from tblmpincode where pincodeid=:id", nativeQuery = true)
    String getPincodeByPincodeId(@Param("id") Long id);

    //List<Pincode> findByPincode(String pincode);

    @Query(value = "select pincodeid from tblmpincode where pincode=:pincode and status = 'Active' and is_deleted = false", nativeQuery = true)
    Integer findIdByPincode(String pincode);

    List<Pincode> findByPincodeContainingIgnoreCase(String pincode);

    @Query(value = "select * from tblmpincode where cityid=:id and status = 'Active' and is_deleted = false", nativeQuery = true)
    List<Pincode> findallcitybyid(@Param("id") Integer id);

    @Query(value = "select pincodeid, pincode from tblmpincode where cityid=:id and status = 'Active' and is_deleted = false", nativeQuery = true)
    List<PincodeDTO> findallcitybyidWithSpecificParameter(@Param("id") Integer id);

    @Query(value = "SELECT pincodeid, pincode FROM tblmpincode WHERE cityid = :id AND status = 'Active' AND is_deleted = false", nativeQuery = true)
    List<Object[]> findPincodeByCityId(@Param("id") Integer id);


    Long countByPincodeAndIsDeletedIsFalseAndAndCityId(String pincode, Integer cityId);

    Long countByPincodeAndIsDeletedIsFalseAndAndCityIdAndMvnoIdIn(String pincode, Integer cityId, List<Integer> mvnoIds);

    Long countByPincodeAndIsDeletedIsFalseAndAndCityIdAndId(String pincode, Integer cityId, Long id);

    Long countByPincodeAndIsDeletedIsFalseAndAndCityIdAndMvnoIdInAndId(String pincode, Integer cityId, List<Integer> mvnoIds, Long id);

    //    @Query("SELECT c.name FROM TBLMCITY c WHERE c.id = (SELECT p.cityId FROM tblmpincode p WHERE p.pincode = :pincode)")
//    String findCityNameByPincode(@Param("pincode") String pincode);
    @Query("SELECT c.name FROM City c JOIN Pincode p ON p.cityId = c.id WHERE p.pincode = :pincode")
    String findCityNameByPincode(@Param("pincode") String pincode);

    //    String findCityNameByPincode(@Param("pincode") String pincode);
    Optional<Pincode> findAllByIdAndPincode(Long id, String pincode);

    boolean existsByPincode(String pincode);

    @Query("select new com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeRespDTO(p.id, p.pincode ,p.status) from Pincode p where p.mvnoId in :mvnoId and p.isDeleted=false")
    List<PincodeRespDTO> findAll(List<Integer> mvnoId);

    @Query("select new com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeRespDTO(p.id, p.pincode ,p.status) from Pincode p where p.isDeleted=false")
    List<PincodeRespDTO> findAllPinCode();

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeMvnoDto(p.id, p.mvnoId) " +
            "FROM Pincode p " +
            "WHERE p.id IN :pincodes AND (:mvnoId = 1 OR p.mvnoId = :mvnoId)")
    List<PincodeMvnoDto> findAllById(@Param("pincodes") List<Long> pincodes, @Param("mvnoId") Integer mvnoId);

    @Query("SELECT p.id FROM Pincode p WHERE p.id IN :pincodes")
    List<Long> findExistingIdsByIds(@Param("pincodes") List<Long> pincodes);


    @Query(value = "SELECT " +
            "p.pincodeid, " +
            "p.pincode, " +
            "p.status, " +
            "p.countryid, " +
            "p.stateid, " +
            "p.cityid, " +
            "c.name AS cityName, " +
            "s.name AS stateName, " +
            "co.name AS countryName, " +
            "'' AS areas, " +
            "p.pincodeid AS displayId, " +
            "p.pincode AS displayName, " +
            "p.mvnoid, " +
            "p.createdate, " +
            "p.lastmodifieddate, " +
            "p.createbyname, " +
            "p.updatebyname, " +
            "p.createdbystaffid, " +
            "p.lastmodifiedbystaffid " +
            "FROM tblmpincode p " +
            "LEFT JOIN tblmarea a ON p.pincodeid = a.pincodeid " +
            "LEFT JOIN tblmcity c ON p.cityid = c.cityid " +
            "LEFT JOIN tblmstate s ON p.stateid = s.stateid " +
            "LEFT JOIN tblmcountry co ON p.countryid = co.countryid " +
            "WHERE p.pincodeid = :id " +
            "GROUP BY p.pincodeid;", nativeQuery = true)
    Object[] getPincodeDtoById(@Param("id") Long id);

    @Query(value = "SELECT a.name " +
            "FROM tblmarea a " +
            "WHERE a.pincodeid = :id ", nativeQuery = true)
    List<String> getAreaList(@Param("id") Long id);

    List<Pincode> findByIsDeletedFalseAndMvnoIdIn(List<Integer> mvnoIds);
}

package com.savbill.commonGateway.moules.MasterManagement.City.repository;



import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.model.CityPojo;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
@Repository
public interface CityRepository extends JpaRepository<City, Integer>, QuerydslPredicateExecutor<City> {

    @Query("SELECT p.pincode FROM Pincode p WHERE p.cityId = :cityId")
    String findPincodeByCityId(@Param("cityId") Integer cityId);

    @Query(value = "select * from TBLMCITY where lower(name) like '%' :search  '%' order by CITYID AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMCITY where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<City> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    List<City> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);

    List<City> findByStateAndIsDeleteIsFalseOrderByIdDesc(State state);

    List<City> findByName(String name);
    List<City> findByNameContainingIgnoreCase(String name);

    @Query("select t from City t where t.isDelete=false")
    List<City> findAll();

    @Query(value = "select * from tblmcity t where t.is_delete=false", nativeQuery = true)
    Page<City> findAll(Pageable pageable);

    @Query("update City b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select city.* from tblmcity city \n" +
            "left join tblmstate state \n" +
            "on state.STATEID = city.STATEID \n" +
            "left join tblmcountry country \n" +
            "on country.COUNTRYID = city.countryid \n" +
            "where city.is_delete = false AND city.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblmcity city\n" +
            "left join tblmstate state \n" +
            "on state.STATEID = city.STATEID \n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = city.countryid \n" +
            "where city.is_delete = false AND city.MVNOID in :mvnoIds"
            , nativeQuery = true)
    Page<City> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true
            , value = "select * from tblmcity city\n" +
            "left join tblmstate state\n" +
            "on state.STATEID = city.STATEID\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = city.countryid\n" +
            "where (city.NAME like '%' :s1 '%' or state.NAME like '%' :s2 '%' or country.NAME like '%' :s3 '%'or city.status=:s4) \n" +
            "and city.is_delete = false AND city.MVNOID in :MVNOID"
            , countQuery = "select count(*) from tblmcity city\n" +
            "left join tblmstate state\n" +
            "on state.STATEID = city.STATEID\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = city.countryid\n" +
            "where (city.NAME like '%' :s1 '%' or state.NAME like  '%' :s2 '%' or country.NAME like '%' :s3 '%' or city.status=:s4) \n" +
            "and city.is_delete = false AND city.MVNOID in :MVNOID")
    Page<City> findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, Pageable pageable, @Param("MVNOID") List MVNOID);

    @Query(nativeQuery = true
            , value = "select * from tblmcity city\n" +
            "left join tblmstate state\n" +
            "on state.STATEID = city.STATEID\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = city.countryid\n" +
            "where (city.NAME like '%' :s1 '%' or state.NAME like '%' :s2 '%' or country.NAME like '%' :s3 '%' or city.status=:s4) \n" +
            "and city.is_delete = false"
            , countQuery = "select count(*) from tblmcity city\n" +
            "left join tblmstate state\n" +
            "on state.STATEID = city.STATEID\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = city.countryid\n" +
            "where (city.NAME like '%' :s1 '%' or state.NAME like  '%' :s2 '%' or country.NAME like '%' :s3 '%' or city.status=:s4) \n" +
            "and city.is_delete = false")
    Page<City> findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, Pageable pageable);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name  and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.COUNTRYID =:countryId and c.STATEID =:STATEID  and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyStateAtSave(@Param("name") String name, @Param("countryId") Integer countryId , @Param("STATEID") Integer STATEID);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.CITYID =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.CITYID =:id and c.COUNTRYID =:countryId and c.STATEID =:STATEID  and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyStateAtEdit(@Param("name") String name, @Param("countryId") Integer countryId , @Param("STATEID") Integer STATEID,@Param("id") Integer id);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name  and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.COUNTRYID =:countryId and c.STATEID =:STATEID  and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyStateAtSave(@Param("name") String name, @Param("countryId") Integer countryId , @Param("STATEID") Integer STATEID, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.CITYID =:id and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmcity c where c.NAME=:name and c.CITYID =:id and c.COUNTRYID =:countryId and c.STATEID =:STATEID  and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyStateAtEdit(@Param("name") String name, @Param("countryId") Integer countryId , @Param("STATEID") Integer STATEID,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmpincode t3 where t3.CITYID =:id and t3.is_deleted =false \n" +
            "union all\n" +
            /*
            "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.CITYID =:id and t4.is_delete =false \n" +
            "union all \n" + */
            "select count(*) as tab from tblmarea t5  where t5.CITYID =:id and t5.is_deleted =false \n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);


    Long countByNameAndIsDeleteIsFalseAndCountryIdAndState_Id(String name, Integer country_id, Integer state_id);
    Long countByNameAndIsDeleteIsFalseAndMvnoIdInAndCountryIdAndState_Id(String name, List<Integer> mvnoId, Integer country_id, Integer state_id);
    Long countByNameAndIdAndIsDeleteIsFalseAndCountryIdAndState_Id(String name, Integer id, Integer country_id, Integer state_id);
    Long countByNameAndIdAndIsDeleteIsFalseAndMvnoIdInAndCountryIdAndState_Id(String name, Integer id, List<Integer> mvnoId, Integer country_id, Integer state_id);
    Optional<City> findAllByIdAndName(Integer id, String name);



    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.City.model.CityPojo(" +
            "c.id, c.name, c.status, c.countryId, country.name, c.state.name, c.isDelete, c.mvnoId, c.id, c.name) " +
            "FROM City c " +
            "LEFT JOIN Country country ON country.id = c.countryId " +
            "WHERE c.status = :status AND c.isDelete = false " +
            "AND (c.mvnoId = :mvnoId OR c.mvnoId IS NULL OR c.mvnoId = 1 OR :mvnoId = 1) " +
            "ORDER BY c.id DESC")
    List<CityPojo> findAllActiveCitiesByStatusAndMvnoId(@Param("status") String status,
                                                        @Param("mvnoId") Integer mvnoId);

    @Query("SELECT c.name FROM City c WHERE c.id = :cityId")
    String findNameByCityId(@Param("cityId") Integer cityId);

}

package com.savbill.partnermanagement.modules.partner.repository;

import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
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
import java.util.Map;

@JaversSpringDataAuditable
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>, QuerydslPredicateExecutor<Partner> {

    List<Partner> findByStatusAndIsDeleteIsFalse(String status);

    @Query(value = "select * from tblpartners where partnerid <> :id", nativeQuery = true)
    List<Partner> getAllParentPartners(@Param("id") Integer id);

    @Override
    @Query("select p from Partner p where p.isDelete=false")
    List<Partner> findAll();

    @Query(value = "select * from tblpartners as t where t.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            , nativeQuery = true
            , countQuery = "select count(*) from tblpartners as t where t.is_delete = false and MVNOID = :mvnoId AND t.BUID in :buIds")
    Page<Partner> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select new com.savbill.partnermanagement.modules.partner.dto.PartnerPojo(p.id, p.name, p.status, p.commtype, p.commrelvalue, p.balance, p.commdueday, p.nextbilldate, p.lastbilldate, p.taxid, p.credit, p.addresstype, p.address1, p.address2, p.city, p.state, p.country, p.pincode, p.mobile, p.countryCode, p.prcode, p.partnerType, p.email, p.parentPartner.id, p.isDelete, p.priceBookId.id, p.calendarType, p.commissionShareType, p.mvnoId, p.buId, p.creditConsume, p.id, p.name, p.region, p.branch, p.bussinessvertical, p.commissionInterval, p.isVisibleToIsp, p.createdate, p.updatedate, c.name, co.name, s.name, t.name, pp.name, p.balance, p.totalCustomerCount, p.renewCustomerCount, p.newCustomerCount, pb.bookname, sa.id, sa.name) " +
            "from tblpartners as p Left join City c on c.id = p.city " +
            "LEFT JOIN State s ON s.id = p.state " +
            "LEFT JOIN Country co ON co.id = p.country " +
            "LEFT JOIN Tax t ON t.id = p.taxid " +
            "LEFT JOIN Partner pp ON pp.id = p.parentPartner.id " +
            "LEFT JOIN PriceBook1 pb ON pb.id = p.priceBookId.id " +
            "LEFT JOIN p.serviceAreaList sa " +
            "where p.is_delete = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))"
            , nativeQuery = true
            , countQuery = "select count(*) from tblpartners as t where t.is_delete = false and MVNOID = :mvnoId AND t.BUID in :buIds")
    Page<PartnerPojo> findAllPartnerPojo(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tblpartners as t where t.is_delete = false and t.MVNOID in :mvnoId"
            , nativeQuery = true
            , countQuery = "select count(*) from tblpartners as t where t.is_delete = false and t.MVNOID in :mvnoId")
    Page<Partner> findAll(Pageable pageable, @Param("mvnoId") List mvnoId);

    @Query("SELECT new com.savbill.partnermanagement.modules.partner.dto.PartnerPojo(p.id, p.name, p.status, p.commtype, p.commrelvalue, p.balance, p.commdueday, p.nextbilldate, p.lastbilldate, p.taxid, p.credit, p.addresstype, p.address1, p.address2, p.city, p.state, p.country, p.pincode, p.mobile, p.countryCode, p.prcode, p.partnerType, p.email, p.parentPartner.id, p.isDelete, p.priceBookId.id, p.calendarType, p.commissionShareType, p.mvnoId, p.buId, p.creditConsume, p.id, p.name, p.region, p.branch, p.bussinessvertical, p.commissionInterval, p.isVisibleToIsp, p.createdate, p.updatedate, c.name, co.name, s.name, t.name, pp.name, p.balance, p.totalCustomerCount, p.renewCustomerCount, p.newCustomerCount, pb.bookname, sa.id, sa.name) " +
            "FROM Partner p Left join City c on c.id = p.city " +
            "LEFT JOIN State s ON s.id = p.state " +
            "LEFT JOIN Country co ON co.id = p.country " +
            "LEFT JOIN Tax t ON t.id = p.taxid " +
            "LEFT JOIN Partner pp ON pp.id = p.parentPartner.id " +
            "LEFT JOIN PriceBook1 pb ON pb.id = p.priceBookId.id " +
            "LEFT JOIN p.serviceAreaList sa " +
            "WHERE p.isDelete = false and p.mvnoId IN :mvnoIds")
    Page<PartnerPojo> findAllPartnerPojo(Pageable pageable, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.partnermanagement.modules.partner.dto.PartnerPojo(" +
            "p.id, p.name, p.status, p.commtype, p.commrelvalue, p.balance, " +
            "p.commdueday, p.nextbilldate, p.lastbilldate, p.taxid, p.credit, p.addresstype, " +
            "p.address1, p.address2, p.city, p.state, p.country, p.pincode, p.mobile, " +
            "p.countryCode, p.prcode, p.partnerType, p.email, p.parentPartner.id, " +
            "p.isDelete, p.priceBookId.id, p.calendarType, p.commissionShareType, " +
            "p.mvnoId, p.buId, p.creditConsume, p.id, p.name, p.region, p.branch, " +
            "p.bussinessvertical, p.commissionInterval, p.isVisibleToIsp, p.createdate, " +
            "p.updatedate, c.name, co.name, s.name, t.name, pp.name, p.balance, " +
            "p.totalCustomerCount, p.renewCustomerCount, p.newCustomerCount, pb.bookname, " +
            "sa.id, sa.name) " +
            "FROM Partner p " +
            "LEFT JOIN City c ON c.id = p.city " +
            "LEFT JOIN State s ON s.id = p.state " +
            "LEFT JOIN Country co ON co.id = p.country " +
            "LEFT JOIN Tax t ON t.id = p.taxid " +
            "LEFT JOIN Partner pp ON pp.id = p.parentPartner.id " +
            "LEFT JOIN PriceBook1 pb ON pb.id = p.priceBookId.id " +
            "LEFT JOIN p.serviceAreaList sa " +
            "WHERE p.isDelete = false AND p.id = :id")
    List<PartnerPojo> findPartnerPojoListById(@Param("id") Integer id);

    @Query(value = "select distinct t.* from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " where t3.serviceareaid in :serviaceareaId group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            "inner join tblpartnerservicearearel t5 on t5.partnerid = t.partnerid\n" +
            "where t5.serviceareaid in :serviaceareaId  and t.is_delete = 0 AND t.MVNOID in :mvnoIds"
            , countQuery ="select count(distinct t.partnerid) from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " where t3.serviceareaid in :serviaceareaId group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            "inner join tblpartnerservicearearel t5 on t5.partnerid = t.partnerid\n" +
            "where t5.serviceareaid in :serviaceareaId  and t.is_delete = 0 AND t.MVNOID in :mvnoIds" , nativeQuery = true)
    Page<Partner> findAll(Pageable pageable,@Param("serviaceareaId") List serviaceareaId, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select distinct new com.savbill.partnermanagement.modules.partner.dto.PartnerPojo(p.id, p.name, p.status, p.commtype, p.commrelvalue, p.balance, p.commdueday, p.nextbilldate, p.lastbilldate, p.taxid, p.credit, p.addresstype, p.address1, p.address2, p.city, p.state, p.country, p.pincode, p.mobile, p.countryCode, p.prcode, p.partnerType, p.email, p.parentPartner.id, p.isDelete, p.priceBookId.id, p.calendarType, p.commissionShareType, p.mvnoId, p.buId, p.creditConsume, p.id, p.name, p.region, p.branch, p.bussinessvertical, p.commissionInterval, p.isVisibleToIsp, p.createdate, p.updatedate, c.name, co.name, s.name, t.name, pp.name, p.balance, p.totalCustomerCount, p.renewCustomerCount, p.newCustomerCount, pb.bookname, sa.id, sa.name) " +
            "from tblpartners p Left join City c on c.id = p.city " +
            "LEFT JOIN State s ON s.id = p.state " +
            "LEFT JOIN Country co ON co.id = p.country " +
            "LEFT JOIN Tax t ON t.id = p.taxid " +
            "LEFT JOIN Partner pp ON pp.id = p.parentPartner.id " +
            "LEFT JOIN PriceBook1 pb ON pb.id = p.priceBookId.id " +
            "LEFT JOIN p.serviceAreaList sa " +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " where t3.serviceareaid in :serviaceareaId group by t2.partnerid ) srn on srn.partnerid = p.PARTNERID \n" +
            "inner join tblpartnerservicearearel t5 on t5.partnerid = p.partnerid\n" +
            "where t5.serviceareaid in :serviaceareaId  and p.is_delete = 0 AND p.MVNOID in :mvnoIds"
            , countQuery ="select count(distinct p.partnerid) from tblpartners p \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " where t3.serviceareaid in :serviaceareaId group by t2.partnerid ) srn on srn.partnerid = p.PARTNERID \n" +
            "inner join tblpartnerservicearearel t5 on t5.partnerid = p.partnerid\n" +
            "where t5.serviceareaid in :serviaceareaId  and p.is_delete = 0 AND p.MVNOID in :mvnoIds" , nativeQuery = true)
    Page<PartnerPojo> findAllPartnerPojo(Pageable pageable,@Param("serviaceareaId") List serviaceareaId, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0"
            , countQuery = "select count(*) from tblpartners t \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = t.PARTNERID \n" +
            " where t.is_delete = 0", nativeQuery = true)
    Page<Partner> findAll(Pageable pageable);

    @Query(value = "select new com.savbill.partnermanagement.modules.partner.dto.PartnerPojo(p.id, p.name, p.status, p.commtype, p.commrelvalue, p.balance, p.commdueday, p.nextbilldate, p.lastbilldate, p.taxid, p.credit, p.addresstype, p.address1, p.address2, p.city, p.state, p.country, p.pincode, p.mobile, p.countryCode, p.prcode, p.partnerType, p.email, p.parentPartner.id, p.isDelete, p.priceBookId.id, p.calendarType, p.commissionShareType, p.mvnoId, p.buId, p.creditConsume, p.id, p.name, p.region, p.branch, p.bussinessvertical, p.commissionInterval, p.isVisibleToIsp, p.createdate, p.updatedate, c.name, co.name, s.name, t.name, pp.name, p.balance, p.totalCustomerCount, p.renewCustomerCount, p.newCustomerCount, pb.bookname, sa.id, sa.name) " +
            "from tblpartners p Left join City c on c.id = p.city " +
            "LEFT JOIN State s ON s.id = p.state " +
            "LEFT JOIN Country co ON co.id = p.country " +
            "LEFT JOIN Tax t ON t.id = p.taxid " +
            "LEFT JOIN Partner pp ON pp.id = p.parentPartner.id " +
            "LEFT JOIN PriceBook1 pb ON pb.id = p.priceBookId.id " +
            "LEFT JOIN p.serviceAreaList sa " +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = p.PARTNERID \n" +
            " where p.is_delete = 0"
            , countQuery = "select count(*) from tblpartners p \n" +
            "left join (" +
            " select t2.partnerid,group_concat(t4.name) concatname from tblpartners t2\n" +
            " inner join tblpartnerservicearearel t3 on t3.partnerid = t2.partnerid\n" +
            " inner join tblmservicearea t4 on t4.service_area_id = t3.serviceareaid \n" +
            " group by t2.partnerid ) srn on srn.partnerid = p.PARTNERID \n" +
            " where p.is_delete = 0", nativeQuery = true)
    Page<PartnerPojo> findAllPartnerPojo(Pageable pageable);

    @Query("update Partner p set p.isDelete=true where p.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "select * from tblpartners t where (t.PARTNERNAME like '%' :s1 '%'  or t.mobile like '%' :s2 '%' or t.email like '%' :s3 '%')\n" +
            "and t.is_delete = 0")
    List<Partner> searchPartner(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId")Integer mvnoId , @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false and t.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoId")Integer mvnoIds ,@Param("buIds") List buIds );

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblpartners t where t.PARTNERNAME=:name and t.PARTNERID =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select CREATEDBYSTAFFID from tblpartners where PARTNERNAME=:name and is_delete=false and MVNOID=:mvnoId and BUID in :buIds", nativeQuery = true)
    Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select CREATEDBYSTAFFID from tblpartners where PARTNERNAME=:name and is_delete=false and MVNOID=:mvnoId", nativeQuery = true)
    Integer getCreatedBy(@Param("name")String name,  @Param("mvnoId")Integer mvnoId);

    Partner findByIdAndIsDeleteIsFalseAndMvnoIdIn(Integer id, List<Integer> asList);

    Partner findByIdAndIsDeleteIsFalse(Integer id);

   @Query(value = "select count(PARTNERID) from tblpartners t where t.email = :emailId", nativeQuery = true)
    Integer emailCount(@Param("emailId") String emailId);

    @Query(value = "select * from tblpartners as t where t.is_delete = false and parentpartnerid=:partnerId"
            , nativeQuery = true
            , countQuery = "select count(*) from tblpartners as t where t.is_delete = false and parentpartnerid=:partnerId")
    Page<Partner> findAll(Pageable pageable, @Param("partnerId") Integer partnerId);

    @Query(value = "select * from tblpartners where is_delete = false and parentpartnerid IS NOT NULL AND parentpartnerid=:id", nativeQuery = true)
    List<Partner> getAllChildPartners(@Param("id") Integer id);

    List<Partner> findByParentPartnerId(int parentPartnerId);

}

package com.diameter.repository;

import com.diameter.enums.PackageType;
import com.diameter.model.RatePackageGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RatePackageGroupMappingRepo extends JpaRepository<RatePackageGroupMapping, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tblt_rate_package_group_mapping WHERE group_id = :groupId", nativeQuery = true)
    void deleteMappingsByGroupId(Long groupId);

    /**
     * Find all mappings for a specific group
     */
    List<RatePackageGroupMapping> findByGroupId(Long groupId);
    /**
     * Find active mappings for a group (within effective dates)
     * CRITICAL for zone-based rating
     */
    @Query("SELECT rpm FROM RatePackageGroupMapping rpm " +
            "WHERE rpm.groupId = :groupId " +
            "AND rpm.effectiveDate <= :currentDate " +
            "AND (rpm.expiryDate IS NULL OR rpm.expiryDate >= :currentDate)")
    List<RatePackageGroupMapping> findActiveByGroupId(
            @Param("groupId") Long groupId,
            @Param("currentDate") LocalDateTime currentDate
    );

    @Query("SELECT rpm FROM RatePackageGroupMapping rpm " +
            "WHERE rpm.groupId = :groupId " +
            "AND rpm.zoneMappingId = :zoneMappingId")
    RatePackageGroupMapping findByGroupIdAndZoneMappingId(
            @Param("groupId") Long groupId,
            @Param("zoneMappingId") Long zoneMappingId
    );

    // Primary lookup method
    @Query("SELECT rpm FROM RatePackageGroupMapping rpm " +
            "WHERE rpm.groupId = :groupId " +
            "AND rpm.zoneMappingId = :zoneMappingId " +
            "AND rpm.packageType = :packageType")
    RatePackageGroupMapping findByGroupIdAndZoneMappingIdAndPackageType(
            @Param("groupId") Long groupId,
            @Param("zoneMappingId") Long zoneMappingId,
            @Param("packageType") PackageType packageType
    );

    // Get both NON_MONETARY and MONETARY mappings for a zone
    @Query("SELECT rpm FROM RatePackageGroupMapping rpm " +
            "WHERE rpm.groupId = :groupId " +
            "AND rpm.zoneMappingId = :zoneMappingId " +
            "ORDER BY rpm.packageType DESC") // NON_MONETARY first
    List<RatePackageGroupMapping> findAllByGroupIdAndZoneMappingId(
            @Param("groupId") Long groupId,
            @Param("zoneMappingId") Long zoneMappingId
    );

    @Query("SELECT count(r) FROM RatePackageGroupMapping r WHERE r.checkedItem = :checkedItem")
    Integer duplicateNasIp(@Param("checkedItem") String checkedItem);

    @Modifying
    @Query("DELETE FROM RatePackageGroupMapping r WHERE r.groupId = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT r FROM RatePackageGroupMapping r WHERE r.groupId = :groupId")
    List<RatePackageGroupMapping> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT count(r) FROM RatePackageGroupMapping r WHERE r.ratePackageId = :ratePackageId")
    Integer countByRatePackageId(@Param("ratePackageId") Long ratePackageId);

    List<RatePackageGroupMapping> findAllByRatePackageIdIn(Collection<Long> ratePackageIds);

    @Modifying
    @Query("UPDATE RatePackageGroupMapping m SET m.isDeleted = true WHERE m.groupId = :groupId")
    void softDeleteAllByGroupId(@Param("groupId") Long groupId);
}

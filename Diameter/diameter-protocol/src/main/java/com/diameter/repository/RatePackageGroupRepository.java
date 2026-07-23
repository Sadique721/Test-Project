package com.diameter.repository;


import com.diameter.model.RatePackageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatePackageGroupRepository extends JpaRepository<RatePackageGroup, Long> {
    RatePackageGroup findByGroupName(String groupName);

    void deleteByGroupName(String groupName);


    RatePackageGroup findByGroupId(Long groupId);

    void deleteByGroupId(Long groupId);
}

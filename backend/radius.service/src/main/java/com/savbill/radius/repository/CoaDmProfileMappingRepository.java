package com.savbill.radius.repository;

import com.savbill.radius.entity.CoaDmProfileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoaDmProfileMappingRepository extends JpaRepository<CoaDmProfileMapping , Long> {

    @Query
    List<CoaDmProfileMapping> findAllByClientGroupId(Long clientGroupId);
}

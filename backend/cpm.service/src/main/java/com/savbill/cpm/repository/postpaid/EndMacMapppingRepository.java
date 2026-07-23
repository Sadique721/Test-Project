package com.savbill.cpm.repository.postpaid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.postpaid.EndMacMappping;

import javax.transaction.Transactional;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface EndMacMapppingRepository extends JpaRepository<EndMacMappping, Integer>, QuerydslPredicateExecutor<EndMacMappping> {

    List<EndMacMappping> findByOwnerIdAndOwnerType(Integer ownerId, String ownerType);

    List<EndMacMappping> findByOwnerIdAndOwnerTypeAndIsDeletedIsFalse(Integer ownerId, String ownerType);
    @Transactional
    void deleteByOwnerIdAndOwnerType(Integer ownerId, String ownerType);
}

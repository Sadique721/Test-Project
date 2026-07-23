package com.savbill.partnermanagement.modules.Tax.repository;

import com.savbill.partnermanagement.modules.Tax.domain.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TaxRepository extends JpaRepository<Tax,Integer> {

    @Query("SELECT new map(t.id as id, t.name as name) FROM Tax t WHERE t.id IN :ids")
    List<Map<String, Object>> findIdNamePairs(@Param("ids") List<Integer> ids);

}

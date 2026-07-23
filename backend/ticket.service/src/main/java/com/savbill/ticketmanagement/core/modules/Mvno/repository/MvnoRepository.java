package com.savbill.ticketmanagement.core.modules.Mvno.repository;



import com.savbill.ticketmanagement.core.modules.Mvno.domain.Mvno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MvnoRepository extends JpaRepository<Mvno, Long> {

    Page<Mvno> findAll(Pageable pageable);

    @Query("select t.name from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);

    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void updatesMvnoidIsp(@Param("oldMvnoid") long oldMvnoid, @Param("newMvnoid") long newMvnoid);

}

package com.savbill.notification.Mvno.repository;



import com.savbill.notification.Mvno.domain.Mvno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MvnoRepository extends JpaRepository<Mvno, Long> {

    Page<Mvno> findAll(Pageable pageable);

    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void UpdateMvnoidISP(@Param("oldMvnoid") Integer oldMvnoid, @Param("newMvnoid") Integer newMvnoid);

}

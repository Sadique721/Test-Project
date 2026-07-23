package com.savbill.radius.mvno.Repository;

import com.savbill.radius.mvno.Entity.Mvno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MvnoRepository extends JpaRepository<Mvno,Long> {

    @Query("select t.name from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);

}

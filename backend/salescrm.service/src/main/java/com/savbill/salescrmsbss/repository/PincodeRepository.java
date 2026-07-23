package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Pincode;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, Long>{

}

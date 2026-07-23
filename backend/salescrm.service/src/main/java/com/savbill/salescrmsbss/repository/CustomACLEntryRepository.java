package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustomACLEntry;

@Repository
public interface CustomACLEntryRepository extends JpaRepository<CustomACLEntry, Integer>{

}

package com.diameter.repository;

import com.diameter.model.TestKafkaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestKafkaRepository extends JpaRepository<TestKafkaDto, Long> {
}

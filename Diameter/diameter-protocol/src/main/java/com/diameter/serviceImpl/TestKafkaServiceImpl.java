package com.diameter.serviceImpl;

import com.diameter.model.TestKafkaDto;
import com.diameter.repository.TestKafkaRepository;
import com.diameter.service.TestKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestKafkaServiceImpl implements TestKafkaService {

    @Autowired
    private TestKafkaRepository repository;

    @Override
    public void saveTestKafka(TestKafkaDto testKafkaDto) {

        TestKafkaDto entity = new TestKafkaDto();
        entity.setName(testKafkaDto.getName());

        repository.save(entity);

    }
}

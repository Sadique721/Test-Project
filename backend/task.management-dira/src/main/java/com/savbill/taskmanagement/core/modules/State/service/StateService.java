package com.savbill.taskmanagement.core.modules.State.service;

import com.savbill.taskmanagement.core.modules.State.domian.State;
import com.savbill.taskmanagement.core.modules.State.dto.StatePojo;
import com.savbill.taskmanagement.core.modules.State.repository.StateRepository;
import com.savbill.taskmanagement.core.service.AbstractService;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveStateSharedDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.StateSharedDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateStateSharedDataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StateService extends AbstractService<State, StatePojo, Integer> {

    @Autowired
    private StateRepository entityRepository;
    @Override
    protected JpaRepository<State, Integer> getRepository() {
        return entityRepository;
    }
    private static Log log = LogFactory.getLog(StateService.class);


    public void saveStateEntity(SaveStateSharedDataMessage stateSharedDataMessage){
        try {
            State state = new State();
            state.setId(stateSharedDataMessage.getId());
            state.setName(stateSharedDataMessage.getName());
            state.setStatus(stateSharedDataMessage.getStatus());
            state.setCountry(stateSharedDataMessage.getCountry());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setIsDeleted(stateSharedDataMessage.getIsDeleted());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            entityRepository.save(state);
        }catch (Exception e){
            log.info("Unable to Create State with name "+stateSharedDataMessage.getName()+" :"+e.getMessage());
        }


    }

    public void updateStateEntity(UpdateStateSharedDataMessage message){
        try {
            if(message.getId()!=null) {
                State state = entityRepository.findById(message.getId()).orElse(null);
                if(state!=null) {
                    state.setName(message.getName());
                    state.setStatus(message.getStatus());
                    state.setCountry(message.getCountry());
                    state.setMvnoId(message.getMvnoId());
                    state.setIsDeleted(message.getIsDeleted());
                    state.setMvnoId(message.getMvnoId());
                    entityRepository.save(state);
                }else {
                    log.info("No Data Found   ");
                }
            }
        }catch (Exception e){
            log.info("Unable to Create State with name "+message.getName()+" :"+e.getMessage());
        }


    }

    public void updateState(ConsumerRecord<String, Object> records) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = (String) records.value();
            UpdateStateSharedDataMessage stateSharedDataMessage = objectMapper.readValue(jsonString, UpdateStateSharedDataMessage.class);
            if(stateSharedDataMessage.getId() != null) {
                State state = entityRepository.findById(stateSharedDataMessage.getId().intValue()).orElse(null);
                if(state != null) {
                    state.setId(stateSharedDataMessage.getId());
                    state.setName(stateSharedDataMessage.getName());
                    state.setStatus(stateSharedDataMessage.getStatus());
                    state.setCountry(stateSharedDataMessage.getCountry());
                    state.setMvnoId(stateSharedDataMessage.getMvnoId());
                    state.setIsDeleted(stateSharedDataMessage.getIsDeleted());
                    state.setMvnoId(stateSharedDataMessage.getMvnoId());
                    entityRepository.save(state);
                }else {
                    log.info("No data found ");
                }
            }
        }catch (Exception e){
            log.info("Erroe message : " +e.getMessage());
        }
    }

    public void saveState(ConsumerRecord<String, Object> records) {
        try {
//            CountryRepository countryService = (CountryRepository) context.getBean(String.valueOf(CountryRepository.class));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = (String) records.value();
            StateSharedDataMessage stateSharedDataMessage = objectMapper.readValue(jsonString, StateSharedDataMessage.class);

            State state = new State();
            state.setId(stateSharedDataMessage.getId());
            state.setName(stateSharedDataMessage.getName());
            state.setStatus(stateSharedDataMessage.getStatus());
            state.setCountry(stateSharedDataMessage.getCountry());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setIsDeleted(stateSharedDataMessage.getIsDeleted());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            entityRepository.save(state);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("log message "+e.getMessage());

        }
    }}

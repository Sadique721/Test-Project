package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.State;
import com.savbill.salescrmsbss.rabbitMq.message.SaveStateSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateStateSharedDataMessage;
import com.savbill.salescrmsbss.repository.StateRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService {


    @Autowired
    private StateRepository entityRepository;

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

    public void updateStateEntity(UpdateStateSharedDataMessage message) {
        try {
            if (message.getId() != null) {
                State state = entityRepository.findById(message.getId()).orElse(null);
                if (state != null) {
                    state.setName(message.getName());
                    state.setStatus(message.getStatus());
                    state.setCountry(message.getCountry());
                    state.setMvnoId(message.getMvnoId());
                    state.setIsDeleted(message.getIsDeleted());
                    state.setMvnoId(message.getMvnoId());
                    entityRepository.save(state);
                } else {
//                    log.info("No Data Found   ");
                    State state1 = new State();
                    state1.setId(message.getId());
                    state1.setName(message.getName());
                    state1.setStatus(message.getStatus());
                    state1.setCountry(message.getCountry());
                    state1.setMvnoId(message.getMvnoId());
                    state1.setIsDeleted(message.getIsDeleted());
                    entityRepository.save(state1);
                }
            }
        } catch (Exception e) {
            log.info("Unable to Create State with name " + message.getName() + " :" + e.getMessage());
        }
    }
}

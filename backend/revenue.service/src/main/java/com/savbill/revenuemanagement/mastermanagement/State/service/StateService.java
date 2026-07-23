package com.savbill.revenuemanagement.mastermanagement.State.service;

import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.mastermanagement.State.domian.State;
import com.savbill.revenuemanagement.mastermanagement.State.dto.StatePojo;
import com.savbill.revenuemanagement.mastermanagement.State.repository.StateRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.StateSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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


    public void saveStateEntity(StateSharedDataMessage stateSharedDataMessage){
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

    public void updateStateEntity(StateSharedDataMessage message){
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
}

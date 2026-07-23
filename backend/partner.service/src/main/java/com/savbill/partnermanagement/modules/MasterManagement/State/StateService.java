package com.savbill.partnermanagement.modules.MasterManagement.State;

import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.rabbitmq.master.SaveStateSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateStateSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class StateService extends AbstractService<State, State, Integer> {

    @Autowired
    StateRepository stateRepository;

    private static final Logger logger = LoggerFactory.getLogger(StateService.class);


    @Override
    protected JpaRepository<State, Integer> getRepository() {
        return stateRepository;
    }

    public void saveStateEntity(SaveStateSharedDataMessage stateSharedDataMessage) throws Exception{
        logger.info("Creating state with name " + stateSharedDataMessage.getName());
        try {
            State state = new State();
            state.setId(stateSharedDataMessage.getId());
            state.setName(stateSharedDataMessage.getName());
            state.setStatus(stateSharedDataMessage.getStatus());
            state.setCountry(stateSharedDataMessage.getCountry());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setIsDeleted(stateSharedDataMessage.getIsDeleted());
            state.setMvnoId(stateSharedDataMessage.getMvnoId());
            state.setCreatedById(stateSharedDataMessage.getCreatedById());
            state.setLastModifiedById(stateSharedDataMessage.getLastModifiedById());
            stateRepository.save(state);
            logger.info("State created successfully with name " + stateSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create state with name " + stateSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void updateStateEntity(UpdateStateSharedDataMessage message) throws Exception{
        logger.info("Updating state with name " + message.getName());
        try {
            State state = stateRepository.findById(message.getId()).orElse(null);
            if (state != null) {
                state.setName(message.getName());
                state.setStatus(message.getStatus());
                state.setCountry(message.getCountry());
                state.setMvnoId(message.getMvnoId());
                state.setIsDeleted(message.getIsDeleted());
                state.setMvnoId(message.getMvnoId());
                state.setCreatedById(message.getCreatedById());
                state.setLastModifiedById(message.getLastModifiedById());
                stateRepository.save(state);
                logger.info("State updated successfully with name " + message.getName());
            } else {
                State state1 = new State();
                state1.setId(message.getId());
                state1.setName(message.getName());
                state1.setStatus(message.getStatus());
                state1.setCountry(message.getCountry());
                state1.setMvnoId(message.getMvnoId());
                state1.setIsDeleted(message.getIsDeleted());
                state1.setMvnoId(message.getMvnoId());
                state1.setCreatedById(message.getCreatedById());
                state1.setLastModifiedById(message.getLastModifiedById());
                stateRepository.save(state1);
                logger.info("State updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update state with name " + message.getName(), e.getMessage());
        }
    }
}

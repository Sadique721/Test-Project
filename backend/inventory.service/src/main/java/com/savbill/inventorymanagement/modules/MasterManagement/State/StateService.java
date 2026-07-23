package com.savbill.inventorymanagement.modules.MasterManagement.State;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveStateSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateStateSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService extends ExBaseAbstractService<StatePojo, State, Integer> {

    @Autowired
    StateRepository stateRepository;
    public StateService(StateRepository repository, StateMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[StateService]";
    }
    private static final Logger logger = Logger.getLogger(StateService.class);

    public void saveStateEntity(SaveStateSharedDataMessage stateSharedDataMessage) throws Exception{
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
            logger.error("Unable to create state with name " + stateSharedDataMessage.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateStateEntity(UpdateStateSharedDataMessage message) throws Exception{
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
            logger.error("Unable to update state with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}

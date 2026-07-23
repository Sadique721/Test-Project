package com.savbill.cpm.modules.DisconnectSubscriber.service;

import org.springframework.stereotype.Service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.DisconnectSubscriber.domain.UserDisconnectDtl;
import com.savbill.cpm.modules.DisconnectSubscriber.mapper.UserDisconnectDtlMapper;
import com.savbill.cpm.modules.DisconnectSubscriber.model.UserDisconnectDtlDTO;
import com.savbill.cpm.modules.DisconnectSubscriber.repository.UserDisconnectDtlRepository;

@Service
public class UserDisconnectDtlService  extends ExBaseAbstractService<UserDisconnectDtlDTO, UserDisconnectDtl, Long> {
    public UserDisconnectDtlService(UserDisconnectDtlRepository repository, UserDisconnectDtlMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[UserDiconnectDtlService]";
    }
}

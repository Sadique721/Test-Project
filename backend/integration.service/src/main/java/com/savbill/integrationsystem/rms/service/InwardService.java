package com.savbill.integrationsystem.rms.service;

import com.savbill.integrationsystem.rms.entity.Inward;
import com.savbill.integrationsystem.rms.model.InwardDto;
import com.savbill.integrationsystem.rms.model.InwardRmsDto;

public interface InwardService {
    Inward saveInwardFromRms(InwardRmsDto inwardRmsDto);
    Inward saveInwardFromInventory(InwardDto inwardDto);
}

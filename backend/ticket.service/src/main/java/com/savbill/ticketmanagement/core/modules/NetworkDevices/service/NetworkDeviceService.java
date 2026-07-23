package com.savbill.ticketmanagement.core.modules.NetworkDevices.service;



import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.dto.NetworkDeviceDTO;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


@Service
public class NetworkDeviceService extends ExBaseAbstractService<NetworkDeviceDTO, NetworkDevices, Long> {

    public static final String MODULE = " [NetworkDeviceService] ";

    public NetworkDeviceService(JpaRepository<NetworkDevices, Long> repository, IBaseMapper<NetworkDeviceDTO, NetworkDevices> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return MODULE;
    }
}

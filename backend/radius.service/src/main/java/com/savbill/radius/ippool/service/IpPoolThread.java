package com.savbill.radius.ippool.service;


import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.repository.IPPoolAllocationRepository;

import java.util.List;

public class IpPoolThread implements Runnable{

    private List<IPPoolAllocationDtls> ipPoolDtlsList;
    private IPPoolAllocationRepository ipPoolDtlsRepository;

    public IpPoolThread(List<IPPoolAllocationDtls> ipPoolDtlsList, IPPoolAllocationRepository ipPoolDtlsRepository)
    {
        this.ipPoolDtlsList =ipPoolDtlsList;
        this.ipPoolDtlsRepository=ipPoolDtlsRepository;
    }

    @Override
    public void run() {
        ipPoolDtlsRepository.saveAll(ipPoolDtlsList);
    }
}

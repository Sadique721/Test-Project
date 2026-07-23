package com.savbill.radius.services;

import com.savbill.radius.entity.ClientGroup;
import com.savbill.radius.helper.ClientGroupDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ClientGroupService {
    List<ClientGroup> findClientGroupByName(String groupName, Integer mvnoId);

    ClientGroup findClientGroupById(Long id, Integer mvnoId);

    List<ClientGroup> findAllClientGroups(Integer mvnoId);

    void deleteClientGroupById(Long id, Integer mvnoId);

    ClientGroup saveClientGroup(ClientGroupDto clientGroupDto, Integer mvnoId);

    ClientGroup updateClientGroup(ClientGroupDto clientGroupDto, Integer mvnoId);

    String updateClientGroupStatus(Long clientGroupId, String status, Integer mvnoId, HttpServletRequest request);

    ClientGroup validateGroupById(Long id, Integer mvnoId);

    List<ClientGroup> getRadiusGroups(Integer mvnoId);

    List<ClientGroup> getCltGroupByIdsAndConcurrencyAndSessionLogout(List<Long> cltGrps, boolean checkConcurrency, boolean checkSession);
}

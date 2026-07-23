package com.savbill.radius.services;

import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.helper.UsersDto;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface LiveUserService {
    void disconnectLiveUsers(List<Long> ids, Integer mvnoId, boolean isDisconnect);

    void disconnectLiveUsersByUsername(String username, Integer mvnoId, boolean isDisconnect);

    LiveUser findByUserNameLimit(String userName);

    Page<LiveUser> findLiveUsersUsingFilter(LiveUserSearchDTO paginationDTO, Integer mvnoId);

    Page<LiveUser> getAll(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request);

    void delete(Long id, Integer mvnoId);

    LiveUser findLiveUserById(Long id, Integer mvnoId);

    List<String> findUserStatusOnlineOrOffline(UsersDto usersDto);

    void dummyEntries(String userName, String password);

    List<LiveUser> findLiveUsersByMacAddress(String mac);

    List<LiveUser> findLiveUsersByLClass(String username);

    boolean existsWithUsername(String username);

    void delete(List<Long> id, Integer mvnoId);


    void disconnectLiveUsersOfStaleSession(List<LiveUser> sessionIdToPurgeSessions, Integer mvnoId);

    Long countByCustId(String custId);

    List<LiveUser> findLiveUserByCdrId(List<Long> cdrIds);

    List<LiveUser> findAllLiveUserByCustId(String custid);

    String getframedIpAddress (String custId);
}

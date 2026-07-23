package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.ProxyServer;
import com.savbill.radius.helper.ProxyServerDto;

import javax.servlet.http.HttpServletRequest;

public interface ProxyServerService {

	List<ProxyServer> getByName(String name, Integer mvnoId);
	ProxyServer getById(Long id, Integer mvnoId);
	List<ProxyServer> getAll(Integer mvnoId);
	void delete(Long id, Integer mvnoId);
	ProxyServer save(ProxyServerDto proxyServerDto, Integer mvnoId);
	ProxyServer update(Long id, ProxyServerDto proxyServerDto, Integer mvnoId, HttpServletRequest request);
	void updateStatus(Long id, String status, Integer mvnoId,HttpServletRequest request);
}

package com.diameter.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.PeerConfiguration;
import com.diameter.model.PeerConfiguration.Status;
import com.diameter.model.PeerConfiguration.VerificationMode;

@Repository
public class PeerConfigurationRepository {

    private final JdbcTemplate jdbcTemplate;

    public PeerConfigurationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PeerConfiguration save(PeerConfiguration dto) throws ValidationException {
    	
    	checkPeerExistsWithName(dto);
    	
        int result = jdbcTemplate.update(
            DiameterSQLConstants.INSERT_PEER_CONFIGURATION,
            dto.getNodeName(),
            dto.getRealm(),
            dto.getFqdn(),
            dto.getSctpListenPort(),
            dto.getTcpListenPort(),
            dto.getDtlsSctpListenPort(),
            dto.getTlsTcpListenPort(),
            dto.getRadiusUdpServerPorts(),
            dto.isEnableRadiusUdpClientPorts(),
            dto.getRadiusClientUdpPortRangeStart(),
            dto.getRadiusClientUdpPortRangeEnd(),
            dto.getVerificationMode().getDbValue(),
            dto.getCertificateType(),
            dto.getCertificateName(),
            convertListToCsv(dto.getIpAddresses()),
            dto.getRemoteIpAddress(),
            dto.getRemotePort(),
            dto.getStatus().getDbValue(),
            dto.getWatchdogInterval()
        );
        
        if(result > 0) {
        	PeerConfiguration newDto = getByName(dto.getNodeName());
        	if (newDto != null) {
    			dto.setId(newDto.getId());
    		} else {
    			throw new RuntimeException("Failed to retrieve PeerConfiguration by name: " + dto.getNodeName());
    		}
		} else {
			throw new RuntimeException("Failed to save PeerConfiguration: " + dto.getNodeName());
        }
        return dto;
    }

	private void checkPeerExistsWithName(PeerConfiguration dto) throws ValidationException {
		Integer count = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_PEER_EXISTS_BY_NAME, Integer.class,
    			dto.getNodeName());

        if (count != null && count > 0) {
            throw new ValidationException("Peer already exists with given node name");
        }
	}

    @SuppressWarnings("deprecation")
	public Optional<PeerConfiguration> findById(long id) throws EmptyResultDataAccessException {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            DiameterSQLConstants.SELECT_PEER_CONFIG_BY_ID,
            new Object[]{id},
            new PeerConfigurationRowMapper()
        ));
    }

    public List<PeerConfiguration> findAll() {
        return jdbcTemplate.query(
            DiameterSQLConstants.SELECT_ALL_PEER_CONFIGS,
            new PeerConfigurationRowMapper()
        );
    }

    public PeerConfiguration update(long id, PeerConfiguration dto) throws ValidationException {

    	Integer count = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_PEER_EXISTS_BY_NAME_EXCEPT_ID, Integer.class,
				dto.getNodeName(), id);

		if (count != null && count > 0) {
			throw new ValidationException("Peer already exists with given node name");
		}
    	
        jdbcTemplate.update(
            DiameterSQLConstants.UPDATE_PEER_CONFIGURATION,
            dto.getNodeName(),
            dto.getRealm(),
            dto.getFqdn(),
            dto.getSctpListenPort(),
            dto.getTcpListenPort(),
            dto.getDtlsSctpListenPort(),
            dto.getTlsTcpListenPort(),
            dto.getRadiusUdpServerPorts(),
            dto.isEnableRadiusUdpClientPorts(),
            dto.getRadiusClientUdpPortRangeStart(),
            dto.getRadiusClientUdpPortRangeEnd(),
            dto.getVerificationMode().getDbValue(),
            dto.getCertificateType(),
            dto.getCertificateName(),
            convertListToCsv(dto.getIpAddresses()),
            dto.getRemoteIpAddress(),
            dto.getRemotePort(),
            dto.getStatus().getDbValue(),
            dto.getWatchdogInterval(),
            id
        );
        dto.setId(id);
        return dto;
    }

    public void delete(long id) {
        jdbcTemplate.update(DiameterSQLConstants.DELETE_PEER_CONFIGURATION, id);
    }

    @SuppressWarnings("deprecation")
	public boolean existsById(long id) {
        Integer count = jdbcTemplate.queryForObject(
            DiameterSQLConstants.CHECK_PEER_CONFIG_EXISTS,
            new Object[]{id},
            Integer.class
        );
        return count != null && count > 0;
    }

    private String convertListToCsv(List<?> list) {
        return list == null ? null : list.stream()
            .map(Object::toString)
            .collect(Collectors.joining(","));
    }

    private List<String> convertCsvToList(String csv) {
        return csv == null || csv.isEmpty()
            ? List.of()
            : Arrays.asList(csv.split(","));
    }

    @SuppressWarnings("deprecation")
	public PeerConfiguration getById(Long id) {
    	return jdbcTemplate.queryForObject(
				DiameterSQLConstants.SELECT_PEER_CONFIG_BY_ID,
				new Object[]{id},
				new PeerConfigurationRowMapper()
			);	
	}
    
    @SuppressWarnings("deprecation")
	public PeerConfiguration getByName(String name) {
			return jdbcTemplate.queryForObject(
				DiameterSQLConstants.SELECT_PEER_CONFIG_BY_NAME,
				new Object[]{name},
				new PeerConfigurationRowMapper()
			);
	}
    
    @SuppressWarnings("deprecation")
	public List<PeerConfiguration> getPeerConfigByStatus(Status status) {
		return jdbcTemplate.query(
			DiameterSQLConstants.SELECT_PEER_CONFIG_BY_STATUS,
			new Object[]{status.getDbValue()},
			new PeerConfigurationRowMapper()
		);
	}

    public List<PeerConfiguration> getByRemoteIp(String remoteIp) {
        return jdbcTemplate.query(
                DiameterSQLConstants.SELECT_PEER_CONFIG_BY_REMOTE_IP,
                new Object[]{ remoteIp },
                new PeerConfigurationRowMapper()
        );
    }

    public List<PeerConfiguration> getByRealm(String realm) {
        return jdbcTemplate.query(
                DiameterSQLConstants.SELECT_PEER_CONFIG_BY_REALM,
                new Object[]{ realm },
                new PeerConfigurationRowMapper()
        );
    }

    class PeerConfigurationRowMapper implements RowMapper<PeerConfiguration> {
        @Override
        public PeerConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
            PeerConfiguration dto = new PeerConfiguration();
            dto.setId(rs.getLong("id"));
            dto.setNodeName(rs.getString("node_name"));
            dto.setRealm(rs.getString("realm"));
            dto.setFqdn(rs.getString("fqdn"));
            dto.setSctpListenPort(rs.getInt("sctp_listen_port"));
            dto.setTcpListenPort(rs.getInt("tcp_listen_port"));
            dto.setDtlsSctpListenPort(rs.getInt("dtls_sctp_listen_port"));
            dto.setTlsTcpListenPort(rs.getInt("tls_tcp_listen_port"));
            dto.setRadiusUdpServerPorts(rs.getInt("radius_udp_server_ports"));
            dto.setEnableRadiusUdpClientPorts(rs.getBoolean("enable_radius_udp_client_ports"));
            dto.setRadiusClientUdpPortRangeStart(rs.getInt("radius_client_udp_port_range_start"));
            dto.setRadiusClientUdpPortRangeEnd(rs.getInt("radius_client_udp_port_range_end"));
            dto.setVerificationMode(VerificationMode.fromDbValue(rs.getString("verification_mode")));
            dto.setCertificateType(rs.getString("certificate_type"));
            dto.setCertificateName(rs.getString("certificate_name"));
            dto.setIpAddresses(convertCsvToList(rs.getString("ip_addresses")));
            dto.setRemoteIpAddress(rs.getString("remote_ip_address"));
            dto.setRemotePort(rs.getInt("remote_port"));
            dto.setStatus(Status.fromDbValue(rs.getString("status")));
            dto.setWatchdogInterval(rs.getInt("watchdog_interval"));
            return dto;
        }
    }

}

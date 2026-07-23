package com.diameter.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import com.diameter.model.QOSPolicyGatewayMapping;
import com.diameter.model.QOSPolicyGatewayMappingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.QOSPolicy;

@Repository
public class QOSPolicyRepository {
	
	private static final String CLASSNAME = "QOSPolicyRepository";
	private static final Logger logger = LoggerFactory.getLogger(QOSPolicyRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
	public QOSPolicyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

	public QOSPolicy save(QOSPolicy policy) throws ValidationException {

		String methodName = "save";
		if (logger.isInfoEnabled()) {
			logger.info(CLASSNAME, methodName, " method started");
		}

		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(DiameterSQLConstants.CHECK_POLICY_EXISTS, policy.getName());

		if (rowSet.next()) {
			throw new ValidationException("Policy already exists with name: " + policy.getName());
		}

		// ✅ Holder for auto-generated ID
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(DiameterSQLConstants.INSERT_POLICY, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, policy.getName());
			ps.setString(2, policy.getDescription());
			ps.setString(3, policy.getThPolicyName());
			ps.setString(4, policy.getBaseParam1());
			ps.setString(5, policy.getBaseParam2());
			ps.setString(6, policy.getBaseParam3());
			ps.setString(7, policy.getThParam1());
			ps.setString(8, policy.getThParam2());
			ps.setString(9, policy.getThParam3());
			ps.setShort(10, policy.getIsDeleted());
			ps.setBigDecimal(11, policy.getCreatedByStaffid());
			ps.setTimestamp(12, policy.getCreateDate());
			ps.setBigDecimal(13, policy.getLastModifiedByStaffId());
			ps.setTimestamp(14, policy.getLastModifiedDate());
			ps.setString(15, policy.getBasePolicyName());
			ps.setString(16, policy.getCreateByName());
			ps.setString(17, policy.getUpdateByName());
			ps.setLong(18, policy.getMVNOID().longValue());
			ps.setString(19, policy.getType());
			ps.setString(20, policy.getQosSpeed());
			ps.setString(21, policy.getUpstreamProfileUID());
			ps.setString(22, policy.getDownstreamProfileUID());
			return ps;
		}, keyHolder);
       // Convert generated BIGINT → BigInteger
		policy.setId(BigInteger.valueOf(keyHolder.getKey().longValue()));
		return policy;
	}


    public List<QOSPolicy> findAll() {
    	String methodName = "findAll";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
        return jdbcTemplate.query(DiameterSQLConstants.GET_ALL_POLICIES, this::mapRow);
    }

    public QOSPolicy findById(String id) {
    	String methodName = "findById";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
        return jdbcTemplate.queryForObject(DiameterSQLConstants.GET_POLICY_BY_ID, this::mapRow, id);
    }

    public QOSPolicy findByName(String name) {
    	String methodName = "findByName";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
        return jdbcTemplate.queryForObject(DiameterSQLConstants.GET_POLICY_BY_NAME, this::mapRow, name);
    }


	public List<QOSPolicyGatewayMapping> getGatewayMappingByQosPolicyId(String qosPolicyId) {

		String methodName = "getGatewayMappingByQosPolicyId";

		if (logger.isInfoEnabled()) {
			logger.info(CLASSNAME, methodName, " method started");
		}

		return jdbcTemplate.query(
				DiameterSQLConstants.GET_GATEWAY_MAPPING_BY_QOS_POLICY_ID,
				new Object[]{ new BigInteger(qosPolicyId) },
				this::mapGatewayMappingRow
		);
	}


	private QOSPolicyGatewayMapping mapGatewayMappingRow(
			ResultSet rs, int rowNum) throws SQLException {

		String methodName = "mapGatewayMappingRow";

		if (logger.isInfoEnabled()) {
			logger.info(CLASSNAME, methodName, " method started");
		}

		QOSPolicyGatewayMapping m = new QOSPolicyGatewayMapping();

		m.setId(rs.getObject("id", BigInteger.class));
		m.setName(rs.getString("name"));

		m.setDownloadSpeed(rs.getString("download_speed"));
		m.setUploadSpeed(rs.getString("upload_speed"));

		m.setBaseDownloadSpeed(rs.getString("base_download_speed"));
		m.setBaseUploadSpeed(rs.getString("base_upload_speed"));

		m.setThrottleDownloadSpeed(rs.getString("throttle_download_speed"));
		m.setThrottleUploadSpeed(rs.getString("throttle_upload_speed"));

		m.setQosPolicyId(rs.getObject("qos_policy_id", BigInteger.class));

		return m;
	}


	private QOSPolicy mapRow(ResultSet rs, int rowNum) throws SQLException {
    	String methodName = "mapRow";
    	if (logger.isInfoEnabled()) {
    		logger.info(CLASSNAME, methodName, " method started");
        }
    	QOSPolicy p = new QOSPolicy();
    	p.setId(BigInteger.valueOf(rs.getLong("id")));
		p.setName(rs.getString("name"));
		p.setDescription(rs.getString("description"));
		p.setThPolicyName(rs.getString("thpolicyname"));
		p.setBaseParam1(rs.getString("baseparam1"));
		p.setBaseParam2(rs.getString("baseparam2"));
		p.setBaseParam3(rs.getString("baseparam3"));
		p.setThParam1(rs.getString("thparam1"));
		p.setThParam2(rs.getString("thparam2"));
		p.setThParam3(rs.getString("thparam3"));
		p.setIsDeleted(rs.getShort("is_deleted"));
		p.setCreatedByStaffid(rs.getBigDecimal("createdbystaffid"));
		p.setCreateDate(rs.getTimestamp("createdate"));
		p.setLastModifiedByStaffId(rs.getBigDecimal("lastmodifiedbystaffid"));
		p.setLastModifiedDate(rs.getTimestamp("lastmodifieddate"));
		p.setBasePolicyName(rs.getString("basepolicyname"));
		p.setCreateByName(rs.getString("createbyname"));
		p.setUpdateByName(rs.getString("updatebyname"));
		p.setMVNOID(BigInteger.valueOf(rs.getLong("MVNOID")));
		p.setType(rs.getString("type"));
		p.setQosSpeed(rs.getString("qosspeed"));
		p.setUpstreamProfileUID(rs.getString("upstreamprofileuid"));
		p.setDownstreamProfileUID(rs.getString("downstreamprofileuid"));
		 
        return p;
    }

	public int updatePolicy(QOSPolicy policy) {

		return jdbcTemplate.update(
				DiameterSQLConstants.UPDATE_POLICY,
				policy.getName(),
				policy.getDescription(),
				policy.getThPolicyName(),
				policy.getBaseParam1(),
				policy.getBaseParam2(),
				policy.getBaseParam3(),
				policy.getThParam1(),
				policy.getThParam2(),
				policy.getThParam3(),
				policy.getIsDeleted(),
				policy.getLastModifiedByStaffId(),
				policy.getLastModifiedDate(),
				policy.getBasePolicyName(),
				policy.getUpdateByName(),
				policy.getMVNOID(),
				policy.getType(),
				policy.getQosSpeed(),
				policy.getUpstreamProfileUID(),
				policy.getDownstreamProfileUID(),
				policy.getId()
		);
	}

	public int deleteGatewayMappings(BigInteger qosPolicyId) {

		return jdbcTemplate.update(
				DiameterSQLConstants.DELETE_GATEWAY_MAPPING_BY_POLICY_ID,
				qosPolicyId
		);
	}

	public int insertGatewayMapping(
			QOSPolicyGatewayMappingEntity child,
			BigInteger qosPolicyId) {

		return jdbcTemplate.update(
				DiameterSQLConstants.INSERT_GATEWAY_MAPPING,
				child.getId(),
				child.getGatewayName(),
				child.getDownloadSpeed(),
				child.getUploadSpeed(),
				child.getBaseDownloadSpeed(),
				child.getBaseUploadSpeed(),
				child.getThrottleDownloadSpeed(),
				child.getThrottleUploadSpeed(),
				qosPolicyId
		);
	}

	public Optional<QOSPolicy> findOptionalById(String id) {
		try {
			return Optional.ofNullable(
					jdbcTemplate.queryForObject(
							DiameterSQLConstants.GET_POLICY_BY_ID,
							this::mapRow,
							id
					)
			);
		} catch (EmptyResultDataAccessException ex) {
			return Optional.empty();
		}
	}

}

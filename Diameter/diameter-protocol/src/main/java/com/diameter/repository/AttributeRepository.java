package com.diameter.repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.diameter.constant.DiameterSQLConstants;
import com.diameter.model.Attribute;
import com.diameter.model.Vendor;

@Repository
public class AttributeRepository {

    private static final Logger logger = LoggerFactory.getLogger(AttributeRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Attribute> rowMapper = (rs, rowNum) -> {
        Attribute attr = new Attribute();
        attr.setId(rs.getString("id"));
        attr.setAttributeId(rs.getInt("attribute_id"));
        attr.setName(rs.getString("name"));
        attr.setMandatory(rs.getString("mandatory"));
        attr.setProtectedFlag(rs.getString("protected"));
        attr.setEncryption(rs.getString("encryption"));
        attr.setType(rs.getString("type"));
        attr.setStatus(rs.getString("status"));
        attr.setDictionaryType(rs.getString("dictionary_type"));
        attr.setMinimum(rs.getObject("minimum", Integer.class));
        attr.setMaximum(rs.getObject("maximum", Integer.class));
        attr.setAttributeVendorId(rs.getObject("attribute_vendor_id", Integer.class));
        attr.setParentAttributeId(rs.getString("parent_attribute_id"));
        attr.setVendorId(rs.getString("vendor_id"));

        Timestamp created = rs.getTimestamp("created_date");
        attr.setCreatedDate(created != null ? created.toLocalDateTime() : null);
        attr.setCreatedBy(rs.getString("created_by"));

        Timestamp modified = rs.getTimestamp("modified_date");
        attr.setModifiedDate(modified != null ? modified.toLocalDateTime() : null);
        attr.setModifiedBy(rs.getString("modified_by"));

        attr.setRegex(rs.getString("regex"));
        return attr;
    };

    public Attribute saveAttribute(Attribute attr) {

        if (attr.getCreatedDate() == null) {
            attr.setCreatedDate(LocalDateTime.now());
        }

        attr.setModifiedDate(null);

        int result = jdbcTemplate.update(DiameterSQLConstants.INSERT_ATTRIBUTE,
                attr.getId(), attr.getAttributeId(), attr.getName(), attr.getMandatory(), attr.getProtectedFlag(),
                attr.getEncryption(), attr.getType(), attr.getStatus(), attr.getDictionaryType(),
                attr.getMinimum(), attr.getMaximum(), attr.getAttributeVendorId(), attr.getParentAttributeId(),
                attr.getVendorId(), Timestamp.valueOf(attr.getCreatedDate()), attr.getCreatedBy(), null,
                attr.getModifiedBy(), attr.getRegex()
        );

    	if(result > 0) {
    		Attribute newDto = findByName(attr.getName());
        	if (newDto != null) {
        		attr.setId(newDto.getId());
    		} else {
    			throw new RuntimeException("Failed to retrieve PeerConfiguration by name: " + attr.getName());
    		}
		} else {
			throw new RuntimeException("Failed to save PeerConfiguration: " + attr.getName());
        }
		return attr;
    }

    public void saveAttributeMapping(String mappingId, String attributeId, String vendorId) {
        jdbcTemplate.update(DiameterSQLConstants.INSERT_ATTRIBUTE_MAPPING, mappingId, attributeId, vendorId);
    }

    public boolean mappingExists(String attributeId, String vendorId) {
        Integer count = jdbcTemplate.queryForObject(DiameterSQLConstants.CHECK_IF_ATTRIBUTE_MAPPING_EXISTS, Integer.class, attributeId, vendorId);
        return count != null && count > 0;
    }

    public List<Attribute> findAll() {
        return jdbcTemplate.query(DiameterSQLConstants.SELECT_ALL_ATTRIBUTES, rowMapper);
    }

    public Attribute findById(String id) {
        try {
            return jdbcTemplate.queryForObject(DiameterSQLConstants.SELECT_ATTRIBUTE_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Attribute findByName(String name) {
        try {
            return jdbcTemplate.queryForObject(DiameterSQLConstants.SELECT_ATTRIBUTE_BY_NAME, rowMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Attribute> findByAttributeId(Integer attributeId) {
        try {
            return jdbcTemplate.query(
                    DiameterSQLConstants.SELECT_ATTRIBUTE_BY_ATTRIBUTE_ID,
                    rowMapper,
                    attributeId
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public void deleteById(String id) {
    	deleteAttributeMappings(id);
        jdbcTemplate.update(DiameterSQLConstants.DELETE_ATTRIBUTE, id);
    }

    public Attribute updateAttribute(Attribute attr) {
        jdbcTemplate.update(DiameterSQLConstants.UPDATE_ATTRIBUTE,
                attr.getAttributeId(), attr.getName(), attr.getMandatory(), attr.getProtectedFlag(),
                attr.getEncryption(), attr.getType(), attr.getStatus(), attr.getDictionaryType(),
                attr.getMinimum(), attr.getMaximum(), attr.getAttributeVendorId(), attr.getParentAttributeId(),
                attr.getVendorId(), Timestamp.valueOf(attr.getModifiedDate()), attr.getModifiedBy(),
                attr.getRegex(), attr.getId()
        );
		return attr;
    }
    
    public void deleteAttributeMappings(String attributeId) {
        jdbcTemplate.update(DiameterSQLConstants.DELETE_ATTRIBUTE_MAPPINGS, attributeId);
    }

	@SuppressWarnings("deprecation")
	public List<String> getValidVendorIds(List<String> vendorList) {
		if (vendorList.isEmpty()) return List.of();

	    String placeholders = String.join(",", Collections.nCopies(vendorList.size(), "?"));
	    String sql = String.format(DiameterSQLConstants.SELECT_VALID_VENDOR_IDS, placeholders);

	    return jdbcTemplate.query(sql, vendorList.toArray(), (rs, rowNum) -> rs.getString("id"));
	}
	
	@SuppressWarnings("deprecation")
	public List<Vendor> findAllActiveAttributes(String status) {
        return jdbcTemplate.query(DiameterSQLConstants.SELECT_ALL_ACTIVE_VENDORS_WITH_ATTRIBUTES, rs -> {
            Map<String, Vendor> vendorMap = new LinkedHashMap<>();

            while (rs.next()) {
                String vId = rs.getString("id");

                Vendor vendor = vendorMap.computeIfAbsent(vId, id -> {
                Vendor vendorInfo = new Vendor();
                try {
                	vendorInfo.setId(vId);
                	vendorInfo.setVendor_id(rs.getInt("vendor_id"));
                	vendorInfo.setName(rs.getString("vendor_name"));
                	vendorInfo.setStatus(rs.getString("vendor_status"));
                	vendorInfo.setDescription(rs.getString("vendor_description"));
				} catch (SQLException e) {
					logger.error("Failed to map vendor row for vendor id {}", vId, e);
				}
                  return vendorInfo;
                });

                Attribute attr = new Attribute();
                attr.setAttributeId(rs.getInt("attribute_id"));
                attr.setDictionaryType(rs.getString("dictionary_type"));
                attr.setMinimum(rs.getObject("minimum", Integer.class));
                attr.setMaximum(rs.getObject("maximum", Integer.class));
                attr.setAttributeVendorId(rs.getObject("attribute_vendor_id", Integer.class));
                attr.setParentAttributeId(rs.getString("parent_attribute_id"));
                attr.setVendorId(rs.getString("attribute_vendor_id_fk"));

                // ----------------------------------------------------------
                // Handle NULL timestamps safely  (created_date & modified_date)
                // ----------------------------------------------------------
                // rs.getTimestamp(...) may return NULL when record is newly
                // created or when modified_date is not yet populated.
                // Calling toLocalDateTime() on NULL will throw NPE.
                // Hence, we first store Timestamp in a variable and
                // convert only if it is not null.
                Timestamp createdTs = rs.getTimestamp("created_date");
                attr.setCreatedDate(
                        createdTs != null ? createdTs.toLocalDateTime() : null
                );
                attr.setCreatedBy(rs.getString("created_by"));
                Timestamp modifiedTs = rs.getTimestamp("modified_date");
                attr.setModifiedDate(
                        modifiedTs != null ? modifiedTs.toLocalDateTime() : null
                );

                attr.setModifiedBy(rs.getString("modified_by"));
                attr.setRegex(rs.getString("regex"));
                attr.setStatus(rs.getString("attribute_status"));
                attr.setEncryption(rs.getString("encryption"));
                attr.setMandatory(rs.getString("mandatory"));
                attr.setProtectedFlag(rs.getString("protected"));
                attr.setType(rs.getString("type"));
                attr.setName(rs.getString("name"));
                vendor.getAttributes().add(attr);
            }

            return new ArrayList<Vendor>(vendorMap.values());
        });
    }

}
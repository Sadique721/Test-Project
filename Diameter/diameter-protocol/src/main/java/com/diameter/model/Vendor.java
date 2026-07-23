package com.diameter.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblm_vendor")
public class Vendor {
	@Id
    private String id;
    private Integer vendor_id;
    private String name;
    private String description;
    private String status;

	@OneToMany(mappedBy = "vendorId",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
    private List<Attribute> attributes = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(Integer vendor_id) {
		this.vendor_id = vendor_id;
	}
	public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    
    public void setDescription(String description) { this.description = description; }
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

}


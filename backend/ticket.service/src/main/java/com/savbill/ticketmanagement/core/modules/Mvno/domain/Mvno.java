package com.savbill.ticketmanagement.core.modules.Mvno.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "tblmmvno")
@EntityListeners(AuditableListener.class)
public class Mvno extends Auditable implements IBaseData<Long> {
	
	@Id
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Long id;
	
	@Column(name = "NAME", nullable = false, length = 64)
    private String name;

	@Column(name = "USERNAME", nullable = false, length = 200)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 200)
	private String password;
	
	@Column(name = "SUFFIX", nullable = false, length = 16)
    private String suffix;

	@Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;
	
	@Column(name = "EMAIL", nullable = false, length = 255)
    private String email;
	
	@Column(name = "PHONE", nullable = false, length = 255)
    private String phone;
    
	@Column(name = "STATUS", nullable = false, length = 40)
	private String status;
	
	@Column(name = "LOGOFILE", nullable = false, length = 255)
	private String logfile;
	
	@Column(name = "MVNOHEADER", nullable = false, length = 255)
	private String mvnoHeader;
	
	@Column(name = "MVNOFOOTER", nullable = false, length = 255)
	private String mvnoFooter;
	
	@Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

	@JsonIgnore
	@Override
	public Long getPrimaryKey() {
        return id;
	}

	@Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }

}

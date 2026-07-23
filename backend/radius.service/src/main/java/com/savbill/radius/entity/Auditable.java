package com.savbill.radius.entity;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Auditable<U> {

    @CreationTimestamp
    @Column(name = "createdate", nullable=false, updatable = false)
	@JsonProperty("createDate")
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "lastmodificationdate", nullable=true, updatable = true)
	@JsonProperty("lastModificationDate")
    private LocalDateTime lastModifiedOn;

    @CreatedBy
    @Column(name="createdby", nullable = false, length = 40,updatable = false)
	private String createdBy;
    
    @LastModifiedBy
    @Column(name="lastmodifiedby", nullable = false, length = 40)
	private String lastModifiedBy;

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}
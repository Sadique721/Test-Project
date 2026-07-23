package com.savbill.notification.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

    @CreationTimestamp
    @Column(name = "createdate", nullable=false, updatable = false)
    @JsonProperty("createDate")
    private LocalDateTime createDate;
    @DiffIgnore
    @UpdateTimestamp
    @Column(name = "lastmodifieddate", nullable=true, updatable = true)
    @JsonProperty("lastModifiedDate")
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name="createdby", nullable = false, length = 40,updatable = false)
    private String createdBy;
    @DiffIgnore
    @LastModifiedBy
    @Column(name="lastmodifiedby", nullable = false, length = 40)
    private String lastModifiedBy;

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

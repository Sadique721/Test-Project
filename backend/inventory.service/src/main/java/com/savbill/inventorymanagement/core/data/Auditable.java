package com.savbill.inventorymanagement.core.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "createdate", nullable = false, updatable = false)
    @DiffIgnore
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "lastmodifieddate")
    @DiffIgnore
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    @DiffIgnore
    private String lastModifiedByName;

    @Column(name = "createdbystaffid", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer createdById;

    @Column(name = "lastmodifiedbystaffid", nullable = false, length = 40)
    @DiffIgnore
    private Integer lastModifiedById;

//    @ApiModelProperty(hidden = true)
//    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
//    private Integer mvnoId;
//
//    public Integer LoggedInUserMvnoId() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        if(request.getAttribute(Constants.MVNO_ID_FROM_APIGW) != null)
//            this.mvnoId = Integer.parseInt(request.getAttribute(Constants.MVNO_ID_FROM_APIGW).toString());
//        return this.mvnoId;
//    }

    public Auditable(String createdByName, int createdById, LocalDateTime createdate) {
        this.createdByName = createdByName;
        this.createdById = createdById;
        this.createdate = createdate;


    }
}

package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SaveCountrySharedDataMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMCOUNTRY")
public class Country {

	@Id
    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column( name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    public Country(SaveCountrySharedDataMessage countryMessage) {
    	this.id = countryMessage.getId();
    	this.name = countryMessage.getName();
    	this.status = countryMessage.getStatus();
    	this.isDelete = countryMessage.getIsDelete();
        this.mvnoId = countryMessage.getMvnoId();
    }
}

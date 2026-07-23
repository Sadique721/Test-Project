package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "TBLMSERVERCONF")
@NoArgsConstructor
public class ServerConfig 
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serverconf")
    private Long serverConfigId;
	
	@Column(name = "attributename")
	private String attributeName;
	
	@Column(name = "attributevalue")
	private String attributeValue;
    
    @ApiModelProperty(hidden = true)
   	@Column (name="createdate")
   	private Timestamp createdOn;

   	@ApiModelProperty(hidden = true)
   	@Column (name="lastmodificationdate")
   	private Timestamp lastModifiedOn;
}

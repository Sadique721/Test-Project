package com.savbill.taskmanagement.core.modules.EmailConfig.domain;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMEMAILCONFIG")
public class EmailConfigBSS
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated email config Id",required = true)
    @Column (name="emailconfigid", nullable = false)
	private Long emailConfigId;
	
	@ApiModelProperty(notes = "This is username",required = true)
    @Column (name="username", length = 100,nullable = false)
    private String userName;
	
	@ApiModelProperty(notes = "This is password",required = true)
    @Column (name="password", length = 100,nullable = false)
    private String password;
	
	@ApiModelProperty(notes = "This is smtp authentication value",required = true)
    @Column (name="smtpauth", nullable = false)
    private boolean smtpAuth;
	
	@ApiModelProperty(notes = "This is auth type",allowableValues = "StartTLS,SSL",  value = "This field accept value only : StartTLS or SSL",required = true)
    @Column (name="authtype", length = 100,nullable = false)
    private String authType;
	
	@ApiModelProperty(notes = "This is hostvalue",required = true)
    @Column (name="hostserver", length = 100,nullable = false)
    private String hostServer;
	
	@ApiModelProperty(notes = "This is port value",required = true)
    @Column (name="port", length = 100,nullable = false)
    private String port;
	
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@ApiModelProperty(notes = "this is bu id" , required = false)
	@Column(name = "BUID", length = 40, updatable = false)
	private Long buId;

}

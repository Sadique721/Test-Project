package com.savbill.taskmanagement.core.modules.Mail.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receiveemailconfiguration")
public class ReceiveEmailConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;

    @Column(name = "is_delete", length = 1)
    private Boolean isDelete;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "username", length = 100)
    private String userName;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "host", length = 100)
    private String host;

    @Column(name = "port", length = 100)
    private String port;

    @Column(name = "is_enable", length = 1)
    private Boolean isEnable;

    @Column (name="mvnoid")
    private Long mvnoId;

    @Column (name="buid")
    private Long buId;

    public String getImapUrl(){
        String url = "";
        url = "imaps://" + this.getUserName() + ":" + this.getPassword() + "@" + this.getHost() + ":" + this.getPort() + "/INBOX";
        return url;
    }
}

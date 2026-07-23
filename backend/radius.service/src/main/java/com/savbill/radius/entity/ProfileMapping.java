package com.savbill.radius.entity;

import javax.persistence.*;

@Entity
@Table(name = "TBLTTLSPROFILEMAPPING")
public class ProfileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "PROFILEID")
    private Long profileId;

    @Column(name = "PASSWORD", length = 250)
    private String password;

    @Column(name = "FILE_PATH", length = 250)
    private String filePath;

    @Column(name = "FILE_TYPE", length = 250)
    private String fileType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

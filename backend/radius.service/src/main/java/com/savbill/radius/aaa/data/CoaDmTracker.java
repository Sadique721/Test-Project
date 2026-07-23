package com.savbill.radius.aaa.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblmcoadmtracker")
public class CoaDmTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "custpackageid")
    private Integer custpackageid;
    @Column(name = "timebasepolicyid")
    private String timeBasePolicyId;
    @Column(name = "classstr")
    private String classStr;
    @Column(name = "cause")
    private String cause;
    @Column(name = "custid")
    private Integer custId;
    @Column(name = "stracctsessionid")
    private String strAcctSessionId;

    public CoaDmTracker(Integer custpackageid, String timeBasePolicyId, String classStr, String cause, Integer custId, String strAcctSessionId) {
        this.custpackageid = custpackageid;
        this.timeBasePolicyId = timeBasePolicyId;
        this.classStr = classStr;
        this.cause = cause;
        this.custId = custId;
        this.strAcctSessionId = strAcctSessionId;
    }

    @Override
    public String toString() {
        return "CoaDmTracker{" +
                "custpackageid=" + custpackageid +
                ", timeBasePolicyId='" + timeBasePolicyId + '\'' +
                ", classStr='" + classStr + '\'' +
                ", cause='" + cause + '\'' +
                ", custId=" + custId +
                ", strAcctSessionId='" + strAcctSessionId + '\'' +
                '}';
    }
}

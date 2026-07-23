package com.savbill.radius.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "tblmcoaresponse")
@Data
public class COAResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coaresponseid")
    private Integer coaResponseId;

    @Column(name = "nasipaddress")
    private String nasIpAddress;

    @Column(name = "coapacket")
    private String coaPacket;

    @Column(name = "coaresponse")
    private String coaResponse;

    @Column(name = "reason")
    private String reason;

    @Column(name = "createdate")
    private LocalDateTime createDate;

    @Column(name = "mvnoid")
    private Integer mvnoId;

    @Column(name = "coaresponsemessage")
    private Integer coaResponseMessage;

    // Constructors
    public COAResponse() {}

    public COAResponse(String nasIpAddress, String coaPacket, String coaResponse,
                       String reason, LocalDateTime createDate, Integer mvnoId) {
        this.nasIpAddress = nasIpAddress;
        this.coaPacket = coaPacket;
        this.coaResponse = coaResponse;
        this.reason = reason;
        this.createDate = createDate;
        this.mvnoId = mvnoId;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        COAResponse that = (COAResponse) o;
        return Objects.equals(coaResponseId, that.coaResponseId) &&
                Objects.equals(nasIpAddress, that.nasIpAddress) &&
                Objects.equals(coaPacket, that.coaPacket) &&
                Objects.equals(coaResponse, that.coaResponse) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(mvnoId, that.mvnoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coaResponseId, nasIpAddress, coaPacket,
                coaResponse, reason, createDate, mvnoId);
    }

    // toString
    @Override
    public String toString() {
        return "COAResponse{" +
                "coaResponseId=" + coaResponseId +
                ", nasIpAddress='" + nasIpAddress + '\'' +
                ", coaPacket='" + coaPacket + '\'' +
                ", coaResponse='" + coaResponse + '\'' +
                ", reason='" + reason + '\'' +
                ", createDate=" + createDate +
                ", mvnoId=" + mvnoId +
                '}';
    }
}
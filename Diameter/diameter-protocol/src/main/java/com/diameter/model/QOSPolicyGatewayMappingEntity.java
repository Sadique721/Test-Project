package com.diameter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltqospolicy_gateway_mapping")
public class QOSPolicyGatewayMappingEntity {

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String gatewayName;
    @Column(name = "download_speed")
    private String downloadSpeed;
    @Column(name = "upload_speed")
    private String uploadSpeed;
    @Column(name = "base_download_speed")
    private String baseDownloadSpeed;
    @Column(name = "base_upload_speed")
    private String baseUploadSpeed;
    @Column(name = "throttle_download_speed")
    private String throttleDownloadSpeed;
    @Column(name = "throttle_upload_speed")
    private String throttleUploadSpeed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qos_policy_id", nullable = false)
    private QOSPolicy qosPolicy;

    @Override
    public String toString() {
        return "QOSPolicyGatewayMapping{" +
                "id=" + id +
                ", gatewayName='" + gatewayName + '\'' +
                ", downloadSpeed='" + downloadSpeed + '\'' +
                ", uploadSpeed='" + uploadSpeed + '\'' +
                ", baseDownloadSpeed='" + baseDownloadSpeed + '\'' +
                ", baseUploadSpeed='" + baseUploadSpeed + '\'' +
                ", throttleDownloadSpeed='" + throttleDownloadSpeed + '\'' +
                ", throttleUploadSpeed='" + throttleUploadSpeed + '\'' +
                '}';
    }
}

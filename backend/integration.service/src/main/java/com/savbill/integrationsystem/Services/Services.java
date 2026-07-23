package com.savbill.integrationsystem.Services;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tblmservices")
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "servicename", nullable = false, length = 40)
    private String name;

    @Column(name = "icname", nullable = false, length = 40)
    private String icname;

    @Column(name = "iccode", nullable = false, length = 40)
    private String iccode;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_qosv", nullable = false, columnDefinition = "Boolean default true")
    private Boolean isQoSV;

    @Column(name = "expiry",nullable = false,length = 100)
    private String expiry;

    @Column(name = "is_dtv")
    private Boolean is_dtv;

    @Column(name = "investmentcode_id")
    private Long investmentid;
    @Column(name = "feasibility")
    private Boolean feasibility;
    @Column(name = "poc")
    private Boolean poc;
    @Column(name = "installation")
    private Boolean installation;
    @Column(name = "provisioning")
    private Boolean provisioning;
    @Column(name = "is_price_editable")
    private Boolean isPriceEditable;
    @Column(name = "feasibility_team_id")
    private Long feasibilityTeamId;
    @Column(name = "poc_team_id")
    private Long pocTeamId;
    @Column(name = "installation_team_id")
    private Long installationTeamId;
    @Column(name = "provisioning_team_id")
    private Long provisioningTeamId;

    public Services (Map message){
        if (message.get("lanServiceData(id") != null)
            this.id = Integer.parseInt(message.get("lanServiceData(id").toString());
//        if(message.get("customer") != null)
//            this.customer = new Customers((Map)message.get("customer"));
        if (message.get("name") != null)
            this.name = message.get("name").toString();
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
      if (message.get("icname") != null)
            this.icname = message.get("icname").toString();
        if (message.get("iccode") != null)
            this.iccode = message.get("iccode").toString();
    }

}

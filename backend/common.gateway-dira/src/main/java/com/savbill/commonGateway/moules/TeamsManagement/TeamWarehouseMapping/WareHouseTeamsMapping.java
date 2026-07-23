package com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltwarehousemanagmentteamsmapping")
@Data
@NoArgsConstructor
public class WareHouseTeamsMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name="team_id",nullable=false)
    private Long teamId;

    @Column(name = "warehouse_id", nullable = false, length = 40)
    private Long  warehouseId;
}

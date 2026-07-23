package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltwarehousemanagmentteamsmapping")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class WareHouseTeamsMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (name="team_id",nullable=false)
    private Long teamId;

    @Column(name = "warehouse_id", nullable = false, length = 40)
    private Long  warehouseId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;
}

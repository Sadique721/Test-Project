package com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltitemassignhistorymapping")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemAssignHistoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "itemid")
    private Long itemId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "specification_history_id")
    private Long specificationHistoryId;

    @Column(name = "createdate")
    private LocalDateTime createdate;

}

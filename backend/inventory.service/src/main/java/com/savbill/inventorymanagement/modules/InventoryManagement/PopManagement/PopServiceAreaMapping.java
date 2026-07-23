package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltpopmanagemengservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PopServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pop_id", nullable = false, length = 40)
    private Long  popId;

    @Column(name = "servicearea_id", nullable = false, length = 40)
    private  Integer serviceAreaId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;
}

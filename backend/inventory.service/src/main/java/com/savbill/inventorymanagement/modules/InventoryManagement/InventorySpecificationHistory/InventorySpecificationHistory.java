package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


    @Data
    @Entity
    @NoArgsConstructor
    @Table(name = "tblhinventoryspecificationhistory")
    @EntityListeners(AuditableListener.class)
    public class InventorySpecificationHistory extends Auditable implements IBaseData {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "itemid")
        private Long itemId;

        @Column(name = "param_id")
        private Long paramId;

        @Column(name = "param_value")
        private String paramValue;

        @Column(name = "is_mandatory")
        private Boolean isMandatory;

        @Column(name = "inven_id")
        private Long invenId;

        @Column(name = "status")
        private String status;

        @Override
        public Serializable getPrimaryKey() {
            return id;
        }

        @Override
        public void setDeleteFlag(boolean deleteFlag) {

        }

        @Override
        public boolean getDeleteFlag() {
            return false;
        }
    }


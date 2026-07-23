package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbltcustomer_inventory_file_mapping")
@Data
public class CustomerInventoryFileMapping {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "customer_inventory_mapping_id", nullable = false)
        private Long customerInventoryMapping;


        @Column(name = "section", nullable = false)
        private String section;

        @Column(name = "filename")
        private String filename;

        @Column(name = "uniquename")
        private String uniquename;

        @Column(name = "latitude")
        private String latitiude;

        @Column(name = "longitude")
        private String longitude;

        @Column(name = "optical_range")
        private String opticalRange;

}

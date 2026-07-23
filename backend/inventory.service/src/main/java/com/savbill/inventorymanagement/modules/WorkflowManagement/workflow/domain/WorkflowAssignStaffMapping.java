package com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.domain;

import lombok.Data;

import javax.persistence.*;

    @Data
    @Entity
    @Table(name = "tblmworkflowassignstaffmapping")
    public class WorkflowAssignStaffMapping {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "event_name")
        private String eventName;

        @Column(name = "entity_id")
        private Integer entityId;

        @Column(name = "staff_id")
        private Integer staffId;

        @Column(name = "team_hierarchy_mapping_id")
        private Integer teamHierarchyMappingId;
    }


package com.savbill.taskmanagement.core.modules.ResolutionReasons.model;

import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltrootcauseresolutionmapping")
public class RootCauseResolutionMapping {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String rootCauseReason;

    @DiffIgnore
    @Column(name = "resolution_id")
    private Long resolutionId;
}

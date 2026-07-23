package com.savbill.ticketmanagement.core.modules.tickets.domain;

import com.savbill.ticketmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblt_resolution_file_mapping")
public class ResoultionFileMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resolution_id", nullable = false)
    @JsonBackReference
    private ResolutionReasons resolution;

//    @Column(name = "section", nullable = false)
//    private String section;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniquename;

    @Column(name = "latitude")
    private String latitiude;

    @Column(name = "longitude")
    private String longitude;
    @Column(name = "case_id")
    private Long caseId;
    @Column(name = "staff_id")
    private Long staffId;
    @Column(name = "resolution_time")
    private LocalDateTime resolutionTime;
    @Column(name = "remarks")
    private String remarks;

//    @Column(name = "optical_range")
//    private String opticalRange;
}

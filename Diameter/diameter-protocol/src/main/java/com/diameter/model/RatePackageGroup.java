package com.diameter.model;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblm_rate_package_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RatePackageGroup{
    @Id
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


}
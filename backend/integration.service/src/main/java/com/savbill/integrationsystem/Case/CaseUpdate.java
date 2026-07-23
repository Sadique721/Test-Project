package com.savbill.integrationsystem.Case;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblcaseupdates")
public class CaseUpdate {

    @Id
    @Column(name = "updateid")
    private Long id;
 /*   @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "caseid")
    @ToString.Exclude*/
    @Column(name = "caseid")
    private Integer ticket;
    @JsonManagedReference
    @OneToMany(mappedBy = "caseUpdate", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CaseUpdateDetails> updateDetails = new ArrayList<>();
    @Column(name = "comment_by")
    private String commentBy;
    private String createby;
    private String updateby;
    @Column(name = "remarktype")
    private String remarkType;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

}

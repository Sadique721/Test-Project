package com.savbill.commonGateway.moules.knowledgeBaseDocs;

import com.savbill.commonGateway.core.data.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmknowledgebasedocs")
public class KnowledgeBaseDocuments extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mvno_id", nullable = false, length = 40)
    private Long mvnoId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "doc_for")
    private String documentFor;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniqueName;

    @Column(name = "remarks")
    private String remarks;


    public KnowledgeBaseDocuments(String eventName, String docType) {
        this.eventName = eventName;
        this.docType = docType;
    }
}

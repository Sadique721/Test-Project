package com.savbill.commonGateway.moules.knowledgeBaseDocs;

import com.savbill.commonGateway.core.data.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeBaseDocDTO extends Auditable {
    private String eventName;
    private String documentFor;
    private String docType;
    private String filename;
    private String uniqueName;
    private String remarks;
}

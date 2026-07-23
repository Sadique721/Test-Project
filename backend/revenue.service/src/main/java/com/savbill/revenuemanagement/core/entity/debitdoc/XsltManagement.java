package com.savbill.revenuemanagement.core.entity.debitdoc;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbltemplatemanagement")
@EntityListeners(AuditableListener.class)
public class XsltManagement extends Auditable {

    @Id
    @DiffIgnore
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "templateid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String templatename;

    @Column(nullable = false, length = 40)
    private String templatetype;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "jrxmlfile", nullable = false)
    private String jrxmlfile;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name="lcoid", length = 40)
    private Integer lcoid;

    @Transient
    private String mvnoName;

  public XsltManagement(XsltManagement xsltManagement) {
  this.id = xsltManagement.id;
  this.templatename = xsltManagement.templatename;
  this.templatetype = xsltManagement.templatetype;
  this.status = xsltManagement.status;
  this.jrxmlfile = xsltManagement.jrxmlfile;
  this.isDelete = xsltManagement.isDelete;
  this.mvnoId = xsltManagement.mvnoId;
  this.buId = xsltManagement.buId;
  this.lcoid = xsltManagement.lcoid;
 }

    public XsltManagement() {

    }
}

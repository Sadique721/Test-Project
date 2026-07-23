package com.savbill.integrationsystem.Case;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltresosubcategorymapping")
public class ResoSubCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="res_id")
    private Long resId;
    @Column(name = "subcate_id")
    private Long subcateId;


}
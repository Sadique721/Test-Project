package com.savbill.inventorymanagement.modules.InventoryManagement.Product;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name="tbltproductparammapping")
public class ProductParameterDefaultValueMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "product_id")
    private Long productId;

    @Column(name="param_id")
    private Long parameterId;

    @Column(name="default_value")
    private String defaultValue;

}

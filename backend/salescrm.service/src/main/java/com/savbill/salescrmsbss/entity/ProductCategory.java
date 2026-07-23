package com.savbill.salescrmsbss.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import javax.persistence.*;

import com.savbill.salescrmsbss.audit.Auditable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmproductcategory")
public class ProductCategory extends Auditable<ProductCategory> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "unit")
	private String unit;

	@Column(name = "mvno_id", updatable = false)
	private Integer mvnoId;

	@Column(name = "has_mac")
	private boolean hasMac;

	@Column(name = "type")
	private String type;

	@Column(name = "status")
	private String status;

	@Column(name = "rms_product_id")
	private String productId;

	public ProductCategory(Long id) {
		this.id = id;
	}

	@Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "has_serial")
	private boolean hasSerial;

	@Column(name = "has_trackable")
	private boolean hasTrackable;
	@Column(name = "has_port")
	private boolean hasPort;

	@Column(name = "has_cas")
	private boolean hasCas;

	@Column(name = "dtvcategory")
	private String dtvCategory;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductCategory other = (ProductCategory) obj;
		return Objects.equals(dtvCategory, other.dtvCategory) && hasCas == other.hasCas && hasMac == other.hasMac
				&& hasPort == other.hasPort && hasSerial == other.hasSerial && hasTrackable == other.hasTrackable
				&& Objects.equals(id, other.id) && Objects.equals(isDeleted, other.isDeleted)
				&& Objects.equals(mvnoId, other.mvnoId) && Objects.equals(name, other.name)
				&& Objects.equals(productId, other.productId) && Objects.equals(status, other.status)
				&& Objects.equals(type, other.type) && Objects.equals(unit, other.unit);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(dtvCategory, hasCas, hasMac, hasPort, hasSerial, hasTrackable, id,
				isDeleted, mvnoId, name, productId, status, type, unit);
		return result;
	}
}

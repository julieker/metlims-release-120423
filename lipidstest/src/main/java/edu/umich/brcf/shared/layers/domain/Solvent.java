
package edu.umich.brcf.shared.layers.domain;



import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "SOLVENT")
public class Solvent implements Serializable {
	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 10, columnDefinition = "VARCHAR2(10)")
	private String id;

	@Basic()
	@Column(name = "NAME", nullable = false, length = 32, columnDefinition = "VARCHAR2(32)")
	private String name;

	@Basic()
	@Column(name = "START_LOGP", nullable = false, precision = 22, scale = 5, columnDefinition = "NUMBER(22,5)")
	private BigDecimal startLogP;

	@Basic()
	@Column(name = "END_LOGP", nullable = false, precision = 22, scale = 5, columnDefinition = "NUMBER(22,5)")
	private BigDecimal endLogP;

	public Solvent() {

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getStartLogP() {
		return startLogP;
	}

	public BigDecimal getEndLogP() {
		return endLogP;
	}
}

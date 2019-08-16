package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "FACTOR_LEVELS")
public class FactorLevel implements Serializable{

	public static FactorLevel instance(String value, Factor factor) {
		return new FactorLevel(null, value, factor);
	}

	public static int FACTOR_VALUE_FIELD_LEN = 40;
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", 
		parameters = {@Parameter(name = "idClass", value = "FactorLevel"), @Parameter(name = "width", value = "8") })
	@Column(name = "LEVEL_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String levelId;
	
	@Basic()
	@Column(name = "VALUE", nullable = false, columnDefinition = "VARCHAR2(40)")
	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FACTOR_ID", referencedColumnName = "FACTOR_ID", nullable = false)
	private Factor factor;

	private FactorLevel(String levelId, String value, Factor factor) 
		{
		this.levelId = levelId;
		this.value = value;
		this.factor = factor;
		}

	public FactorLevel() {   }

	public String getLevelId() {
		return levelId;
	}

	public String getValue() {
		return value;
	}

	public Factor getFactor() {
		return factor;
	}
}

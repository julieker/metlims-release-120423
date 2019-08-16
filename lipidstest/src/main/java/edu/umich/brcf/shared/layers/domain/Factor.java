package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "EXPERIMENTAL_FACTORS")
public class Factor implements Serializable
	{

	public static Factor instance(String factorName, Experiment exp)
		{
		return new Factor(null, factorName, exp);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
		@Parameter(name = "idClass", value = "Factor"), @Parameter(name = "width", value = "7") })
	@Column(name = "FACTOR_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(7)")
	private String factorId;

	@Basic()
	@Column(name = "FACTOR_NAME", nullable = false, columnDefinition = "VARCHAR2(120)")
	private String factorName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPERIMENT_ID", referencedColumnName = "EXP_ID", nullable = false)
	private Experiment exp;

	@OneToMany(mappedBy = "factor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<FactorLevel> levels;

	
	private Factor(String factorId, String factorName, Experiment exp)
		{
		this.factorId = factorId;
		this.factorName = factorName;
		this.exp = exp;
		}

	
	public Factor()  {  }

	public String getFactorId()
		{
		return factorId;
		}

	public String getFactorName()
		{
		return factorName;
		}

	public Experiment getExp()
		{
		return exp;
		}

	public List<FactorLevel> getLevels()
		{
		return levels;
		}
	}

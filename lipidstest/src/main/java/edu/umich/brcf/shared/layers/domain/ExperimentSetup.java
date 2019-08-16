package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity()
@org.hibernate.annotations.Entity(mutable = false)
@Table(name = "EXPERIMENT_SETUP", uniqueConstraints = @UniqueConstraint(columnNames = { "SAMPLE_ID", "LEVEL_ID" }))
public class ExperimentSetup implements Serializable
	{
	public static ExperimentSetup instance(Sample sample, FactorLevel level)
		{
		return new ExperimentSetup(sample, level);
		}

	@Embeddable
	public static class ExpSetupPK implements Serializable
		{
		public static ExpSetupPK instance(Sample sample, FactorLevel level)
			{
			return new ExpSetupPK(sample, level);
			}

		@Column(name = "SAMPLE_ID")
		private String sampleId;

		@Column(name = "LEVEL_ID")
		private String levelId;

		private ExpSetupPK(Sample sample, FactorLevel level)
			{
			this.sampleId = sample.getSampleID();
			this.levelId = level.getLevelId();
			}

		public ExpSetupPK()  {  } 

		
		public boolean equals(Object o)
			{
			if (o != null && o instanceof ExpSetupPK)
				{
				ExpSetupPK that = (ExpSetupPK) o;
				return this.sampleId.equals(that.sampleId) && this.levelId.equals(that.levelId);
				} 
			
			return false;
			}

		public int hashCode()
			{
			return sampleId.hashCode() + levelId.hashCode();
			}
		}

	@EmbeddedId
	protected ExpSetupPK id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_ID", insertable = false, updatable = false)
	// Issue 250
	@org.hibernate.annotations.ForeignKey(name = "EXPERIMENT_SETUP_FK2")
	private Sample sample;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LEVEL_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "EXPERIMENT_SETUP_FK1")
	private FactorLevel level;

	public ExperimentSetup()  { } 

	private ExperimentSetup(Sample sample, FactorLevel level)
		{
		this.sample = sample;
		this.level = level;
		this.id = ExpSetupPK.instance(sample, level);
		}

	public ExpSetupPK getId()
		{
		return id;
		}

	public Sample getSample()
		{
		return sample;
		}

	public FactorLevel getLevel()
		{
		return level;
		}
	}

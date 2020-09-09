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
@Table(name = "EXPERIMENT_ALIQUOT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"EXP_ID", "ALIQUOT_ID" }))
public class ExperimentAliquot implements Serializable
	{
	public static ExperimentAliquot instance(Experiment experiment, Aliquot aliquot)
		{
		return new ExperimentAliquot(experiment, aliquot);
		}

	@EmbeddedId
	protected ExperimentAliquotPK id;

	// Issue 250
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "SAMPLE_ASSAYS_FK1")
	private Experiment experiment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSAY_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "SAMPLE_ASSAYS_FK2")
	private Aliquot aliquot;


	

	public ExperimentAliquot()
		{
		}

	private ExperimentAliquot(Experiment experiment, Aliquot aliquot)
		{
		System.out.println("in new experiment aliquot.......");
		this.aliquot = aliquot;
		this.experiment = experiment;
		this.id = ExperimentAliquotPK.instance(experiment, aliquot);
	
		}

	public ExperimentAliquotPK getId()
		{
		return id;
		}

	public Experiment getExperiment()
		{
		return experiment;
		}

	public Aliquot getAliquot()
		{
		return aliquot;
		}


	
	}

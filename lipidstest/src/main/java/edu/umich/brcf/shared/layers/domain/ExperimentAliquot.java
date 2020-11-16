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

	// Issue 79
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALIQUOT_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "EXPERIMENT_ALIQUOT_FK1")
	// issue 98
	private Aliquot aliquot;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", insertable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "EXPERIMENT_ALIQUOT_FK2")
	private Experiment experiment;

	public ExperimentAliquot()
		{
		}

	private ExperimentAliquot(Experiment experiment, Aliquot aliquot)
		{
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

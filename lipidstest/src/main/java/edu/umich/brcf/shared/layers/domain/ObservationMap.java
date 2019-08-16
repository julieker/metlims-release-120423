package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity()
@Table(name = "PREP_OBSERVATION_MAP")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class ObservationMap implements Serializable
	{
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "PrepObservationMap"), @Parameter(name = "width", value = "9") })
	@Column(name = "ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	protected String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBSERVATION_ID", referencedColumnName = "OBSERVATION_ID", nullable = true, columnDefinition = "CHAR(8)")
	private Observation observation;
	

	protected ObservationMap(String id, Observation observation) 
		{
		this.id=id;
		this.observation=observation;
		}

	public ObservationMap(){  } 

	public String getId() 
		{
		return id;
		}

	public void setId(String id) 
		{
		this.id = id;
		}

	public Observation getObservation() 
		{
		return observation;
		}

	public void setObservation(Observation observation) 
		{
		this.observation = observation;
		}
	}

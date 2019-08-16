package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity()
@DiscriminatorValue(value = "P")
public class PlatePrepObservation extends ObservationMap
	{
	public static PlatePrepObservation instance(PrepPlate prepPlate, Observation observation)
		{
		return new PlatePrepObservation(null, prepPlate, observation);		
		}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBSERVATION_ITEM_ID", referencedColumnName = "PLATE_ID", nullable = true, columnDefinition = "CHAR(9)")
	protected PrepPlate prepPlate;
	
	private PlatePrepObservation(String id, PrepPlate prepPlate, Observation observation) 
		{
		super(id,observation);
		this.prepPlate=prepPlate;
		}
	
	public PlatePrepObservation(){ }

	public PrepPlate getPrepPlate() 
		{
		return prepPlate;
		}

	public void setPrepPlate(PrepPlate prepPlate) 
		{
		this.prepPlate = prepPlate;
		}
	}

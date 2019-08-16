package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity()
@DiscriminatorValue(value = "S")
public class SamplePrepObservation extends ObservationMap{

	public static SamplePrepObservation instance(PreppedItem preppedItem, Observation observation){
		return new SamplePrepObservation(null, preppedItem, observation);		
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBSERVATION_ITEM_ID", referencedColumnName = "PREP_ITEM_ID", nullable = true, columnDefinition = "CHAR(9)")
	protected PreppedItem preppedItem;
	
	private SamplePrepObservation(String id, PreppedItem preppedItem, Observation observation) {
		super(id,observation);
		this.preppedItem=preppedItem;
	}
	
	public SamplePrepObservation(){
	}

	public PreppedItem getPreppedItem() {
		return preppedItem;
	}

	public void setPreppedItem(PreppedItem preppedItem) {
		this.preppedItem = preppedItem;
	}

}

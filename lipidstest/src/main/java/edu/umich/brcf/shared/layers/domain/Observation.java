package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity()
@Table(name = "OBSERVATIONS")
public class Observation implements Serializable{

	public static Observation instance(String description)
		{
		return new Observation(null, description);		
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
		@Parameter(name = "idClass", value = "Observation"), @Parameter(name = "width", value = "8") })
	@Column(name = "OBSERVATION_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String id;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "PREP_ITEM_ID", referencedColumnName = "PREP_ITEM_ID", nullable = true, columnDefinition = "CHAR(9)")
//	protected PreppedSample preppedSample;
	
	@Basic()
	@Column(name = "DESCRIPTION", nullable = true, columnDefinition = "VARCHAR2(1000)")
	private String description;
	
	@OneToMany(mappedBy = "observation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ObservationMap> observationMapList;

	private Observation(String id, String description) 
		{
		this.id=id;
		//this.preppedSample=preppedSample;
		this.description=description;
		observationMapList=new ArrayList<ObservationMap>();
		}
	
	public Observation(){  } 

	public String getId() 
		{
		return id;
		}

	public void setId(String id) 
		{
		this.id = id;
		}

//	public PreppedSample getPreppedSample() {
//		return preppedSample;
//	}
//
//	public void setPreppedSample(PreppedSample preppedSample) {
//		this.preppedSample = preppedSample;
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ObservationMap> getObservationMapList() {
		return observationMapList;
	}
	
	public void setObservationMapList(List<ObservationMap> observationMapList) {
		this.observationMapList = observationMapList;
	}
}

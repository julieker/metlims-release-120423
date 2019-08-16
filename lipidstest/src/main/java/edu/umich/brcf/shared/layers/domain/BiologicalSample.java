package edu.umich.brcf.shared.layers.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
//import javax.persistence.ManyToOne;




import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.metabolomics.layers.domain.Injections;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;


@Entity()
@DiscriminatorValue(value = "S")
public class BiologicalSample extends Sample implements ISampleItem
	{
	public static BiologicalSample instance(String sampleID, String sampleName, Experiment exp,
		Subject subject, GenusSpecies genusOrSpeciesID, String locID, String UserDefSampleType, String UserDefGOS, 
		BigDecimal volume, String volUnits, SampleStatus status, Calendar dateCreated, SampleType sampleType, 
		ExperimentalGroup group, Sample parent)
		{ 
		return new BiologicalSample(sampleID, sampleName, exp, subject, genusOrSpeciesID, locID, UserDefSampleType, 
			UserDefGOS, volume, volUnits, status, dateCreated, sampleType, group, parent);
		}

	
	@OneToMany(mappedBy = "sample", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<PreppedSample> preppedList;

	
	private BiologicalSample( String sampleID, String sampleName, Experiment exp, Subject subject,
	  GenusSpecies genusOrSpecies, String locID, String UserDefSampleType, String UserDefGOS, BigDecimal volume, 
	  String volUnits, SampleStatus status, Calendar dateCreated, SampleType sampleType, ExperimentalGroup group, Sample parent)
		{
		super(sampleID, sampleName, exp, subject, genusOrSpecies, locID, UserDefSampleType, UserDefGOS, volume, 
			volUnits, status, dateCreated, sampleType, group, parent);
		}

	
	public BiologicalSample() {  }

	
	public List<PreppedSample> getPreppedList()
		{
		return preppedList;
		}

	
	public List<Preparation> getPrepList()
		{
		List<Preparation> preps = new ArrayList<Preparation>();
		for (PreppedSample preppedSample : preppedList)
			if (!preps.contains(preppedSample.getSamplePrep()))
				preps.add(preppedSample.getSamplePrep());
		return preps;
		}

	
	 public List<Injections> getInjections()
		 {
		 List injections = new ArrayList<Injections>();
		 for (PreppedSample preppedSample: preppedList)
		 	if(preppedSample.getInjectionList().size()>0)
		 		injections.addAll(preppedSample.getInjectionList());
		 	
		 return injections;
		 }


	@Override
	public String getSampleId()
		{
		return this.getSampleID();
		}
	}


package edu.umich.brcf.metabolomics.layers.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import edu.umich.brcf.metabolomics.layers.domain.GenusSpecies;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentalGroup;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleStatus;
import edu.umich.brcf.shared.layers.domain.SampleType;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;

@Entity()
@DiscriminatorValue(value = "F")
public class FractionSample extends Sample implements ISampleItem
	{
	public static FractionSample instance(String sampleID, String sampleName, Experiment exp, String userDescription,
			GenusSpecies genusOrSpeciesID, String locID, String UserDefSampleType, BigDecimal volume, String volUnits,
			SampleStatus status, boolean sampleControlType, Calendar dateCreated, SampleType sampleType, ExperimentalGroup group, Sample parent) {
		return new FractionSample(sampleID, sampleName, exp, userDescription, genusOrSpeciesID, locID, UserDefSampleType, volume,
				volUnits, status, sampleControlType, dateCreated, sampleType, group, parent);
	}
	
	@OneToMany(mappedBy = "fraction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	List<PreppedFraction> preppedList;
	
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "PARENT_ID", referencedColumnName = "SAMPLE_ID", nullable = true, columnDefinition = "CHAR(9)")
//	private Sample parent;
	// MESSAGING_LOG
	private FractionSample(String sampleID, String sampleName, Experiment exp, String userDescription,
			GenusSpecies genusOrSpecies, String locID, String UserDefSampleType, BigDecimal volume, String volUnits,
			SampleStatus status, boolean sampleControlType, Calendar dateCreated, SampleType sampleType, ExperimentalGroup group, Sample parent){
//		super(sampleID, sampleName, exp, userDescription, genusOrSpecies, locID, UserDefSampleType, volume,
//				volUnits, status, sampleControlType, dateCreated, sampleType, group, parent);
//		this.parent=parent;
	}
	
	public FractionSample(){
		
	}

	public List<PreppedFraction> getPreppedList() {
		return preppedList;
	}

	@Override
	public String getSampleId()
		{
		return this.getSampleID();
		}
	}


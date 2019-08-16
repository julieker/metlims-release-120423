package edu.umich.brcf.shared.layers.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;


@Entity()
@DiscriminatorValue(value = "S")
public class PreppedSample extends PreppedItem
	{

	public static PreppedSample instance(Preparation samplePrep, BiologicalSample sample, PrepWell well, 
		String aliquotType, GeneralPrepSOP generalPrepSOP, BigDecimal volume, String volUnits)
		{
		return new PreppedSample(null, samplePrep, sample, well, aliquotType,generalPrepSOP, volume, volUnits);
		}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_ID", referencedColumnName = "SAMPLE_ID", nullable = true, columnDefinition = "CHAR(9)")
	private BiologicalSample sample;

	private PreppedSample(String itemID, Preparation samplePrep, BiologicalSample sample, PrepWell well, 
		String aliquotType, GeneralPrepSOP generalPrepSOP, BigDecimal volume, String volUnits)
		{
		super(itemID, samplePrep, well, aliquotType, generalPrepSOP, volume, volUnits);
		this.sample = sample;
		}

	public  PreppedSample()  {   } 

	
	public BiologicalSample getSample()
		{
		return sample;
		}

	public void setSample(BiologicalSample sample)
		{
		this.sample = sample;
		}

	public List<PreppedSample> getInjectionList()
		{
		return null;
		}
	}

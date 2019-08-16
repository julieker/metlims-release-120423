package edu.umich.brcf.shared.layers.domain;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umich.brcf.metabolomics.layers.domain.FractionSample;
import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;


@Entity()
@DiscriminatorValue(value = "F")
public class PreppedFraction extends PreppedItem{

	public static PreppedFraction instance(Preparation samplePrep, FractionSample fraction, PrepWell well, String aliquotType, 
		GeneralPrepSOP generalPrepSOP, BigDecimal volume, String volUnits)
		{
		return new PreppedFraction(null, samplePrep, fraction, well, aliquotType, generalPrepSOP, volume, volUnits);		
		}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_ID", referencedColumnName = "SAMPLE_ID", nullable = true, columnDefinition = "CHAR(9)")
	private FractionSample fraction;
	
	private PreppedFraction(String itemID, Preparation samplePrep, FractionSample fraction, PrepWell well, String aliquotType, 
			GeneralPrepSOP generalPrepSOP, BigDecimal volume, String volUnits)
		{
		super(itemID, samplePrep, well, aliquotType, generalPrepSOP, volume, volUnits);
		this.fraction=fraction;
		}
	
	public PreppedFraction(){  } 

	
	public FractionSample getFraction() 
		{
		return fraction;
		}

	public void setFraction(FractionSample fraction) 
		{
		this.fraction = fraction;
		}

	public Object getInjectionList()
		{
		return null;
		}
	}

package edu.umich.brcf.metabolomics.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;


@Entity()
@DiscriminatorValue(value = "GC")
public class GCPlate extends PrepPlate
	{
	public static GCPlate instance(Preparation samplePrep, String plateFormat, Instrument instrument, GCDerivatizationMethod derivatizationMethod) 
		{
		return new GCPlate(null, samplePrep, plateFormat, instrument, derivatizationMethod);
		}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PREP_METHOD", referencedColumnName = "GCD_ID", nullable = true)
	private GCDerivatizationMethod derivatizationMethod;
	
	private GCPlate(String plateID, Preparation samplePrep, String plateFormat, Instrument instrument, GCDerivatizationMethod derivatizationMethod) 
		{
		super(plateID, samplePrep, plateFormat, instrument);
		this.derivatizationMethod = derivatizationMethod;
		}

	public GCPlate() { }

	public void setDerivatizationMethod(GCDerivatizationMethod derivatizationMethod) 
		{
		this.derivatizationMethod = derivatizationMethod;
		}

	public GCDerivatizationMethod getDerivatizationMethod() 
		{
		return derivatizationMethod;
		}
	}

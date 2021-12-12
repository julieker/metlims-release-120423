package edu.umich.brcf.metabolomics.panels.lims.mixtures;
import java.io.Serializable;

public class MixAliquotInfo implements Serializable
	{
	String mixtureId;		
	String mixAliquotConcentrationFinal;
	String aliquotId;
	String aliquotName;
	String mixAliquotConcentration;	
	String mixAliquotConUnits;
	String molecularWeightMix;
	String weightedAmountMix;
	String weightedAmountMixUnit;
	
	// issue 196
	public String getMolecularWeightMix()
		{
		return this.molecularWeightMix;
		}
	
	public void  setMolecularWeightMix(String molecularWeightMix)
		{
		this.molecularWeightMix = molecularWeightMix;
		}
	
	// issue 196
	public String getWeightedAmountMix()
		{
		return this.weightedAmountMix;
		}

	public void  setWeightedAmountMix(String weightedAmountMix)
		{
		this.weightedAmountMix = weightedAmountMix;
		}
	
	// issue 196
	public String getWeightedAmountMixUnit()
		{
		return this.weightedAmountMixUnit;
		}

	public void  setWeightedAmountMixUnit(String weightedAmountMixUnit)
		{
		this.weightedAmountMixUnit = weightedAmountMixUnit;
		}
	
	public String  getMixtureId()
		{
		return this.mixtureId;
		}
	public void  setMixtureId(String mixtureId)
		{
		this.mixtureId = mixtureId;
		}	
	public String  getAliquotName()
		{
		return this.aliquotName;
		}
	public void  setAliquotName(String aliquotName)
		{
		this.aliquotName =aliquotName;
		}
	
	public String  getAliquotId()
		{
		return this.aliquotId;
		}
	public void  setAliquotId(String aliquotId)
		{
		this.aliquotId = aliquotId;
		}
	public String  getMixAliquotConcentrationFinal()
		{
		return this.mixAliquotConcentrationFinal;
		}
	public void  setMixAliquotConcentrationFinal(String mixAliquotConcentrationFinal)
		{
		this.mixAliquotConcentrationFinal = mixAliquotConcentrationFinal;
		}
	
	public String  getMixAliquotConcentration()
		{
		return this.mixAliquotConcentration;
		}
	
	public void  setMixAliquotConcentration(String mixAliquotConcentration)
		{
		this.mixAliquotConcentration = mixAliquotConcentration;
		}
	
	public String  getMixAliquotConUnits()
		{
		return this.mixAliquotConUnits;
		}

	public void  setMixAliquotConUnits(String mixAliquotConUnits)
		{
		this.mixAliquotConUnits = mixAliquotConUnits;
		}
	}



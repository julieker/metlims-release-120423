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



package edu.umich.brcf.metabolomics.panels.lims.mixtures;
import java.io.Serializable;

public class AliquotInfo implements Serializable
	{
 	String aliquotId;
 	String volumeTxt;
 	String concentrationTxt;
 	String concentrationTxtFinal;
 	String concentrationUnitsTxt;
 	String molecularWeightTxt;
 	String weightedAmountTxt;
 	String weightedAmountUnitsTxt;
    String volumeAliquotUnits; // issue 196
 	
	public AliquotInfo (String aliquotId, String volumeTxt)
		{
		
		}
	public String  getVolumeTxt()
		{
		return this.volumeTxt;
		}
	public void  setVolumeTxt(String volumeTxt)
		{
		this.volumeTxt = volumeTxt;
		}
	
	// issue 196
	public String  getVolumeAliquotUnits()
		{
		return this.volumeAliquotUnits; // issue 196
		}
	public void  setVolumeAliquotUnits(String volumeAliquotUnits)
		{
		this.volumeAliquotUnits = volumeAliquotUnits; // issue 196
		} 
	
	
	// issue 196
	public String  getWeightedAmountTxt()
		{
		return this.weightedAmountTxt;
		}
	
	// issue 196
	public void  setWeightedAmountTxt (String weightedAmountTxt)
		{
		this.weightedAmountTxt = weightedAmountTxt;
		}
	
	// issue 196
	public String  getWeightedAmountUnitsTxt()
		{
		return this.weightedAmountUnitsTxt;
		}
	
	// issue 196
	public void  setWeightedAmountUnitsTxt (String weightedAmountUnitsTxt)
		{
		this.weightedAmountUnitsTxt = weightedAmountUnitsTxt;
		}
	
	// issue 196
	public String  getMolecularWeightTxt()
		{
		return this.molecularWeightTxt;
		}
	
	// issue 196
	public void  setMolecularWeightTxt(String molecularWeightTxt)
		{
		this.molecularWeightTxt = molecularWeightTxt;
		}
	
	public String  getConcentrationTxt()
		{
		return this.concentrationTxt;
		}
	
	public void  setConcentrationTxt(String concentrationTxt)
		{
		this.concentrationTxt = concentrationTxt;
		}
	public String  getConcentrationUnitsTxt()
		{
		return this.concentrationUnitsTxt;
		}

	public void  setConcentrationUnitsTxt(String concentrationUnitsTxt)
		{
		this.concentrationUnitsTxt = concentrationUnitsTxt;
		}
		
	public String  getConcentrationTxtFinal()
		{
		return this.concentrationTxtFinal;
		}

	public void  setConcentrationTxtFinal(String concentrationTxtFinal)
		{
		this.concentrationTxtFinal = concentrationTxtFinal;
		}
    
	public String  getAliquotId()
		{
		return this.aliquotId;
		}
	public void  setAliquotId(String aliquotId)
		{
		this.aliquotId = aliquotId;
		}
	}
package edu.umich.brcf.metabolomics.panels.lims.mixtures;
import java.io.Serializable;

public class AliquotInfo implements Serializable
	{
 	String aliquotId;
 	String volumeTxt;
 	String concentrationTxt;
 	String concentrationTxtFinal;
 	String concentrationUnitsTxt;
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
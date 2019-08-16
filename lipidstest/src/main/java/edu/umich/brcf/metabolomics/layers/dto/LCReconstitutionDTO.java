package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;



public class LCReconstitutionDTO implements Serializable{

	private String reconstitutionID;
	private String reconSolvent;
	private BigDecimal reconVolume;
	
	public LCReconstitutionDTO(String reconstitutionID,String reconSolvent,	BigDecimal reconVolume) {
		this.reconstitutionID=reconstitutionID;
		this.reconSolvent = reconSolvent;
		this.reconVolume = reconVolume;
	}
	
	public static LCReconstitutionDTO instance(LCReconstitutionMethod lcMethod){
		return new LCReconstitutionDTO(lcMethod.getReconstitutionID(), lcMethod.getReconSolvent(), lcMethod.getReconVolume());
	}
	
	public LCReconstitutionDTO() {
	}
	
	public String getReconstitutionID() {
		return reconstitutionID;
	}
	public void setReconstitutionID(String reconstitutionID) {
		this.reconstitutionID = reconstitutionID;
	}
	public String getReconSolvent() {
		return reconSolvent;
	}
	public void setReconSolvent(String reconSolvent) {
		this.reconSolvent = reconSolvent;
	}
	public BigDecimal getReconVolume() {
		return reconVolume;
	}
	public void setReconVolume(BigDecimal reconVolume) {
		this.reconVolume = reconVolume;
	}
	public String toString()
    {
		try{
			return "[ReconstitutionID=" + reconstitutionID + ", ReconSolvent=" + reconSolvent + 
			", ReconVolume=" + reconVolume +"]";
		}catch (Exception ex){return "";}
    }
	public boolean equals(LCReconstitutionMethod lcMethod){
		return((this.reconSolvent.trim().equals(lcMethod.getReconSolvent()))&&
				(this.reconVolume.equals(lcMethod.getReconVolume())));
	}
}

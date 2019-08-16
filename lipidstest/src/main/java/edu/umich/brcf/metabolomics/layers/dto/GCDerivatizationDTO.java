package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;


public class GCDerivatizationDTO implements Serializable{
	
	private String derivatizationID;
	private String reagentComposition;
	private String incubationConditions;
	private BigDecimal derivatizationVolume;
	
	public GCDerivatizationDTO(String derivatizationID,String reagentComposition,String incubationConditions ,
			BigDecimal derivatizationVolume) {
		this.derivatizationID=derivatizationID;
		this.reagentComposition = reagentComposition;
		this.incubationConditions = incubationConditions;
		this.derivatizationVolume = derivatizationVolume;
	}
	
	public static GCDerivatizationDTO instance(GCDerivatizationMethod gcMethod){
		return new GCDerivatizationDTO(gcMethod.getDerivatizationID(), gcMethod.getReagentComposition(), 
				gcMethod.getIncubationConditions(), gcMethod.getDerivatizationVolume());
	}
	
	public GCDerivatizationDTO() {
	}
	
	public String getDerivatizationID() {
		return derivatizationID;
	}
	public void setDerivatizationID(String derivatizationID) {
		this.derivatizationID = derivatizationID;
	}
	public String getReagentComposition() {
		return reagentComposition;
	}
	public void setReagentComposition(String reagentComposition) {
		this.reagentComposition = reagentComposition;
	}
	public String getIncubationConditions() {
		return incubationConditions;
	}
	public void setIncubationConditions(String incubationConditions) {
		this.incubationConditions = incubationConditions;
	}
	public BigDecimal getDerivatizationVolume() {
		return derivatizationVolume;
	}
	public void setDerivatizationVolume(BigDecimal derivatizationVolume) {
		this.derivatizationVolume = derivatizationVolume;
	}
	public String toString()
    {
		try{
			return "[DerivatizationID=" + derivatizationID + ", ReagentComposition=" + reagentComposition + 
			", IncubationConditions=" + incubationConditions + ", DerivatizationVolume=" + derivatizationVolume +"]";
		}catch (Exception ex){return "";}
    }
	public boolean equals(GCDerivatizationMethod gcMethod){
		return((this.reagentComposition.trim().equals(gcMethod.getReagentComposition()))&&
				(this.incubationConditions.trim().equals(gcMethod.getIncubationConditions()))&&
				(this.derivatizationVolume.equals(gcMethod.getDerivatizationVolume())));
	}
}

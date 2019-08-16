package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.shared.layers.domain.Experiment;


public class FractionDTO implements Serializable{

	private String fractionId;
	private String fractionName;
	private BigDecimal volume;
	private String volUnits;
	private BigDecimal vialTare;
	private BigDecimal mass;
	private String massUnits;
	private Experiment exp;
	private String parentId;
	private String locId;
	private String notes;
	
	private FractionDTO(String fractionId, String fractionName, BigDecimal volume, String volUnits, 
			BigDecimal vialTare, BigDecimal mass, String massUnits, String parentId, Experiment exp, String locId, String notes){
		this.fractionId=fractionId;
		this.fractionName=fractionName;
		this.volume=volume;
		this.volUnits=volUnits;
		this.vialTare=vialTare;
		this.mass=mass;
		this.massUnits=massUnits;
		this.parentId=parentId;
		this.exp=exp;
		this.locId=locId;
		this.notes=notes;
	}
	
	public FractionDTO() {
	}
			
	public String getFractionId() {
		return fractionId;
	}
	public void setFractionId(String fractionId) {
		this.fractionId = fractionId;
	}
	public String getFractionName() {
		return fractionName;
	}
	public void setFractionName(String fractionName) {
		this.fractionName = fractionName;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public String getVolUnits() {
		return volUnits;
	}
	public void setVolUnits(String volUnits) {
		this.volUnits = volUnits;
	}
	public BigDecimal getVialTare() {
		return vialTare;
	}
	public void setVialTare(BigDecimal vialTare) {
		this.vialTare = vialTare;
	}
	public BigDecimal getMass() {
		return mass;
	}
	public void setMass(BigDecimal mass) {
		this.mass = mass;
	}
	public String getMassUnits() {
		return massUnits;
	}
	public void setMassUnits(String massUnits) {
		this.massUnits = massUnits;
	}
	public Experiment getExp() {
		return exp;
	}
	public void setExp(Experiment exp) {
		this.exp = exp;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLocId() {
		return locId;
	}

	public void setLocId(String locId) {
		this.locId = locId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}

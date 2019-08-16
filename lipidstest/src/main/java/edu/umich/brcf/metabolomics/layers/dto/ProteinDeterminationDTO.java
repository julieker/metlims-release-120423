package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;


public class ProteinDeterminationDTO implements Serializable
	{
	private String id;
//	private String sampleVolume;
	private String bradfordAgent;
	private String wavelength;
	private String incubationTime;
	
	public ProteinDeterminationDTO(String id, String bradfordAgent, String wavelength, 
			String incubationTime) {
		this.id=id;
//		this.sampleVolume=sampleVolume;
		this.bradfordAgent=bradfordAgent;
		this.wavelength=wavelength;
		this.incubationTime=incubationTime;
	}
	
	public static ProteinDeterminationDTO instance(ProtienDeterminationSOP sop){
		return new ProteinDeterminationDTO(sop.getId(), sop.getBradfordAgent(), 
				sop.getWavelength(), sop.getIncubationTime());
	}
	
	public ProteinDeterminationDTO() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
//	public String getSampleVolume() {
//		return sampleVolume;
//	}
//	public void setSampleVolume(String sampleVolume) {
//		this.sampleVolume = sampleVolume;
//	}
	public String getBradfordAgent() {
		return bradfordAgent;
	}
	public void setBradfordAgent(String bradfordAgent) {
		this.bradfordAgent = bradfordAgent;
	}
	public String getWavelength() {
		return wavelength;
	}
	public void setWavelength(String wavelength) {
		this.wavelength = wavelength;
	}
	public String getIncubationTime() {
		return incubationTime;
	}
	public void setIncubationTime(String incubationTime) {
		this.incubationTime = incubationTime;
	}
	public boolean equals(ProtienDeterminationSOP sop){
		return((this.bradfordAgent.trim().equals(sop.getBradfordAgent()))&&(this.wavelength.trim().equals(sop.getWavelength()))&&
				(this.incubationTime.trim().equals(sop.getIncubationTime())));
	}
}

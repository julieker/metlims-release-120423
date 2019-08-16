package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;


public class HomogenizationDTO implements Serializable{
	
	private String id;
	private String beadType;
	private String beadSize;
	private String beadVolume;
	private String vortex;
	private String time;
	private String temp;
	
	public HomogenizationDTO(String id, String beadType, String beadSize, String beadVolume, 
			String vortex, String time, String temp) {
		this.id=id;
		this.beadType=beadType;
		this.beadSize=beadSize;
		this.beadVolume=beadVolume;
		this.vortex=vortex;
		this.time=time;
		this.temp=temp;
	}
	
	public static HomogenizationDTO instance(HomogenizationSOP sop){
		return new HomogenizationDTO(sop.getId(), sop.getBeadType(), sop.getBeadSize(), sop.getBeadVolume(),
				sop.getVortex(), sop.getTime(), sop.getTemp());
	}
	
	public HomogenizationDTO() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBeadType() {
		return beadType;
	}
	public void setBeadType(String beadType) {
		this.beadType = beadType;
	}
	public String getBeadSize() {
		return beadSize;
	}
	public void setBeadSize(String beadSize) {
		this.beadSize = beadSize;
	}
	public String getBeadVolume() {
		return beadVolume;
	}
	public void setBeadVolume(String beadVolume) {
		this.beadVolume = beadVolume;
	}
	public String getVortex() {
		return vortex;
	}
	public void setVortex(String vortex) {
		this.vortex = vortex;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public boolean equals(HomogenizationSOP sop){
		return((this.beadType.trim().equals(sop.getBeadType()))&&(this.getBeadSize().trim().equals(sop.getBeadSize()))&&
				(this.beadVolume.trim().equals(sop.getBeadVolume()))&&(this.vortex.trim().equals(sop.getVortex()))&&
				(this.time.trim().equals(sop.getTime()))&&(this.temp.trim().equals(sop.getTemp())));
	}
}

package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;


public class PreppedFractionDTO implements Serializable{

	private String id;
	private String volume;
	private String volUnits;
	
	public PreppedFractionDTO() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getVolUnits() {
		return volUnits;
	}
	public void setVolUnits(String volUnits) {
		this.volUnits = volUnits;
	}
}

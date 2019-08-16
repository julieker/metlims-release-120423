package edu.umich.brcf.metabolomics.layers.dto;


public class CEFCompoundBindingDTO {
	Double retentionTime;
	Double abundance;
	Double height;
	Double mass;
	Double volume;
	String algorithm;

	public Double getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(Double retentionTime) {
		this.retentionTime = retentionTime;
	}

	public Double getAbundance() {
		return abundance;
	}

	public void setAbundance(Double abundance) {
		this.abundance = abundance;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getMass() {
		return mass;
	}

	public void setMass(Double mass) {
		this.mass = mass;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}

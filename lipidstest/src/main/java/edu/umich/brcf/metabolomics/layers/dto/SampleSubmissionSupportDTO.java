package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

public class SampleSubmissionSupportDTO implements Serializable{
	
	private String genusName;
	private Long gsID;
	private String sampleType;
	private String stID;

	public SampleSubmissionSupportDTO() {
		
	}

	public String getGenusName() {
		return genusName;
	}

	public void setGenusName(String genusName) {
		this.genusName = genusName;
	}

	public Long getGsID() {
		return gsID;
	}

	public void setGsID(Long gsID) {
		this.gsID = gsID;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getStID() {
		return stID;
	}

	public void setStID(String stID) {
		this.stID = stID;
	}

}

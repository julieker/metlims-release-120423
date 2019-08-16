package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.domain.PreppedSample;


public class PreppedSampleDTO implements Serializable{

	private String id;
	private String sampleid;
	private String well;
	private String volume;
	private String volUnits;
	private String bufferType;
	private BigDecimal bufferVolume;
	private BigDecimal sampleDiluted;
	private String dilutant;
	private BigDecimal dilutantVolume;
	private BigDecimal volumeTransferred;
	private String homogenizationID;
//	private String generalPrepMethod;
//	private String matrixPrepMethod;
	
//	public PreppedSampleDTO(String id, String volume, String volUnits, String generalPrepMethod, String matrixPrepMethod) {
//		this.id = id;
//		this.volume = volume;
//		this.volUnits = volUnits;
//		this.generalPrepMethod = generalPrepMethod;
//		this.matrixPrepMethod = matrixPrepMethod;
//	}
	
	public PreppedSampleDTO() {
	}

	public PreppedSampleDTO(PreppedItem preppedItem) 
		{
		this.id = preppedItem.getItemID();
		
		if(preppedItem instanceof PreppedSample)
			this.sampleid= ((PreppedSample)preppedItem).getSample().getSampleID();
		else
			this.sampleid= ((PreppedFraction)preppedItem).getFraction().getSampleID();
		
		this.well = preppedItem.getWell().getLocation();
		this.volume = preppedItem.getVolume()+"";
		this.volUnits = preppedItem.getVolUnits();
		this.bufferType=preppedItem.getBufferType();
		this.bufferVolume=preppedItem.getBufferVolume();
		this.sampleDiluted=preppedItem.getSampleDiluted();
		this.dilutant=preppedItem.getDilutant();
		this.dilutantVolume=preppedItem.getDilutantVolume();
		this.volumeTransferred=preppedItem.getVolumeTransferred();
		this.homogenizationID=(preppedItem.getHomogenization()!=null)?preppedItem.getHomogenization().getId():"";
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSampleid() {
		return sampleid;
	}

	public void setSampleid(String sampleid) {
		this.sampleid = sampleid;
	}

	public String getWell() {
		return well;
	}

	public void setWell(String well) {
		this.well = well;
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
	
	public String getBufferType() {
		return bufferType;
	}

	public void setBufferType(String bufferType) {
		this.bufferType = bufferType;
	}

	public BigDecimal getBufferVolume() {
		return bufferVolume;
	}

	public void setBufferVolume(BigDecimal bufferVolume) {
		this.bufferVolume = bufferVolume;
	}

	public BigDecimal getSampleDiluted() {
		return sampleDiluted;
	}

	public void setSampleDiluted(BigDecimal sampleDiluted) {
		this.sampleDiluted = sampleDiluted;
	}

	public String getDilutant() {
		return dilutant;
	}

	public void setDilutant(String dilutant) {
		this.dilutant = dilutant;
	}

	public BigDecimal getDilutantVolume() {
		return dilutantVolume;
	}

	public void setDilutantVolume(BigDecimal dilutantVolume) {
		this.dilutantVolume = dilutantVolume;
	}

	public BigDecimal getVolumeTransferred() {
		return volumeTransferred;
	}

	public void setVolumeTransferred(BigDecimal volumeTransferred) {
		this.volumeTransferred = volumeTransferred;
	}

	public String getHomogenizationID() {
		return homogenizationID;
	}

	public void setHomogenizationID(String homogenizationID) {
		this.homogenizationID = homogenizationID;
	}
}

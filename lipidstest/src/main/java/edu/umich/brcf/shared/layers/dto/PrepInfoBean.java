package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;


public class PrepInfoBean implements Serializable{
	
	private String bufferType;
	private BigDecimal bufferVolume;
	private GeneralPrepSOP generalPrepMethod;
	private HomogenizationSOP homogenization;
	private ProtienDeterminationSOP protienDetermination;
	private BigDecimal sampleDiluted;
	private String dilutant;
	private BigDecimal dilutantVolume;
	
	public PrepInfoBean(String bufferType, BigDecimal bufferVolume, GeneralPrepSOP generalPrepMethod, 
			HomogenizationSOP homogenization, ProtienDeterminationSOP protienDetermination, 
			BigDecimal sampleDiluted, String dilutant, BigDecimal dilutantVolume) {
		this.bufferType=bufferType;
		this.bufferVolume = bufferVolume;
		this.generalPrepMethod = generalPrepMethod;
		this.homogenization = homogenization;
		this.protienDetermination = protienDetermination;
		this.sampleDiluted=sampleDiluted;
		this.dilutant=dilutant;
		this.dilutantVolume=dilutantVolume;
	}
	
	public PrepInfoBean() {
	}

	public BigDecimal getBufferVolume() {
		return bufferVolume;
	}
	public void setBufferVolume(BigDecimal bufferVolume) {
		this.bufferVolume = bufferVolume;
	}
	public GeneralPrepSOP getGeneralPrepMethod() {
		return generalPrepMethod;
	}
	public void setGeneralPrepMethod(GeneralPrepSOP generalPrepMethod) {
		this.generalPrepMethod = generalPrepMethod;
	}
	public HomogenizationSOP getHomogenization() {
		return homogenization;
	}
	public void setHomogenization(HomogenizationSOP homogenization) {
		this.homogenization = homogenization;
	}
	public ProtienDeterminationSOP getProtienDetermination() {
		return protienDetermination;
	}
	public void setProtienDetermination(ProtienDeterminationSOP protienDetermination) {
		this.protienDetermination = protienDetermination;
	}

	public String getBufferType() {
		return bufferType;
	}

	public void setBufferType(String bufferType) {
		this.bufferType = bufferType;
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
}

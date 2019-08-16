package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class BufferBean implements Serializable{

	private String bufferType;
	private BigDecimal bufferVolume;
	
	public BufferBean(String bufferType, BigDecimal bufferVolume) {
		this.bufferType=bufferType;
		this.bufferVolume = bufferVolume;
	}
	
	public BufferBean() {
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

}

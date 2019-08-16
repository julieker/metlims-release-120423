package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class DilutionBean implements Serializable
	{
	private BigDecimal sampleDiluted;
	private String dilutant;
	private BigDecimal dilutantVolume;
	
	
	public DilutionBean(BigDecimal sampleDiluted, String dilutant, BigDecimal dilutantVolume) 
		{
		this.sampleDiluted=sampleDiluted;
		this.dilutant=dilutant;
		this.dilutantVolume=dilutantVolume;
		}
	
	public DilutionBean() {   }

	public BigDecimal getSampleDiluted() 
		{
		return sampleDiluted;
		}

	public void setSampleDiluted(BigDecimal sampleDiluted) 
		{
		this.sampleDiluted = sampleDiluted;
		}

	public String getDilutant() 
		{
		return dilutant;
		}

	public void setDilutant(String dilutant) 
		{
		this.dilutant = dilutant;
		}

	public BigDecimal getDilutantVolume() 
		{
		return dilutantVolume;
		}

	public void setDilutantVolume(BigDecimal dilutantVolume) 
		{
		this.dilutantVolume = dilutantVolume;
		}
	}

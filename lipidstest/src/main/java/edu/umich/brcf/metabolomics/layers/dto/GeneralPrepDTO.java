package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;


public class GeneralPrepDTO implements Serializable
	{
	private String id;
	private BigDecimal sampleVolume;
	private String crashSolvent;
	private String recoveryStandardContent;
	private BigDecimal crashVolume;
	private String vortex;
	private String spin;
	private String nitrogenBlowdownTime;
	private String lyophilizerTime;
	private BigDecimal gcVolume;
	private BigDecimal lcVolume;
	
	
	public GeneralPrepDTO(String id, BigDecimal sampleVolume, String crashSolvent, String recoveryStandardContent, BigDecimal crashVolume, 
			String vortex, String spin, String nitrogenBlowdownTime, String lyophilizerTime, BigDecimal gcVolume,
			BigDecimal lcVolume) 
		{
		this.id=id;
		this.sampleVolume=sampleVolume;
		this.crashSolvent = crashSolvent;
		this.recoveryStandardContent = recoveryStandardContent;
		this.crashVolume = crashVolume;
		this.vortex = vortex;
		this.spin = spin;
		this.nitrogenBlowdownTime = nitrogenBlowdownTime;
		this.lyophilizerTime = lyophilizerTime;
		this.gcVolume = gcVolume;
		this.lcVolume = lcVolume;
		}
	
	
	public static GeneralPrepDTO instance(GeneralPrepSOP gSOP)
		{
		return new GeneralPrepDTO(gSOP.getPrepID(), gSOP.getSampleVolume(), gSOP.getCrashSolvent(), gSOP.getRecoveryStandardContent(), 
				gSOP.getCrashVolume(), gSOP.getVortex(), gSOP.getSpin(), gSOP.getNitrogenBlowdownTime(), 
				gSOP.getLyophilizerTime(), gSOP.getGCVolume(), gSOP.getLCVolume());
		}
	
	public GeneralPrepDTO() { }
	
	public String getId() 
		{
		return id;
		}

	public void setId(String id) 
		{
		this.id = id;
		}

	public String getCrashSolvent() 
		{
		return crashSolvent;
		}
	
	public void setCrashSolvent(String crashSolvent) 
		{
		this.crashSolvent = crashSolvent;
		}
	
	public String getRecoveryStandardContent() 
		{
		return recoveryStandardContent;
		}

	public void setRecoveryStandardContent(String recoveryStandardContent) 
		{
		this.recoveryStandardContent = recoveryStandardContent;
		}
	
	public BigDecimal getCrashVolume() 
		{
		return crashVolume;
		}
	
	public void setCrashVolume(BigDecimal crashVolume) 
		{
		this.crashVolume = crashVolume;
		}
	
	public String getVortex() 
		{
		return vortex;
		}
	
	public void setVortex(String vortex) 
		{
		this.vortex = vortex;
		}
	
	public String getSpin() 
		{
		return spin;
		}
	
	public void setSpin(String spin) 
		{
		this.spin = spin;
		}
	
	public String getNitrogenBlowdownTime() 
		{
		return nitrogenBlowdownTime;
		}
	public void setNitrogenBlowdownTime(String nitrogenBlowdownTime) {
		this.nitrogenBlowdownTime = nitrogenBlowdownTime;
	}
	public String getLyophilizerTime() 
		{
		return lyophilizerTime;
		}
	
	public void setLyophilizerTime(String lyophilizerTime) 
		{
		this.lyophilizerTime = lyophilizerTime;
		}
	
	public BigDecimal getGcVolume() 
		{
		return gcVolume;
		}
	
	public void setGcVolume(BigDecimal gcVolume) 
		{
		this.gcVolume = gcVolume;
		}
	
	public BigDecimal getLcVolume() 
		{
		return lcVolume;
		}
	
	public void setLcVolume(BigDecimal lcVolume) 
		{
		this.lcVolume = lcVolume;
		}
	
	public BigDecimal getSampleVolume() 
		{
		return sampleVolume;
		}

	public void setSampleVolume(BigDecimal sampleVolume) 
		{
		this.sampleVolume = sampleVolume;
		}

	public String toString()
		{
		try
			{
			return "[SampleVolume="+sampleVolume+", CrashSolvent=" + crashSolvent + ", RecoveryStandardContent=" + recoveryStandardContent +
	        ", CrashVolume=" + crashVolume + ", Vortex=" + vortex + ", Spin=" + spin + 
	        ", NitrogenBlowdownTime=" + nitrogenBlowdownTime + ", LyophilizerTime=" + lyophilizerTime + 
	        ", GC_Volume=" + gcVolume + ", LC_Volume=" + lcVolume +"]";
			} 
		catch (Exception ex){ return "";  }
		}
	
	
	public boolean equals(GeneralPrepSOP gSOP)
		{
		return((this.sampleVolume.equals(gSOP.getSampleVolume()))&&(this.crashSolvent.trim().equals(gSOP.getCrashSolvent()))
				&&(this.recoveryStandardContent.trim().equals(gSOP.getRecoveryStandardContent()))&&
				(this.crashVolume.equals(gSOP.getCrashVolume()))&&(this.vortex.trim().equals(gSOP.getVortex()))&&
				(this.spin.trim().equals(gSOP.getSpin()))&&(this.nitrogenBlowdownTime.trim().equals(gSOP.getNitrogenBlowdownTime()))&&
				(this.lyophilizerTime.trim().equals(gSOP.getLyophilizerTime()))&&(this.gcVolume.equals(gSOP.getGCVolume()))&&
				(this.lcVolume.equals(gSOP.getLCVolume())));
		}
	}

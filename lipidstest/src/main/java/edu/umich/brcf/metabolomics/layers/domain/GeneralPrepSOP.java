package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@Entity()
@Table(name = "GENERAL_PREP")
public class GeneralPrepSOP implements Serializable
	{
	public final static String DEFAULT_SOP = "GP000278";

	public static GeneralPrepSOP instance(BigDecimal sampleVolume, String crashSolvent, String recoveryStandardContent,
	   BigDecimal crashVolume, String vortex, String spin, String nitrogenBlowdownTime, String lyophilizerTime, BigDecimal GCVolume, BigDecimal LCVolume)
		{
		return new GeneralPrepSOP(null, sampleVolume, crashSolvent,recoveryStandardContent, crashVolume, vortex, 
		 spin, nitrogenBlowdownTime, lyophilizerTime, GCVolume, LCVolume);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "GeneralPrep"),
			@Parameter(name = "width", value = "8") })
	@Column(name = "PREP_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String prepID;

	@Basic()
	@Column(name = "SAMPLE_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal sampleVolume;

	@Basic()
	@Column(name = "CRASH_SOLVENT", nullable = true, columnDefinition = "VARCHAR2(20)")
	private String crashSolvent;

	@Basic()
	@Column(name = "RECOVERY_STANDARD_CONTENT", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String recoveryStandardContent;

	@Basic()
	@Column(name = "CRASH_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal crashVolume;

	@Basic()
	@Column(name = "VORTEX", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String vortex;

	@Basic()
	@Column(name = "SPIN", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String spin;

	@Basic()
	@Column(name = "NITROGEN_BLOWDOWN_TIME", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String nitrogenBlowdownTime;

	@Basic()
	@Column(name = "LYOPHILIZER_TIME", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String lyophilizerTime;

	@Basic()
	@Column(name = "GC_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal GCVolume;

	@Basic()
	@Column(name = "LC_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal LCVolume;

	
	private GeneralPrepSOP(String prepID, BigDecimal sampleVolume, String crashSolvent, String recoveryStandardContent,
	 BigDecimal crashVolume, String vortex, String spin, String nitrogenBlowdownTime, String lyophilizerTime,
	 BigDecimal GCVolume, BigDecimal LCVolume)
		{
		this.prepID = prepID;
		this.sampleVolume = sampleVolume;
		this.crashSolvent = crashSolvent;
		this.recoveryStandardContent = recoveryStandardContent;
		this.crashVolume = crashVolume;
		this.vortex = vortex;
		this.spin = spin;
		this.nitrogenBlowdownTime = nitrogenBlowdownTime;
		this.lyophilizerTime = lyophilizerTime;
		this.GCVolume = GCVolume;
		this.LCVolume = LCVolume;
		}

	public GeneralPrepSOP() { }

	
	public String getPrepID()
		{
		return prepID;
		}

	public void setPrepID(String prepID)
		{
		this.prepID = prepID;
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

	
	public void setNitrogenBlowdownTime(String nitrogenBlowdownTime)
		{
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

	
	public BigDecimal getGCVolume()
		{
		return GCVolume;
		}

	
	public void setGCVolume(BigDecimal volume)
		{
		GCVolume = volume;
		}

	
	public BigDecimal getLCVolume()
		{
		return LCVolume;
		}

	
	public void setLCVolume(BigDecimal volume)
		{
		LCVolume = volume;
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
			return "[SampleVolume=" + sampleVolume + " µL, CrashSolvent=" + crashSolvent + ", RecoveryStandardContent="
				+ recoveryStandardContent + ", CrashVolume=" + crashVolume + " µL, Vortex=" + vortex + ", Spin=" + spin
				+ ", NitrogenBlowdownTime=" + nitrogenBlowdownTime + ", LyophilizerTime=" + lyophilizerTime + ", GC_Volume="
				+ GCVolume + " µL, LC_Volume=" + LCVolume + " µL]";
			} 
		catch (Exception ex) { return ""; }
		}
	}

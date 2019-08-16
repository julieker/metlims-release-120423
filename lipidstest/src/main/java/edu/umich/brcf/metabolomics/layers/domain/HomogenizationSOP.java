package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "HOMOGENIZATION_SOP")
public class HomogenizationSOP implements Serializable
	{
	public final static String DEFAULT_SOP = "H0000001";

	public static HomogenizationSOP instance(String beadType, String beadSize,
			String beadVolume, String vortex, String time, String temp)
		{
		return new HomogenizationSOP(null, beadType, beadSize, beadVolume,
				vortex, time, temp);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Homogenization"),
			@Parameter(name = "width", value = "8") })
	@Column(name = "ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String id;

	@Basic()
	@Column(name = "BEAD_TYPE", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String beadType;

	@Basic()
	@Column(name = "BEAD_SIZE", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String beadSize;

	@Basic()
	@Column(name = "BEAD_VOLUME", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String beadVolume;

	@Basic()
	@Column(name = "VORTEX", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String vortex;

	@Basic()
	@Column(name = "TIME", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String time;

	@Basic()
	@Column(name = "TEMPERATURE", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String temp;

	private HomogenizationSOP(String id, String beadType, String beadSize,
			String beadVolume, String vortex, String time, String temp)
		{
		this.id = id;
		this.beadType = beadType;
		this.beadSize = beadSize;
		this.beadVolume = beadVolume;
		this.vortex = vortex;
		this.time = time;
		this.temp = temp;
		}

	public HomogenizationSOP()
		{
		}

	public String getId()
		{
		return id;
		}

	public void setId(String id)
		{
		this.id = id;
		}

	public String getBeadType()
		{
		return beadType;
		}

	public void setBeadType(String beadType)
		{
		this.beadType = beadType;
		}

	public String getBeadSize()
		{
		return beadSize;
		}

	public void setBeadSize(String beadSize)
		{
		this.beadSize = beadSize;
		}

	public String getBeadVolume()
		{
		return beadVolume;
		}

	public void setBeadVolume(String beadVolume)
		{
		this.beadVolume = beadVolume;
		}

	public String getVortex()
		{
		return vortex;
		}

	public void setVortex(String vortex)
		{
		this.vortex = vortex;
		}

	public String getTime()
		{
		return time;
		}

	public void setTime(String time)
		{
		this.time = time;
		}

	public String getTemp()
		{
		return temp;
		}

	public void setTemp(String temp)
		{
		this.temp = temp;
		}

	public String toString()
		{
		try
			{
			return "[BeadType=" + beadType + ", BeadSize=" + beadSize
					+ ", BeadVolume=" + beadVolume + ", Vortex=" + vortex
					+ ", Time=" + time + ", Temp=" + temp + "]";
			} catch (Exception ex)
			{
			return "";
			}
		}
	}

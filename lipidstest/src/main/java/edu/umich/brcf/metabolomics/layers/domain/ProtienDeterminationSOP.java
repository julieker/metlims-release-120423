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
@Table(name = "PROTIENDETERMINATION_SOP")
public class ProtienDeterminationSOP implements Serializable
	{

	public final static String DEFAULT_SOP = "PD000001";

	public static ProtienDeterminationSOP instance(String bradfordAgent,
			String wavelength, String incubationTime)
		{
		return new ProtienDeterminationSOP(null, bradfordAgent, wavelength,
				incubationTime);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "ProtienDetermination"),
			@Parameter(name = "width", value = "8") })
	@Column(name = "ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String id;

	// @Basic()
	// @Column(name = "SAMPLE_VOLUME", nullable = true, columnDefinition =
	// "VARCHAR2(30)")
	// private String sampleVolume;

	@Basic()
	@Column(name = "BRADFORD_AGENT", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String bradfordAgent;

	@Basic()
	@Column(name = "WAVELENGTH", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String wavelength;

	@Basic()
	@Column(name = "INCUBATION_TIME", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String incubationTime;

	private ProtienDeterminationSOP(String id, String bradfordAgent,
			String wavelength, String incubationTime)
		{
		this.id = id;
		// this.sampleVolume=sampleVolume;
		this.bradfordAgent = bradfordAgent;
		this.wavelength = wavelength;
		this.incubationTime = incubationTime;
		}

	public ProtienDeterminationSOP()
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

	// public String getSampleVolume() {
	// return sampleVolume;
	// }
	//
	// public void setSampleVolume(String sampleVolume) {
	// this.sampleVolume = sampleVolume;
	// }

	public String getBradfordAgent()
		{
		return bradfordAgent;
		}

	public void setBradfordAgent(String bradfordAgent)
		{
		this.bradfordAgent = bradfordAgent;
		}

	public String getWavelength()
		{
		return wavelength;
		}

	public void setWavelength(String wavelength)
		{
		this.wavelength = wavelength;
		}

	public String getIncubationTime()
		{
		return incubationTime;
		}

	public void setIncubationTime(String incubationTime)
		{
		this.incubationTime = incubationTime;
		}

	public String toString()
		{
		try
			{
			return "[BradfordAgent=" + bradfordAgent + ", Wavelength="
					+ wavelength + ", IncubationTime=" + incubationTime + "]";
			} catch (Exception ex)
			{
			return "";
			}
		}
	}

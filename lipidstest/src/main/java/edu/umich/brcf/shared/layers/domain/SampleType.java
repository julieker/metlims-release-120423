package edu.umich.brcf.shared.layers.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;


@Entity()
@Table(name = "SAMPLE_TYPE")
public class SampleType implements IClusterable
	{

	public static SampleType instance(String description, String usage)
		{
		return new SampleType(null, description);
		}

	@Id()
	@Column(name = "SAMPLE_TYPE_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(7)")
	private String sampleTypeId;

	@Basic()
	@Column(name = "DESCRIPTION", nullable = false, columnDefinition = "VARCHAR2(150)")
	private String description;

	private SampleType(String sampleTypeId, String description)
		{
		super();
		this.description = description;
		this.sampleTypeId = sampleTypeId;
		}

	public SampleType()
		{

		}

	public String getSampleTypeId()
		{
		return sampleTypeId;
		}

	public String getDescription()
		{
		return description;
		}

	}
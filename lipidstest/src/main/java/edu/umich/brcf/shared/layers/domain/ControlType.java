package edu.umich.brcf.shared.layers.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;


@Entity()
@Table(name = "CONTROL_TYPE")
public class ControlType implements IClusterable
	{
	public static ControlType instance(String description, String platformId, String usage)
		{
		return new ControlType(null, platformId, description);
		}
	

	@Id()
	@Column(name = "CONTROL_TYPE_ID", unique = true, nullable = false, length = 7, columnDefinition = "CHAR(9)")
	private String controlTypeId;

	@Basic()
	@Column(name = "CONTROL_TYPE_NAME", nullable = false, columnDefinition = "VARCHAR2(50)")
	private String description;

	@Basic()
	@Column(name = "PLATFORM_ID", nullable = false, columnDefinition = "CHAR(4)")
	private String platformId;

	
	private ControlType(String controlTypeId, String description, String platformId)
		{
		super();
		this.description = description;
		this.controlTypeId = controlTypeId;
		this.platformId = platformId;
		}

	public ControlType()  {  }

	
	public String getControlTypeId()
		{
		return controlTypeId;
		}

	public String getDescription()
		{
		return description;
		}

	public String getPlatformId()
		{
		return platformId;
		}
	}

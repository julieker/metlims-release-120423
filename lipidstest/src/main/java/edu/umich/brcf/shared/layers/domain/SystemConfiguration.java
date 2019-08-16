package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "SYSTEM_CONFIGURATION")
public class SystemConfiguration implements Serializable
	{
	@EmbeddedId
	private SystemConfigurationPK id;

	
	public SystemConfiguration()  {  } 

	
	public String getParameter()
		{
		return id.getParameter();
		}

	public Boolean isUnique()
		{
		return id.getIsUniqueParameter();
		}

	public String getValue()
		{
		return id.getValue();
		}

	public Long getValueAsLong()
		{
		return Long.parseLong(id.getValue());
		}

	public Double getValueAsDouble()
		{
		return Double.parseDouble(id.getValue());
		}
	}

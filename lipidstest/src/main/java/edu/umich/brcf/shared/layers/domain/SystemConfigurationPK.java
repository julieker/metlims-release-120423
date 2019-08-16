package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class SystemConfigurationPK implements Serializable
	{
	@Basic()
	@Column(name = "PARAMETER", length = 32, nullable = false)
	String parameter;

	@Basic()
	@Column(name = "IS_PARAMETER_UNIQUE", length = 1, nullable = false)
	Boolean isUniqueParameter;

	@Basic()
	@Column(name = "VALUE", length = 128, nullable = false)
	String value;

	public SystemConfigurationPK()  {   }

	
	public boolean equals(Object o)
		{
		if (!(o instanceof SystemConfigurationPK))
			return false;
		
		SystemConfigurationPK that = (SystemConfigurationPK) o;
		if (this.isUniqueParameter == that.isUniqueParameter && this.parameter.equals(that.parameter) && this.value.equals(that.value))
			return true;
		
		return false;
		}

	public int hashCode()
		{
		return parameter.hashCode() + value.hashCode() + isUniqueParameter.hashCode();
		}

	public String getParameter()
		{
		return parameter;
		}

	public Boolean getIsUniqueParameter()
		{
		return isUniqueParameter;
		}

	public String getValue()
		{
		return value;
		}
	}

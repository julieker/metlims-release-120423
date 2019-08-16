////////////////////////////////////////////////////
// ExperimentInventoryInfo.java
// Written by Jan Wigginton, Nov 12, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class ExperimentInventoryInfo implements Serializable
	{
	String expId, clientId;
	String contactName, contactPhone, piName, piPhone;
	Calendar completionDate;
	String samplesDescriptor;
	Integer sampleCount;
	

	public ExperimentInventoryInfo() { }

	
	public Integer getSampleCount()
		{
		return sampleCount;
		}

	public void setSampleCount(Integer sampleCount)
		{
		this.sampleCount = sampleCount;
		}


	public String getExpId()
		{
		return expId;
		}

	public String getClientId()
		{
		return clientId;
		}

	public String getContactName()
		{
		return contactName;
		}
	
	public String getCompletionDateAsStr()
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(completionDate.getTime());
		}

	public String getContactPhone()
		{
		return contactPhone;
		}

	public void setExpId(String expId)
		{
		this.expId = expId;
		}

	
	public void setClientId(String clientId)
		{
		this.clientId = clientId;
		}


	public void setContactName(String contactName)
		{
		this.contactName = contactName;
		}


	public void setContactPhone(String contactPhone)
		{
		this.contactPhone = contactPhone;
		}


	public Calendar getCompletionDate()
		{
		return completionDate;
		}

	public String getSamplesDescriptor()
		{
		return samplesDescriptor;
		}


	public void setCompletionDate(Calendar completionDate)
		{
		this.completionDate = completionDate;
		}

	public void setSamplesDescriptor(String samplesDescriptor)
		{
		this.samplesDescriptor = samplesDescriptor;
		}


	public String getPiName()
		{
		return piName;
		}


	public String getPiPhone()
		{
		return piPhone;
		}


	public void setPiName(String piName)
		{
		this.piName = piName;
		}


	public void setPiPhone(String piPhone)
		{
		this.piPhone = piPhone;
		} 
	}

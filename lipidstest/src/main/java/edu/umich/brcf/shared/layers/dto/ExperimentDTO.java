package edu.umich.brcf.shared.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.shared.layers.domain.Experiment;

// Issue 206
public class ExperimentDTO implements Serializable
	{
	private String expID;
	private String expName;
	private String projID;
	private String expDescription;
	private String priority;
	private String notes;
	private String serviceRequest;
	private Boolean isChear;

	
	private ExperimentDTO(String expID, String expName, String projID, String expDescription, String priority,
		String notes, String serviceRequest, Boolean isChear)
		{
		this.expID = expID;
		this.expName = expName;
		this.projID = projID;
		this.expDescription = expDescription;
		this.priority = priority;
		this.notes = notes;
		this.serviceRequest = serviceRequest;
		this.isChear = isChear;
		}

	
	public ExperimentDTO() { } 

	
	public static ExperimentDTO instance(String expName, String projID, String expDescription, String priority,
			String notes, String serviceRequest, Boolean isChear)
		{
		return new ExperimentDTO(null, expName, projID, expDescription,
				priority, notes, serviceRequest, isChear);
		}

	public static ExperimentDTO instance(Experiment exp)
		{
		return new ExperimentDTO(exp.getExpID(), exp.getExpName(), exp.getProject().getProjectID(), 
				exp.getExpDescription(), exp.getPriority().getId(), exp.getNotes(), exp.getServiceRequest(), exp.getIsChear());
		}

	
	public String getExpID()
		{
		return expID;
		}

	
	public void setExpID(String expID)
		{
		this.expID = expID;
		}

	
	public String getExpName()
		{
		return expName;
		}

	
	public void setExpName(String expName)
		{
		this.expName = expName;
		}

	
	public String getProjID()
		{
		return projID;
		}

	public void setProjID(String projID)
		{
		this.projID = projID;
		}

	public String getExpDescription()
		{
		return expDescription;
		}

	public void setExpDescription(String expDescription)
		{
		this.expDescription = expDescription;
		}

	public String getPriority()
		{
		return priority;
		}

	public void setPriority(String priority)
		{
		this.priority = priority;
		}

	public String getNotes()
		{
		return notes;
		}

	public void setNotes(String notes)
		{
		this.notes = notes;
		}

	public String getServiceRequest()
		{
		return serviceRequest;
		}

	public void setServiceRequest(String serviceRequest)
		{
		this.serviceRequest = serviceRequest;
		}

// Issue 206
	public Boolean getIsChear() 
		{
		return isChear;
		}

	public void setIsChear(Boolean isChear) 
		{
		this.isChear = isChear;
		}
	
	}

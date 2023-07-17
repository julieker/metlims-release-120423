
////////////////////////////////////////////////////
// ProcessTrackingDetailsDTO.java

// Created by by Julie Keros July 24 2022 
// issue 210
////////////////////////////////////////////////////
// Updated by Julie Keros May 28, 2020

package edu.umich.brcf.shared.layers.dto;

// issue 61 2020
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;

// issue 61 2020
// issue 31 2020
public class ProcessTrackingDetailsDTO implements Serializable
	{
	public static ProcessTrackingDetailsDTO instance(String jobID,  String startDate, String completedDate, String comments,  String taskID, String assignedTo) 
	    {
	    return new ProcessTrackingDetailsDTO(jobID, startDate, completedDate, comments, taskID, assignedTo);
		}
	
	public static ProcessTrackingDetailsDTO instance(ProcessTrackingDetails processTrackingDetails)
		{
		SimpleDateFormat format1 = new SimpleDateFormat("mm/dd/yyyy");
		//String formattedDateStarted = format1.format(processTrackingDetails.convertToDateString(processTrackingDetails.getDateStarted()));
		String dateStarted = processTrackingDetails.convertToDateString(processTrackingDetails.getDateStarted());
		//String formattedDateCompleted = format1.format(processTrackingDetails.convertToDateString(processTrackingDetails.getDateCompleted()));
		String dateCompleted = processTrackingDetails.convertToDateString(processTrackingDetails.getDateCompleted());		
		return new ProcessTrackingDetailsDTO(processTrackingDetails.getJobid(), dateStarted, dateCompleted, processTrackingDetails.getComments(),processTrackingDetails.getProcessTracking().getProcessTrackingId(),  processTrackingDetails.getAssignedTo().getId());
		}	
	// issue 61 2020
	private String finalVolumeUnits ; // issue 196
	private String mixtureId;
	private String createDate;
	private List<String> aliquotList;
	private List<String> aliquotNoAssayMultipleChoiceList;
	private List<String> aliquotNoAssayMultipleChoiceListDry; // issue 196
	private List<String> mixtureList;
	private List<String> aliquotVolumeList;
	private List<String> aliquotConcentrationList;
	private List<String> mixtureVolumeList;
	private List<String> mixtureConcentrationList;
	private String createdBy ;
	private String aliquotsChoice; // issue 94
	private String mixturesChoice; // issue 94
    private String volumeSolventToAdd; // issue 94
    private String desiredFinalVolume;
    private String mixtureName;  // issue 118
    private String volumeAliquotUnits; //issue 196
    private List <String> aliquotVolumeUnitList; // issue 196
    private List<String> mixtureVolumeUnitList;  // issue 196
    private List<Character> dryRetiredList; // issue 199
    private String jobID;
    private String dateStarted;
    private String dateCompleted;
    private String dateOnHold;
    private String trackingId;
    private String assignedTo;
    private String comments;
    private String taskDesc;
    private String expID;
    private String wfID;
    private String status;
    private String assayID;
    private String daysExpected;
    private Integer detailOrder;
    // issue 283
    private String dateInProgress;
    
    // issue 123
    Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>(); 
    Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
    // issue 116
	private ProcessTrackingDetailsDTO(String jobID, String dateStarted,   String dateCompleted, String comments,  String trackingId, String assignedTo)
		{
		this.jobID = jobID;		
		this.dateStarted = dateStarted;
		this.dateCompleted = dateCompleted;
		this.trackingId = trackingId;
		this.assignedTo = assignedTo;	
		this.comments = comments;
		} 
	
	public String getTaskDesc()
		{
		return taskDesc;
		}

//issue 196
	public void setTaskDesc(String taskDesc)
		{
		this.taskDesc= taskDesc;
		}

	public String getWfID()
		{
		return wfID;
		}

//issue 196
	public void setWfID(String wfID)
		{
		this.wfID= wfID;
		}
	
	
	public ProcessTrackingDetailsDTO() { }	
	
	public String getJobID()
		{
		return jobID;
		}

// issue 196
	public void setJobID(String jobID)
		{
		this.jobID= jobID;
		}
	
	public String getDateStarted()
		{
		return dateStarted;
		}

//issue 196
	public void setDateStarted(String dateStarted)
		{
		this.dateStarted= dateStarted;
		}
	
	public String getDateCompleted()
		{
		return dateCompleted;
		}
	
//issue 196
	public void setDateCompleted(String dateCompleted)
		{
		this.dateCompleted= dateCompleted;
		}	
		
	public String getDateOnHold()
		{
		return dateOnHold;
		}

//issue 196
	public void setDateOnHold(String dateOnHold)
		{
		this.dateOnHold= dateOnHold;
		}
	
// issue 283	
public String getDateInProgress()
	{
	return dateInProgress;
	}

//issue 196
public void setDateInProgress(String dateInProgress)
	{
	this.dateInProgress= dateInProgress;
	}



	public String getTrackingId()
		{
		return trackingId;
		}

//issue 196
	public void setTrackingId(String trackingId)
		{
		this.trackingId= trackingId;
		}
	
	public String getAssignedTo()
		{
		return assignedTo;
		}

//issue 196
	public void setAssignedTo(String assignedTo)
		{
		this.assignedTo= assignedTo;
		}
	
	public String getComments()
		{
		return comments;
		}

//issue 196
	public void setComments(String comments)
		{
		this.comments= comments;
		}	
	
	public String getExpID()
		{
		return expID;
		}

//issue 196
	public void setExpID(String expID)
		{
		this.expID= expID;
		}
	
	public String getStatus()
	{
	return status;
	}

//issue 196
	public void setStatus(String status)
		{
		this.status= status;
		}
	
	public String getAssayID()
	{
	return assayID;
	}

//issue 196
	public void setAssayID(String assayID)
		{
		this.assayID= assayID;
		}
	
	public String getDaysExpected()
		{
		return daysExpected;
		}

//issue 196
	public void setDaysExpected(String daysExpected)
		{
		this.daysExpected= daysExpected;
		}
	
	public Integer getDetailOrder()
		{
		return detailOrder;
		}

//issue 196
	public void setDetailOrder(Integer detailOrder)
		{
		this.detailOrder= detailOrder;
		}
	
	
	}


////////////////////////////////////////////////////
// ProcessDefaultDTO.java

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
import edu.umich.brcf.shared.layers.domain.DefaultTrackingTasks;
import edu.umich.brcf.shared.layers.domain.Mixture;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Workflow;

// issue 61 2020
// issue 31 2020
public class ProcessDefaultDTO implements Serializable
	{
	public static ProcessDefaultDTO instance(   String defaultTaskId, String taskID,   String assignedTo,   String wfId,   Integer taskOrder ) 
	    {
	    return new ProcessDefaultDTO(defaultTaskId, taskID, assignedTo, wfId, taskOrder);
		}
	
	public static ProcessDefaultDTO instance(DefaultTrackingTasks defaultTrackingTasks)
		{
		
		// Output "Wed Sep 26 14:23:28 EST 2012"		
		return new ProcessDefaultDTO(defaultTrackingTasks.getDefaultTaskId(), defaultTrackingTasks.getProcessTracking().getProcessTrackingId(), defaultTrackingTasks.getAssignedTo().getId(), defaultTrackingTasks.getWorkflow().getWfID(),defaultTrackingTasks.getTaskOrder());
		}	
	// issue 61 2020
	

    private String assignedTo;
    private String taskID;
    private String defaultTaskId;
    private String wfId;
    private Integer taskOrder;
    
    // issue 123
    Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>(); 
    Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
    // issue 116
	private ProcessDefaultDTO(  String defaultTaskId, String taskID,   String assignedTo,  String wfId,   Integer taskOrder)
		{
	    this.defaultTaskId = defaultTaskId;
	    this.taskID = taskID;
	    this.assignedTo = assignedTo;
	    this.wfId = wfId;
	    this.taskOrder = taskOrder;		
		} 
	
	public ProcessDefaultDTO() { }	
	
	

	public String getTaskID ()
		{
		return taskID;
		}

//issue 196
	public void setTaskID(String taskID)
		{
		this.taskID= taskID;
		}
	
	public String getDefaultTaskId ()
		{
		return defaultTaskId;
		}

//issue 196
	public void setDefaultTaskId(String defaultTaskId)
		{
		this.defaultTaskId= defaultTaskId;
		}
	
// issue 210
	public String getAssignedTo ()
		{
		return assignedTo;
		}

//issue 196
	public void setAssignedTo(String assignedTo)
		{
		this.assignedTo= assignedTo;
		}
	
	// issue 210
	public String getWfId ()
		{
		return wfId;
		}

	//issue 210
	public void setWfId(String wfId)
		{
		this.wfId= wfId;
		}
	// issue 210
	public Integer getTaskOrder()
		{
		return taskOrder;
		}

	//issue 210
	public void setTaskOrder(Integer taskOrder)
		{
		this.taskOrder= taskOrder;
		}
	
	}

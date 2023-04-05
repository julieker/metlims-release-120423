
////////////////////////////////////////////////////
// ProcessTrackingDTO.java

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
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;

// issue 61 2020
// issue 31 2020
public class ProcessTrackingDTO implements Serializable
	{
	public static ProcessTrackingDTO instance(String taskDesc,  String taskID) 
	    {
	    return new ProcessTrackingDTO(taskDesc, taskID);
		}
	
	public static ProcessTrackingDTO instance(ProcessTracking processTracking)
		{
		
		// Output "Wed Sep 26 14:23:28 EST 2012"		
		return new ProcessTrackingDTO(processTracking.getTaskDesc(), processTracking.getProcessTrackingId());
		}	
	// issue 61 2020
	
	
    private List <String> aliquotVolumeUnitList; // issue 196
    private List<String> mixtureVolumeUnitList;  // issue 196
    private List<Character> dryRetiredList; // issue 199
    private String jobID;
    private String dateStarted;
    private String dateCompleted;
    private String dateAssigned;
    private String trackingId;
    private String assignedTo;
    private String comments;
    private String taskDescNew;
    private String taskID;
    
    // issue 123
    Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>(); 
    Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
    // issue 116
	private ProcessTrackingDTO( String processTaskDesc, String processTaskId)
		{
		this.taskDescNew = processTaskDesc;
		this.taskID = processTaskId;
		this.dateStarted = dateStarted;
		
		} 
	
	public String getTaskDesc()
		{
		return taskDescNew;
		}

//issue 196
	public void setTaskDesc(String taskDescNew)
		{
		this.taskDescNew= taskDescNew;
		}

	
	public ProcessTrackingDTO() { }	
	
	

	public String getTaskID ()
		{
		return taskID;
		}

//issue 196
	public void setTaskID(String taskID)
		{
		this.taskID= taskID;
		}
	

	
	}

package edu.umich.brcf.shared.layers.domain;
/***************************
 *Created by Julie Keros issue 94
 * 
 * 
 ********************/

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.layers.dto.ProcessTrackingDTO;
//import edu.umich.brcf.shared.layers.dto.ProcessTrackingDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;

@Entity()
@Table(name = "TRACKING_TASKS")

// issue 61
public class ProcessTracking implements Serializable 
	{
	public static String ProcessTracking_DATE_FORMAT = "MM/dd/yy";

	public static ProcessTracking instance(String taskId,  String taskDesc  ) 
		{
		return new ProcessTracking(taskId, taskDesc);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "ProcessTracking"), @Parameter(name = "width", value = "10") })
	@Column(name = "TASK_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String processTrackingId;
	
	@Basic()
	@Column(name = "TASK_DESC", nullable = true)
	private String taskDesc;
	
	@Basic()
	@Column(name = "DAYS_REQUIRED", nullable = true)
	private Integer daysRequired;
	

	
	public ProcessTracking() {  }
	
	private ProcessTracking(String processTrackingId ,  String taskDesc )
		{
		this.processTrackingId = processTrackingId;
		this.taskDesc = taskDesc;
		
	 //issue 196
		}
	
    public void update(ProcessTrackingDTO processTrackingDto)
		{		
		this.taskDesc = processTrackingDto.getTaskDesc();
		} 

	public String getProcessTrackingId()
		{
		return processTrackingId;
		}
	
	public void setProcessTrackingId(String processTrackingId)
		{
		this.processTrackingId = processTrackingId;
		}
	
	public String getTaskDesc()
		{
		return taskDesc;
		}

	public void setTaskDesc(String taskDesc)
		{
		this.taskDesc = taskDesc;
		}
   
	public Integer getDaysRequired()
		{
		return daysRequired;
		}

	public void setDaysRequired (Integer daysRequired)
		{
		this.daysRequired = daysRequired;
		}
	
	}
	
	

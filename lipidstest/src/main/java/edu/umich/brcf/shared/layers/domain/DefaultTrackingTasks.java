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

//import edu.umich.brcf.shared.layers.dto.DefaultTrackingTasksDTO;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;
//import edu.umich.brcf.shared.layers.dto.DefaultTrackingTasksDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;

@Entity()
@Table(name = "DEFAULT_TRACKING_TASKS")

// issue 61
public class DefaultTrackingTasks implements Serializable 
	{
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSIGNED_TO", nullable = true, columnDefinition = "CHAR(6)")
	private User assignedTo;
	
	public static DefaultTrackingTasks instance( String defaultTaskId, ProcessTracking processTracking,   User assignedTo,   Workflow workflow,   Integer taskOrder  ) 
		{
		//System.out.println("here is days expected:" + daysExpected);
		return new DefaultTrackingTasks(defaultTaskId,  processTracking, assignedTo,  workflow,  taskOrder);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "DefaultTracking"), @Parameter(name = "width", value = "10") })
	@Column(name = "DEFAULT_TASK_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String defaultTaskId;
	
	
	
	@Basic()
	@Column(name = "TASK_ORDER", nullable = true)
	private Integer taskOrder;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TASK_ID", nullable = false, columnDefinition = "VARCHAR2(10)")
	private ProcessTracking processTracking;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WF_ID", nullable = false, columnDefinition = "VARCHAR2(7)")
	private Workflow workflow;
	
	
	public DefaultTrackingTasks() {  }
	
	private DefaultTrackingTasks(String defaultTaskId, ProcessTracking processTracking,  User assignedTo, Workflow workflow, Integer taskOrder)
		{
		this.processTracking = processTracking;
		this.assignedTo = assignedTo;
		this.workflow = workflow;
		this.taskOrder = taskOrder;
	 //issue 196
		}
	
	/* public void update(DefaultTrackingTasksDTO processTrackingDetailsDto, User assignedToUser, Calendar dateCompleted)
		{		
		this.comments = processTrackingDetailsDto.getComments();
		this.dateCompleted = (StringUtils.isEmptyOrNull(processTrackingDetailsDto.getDateCompleted()) ? null : CalendarUtils.calendarFromString(processTrackingDetailsDto.getDateCompleted(),ProcessTracking.ProcessTracking_DATE_FORMAT) );
		this.dateStarted = (StringUtils.isEmptyOrNull(processTrackingDetailsDto.getDateStarted()) ? null : CalendarUtils.calendarFromString(processTrackingDetailsDto.getDateStarted(),ProcessTracking.ProcessTracking_DATE_FORMAT) );
		this.assignedTo = assignedToUser;
		this.dateAssigned = (StringUtils.isEmptyOrNull(processTrackingDetailsDto.getDateAssigned()) ? null : CalendarUtils.calendarFromString(processTrackingDetailsDto.getDateAssigned(),ProcessTracking.ProcessTracking_DATE_FORMAT) );	
		this.status = processTrackingDetailsDto.getStatus();
		this.daysExpected = processTrackingDetailsDto.getDaysExpected();
		this.detailOrder = processTrackingDetailsDto.getDetailOrder();
		this.dateOnHold = (StringUtils.isEmptyOrNull(processTrackingDetailsDto.getDateOnHold()) ? null : CalendarUtils.calendarFromString(processTrackingDetailsDto.getDateOnHold(),ProcessTracking.ProcessTracking_DATE_FORMAT) );
		} */

	
	public String getDefaultTaskId()
		{
		return defaultTaskId;
		}
	
	public void setDefaultTaskId(String defaultTaskId)
		{
		this.defaultTaskId = defaultTaskId;
		}
	
	public Integer getTaskOrder()
		{
		return taskOrder;
		}

	public void setTaskOrder(Integer taskOrder)
		{
		this.taskOrder = taskOrder;
		}
	
	public ProcessTracking getProcessTracking()
		{
		return processTracking;
		}
	
	public void setProcessTracking(ProcessTracking processTracking)
		{
		this.processTracking = processTracking;
		}
	
	
// issue 210
	
	public User getAssignedTo()
		{
		return this.assignedTo;
		}
	

	public void setAssignedTo(User assignedTo)
		{
		this.assignedTo = assignedTo;
		}
	

	
	public String convertToDateString(Calendar dateToConvert)
		{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return (dateToConvert == null) ? "" : sdf.format(dateToConvert.getTime());
		}
	
	public Workflow getWorkflow()
		{
		return this.workflow;
		}

	public void setWorkflow(Workflow workflow)
		{
		this.workflow = workflow;
		}
	
	
	}
	
	

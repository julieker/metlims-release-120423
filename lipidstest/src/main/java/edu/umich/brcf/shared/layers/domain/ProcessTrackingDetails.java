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

import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;
//import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;

@Entity()
@Table(name = "TRACKING_TASKS_DETAILS")

// issue 61
public class ProcessTrackingDetails implements Serializable 
	{
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSIGNED_TO", nullable = true, columnDefinition = "CHAR(6)")
	private User assignedTo;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXP_ID", nullable = true, columnDefinition = "CHAR(6)")
	private Experiment experiment;
	
	
	public static ProcessTrackingDetails instance( String jobid, ProcessTracking processTracking, Calendar dateStarted, Calendar dateCompleted, String comments,  User assignedTo, Calendar dateAssigned, Experiment experiment, Workflow workflow, String status, Assay assay, String daysExpected, Integer detailOrder , Calendar dateOnHold) 
		{
		//System.out.println("here is days expected:" + daysExpected);
		return new ProcessTrackingDetails(jobid,  processTracking,  dateStarted,  dateCompleted,  comments,  assignedTo, dateAssigned, experiment, workflow, status, assay, daysExpected, detailOrder, dateOnHold);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "ProcessTrackDetails"), @Parameter(name = "width", value = "10") })
	@Column(name = "JOB_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String jobid;
	
	
	
	@Basic()
	@Column(name = "DETAIL_ORDER", nullable = true)
	private Integer detailOrder;
	
	
	
	@Basic()
	@Column(name = "DATE_STARTED", nullable = true)
	private Calendar dateStarted;
	
	@Basic()
	@Column(name = "DATE_COMPLETED", nullable = true)
	private Calendar dateCompleted ;
	
	@Basic()
	@Column(name = "STATUS", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String status;
	
	
	@Basic()
	@Column(name = "DATE_ONHOLD", nullable = true)
	private Calendar dateOnHold ;
	
	
	
	@Basic()
	@Column(name = "DAYS_EXPECTED", nullable = true, columnDefinition = "VARCHAR2(5)")
	private String daysExpected;
	
	@Basic()
	@Column(name = "DATE_ASSIGNED", nullable = true)
	private Calendar dateAssigned ;
	@Basic()
	@Column(name = "COMMENTS", nullable = true)
	private String comments;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TASK_ID", nullable = false, columnDefinition = "VARCHAR2(10)")
	private ProcessTracking processTracking;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WF_ID", nullable = false, columnDefinition = "VARCHAR2(7)")
	private Workflow workflow;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSAY_ID", nullable = false, columnDefinition = "char(4)")
	private Assay assay;
	
	
	public ProcessTrackingDetails() {  }
	
	private ProcessTrackingDetails(String jobid, ProcessTracking processTracking, Calendar dateStarted, Calendar dateCompleted, String comments,  User assignedTo, Calendar dateAssigned, Experiment experiment, Workflow workflow, String status, Assay assay, String daysExpected, Integer detailOrder, Calendar dateOnHold)
		{
		this.jobid = jobid;
		this.processTracking = processTracking;
		this.dateStarted = dateStarted;
		this.dateCompleted = dateCompleted;
		this.comments = comments;
		this.assignedTo = assignedTo;
		this.dateAssigned = dateAssigned;	
		this.experiment = experiment;
		this.workflow = workflow;
		this.status = status;
		this.assay = assay;
		this.daysExpected = daysExpected;
		this.detailOrder = detailOrder;
		this.dateOnHold = dateOnHold;
		
	 //issue 196
		}
	
	public void update(ProcessTrackingDetailsDTO processTrackingDetailsDto, User assignedToUser, Calendar dateCompleted)
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
		} 

	public void updateDaysExpected(int newDaysExpected)
		{	
		if (this.dateStarted != null)
			dateStarted.add(Calendar.DAY_OF_MONTH, newDaysExpected);
		
		} 
	
	public void updateStartDate(Calendar dateCompleted, int amtToAdd)
		{	
		Calendar dateC = dateCompleted;
		dateC.add(Calendar.DAY_OF_MONTH, amtToAdd+ 1);
		dateStarted = dateC;
		} 
	
	
	public void updateStatus()
		{	
		status = "In progress";
		} 
	
	public String getJobid()
		{
		return jobid;
		}
	
	public void setJobId(String jobid)
		{
		this.jobid = jobid;
		}

	public ProcessTracking getProcessTracking()
		{
		return processTracking;
		}
	
	public void setProcessTracking(ProcessTracking processTracking)
		{
		this.processTracking = processTracking;
		}
	
	public Calendar getDateStarted()
		{
		return dateStarted;
		}

	public void setDateStarted(Calendar dateStarted)
		{
		this.dateStarted = dateStarted;
		}
	
	////////////////////////////
	public Calendar getDateOnHold()
		{
		return dateOnHold;
		}

	public void setDateOnHold(Calendar dateOnHold)
		{
		this.dateOnHold = dateOnHold;
		}
	///////////////////////////
	
	public Calendar getDateAssigned()
		{
		return dateAssigned;
		}

	public void setDateAssigned(Calendar dateAssigned)
		{
		this.dateAssigned = dateAssigned;
		}
	
	public Calendar getDateCompleted()
		{
		return dateCompleted;
		}

	public void setDateCompleted(Calendar dateCompleted)
		{
		this.dateCompleted = dateCompleted;
		}
	
	public String getComments()
		{
		return comments;
		}

	public void setComments(String comments)
		{
		this.comments = comments;
		}
	
	public String getStatus()
		{
		return status;
		}

	public void setStatus(String status)
		{
		this.status = status;
		}
	
	public String getDaysExpected()
		{
		return daysExpected;
		}

	public void setDaysExpected(String daysExpected)
		{
		this.daysExpected = daysExpected;
		}
	
	public User getAssignedTo()
		{
		return assignedTo;
		}

	public void setAssignedTo(User assignedTo)
		{
		this.assignedTo = assignedTo;
		}
	
	
	public Experiment getExperiment()
		{
		return experiment;
		}

	public void setAssignedTo(Experiment experiment)
		{
		this.experiment = experiment;
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
	
	public Assay getAssay()
		{
		return this.assay;
		}

	public void setAssay(Assay assay)
		{
		this.assay = assay;
		}
	
	
	public Integer getDetailOrder()
		{
		return this.detailOrder;
		}

	public void setDetailOrder(Integer detailOrder)
		{
		this.detailOrder = detailOrder;
		}
	
	}
	
	

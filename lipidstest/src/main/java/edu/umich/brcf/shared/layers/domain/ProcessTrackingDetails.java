package edu.umich.brcf.shared.layers.domain;
/***************************
 *Created by Julie Keros issue 94
 * 
 * 
 ********************/

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
import edu.umich.brcf.shared.util.comparator.ProgressTrackDetailsComparator;
import edu.umich.brcf.shared.util.utilpackages.CalendarUtils;
//import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;
import javax.persistence.Transient;

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
	
	
	public static ProcessTrackingDetails instance( String jobid, ProcessTracking processTracking, Calendar dateStarted, Calendar dateCompleted, String comments,  User assignedTo,  Experiment experiment, Workflow workflow, String status, Assay assay, String daysExpected, Integer detailOrder , Calendar dateOnHold) 
		{
		//System.out.println("here is days expected:" + daysExpected);
		return new ProcessTrackingDetails(jobid,  processTracking,  dateStarted,  dateCompleted,  comments,  assignedTo, experiment, workflow, status, assay, daysExpected, detailOrder, dateOnHold);
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
	
	
	  @Transient 
	  private String expID;
	  @Transient 
	  private String assayID;
	
	
	
	
	public ProcessTrackingDetails() {  }
	
	public ProcessTrackingDetails(String jobid, ProcessTracking processTracking, Calendar dateStarted, Calendar dateCompleted, String comments,  User assignedTo,  Experiment experiment, Workflow workflow, String status, Assay assay, String daysExpected, Integer detailOrder, Calendar dateOnHold)
		{
		this.jobid = jobid;
		this.processTracking = processTracking;
		this.dateStarted = dateStarted;
		this.dateCompleted = dateCompleted;
		this.comments = comments;
		this.assignedTo = assignedTo;	
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
	
	
	///////////////////////// issue 290
	
	@Transient
	public String getExpID()
		{
		return expID;
		}
	
	@Transient
	public void setExpID(String expID)
		{
		this.expID = expID;
		}
	
	@Transient
	public String getAssayID()
		{
		return assayID;
		}
	
	@Transient
	public void setAssayID(String assayID)
		{
		this.assayID = assayID;
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
	
	
	////////////////////////
	
	/*   public List<ProcessTrackingDetails> getProcessTrackingDetailsList()
			{
	    //	if (collapse)    		
	    //	   return new ArrayList <ProcessTrackingDetails> ();
	    	
			
			List<ProcessTrackingDetails> nList = processTrackingDetailsList;
			List<ProcessTrackingDetails> nList2 = new ArrayList<ProcessTrackingDetails> ();
			List<ProcessTrackingDetails> nList3 = new ArrayList<ProcessTrackingDetails> ();
			List<ProcessTrackingDetails> nList4 = new ArrayList<ProcessTrackingDetails> ();
			
			//nList2.addAll(nList);
			if (!StringUtils.isNullOrEmpty(assignedTo))
				{
				
				for (ProcessTrackingDetails pd : nList )
					{
					if (!StringUtils.isNullOrEmpty(assignedTo))	
						{
					    if (pd.getAssignedTo().getFullNameByLast().equals(assignedTo))
					    	nList2.add(pd);
						}
					}
				}
			if (nList2.size() == 0 && StringUtils.isNullOrEmpty(assignedTo))
				{
				nList2.addAll(nList);
				}
			nList3.addAll(nList2);
			for (ProcessTrackingDetails pd : nList3 )
				{
				if (onHold)	
						{
					    if (pd.getStatus() != null && pd.getStatus().equals("On hold"))				    	
					    	nList4.add(pd);
						}
					
				if (inProgress)	
						{
					    if (pd.getStatus() != null && pd.getStatus().equals("In progress"))
					    	nList4.add(pd);
						}
				
				if (completed)	
					{
				    if (pd.getStatus() != null && pd.getStatus().equals("Completed"))
				    	nList4.add(pd);
					}
				
				if (inQueue)	
					{
				    if (pd.getStatus() != null && pd.getStatus().equals("In queue"))
				    	nList4.add(pd);
					}
				}   
						
			nList = new ArrayList<ProcessTrackingDetails> ();
			nList.addAll(nList4);	
			return nList;
			
			}
	    @Transient   
	    public List <ProcessTrackingDetails> getTrackingListForExpAssay ()
			{
	            	  
			List <ProcessTrackingDetails> ptListCriteria  = new ArrayList <ProcessTrackingDetails> ();
			List <ProcessTrackingDetails> ptListExpAssayWk  = new ArrayList <ProcessTrackingDetails> ();
			Collections.sort(getProcessTrackingDetailsList(), new ProgressTrackDetailsComparator());
			for (ProcessTrackingDetails pt : getProcessTrackingDetailsList())
				{
				
				// issue 283
				if (StringUtils.isEmptyOrNull(getExperiment().getExpID()) && StringUtils.isEmptyOrNull(getAssay().getAssayId()))   
					ptListCriteria.add(pt);	
				else if (!StringUtils.isEmptyOrNull(getExpID()) && pt.getExperiment().getExpID().equals(getExpID() ) && pt.getAssay().getAssayId().equals(getAssayID() ))
					ptListCriteria.add(pt);
				else if ( !StringUtils.isEmptyOrNull(getExpID()) && pt.getExperiment().getExpID().equals(getExpID() ) && StringUtils.isEmptyOrNull(getAssayID()))
					ptListCriteria.add(pt);
				// issue 287
				else if ( StringUtils.isEmptyOrNull(getExpID()) &&  !StringUtils.isEmptyOrNull(getAssayID()) &&   pt.getAssay().getAssayId().equals(getAssayID()))
					ptListCriteria.add(pt);
				}   
			Collections.sort(ptListCriteria, new ProgressTrackDetailsComparator());
			return ptListCriteria;
			} */
	
	/////////////////////////
	
	}
	
	

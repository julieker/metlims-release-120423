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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.shared.util.comparator.ProgressTrackDetailsComparator;
import edu.umich.brcf.shared.util.comparator.ProgressTrackDetailsExpComparator;
//import edu.umich.brcf.shared.layers.dto.WorkflowDTO;
//import edu.umich.brcf.shared.layers.dto.WorkflowDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

import javax.persistence.Table;
import javax.persistence.Transient;

@Entity()
@Table(name = "WORKFLOW")

// issue 61
public class Workflow implements Serializable 
	{
	public static String Workflow_DATE_FORMAT = "MM/dd/yy";

	public static Workflow instance(String wfID,  String wfDesc  ) 
		{
		return new Workflow(wfID, wfDesc);
		}

	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
	@Parameter(name = "idClass", value = "Workflow"), @Parameter(name = "width", value = "10") })
	@Column(name = "WF_ID", nullable = false, unique = true, length = 10, columnDefinition = "CHAR(10)")
	private String wfID;
	
	@Basic()
	@Column(name = "WF_DESC", nullable = true)
	private String wfDesc;
	@Transient
	private boolean collapse ;
	
	
	@OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ProcessTrackingDetails> processTrackingDetailsList;

  @Transient 	
  private boolean onHold = true;
  @Transient
  private boolean inProgress = true;
  @Transient
  private boolean completed = false;
  @Transient
  private boolean inQueue = false;
  @Transient 
  private String assignedTo;
  @Transient 
  private String expID;
  @Transient 
  private String assayID;
  @Transient
  private List <ProcessTrackingDetails> trackingListForExpAssay;
	
	public Workflow() {  }
	
	private Workflow(String wfID ,  String wfDesc )
		{
		this.wfID = wfID;
		this.wfDesc = wfDesc ;
		
		
	 //issue 196
		}
	
   /* public void update(WorkflowDTO WorkflowDto)
		{		
		this.taskDesc = WorkflowDto.getTaskDesc();
		} */

	public String getWfID()
		{
		return wfID;
		}
	
	public void setWfID(String wfID)
		{
		this.wfID = wfID;
		}
	
	@Transient
	public String getAssignedTo()
		{
		return wfID;
		}
	
	@Transient
	public void setAssignedTo(String assignedTo)
		{
		this.assignedTo = assignedTo;
		}
	
	
	public String getWfDesc()
		{
		return wfDesc;
		}

	public void setWfDesc(String wfDesc)
		{
		this.wfDesc = wfDesc;
		}
	
	@Transient
	public boolean getCollapse()
		{
		return collapse;
		}
	@Transient
	public void setCollapse(boolean collapse)
		{
		this.collapse = collapse;
		}
	
	@Transient
	public boolean getOnHold()
		{
		return onHold;
		}
	@Transient
	public void setOnHold(boolean onHold)
		{
		this.onHold = onHold;
		}
	
	@Transient
	public boolean getInProgress()
		{
		return inProgress;
		}
	@Transient
	public void setInProgress (boolean inProgress)
		{
		this.inProgress = inProgress;
		}
	
	
	@Transient
	public boolean getCompleted()
		{
		return completed ;
		}
	@Transient
	public void setCompleted (boolean completed)
		{
		this.completed  = completed ;
		}
	
	
	@Transient
	public boolean getInQueue()
		{
		return inQueue ;
		}
	@Transient
	public void setInQueue (boolean inQueue)
		{
		this.inQueue  = inQueue ;
		}
	
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
	
	
    public List<ProcessTrackingDetails> getProcessTrackingDetailsList()
		{
    	if (collapse)    		
    	   return new ArrayList <ProcessTrackingDetails> ();
    	
		
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
			if (StringUtils.isEmptyOrNull(getExpID()) && StringUtils.isEmptyOrNull(getAssayID()))   
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
		}
	}
	
	

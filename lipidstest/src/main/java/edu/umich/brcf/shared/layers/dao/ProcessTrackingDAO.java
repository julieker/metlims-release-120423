/**********************
 * Updated by Julie Keros Sept 2000
 * Added aliquotIdsForExpId ,
 * getMatchingAliquotIds,loadByCid and other routines for Aliquots.
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.DefaultTrackingTasks;
import edu.umich.brcf.shared.layers.domain.Experiment;

// issue 61

@Repository
public class ProcessTrackingDAO extends BaseDAO 
	{	

	// issue 210
	public void createProcessTracking(ProcessTrackingDetails pd)
		{
		getEntityManager().persist(pd);
		}
	
	public void createDefaultTrackingTasks (DefaultTrackingTasks dt)
		{
		getEntityManager().persist(dt);
		}
	
	public void createTask(ProcessTracking pt)
		{
		getEntityManager().persist(pt);
		}
	
	public String grabTaskIdFromDesc(String taskDesc)
		{
		Query query = getEntityManager().createNativeQuery("select distinct cast(t.TASK_id as VARCHAR2(9)) "
				+ " from tracking_tasks t where task_desc = ?1 order by 1").setParameter(1, taskDesc);
		
		return  query.getResultList().get(0).toString();
		
		}
	
	public String grabMinDateStarted(String wfID)
		{
		Query query = getEntityManager().createNativeQuery("select to_char(min(date_started), 'mm/dd/yyyy') "
				+ " from tracking_tasks_details t where wf_id = ?1 order by 1").setParameter(1, wfID);
		
		return  query.getResultList().get(0).toString();
		
		}
	
	public String grabSampleType(String wfID, String expID)
		{
		Query query = getEntityManager().createNativeQuery("select distinct description from sample_type t1, sample t2, tracking_tasks_details t3 "
				+ " where t1.sample_type_id = t2.sample_type_id and t2.exp_id = t3.exp_id and t3.wf_id =  ?1 and t3.exp_id = ?2").setParameter(1, wfID).setParameter(2, expID);		
		return  query.getResultList().size() == 0 ? " " :query.getResultList().get(0).toString();		
		}
	
	public String grabSampleType(String expID)
	{
	Query query = getEntityManager().createNativeQuery("select distinct description from sample_type t1, sample t2, tracking_tasks_details t3 "
			+ " where t1.sample_type_id = t2.sample_type_id and t2.exp_id = t3.exp_id  and t3.exp_id = ?2").setParameter(2, expID);		
	return  query.getResultList().size() == 0 ? " " :query.getResultList().get(0).toString();		
	}
	
	
	// issue 290
	
	// issue 290
	public Map <String, String> grabAllSampleTypes()
		{
		Map <String, String> sampleTypeMap = new HashMap <String, String> ();
		Query query = getEntityManager().createNativeQuery("select distinct t3.exp_id, description from sample_type t1, sample t2, tracking_tasks_details t3 "
				+ " where t1.sample_type_id = t2.sample_type_id and t2.exp_id = t3.exp_id ");		
		List <Object[]> sampleTypeList = query.getResultList();
		for (Object [] obj :sampleTypeList )
			{
			sampleTypeMap.put((String) obj[0], (String) obj[1]);
			}
		
		return  sampleTypeMap;		
		}
	
	// issue 262
	public Map <String, String>  createSampleTypeStringMapFromList( )
		{
		Map <String, String> sampleTypeMap =  new HashMap<String, String>();
		String sampleTypeString = "";
		Query query = getEntityManager().createNativeQuery("select distinct cast(description as VARCHAR2(150)), exp_id from sample_type t1, sample t2 "
				+ " where t1.sample_type_id = t2.sample_type_id and exp_id in (select exp_id from tracking_tasks_details) ");		
		
		List <Object []> sampleTypeObjs = query.getResultList();
	    
		Query queryExp = getEntityManager().createNativeQuery("select distinct exp_id from tracking_tasks_details");
		
		for (Object sExp : queryExp.getResultList())
		    {
			sampleTypeString = "";
			for (Object [] obj : sampleTypeObjs)
				{
				if (!obj[1].toString().equals(sExp.toString()))
					continue;
				sampleTypeString = sampleTypeString + obj[0].toString() +  ", ";
				}
			if (!StringUtils.isNullOrEmpty(sampleTypeString))	
			    {
				sampleTypeString = sampleTypeString.trim();
	    		sampleTypeString = sampleTypeString.substring(0,sampleTypeString.length()-1);
	    		sampleTypeMap.put(sExp.toString(), sampleTypeString);
			    }
			}
	     return sampleTypeMap;
		 }
	
	public List <Object[]> grabSampleTypeStringFromList( String expID,String wfID)
		{
		String sampleTypeString = "";
		Query query = getEntityManager().createNativeQuery("select distinct cast(description as VARCHAR2(150)), t2.sample_type_id from sample_type t1, sample t2, tracking_tasks_details t3 "
				+ " where t1.sample_type_id = t2.sample_type_id and t2.exp_id = t3.exp_id and t3.wf_id =  ?1 and t3.exp_id = ?2").setParameter(1, wfID).setParameter(2, expID);		
		
		List <Object []> sampleTypeObjs = query.getResultList();
	    
		if (query.getResultList().size() == 0) 
			return new ArrayList <Object[]> ();
		int i = 0;
		
		
	    return  sampleTypeObjs;
		}
	
	public String existsOnHold(String wfID)
		{
		Query query = getEntityManager().createNativeQuery("select status "
				+ " from tracking_tasks_details t where wf_id = ?1 and status = 'On hold' order by 1").setParameter(1, wfID);
		return  (query.getResultList().size() == 0 ? "" : query.getResultList().get(0).toString());
		
		}
	
	public String grabWfIDFromDesc(String taskDesc)
		{
		Query query = getEntityManager().createNativeQuery("select distinct cast(w.wf_id as VARCHAR2(7)) "
				+ " from workflow w where wf_desc = ?1 order by 1").setParameter(1, taskDesc);
		
		return  query.getResultList().get(0).toString();
		
		}
	
	// issue 61 2020 
	public ProcessTrackingDetails loadById(String jobid)
		{
		ProcessTrackingDetails ptd = getEntityManager().find(ProcessTrackingDetails.class, jobid);
		initializeTheKids(ptd, new String[] { "processTracking" });
		return ptd;
		}
	
	public Workflow loadByIdWF(String wfID)
		{
		Workflow wf = getEntityManager().find(Workflow.class, wfID);
		initializeTheKids(wf, new String[]{"processTrackingDetailsList"});
		return wf;
		}
	
	// issue 210
	public ProcessTracking loadByPTbyTaskID(String taskid)
		{
		ProcessTracking pt = getEntityManager().find(ProcessTracking.class, taskid);
		return pt;
		}
		
	// issue 210
	public List<String> allTaskDesc()
		{
		Query query = getEntityManager().createNativeQuery("select distinct cast(t.TASK_DESC as VARCHAR2(300)) "
					+ " from tracking_tasks t order by 1");
	
		return  query.getResultList();
		} 
	
	public List<ProcessTrackingDetails> loadByAssignedTo(String assignedTo)
		{
		List<ProcessTrackingDetails> ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails where assignedTo.id = ?1  order by 1")
				.setParameter(1, assignedTo).getResultList();	
	    for (ProcessTrackingDetails ptd : ptdList)
			{
			initializeTheKids(ptd, new String[] { "processTracking", "assignedTo"});
			}
		return ptdList;
		}
	
	////
	//// issue 287
	public List<String> loadAllWFsAssigned(String expID, String assayID)
		{
		List <String> ptdList = new ArrayList <String> ();
		if (StringUtils.isNullOrEmpty(expID) && StringUtils.isNullOrEmpty(assayID))
			ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id  ")
			.getResultList();
		else if (!StringUtils.isNullOrEmpty(expID) && StringUtils.isNullOrEmpty(assayID))
		    ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id and t2.exp_id = ?1 ").setParameter(1,expID)
			.getResultList();
		else if (!StringUtils.isNullOrEmpty(expID) && !StringUtils.isNullOrEmpty(assayID))
		    ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id and t2.exp_id = ?1 and t2.assay_id = ?2 ").setParameter(1,expID).setParameter(2,assayID)
			.getResultList();
		// issue 287
		else if (StringUtils.isNullOrEmpty(expID) && !StringUtils.isNullOrEmpty(assayID))
		    ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id and t2.assay_id = ?2 ").setParameter(2,assayID)
			.getResultList();
	
		return ptdList;
		}
	
	////
	////
	public List<ProcessTrackingDetails> loadAllTasksAssigned()
		{
		return loadAllTasksAssigned(null,null, false, null);
		}
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId , boolean allExpAssay, String assignedTo)
		{
		return loadAllTasksAssigned(expId, assayDescId, allExpAssay, assignedTo, false, false , false);
		}
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId , boolean allExpAssay, String assignedTo, boolean isCurrent, boolean isInProgress, boolean isOnHold)
		{
		return loadAllTasksAssigned(expId,assayDescId ,  allExpAssay,  assignedTo, isCurrent,isInProgress,  isOnHold,  false,  true,true);
		}
	
	// issue 290
	// issue 298
	public List<ProcessTrackingDetails>
	loadAllTasksBelowEditedExperiment(String expId, String assayDescId , int detailOrder,  String assignedTo)
		{
		String queryStr = ""; 
		List<ProcessTrackingDetails> ptdList = new ArrayList <ProcessTrackingDetails> ();
		if (StringUtils.isNullOrEmpty(assignedTo))
			ptdList = getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1 and pd.assay.assayId= ?2 and detailOrder >= ?3   order by detailOrder  ").setParameter(1,  expId).setParameter(2, assayDescId).setParameter(3, detailOrder).getResultList();
		else
			ptdList = getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1 and pd.assay.assayId= ?2 and detailOrder >= ?3  and pd.assignedTo.lastName || ', ' || pd.assignedTo.firstName= ?5  order by detailOrder  ").setParameter(1,  expId).setParameter(2, assayDescId).setParameter(3, detailOrder).setParameter(5, assignedTo).getResultList();
		return ptdList;   
		}
	
	// issue 292
	public String grabMaxCompletedDate (String expId, String assayDescId, int detailOrder)       
		{       
		// issue 298 
		List<String> complDateOrderList = new ArrayList <String> ();
		List<String> maxDateForSection  = new ArrayList <String> ();            
		
		complDateOrderList .addAll(getEntityManager().createNativeQuery(" select to_char(detail_order)  from tracking_tasks_details where date_completed is not null and exp_id = ?1 and assay_id = ?2 and detail_order < ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3 , detailOrder).getResultList());
	    if (complDateOrderList.size() == 0 )            
	    	return null;  
	    complDateOrderList = new ArrayList <String> ();    
	    maxDateForSection.addAll(getEntityManager().createNativeQuery(" select to_char(max(detail_order))  from tracking_tasks_details where date_completed is not null and exp_id = ?1 and assay_id = ?2 and detail_order < ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3 , detailOrder).getResultList());
	    int theDetailOrderForMaxDateSection = Integer.parseInt(maxDateForSection.get(0).toString());
	 
		maxDateForSection  = new ArrayList <String> (); 
		maxDateForSection.addAll(getEntityManager().createNativeQuery(" select to_char(date_completed, 'mm/dd/yyyy') from tracking_tasks_details where date_completed is not null and exp_id = ?1 and assay_id = ?2 and detail_order = ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3,theDetailOrderForMaxDateSection).getResultList());		 
		if (maxDateForSection.size() == 0 )
			return null;     
	    return maxDateForSection.get(0).toString();	               	     		
		}    
	
	// issue 292
	public String grabMaxOnHoldDate (String expId, String assayDescId, int detailOrder)             
		{     
		// issue 298
		List<String> onHoldDateOrderList = new ArrayList <String> ();
		List<String> maxOnHoldDateForSection = new ArrayList <String> ();
		onHoldDateOrderList .addAll(getEntityManager().createNativeQuery(" select to_char(detail_order)  from tracking_tasks_details where date_onhold is not null and date_completed is null and exp_id = ?1 and assay_id = ?2 and detail_order < ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3 , detailOrder).getResultList());
		
		if (onHoldDateOrderList.size() == 0 )                
		    	return null; 
		onHoldDateOrderList = new ArrayList <String> ();                      
		
		maxOnHoldDateForSection.addAll(getEntityManager().createNativeQuery(" select to_char(max(detail_order))  from tracking_tasks_details where date_onhold is not null and date_completed is null and exp_id = ?1 and assay_id = ?2 and detail_order < ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3 , detailOrder).getResultList());
	    int theDetailOrderForMaxDateSection = Integer.parseInt(maxOnHoldDateForSection.get(0).toString());
		 
		   // int theDetailOrderForMaxDateSection = Integer.parseInt(complDateOrderList.get(0).toString());
			maxOnHoldDateForSection  = new ArrayList <String> (); 
			maxOnHoldDateForSection.addAll(getEntityManager().createNativeQuery(" select to_char(date_onhold, 'mm/dd/yyyy') from tracking_tasks_details where date_onhold is not null and date_completed is null and exp_id = ?1 and assay_id = ?2 and detail_order = ?3").setParameter(1,  expId).setParameter(2 , assayDescId).setParameter(3,theDetailOrderForMaxDateSection).getResultList());		 
			if (maxOnHoldDateForSection.size() == 0 )
				return null;     
		return maxOnHoldDateForSection.get(0).toString();                    	     		
		}    
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId , boolean allExpAssay, String assignedTo, boolean isCurrent, boolean isInProgress, boolean isOnHold, boolean isComplete, boolean isInqueue, boolean isGantt)
		{
		List<ProcessTrackingDetails> ptdListWF = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdListWFInProgress = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdListWFOnHold = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdList = new ArrayList <ProcessTrackingDetails> ();       
		ProcessTrackingDetails pd;
		// issue 273
		if (allExpAssay && StringUtils.isNullOrEmpty(assayDescId))
			{
			ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd  order by   experiment.expID, assay.assayId, detailOrder  ")
			.getResultList();   
			}
		else if ((allExpAssay || StringUtils.isNullOrEmpty(expId)) && !StringUtils.isNullOrEmpty(assayDescId))
			ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd  where pd.assay.assayId= ?2  order by   experiment.expID, assay.assayId, detailOrder  ").setParameter(2, assayDescId)
			.getResultList();
		else if (StringUtils.isNullOrEmpty(expId) || StringUtils.isNullOrEmpty(assayDescId))
			//return ptdListWF;
			ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1  order by experiment.expID, assay.assayId, detailOrder  ").setParameter(1,  expId).getResultList();
		else 
		    ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1 and pd.assay.assayId= ?2 order by detailOrder  ").setParameter(1,  expId).setParameter(2, assayDescId)
				.getResultList();
		int i = 0;
		String prevExp = "";
		String prevAssay = "";
		boolean itBelongs = false;
		
		for (ProcessTrackingDetails ptd : ptdList)
			{
		
			itBelongs = false; 
			// issue 290               
			initializeTheKids(ptd, new String[] { "processTracking", "assignedTo", "experiment", "assay"});
			initializeTheKids(ptd.getExperiment(), new String[] { "project"});
			//////if ( ptd.getWorkflow().getWfDesc().equals(wfDescString))

			if (isCurrent && ptd.getStatus().equals("Completed"))
				continue;
			else if (isInProgress && ptd.getStatus().equals("In progress"))
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("In progress")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("In progress"))
						)
					//ptdListWF.add(ptd);
				    itBelongs = true;
			
				}
			// issue 290
			
			else if (isOnHold && ptd.getStatus().equals("On hold"))
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("On hold")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("On hold")))
						{
					    // ptdListWF.add(ptd);  
					    itBelongs = true;
						}
				}
		
			
			else if (isComplete && ptd.getStatus().equals("Completed"))
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("Completed")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("Completed")))
						{
					    // ptdListWF.add(ptd);  
						itBelongs = true;
						}
				}

			
			// issue 290
			else if (isInqueue && ptd.getStatus().equals("In queue"))
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("In queue")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("In queue")))
						{
					    //ptdListWF.add(ptd); 
					    itBelongs = true;     
						}
				}
		
		   if (!StringUtils.isNullOrEmpty(assignedTo) && !assignedTo.equals("All Users"))
				{
				if (! ptd.getAssignedTo().getFullNameByLast().equals(assignedTo)    )
					{
					itBelongs = false;          
					}    
				}
		   
		   if (itBelongs == true)
		       ptdListWF.add(ptd);
				
		  
		   }
		// issue 290
		  List<ProcessTrackingDetails> ptdListWFTemp = new ArrayList <ProcessTrackingDetails> ();
		  //ptdListWFTemp.addAll(ptdListWF);
		  i=0;
		  for (ProcessTrackingDetails ptd : ptdListWF) 
			  {
			  if (i==0)       
					{
					prevExp= "";
				    prevAssay = "";  
					} 
			  else if (i>0 && !isGantt)
					{
					if ( (!prevAssay.equals(ptd.getAssay().getAssayId()) || !prevExp.equals(ptd.getExperiment().getExpID()))  )   
						ptdListWFTemp.add(new ProcessTrackingDetails(" ", null, null, null, " ",  null,  null, null, " ", null, " ", null, 
										null));
					} 
			  ptdListWFTemp.add(ptd);
			  prevExp = ptd.getExperiment().getExpID();    
			  prevAssay = ptd.getAssay().getAssayId();
			  i++;    
			  }   
		 
		ptdListWF = new ArrayList <ProcessTrackingDetails> ();
		ptdListWF.addAll(ptdListWFTemp);
		return ptdListWF;
		} 
	
	// issue 298
	 public List<ProcessTrackingDetails> addBlankLinksToPtdList (List<ProcessTrackingDetails> ptdListWF, boolean isGantt)
	 	{
		  int i=0;
		  String prevExp = "";
		  String prevAssay = "";
		  List<ProcessTrackingDetails> ptdListWFTemp = new ArrayList <ProcessTrackingDetails> ();
		  /// issue 298 remove previous blanks....
		  ///
		  int sizee = ptdListWFTemp.size();
		  
		  // issue 298
		/*  for (i=0; i<sizee; i++ )
		  	{
			if (ptdListWF.get(i).getJobid().length() < 7)  
			    ptdListWF.remove(i);	
		  	}*/
		  
		  i= 0;
		  for (ProcessTrackingDetails ptd : ptdListWF) 
			  {
			  if (i==0)       
					{
					prevExp= "";
				    prevAssay = "";  
					} 
			  else if (i>0 && !isGantt)
					{
				    initializeProcessKids(ptd);
					if ( (!prevAssay.equals(ptd.getAssay().getAssayId()) || !prevExp.equals(ptd.getExperiment().getExpID()))  )   
						ptdListWFTemp.add(new ProcessTrackingDetails(" ", null, null, null, " ",  null,  null, null, " ", null, " ", null, 
										null));
					} 
			  ptdListWFTemp.add(ptd);
			  prevExp = ptd.getExperiment().getExpID();    
			  prevAssay = ptd.getAssay().getAssayId();
			  i++;    
			  }       
		 
		return  ptdListWFTemp;
		
	 	}
	 public List<Object []> loadAllDefaultTasksAssigned(String wfDesc)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery("select  t2.task_id, task_desc,  to_char(sysdate, 'mm/dd/yyyy') , ' ' a,' ' b,' ' c, last_Name || ', '  || first_Name, t2.task_order, days_required "
				 + " from tracking_tasks t1, default_tracking_tasks t2, researcher t3, workflow t4 " + 
				 " where t1.task_id = t2.task_id and t2.assigned_to = t3.researcher_id and t2.wf_id = t4.wf_id and wf_desc = ?1 "
				 +  " order by  t2.task_order ").setParameter(1, wfDesc)
				.getResultList();	
		return defaultList;
		}
	 
	 // issue 210
	 public List<Object []> loadTasksAssignedForUser(String email)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery(" select last_name || ',' || first_name uname, "
				 + " task_desc,  " + 
				 "  to_char(date_started, 'mm/dd/yyyy') date_started,  "  + 
				 "  to_char(date_onhold, 'mm/dd/yyyy') date_onHold,  "  + 
				 " status,  " + 
				 "exp_id,    " + 
				 "    assay_name || ' (' || t4.assay_id || ')' assayid,   " + 
				 " detail_order, " + 
				 " wf_desc " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and email = ?1 and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' order by exp_id, assayid, detail_order"
				 ).setParameter(1, email)
				.getResultList();	
		return defaultList;
		}
	 
	 // issue 269
	 ////////////
	 public List<Object []> loadTasksAssignedForUserExpAssay(String email, String expId, String assayId)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery(" select last_name || ',' || first_name uname, "
				 + " task_desc,  " + 
				 "  to_char(date_started, 'mm/dd/yyyy') date_started,  "  + 
				 "  to_char(date_onhold, 'mm/dd/yyyy') date_onHold,  "  + 
				 " status,  " + 
				 "exp_id,    " + 
				 "    assay_name || ' (' || t4.assay_id || ')' assayid,   " + 
				 " detail_order, " + 
				 " wf_desc " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and email = ?1  and t1.exp_id = ?2  and t1.assay_id = ?3 and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' order by exp_id, assayid, detail_order"
				 ).setParameter(1, email).setParameter(2,  expId).setParameter(3,  assayId)
				.getResultList();	
		return defaultList;
		}
	 
	 public List<Object []> listExpAssayForUser(String email)
		{
		List<Object []> expAssayList =  getEntityManager().createNativeQuery(" select "				
				+  " distinct exp_id,    " 
				+  " t4.assay_id    " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to and email = ?1 and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' order by 1,2 "
				 )
				.setParameter(1,email).getResultList();	
		return expAssayList;
		}
	 
	 
	 // issue 269
	 public List<Object []> loadTasksAssignedForExpAssay(String expId, String assayId)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery(" select "
				 + " task_desc,  " + 
				 "  to_char(date_started, 'mm/dd/yyyy') date_started,  "  + 
				 "  to_char(date_onhold, 'mm/dd/yyyy') date_onHold,  "  + 
				 " status,  " + 
				 "exp_id,    " + 
				 "    assay_name || ' (' || t4.assay_id || ')' assayid,   " + 
				 " detail_order, " + 
				 " wf_desc " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , assays t4, workflow t5 " + 
				 " where t1.exp_id = ?1 and t1.assay_id= ?2 and t1.task_id = t2.task_id  and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' order by exp_id, assayid, detail_order"
				 ).setParameter(1, expId).setParameter(2, assayId)
				.getResultList();	
		return defaultList;
		}
	 
	 // issue 210
	 public List<Object []> listExpAssay()
		{
		List<Object []> expAssayList =  getEntityManager().createNativeQuery(" select "				
				+  " distinct exp_id,    " 
				+  "    assay_name || ' (' || t4.assay_id || ')' assayid    " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' order by 1,2 "
				 )
				.getResultList();	
		return expAssayList;
		}
	 
	 // issue 290
	 public List<ProcessTrackingDetails> listProcTrackDetails()
		{
		List<ProcessTrackingDetails> expAssayList =  getEntityManager().createQuery(" from ProcessTrackingDetails order by experiment.expID, assay.assayId"				
		).getResultList();	
		return expAssayList;
		}
	 
	 public List<Object []> listExpAssayExp(String expId)
		{
		List<Object []> expAssayList =  getEntityManager().createNativeQuery(" select "				
				+  " distinct exp_id,    " 
				+  "    assay_name || ' (' || t4.assay_id || ')' assayid    " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id and status != 'Completed' and exp_id = ?1 "
				 ).setParameter(1,  expId)
				.getResultList();	
		return expAssayList;
		}
	 
	 public List<Object []> loadTasksAssignedForExp(String expid)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery(" select last_name || ',' || first_name uname, "
				 + " task_desc,  " + 
				 "  to_char(date_started, 'mm/dd/yyyy') date_started,  "  + 
				 "  to_char(date_onhold, 'mm/dd/yyyy') date_onHold,  "  + 
				 " status,  " + 
				 "exp_id,    " + 
				 "    assay_name || ' (' || t4.assay_id || ')' assayid,   " + 
				 " detail_order, " + 
				 " wf_desc " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and t1.exp_id = ?1 and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id order by wf_desc, exp_id, detail_order "
				 ).setParameter(1, expid)
				.getResultList();	
		return defaultList;
		}
	 
	 public List<Object []> loadTasksAssignedForExpAndAssay(String expid, String assayId)
		{
		List<Object []> defaultList =  getEntityManager().createNativeQuery(" select last_name || ',' || first_name uname, "
				 + " task_desc,  " + 
				 "  to_char(date_started, 'mm/dd/yyyy') date_started,  "  + 
				 "  to_char(date_onhold, 'mm/dd/yyyy') date_onHold,  "  + 
				 " status,  " + 
				 "exp_id,    " + 
				 "    assay_name || ' (' || t4.assay_id || ')' assayid,   " + 
				 " detail_order, " + 
				 " wf_desc " + 
				 " from tracking_tasks_details t1, tracking_tasks t2 , researcher t3, assays t4, workflow t5 " + 
				 " where t1.task_id = t2.task_id  and t3.researcher_id = t1.assigned_to  and t1.exp_id = ?1 and t1.assay_id = ?2 and t5.wf_id = t1.wf_id and t4.assay_id = t1.assay_id order by wf_desc, exp_id, detail_order "
				 ).setParameter(1, expid).setParameter(2, assayId)
				.getResultList();	
		return defaultList;
		} 
	
	 public List<String> loadAllWorkFlows()
		{
		List<String> defaultList =  getEntityManager().createNativeQuery("select wf_desc "
				 + " from workflow  order by 1 ")
				.getResultList();	
	   
		return defaultList;
		}
	 
	 public List<String> loadAllAssignedWorkFlows()
		{
		List<String> assignedList =  getEntityManager().createNativeQuery("select distinct wf_desc "
				 + " from workflow t1, tracking_tasks_details t2 where t1.wf_id = t2.wf_id  ")
				.getResultList();	
	   
		return assignedList;
		}
	 // issue 285
	 public List<String> loadAllAssignedExperiments()
		{
		List<String> assignedList =  getEntityManager().createNativeQuery("select distinct exp_id "
				 + " from tracking_tasks_details t2  order by 1 desc ")
				.getResultList();	
	   
		return assignedList;
		}
	 
	 //issue 285
	 public List<Object []> loadAllComments(String exp, String assay)
		{
		List<Object []> commentObjList =  getEntityManager().createNativeQuery("select distinct exp_id, assay_id, task_desc, comments "
				 + " from tracking_tasks t1, tracking_tasks_details t2 where t1.task_id = t2.task_id and exp_id = ?1 and assay_id = ?2 and trim(comments) is not null order by 1 asc ")
				.setParameter(1, exp).setParameter(2, assay).getResultList();	
	   
		return commentObjList;
		}
	
	public List<ProcessTracking> loadAllTasks()
		{
		List<ProcessTracking> ptList =  getEntityManager().createQuery(" from ProcessTracking order by 1")
				.getResultList();	
	   
		return ptList;
		}
	/////////////////////////////////////////////
	
	public List<String> grabUsersWithAssignedTasks ()
		{
		Query query2 = getEntityManager().createNativeQuery ("select distinct email from researcher t1, tracking_tasks_researcher t2 " + 
									" where t1.researcher_id = t2.researcher_id and " + 
				                    " t1.researcher_id in (select distinct assigned_to from tracking_tasks_details where status != 'Completed')   "		
				);
		return query2.getResultList();
		}
	
	
	
	public List<String> allAssayNamesForExpIdInTracking (String eid, boolean skipAbsciex)
		{
		// issue 287
		Query query2 = null;
		if (edu.umich.brcf.shared.util.utilpackages.StringUtils.isEmptyOrNull((eid)))
	        query2 = getEntityManager().createNativeQuery("select distinct cast(assay_name as VARCHAR2(150)), "
	        		+ " cast(t1.assay_id as VARCHAR2(4))  " + 
	        		 "  from tracking_tasks_details t1, assays t2   " + 
	        		 "   where t1.assay_id = t2.assay_id ");
		else 
			query2 = getEntityManager().createNativeQuery("select cast(a.assay_name as VARCHAR2(150)), "
									+ " cast(a.assay_id as VARCHAR2(4)) from (select s.sample_id from Sample s where s.exp_id ="
									+ "?1"
									+ ") t"
									+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id "
									+ " inner join Assays a on sa.assay_id = a.assay_id and a.assay_id in (select distinct assay_id from tracking_tasks_details) "
									+ " group by a.assay_name, a.assay_id").setParameter(1,eid) ;		
		ArrayList<String> labelledAssays = new ArrayList<String>();
		List<Object[]> assayList = query2.getResultList();	
		for (Object[] assayResult : assayList)
			{
			String assayId = (String) assayResult[1];
			String assayName = (String) assayResult[0];
			if (skipAbsciex && assayId.equals("A004"))
				continue;
			labelledAssays.add(assayName + " (" + assayId + ")");
			}	
		return labelledAssays;
		}
	
	public void deleteTracking (String expID)
		{
		String queryString = "delete from tracking_tasks_details where exp_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expID);		
		query.executeUpdate();
		}
	
	// issue 262
	public void deleteTrackingDetails (String jobid)
		{
		String queryString = "delete from tracking_tasks_details where job_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, jobid);		
		query.executeUpdate();
		}
	
	public void deleteTracking (String expID, String assay_id)
		{
		String queryString = "delete from tracking_tasks_details where exp_id = ?1 and assay_id = ?2";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, expID).setParameter(2, assay_id);		
		query.executeUpdate();
		}
	
	public void deleteDefault (String wfID)
		{
		String queryString = "delete from default_tracking_tasks where wf_id = ?1";
		Query query = getEntityManager().createNativeQuery(queryString).setParameter(1, wfID);		
		query.executeUpdate();
		}
	

	// issue 292
	public void doMoveAhead(String wfID, String expID, String assayId, int increment, int trackingorder, String status, String dateToPropagateAgainst)
	{
	boolean movedownInProcess = false;
		// issue 292 if you change completed make sure the task below in process is actually + 1
	Query query = 	getEntityManager().createNativeQuery("select detail_order from tracking_tasks_details  where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order =?5 and status = 'In progress'  order by 1 " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, trackingorder + 1);	
	if (query.getResultList().size() == 0)  
		{
		movedownInProcess = false;
		}
	else
		{
		movedownInProcess = true;
		}
	
	// issue 287
	query = 	getEntityManager().createNativeQuery("select detail_order from tracking_tasks_details  where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order >?5 and status in ('On hold', 'Completed') and rownum = 1 order by 1 " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, trackingorder);
	int trackOrder = 0;  
	if (query.getResultList().size() == 0)  
	    query = getEntityManager().createNativeQuery("select detail_order from tracking_tasks_details  where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order >?5 and status = 'In queue' order by 1 " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, trackingorder);

	else 
		{
		trackOrder = Integer.parseInt(query.getResultList().get(0).toString());
		query = getEntityManager().createNativeQuery("select detail_order from tracking_tasks_details  where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order >?5 and detail_order < ?6 and status = 'In queue' order by 1 " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, trackingorder).setParameter(5, trackingorder).setParameter(6, trackOrder);
		}
	List <String> detailOrdersList  = query.getResultList();
	for (int i = 0; i < detailOrdersList.size(); i++)   
		{
		// issue 287 use i+1 for increment
		Query query2 = getEntityManager().createNativeQuery("update tracking_tasks_details set date_started = to_date(?6, 'mm/dd/yyyy') + ?4 " + " where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order =?5 and status = 'In queue' " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(4, movedownInProcess ? i + 1 : i).setParameter(5, detailOrdersList.get(i)).setParameter(6, dateToPropagateAgainst);
		query2.executeUpdate();
		}      
	
    if (status.equals("Completed"))                               
		// issue 277
		{  
		query = getEntityManager().createNativeQuery("update tracking_tasks_details set status=  'In progress' " +  " where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order = ?5 and status = 'In queue' " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, (trackingorder+ 1));
		query.executeUpdate();	  
		}  
    // take care of in process right after completed         
    Query query2 = getEntityManager().createNativeQuery("update tracking_tasks_details set date_started = to_date(?6, 'mm/dd/yyyy')  " + " where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order = ?5 and status = 'In progress' " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, (trackingorder + 1)).setParameter(6, dateToPropagateAgainst);
	query2.executeUpdate();             
	}        
	
	
	// issue 277 
	public String  grabNumberOfSamplesForEmail (String expID)
		{
		Query query;
		query = getEntityManager().createNativeQuery ("select count(*) from sample where exp_id = ?1").setParameter (1, expID);
		String numSamplesStr = query.getResultList().get(0).toString();
		
		return numSamplesStr;
		}
	
	// issue 277
	public String  grabSampleTypeForEmail (String expID)
		{
		Query query;
		query = getEntityManager().createNativeQuery ("select distinct description from sample_type t1, sample t2 where t1.sample_type_id = t2.sample_type_id and exp_id = ?1").setParameter (1, expID);
		String sampleTypeStr = query.getResultList().get(0).toString();
		return sampleTypeStr;
		}
	
	 public void initializeProcessKids (ProcessTrackingDetails vPTD)
	 	{
		initializeTheKids(vPTD, new String[] { "processTracking", "assignedTo", "experiment", "assay"});
		initializeTheKids(vPTD.getExperiment(), new String[] { "project"});		 
	 	}
	}

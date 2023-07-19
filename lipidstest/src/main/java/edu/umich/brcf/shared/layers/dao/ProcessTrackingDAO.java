/**********************
 * Updated by Julie Keros Sept 2000
 * Added aliquotIdsForExpId ,
 * getMatchingAliquotIds,loadByCid and other routines for Aliquots.
 **********************/
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.layers.domain.DefaultTrackingTasks;

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
	
	// issue 262
	public Map <String, String>  createSampleTypeStringFromList( )
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
	////
	public List<String> loadAllWFsAssigned(String expID)
		{
		List <String> ptdList = new ArrayList <String> ();
		if (StringUtils.isNullOrEmpty(expID))
			ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id  ")
			.getResultList();
		else 
		    ptdList =  getEntityManager().createNativeQuery(" select distinct wf_desc from workflow t1, tracking_tasks_details t2   where t1.wf_id = t2.wf_id and t2.exp_id = ?1 ").setParameter(1,expID)
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
		List<ProcessTrackingDetails> ptdListWF = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdListWFInProgress = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdListWFOnHold = new ArrayList <ProcessTrackingDetails> ();
		List<ProcessTrackingDetails> ptdList = new ArrayList <ProcessTrackingDetails> ();
		ProcessTrackingDetails pd;
		// issue 273
		if (allExpAssay )
			{
			ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd  order by   experiment.expID, assay.assayId, detailOrder  ")
			.getResultList();
			}
		else if (StringUtils.isNullOrEmpty(expId) || StringUtils.isNullOrEmpty(assayDescId))
			//return ptdListWF;
			 ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1  order by experiment.expID, assay.assayId, detailOrder  ").setParameter(1,  expId).getResultList();
		else 
		    ptdList =  getEntityManager().createQuery("from ProcessTrackingDetails pd where pd.experiment.expID = ?1 and pd.assay.assayId= ?2 order by detailOrder  ").setParameter(1,  expId).setParameter(2, assayDescId)
				.getResultList();
		for (ProcessTrackingDetails ptd : ptdList)
			{
			initializeTheKids(ptd, new String[] { "processTracking", "assignedTo"});
			//////if ( ptd.getWorkflow().getWfDesc().equals(wfDescString))
			if (isCurrent)
				{
				if (    (  !StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && !ptd.getStatus().equals("Completed") )   ||   
						(StringUtils.isNullOrEmpty(assignedTo)  && !ptd.getStatus().equals("Completed"))
				   )
				   ptdListWF.add(ptd);
				}
			else if (isInProgress)
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("In progress")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("In progress"))
						)
					ptdListWF.add(ptd);
			
				}
			else if (isOnHold)
				{
				if ( (!StringUtils.isNullOrEmpty(assignedTo) &&  ptd.getAssignedTo().getFullNameByLast().equals(assignedTo) && ptd.getStatus().equals("On hold")) ||
						(StringUtils.isNullOrEmpty(assignedTo)  && ptd.getStatus().equals("On hold")))
						ptdListWF.add(ptd);
				}
			else if (!StringUtils.isNullOrEmpty(assignedTo) && !assignedTo.equals("All Users"))
				{
				if (ptd.getAssignedTo().getFullNameByLast().equals(assignedTo)    )
					ptdListWF.add(ptd);
				}
			else if (StringUtils.isNullOrEmpty(assignedTo) || assignedTo.equals("All Users"))
				ptdListWF.add(ptd);
			}
		return ptdListWF;
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
				 + " from workflow  ")
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
	 
	 public List<String> loadAllAssignedExperiments()
		{
		List<String> assignedList =  getEntityManager().createNativeQuery("select distinct exp_id "
				 + " from tracking_tasks_details t2  order by 1 asc ")
				.getResultList();	
	   
		return assignedList;
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
		Query query2 = getEntityManager().createNativeQuery("select cast(a.assay_name as VARCHAR2(150)), "
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
	// issue 277
	/////////////////////////////////////////////
	public void doMoveAhead(String wfID, String expID, String assayId, int increment, int trackingorder, String status)
		{
		String theQuery = 
				"update tracking_tasks_details set date_started = date_started + " + increment + " where wf_id = " + "'" + wfID + "'" + " and exp_id = " + "'" + expID + "'" + " and assay_id = " + "'" + assayId + "'" + " and detail_order > " + trackingorder;  
		System.out.println("here is the query :" + theQuery);
		Query query = getEntityManager().createNativeQuery("update tracking_tasks_details set date_started = date_started + ?4 " + " where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order >?5  and status = 'In queue'"  ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(4, increment).setParameter(5, trackingorder);
		query.executeUpdate();	
	    if (status.equals("Completed"))
			// issue 277
			{
			query = getEntityManager().createNativeQuery("update tracking_tasks_details set status=  'In progress' " +  " where wf_id = ?1 and exp_id = ?2 " + " and assay_id = ?3 " + " and detail_order = ?5 " ).setParameter(1, wfID).setParameter(2, expID).setParameter(3, assayId).setParameter(5, (trackingorder+ 1));
			query.executeUpdate();	
			} 
		}
	
      
	// issue 283
	public void doAutomaticPropagation()
		{
		int i = 0;
		int prevExpDays = 0;
		int inProcessDays = 0;
		List <Object []> listOfExpAssay = listExpAssay();
		int idx = 0;
		for (Object [] obj : listOfExpAssay)
			{
			i = 1;
		            
			Query queryListInProcess =  getEntityManager().createNativeQuery("select days_expected from tracking_tasks_details where exp_id = ?1 and assay_id = ?2 and status = 'In progress' order by detail_order ").setParameter(1, obj[0].toString()).setParameter(2,  StringParser.parseId(obj[1].toString()));
			List <String> theInprocessResult = queryListInProcess.getResultList();
			if (theInprocessResult.size() > 0  )
				inProcessDays = Integer.valueOf(theInprocessResult.get(theInprocessResult.size() -1 ).toString());			
			Query queryListJobs = getEntityManager().createNativeQuery("select job_id, days_expected from tracking_tasks_details where exp_id = ?1 and assay_id = ?2 and status = 'In queue' order by detail_order ").setParameter(1, obj[0].toString()).setParameter(2,  StringParser.parseId(obj[1].toString()));
		    System.out.println("Here is the querylistjobs:" + "select job_id, days_expected from tracking_tasks_details where exp_id =" + obj[0].toString()  + " and assay_id = " + StringParser.parseId(obj[1].toString() +   " and status = 'In queue' order by detail_order "));
			
			List <Object []> listJobs =  queryListJobs.getResultList();
			Query query = null;
			query = getEntityManager().createNativeQuery("update tracking_tasks_details set date_started = sysdate where exp_id = ?1 and assay_id = ?2 and status = 'In progress' and date_inprogress is null ").setParameter(1,obj[0].toString()).setParameter(2,  StringParser.parseId(obj[1].toString()));
			query.executeUpdate();
			for (Object [] strJob : listJobs)      
				{    
			
				query = getEntityManager().createNativeQuery("update tracking_tasks_details set date_started = sysdate + ?1  where job_id = ?2").setParameter(1, i).setParameter(2, strJob[0].toString());
				System.out.println("query for else:  update tracking_tasks_details set date_started = sysdate + " + " " + " +  " + (i) + " where job_id = " + strJob[0].toString());
				query.executeUpdate();	
				prevExpDays = Integer.valueOf(strJob[1].toString());
				
				i = i + prevExpDays;
				idx++;
				          
				}
			}    
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
	}

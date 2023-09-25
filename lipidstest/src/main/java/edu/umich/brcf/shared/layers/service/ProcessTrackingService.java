// Created by Julie Keros Aug 20 2022 for LIMS tracking issue 
package edu.umich.brcf.shared.layers.service;


import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.ProcessTrackingDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.DefaultTrackingTasks;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ProcessTracking;
import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.layers.domain.Workflow;
import edu.umich.brcf.shared.layers.dto.ProcessDefaultDTO;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDTO;
import edu.umich.brcf.shared.layers.dto.ProcessTrackingDetailsDTO;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;

@Transactional(rollbackFor = Exception.class)
public class ProcessTrackingService 
    {	
	
	ProcessTrackingDAO processTrackingDao;
	UserDAO userDao;
	ExperimentDAO experimentDao;
	AssayDAO assayDao;

	// issue 262
	public void deleteTrackingDetails (String jobid)
		{
		processTrackingDao.deleteTrackingDetails(jobid);
		}
		
	public void setExperimentDao(ExperimentDAO experimentDao) 
	    {
		this.experimentDao = experimentDao;
	    }

	public ExperimentDAO getExperimentDao()
	    {
		return experimentDao;
		}
	
	public void setUserDao(UserDAO userDao) 
	    {
		this.userDao = userDao;
	    }

	public UserDAO getUserDao()
	    {
		return userDao;
		}
	
	public void setAssayDao(AssayDAO assayDao) 
	    {
		this.assayDao = assayDao;
	    }

	public AssayDAO getAssayDao()
	    {
		return assayDao;
		}
	
	
	public List <Object[]> grabSampleTypeStringFromList(String expID, String wfID )
		{
		return processTrackingDao.grabSampleTypeStringFromList(expID, wfID );
		}
	
	public String grabSampleType(String wfID, String expID)
		{
		return processTrackingDao.grabSampleType(wfID, expID);
		}
	
	// issue 290
	public String grabSampleType( String expID)
		{
		return processTrackingDao.grabSampleType( expID);
		}
	
	// issue 290
	public Map <String, String> grabAllSampleTypes( )
		{
		return processTrackingDao.grabAllSampleTypes();
		}
	
	
	public String grabMinDateStarted(String wfID)
		{
		return  processTrackingDao.grabMinDateStarted(wfID);		
		}
	
	public String existsOnHold(String wfID)
		{
		return processTrackingDao.existsOnHold(wfID);
		}	
	
	public ProcessTrackingDetails loadById(String jobid)
		{		
		return processTrackingDao.loadById(jobid);
		}
	
	public List<ProcessTrackingDetails> loadByAssignedTo(String assignedTo)
		{
		return processTrackingDao.loadByAssignedTo(assignedTo);
		}
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned()
		{
		return processTrackingDao.loadAllTasksAssigned();
		}
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId, boolean allExpAssay, String assignedTo)
		{
		return processTrackingDao.loadAllTasksAssigned(expId,assayDescId, allExpAssay, assignedTo);
		}
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId, boolean allExpAssay, String assignedTo, boolean isCurrent, boolean isInProgress, boolean isOnHold)
		{
		return processTrackingDao.loadAllTasksAssigned(expId,assayDescId, allExpAssay, assignedTo, isCurrent, isInProgress, isOnHold);
		}
	
	// issue 290
	// issue 298
	public List<ProcessTrackingDetails> loadAllTasksBelowEditedExperiment(String expId, String assayDescId , int detailOrder,  String assignedTo)
		{
		return processTrackingDao.loadAllTasksBelowEditedExperiment(expId, assayDescId, detailOrder,  assignedTo);
		}
	
	
	public List<ProcessTrackingDetails> loadAllTasksAssigned(String expId, String assayDescId, boolean allExpAssay, String assignedTo, boolean isCurrent, boolean isInProgress, boolean isOnHold, boolean isCompleted, boolean isInQueue, boolean isGant)
		{
		return processTrackingDao.loadAllTasksAssigned(expId,assayDescId, allExpAssay, assignedTo, isCurrent, isInProgress, isOnHold, isCompleted, isInQueue, isGant);
		}
	
	// issue 298
	public List<ProcessTrackingDetails> addBlankLinksToPtdList (List<ProcessTrackingDetails> ptdListWF, boolean isGantt)
 		{
        return processTrackingDao.addBlankLinksToPtdList (ptdListWF,isGantt );
 		}
	
	// issue 262
	public Map <String, String> createSampleTypeStringMapFromList ()
	 	{
		 return processTrackingDao.createSampleTypeStringMapFromList();
	 	}
	
	 public List<Object []> loadTasksAssignedForUser(String email)
	    {
		return processTrackingDao.loadTasksAssignedForUser(email);
		}
	 
	 public List<Object []> listExpAssay()
	    {
		return processTrackingDao.listExpAssay();
		}
	 
	 public List<Object []> listExpAssayExp(String expId)
	 	{
		return processTrackingDao.listExpAssayExp(expId);
	 	}
	 
	 public List<ProcessTrackingDetails> listProcTrackDetails()
	 	{
		 return processTrackingDao.listProcTrackDetails();
	 	}
	 	 
	 public List<Object []> loadTasksAssignedForExp(String expid)
	    {
		return processTrackingDao.loadTasksAssignedForExp(expid);
		}
	 
	 public List<Object []> loadTasksAssignedForExpAndAssay(String expid, String assayId)
	    {
		return processTrackingDao.loadTasksAssignedForExpAndAssay(expid, assayId);
		}
	 	
	 // issue 287
	public List<String> loadAllWFsAssigned(String expID, String assayID)
		{
		return processTrackingDao.loadAllWFsAssigned(expID, assayID);
		}
	
	public List<Object []> loadAllDefaultTasksAssigned(String wfDesc)
		{
		return processTrackingDao.loadAllDefaultTasksAssigned(wfDesc);
		}
	
	
	public List<ProcessTracking> loadAllTasks()
		{
		return processTrackingDao.loadAllTasks();
		}	
	
	public List<String> allTaskDesc()
		{
		return processTrackingDao.allTaskDesc();
		}
	
	
	// issue 61	
	public ProcessTrackingDAO getProcessTrackingDao()
		{
		return processTrackingDao;
		}
	
	public void setProcessTrackingDao(ProcessTrackingDAO processTrackingDao) 
		{
		this.processTrackingDao = processTrackingDao;
		}
	
	// issue 210 
	public void saveDefaultDTOs (List <ProcessTrackingDetailsDTO> arrayDTO)
		{
		saveDefaultDTOs (arrayDTO, false);
		}
	
	public void saveDefaultDTOs (List <ProcessTrackingDetailsDTO> arrayDTO, boolean modifyDefault)
		{
		User user = null;
		Workflow workflow = null;
		int idxorder = 1;
		ProcessDefaultDTO dtoDefault = new ProcessDefaultDTO();
		try
		    {
			
			for (ProcessTrackingDetailsDTO dto : arrayDTO)
				{
				if (StringUtils.isEmptyOrNull(dto.getAssignedTo()))
				   continue;
				user = userDao.loadUserByFullName(dto.getAssignedTo());
				//Workflow workflow = new Workflow();
				
				workflow  = processTrackingDao.loadByIdWF(processTrackingDao.grabWfIDFromDesc(dto.getWfID()) );
				
				Assay a = assayDao.loadAssayByID(dto.getAssayID());
				if (modifyDefault)
					dto.setDetailOrder(idxorder);
				save (dto, user, workflow , a);
				idxorder++;
				}
			
				if (modifyDefault)
					processTrackingDao.deleteDefault(grabWfIDFromDesc(arrayDTO.get(0).getWfID()));	
				dtoDefault = new ProcessDefaultDTO ();
				if (modifyDefault)
					{
					idxorder = 1;
					for (ProcessTrackingDetailsDTO dto : arrayDTO)
						{
						user = userDao.loadUserByFullName(dto.getAssignedTo());
						doModifyDefault (  dtoDefault,  user, workflow, dto, idxorder);
						idxorder++;
						}
					}
				//User user =  userDao.loadUserByFullName(processTrackingDetailsDTO.getAssignedTo());	
			
		    }
		catch (Exception e)
			{
			e.printStackTrace();	
			}
		}
	// issue 210
	
	/* public ProcessTrackingDetails save (ProcessTrackingDetailsDTO dto, User user, Workflow w, Assay a)
		{
		return save ( dto, user,null, a);
		
		}*/
	
	public List<String> allAssayNamesForExpIdInTracking (String eid, boolean skipAbsciex)
		{
		return processTrackingDao.allAssayNamesForExpIdInTracking(eid, skipAbsciex);
		}
	
	public String grabTaskIdFromDesc (String s)
		{
		return processTrackingDao.grabTaskIdFromDesc(s);
		}
	
	public String grabWfIDFromDesc(String taskDesc)
		{
		return  processTrackingDao.grabWfIDFromDesc( taskDesc);
		
		}
	public Workflow loadByIdWF(String wfID)
		{
	//	
		
		return processTrackingDao.loadByIdWF(wfID);
		}
		
	// issue 210 
	public  DefaultTrackingTasks doModifyDefault ( ProcessDefaultDTO dto, User user, Workflow workflow, ProcessTrackingDetailsDTO dtoDetails, int idxorder)
		{
		dto.setTaskID(processTrackingDao.grabTaskIdFromDesc(dtoDetails.getTaskDesc()));
		//dto.setAssignedTo(dtoDetails.getAssignedTo());
		dto.setAssignedTo(user.getId());
		dto.setTaskOrder(idxorder);
		//dto.setTaskOrder(dto.getTaskOrder());
		/////////////dto.setTaskOrder(dtoDetails.getDetailOrder());
		dto.setWfId(dtoDetails.getWfID());		
		ProcessTracking pt = processTrackingDao.loadByPTbyTaskID(dto.getTaskID());
		DefaultTrackingTasks dtt = DefaultTrackingTasks.instance( dto.getDefaultTaskId(), pt,  user, workflow,   dto.getTaskOrder()        );	
		try
			{			
			processTrackingDao.createDefaultTrackingTasks (dtt)	;				
			}
		catch (Exception e)
			{
			e.printStackTrace();	
			}
		return null;
		}
	
	
	public  ProcessTrackingDetails save (ProcessTrackingDetailsDTO dto, User user, Workflow workflow, Assay a)
    	{
		try
			{
			String taskid = processTrackingDao.grabTaskIdFromDesc(dto.getTaskDesc());
			ProcessTracking pt = processTrackingDao.loadByPTbyTaskID(taskid);
		    if (pt.getDaysRequired() == null)
		    	pt.setDaysRequired(1);
			Experiment experiment = StringUtils.isEmptyOrNull(dto.getExpID()) ? null : experimentDao.loadById(dto.getExpID()) ;
		  //  ProcessTrackingDetails ptd  = ProcessTrackingDetails.instance(dto.getJobID(), pt, dto.getDateStarted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateStarted()),  dto.getDateCompleted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateCompleted()), dto.getComments(),  user, DateUtils.calendarFromDateStr(dto.getDateAssigned()) , experiment, workflow, dto.getStatus(), a, dto.getDaysExpected() , dto.getDetailOrder(), dto.getDateOnHold() == null ? null : DateUtils.calendarFromDateStr(dto.getDateOnHold()));	
			// issue 277
		//	ProcessTrackingDetails ptd  = ProcessTrackingDetails.instance(dto.getJobID(), pt, dto.getDateStarted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateStarted()),  dto.getDateCompleted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateCompleted()), dto.getComments(),  user, null , experiment, workflow, dto.getStatus(), a, dto.getDaysExpected() , dto.getDetailOrder(), dto.getDateOnHold() == null ? null : DateUtils.calendarFromDateStr(dto.getDateOnHold()));
			// issue 287
			ProcessTrackingDetails ptd  = ProcessTrackingDetails.instance(dto.getJobID(), pt, dto.getDateStarted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateStarted()),  dto.getDateCompleted() == null ? null : DateUtils.calendarFromDateStr(dto.getDateCompleted()), dto.getComments(),  user,  experiment, workflow, dto.getStatus(), a, dto.getDaysExpected() , dto.getDetailOrder(), StringUtils.isEmptyOrNull(dto.getDateOnHold())  ? null : DateUtils.calendarFromDateStr(dto.getDateOnHold()));
			if (dto.getJobID() == null || dto.getJobID().equals("to be assigned"))
				{
				processTrackingDao.createProcessTracking(ptd);
				}
			else    
				{
				ptd = processTrackingDao.loadById(dto.getJobID());
				ptd.update(dto, user, ptd.getDateStarted());
				}
			
			return ptd;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new RuntimeException("Error when saving note");
			}
    	}
	
	// issue 210
	public  ProcessTracking saveTask (ProcessTrackingDTO dto)
		{
		ProcessTracking pt  = ProcessTracking.instance(dto.getTaskID(), dto.getTaskDesc());
		if (dto.getTaskID() == null || dto.getTaskID().equals("to be assigned"))
			processTrackingDao.createTask(pt);
		else 
			{
			pt = processTrackingDao.loadByPTbyTaskID(dto.getTaskID());
			pt.update(dto);
			}
		
		return pt;
		}
	
	 public List<String> loadAllWorkFlows()
		{
		return processTrackingDao.loadAllWorkFlows();		
		}
	 
	 public List<String>loadAllAssignedWorkFlows()
	 	{
		 return processTrackingDao.loadAllAssignedWorkFlows();
	 	}
	 
	 public List<String>loadAllAssignedExperiments()
	 	{
		 return processTrackingDao.loadAllAssignedExperiments();
	 	}
	 
	public ProcessTracking loadByPTbyTaskID(String taskid)
		{
		return processTrackingDao.loadByPTbyTaskID(taskid);
		}
	
	protected String getMailTitle() { return ""; }
	protected String getMailAddress() { return "metabolomics@med.umich.edu"; }
	
	public void deleteTracking (String expID)
		{
		processTrackingDao.deleteTracking(expID);
		}
	
	public void deleteTracking (String expID, String assayID)
		{
		processTrackingDao.deleteTracking(expID, assayID);
		}

	// issue 269
	public List<String> grabUsersWithAssignedTasks ()
		{
		return processTrackingDao.grabUsersWithAssignedTasks();
		}
	
	 public List<Object []> loadTasksAssignedForExpAssay(String expId, String assayId)
	 	{
		 return processTrackingDao.loadTasksAssignedForExpAssay(expId, assayId);
	 	}
	 
	 // issue 269
	 public List<Object []> loadTasksAssignedForUserExpAssay(String email, String expId, String assayId)
		{
		 return processTrackingDao.loadTasksAssignedForUserExpAssay(email, expId, assayId);
		}
	 
	 // issue 269
	 public  List<Object []> listExpAssayForUser(String email)
		{
		 return processTrackingDao.listExpAssayForUser(email);
		}
	 
	 // issue 292
	public String grabMaxCompletedDate (String expId, String assayDescId, int detailOrder)
		{
		return processTrackingDao.grabMaxCompletedDate (expId, assayDescId, detailOrder);	   	 		
		}
	
	 // issue 292
	public String grabMaxOnHoldDate (String expId, String assayDescId, int detailOrder)
		{
		return processTrackingDao.grabMaxOnHoldDate (expId, assayDescId, detailOrder);		 		
		}
	 
	 // issue 287
	 public void doMoveAhead(String wfID, String expID, String assayId, int increment, int trackingorder, String status, String onHoldDate)
		{
		 processTrackingDao.doMoveAhead(wfID, expID, assayId, increment, trackingorder, status, onHoldDate);
		}
	 	 
	 // issue 277
	 public String  grabNumberOfSamplesForEmail (String expID)
		{
		return processTrackingDao.grabNumberOfSamplesForEmail(expID) ;
		}
	 
	 // issue 277
	 public String  grabSampleTypeForEmail (String expID)
	 	{
		return processTrackingDao.grabSampleTypeForEmail(expID);
	 	}
	 
	 public List<Object []> loadAllComments(String exp, String assay)
	 	{
		 return processTrackingDao.loadAllComments(exp, assay);
	 	}
	 public void initializeProcessKids (ProcessTrackingDetails vPTD)
	 	{
		processTrackingDao.initializeProcessKids (vPTD);	 
	 	}
    }

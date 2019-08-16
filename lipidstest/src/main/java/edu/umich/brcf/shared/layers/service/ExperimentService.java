package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.wicket.Session;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoItem;
import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ClientReportDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.ProjectDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Priority;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.domain.Shortcode;
import edu.umich.brcf.shared.layers.dto.ClientSampleAliquotsBean;
import edu.umich.brcf.shared.layers.dto.ClientSampleAssaysBean;
import edu.umich.brcf.shared.layers.dto.ExperimentDTO;
import edu.umich.brcf.shared.layers.dto.SampleAssaysBean;
import edu.umich.brcf.shared.layers.dto.ShortcodeDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.comparator.ExperimentInventoryByDateComparator;
import edu.umich.brcf.shared.util.comparator.SampleBySampleIdComparator;
import edu.umich.brcf.shared.util.datacollectors.ExperimentInventoryInfo;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.ExperimentRandomization;


@Transactional
public class ExperimentService 
	{
	ExperimentDAO experimentDao;
	ProjectDAO projectDao;
	UserDAO userDao;
	AssayDAO assayDao;
	ClientReportDAO clientReportDao;
	ExperimentRandomization itemExpRnd ;// issue 268;
		
	public List<Experiment> allExperiments(){ return experimentDao.allExperiments(); }
	public List<String> getMatchingExperiments(String input) { return experimentDao.getMatchingExperiments(input); }
	public List<Project> getProjects(String exp) { return experimentDao.getProjects(exp); }
	public List<String> getFactorNamesForExpId(String expId) { return experimentDao.getFactorNamesForExpId(expId); }
	public Experiment loadSimpleExperimetById(String id) { return experimentDao.loadSimpleExperimetById(id); }
	public Experiment loadExperimentWithInfoForDrcc(String expId) { return experimentDao.loadExperimentWithInfoForDrcc(expId);  }
	public Experiment loadExperimentWithInfoForEditing(String expId){ return experimentDao.loadExperimentWithInfoForEditing(expId);  }
	public List<Experiment> loadExperimentByProject(Project project){ return experimentDao.loadExperimentByProject(project); }
	public List<Experiment> loadExperimentsByProjectID(String project) { return experimentDao.loadExperimentsByProjectID(project);	}
	public List<Experiment> loadExperimentsByClientContact(String contact)  { return experimentDao.loadExperimentsByClientContact(contact); }
	public List<String> getShortcodeIdsForExpId(String expId) { return experimentDao.getShortcodeIdsForExpId(expId); }
	public Shortcode getExperimentShortcode(String code, String expId) { return experimentDao.getExperimentShortcode(code, expId); }
	public List<Experiment> allActiveExperiments() 	{ return experimentDao.allActiveExperiments(); }
	public List<String> expIdsByInceptionDate()	{ return experimentDao.expIdsByInceptionDate(); }
	public List<SampleAssaysBean> getAssaysForExperiment(Experiment experiment)  { return experimentDao.getAssaysForExperiment(experiment); }
	public List <ClientSampleAssaysBean> getExperimentAssaysForClient(Experiment experiment) { return experimentDao.getExperimentAssaysForClient(experiment); }
	public List<SampleAssay> getSampleAssaysForExperimentAndAssay(String expId, String assayId) { return experimentDao.getSampleAssaysForExperimentAndAssay(expId, assayId); }
	public void updateServiceRequestForExperiment(Experiment exp, String serviceRequest) { //exp.updateServiceRequest(serviceRequest)
	}
	
	
	public Experiment loadWithInfoForSubjectTracking(String expId)
		{
		return experimentDao.loadWithInfoForSubjectTracking(expId);
		}
	
	
	public List<SubjectInfoItem> loadOptimizedForSubjectTracking(String expId)
		{
		return experimentDao.loadOptimizedWithInfoForSubjectTracking(expId);
		}
	
	
	public String experimentNameForId(String expId) 
		{ 
		return experimentDao.experimentNameForId(expId); 
		}
	
	
	public String[][] getDesignMatrix(Experiment experiment) { return experimentDao.getDesignMatrix(experiment); }


	public List<ClientSampleAliquotsBean> getExperimentAliquots(Experiment experiment) { return experimentDao.getExperimentAliquots(experiment); }

	
	public List<String> allExpIdsForAgilent()
		{
		return experimentDao.allExpIdsForAgilent();	
		}
	
	
	public Experiment loadSimplestById(String expID)
		{
		return experimentDao.loadSimplestById(expID);
		}

	
	public List<String> allExpIdsForAbsciex()
		{
		return experimentDao.allExpIdsForAbsciex();	
		}
	
	
	public Experiment getExperiments(String exp) 
		{
		if (FormatVerifier.verifyFormat(Experiment.fullIdFormat,exp.toUpperCase()))
			return loadById(exp);
		
		return loadById(StringParser.parseId(exp));
		}

	
	public Experiment loadById(String id)
		{
		Assert.notNull(id);
	
		return experimentDao.loadById(id);
		}

	public Experiment loadByIdWithProject(String id)
		{
		Assert.notNull(id);
		return experimentDao.loadByIdWithProject(id);
		}
	
	public Experiment loadByName(String expName)
		{
		Assert.notNull(expName);
		return experimentDao.loadByName(expName);
		}
	
	
	public Experiment loadExperimentWithInfoForInventoryReport(String expId)
		{
		return experimentDao.loadExperimentWithInfoForInventoryReport(expId);
		}
	
	
    public Experiment save(ExperimentDTO dto)
		{
		Assert.notNull(dto);
		
		Project proj; 
		try 
			{
			proj = projectDao.loadById(dto.getProjID());
			proj.getContactPersonName();
			}
		catch (Exception e)
			{
			throw new RuntimeException("Project field cannot be empty and must refer to an existing project");
			}
		
		Priority priority = experimentDao.loadPriorityByID(dto.getPriority());
		
        Experiment exp = null;
        String creator = ((MedWorksSession) Session.get()).getCurrentUserId();

  
        if (StringUtils.isEmptyOrNull(dto.getExpID()) || dto.getExpID().equals("to be assigned"))
        	{
        	if (experimentDao.checkExpNameExists(dto.getExpName()))
				throw new RuntimeException ("Duplicate : Experiment " + dto.getExpName() + " already exists - please choose another name");
            // Issue 206
			try
                {
                exp = Experiment.instance(dto.getExpName(),proj, dto.getExpDescription(), priority, dto.getNotes(), userDao.loadById(creator), dto.getServiceRequest(), dto.getIsChear());
		        experimentDao.createExperiment(exp);
                }              
            catch (Exception e) { e.printStackTrace(); exp = null; } 
		    }
		else
			{
			if (experimentDao.checkExpNameExistsAndIsNotSameItem(dto.getExpName(), dto))
				throw new RuntimeException ("Duplicate :  Another experiment with name " + dto.getExpName() + " already exists - please choose another name");

			try
				{
			    exp = experimentDao.loadById(dto.getExpID());
			    exp.update(dto, proj, priority, userDao.loadById(creator)); 
			 	} 
			catch (Exception e) { e.printStackTrace(); exp = null; } 
			}
        return exp;
		}

    
	public void saveShortcodeNew(ShortcodeDTO sc) 
		{
		Shortcode scode;
		try
			{
			scode=getExperimentShortcode(sc.getCode(), sc.getExp().getExpID());
			if (scode==null)
				{
				scode = Shortcode.instance(sc.getCode(), sc.getNIH_GrantNumber(), sc.getExp(), sc.getNIH_GrantNumber_2(), sc.getNIH_GrantNumber_3());
				experimentDao.saveShortcode(scode);
				}
			}
		catch (Exception e){    }
		}

	
	public void saveShortcode(ShortcodeDTO sc) 
		{
		Shortcode scode;
		try
			{
			scode=getExperimentShortcode(sc.getCode(), sc.getExp().getExpID());
			if (scode==null)
				{
				scode = Shortcode.instance(sc.getCode(), sc.getNIH_GrantNumber(), sc.getExp(), sc.getNIH_GrantNumber_2(), sc.getNIH_GrantNumber_3());
				experimentDao.saveShortcode(scode);
				}
			}
		catch (Exception e)	{ throw new RuntimeException("Error while saving shortcode " + sc.getCode() + ". Please make sure shortcode has no more than 20 characters and that grant number has no more than 100 characters");  }
		}

	
	public Integer countSamplesAsInt(String expId)
		{
		String ct = experimentDao.countSamples(expId);
		Integer intCt = 0;
		try 
			{
			intCt = Integer.parseInt(ct);
			}
		catch (Exception e)  { throw e; }
		
		return intCt;
		}
	
	public void updateExperiment(String id, ExperimentDTO dto)
		{
		Assert.notNull(id);
		Assert.notNull(dto);
		
		Experiment exp = experimentDao.loadById(id);
		Project proj=projectDao.loadById(dto.getProjID());
		Priority priority = experimentDao.loadPriorityByID(dto.getPriority());
		String creator = ((MedWorksSession) Session.get()).getCurrentUserId();
		exp.update(dto, proj, priority, userDao.loadById(creator));
		}

	
	public String countSamples(String expId)
		{
		return experimentDao.countSamples(expId);
		}
	

	public boolean isNameLoad(String exp)
		{
		return (!FormatVerifier.verifyFormat(Experiment.fullIdFormat,exp.toUpperCase())) && StringParser.parseId(exp).equals("");	
		}
	
	
	public boolean isValidExperimentSearch(String exp) 
		{
		Experiment ex;
		if (StringUtils.isEmptyOrNull(exp))
			return false;
		
		if (FormatVerifier.verifyFormat(Experiment.fullIdFormat,exp.toUpperCase()))
			try{   ex= loadById(exp); }
			catch(EmptyResultDataAccessException e){  ex=null; }
		else
			try { ex= loadById(StringParser.parseId(exp)); }
		 	catch(EmptyResultDataAccessException e){ ex=null; }
		
		return (ex!=null);
		}
	
	
	public List<String> allActiveExperimentNames() 
		{
		List<Experiment> expList =  allActiveExperiments();
		List<String> namesList = new ArrayList<String>();
		
		for (int i = 0; i < expList.size(); i++)
			namesList.add(expList.get(i).getExpID());
	
		return namesList;
		}

	
	public Boolean invalidateSamples(String expId)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.fullIdFormat, expId.toUpperCase()))) 
				return false;
		
		try { experimentDao.invalidateSamples(expId); }
		catch (Exception e)
			{
			e.printStackTrace();
			return false;
			}
		
		return true;
		}
	
	
	public List<String> getGrantInfo(String expId)
		{
		List <String> grantSources = experimentDao.getGrantInfo(expId);
		
		if (grantSources == null)
			grantSources = new ArrayList<String>();
		
		if (grantSources.size() == 0)
		   grantSources.add(new String("")); 
		
		return grantSources;
		}
	
	
	public String getExperimentIdForSearchString(String str)
		{
		return getExperimentIdForSearchString(str, " for search string");
		}
	
	
	public String getExperimentIdForSearchString(String str, String label)
		{
		if (str == null) 
			throw new RuntimeException("Experiment string string can't be null");
		
		String expId = str;
		if(!FormatVerifier.verifyFormat(Experiment.fullIdFormat,str.toUpperCase()))
			expId = StringParser.parseId(str);
		
		if(!FormatVerifier.verifyFormat(Experiment.fullIdFormat,expId.toUpperCase()))
			{
			try  
				{
				Experiment exp = experimentDao.loadByName(str);
				expId = exp.getExpID(); 
				}
			catch (Exception e) { throw new RuntimeException("Experiment load error : cannot find experiment  " + label + " "  + str);  }
			}
		
		return expId;
		} 
	
	
	
	public List<ExperimentInventoryInfo> completedExperiments()
		{
		List<String> allExp = this.expIdsByInceptionDate();
		List<ExperimentInventoryInfo> completedExp = new ArrayList<ExperimentInventoryInfo>();
		
		int i = 0; 
		for (String id : allExp)
			{
			i++;
			if (i > 100)
				break;
			
			if (isExperimentComplete(id) )
				{
				ExperimentInventoryInfo info = new ExperimentInventoryInfo();
				Experiment exp = loadExperimentWithInfoForInventoryReport(id);
				
				info.setCompletionDate(clientReportDao.dateOfLastReport(id));
				
				info.setExpId(id);
				info.setClientId(exp.getProject().getClient().getLab());
				info.setContactName(exp.getProject().getContactPersonName());
				info.setContactPhone(exp.getProject().getContactPerson().getEmail());
				info.setPiName(exp.getProject().getClient().getInvestigatorName());
				info.setPiPhone(exp.getProject().getClient().getInvestigator().getEmail());
				
				List<Sample> samples = exp.getSampleList();
				Collections.sort(samples, new SampleBySampleIdComparator());
				String first = samples.get(0).getSampleID();
				String last = samples.get(samples.size() - 1).getSampleID();
				info.setSamplesDescriptor(samples.size() + "  (" + first + " - " + last + ")");
				
				completedExp.add(info);
				}
			}
		
		return completedExp;
		}
	
	
	public List<ExperimentInventoryInfo> completedExperimentsInRange(Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);
		
		List<String> allExp = this.expIdsByInceptionDate();
		List<ExperimentInventoryInfo> completedExp = new ArrayList<ExperimentInventoryInfo>();
		
		for (String id : allExp)
			{
			Calendar intakeDate = experimentDao.loadSimplestById(id).getCreationDate();
			
			
			if (intakeDate != null)
				{
				int month_0 = intakeDate.get(Calendar.MONTH);
				int year_0  = intakeDate.get(Calendar.YEAR);
				
				int month_1 = toDate.get(Calendar.MONTH);
				int year_1  = toDate.get(Calendar.YEAR);
				
				int months_elapsed = (year_1 - year_0) * 12 + (month_1 - month_0);
				
				if (months_elapsed > 24)
					{
					break;
					}
				}
					
	
			if (isExperimentComplete(id) )
				{
				Calendar completionDate = clientReportDao.dateOfLastReport(id);
				
				if (completionDate.compareTo(fromDate) < 0)
					continue;
				
				if (completionDate.compareTo(toDate) > 0)
					continue;
				
				ExperimentInventoryInfo info = new ExperimentInventoryInfo();
				Experiment exp = loadExperimentWithInfoForInventoryReport(id);
				
				info.setCompletionDate(clientReportDao.dateOfLastReport(id));
				
				info.setExpId(id);
				info.setClientId(exp.getProject().getClient().getLab());
				info.setContactName(exp.getProject().getContactPersonName());
				info.setContactPhone(exp.getProject().getContactPerson().getEmail());
				info.setPiName(exp.getProject().getClient().getInvestigatorName());
				info.setPiPhone(exp.getProject().getClient().getInvestigator().getEmail());
				
				List<Sample> samples = exp.getSampleList();
				Collections.sort(samples, new SampleBySampleIdComparator());
				String first = samples.get(0).getSampleID();
				String last = samples.get(samples.size() - 1).getSampleID();
				info.setSamplesDescriptor("(" + first + " - " + last + ")");
				info.setSampleCount(samples.size());
				completedExp.add(info);
				}
			}
		
		Collections.sort(completedExp, new ExperimentInventoryByDateComparator());
		
		return completedExp;
		}
	
	
	public boolean isExperimentComplete(String expId)
		{
		List<String> assaysForExpId = assayDao.allAssayIdsForExpId(expId, false);
		if (assaysForExpId.size() == 0)
			return false;
		
		List<String> submittedReportsForExpId = clientReportDao.assaysReportedForExpId(expId);
		return (assaysForExpId.size() == submittedReportsForExpId.size());
		}
	
	public void setAssayDao(AssayDAO assayDao) { this.assayDao = assayDao; }
	public void setClientReportDao(ClientReportDAO clientReportDao) { this.clientReportDao = clientReportDao; }
	public void setProjectDao(ProjectDAO projectDao) { this.projectDao = projectDao; }
	public void setExperimentDao(ExperimentDAO experimentDao) { this.experimentDao = experimentDao; }
	public void setUserDao(UserDAO userDao) { this.userDao = userDao; }
	}

//////////////////////////////////////////
//ControlService.java
//Written by Jan Wigginton March 2015
/////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ControlDAO;
import edu.umich.brcf.shared.layers.dao.ControlTypeDAO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.IdGeneratorDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.Control;
import edu.umich.brcf.shared.layers.domain.ControlType;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.ControlDTO;
import edu.umich.brcf.shared.util.FormatVerifier;



@Transactional
public class ControlService 
	{
	ControlDAO controlDao;
	AssayDAO assayDao;
	ExperimentDAO expDao;
	ControlTypeDAO controlTypeDao;
	IdGeneratorDAO idGeneratorDao;
	
	
	public Control save(ControlDTO dto)
		{
		Assert.notNull(dto);
		Experiment exp = expDao.loadById(dto.getExpId());
		ControlType ct = controlTypeDao.loadById(dto.getControlTypeId());
		Assay assay = assayDao.loadById(dto.getAssayId());
		
		Control control = null;
		
		if (dto.getControlId() != null)
			try 
				{ control = controlDao.loadById(dto.getControlId());
				//sample.update(dto, exp,gs,ss,st, group, parent);
				}
			catch (Exception e) { control = null; }
		else
			try
				{
				control = Control.instance(dto.getControlId(), exp, assay, dto.getControlId());
				controlDao.createControl(control);
				}
			catch (Exception e) {  control = null; }
		
		return control;
		}
	
	
	public int saveControls(List<ControlDTO> controlDtoList) 
		{
		List<Control> controls = new ArrayList<Control>();
		
		for (ControlDTO dto:controlDtoList)
		{
		String cid = dto.getControlId();
		String eid = dto.getExpId();
		String aid = dto.getAssayId();
		String ctid = dto.getControlTypeId();
		
		Control control = Control.instance(cid, expDao.loadById(eid), assayDao.loadById(aid), ctid);
		controlDao.createControl(Control.instance(cid, expDao.loadById(eid), assayDao.loadById(aid), ctid));
		controls.add(control);
		}
	
	return controls.size();
	}
	
	
	public List<String> allControlIdsForExpId(String eid)
		{
		return controlDao.allControlNamesAndIdsForExpId(eid);
		}
		
	
	public List<String> allControlNamesAndIdsForExpIdAndAssayId(String eid, String aid)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.fullIdFormat,eid.toUpperCase()) && FormatVerifier.verifyFormat(Assay.fullIdFormat, aid.toUpperCase())))
		return new ArrayList<String>();
		
		String platformId = assayDao.platformIdForAssayId(aid);
		
		return controlDao.allControlNamesAndIdsForExpIdAndAssayId(eid, aid, platformId);
		}
	
	
	public List<String> allControlNamesAndIdsForPlatformAndExpId(String eid, String platform)
		{
		return controlDao.allControlNamesAndIdsForExpIdAndPlatformId(eid, platform.equalsIgnoreCase("absciex") ? "PL002" : "PL001");
		}
	
	public List<String> allControlNamesAndIdsForExpIdAndAbsciex(String eid)
		{
		return allControlNamesAndIdsForPlatformAndExpId(eid, "absciex");
		}
	
	public List<String> allControlNamesAndIdsForExpIdAndAgilent(String eid)
		{
		return allControlNamesAndIdsForPlatformAndExpId(eid,  "agilent");
		}
	
	public String controlIdForNameAndAgilent(String name)
		{
		return controlDao.controlIdForNameAndAgilent(name);
		}
	
	
	public String controlIdForExpIdAssayIdAndControlTypeId(String eid, String aid, String ctid)
		{
		return controlDao.controlIdForExpIdAssayIdAndControlTypeId(eid, aid, ctid);
		}
	
	public String testPoolControlIdForExpIdAndAssayId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT001");
		}
	
	public String blankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT002");
		}
	
	public String pooledPlasmaControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT003");
		}
	
	public String standardMixControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT004");
		}
	
	public String dropStringForIdAndAgilent(String id)
		{
		return controlDao.dropStringForIdAndAgilent(id);
		}
	
	public String processBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT005");
		}
	
	public String solventBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT006");
		}
	
	public String matrixBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT007");
		}
	
	public String neatBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT008");
		}
	
	public String poolIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT009");
		}
	
	public String getNextControlID()
		{
		return ((String) idGeneratorDao.getNextValue("Control"));
		}
		
	public void setControlDao(ControlDAO controlDao) 
		{
		Assert.notNull(controlDao);
		this.controlDao = controlDao;
		}
	
	public void setIdGeneratorDao(IdGeneratorDAO idGeneratorDao) 
		{
		Assert.notNull(idGeneratorDao);
		this.idGeneratorDao = idGeneratorDao;
		}
	
	public void setExperimentDao(ExperimentDAO experimentDao) 
		{
		Assert.notNull(experimentDao);
		this.expDao = experimentDao;
		}
	
	public void setControlTypeDao(ControlTypeDAO controlTypeDao) 
		{
		Assert.notNull(controlTypeDao);
		this.controlTypeDao = controlTypeDao;
		}
	
	public void setAssayDao(AssayDAO assayDao)
		{
		Assert.notNull(assayDao);
		this.assayDao = assayDao;
		}
	}

/*

@Transactional
public class ControlService 
	{
	ControlDAO controlDao;
	AssayDAO assayDao;
	ExperimentDAO expDao;
	ControlTypeDAO controlTypeDao;
	IdGeneratorDAO idGeneratorDao;
	
	
	public Control save(ControlDTO dto)
		{
		Assert.notNull(dto);
		Experiment exp = expDao.loadById(dto.getExpId());
		ControlType ct = controlTypeDao.loadById(dto.getControlTypeId());
		Assay assay = assayDao.loadById(dto.getAssayId());
		
		Control control = null;
		
		if (dto.getControlId() != null)
			try 
				{ control = controlDao.loadById(dto.getControlId());
				//sample.update(dto, exp,gs,ss,st, group, parent);
				}
			catch (Exception e) { control = null; }
		else
			try
				{
				control = Control.instance(dto.getControlId(), exp, assay, dto.getControlId());
				controlDao.createControl(control);
				}
			catch (Exception e) {  control = null; }
		
		return control;
		}

	
	public int saveControls(List<ControlDTO> controlDtoList) 
		{
		List<Control> controls = new ArrayList<Control>();
		
		for (ControlDTO dto:controlDtoList)
			{
			String cid = dto.getControlId();
			String eid = dto.getExpId();
			String aid = dto.getAssayId();
			String ctid = dto.getControlTypeId();
							
			Control control = Control.instance(cid, expDao.loadById(eid), assayDao.loadById(aid), ctid);
			controlDao.createControl(Control.instance(cid, expDao.loadById(eid), assayDao.loadById(aid), ctid));
			controls.add(control);
			}
		
        return controls.size();
		}
	
	
	public List<String> allControlIdsForExpId(String eid)
		{
		return controlDao.allControlNamesAndIdsForExpId(eid);
		}

	
	public List<String> allControlNamesAndIdsForExpIdAndAssayId(String eid, String aid)
		{
		if (!(FormatVerifier.verifyFormat(Experiment.fullIdFormat,eid.toUpperCase()) && FormatVerifier.verifyFormat(Assay.fullIdFormat, aid.toUpperCase())))
				return new ArrayList<String>();
			
		String platformId = assayDao.platformIdForAssayId(aid);
		
		return controlDao.allControlNamesAndIdsForExpIdAndAssayId(eid, aid, platformId);
		}
	
	
	public List<String> allControlNamesAndIdsForPlatformAndExpId(String eid, String platform)
		{
		return controlDao.allControlNamesAndIdsForExpIdAndPlatformId(eid, platform.equalsIgnoreCase("absciex") ? "PL002" : "PL001");
		}
	
	public List<String> allControlNamesAndIdsForExpIdAndAbsciex(String eid)
		{
		return allControlNamesAndIdsForPlatformAndExpId(eid, "absciex");
		}
		
	public List<String> allControlNamesAndIdsForExpIdAndAgilent(String eid)
		{
		return allControlNamesAndIdsForPlatformAndExpId(eid,  "agilent");
		}
		
	public String controlIdForExpIdAssayIdAndControlTypeId(String eid, String aid, String ctid)
		{
		return controlDao.controlIdForExpIdAssayIdAndControlTypeId(eid, aid, ctid);
		}

	public String testPoolControlIdForExpIdAndAssayId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT001");
		}
	
	public String blankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT002");
		}
	
	public String pooledPlasmaControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT003");
		}
	
	public String standardMixControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT004");
		}
	
	public String processBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT005");
		}
	
	public String solventBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT006");
		}
	
	public String matrixBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT007");
		}
	
	public String neatBlankControlIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT008");
		}
	
	public String poolIdForExpId(String eid, String aid)
		{
		return controlIdForExpIdAssayIdAndControlTypeId(eid, aid, "CT009");
		}
	
	public String getNextControlID()
		{
		return ((String) idGeneratorDao.getNextValue("Control"));
		}
	
	public void setControlDao(ControlDAO controlDao) 
		{
		Assert.notNull(controlDao);
		this.controlDao = controlDao;
		}
	
	public void setIdGeneratorDao(IdGeneratorDAO idGeneratorDao) 
		{
		Assert.notNull(idGeneratorDao);
		this.idGeneratorDao = idGeneratorDao;
		}
	
	public void setExperimentDao(ExperimentDAO experimentDao) 
		{
		Assert.notNull(experimentDao);
		this.expDao = experimentDao;
		}

	public void setControlTypeDao(ControlTypeDAO controlTypeDao) 
		{
		Assert.notNull(controlTypeDao);
		this.controlTypeDao = controlTypeDao;
		}
	
	public void setAssayDao(AssayDAO assayDao)
		{
		Assert.notNull(assayDao);
		this.assayDao = assayDao;
		}
	
	public String controlIdForNameAndAgilent(String name)
		{
		return controlDao.controlIdForNameAndAgilent(name);
		}
	
	
	public String dropStringForIdAndAgilent(String id)
		{
		return controlDao.dropStringForIdAndAgilent(id);
		}
	}
*/
////////////////////////////////////////////////////
// ProtocolSheetService.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ProtocolSheetDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.util.io.StringUtils;



@Transactional
public class ProtocolSheetService
	{
	ProtocolSheetDAO protocolSheetDao;
	AssayDAO assayDao;
	UserDAO userDao;

	public void deleteSheet(String id)
		{
		protocolSheetDao.deleteSheet(id);
		}
	
	public ProtocolSheet update(ProtocolSheetDTO dto)
		{
		Assert.notNull(dto);		
		ProtocolSheet sheet = null;
		try
			{
			sheet  = protocolSheetDao.loadById(dto.getId());
			sheet.update(dto);
			}
		catch (Exception e) { e.printStackTrace(); sheet = null; }
		return sheet;
		}

	public ProtocolSheet save(ProtocolSheetDTO dto) 
		{
		Assert.notNull(dto);
		ProtocolSheet sheet = null; 
		if (StringUtils.isEmptyOrNull(dto.getId()) || "to be determined".equals(dto.getId()))
			try
				{
				sheet = ProtocolSheet.instance(dto.getAssayId(), dto.getExperimentId(), dto.getRecordedDate(), 
					dto.getRecordedBy(), dto.getnCellPlates(), dto.getLocationId(), dto.getNotes(), dto.getnSamples(),
					dto.getExtractVolume(), dto.getExtractVolUnits(), dto.getSampleType(), dto.getProtocolDocumentId());
				protocolSheetDao.createProtocolSheet(sheet);
				dto.setId(sheet.getId()); // Issue 236
				}
			catch (Exception ee)  
				{ 
				ee.printStackTrace();  
				throw new RuntimeException("Error while saving protocol sheet. Please be sure that the number of cell plates and the number of samples are integer values");
				}
		else
			try
				{
				sheet  = protocolSheetDao.loadById(dto.getId());
				sheet.update(dto);
				}
			catch (Exception e) { System.out.println("catching update exception");
			e.printStackTrace(); sheet = null; }
		return sheet;
		}
	
	public ProtocolSheetDAO getProtocolSheetDao()
		{
		return protocolSheetDao;
		}
	
	public void setProtocolSheetDao(ProtocolSheetDAO protocolSheetDao)
		{
		this.protocolSheetDao = protocolSheetDao;
		}
		
	public List<ProtocolSheetDTO> loadDTOsForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		List<ProtocolSheet> lst = protocolSheetDao.loadForUploadDateRange(fromDate, toDate);
		List<ProtocolSheetDTO> dtos = new ArrayList<ProtocolSheetDTO>();	
		for (ProtocolSheet sheet : lst)
			dtos.add(ProtocolSheetDTO.instance(sheet));
		return dtos;
		}
	
	public List<ProtocolSheet> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return protocolSheetDao.loadForUploadDateRange(fromDate, toDate);
		}
	
	public List<ProtocolSheet> loadForExpId(String expId)
		{
		return protocolSheetDao.loadForExpId(expId);
		}
	
	// issue 245
	public List<ProtocolSheet> loadForExpId(String expId, boolean excludeDeleteFlag)
	    {
	    return protocolSheetDao.loadForExpId(expId, excludeDeleteFlag);
	    }
		
	public List<ProtocolSheetDTO> loadDTOsForExpId(String expId)
		{
		List<ProtocolSheet> lst =  protocolSheetDao.loadForExpId(expId);
		List<ProtocolSheetDTO> dtos = new ArrayList<ProtocolSheetDTO>();		
		for (ProtocolSheet sheet : lst)
			dtos.add(ProtocolSheetDTO.instance(sheet));		
		return dtos;
		}
		
	public String getDescriptorString(ProtocolSheet rep)
		{
		String assayId = rep.getAssayId();
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();		
		final String userName = userDao.getFullNameByUserId(rep.getRecordedBy());
		String st = rep.getSampleType();		
		return  StringUtils.capitalize(assayName) + "for sample type " + st + " " + System.getProperty("line.separator") +  " last edited " +  rep.getDateCreatedStr() + " by " + userName;
	    }

	public AssayDAO getAssayDao()
		{
		return assayDao;
		}

	public void setAssayDao(AssayDAO assayDao)
		{
		this.assayDao = assayDao;
		}

	public UserDAO getUserDao()
		{
		return userDao;
		}

	public void setUserDao(UserDAO userDao)
		{
		this.userDao = userDao;
		}
	}

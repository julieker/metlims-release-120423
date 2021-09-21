////////////////////////////////////////////////////
// ProtocolReportService.java
// Written by Jan Wigginton, Nov 10, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.service;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ProtocolReportDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.dto.ProtocolReportDTO;
import edu.umich.brcf.shared.util.utilpackages.MimeTypeUtils;


@Transactional
public class ProtocolReportService
	{
	AssayDAO assayDao;
	ProtocolReportDAO protocolReportDao;
	UserDAO userDao;
	
	// issue 441
	public Map<String, ProtocolReport> loadSmallDocByByIdForExpIdNotDeleted(String expId) 
		{
		return protocolReportDao.loadSmallDocByIdForExpIdNotDeleted(expId);
		}
	
	// 441
	public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId, Boolean forPreps) 
		{
		return protocolReportDao.loadIdNameMapByExpIdNotDeleted(expId);
		}
	
	// issue 441
	public Map <String, String> getProtocolDescriptors(List <String> lstProtocolIds)
		{
		Map<String, String> protocolDescriptorsMap = new HashMap <String, String> ();
		for (String sdesc : lstProtocolIds)
			protocolDescriptorsMap.put(sdesc, getDescriptorString(sdesc));
        return protocolDescriptorsMap;
		}
	
	// issue 441
	public Map <String, String> getLoadedBy(List <String> lstProtocolIds)
		{
		Map<String, String> protocolLoadedByMap = new HashMap <String, String> ();
		for (String sdesc : lstProtocolIds)
			protocolLoadedByMap.put(sdesc, getUploadedBy(sdesc));
	    return protocolLoadedByMap;
		}

	public List<ProtocolReport> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return protocolReportDao.loadForUploadDateRange(fromDate, toDate);
		}

	public List<ProtocolReportDTO> loadInfoForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return protocolReportDao.loadInfoForRunDateRange(fromDate, toDate);
		}
	
	// Issue 176
	public List<Object[]> getMissingProtocols (Calendar fromDate, Calendar toDate)
		{
		return protocolReportDao.getMissingProtocols(fromDate, toDate);
		}
	
	// Issue 245
	public void deleteProtocolReport(long reportId)
        {
        protocolReportDao.deleteProtocolReport(reportId);
        }

	// Issue 245
	public List<ProtocolReport> loadByExpId(String id)
		{
		return protocolReportDao.loadByExpId(id);
		}
	
	// Issue 245
	public List<ProtocolReport> loadByExpId(Experiment exp, boolean excludeDeleted)
	    {
	    return protocolReportDao.loadByExpId(exp, excludeDeleted);
	    }

	public ProtocolReport loadByReportId(String id)
		{
		return protocolReportDao.loadByReportId(id);
		}
	
	public List<ProtocolReport> loadAll()
		{
		return protocolReportDao.loadAll();
		}

	public String getDescriptorString(ProtocolReport rep)
		{
		String assayId = rep.getAssayId();
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();
		
		final String userName = userDao.getFullNameByUserId(rep.getLoadedBy());
		
		return  StringUtils.capitalize(assayName) +  System.getProperty("line.separator") +  " uploaded " +  rep.getDateCreatedStr() + " by " + userName;
	    }
	
	// issue 441
	public String getDescriptorString(String reportId)
		{
		List <String> descriptionList =  protocolReportDao.getDescriptorList(reportId);
		String assayId = descriptionList.get(0).toString();
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();
		final String userName = userDao.getFullNameByUserId(descriptionList.get(1).toString());
		return  StringUtils.capitalize(assayName) +  System.getProperty("line.separator") +  " uploaded " +  descriptionList.get(2).toString() + " by " + userName;
		}
	
	// issue 441
	public String getUploadedBy(String reportId)
		{
		List <String> descriptionList =  protocolReportDao.getDescriptorList(reportId);
		return descriptionList == null ? null :descriptionList.get(1).toString();
		}
	
	public String getPrettyReportName(ProtocolReport rep)
		{
		StringBuilder builder = new StringBuilder();

		String assayId = rep.getAssayId();
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();

		if (!(rep.getExp() == null))
			builder.append(rep.getExp().getExpID() + "_");

		if (!assayName.equals(""))
			builder.append(assayName + "_");

		String dateCreatedStr = rep.getDateCreatedStr();
		if (!(dateCreatedStr == null) && !dateCreatedStr.equals(""))
			builder.append(dateCreatedStr);

		String extension = MimeTypeUtils.getExtensionForMimeType(rep
				.getFileType());
		if (extension != null && !extension.trim().equals(""))
			builder.append("." + extension);

		String prettyName = builder.toString();
		if (prettyName.length() < 12)
			return rep.getFileName();

		return prettyName;
		}
	

	public String getNiceReportName(ProtocolReport rep)
		{
		String name = rep.getFileName();
		String[] tokens = name.split("\\.");
		if (tokens.length > 1)
			return name;

		String extension = MimeTypeUtils.getExtensionForMimeType(rep.getFileType());
		if (extension != null && !extension.trim().equals(""))
			name += "." + extension;

		return name;
		}
	

	public AssayDAO getAssayDao()
		{
		return assayDao;
		}

	
	public void setAssayDao(AssayDAO assayDao)
		{
		this.assayDao = assayDao;
		}

	
	public ProtocolReportDAO getProtocolReportDao()
		{
		return protocolReportDao;
		}

	public void setProtocolReportDao(ProtocolReportDAO protocolReportDao)
		{
		this.protocolReportDao = protocolReportDao;
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


///////////////////////////////////////////
// Writtten by Anu Janga
// Revisited by Jan Wigginton June  2016
///////////////////////////////////////////

package edu.umich.brcf.shared.layers.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.umich.brcf.shared.layers.dao.AssayDAO;
import edu.umich.brcf.shared.layers.dao.ClientReportDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.util.utilpackages.MimeTypeUtils;


@Transactional
public class ClientReportService
	{
	AssayDAO assayDao;
	ClientReportDAO clientReportDao;
	UserDAO userDao;

	
	// issue 441
	public Map<String, ClientReport> loadSmallDocByByIdForExpIdNotDeleted(String expId) 
		{
		return clientReportDao.loadSmallDocByIdForExpIdNotDeleted(expId);
		}
	
	// issue 441
	public ClientReport loadById(String documentId)
		{
		return clientReportDao.loadById(documentId);
		}
	
	public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId) 
		{
		return clientReportDao.loadIdNameMapByExpIdNotDeleted(expId);
		}
	
	// issue 441
	public Map <String, String> getRepDescriptors(List <String> lstRepIds)
		{
		Map<String, String> repDescriptorsMap = new HashMap <String, String> ();
		for (String sdesc : lstRepIds)
			repDescriptorsMap.put(sdesc, getDescriptorString(sdesc));
        return repDescriptorsMap;
		}
	
	// issue 441
	public Map <String, String> getLoadedBy(List <String> lstRepIds)
		{
		Map<String, String> repLoadedByMap = new HashMap <String, String> ();
		for (String sdesc : lstRepIds)
			repLoadedByMap.put(sdesc, getUploadedBy(sdesc));
	    return repLoadedByMap;
		}
	
	public List<ClientReport> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return clientReportDao.loadForUploadDateRange(fromDate, toDate);
		}

	public List<ClientReportDTO> loadInfoForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		return clientReportDao.loadInfoForRunDateRange(fromDate, toDate);
		}

	public List<ClientReport> loadByExpId(String id)
		{
		return clientReportDao.loadByExpId(id);
		}
	
	// Issue 245
	public List<ClientReport> loadByExpId(String id, boolean excludeDeleted)
	    {
	    return clientReportDao.loadByExpId(id, excludeDeleted);
	    }

	public ClientReport loadByReportId(String id)
		{
		return clientReportDao.loadByReportId(id);
		}

	// Issue 245
	public void deleteClientReport(long reportId)
	    {
		clientReportDao.deleteClientReport(reportId);
	    }
	
	public List<ClientReport> loadAll()
		{
		return clientReportDao.loadAll();
		}

	// issue 441
	public String getDescriptorString(String reportId)
		{
		List <String> descriptionList =  clientReportDao.getDescriptorList(reportId);
		//String assayId = descriptionList.get(0).toString();
		String assayId = descriptionList.get(0); // issue 479
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();
		final String userName = userDao.getFullNameByUserId(descriptionList.get(1).toString());
		return  StringUtils.capitalize(assayName) +  System.getProperty("line.separator") +  " uploaded " +  descriptionList.get(2).toString() + " by " + userName;
		}
	
	// issue 441
	public String getUploadedBy(String reportId)
		{
		List <String> descriptionList =  clientReportDao.getDescriptorList(reportId);
		return descriptionList == null ? null :descriptionList.get(1).toString();
		}
	
	public String getDescriptorString(ClientReport rep)
		{
		String assayId = rep.getAssayId();
		Assay assay = assayId == null ? null : assayDao.loadAssayByID(assayId);
		String assayName = assay == null ? "" : assay.getAssayName();
		
		final String userName = userDao.getFullNameByUserId(rep.getLoadedBy());
		
		return  StringUtils.capitalize(assayName) +  System.getProperty("line.separator") +  " uploaded " +  rep.getDateCreatedStr() + " by " + userName;
	    }
	
	
	public String getPrettyReportName(ClientReport rep)
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
	

	public String getNiceReportName(ClientReport rep)
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

	public ClientReportDAO getClientReportDao()
		{
		return clientReportDao;
		}

	public void setClientReportDao(ClientReportDAO clientReportDao)
		{
		this.clientReportDao = clientReportDao;
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

package edu.umich.brcf.shared.layers.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.utilpackages.FileUtils;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;


public class ClientReportDAO extends BaseDAO
	{

	public List<ClientReport> loadAll()
		{
		List<ClientReport> reportList = getEntityManager().createQuery("from ClientReport order by reportId desc").getResultList();

		for (ClientReport set : reportList)
			initializeTheKids(set, new String[] { "experiment" });

		return reportList;
		}

	// createNative
	public ClientReport loadByReportId(String id)
		{
		Long id2 = Long.parseLong(id);
	
		List<ClientReport> lst = getEntityManager().createQuery("from ClientReport d where d.reportId = :id2")
				.setParameter("id2", id2).getResultList();
		
		for (ClientReport set : lst)
			initializeTheKids(set, new String[] { "exp", "contents" });

		return (lst == null || lst.size() == 0 ? new ClientReport() : lst.get(0));
		}
	
	// Issue 245
	public void deleteClientReport(long reportId)
	    {
	    ClientReport clientReport = getEntityManager().find(ClientReport.class, reportId);
	    clientReport.setDeleted();
	    }
	
	// Issue 441
	public ClientReport loadById(String sReportId)
	    {
	    ClientReport clientReport = getEntityManager().find(ClientReport.class, Long.parseLong(sReportId));
	    return clientReport;
	    }
	
	
	// Issue 245	
	public List<ClientReport> loadByExpId (String id)
	    {
		return loadByExpId(id, false);
	    }
	
	// issue 245
	public List<ClientReport> loadByExpId(String id, boolean excludeDeleted)
		{
		List<ClientReport> lst = null; 
		
		try
			{
			 if (!excludeDeleted)
		         lst = getEntityManager().createQuery("from ClientReport d where d.exp.expID = :id")
				.setParameter("id", id).getResultList();
			 else
				 lst = getEntityManager().createQuery("from ClientReport d where d.exp.expID = :id and (d.deletedFlag is null or d.deletedFlag = false) ")
					.setParameter("id", id).getResultList();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return new ArrayList<ClientReport>();
			}
		
		return lst;
		}
	
	
	//////////

	// issue 441
	public Map<String, ClientReport> loadSmallDocByIdForExpIdNotDeleted(String expId) 
	    {
		String queryStr =  "from ClientReport d where d.deletedFlag is null and d.exp.expID = ?1 and dbms_lob.getlength(contents) < " + FieldLengths.SMALL_DOCUMENT_LIMIT;
	    List<ClientReport> docs =  getEntityManager().createQuery(queryStr).setParameter(1, expId).getResultList();	    
	    Map<String, ClientReport> smallDocMap = new HashMap<String, ClientReport>();
	    if (docs != null)
	    	for (ClientReport doc : docs)
	    		smallDocMap.put(doc.getReportId().toString(), doc);    	
	    return smallDocMap;		
	    }
				
	// issue 441
	public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId) 
	    {
		Query query = getEntityManager().createNativeQuery("select to_char(report_id) report_id, "
				+ "cast(file_name as VARCHAR2(150)), cast(file_type as VARCHAR2(80)), dbms_lob.getlength( REPORT_FILE) from METLIMS_LIBRARY.ANALYSIS_REPORT where deleted is null and  experiment_id = ?1");	    
	    List<Object[]> resultList  = query.setParameter(1, expId).getResultList();		
	    Map<String, String> idNameMap = new HashMap<String, String>();		
	    if (resultList != null)
		    for (Object [] obj : resultList)
				{		
				String strReport_id = "", filename = "", filetype;
				Double size;				
				if (obj.length < 4)
					continue;									
			    strReport_id = (String) obj[0];				
				filename = (String) obj[1];
				filetype = (String) obj[2];
				BigDecimal sz = (BigDecimal) obj[3];
				String inM = " ( " + sz + " bytes )"; 				
				try 
				    {
					String tag = "M";
					Double inMDbl = sz.doubleValue()/ (1024.0 * 1024.0);
					if (inMDbl < 1.0)
						{
						inMDbl *= 1024.0;
						tag = "K";
						}
					inM = "    ( Size : " + String.format("%.1f", inMDbl) + tag +" )";
					}
				catch (Exception e)  { System.out.println("Error while parsing size"); }				
				idNameMap.put(strReport_id, FileUtils.getNiceName(filename, filetype, inM));
				}		
	    return idNameMap;
	    }
		
	//////////	
	// issue 441
	public List<String> getDescriptorList (String sReportId)
		{
		Query query = getEntityManager().createNativeQuery("select ASSAY_ID, LOADED_BY, to_char(DATE_CREATED, 'MM-dd-yy') "
				+ " from METLIMS_LIBRARY.ANALYSIS_REPORT where deleted is null and  report_id  = ?1");	
		ArrayList<String> rDescStringList = new ArrayList<String>();
		List<Object[]> reportDescList = query.setParameter(1, sReportId).getResultList();
		int i = 0;
		for (Object[] reportResult : reportDescList)
			{
			if (i > 0)
				break;
			String assayId = (String) reportResult[0];	
			String loadedBy = (String) reportResult[1];
			String createDate = (String) reportResult[2];
			rDescStringList.add(assayId);
			rDescStringList.add(loadedBy);
			rDescStringList.add(createDate);						
			i++;
			}
		return rDescStringList;
		}
	
	public List<String> assaysReportedForExpId(String expId)
		{
		List<ClientReport> reports = loadByExpId(expId);
		
		if (reports == null)
			return new ArrayList<String>();
		
		List<String> assayIds = new ArrayList<String>();
		for (ClientReport report : reports)
			assayIds.add(report.getAssayId());

		if (assayIds.size() == 0)
			return assayIds;
		
		return ListUtils.uniqueEntries(assayIds);
		}
	
	
	public Calendar dateOfLastReport(String expId)
		{
		List<ClientReport> lst = getEntityManager().createQuery("from ClientReport d where d.exp.expID = :id order by d.dateCreated desc")
				.setParameter("id", expId).getResultList();
		
		if (lst.size() < 1) return null;

		return lst.get(0).getDateCreated();
		}
	
	
	public List<ClientReport> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);

		List<ClientReport> lst = getEntityManager().createQuery("from ClientReport r where r.dateCreated >= :fromDate and r.dateCreated < :toDate order by r.dateCreated desc")
				.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
		
		for (ClientReport set : lst)
			initializeTheKids(set, new String[] { "exp" });

		return lst;
		}

	
	
	
	public List<ClientReportDTO> loadInfoForRunDateRange(Calendar fromDate,
			Calendar toDate)
		{
		List<ClientReportDTO> info = new ArrayList<ClientReportDTO>();
		List<ClientReport> infoAsObjects = loadForUploadDateRange(fromDate, toDate);

		for (int i = 0; i < infoAsObjects.size(); i++)
			info.add(new ClientReportDTO(infoAsObjects.get(i)));

		return info;
		}
	}

////////////////////////////////////////////////////
// ProtocolReportDAO.java
// Written by Jan Wigginton, Nov 10, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dao;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.dto.ProtocolReportDTO;
import edu.umich.brcf.shared.util.FieldLengths;
import edu.umich.brcf.shared.util.utilpackages.FileUtils;

@Repository
public class ProtocolReportDAO extends BaseDAO
	{

	public List<ProtocolReport> loadAll()
		{
		List<ProtocolReport> reportList = getEntityManager().createQuery("from ProtocolReport order by reportId desc").getResultList();

		for (ProtocolReport set : reportList)
			initializeTheKids(set, new String[] { "experiment" });

		return reportList;
		}

	
	public ProtocolReport loadByReportId(String id)
		{
		Long id2 = Long.parseLong(id);
	
		List<ProtocolReport> lst = getEntityManager().createQuery("from ProtocolReport d where d.reportId = :id2")
				.setParameter("id2", id2).getResultList();
		
		for (ProtocolReport set : lst)
			initializeTheKids(set, new String[] { "exp", "contents" });

		return (lst == null || lst.size() == 0 ? new ProtocolReport() : lst.get(0));
		}
    
	// Issue 245
	public void deleteProtocolReport(Long reportId)
	    {
	    ProtocolReport protocolReport = getEntityManager().find(ProtocolReport.class, reportId);
	    protocolReport.setDeleted();
	    }
		
	
	public List<ProtocolReport> loadByExpId(String id)
	    {
	    List<ProtocolReport> lst = getEntityManager().createQuery("from ProtocolReport d where d.expId = :id")
			.setParameter("id", id).getResultList();
	
	    for (ProtocolReport set : lst)
		    initializeTheKids(set, new String[] { "exp" });
		
	    return lst;
	    }
	
	// Issue 245	
	public List<ProtocolReport> loadByExpId (Experiment exp)
	    {
		return loadByExpId(exp, false);
		}
		
	// issue 245
	public List<ProtocolReport> loadByExpId(Experiment exp, boolean excludeDeleted)
		{
		List<ProtocolReport> lst = null; 			
		if (!excludeDeleted)
			{
			lst = getEntityManager().createQuery("from ProtocolReport d where d.exp = :exp")
			.setParameter("exp", exp).getResultList();
			}
		else
			{
			lst = getEntityManager().createQuery("from ProtocolReport d where d.exp = :exp and (d.deletedFlag is null or d.deletedFlag = false) ")
			.setParameter("exp", exp).getResultList();
			}
		for (ProtocolReport set : lst)
			initializeTheKids(set, new String[] { "exp" });
		return lst;
		}
	
	public List<ProtocolReport> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);

		List<ProtocolReport> lst = getEntityManager().createQuery("from ProtocolReport r where r.dateCreated >= :fromDate and r.dateCreated < :toDate order by r.dateCreated desc")
				.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
		
		for (ProtocolReport set : lst)
			initializeTheKids(set, new String[] { "exp" });

		return lst;
		}

	
	public List<ProtocolReportDTO> loadInfoForRunDateRange(Calendar fromDate,
			Calendar toDate)
		{
		List<ProtocolReportDTO> info = new ArrayList<ProtocolReportDTO>();
		List<ProtocolReport> infoAsObjects = loadForUploadDateRange(fromDate, toDate);

		for (int i = 0; i < infoAsObjects.size(); i++)
			info.add(new ProtocolReportDTO(infoAsObjects.get(i)));

		return info;
		}
	
	// issue 441
	public Map<String, ProtocolReport> loadSmallDocByIdForExpIdNotDeleted(String expId) 
	    {
		String queryStr =  "from ProtocolReport d where d.deletedFlag is null and d.exp.expID = ?1 and dbms_lob.getlength(contents) < " + FieldLengths.SMALL_DOCUMENT_LIMIT;
	    List<ProtocolReport> docs =  getEntityManager().createQuery(queryStr).setParameter(1, expId).getResultList();	    
	    Map<String, ProtocolReport> smallDocMap = new HashMap<String, ProtocolReport>();
	    if (docs != null)
	    	for (ProtocolReport doc : docs)
	    		smallDocMap.put(doc.getReportId().toString(), doc);	    	
	    return smallDocMap;		
	    }
	
	// issue 441
    public Map <String, String> loadIdNameMapByExpIdNotDeleted(String expId) 
	    {
    
		Query query = getEntityManager().createNativeQuery("select to_char(report_id) report_id, "
				+ "cast(file_name as VARCHAR2(150)), cast(file_type as VARCHAR2(80)), dbms_lob.getlength( REPORT_FILE) from PROTOCOL_REPORT where deleted is null and  experiment_id = ?1");	    
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
				try {
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
//issue 176
///////////////////////
	public List<Object[]> getMissingProtocols (Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);

		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		Query query = getEntityManager().createNativeQuery("select exp_id, exp_name, creationdate "
				+ " from experiment where trunc(creationdate) between ?1 and ?2 and exp_id not in (select  EXPERIMENT_ID from PROTOCOL_REPORT where deleted is null) order by 1 ").setParameter(1, fromDate).setParameter(2, toDate);	
		ArrayList<String> missingProtocolList = new ArrayList<String>();
		List<Object[]> resultList  = query.getResultList();	    
		return resultList;
		}  
    
//////////
// issue 441
	public List<String> getDescriptorList (String sReportId)
		{
		Query query = getEntityManager().createNativeQuery("select ASSAY_ID, LOADED_BY, to_char(DATE_CREATED, 'MM-dd-yy') "
				+ " from PROTOCOL_REPORT where deleted is null and  report_id  = ?1");	
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
	}

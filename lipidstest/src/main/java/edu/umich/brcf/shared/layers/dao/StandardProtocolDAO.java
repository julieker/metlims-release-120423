////////////////////////////////////////////////////
// StandardProtocolDAO.java
// Written by Jan Wigginton, Dec 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;


@Repository
public class StandardProtocolDAO extends BaseDAO
	{
	public List<StandardProtocol> loadAllBasics()
		{
		List<StandardProtocol> reportList = getEntityManager().createQuery("from StandardProtocol order by protocolId desc").getResultList();
		return reportList;
		}
	
	public StandardProtocol loadById(String id)
		{
		Assert.notNull(id);
		
		StandardProtocol doc = getEntityManager().find(StandardProtocol.class, id);
		return doc;
		}

	
	public StandardProtocol loadLatestByAssayIdAndSampleType(String assayId, String sampleType)
		{
		List<StandardProtocol> lst = getEntityManager().createQuery("from StandardProtocol d where d.assay.assayId = ?1 and d.sampleType = ?2 order by d.startDate desc")
				.setParameter(1, assayId).setParameter(2, sampleType).getResultList();
		
		if (ListUtils.isNonEmpty(lst))
			{
			for (StandardProtocol set : lst)
				initializeTheKids(set, new String[] { "assay", "contents" });
			
			return lst.get(0);
			}
		
		return loadByProtocolId("SD0029");
		}
	
	// JAK issue 197
	public StandardProtocol loadLatestByAssayIdAndSampleTypeEfficiently(String assayId, String sampleType)
	    {
	    List<String> lst = getEntityManager().createNativeQuery("select protocol_id from standard_protocols where assay_id =?1 and sample_type = ?2 and start_date = (select max(start_date) from standard_protocols where assay_id = ?1 and sample_type =?2) ")
			.setParameter(1, assayId).setParameter(2, sampleType).getResultList();

	    if (ListUtils.isNonEmpty(lst))
		    {
		    StandardProtocol stProto = loadByProtocolId(lst.get(0));
			initializeTheKids(stProto, new String[] { "assay", "contents" });
		    return stProto;
		    }
	    return loadByProtocolId("SD0029");
	    }
	
	public StandardProtocol loadByProtocolId(String id)
		{
		Assert.notNull(id);
		
		StandardProtocol protocol = getEntityManager().find(StandardProtocol.class, id);
		if (protocol != null) 
			initializeTheKids(protocol, new String[] { "assay", "contents" });
		return protocol;
		}

	
	public StandardProtocol loadySafeByProtocolId(String protocolId)
		{
		List<StandardProtocol> lst = getEntityManager().createQuery("from StandardProtocol d where d.protocolId = ?1")
				.setParameter(1, protocolId).getResultList();
		
		for (StandardProtocol set : lst)
			initializeTheKids(set, new String[] { "assay", "contents" });
	
		return (lst == null || lst.size() == 0 ? new StandardProtocol() : lst.get(0));
		}
	
	
	public List<StandardProtocol> loadByAssayId(String id)
		{
		List<StandardProtocol> lst = null; 
		
		try
			{
		lst = getEntityManager().createQuery("from StandardProtocol d where d.assay.assayId = :id")
				.setParameter("id", id).getResultList();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return new ArrayList<StandardProtocol>();
			}
		
		return lst;
		}
	
	
	public List<StandardProtocol> loadForUploadDateRange(Calendar fromDate, Calendar toDate)
		{
		fromDate.set(Calendar.HOUR_OF_DAY, 0);
		fromDate.set(Calendar.MINUTE, 0);
		fromDate.set(Calendar.SECOND, 0);
	
		toDate.set(Calendar.HOUR_OF_DAY, 0);
		toDate.set(Calendar.MINUTE, 0);
		toDate.set(Calendar.SECOND, 0);
		toDate.roll(Calendar.DAY_OF_YEAR, 1);
	
		List<StandardProtocol> lst = getEntityManager().createQuery("from StandardProtocol r where r.startDate >= :fromDate and r.startDate < :toDate order by r.startDate desc")
				.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
		
		System.out.println("Reaturned protocols : n =" + (lst == null ? 0 : lst.size()));
		for (StandardProtocol set : lst)
			initializeTheKids(set, new String[] { "assay" });
	
		return lst;
		}
	
	
	public List<StandardProtocolDTO> loadInfoForRunDateRange(Calendar fromDate,
			Calendar toDate)
		{
		List<StandardProtocolDTO> info = new ArrayList<StandardProtocolDTO>();
		List<StandardProtocol> infoAsObjects = loadForUploadDateRange(fromDate, toDate);
	
		for (int i = 0; i < infoAsObjects.size(); i++)
			info.add(StandardProtocolDTO.instance(infoAsObjects.get(i)));
	
		return info;
		}
	
	
	public List<String>  getSampleTypesForStandardProtocolsByAssayId(String assayId)
		{
		// Issue 230 
		Query query = getEntityManager().createNativeQuery("select distinct cast(sample_type as varchar(30)) from standard_protocols where assay_id = ?1");
		query.setParameter(1, assayId);
		return query.getResultList();
		}
	}



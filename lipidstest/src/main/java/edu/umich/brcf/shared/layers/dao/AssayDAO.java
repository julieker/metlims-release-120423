package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.SampleAssay;



@Repository
public class AssayDAO extends BaseDAO
	{
	public Assay loadById(String assayId)
		{
		return (getEntityManager().find(Assay.class, assayId));
		}

	public String platformIdForAssayId(String assayId)
		{
		Assay assay = loadById(assayId);
		return assay.getPlatformId();
		}
	

	public Assay loadAssayByName(String assayName)
		{
		List<Assay> lst = getEntityManager().createQuery("from Assay a where a.assayName = :assayName").setParameter("assayName", assayName).getResultList();
		
		if (lst == null || lst.size() < 1)
			{
			lst = getEntityManager().createQuery("from Assay a where a.alternateName = :assayName").setParameter("assayName", assayName).getResultList();
			
			//if (lst.size() == 1)
			//	System.out.println("Found by alternate name " + assayName + "Assay is " + ((Assay) lst.get(0)).getAssayName() + "(" + ((Assay) lst.get(0)).getAssayId() + ")");
			}

		Assay assay = (Assay) DataAccessUtils.requiredSingleResult(lst);
		return assay;
		}

	
	public String getStandardNameForAssayName(String name)
		{
		Assay assay = loadAssayByName(name);
		return (assay == null ? "" : assay.getAssayName());
		}

	
	public void createSampleAssay(SampleAssay sampleAssay)
		{
		getEntityManager().persist(sampleAssay);
		}

	//Issue 249
	public void deleteSamplesAssociatedWithAssay (String expId, String assayID)
        {	
        Query query = getEntityManager().createNativeQuery(" delete from sample_assays where (sample_id, assay_id) in (select s.sample_id, sa.assay_id from sample s, sample_assays sa where s.sample_id = sa.sample_id and s.exp_id = ?1 and sa.assay_id = ?2  )" ).setParameter(1, expId).setParameter(2,assayID);
        query.executeUpdate();			
        }
	
	// Issue 249
	public void deleteSamplesAssociatedWithAssayNotChosen (String expId, String assayID, String samples)
        {	
        Query query = getEntityManager().createNativeQuery(" delete from sample_assays where (sample_id, assay_id) in (select s.sample_id, sa.assay_id from sample s, sample_assays sa where s.sample_id = sa.sample_id and (1,sa.sample_id) in "  + samples + " and s.exp_id = ?1 and sa.assay_id = ?2  )" ).setParameter(1, expId).setParameter(2,assayID);
        query.executeUpdate();			
        }
	
	public Assay loadAssayByID(String assayId)
		{
		return (getEntityManager().find(Assay.class, assayId));
		}

	
	public List<String> allAssayNames()
		{
		Query query = getEntityManager().createNativeQuery("select a.assay_name||' ('||a.assay_id||')' from assays a order by a.assay_id");
		List<String> assayList = query.getResultList();
		return assayList;
		}

	
	public List<String> allAssayNamesAndIds()
		{
		List<String> names = allAssayNamesForPlatform("agilent");
		List<String> other_names = allAssayNamesForPlatform("absciex");

		for (int i = 0; i < other_names.size(); i++)
			names.add(other_names.get(i));

		return names;
		}
	
	
	public List<String> allAssayNamesForPlatform(String platform)
		{
		String platId = platform.equalsIgnoreCase("absciex") ? "PL002" : "PL001";
	
		Query query = getEntityManager().createNativeQuery("select a.assay_name||' ('||a.assay_id||')' from assays a where a.platform_id = "
				+ "?1  order by a.assay_id").setParameter(1, platId);
		
		List<String> assayList = query.getResultList();
		return assayList;
		}
	
	
	public List<String> allAssayNamesForPlatformAndExpId(String platform, String eid)
		{
		List<String> smallList = new ArrayList<String>();
		smallList.add("Shotgun lipidomics (A004)");

		if (platform.equalsIgnoreCase("absciex"))
			return smallList;

		return allAssayNamesForExpId(eid);
		}


	public List<String> allAssayIdsForPlatformId(String platId)
		{
		Query query = getEntityManager().createNativeQuery("select cast(a.assay_id as VARCHAR2(4)) from Assays a where a.platform_id = ?1"
			+ " order by a.assay_id asc").setParameter(1, platId);

		List<String> assayList = query.getResultList();
		return assayList;
		}
	
	
	public List<String> allAssayNamesForExpId(String eid)
		{
		return allAssayNamesForExpId(eid, true);
		}

		
		/*
		public List<String> allAssayNamesForExpId(String eid, boolean skipAbsciex)
			{
			Query query2 = getEntityManager().createNativeQuery("select cast(a.assay_name as VARCHAR2(150)), "
						+ " cast(a.assay_id as VARCHAR2(4)) from (select s.sample_id from Sample s where s.exp_id = ?1) t"
									+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id "
									+ " inner join Assays a on sa.assay_id = a.assay_id "
									+ " group by a.assay_name, a.assay_id").setParameter(1,eid);
		
			
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
		*/
	
	// issue 249
	public List <String> samplesAssociatedWithAssay (String expId, String assayID)
        {	
        Query query = getEntityManager().createNativeQuery(" select s.sample_id from sample s, sample_assays sa where s.sample_id = sa.sample_id and s.exp_id = ?1 and sa.assay_id = ?2  " ).setParameter(1, expId).setParameter(2,assayID);
        return query.getResultList();				
        }
	
	public List<String> allAssayNamesForExpId(String eid, boolean skipAbsciex)
		{
		Query query2 = getEntityManager().createNativeQuery("select cast(a.assay_name as VARCHAR2(150)), "
								+ " cast(a.assay_id as VARCHAR2(4)) from (select s.sample_id from Sample s where s.exp_id ="
								+ "?1"
								+ ") t"
								+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id "
								+ " inner join Assays a on sa.assay_id = a.assay_id "
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
	
	// Issue 249
	public List <String> samplesTooManyAssaysForSample ( String sampleTupleStr, String assayId)
        {	
		Query query = getEntityManager().createNativeQuery(" select sample_id from assays a, sample_assays sa where (1,sa.sample_id) in " + sampleTupleStr  +  " and  a.assay_id = sa.assay_id and a.assay_id <> ?1 and assay_name != 'DNA Extraction' group by sample_id having count(*) >= 5  ").setParameter(1, assayId);
        return query.getResultList() ;				
        }
	
	public List<String> allAssayIdsForExpId(String expId, boolean skipAbsciex)
		{
		Query query2 = getEntityManager().createNativeQuery("select  cast(a.assay_id as VARCHAR2(4)) from (select s.sample_id from Sample s where s.exp_id = ?1) t"
								+ " inner join Sample_Assays sa on t.sample_id = sa.sample_id "
								+ " inner join Assays a on sa.assay_id = a.assay_id "
								+ " group by a.assay_name, a.assay_id").setParameter(1, expId);
	
		ArrayList<String> labelledAssays = new ArrayList<String>();
		return query2.getResultList();
		}


	public String getIdForAssayName(String assayName)
		{
		return loadAssayByName(assayName).getAssayId();
		}
	

	public String getNameForAssayId(String assayId)
		{
		return loadById(assayId).getAssayName();
		}
	
	// issue 123
	public List<String> loadByAssayWithAliquots()
		{
		Query query = getEntityManager().createNativeQuery("select distinct assay_name || ' (' ||  t2.assay_id || ')' from assay_aliquot t1, assays t2 where t1.assay_id = t2.assay_id and t1.aliquot_id in (select aliquot_id from aliquot where dry = '0' and deleted is null) order by 1 ");		
		List<String> assayList = query.getResultList();	
		return (assayList == null ? new ArrayList<String>() : assayList);
		}
	}







///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakSetDAO.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class Ms2PeakSetDAO extends BaseDAO
	{
	public void createMs2PeakSet(Ms2PeakSet peakSet)
		{
		getEntityManager().persist(peakSet);
		initializeTheKids(peakSet, new String[] { "samplePeaks", "dataSet" });
		}

	
	public void deleteMs2PeakSet(Ms2PeakSet peakSet)
		{
		getEntityManager().remove(peakSet);
		}

	
	public Ms2PeakSet loadById(String id)
		{
		Ms2PeakSet set2 = getEntityManager().find(Ms2PeakSet.class, id);
		initializeTheKids(set2, new String[] { "samplePeaks", "dataSet" });

		return set2;
		}


	List<Ms2PeakSet> loadForExpIdAndDate(String expId, String runDate)
		{
		List<Ms2PeakSet> lst = getEntityManager().createQuery("from Ms2PeakSet s where s.expId = :expId and s.runDate = :runDate")
					.setParameter("expId", expId).setParameter("runDate", runDate).getResultList();
		
		for (Ms2PeakSet ps : lst)
			initializeTheKids(ps, new String[] { "samplePeaks", "dataSet" });
	
		return lst;
		}

	
	public List<Ms2PeakSet> loadInitializedForDataSetId(String dataSetId)
		{
		List<Ms2PeakSet> peakList = getEntityManager().createQuery("from Ms2PeakSet s where s.dataSet.dataSetId = :dataSetId")
                 .setParameter("dataSetId", dataSetId).getResultList();
		
		return peakList;
		}

	/*
	 * public List <LipidBlastEntry> getLipidInfoRelatedTo(String codeName,
	 * String adduct) { Map<String, Object> map = new HashMap<String,Object>();
	 * map.put("codeName", codeName); map.put("adduct", adduct);
	 * 
	 * System.out.println("Check for code name " + codeName + " and adduct " +
	 * adduct); Query query =
	 * getEntityManager().createNativeQuery("select cast(lb_id as VARCHAR2(10)), "
	 * + "cast(full_name as VARCHAR2(500)), " +
	 * "cast(mol_formula as VARCHAR2(50)), " +
	 * "cast(precursor_mz as VARCHAR2(22)), " + "cast(ms_mode as VARCHAR2(1)), "
	 * + "cast(class_code as VARCHAR2(120)), " +
	 * "cast(formula_mass as VARCHAR2(22)), " +
	 * "cast(lipid_maps_class as VARCHAR2(30)) " +
	 * "from EXTERNAL_DB.Lipid_Blast_Precursor lbp " +
	 * "where lbp.code_name like '" + codeName + "%' " + "and lbp.adduct like '"
	 * + adduct + "%' ");
	 * 
	 * 
	 * List<Object[]> assayList = query.getResultList(); /*if (assayList.size()
	 * == 0) { query =
	 * getEntityManager().createNativeQuery("select cast(lb_id as VARCHAR2(10)), "
	 * + "cast(full_name as VARCHAR2(500)), " +
	 * "cast(mol_formula as VARCHAR2(50)), " +
	 * "cast(precursor_mz as VARCHAR2(22)), " + "cast(ms_mode as VARCHAR2(1)), "
	 * + "cast(class_code as VARCHAR2(120)), " +
	 * "cast(formula_mass as VARCHAR2(22)), " +
	 * "cast(lipid_maps_class as VARCHAR2(30)) " +
	 * "from EXTERNAL_DB.Lipid_Blast_Precursor lbp " +
	 * "where lbp.code_name like 'PC 40:5%'");
	 * 
	 * assayList = query.getResultList(); }
	 * 
	 * List <LipidBlastEntry> lst = new ArrayList<LipidBlastEntry>();
	 * 
	 * System.out.println("Gathering lipid info there were " + assayList.size()
	 * + " results."); for (int i = 0; i < assayList.size(); i++) { Object []
	 * assayResult = assayList.get(i); int sz = 8; LipidBlastEntry item = new
	 * LipidBlastEntry();
	 * 
	 * item.setLipidId((String) (sz > 0 ? assayResult[0] : ""));
	 * item.setFullName((String) (sz > 1 ? assayResult[1] : ""));
	 * item.setMolecularFormula((String) (sz > 2 ? assayResult[2] : ""));
	 * item.setPrecursorMz((String) (sz > 3 ? assayResult[3] : "" ));
	 * item.setMsMode((String) (sz > 4 ? assayResult[4] : "" ));
	 * item.setClassCode((String) (sz > 5 ? assayResult[5] : ""));
	 * item.setFormulaMass((String) (sz > 6 ? assayResult[6] : "" ));
	 * item.setLipidMapsClass((String) (sz > 7 ? assayResult[7] : ""));
	 * 
	 * lst.add(item); } System.out.println("Gathering lipid info there were " +
	 * lst.size() + " results.");
	 * 
	 * return lst; }
	 */

	public String getLipidDescriptor(String name)
		{
		Query query = getEntityManager().createNativeQuery(
				"select cast(lb_id as VARCHAR2(10)), "
						+ "cast(full_name as VARCHAR2(500)), "
						+ "cast(mol_formula as VARCHAR2(50)), "
						+ "cast(precursor_mz as VARCHAR2(22)), "
						+ "cast(ms_mode as VARCHAR2(1)), "
						+ "cast(class_code as VARCHAR2(120)), "
						+ "cast(formula_mass as VARCHAR2(22)), "
						+ "cast(lipid_maps_class as VARCHAR2(30)) "
						+ "from EXTERNAL_DB.Lipid_Blast_Precursor lbp "
						+ "where lbp.code_name = 'PC 40:5'");

		List<Object[]> assayList = query.getResultList();
		List<String> resultList = new ArrayList<String>();

		for (Object[] assayResult : assayList)
			{
			int sz = assayResult.length;

			String lbId = sz > 0 ? (String) assayResult[0] : " ";
			String fullName = sz > 1 ? (String) assayResult[1] : " ";
			String molFormula = sz > 2 ? (String) assayResult[2] : " ";
			String precursorMz = sz > 3 ? (String) assayResult[3] : " ";
			String msMode = sz > 4 ? (String) assayResult[4] : " ";
			String classCode = sz > 5 ? (String) assayResult[5] : " ";
			String formulaMass = sz > 6 ? (String) assayResult[6] : " ";
			String lmClass = sz > 7 ? (String) assayResult[7] : " ";
			}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < resultList.size(); i++)
			sb.append(resultList.get(i) + System.getProperty("line.separator"));

		return sb.toString();
		}
	}


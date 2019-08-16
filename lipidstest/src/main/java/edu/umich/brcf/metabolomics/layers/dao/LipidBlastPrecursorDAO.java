// LipidBlastPrecursorDAO.java
// Written by Jan Wigginton, 2015
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.LipidBlastPrecursor;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class LipidBlastPrecursorDAO extends BaseDAO
	{
	public void createLbPrecursorInfo(LipidBlastPrecursor info)
		{
		getEntityManager().persist(info);
		}

	
	public void deleteMs2SampleMap(LipidBlastPrecursor info) 
		{
		getEntityManager().remove(info);
		}
	
	
	public LipidBlastPrecursor loadById(String id)
		{
		LipidBlastPrecursor info =  getEntityManager().find(LipidBlastPrecursor.class, id);
		initializeTheKids(info, new String [] {"lipidClass"});
		return info;
		}
	
	
	public List <LipidBlastPrecursor> getLipidInfoRelatedTo(String codeName, String adduct)
		{
		List<LipidBlastPrecursor> lst = getEntityManager().createQuery("from LipidBlastPrecursor l"
				+ " where l.adduct=:adduct and l.codeName=:codeName")
				.setParameter("codeName", codeName).setParameter("adduct", adduct).getResultList();
		
		for (LipidBlastPrecursor lbp : lst) 
			initializeTheKids(lbp, new String[] {"lipidMapsClass"} ); 
			 
		return lst;
		}
	
	
	/*
	List <LipidBlastPrecursor> getLipidInfoRelatedTo(String codeName, String adduct)
	{
	Map<String, Object> map = new HashMap<String,Object>();
	map.put("codeName", codeName);
	map.put("adduct", adduct);
	
	System.out.println("Check for code name " + codeName + " and adduct " + adduct);
	Query query = getEntityManager().createNativeQuery("select cast(lb_id as VARCHAR2(10)), "
					+ "cast(full_name as VARCHAR2(500)), "
					+ "cast(mol_formula as VARCHAR2(50)), "
					+ "cast(precursor_mz as VARCHAR2(22)), "
					+ "cast(ms_mode as VARCHAR2(1)), "
					+ "cast(class_code as VARCHAR2(120)), "
					+ "cast(formula_mass as VARCHAR2(22)), "
					+ "cast(lipid_maps_class as VARCHAR2(30)) "
					+ "from EXTERNAL_DB.Lipid_Blast_Precursor lbp "
					+ "where lbp.code_name like '" + codeName + "%' "
					+ "and lbp.adduct like '" + adduct + "%' ");
	
	
	List<Object[]> assayList =  query.getResultList();
	/*if (assayList.size() == 0)
		{	
		query = getEntityManager().createNativeQuery("select cast(lb_id as VARCHAR2(10)), "
				+ "cast(full_name as VARCHAR2(500)), "
				+ "cast(mol_formula as VARCHAR2(50)), "
				+ "cast(precursor_mz as VARCHAR2(22)), "
				+ "cast(ms_mode as VARCHAR2(1)), "
				+ "cast(class_code as VARCHAR2(120)), "
				+ "cast(formula_mass as VARCHAR2(22)), "
				+ "cast(lipid_maps_class as VARCHAR2(30)) "
				+ "from EXTERNAL_DB.Lipid_Blast_Precursor lbp "
				+ "where lbp.code_name like 'PC 40:5%'");
		
		assayList =  query.getResultList();
		}
	
	List <LipidBlastPrecursor> lst = new ArrayList<LipidBlastPrecursor>();

	 for (int i = 0; i < assayList.size(); i++)
		{
		Object [] assayResult = assayList.get(i);
		int sz = 8;	
		LipidBlastPrecursor item = new LipidBlastPrecursor();
	
		item.setLipidId((String) (sz > 0 ? assayResult[0] : ""));
		item.setFullName((String) (sz > 1 ? assayResult[1] : ""));
		item.setMolecularFormula((String) (sz > 2 ? assayResult[2] : ""));
		item.setPrecursorMz((String) (sz > 3 ? assayResult[3] : "" ));
		item.setMsMode((String) (sz > 4 ? assayResult[4] : "" ));
		item.setClassCode((String) (sz > 5 ? assayResult[5] : ""));
		item.setFormulaMass((String) (sz > 6 ? assayResult[6] : "" ));
		
		item.setLipidMapsClass((String) (sz > 7 ? assayResult[7] : ""));
		
		lst.add(item);
		}
		
	return lst;
	}*/
	}

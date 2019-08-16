// LipidMapsEntryDAO.java
// Written by Jan Wigginton, 2015

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.List;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.LipidMapsEntry;
import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


@Repository
public class LipidMapsEntryDAO extends BaseDAO
	{
	public void createLipidMapsEntry(LipidMapsEntry entry)
		{
		getEntityManager().persist(entry);
		//initializeTheKids(map, new String [] {"dataSet"});
		}

	
	public void deleteLipidMapsEntry(LipidMapsEntry entry) 
		{
		getEntityManager().remove(entry);
		}
	
	
	public LipidMapsEntry loadById(String id)
		{
		LipidMapsEntry entry =  getEntityManager().find(LipidMapsEntry.class, id);
		return entry;
		}
	
	
	public List <LipidMapsEntry> loadAllForSubClass(String subClass)
		{
		List<LipidMapsEntry> lst = getEntityManager().createQuery("from LipidMapsEntry l"
				+ " where l.subClass=:subClass").setParameter("subClass", subClass).getResultList();
		
		return lst;
		}
	
	
	public List <IWriteConvertable> loadAllForSubClassAsWriteable(String subClass)
		{
		List<IWriteConvertable> lst = getEntityManager().createQuery("from LipidMapsEntry l"
			+ " where l.subClass=:subClass").setParameter("subClass", subClass).getResultList();
		
		return lst;
		}
	}
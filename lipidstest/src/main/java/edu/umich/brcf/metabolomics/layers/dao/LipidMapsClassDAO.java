// LipidMapsClassDAO.java
// Written by Jan Wigginton, 2015
package edu.umich.brcf.metabolomics.layers.dao;

import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.LipidMapsClass;
import edu.umich.brcf.shared.layers.dao.BaseDAO;


@Repository
public class LipidMapsClassDAO extends BaseDAO
	{
	public void createLipidMapsClass(LipidMapsClass lmClass)
		{
		getEntityManager().persist(lmClass);
		//initializeTheKids(map, new String [] {"dataSet"});
		}

	public void deleteLipidMapsClass(LipidMapsClass lmClass) 
		{
		getEntityManager().remove(lmClass);
		}
	
	public LipidMapsClass loadById(String id)
		{
		LipidMapsClass lmClass =  getEntityManager().find(LipidMapsClass.class, id);
		
		return lmClass;
		}
	}

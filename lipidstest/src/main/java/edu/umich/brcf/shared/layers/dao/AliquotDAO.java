
package edu.umich.brcf.shared.layers.dao;


import java.util.List;

import javax.persistence.Query;

//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.shared.layers.domain.VolumeUnits;



@Repository
public class AliquotDAO extends BaseDAO 
	{
	public VolumeUnits loadVolUnitsById(String unitsID) 
		{
		VolumeUnits units = getEntityManager().find(VolumeUnits.class, unitsID);
		return units;
		}

	
	public List<String> getVolUnitsDDList(String unitsID) 
		{
		VolumeUnits unit = loadVolUnitsById(unitsID);
		Query query = getEntityManager().createQuery("select v.units from VolumeUnits v where v.type='" + unit.getType() 
		+ "' and v.priority <= " + unit.getPriority());
		
		List<String> unitsList = query.getResultList();
		return unitsList;
		}

	
	public List<String> getAllVolUnits() 
		{
		Query query = getEntityManager().createQuery("select distinct v.units from VolumeUnits v");
		List<String> unitsList = query.getResultList();
		return unitsList;
		}

	public int getMaxSequence(String sampleid) 
		{
		Query query = getEntityManager().createNativeQuery("select max(a.sequence) from aliquot a where a.sample_id=?1")
			.setParameter(1,sampleid )	;
		
		java.math.BigDecimal seq = (java.math.BigDecimal) query.getSingleResult();
		return seq.intValue();
		}
	
	
	public int getSequence(String sampleid)
		{
		Query query = getEntityManager().createNativeQuery(
			"select min(a.sequence) from aliquot a where a.status = 'A' and  a.sample_id=  ?1 " ).setParameter(1, sampleid);
	
		java.math.BigDecimal seq = (java.math.BigDecimal) query.getSingleResult();
		return seq.intValue();
		}
	
	
	public List<String> getWellDataFile(Long wellId) 
		{
		Query query = getEntityManager().createNativeQuery("select i.file_name from injection_name i where i.well_id = ?1").setParameter(1, wellId)	;	
		List<String> items = query.getResultList();
		return items;
		}
	}

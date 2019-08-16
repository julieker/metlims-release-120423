///////////////////////////////////////////
// Writtten by Anu Janga
///////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.Injections;
import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.layers.domain.BiologicalSample;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.domain.PreppedSample;
import edu.umich.brcf.shared.layers.domain.Sample;

@Repository
public class InjectionsDAO extends BaseDAO 
	{
	public boolean injectionExists(PrepPlate plate, PreppedItem preppedItem, String sequence, String mode) 
		{
		Injections injection = null;

		try { injection = findInjection(plate, preppedItem, sequence, mode); } 
		catch (Exception e) { injection = null; }
		
		return (injection != null);
		}	

	
	public Injections findInjection(PrepPlate plate, PreppedItem preppedItem, String sequence, String mode)
		{
		return (Injections) DataAccessUtils.requiredSingleResult(getEntityManager()
			.createQuery("from Injections i where i.plate = :plate and i.preppedItem = :preppedItem and i.sequence = :sequence and i.mode = :mode")
			.setParameter("plate", plate).setParameter("preppedItem", preppedItem).setParameter("sequence", sequence)
			.setParameter("mode", mode).getResultList());
		}

	public Injections findInjection(String fileName, String sequence, String fileMode) 
		{
		return (Injections) DataAccessUtils.requiredSingleResult(getEntityManager().createQuery(
				"from Injections i where  i.dataFileName = :filename and i.sequence = :sequence and i.mode = :mode")
				.setParameter("filename", fileName)	
				.setParameter("sequence", sequence)	
				.setParameter("mode", fileMode)	
				.getResultList() );
		}

	public List<Injections> getInjectionListForWellAndMode(PrepPlate plate, PreppedItem preppedItem, String fileMode) 
		{
		List<Injections> list = getEntityManager().createQuery("from Injections i where i.plate = :plate and i.preppedItem = :preppedItem and i.mode = :mode")
			.setParameter("plate", plate).setParameter("preppedItem", preppedItem).setParameter("mode", fileMode)
			.getResultList();
		
		return list;
		}

	public List<Injections> loadInjectionsForPlateId(String plateId) 
		{
		return getEntityManager().createQuery("from Injections i where i.plate.plateID = :plateId").setParameter("plateId", plateId).getResultList();
		}
	

	public Injections saveInjection(Injections injection) 
		{
		getEntityManager().persist(injection);
		getEntityManager().flush();
		return injection;
		}

	
	public Injections loadInjectionById(Long injectionId) 
		{
		return getEntityManager().find(Injections.class, injectionId);
		}


	public List<Injections> getInjectionforSample(Sample sample) 
		{
		List<Injections> injList=new ArrayList<Injections>();
		if (sample instanceof BiologicalSample)
			for(Injections inj : ((BiologicalSample)sample).getInjections())
				if (!inj.getMode().equals("Z"))
					if(get_Mass_RT_Data(inj, null, null).size()>0)
						injList.add(inj);
			
		return injList;
		}

	
	public List<String> get_Mass_RT_Data(Injections injection, ArrayList<String> toolTips, ArrayList<String> cAreas) 
		{
		Query query = getEntityManager().createNativeQuery("select a.injection_id||'i' "+ "from metlims_library.analysis_ref a "+
		"where a.injection_id= ?1 " ).setParameter(1, injection.getId());
		
		return  query.getResultList();
		}


	public List<String> get_Mass_RT_DataArray(Injections injection) 
		{
		Query query = getEntityManager().createNativeQuery("select p.mass||'_'||c.apex_rt||'_'||c.comp_area||'_'||cl.cid||'/'||c.component_id "+
				"from metlims_library.analysis_ref a, metlims_library.peaks p, metlims_library.components c left outer join metlims_library.component_library cl on c.target_id=cl.target_id "+
				"where a.injection_id= ?1  and a.analysis_id=c.analysis_id and p.component_id = c.component_id and p.rel_intensity = ?2 order by p.mass") 
				.setParameter(1,  injection.getId())
				.setParameter(2, "100");
		
		return query.getResultList();
		}

	
	public List<String> getSampleRunMethodsForInstrumentTypeAndMode(String instrumentType, String runmode, String creator)
		{
		Query query = getEntityManager().createNativeQuery("select distinct s.method_name from SAMPLERUN_METHOD s where s.instrument_type= ?1 and s.run_mode = ?2  and s.creator = ?3 ")
				.setParameter(1, instrumentType ).setParameter(2,runmode ).setParameter(3, creator);
		
		return query.getResultList();
		}
	
	
	public String getDefaultSampleRunMethodForInstrumentTypeAndMode(String instrumentType, String runmode, String creator) 
		{
		Query query = getEntityManager().createNativeQuery("select s.method_name from SAMPLERUN_METHOD s where s.instrument_type= ?1 and s.run_mode = ?2 and s.default_mode_method = ?3 and s.creator=?4")
			.setParameter(1, instrumentType).setParameter(2,runmode)
			.setParameter(3,  "1").setParameter(4, creator);
		
		String def_method;
		
		try { def_method = (String) query.getSingleResult(); }
		catch (NoResultException e){ def_method=""; }
		
		return def_method;
		}
	

	public List<String> getAllAssays() 
		{
		Query query = getEntityManager().createNativeQuery("select a.ASSAY_NAME from (select * from assays a1 order by 1) a");
		List<String> assayLst=query.getResultList();
	
		return assayLst;
		}
	

	public void updateSampleInjectionStatus(Injections injection) 
		{
		Hibernate.initialize(injection.getPreppedItem());
		PreppedSample pSample = (PreppedSample)injection.getPreppedItem();
		Hibernate.initialize(pSample.getSample());
		pSample.getSample().setInjectedStatus();
		}
}

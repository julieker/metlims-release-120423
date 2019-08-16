package edu.umich.brcf.shared.layers.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;
import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;
import edu.umich.brcf.shared.layers.domain.Observation;
import edu.umich.brcf.shared.layers.domain.ObservationMap;
import edu.umich.brcf.shared.layers.domain.PlatePrepObservation;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.PrepWell;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.domain.PreppedSample;
import edu.umich.brcf.shared.layers.domain.SamplePrepObservation;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;


@Repository
public class SamplePrepDAO extends BaseDAO
	{
	public Preparation saveSamplePrep(Preparation prep) 
		{
		getEntityManager().persist(prep);
		Query query = getEntityManager().createQuery("select max(p.prepID) from SamplePreparation p ");
		String prepId = (String) query.getSingleResult();
		return loadById(prepId);
		}

	
	public Preparation loadById(String prepId) 
		{
		Preparation prep = getEntityManager().find(Preparation.class, prepId);
		Hibernate.initialize(prep.getCreator());
		return prep;
		}
	
	
	public List<Preparation> loadyByCreatorId(String creatorId)
		{
		List<Preparation> lst = getEntityManager().createQuery("from Preparation p where p.creator.id = :creatorId order by p.prepDate desc")
			.setParameter("creatorId", creatorId).getResultList();
		
		return lst;
		}

	
	public PrepWell loadPrepWellByIndex(Integer index, String plateFrmt)
		{
		List<PrepWell> lst = getEntityManager().createQuery("from PrepWell p where p.index = :index and p.plateFormat=:plateFrmt")
				.setParameter("index", index).setParameter("plateFrmt", plateFrmt).getResultList();
		
		PrepWell well = (PrepWell) DataAccessUtils.requiredSingleResult(lst);
		return well;
		}
	
	
	public PrepWell loadPrepWellByLocation(String loc, String plateFrmt)
		{
		List<PrepWell> lst = getEntityManager().createQuery("from PrepWell p where p.location = :loc and p.plateFormat=:plateFrmt")
				.setParameter("loc", loc).setParameter("plateFrmt", plateFrmt).getResultList();
		
		PrepWell well = (PrepWell) DataAccessUtils.requiredSingleResult(lst);
		return well;
		}
	
	
	public GeneralPrepSOP loadGeneralPrepSOPByID(String id)
		{
		GeneralPrepSOP prep = getEntityManager().find(GeneralPrepSOP.class, id);
		return prep;
		}
	
	
	public GCDerivatizationMethod loadGCDerivatizationByID(String id)
		{
		GCDerivatizationMethod method = getEntityManager().find(GCDerivatizationMethod.class, id);
		return method;
		}
	
	
	public LCReconstitutionMethod loadLCReconstitutionByID(String id)
		{
		LCReconstitutionMethod method = getEntityManager().find(LCReconstitutionMethod.class, id);
		return method;
		}

	
	public void savePreppedItem(PreppedSample item) 
		{
		getEntityManager().persist(item);
		}	

	
	public PrepPlate savePrepPlate(PrepPlate prepPlate) 
		{
		getEntityManager().persist(prepPlate);
		Query query = getEntityManager().createQuery("select max(p.plateID) from PrepPlate p ");
		String plateID = (String) query.getSingleResult();
		return loadPlateOnlyByID(plateID);
		}
	
	
	private PrepPlate loadPlateOnlyByID(String plateID) 
		{
		PrepPlate plate = getEntityManager().find(PrepPlate.class, plateID);
		initializeTheKids(plate, new String[] {"samplePrep", "instrument"});
		return plate;
		}

	
	public String saveGeneralPrep(GeneralPrepSOP prep) 
		{
		getEntityManager().persist(prep);
		Query query = getEntityManager().createQuery("select max(p.prepID) from GeneralPrepSOP p ");
		String prepId = (String) query.getSingleResult();
		return prepId;
		}

	
	public List<PreppedSample> loadPreppedSamples(String prep) 
		{
		Preparation preparation=loadById(prep);
		
		List<PreppedSample> lst = getEntityManager().createQuery("from PreppedSample p where p.samplePrep = :preparation order by p.well")
				.setParameter("preparation", preparation).getResultList();
		
		for (PreppedSample prepSample : lst) 
			initializeTheKids(prepSample, new String[] { "sample", "well", "generalPrepSOP", "homogenization", "protienDetermination"});
			
		return lst;
		}

	
	public List<PreppedSample> loadPreppedSamplesInNewOrder(String prep) 
		{
		Preparation preparation=loadById(prep);
		List<PreppedSample> lst = getEntityManager().createQuery("from PreppedSample p where p.samplePrep = :preparation order by p.sample.sampleID")
				.setParameter("preparation", preparation)	.getResultList();
		
		for (PreppedSample prepSample : lst) 
			initializeTheKids(prepSample, new String[] { "sample", "well", "generalPrepSOP", "homogenization", "protienDetermination"});
		
		return lst;
		}
	
	
	public List<Preparation> allPreparations() 
		{
		List<Preparation> list = getEntityManager().createQuery("from Preparation").getResultList();
		return list;
		}

	
	public List<Preparation> allSamplePreparations() 
		{
		List<Preparation> list = getEntityManager().createQuery("from SamplePreparation order by p.prepId desc").getResultList();
		return list;
		}
	
	
	public List<String> allSamplePreparationsSortedByDate() 
		{
		Query query = getEntityManager().createQuery("select p.title||' ('||p.prepID||')' from SamplePreparation p order by p.prepID desc");
		query.setMaxResults(100);
		List<String> list = query.getResultList();
		return list;
		}

	
	public List<String> allSamplePreparationsSortedByDateButShort() 
		{
		Query query = getEntityManager().createQuery("select p.title||' ('||p.prepID||')' from SamplePreparation p order by p.prepID desc");
		query.setMaxResults(5);
		List<String> list = query.getResultList();
		return list;
		}
	
	
	public String saveDerivatization(GCDerivatizationMethod sop) 
		{
		getEntityManager().persist(sop);
		Query query = getEntityManager().createQuery("select max(g.derivatizationID) from GCDerivatizationMethod g ");
		String prepId = (String) query.getSingleResult();
		return prepId;
		}

	
	public String saveReconstitution(LCReconstitutionMethod sop) 
		{
		getEntityManager().persist(sop);
		Query query = getEntityManager().createQuery("select max(l.reconstitutionID) from LCReconstitutionMethod l ");
		String prepId = (String) query.getSingleResult();
		return prepId;
		}

	
	public PrepPlate loadPlateByID(String id)
		{
		PrepPlate plate = getEntityManager().find(PrepPlate.class, id);
		initializeTheKids(plate, new String[] {"samplePrep", "instrument"});
		Hibernate.initialize(plate.getSamplePrep().getItems());
		
		if (plate.getSamplePrep().getItems()!=null)
			for (PreppedItem preppedItem : plate.getSamplePrep().getItems())
				{
				Hibernate.initialize(preppedItem.getWell());
				
				if (preppedItem instanceof PreppedSample)
					Hibernate.initialize(((PreppedSample)preppedItem).getSample());
					
				else if (preppedItem instanceof PreppedFraction)
					Hibernate.initialize(((PreppedFraction)preppedItem).getFraction());
				}
		
		return plate;
		}
	
	
	public List<PrepPlate> loadPlatesByPreparation(String prep) 
		{
		Preparation preparation=loadById(prep);
		
		List<PrepPlate>  prepPlates= getEntityManager().createQuery("from GCPlate p where p.samplePrep = :preparation")
				.setParameter("preparation", preparation).getResultList();
		
		for (PrepPlate plate : prepPlates) 
			initializeTheKids(plate, new String[] {"derivatizationMethod", "instrument"});

		List<PrepPlate> lst=getEntityManager().createQuery("from LCPlate p where p.samplePrep = :preparation")
				.setParameter("preparation", preparation).getResultList();
		
		for (PrepPlate plate : lst) 
			initializeTheKids(plate, new String[] {"reconstitutionMethod", "instrument"});
		
		prepPlates.addAll(lst);
		
		return prepPlates;
		}

	
	public PreppedSample loadPreppedSampleByID(String sample) 
		{
		PreppedSample preppedSample = getEntityManager().find(PreppedSample.class, sample);
		initializeTheKids(preppedSample, new String[] { "sample", "well", "generalPrepSOP", "homogenization", "protienDetermination"});
		return preppedSample;
		}
	
	
	public List<ValueLabelBean> loadSampleObservationByPrepID(String prep) 
		{
		Query query = getEntityManager().createQuery(
				"select distinct o.observation.id from SamplePrepObservation o, PreppedSample p"
				+" where o.preppedItem.itemID=p.itemID and p.samplePrep.prepID='" + prep + "'");
		
		List<String> observationIds=query.getResultList();
		List<ValueLabelBean> lvbList=new ArrayList<ValueLabelBean>();
		
		String csvIDs="";
		for (String id : observationIds) 
			{
			Observation o=loadObservationByID(id.toString());
			for(ObservationMap om : o.getObservationMapList())
				{
				initializeTheKids((SamplePrepObservation)om, new String[] { "preppedItem", "preppedItem.well"});
				csvIDs+=((SamplePrepObservation)om).getPreppedItem().getWell().getLocation()+",";
				}
			lvbList.add(new ValueLabelBean(o.getDescription(),csvIDs.substring(0, csvIDs.lastIndexOf(","))));
			csvIDs="";
			}
		
		if(lvbList.size()==0)
			lvbList.add(new ValueLabelBean("None",""));
		return lvbList;
		}
	
	
	public List<ValueLabelBean> loadPlateObservationByPrepID(String prep) 
		{
		Query query = getEntityManager().createQuery("select distinct o.observation.id from PlatePrepObservation o, PrepPlate p"
				+" where o.prepPlate.plateID=p.plateID and p.samplePrep.prepID='" + prep + "'");
		
		List<String> observationIds=query.getResultList();
		List<ValueLabelBean> lvbList=new ArrayList<ValueLabelBean>();
		
		String csvIDs="";
		for (String id : observationIds) 
			{
			Observation o=loadObservationByID(id);
			for(ObservationMap om : o.getObservationMapList())
				{
				initializeTheKids((PlatePrepObservation)om, new String[] { "prepPlate"});
				csvIDs+=((PlatePrepObservation)om).getPrepPlate().getPlateID()+",";
				}
			lvbList.add(new ValueLabelBean(o.getDescription(),csvIDs.substring(0, csvIDs.lastIndexOf(","))));
			csvIDs="";
			}
		
		if(lvbList.size()==0)
			lvbList.add(new ValueLabelBean("None",""));
		
		return lvbList;
		}
	
	
	public Observation loadObservationByID(String id) 
		{
		Observation observation = getEntityManager().find(Observation.class, id);
		initializeTheKids(observation, new String[] {"observationMapList"});
		return observation;
		}

	
	public Observation saveObservation(Observation observation) 
		{
		getEntityManager().persist(observation);
		Query query = getEntityManager().createQuery("select max(o.id) from Observation o ");
		String id = (String) query.getSingleResult();
		return loadObservationByID(id);
		}

	public void saveObservationMap(ObservationMap observationMap) {
		getEntityManager().persist(observationMap);
		}
	
	
	public List<String> getAllowedDuplicates()
		{
		Query query = getEntityManager().createQuery("select distinct s.sampleID from Sample s, Experiment e, Project p"
				+" where s.exp.expID=e.expID and e.project.projectID='PR0006'");
		List<String> sampleIds=query.getResultList();
		return sampleIds;
		}
	

	public String saveHomogenization(HomogenizationSOP sop) 
		{
		getEntityManager().persist(sop);
		Query query = getEntityManager().createQuery("select max(h.id) from HomogenizationSOP h ");
		String prepId = (String) query.getSingleResult();
		return prepId;
		}

	
	public HomogenizationSOP loadHomogenizationByID(String id) 
		{
		HomogenizationSOP sop = getEntityManager().find(HomogenizationSOP.class, id);
		return sop;
		}
	
	
	public String saveProtienDetermination(ProtienDeterminationSOP sop) 
		{
		getEntityManager().persist(sop);
		Query query = getEntityManager().createQuery("select max(p.id) from ProtienDeterminationSOP p ");
		String prepId = (String) query.getSingleResult();
		return prepId;
		}

	
	public ProtienDeterminationSOP loadProtienDeterminationSOPByID(String id) 
		{
		ProtienDeterminationSOP sop = getEntityManager().find(ProtienDeterminationSOP.class, id);
		return sop;
		}

	
	public PreppedItem getPreppedItemFromLoc(Preparation samplePrep, String loc, String plateFrmt) 
		{
		PrepWell well=loadPrepWellByLocation(loc, plateFrmt);
		
		List<PreppedItem> lst = getEntityManager().createQuery("from PreppedItem p where p.samplePrep=:prep and p.well = :well")
				.setParameter("prep", samplePrep).setParameter("well", well).getResultList();
		
		PreppedItem preppedItem = (PreppedItem) DataAccessUtils.requiredSingleResult(lst);
		
		Hibernate.initialize(preppedItem.getWell());
		if(preppedItem instanceof PreppedSample)
			Hibernate.initialize(((PreppedSample)preppedItem).getSample());
		
		return preppedItem;
		}

	
	public List<PreppedItem> getPreppedSampleFromLocInPlate(String plateId, String loc) 
		{
		PrepPlate plate=loadPlateByID(plateId);
		PrepWell well=loadPrepWellByLocation(loc, plate.getPlateFormat());
		
		List<PreppedItem> lst = getEntityManager().createQuery("from PreppedItem p where p.samplePrep=:prep and p.well = :well")
				.setParameter("prep", plate.getSamplePrep()).setParameter("well", well).getResultList();
		
		for (PreppedItem preppedItem : lst) 
			{
			Hibernate.initialize(preppedItem.getWell());
			if (preppedItem instanceof PreppedSample)
				Hibernate.initialize(((PreppedSample)preppedItem).getSample());
			else if (preppedItem instanceof PreppedFraction){
				Hibernate.initialize(((PreppedFraction)preppedItem).getFraction());}
			}
		return lst;
		}

	
	public PreppedItem loadPreppedItemByID(String sample) 
		{
		PreppedItem preppedItem = getEntityManager().find(PreppedItem.class, sample);
		
		if (preppedItem instanceof PreppedSample)
			initializeTheKids(preppedItem, new String[] { "sample", "well", "generalPrepSOP", "homogenization", "protienDetermination"});
		else
			initializeTheKids(preppedItem, new String[] { "fraction", "well", "generalPrepSOP", "homogenization", "protienDetermination"});
		return preppedItem;
		}

	
	public boolean existsTitle(String title) 
		{
		List<Preparation>  preps= getEntityManager().createQuery("from SamplePreparation p where p.title = :title")
			.setParameter("title", title).getResultList();
		
		return (preps.size()>0);
		}
	

	public List<String> getMatchingPreps(String input) 
		{
		Query query = getEntityManager().createQuery("select p.prepID from SamplePreparation p where p.prepID like '%"+input+"%'");
		List<String> pidList = query.getResultList();
		return pidList;
		}

	
	public List<String> getMatchingPlates(String input) 
		{
		Query query = getEntityManager().createQuery("select p.plateID from PrepPlate p where p.plateID like '%"+input+"%'");
		List<String> eidList = query.getResultList();
		return eidList;
		}
	}

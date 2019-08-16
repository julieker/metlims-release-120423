///////////////////////////////////////////
// Writtten by Anu Janga
///////////////////////////////////////////
package edu.umich.brcf.metabolomics.layers.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.dao.support.DataAccessUtils;
//import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;

import edu.umich.brcf.metabolomics.layers.domain.FractionPreparation;
import edu.umich.brcf.metabolomics.layers.domain.FractionSample;
import edu.umich.brcf.metabolomics.layers.domain.Injections;
import edu.umich.brcf.shared.layers.dao.BaseDAO;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.Sample;



@Repository
public class FractionationDAO  extends BaseDAO
	{

	public FractionPreparation loadById(String prepId) 
		{
		return getEntityManager().find(FractionPreparation.class, prepId);
		}
	
	public FractionSample loadFractionById(String fractionId) 
		{
		return getEntityManager().find(FractionSample.class, fractionId);
		}

	public Preparation saveSamplePrep(FractionPreparation instance) 
		{
		getEntityManager().persist(instance);
		Query query = getEntityManager().createQuery("select max(p.prepID) from FractionPreparation p ");
		String prepId = (String) query.getSingleResult();
		return loadById(prepId);
		}

	public void savePreppedItem(PreppedFraction item) 
		{
		getEntityManager().persist(item);
		}

	public void saveFraction(FractionSample fraction) 
		{
		getEntityManager().persist(fraction);
//		Query query = getEntityManager().createQuery("select max(f.sampleID) from FractionSample f ");
//		String fractionId = (String) query.getSingleResult();
//		return loadFractionById(fractionId);
		}

	
	public List<PreppedFraction> loadPreppedFractions(String prep) 
		{
		Preparation preparation=loadById(prep);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("preparation", preparation);
		List<PreppedFraction> lst = getEntityManager().createQuery(
				"from PreppedFraction p where p.samplePrep = :preparation order by 1")
				.setParameter("preparation", preparation).getResultList();
		
		for (PreppedFraction prepFraction : lst) 
			initializeTheKids(prepFraction, new String[] { "fraction", "well"});
			
		return lst;
		}

	
	public List<PrepPlate> loadPlatesByPreparation(String prep) 
		{
		Preparation preparation=loadById(prep);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("preparation", preparation);
		List<PrepPlate>  prepPlates= getEntityManager()
				.createQuery("from GCPlate p where p.samplePrep = :preparation")
				.setParameter("preparation", preparation).getResultList();
		
		for (PrepPlate plate : prepPlates) 
			initializeTheKids(plate, new String[] {"instrument"});
		
		List<PrepPlate> lst=getEntityManager().createQuery("from LCPlate p where p.samplePrep = :preparation")
				.setParameter("preparation", preparation).getResultList(); 
		
		for (PrepPlate plate : lst) 
			initializeTheKids(plate, new String[] {"instrument"});
		
		prepPlates.addAll(lst);
		return prepPlates;
		}

	
	public List<Preparation> allFractionPreparations() 
		{
		List<Preparation> list = getEntityManager().createQuery("from FractionPreparation").getResultList();
		return list;
		}
	

	public Sample loadCompleteFractionsTree(String id) 
		{
		Map<String, Object> map = new HashMap<String, Object>();
		if(id==null)
			id="S00001201";
		map.put("sid", id);
		List<Sample> fractionsList = getEntityManager()
				.createQuery("from Sample s where s.sampleID = :sid").setParameter("sid", id).getResultList();
		
		Sample parentSample = (Sample) DataAccessUtils.requiredSingleResult(fractionsList);
		recursivelyInitializeKids(parentSample);
		return parentSample;
		}
	
	
	public String getOrgChartData()
		{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		String id="S00001201";
		map.put("sid", id);
		List<Sample> fractionsList = getEntityManager().createQuery("from Sample s where s.sampleID = :sid").setParameter("sid", id).getResultList();
		
		Sample parentSample = (Sample) DataAccessUtils.requiredSingleResult(fractionsList);
		recursivelyInitializeKids(parentSample);
		map3.put("cellCount", 0);
		produceSampleMap(parentSample,map3, map1);
		String chartData = "";
		map2.put("chartData", chartData);
		produceChartData(parentSample, map1, map2);
	
		return "0__0__S00001201__Human Plasma,,,"+((String) map2.get("chartData"));
		}
	

	private void produceChartData(Sample parentSample, Map<String, Object> map1, Map<String, Object> map2) {
		for (Sample child : parentSample.getChildren()) {
			String chartData=((String)map2.get("chartData"))+map1.get(child.getSampleID())+"__"+map1.get(parentSample.getSampleID())+"__"+
			child.getSampleID()+"__"+child.getName()+",,,";//__http://www.google.com__#FFFFFF
			map2.put("chartData", chartData);
			produceChartData(child, map1, map2);
		}
	}

	private void produceSampleMap(Sample parentSample, Map<String, Object> map3, Map<String, Object> map1) {
		if (!map1.containsKey(parentSample.getSampleID())){
			int i = (Integer) map3.get("cellCount");
			map1.put(parentSample.getSampleID(), i);
			map3.put("cellCount", i+1);}
		for (Sample child : parentSample.getChildren()) {
			produceSampleMap(child, map3, map1);
		}
	}

	public void recursivelyInitializeKids(Sample parentSample){
		initializeTheKids(parentSample, new String[] {"children"});
		for (Sample child : parentSample.getChildren()) {
			recursivelyInitializeKids(child);
		}
	}

	public List<String> getMatchingFractions(String input) 
		{
		Query query = getEntityManager().createQuery("select f.sampleID from FractionSample f where f.sampleID like '%"+input+"%'");
		List<String> fidList = query.getResultList();
		return fidList;
		}

	// FractionSample
	public List<Injections> getInjectionforFraction(Sample fraction) 
		{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Injections> injList=new ArrayList<Injections>();
		if (fraction instanceof FractionSample){
			map.put("fraction", (FractionSample)fraction);
		
		List<PreppedFraction> preppedFractions=
				getEntityManager().createQuery(
			    "from PreppedFraction p where p.fraction = :fraction")
				.setParameter("fraction", (FractionSample)fraction)
		        .getResultList();
		
		if(preppedFractions.size()>0)
			{
			for (PreppedFraction preppedFraction : preppedFractions){
				//PreppedFraction preppedFraction=(PreppedFraction) DataAccessUtils.requiredSingleResult(preppedFractions);
				initializeTheKids(preppedFraction,  new String[] {"injectionList"});
				//for(Injections inj : preppedFraction.getInjectionList())
				//	{
				//	if (!inj.getMode().equals("Z"))
				//		{
				//		if(get_Mass_RT_Data(inj, null, null).getItemCount(0)>0)
				//			injList.add(inj);
				//		}
				//	}
				}
			}}
		return injList;
		}

	public XYDataset get_Mass_RT_Data(Injections injection, ArrayList<String> toolTips, ArrayList<String> cAreas) {
		Query query = getEntityManager().createNativeQuery("select p.mass||'_'||c.apex_rt||'_'||c.comp_area||'_'||cl.cid||'/'||c.component_id "+
		"from metlims_library.analysis_ref a, metlims_library.peaks p, metlims_library.components c left outer join metlims_library.component_library cl on c.target_id=cl.target_id "+
		"where a.injection_id='"+injection.getId()+"' and a.analysis_id=c.analysis_id and p.component_id = c.component_id and p.rel_intensity = 100 order by p.mass") ;
		List<String> rsltList=query.getResultList();
		final XYSeries series = new XYSeries("Injection - "+injection.getId()+" ("+injection.getDataFileName()+" "+injection.getSequence()+injection.getMode()+")");
        for (String result : rsltList) {
        	String[] xySet = result.split("_");
            final double x = Double.parseDouble(xySet[0]);
            final double y = Double.parseDouble(xySet[1]);
            series.add(x, y);
            if(cAreas!=null){
	            try{
	            	cAreas.add(xySet[2]);
	            }catch(Exception e){
	            	cAreas.add("");
	            }
            }
            if(toolTips!=null){
	            try{
	            	toolTips.add(xySet[3]);
	            }catch(Exception e){
	            	toolTips.add("");
	            }
            }
        }
        final XYDataset dataset = new XYSeriesCollection(series);
		return dataset;
	}
	
	public List<String> get_Mass_RT_DataArray(Injections injection) {
		Query query = getEntityManager().createNativeQuery("select p.mass||'_'||c.apex_rt||'_'||c.comp_area||'_'||cl.cid||'/'||c.component_id "+
		"from metlims_library.analysis_ref a, metlims_library.peaks p, metlims_library.components c left outer join metlims_library.component_library cl on c.target_id=cl.target_id "+
		"where a.injection_id='"+injection.getId()+"' and a.analysis_id=c.analysis_id and p.component_id = c.component_id and p.rel_intensity = 100 order by p.mass") ;
		List<String> rsltList=query.getResultList();
//		final XYSeries series = new XYSeries("Injection - "+injection.getId()+" ("+injection.getDataFileName()+" "+injection.getSequence()+injection.getMode()+")");
//        for (String result : rsltList) {
//        	String[] xySet = result.split("_");
//            final double x = Double.parseDouble(xySet[0]);
//            final double y = Double.parseDouble(xySet[1]);
//            series.add(x, y);
//            if(cAreas!=null){
//	            try{
//	            	cAreas.add(xySet[2]);
//	            }catch(Exception e){
//	            	cAreas.add("");
//	            }
//            }
//            if(toolTips!=null){
//	            try{
//	            	toolTips.add(xySet[3]);
//	            }catch(Exception e){
//	            	toolTips.add("");
//	            }
//            }
//        }
//        final XYDataset dataset = new XYSeriesCollection(series);
		return rsltList;
	}

	public String getFractionPrepIdByName(String fname) {
		Query query = getEntityManager().createQuery("select f.prepID from FractionPreparation f where f.title = '"+fname+"'");
		List<String> fractionPrepIds = query.getResultList();
		return fractionPrepIds.size()>0? fractionPrepIds.get(0):null;
	}
}

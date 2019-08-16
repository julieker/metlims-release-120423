////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistSampleGroup.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.io.StringUtils;


public class WorklistSampleGroup extends WorklistGroup implements Serializable
	{
	private static final long serialVersionUID = 8831110536902754801L;
	private Map<String, String> idsVsResearcherNameMap; // issue 345

	@SpringBean
	private SampleService sampleService;

	@SpringBean
	private AssayService assayService;

	
	private String assayType = null;
	private String randomizationType;

	private Boolean isRandomized;
	private int nItemsLeft;
	private List<String> randomOrderSamples;
	

	ExperimentRandomization expRandom = null;
	private Map<String, String> sampleIdToPositionMap;
	private Map<String, Boolean> sampleIdToControlStatusMap;
	private Map<String, String> sampleIdToControlGroupNameMap; 

	// issue 350
	public Boolean wasCustomOrdered() 
	    { 
		return expRandom != null;
		}
	
	public WorklistSampleGroup()
		{
		
		this(null, "", "None", null);;
		}
	
	WorklistSampleGroup(WorklistSimple w)
		{
		
		this(null, "", "None", w);
		}
	

	WorklistSampleGroup(String assay, WorklistSimple w)
		{
		this(null, assay, "None", w);
		}

	WorklistSampleGroup(String eid, String assay, String randomization, WorklistSimple w)
		{		
		super(w, eid);
		Injector.get().inject(this);	
		assayType = assay;
		randomizationType = randomization;
		isRandomized = false;	
		randomOrderSamples = new ArrayList<String>();
		sampleIdToPositionMap = new HashMap<String, String>();
		sampleIdToControlStatusMap = new HashMap<String, Boolean>();
		sampleIdToControlGroupNameMap = new HashMap<String, String>();			
		}

	// issue 345
	public void initializeIdsVsResearcherNameMap (String eid)
	    {
		idsVsResearcherNameMap = sampleService.sampleIdToResearcherNameMapForExpId(eid);	//issue 345			
	    }
	
	// Issue 344
	private void initializeForCustomRandomization()
	    {
	    if (expRandom == null)
		    return;
	    randomOrderSamples.clear();
	    sampleIdToControlStatusMap = new HashMap<String, Boolean>();
	    sampleIdToControlGroupNameMap = new HashMap<String, String>();  	
	    for (int i = 0; i < expRandom.getSamplesArray().size(); i++)
		    {
	    	
		    RandomizedSample rs = expRandom.getSamplesArray().get(i);
		    randomOrderSamples.add(rs.getSampleName());
		    sampleIdToControlStatusMap.put(rs.getSampleName(), rs.getReplicate() > 0);		
		    String controlGroupName = "";
		    if (rs.getReplicate() > 0) 
			    {
			    int idxForLastDash = rs.getSampleName().lastIndexOf('-');
			    if (idxForLastDash > 0)
				    controlGroupName =  rs.getSampleName().substring(0, idxForLastDash);
			    else 
				    controlGroupName = rs.getSampleName();
			    }	
		   // controlGroupName = controlGroupName.length()> 9 ? controlGroupName.substring(0,9) : controlGroupName; // issue 346
		    sampleIdToControlGroupNameMap.put(rs.getSampleName(), controlGroupName);
		    }
	    isRandomized = true;
	    }

	public int countSampleItemsForGroup()
		{
		List<String> sampleNames = (isRandomized ? randomOrderSamples: (List<String>) sampleService.sampleIdsForExpIdAndAssayId(
						getExperimentId(), extractAssayIdFromType()));

		return sampleNames.size();
		}

	
	public int countWorklistItemsForGroup()
		{
		List<String> sampleIds = (List<String>) sampleService.sampleIdsForExpIdAndAssayIdMinusExcluded(getExperimentId(), extractAssayIdFromType());
		return (sampleIds == null ? 0 : sampleIds.size());
		}

	
	public int countExcludedItemsForGroup()
		{
		List<String> sampleIds = (List<String>) sampleService.excludedSampleIdsForExpIdAndAssayId(getExperimentId(), extractAssayIdFromType());
		return (sampleIds == null ? 0 : sampleIds.size());
		}

	// issue 344
	public List<WorklistItemSimple> getWorklistItemsForGroup()
	    {
	    initializeForCustomRandomization();
	    List<String> sampleNames = isRandomized ? randomOrderSamples
			: (List<String>) sampleService.sampleIdsForExpIdAndAssayIdMinusExcluded(getExperimentId(), extractAssayIdFromType());
	    List<WorklistItemSimple> list = new ArrayList<WorklistItemSimple>();
	    if (sampleNames == null)
		    return null;
	    nItemsLeft = 0;
	    for (int i = 0; i < sampleNames.size(); i++)
		    {
		    String fullSampleName = sampleNames.get(i);
		    if (fullSampleName.trim().equals(""))
			    continue;
		    Boolean isControl = false;
		    if (sampleIdToControlStatusMap != null && sampleIdToControlStatusMap.containsKey(fullSampleName))
			    isControl = sampleIdToControlStatusMap.get(fullSampleName);		
		    WorklistItemSimple item = newWorklistItem(fullSampleName, parent, isControl);
		    if (sampleIdToControlGroupNameMap != null && sampleIdToControlGroupNameMap.containsKey(fullSampleName))
		        {
			    item.setNameForUserControlGroup(sampleIdToControlGroupNameMap.get(fullSampleName));
			    if (item.getNameForUserControlGroup().length() > 9)
			    	item.setShortSampleName(item.getNameForUserControlGroup().substring(0,9));
			    else
			    	item.setShortSampleName(item.getNameForUserControlGroup().substring(0,item.getNameForUserControlGroup().length()));
		        }   
		    if (getRandomizationType().equals("None") || isRandomized)
			    list.add(item);
		    else if (!isRandomized)
			    addItemToListRandomly(item, list);
		    
		    }
	    if (!isRandomized)
		    buildOrderList(list);
	    isRandomized = true;
	    return list;
	    }

	// issue 345
	private WorklistItemSimple newWorklistItem(String sampleId, WorklistSimple parent, Boolean itemIsControl)
	    {		
	    WorklistItemSimple item = new WorklistItemSimple(sampleId, sampleId, itemIsControl, 
	        parent, this);	
	    item.setRepresentsUserDefinedControl(itemIsControl);
	    item.setExperimentId(getExperimentId());
	    item.setSamplePosition(grabSamplePosition(sampleId));
	   // String sampleResearcherId = 
	   //		parent.isPlatformChosenAs("absciex") || !parent.getIncludeResearcherId() ? "" : 
	   //		sampleService.sampleNameForId(sampleId);
	
	    String sampleResearcherId = parent.isPlatformChosenAs("absciex") || !parent.getIncludeResearcherId() ? "" : idsVsResearcherNameMap.get(item.getSampleName());
	    sampleResearcherId = sampleResearcherId == null ? "" : edu.umich.brcf.shared.util.utilpackages.StringUtils.stripNonAlpha(sampleResearcherId);
	    String outname = parent.grabOutputFileName(
	    sampleId != null ? sampleId.trim() +  (sampleResearcherId .equals("") ? "" : "-" + sampleResearcherId ) : "", item);		
	    item.setOutputFileName(outname);	
	    item.setInjectionVolume(parent.getDefaultInjectionVol());
	   return item;
	   }

	
	private void buildOrderList(List<WorklistItemSimple> list)
		{
		randomOrderSamples.clear();
		for (int i = 0; i < list.size(); i++)
			randomOrderSamples.add(list.get(i).getSampleName());
		}


	private String grabSamplePosition(String sampleId)
		{
		String pos = sampleIdToPositionMap.get(sampleId);

		return pos != null ? pos : "";
		}

	
	private void addItemToListRandomly(WorklistItemSimple w, List<WorklistItemSimple> list)
		{
		int slot_for_new = (int) Math.floor(Math.random() * (nItemsLeft + 1));

		list.add(w);

		list.get(slot_for_new).setRandomIdx(nItemsLeft + 1);
		w.setRandomIdx(slot_for_new + 1);

		list.set(nItemsLeft, list.get(slot_for_new));

		list.set(slot_for_new, w);
		nItemsLeft++;
		}

	
	public String getAssayType()
		{
		return assayType;
		}

	
	public String extractAssayIdFromType()
		{
		String assayStr = getAssayType();

		if (StringUtils.isEmptyOrNull(assayStr))
			return "";

		String aid3 = StringUtils.lastTokenSplitOn(assayStr, "\\(");
		return (aid3 == null ? "" : aid3.replace(')', ' ').trim());
		}

	
	public String getRandomizationType()
		{
		return randomizationType;
		}

	public Boolean getIsRandomized()
		{
		return isRandomized;
		}

	public void setAssayType(String assay)
		{
		this.assayType = assay;
		parent.setDefaultAssayId(this.extractAssayIdFromType());
		}

	public void setRandomizationType(String rt)
		{
		randomizationType = rt;
		}

	public void setIsRandomized(Boolean is)
		{
		isRandomized = is;
		}

	public void setExpRandom(ExperimentRandomization er)
		{
		expRandom = er;
		}

	}

// InsertPointSafe

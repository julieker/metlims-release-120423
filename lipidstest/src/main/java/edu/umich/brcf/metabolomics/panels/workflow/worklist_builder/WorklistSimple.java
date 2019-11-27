////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistSimple.java
//  Written by Jan Wigginton
//  February 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
//import org.hibernate.mapping.Set;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

//DesEncrypter

public class WorklistSimple implements Serializable
	{
	// issue 287	
	@SpringBean
	private SampleService sampleService;
	// issue 394
	private Integer masterPoolsBefore = 3, masterPoolsAfter = 1, batchPoolsBefore = 1, batchPoolsAfter = 1;	
	String worklistName = "values";
	private String selectedPlatform  = null, selectedInstrument = null, selectedMode =  "Positive";
	private String runDate = "";
	private String defaultInjectionVol = "5.0";
	private String defaultMethodFileName = "";
	private String defaultExperimentId = "", defaultAssayId = "";
	private String maxItems = "50"; 
	private Boolean allSelected = false;
	private Boolean openForUpdates = true;
	private boolean plateWarningGiven = false, plateWarningGivenTwice = false;
	private boolean includeResearcherId = true; // issue 288 issue 312
	private boolean isCustomDirectoryStructure = false; // issue 32
	private String customDirectoryStructureName = "<custom directory>"; // issue 32
	private ArrayList <String> sampleNamesArray = new ArrayList<String>();
	private List <WorklistItemSimple> items = new ArrayList <WorklistItemSimple>();
	private List <WorklistSampleGroup> sampleGroupsList = new ArrayList<WorklistSampleGroup>();
	private List <WorklistControlGroup> controlGroupsList = new ArrayList<WorklistControlGroup>();
	private List<String> controlIds = new ArrayList <String>();
	// issue 394
	PlateListHandler plateListHandler; 
	int nPlates;	
	int rowsPerPlate = 6, colsPerPlate = 9;
	private boolean randomizeByPlate = true; // issue 416
    private boolean isCircularRelationship;
    private int lastPoolBlockNumber = 0;
    private Map<String, Integer> ctrlTypeToRunningTotal = new HashMap<String, Integer>();
	private List<Integer> largestPadding = new ArrayList <Integer>();
	private String poolTypeA; //issue 13
	private boolean bothQCMPandMP = false; // issue 17
	private int amountToPad = 2; // issue 16
	private int limitNumberControls = 99;
	private String lastSample ; // issue 29
	private int startingPoint ; // issue 29
	// issue 29
	public String getLastSample()
		{
		return lastSample;	
		}
	
	// issue 29
	public void setLastSample(String vLastSample)
		{
		lastSample = vLastSample;
		}
	
	// issue 29
	public int getStartingPoint()
		{
		return startingPoint;	
		}
	
	// issue 29
	public void setStartingPoint(int vStartingpoint)
		{
		startingPoint = vStartingpoint;
		}
	// issue 27
	// issue 25
	public int getAmountToPad ()
		{
		return amountToPad;
		}

    // issue 25
	public void setAmountToPad (int vAmountToPad)
		{
		amountToPad =  vAmountToPad;
		}
	
	// issue 17
	public boolean getBothQCMPandMP ()
		{
		return bothQCMPandMP;
		}
	
	// issue 16
	public int getLimitNumberControls ()
		{
		return limitNumberControls;
		}
	
	// issue 16
	public void setLimitNumberControls (int vLimitNumberControls)
		{
		limitNumberControls =  vLimitNumberControls;
		}
	
	// issue 17
	public void setBothQCMPandMP (boolean vBothQCMPandMP)
		{
		bothQCMPandMP = vBothQCMPandMP;
		}
	
    public WorklistSimple()
		{
		this("", "");
		}
	
    // issue 11
    public void clearOutPoolIDDAControls ()
		{
		((MedWorksSession) Session.get()).setNBatchPoolsAfter(0);
		((MedWorksSession) Session.get()).setNBatchPoolsBefore(0);
		((MedWorksSession) Session.get()).setNMasterPoolsBefore(0);
		((MedWorksSession) Session.get()).setNMasterPoolsAfter(0);
		((MedWorksSession) Session.get()).setNCE10Reps(0);
		((MedWorksSession) Session.get()).setNCE20Reps(0);
		((MedWorksSession) Session.get()).setNCE40Reps(0);	
		}	
    
    
    // issue 13
    public String getPoolTypeA()
		{
		return 	poolTypeA;
		}
    
    // issue 13
    public void setPoolTypeA(String vPoolTypeA)
		{
		poolTypeA = vPoolTypeA;
		}
    
	// issue 432 
	public int getLastPoolBlockNumber()
		{
		return 	lastPoolBlockNumber;
		}
	
	// issue 432
    public void setLastPoolBlockNumber(int lastPoolBlockNumber)
		{
		this.lastPoolBlockNumber = 	lastPoolBlockNumber;
		}
	
	// issue 426
	public boolean getIsCircularRelationship()
		{
		return isCircularRelationship;
		}

    public void setIsCircularRelationship(boolean isCircularRelationship)
		{
		this.isCircularRelationship = isCircularRelationship;	
		}
	
    // issue 426
    public String isThereCircular()
	    {
    	for (WorklistItemSimple tItem: getItems())
	    	{
	    	if (!tItem.getRepresentsControl())
	    		continue;
	    	if (isChainCircular(tItem))
	    		return tItem.getSampleName();
	    	}
	    return null;	    
	    }
    
    public boolean isChainCircular(WorklistItemSimple tItem)
	    {
    	WorklistItemSimple itemToCheck = tItem;
    	int loopingLimit= 0;
    	while (itemToCheck.getRelatedSample() != null && loopingLimit < 500)
	    	{
	    	for (WorklistItemSimple ttitem: getItems())
		    	{
	    		if (itemToCheck.getRelatedSample()== null)
		    		return false;
		    	if (ttitem.getSampleName().equals(itemToCheck.getRelatedSample()))
		    	    {
		    		itemToCheck = ttitem;
		    		break;
		    	    }		    
		    	}
	    	if (itemToCheck.getRelatedSample() == null)
	    		  return false;
	    	if (itemToCheck.getRelatedSample().equals(tItem.getSampleName()))
	    	      return true;
	    	loopingLimit++;
	    	}
    	  return false;
	    }
	// issue 350
	public Boolean wasCustomOrdered() 
        { 
        return (sampleGroupsList != null && sampleGroupsList.size() > 0  ? sampleGroupsList.get(0).wasCustomOrdered() : false);
        }
		
	public List<String> getSampleIds()
		{
		List<String> sampleIds = new ArrayList<String>();
		for (WorklistItemSimple s : items)
			{
			if (s.getRepresentsControl()) continue;
			sampleIds.add(s.getSampleName());
			}
		return sampleIds;
		}
	
	
	public WorklistSimple(String wln, String platform)
		{
		Injector.get().inject(this); // issue 287
		runDate = DateUtils.todaysDateAsString("MM/dd/yy");
		worklistName = wln;
		setSelectedPlatform(platform);
		setSelectedInstrument(null);
		plateListHandler = new PlateListHandler(rowsPerPlate, colsPerPlate, this.getUseCarousel());
		}

	
	public void updateRedoStatus()
		{
		for (int i = 0; i < getItems().size(); i++)
			getItems().get(i).setSelected(this.allSelected);
		}

	
	public void updateInjectionVolumes()
		{
		for (WorklistItemSimple itm : getItems())
			if (!itm.getIsDeleted())
				itm.setInjectionVolume(getDefaultInjectionVol());
		}
			
	
	public void updateMethodFileNames()
		{
		for (WorklistItemSimple itm : getItems())
			if (!itm.getIsDeleted())
				itm.setMethodFileName(getDefaultMethodFileName());
		}

	
	// issue 287
	public void updateOutputFileNames()
		{
		// Issue 297
		Map<String, String> idsVsReasearcherNameMap =
		    sampleService.sampleIdToResearcherNameMapForExpId(getDefaultExperimentId());
		for (WorklistItemSimple item : getItems())
			{
			
			// issue 288
			// getIncludeResearcherId is the actual sample name or researcher id
			// item.getSampleName is usually the sample id especially when it is pulling samples
			// from the database .  It can be a sample name if we are doing a custom randomization and uploading a CSV file
			// Issue 297
			String sampleResearcherId = this.isPlatformChosenAs("absciex") || !this.getIncludeResearcherId() ? "" : idsVsReasearcherNameMap.get(item.getSampleName());
			sampleResearcherId = sampleResearcherId == null ? "" : StringUtils.stripNonAlpha(sampleResearcherId);
			String outname = grabOutputFileName(sampleResearcherId.equals("") ? item.getSampleName(): item.getSampleName() + "-" + sampleResearcherId, item); 
			item.setOutputFileName(outname);			
			}
		}

	
	public void clearAllItems()
		{
		getItems().clear();
		this.allSelected = false;
		this.includeResearcherId = true;// issue 288 // issue 312
		}

	
	public void clearItemGroup(Boolean clearControls)
	 	{
		boolean clearSamples = !clearControls;
		getItems();
		
		List <WorklistItemSimple> keepers = new ArrayList <WorklistItemSimple>();
		
		for (int i = 0; i < items.size(); i++)
			{
			if (clearControls)
				{
				if (!(items.get(i).getRepresentsControl()))
					keepers.add(items.get(i));
				}
			else if (clearSamples && items.get(i).getRepresentsControl())
				keepers.add(items.get(i));
			}		
		
		items.clear();
		for (int i = 0; i < keepers.size(); i++)
			items.add(keepers.get(i));	
		}
	
	
    public void updateSampleNamesArray()
		{
		sampleNamesArray.clear();
		for (int i =0; i < items.size(); i++)
			{
			//items.get(i).updateSampleName();
			sampleNamesArray.add(getItem(i).getSampleName());
			}
		} 
	
    
    public void clearAll()
		{
		clearControlGroups();
		clearSampleGroups();
		setAllSelected(false);
		} 
	
    
	public void resetOptions(int i)
		{
		if (this.getItems().size() > i)
			getItem(i).resetOptions();
		}
	
	
	private void clearSampleGroups()
		{
		clearItemGroup(false);
		sampleGroupsList.clear();
		sampleGroupsList.add(new WorklistSampleGroup(this)); 
		}
	
	
	public void clearControlGroups()
		{
		clearItemGroup(true);
		controlGroupsList.clear();
		controlGroupsList.add(new WorklistControlGroup(this)); 
		}
	
	
	public void addControlGroup()
		{
		controlGroupsList.add(new WorklistControlGroup(this));
		}
	
	public void addControlGroup(WorklistControlGroup grp)
		{
		controlGroupsList.add(grp);
		}
	
	
	public void addSampleGroup()
		{
		sampleGroupsList.add(new WorklistSampleGroup(this));
		}
	
	
	public int countGroups(Boolean countControls)
		{
		if (countControls && controlGroupsList == null) 
			return 0;
		
		if (sampleGroupsList == null) 
			return 0;
		
		return countControls ? controlGroupsList.size() : sampleGroupsList.size();
		}

	
	public void updateItemsFromSampleGroups() //int totalWellsAvailable)
		{
		clearItemGroup(false);
		// issue 345
		sampleGroupsList.get(0).initializeIdsVsResearcherNameMap(sampleGroupsList.get(0).getExperimentId());
		for (int j = 0; j < countGroups(false); j++)
			addItems(getSampleGroup(j).getWorklistItemsForGroup()); //totalWellsAvailable));
		}


	public void rebuildForNewExperiment(String eid, List <String> controlIds)
		{
		clearAllItems();
		clearControlGroups();
		setDefaultExperimentId(eid);
		setControlIds(controlIds);
		} 
	
	// issue 456
	public List <WorklistItemSimple> populateOriginalItems(List <WorklistItemSimple> originalItems)
		{
		if (getItems().size() > 0)
	 	    {
			for (WorklistItemSimple titem: getItems())
				{
				originalItems.add(titem);	 
				}
	 	    }
		return originalItems;
		}
	
	// issue 456
	public  List<WorklistControlGroup> buildGroupListWithRelatedSamples (List <WorklistControlGroup> controlGroupsList)
		{
		List<WorklistControlGroup> controlGroupsRelatedToSamples = new ArrayList <WorklistControlGroup> ();
		if (controlGroupsList.size() > 0)
	 	    {
			for (WorklistControlGroup tControlGroup: controlGroupsList)
				{
				if (FormatVerifier.verifyFormat(Sample._2019Format, tControlGroup.getRelatedSample()))
					controlGroupsRelatedToSamples.add(tControlGroup);	 
				}
	 	    }
		return controlGroupsRelatedToSamples;
		}
	
	// issue 456
	public  List<WorklistControlGroup> buildGroupListWithRelatedControls (List <WorklistControlGroup> controlGroupsList)
		{
		List<WorklistControlGroup> controlGroupsRelatedToSamples = new ArrayList <WorklistControlGroup> ();
		if (controlGroupsList.size() > 0)
	 	    {
			for (WorklistControlGroup tControlGroup: controlGroupsList)
				{
				if (!FormatVerifier.verifyFormat(Sample._2019Format, tControlGroup.getRelatedSample()))
					controlGroupsRelatedToSamples.add(tControlGroup);	 
				}
	 	    }
		return controlGroupsRelatedToSamples;
		}
	
	// issue 456
	public  Map<String, Integer> populateRunningTotalMap ( Map<String, Integer> ctrlTypeToRunningTotal )
		{  
	    for (int i = 0; i< controlGroupsList.size(); i++) 
	        {  
        	String controlType = controlGroupsList.get(i).getControlType();
	        ctrlTypeToRunningTotal.put(StringParser.parseId(controlType), 1);	
	        } 
		return ctrlTypeToRunningTotal;
		}
	
	//////////////////////////////////	
	public void rebuildEverything()   
	    {  
		List <WorklistItemSimple>  originalItems  = new ArrayList <WorklistItemSimple> ();		
		//if (getItems().size() == 0)
		originalItems = populateOriginalItems(originalItems);
        clearAllItems();
        // issue 426
        List <WorklistItemSimple> controlItemsToAdd;
        colsPerPlate = (isPlatformChosenAs("absciex") ? 9 : 9);
        rowsPerPlate = (isPlatformChosenAs("absciex") ? 6 : 6);  
        Map <String, Integer> controlTypeMap = buildControlTypeMap();
        updateItemsFromSampleGroups();
       	       
        // issue 426 Do this if the related samples are SAMPLES
        List<WorklistControlGroup> controlGroupsRelatedToSamples = buildGroupListWithRelatedSamples(controlGroupsList);
        List<WorklistControlGroup> controlGroupsRelatedToControls = buildGroupListWithRelatedControls(controlGroupsList);
            
        for (int j = 0; j < controlGroupsRelatedToSamples.size(); j++)
	        {        	
	        WorklistControlGroup grp = controlGroupsRelatedToSamples.get(j);	        
	        String controlType = controlGroupsRelatedToSamples.get(j).getControlType();			        
	        controlItemsToAdd = grp.getWorklistItemsForGroup(0, (int) controlTypeMap.get(controlType));		
	        if (controlItemsToAdd.size() == 0)
		        continue;		
	        int insertPt = -99;
	        try 
	            { 
	        	insertPt = grp.getInsertPointSafe();
	        	}
	        catch (METWorksException e)
		        {
	        	// If we have -99 do missing control item:	        	
	            System.out.println("Exception : insert point calculated as " + insertPt);
	            continue;
		        }	
	        catch (Exception e)
		        {
		        e.printStackTrace();
		       // continue;
		        }	
	        // issue 29
	        for (int i = 0; i< controlItemsToAdd.size(); i++)
	            {
	        	controlItemsToAdd.get(i).setRelatedSample(grp.getRelatedSample());
	        	controlItemsToAdd.get(i).setDirection(grp.getDirection().equals("After") ? Constants.AFTER : Constants.BEFORE);
	            }
	        addItemsAt(insertPt, controlItemsToAdd); 
	        ctrlTypeToRunningTotal = new HashMap<String, Integer>();	               
	        ctrlTypeToRunningTotal = populateRunningTotalMap(ctrlTypeToRunningTotal) ; 
	        for (WorklistItemSimple item : getItems())
		        {           	
		      	if (item.getRepresentsControl())
		    	    {  
		    	    int indexOfDash = item.getSampleName().indexOf("-") > -1 ? item.getSampleName().lastIndexOf("-") : 0;	
		    		int iSuffix = ctrlTypeToRunningTotal.get(item.getSampleName().substring(0,indexOfDash));	        		
		    		item.setSampleName(item.getSampleName().substring(0,indexOfDash) + "-" + (iSuffix));
		    	    ctrlTypeToRunningTotal.put(item.getSampleName().substring(0,indexOfDash), ++iSuffix);            	
					String outname = grabOutputFileName(item.getSampleName(), item);
					item.setOutputFileName(outname);
					item.sampleWorklistLabel = item.getSampleWorklistLabel().replace("()","");	    				    	       	    
		    	    } 
		        }	        
            }            
        int cnt = 0;   
        for (int j = 0; j < controlGroupsRelatedToControls.size(); j++)
	        {	    
	        WorklistControlGroup grp = controlGroupsRelatedToControls.get(j);	 	      
	        String controlType = controlGroupsRelatedToControls.get(j).getControlType();	        
	        controlItemsToAdd = grp.getWorklistItemsForGroup(0, (int) controlTypeMap.get(controlType));			       
	        if (controlItemsToAdd.size() == 0)
		        continue;
	        int insertPt = -99;	  
	        try { insertPt = grp.getInsertPointSafe();}
	        catch (METWorksException e)
		        {
	        	// If we have -99 do missing control item:
	        	System.out.println("Exception : insert point calculated as " + insertPt);
	            insertPt = indexForPosition (grp.getRelatedSample().toString(), originalItems, grp.getDirection());
	            if (insertPt > getItems().size() -1)
	            	insertPt = getItems().size() -1;
	            if (insertPt == -1)
	                continue;
		        }	
	        catch (Exception e)
		        {
		        e.printStackTrace();
		       // continue;
		        }	
	        // issue 426
	        for (int i = 0; i< controlItemsToAdd.size(); i++)
	        	controlItemsToAdd.get(i).setRelatedSample(grp.getRelatedSample());
	        addItemsAt(insertPt, controlItemsToAdd); ;
	        // issue 411 issue 426	      
	        ctrlTypeToRunningTotal = new HashMap<String, Integer>();       
	        ctrlTypeToRunningTotal = populateRunningTotalMap(ctrlTypeToRunningTotal) ; 
	        for (WorklistItemSimple item : getItems())
		        {           	
		      	if (item.getRepresentsControl())
		    	    {  
		    	    int indexOfDash = item.getSampleName().indexOf("-") > -1 ? item.getSampleName().lastIndexOf("-") : 0;	 
		    		int iSuffix = ctrlTypeToRunningTotal.get(item.getSampleName().substring(0,indexOfDash));	        		
		    		item.setSampleName(item.getSampleName().substring(0,indexOfDash) + "-" + (iSuffix));
		    	    ctrlTypeToRunningTotal.put(item.getSampleName().substring(0,indexOfDash), ++iSuffix);            	
					String outname = grabOutputFileName(item.getSampleName(), item);
					item.setOutputFileName(outname);
					item.sampleWorklistLabel = item.getSampleWorklistLabel().replace("()","");	    				    	       	    
		    	    } 
		        }
	        }
        //// issue 456
            if (this.getSelectedPlatform().equals("agilent"))
	            {
            	// issue 29
            	this.setStartingPoint(0);
            	//Integer iTotalControlType = getPadding();
		        for (WorklistItemSimple item : getItems())
		        	{         	
		        	if (item.getRepresentsControl())
		    	    	{  
		        		String iSuffixStr = "";
		        		if (item.getSampleName().indexOf("-") > -1)
		        			iSuffixStr = item.getSampleName().substring(item.getSampleName().lastIndexOf("-")+1);
		        		else 
		        			continue;
		        		int indexUnderscore = item.getSampleName().lastIndexOf("-");        		
		        		//Integer iTotalControlType = ctrlTypeToRunningTotal.get(item.getSampleName().substring(0,indexUnderscore)) -1;// issue 486
		        	//	iSuffixStr =  String.format("%0" + iTotalControlType.toString().length() + "d", Integer.parseInt(iSuffixStr));
		        		iSuffixStr =  String.format("%0" + amountToPad + "d", Integer.parseInt(iSuffixStr));
		        		item.setSampleName(item.getSampleName().substring(0,indexUnderscore) + "-" + (iSuffixStr));
		        		String outname = grabOutputFileName(item.getSampleName(), item);
						item.setOutputFileName(outname);
						// issue 29
						if (item.getSampleName().contains(this.getPoolTypeA()) && item.getRelatedSample().equals(this.lastSample) && item.getDirection() == Constants.AFTER)
                            this.setStartingPoint(Integer.parseInt(iSuffixStr));
		    	    	}
		        	}
	            }
		    nPlates = updatePlatePositions();
        }       
	/////////////////////////////// issue 456 or 486 issue 4 in metlims.2019
	/*public Integer getPadding()
		{
		largestPadding = new ArrayList <Integer> ();
		for (Map.Entry<String,Integer> entry : ctrlTypeToRunningTotal.entrySet())  	          
			largestPadding.add(entry.getValue());
		Collections.sort(largestPadding);
		if (largestPadding.size() > 0 )		    
		    return largestPadding.get(largestPadding.size()-1) -1;
		else 
			return 0;		 
		}*/
	
	// issue 16
	// issue 19
	public CountPair getLargestControlTypeTotal() 
	    {
	    Map<String, Integer> controlTypeCts = new HashMap<String, Integer>();
	    int maxCt = 0;
	    String vControltype = "";
	    CountPair countPair = new CountPair();	    
	    for (WorklistControlGroup controlGroup :  getControlGroupsList())   
	        {
	        String type = controlGroup.getControlType();	
	        int iQuantity = controlGroup.getIntQuantity();
	        if (!controlTypeCts.containsKey(type))
	            controlTypeCts.put(type, 0);
	        Integer ct = controlTypeCts.get(type);
	        controlTypeCts.put(type, ct + iQuantity);
	        ct = ct + iQuantity;
	        if (ct > maxCt)
	            {
	            maxCt = ct;
	            vControltype = controlGroup.getControlType();
	            }
	        }
	    countPair.setTag(vControltype);
	    countPair.setCount(maxCt);
	    return countPair;
	    }
	
	public boolean isRelatedControlFoundInItems (String relatedControl)
		{
		for (WorklistItemSimple item: getItems())
			{
			if (item.getSampleName().trim().equals(relatedControl.trim()))
				return true;
			}
		return false;
		}
	
	// issue 456
	public int indexForPosition (String sampleName, List<WorklistItemSimple> originalItems, String direction)
		{
		int i = 0;
		for (WorklistItemSimple item: originalItems)
			{
			if (item.getSampleName().trim().equals(sampleName))
				return direction.equals("After") ? i + 1 : i;
			i++;
			}
		return -1;
		}
	
	public int updatePlatePositions() 
		{
		plateListHandler = new PlateListHandler(rowsPerPlate, colsPerPlate, this.getUseCarousel());
		try 
		    {
			// issue 407
			return isPlatformChosenAs("agilent") ? plateListHandler.updatePlatePositionsForAgilent(this) : 1;
		    } 
		catch (METWorksException e) 
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		    }
		}
	
	protected Map<String, Integer> buildControlTypeMap()
		{
		Map<String, Integer> controlTypeMap = new HashMap<String, Integer>();
		int nUniqueControlTypes = 0;

		for (int j = 0; j < controlGroupsList.size(); j++)
			{
			String controlType = controlGroupsList.get(j).getControlType();
			if (!controlTypeMap.containsKey(controlType))
				controlTypeMap.put(controlType, nUniqueControlTypes++);	
			}

		return controlTypeMap;
		}

	
	public int countSamples()
		{
		int sampleCt = 0;
		for (int i = 0; i < this.sampleGroupsList.size(); i++)
			sampleCt += sampleGroupsList.get(i).countWorklistItemsForGroup();
		
		return sampleCt;
		}
	
	
	public int countExcludedSamples()
		{
		int sampleCt = 0;
		for (int i = 0; i < this.sampleGroupsList.size(); i++)
			sampleCt += sampleGroupsList.get(i).countExcludedItemsForGroup();
		
		return sampleCt;
		}
	

	private int countPrevItemsOfType(WorklistControlGroup grp, int groupIdx)
		{
		String ctrlType = grp.getControlType();
		int groupStartIdx = 0;
		for (int i =0; i < groupIdx; i++)
			{
			WorklistControlGroup precedingGrp = controlGroupsList.get(i);
			if (precedingGrp != null && ctrlType != null)
				if (ctrlType.equals(precedingGrp.getControlType()))
					groupStartIdx += precedingGrp.getIntQuantity();
			}
		
		return groupStartIdx;
		}
	
	
	public void initializeControls()
		{
		if (controlGroupsList == null || controlGroupsList.size() < 1)
			for (int i = 0; i < 1; i++)
				controlGroupsList.add(new WorklistControlGroup(this));
		}
	
	
	public void initializeSamples()
		{
		for (int i = 0; i < 1; i++)
			sampleGroupsList.add(new WorklistSampleGroup(this));
		}
	
	
	public WorklistControlGroup getControlGroup(int i)
		{
		return controlGroupsList.get(i);
		}
	
	
	public WorklistSampleGroup getSampleGroup(int i)
		{
		return sampleGroupsList.get(i);
		}
	
	
	public void deleteControlItem(WorklistControlGroup item)
		{
		int i = 0;
		
		for (i = 0; i < controlGroupsList.size(); i++)
			if (item == controlGroupsList.get(i))
				break;
	
		controlGroupsList.remove(i);
		}
	
	
	public void deleteSampleItem(WorklistSampleGroup item)
		{
		int i = 0;
		for (i = 0; i < sampleGroupsList.size(); i++)
			if (item == sampleGroupsList.get(i))
				break;
	
		sampleGroupsList.remove(i);
		}
	
	
	public void deleteItem(WorklistItemSimple item)
		{
		int i = 0;
		for (i = 0; i < getItems().size(); i++)
			if (item == getItem(i))
				break;
		
		if (i >= getItems().size())
			return;
		
		getItem(i).setIsDeleted(true);
		if (item.getRepresentsControl())
		    getItems().remove(i);	
		
		updateIndices();
		}
	
	
	public void unDeleteItem(WorklistItemSimple item)
		{
		int i = 0;
		for (i = 0; i < getItems().size(); i++)
			if (item == getItem(i))
				break;
		
		if (i >= getItems().size())
			return;
		
		getItem(i).setIsDeleted(false);
		updateIndices();
		}
	
	
	public void updateIndices()
		{
		int j = 0; 
	
		if (getItems() == null || getItems().size() < 1)
			return;
		
		String pageName = getItems().get(0).getSamplePosition();
		String [] tokens = pageName.split("-");
		if (tokens.length > 1)
			pageName = tokens[0];
		
		for (WorklistItemSimple itm : getItems())
			{
			if (itm.getSamplePosition() != null && !itm.getSamplePosition().startsWith(pageName))
				{
				j = 0;
				pageName = itm.getSamplePosition();
				tokens = pageName.split("-");
				if (tokens.length > 1)
					pageName = tokens[0];
				}
				
			if (itm.getIsDeleted())
				itm.setRandomIdx("");
			else
				itm.setRandomIdx(++j);
			}
		}
	
	
	public List<WorklistItemSimple> getItems()
		{
		if (items == null)
			items = new ArrayList <WorklistItemSimple>();
		
		return items;
		}
	
	
	public WorklistItemSimple getItem(int i)
		{
		int sz = getItems().size();
		
		if (sz == 0)
			return null;
		
		if (i < 0 || i >= sz)
			return null;
		
		return items.get(i);		
		}
	
	
	public List <WorklistItemSimple> getItemsOnPage(int pg)
		{		
		int bottom = getMaxItemsAsInt() * pg;
		int top = Math.min(bottom + getMaxItemsAsInt(), getItems().size());
		
		List <WorklistItemSimple> pageItems = new ArrayList <WorklistItemSimple>();
		
		for (int i = bottom; i < top; i++)
			pageItems.add(getItem(i));
		
		return pageItems;
		}
	
	
	public void addItems(List <WorklistItemSimple> list)
		{
		if (list == null)
			return;
		
		for (WorklistItemSimple item : list)
			if (item != null)
				getItems().add(item);
		}
	
	
	public void addItemsAt(int i, List <WorklistItemSimple> list)
		{
		if (list == null)
			return;
		
		getItems().addAll(i, list);
		}
	
	//Please correct your entry in the default date field
	
	
	public String getRunDate()
		{
		return runDate;
		}
	
	
	public void setRunDate(String strDate)
		{
		runDate = strDate;
		}
	
	
	public String getFormattedRunDateString()
		{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return dateFormat.format(getRunDate());
		}
	

// Error in default date field

	public String getMaxItems()
		{
		return maxItems;
		}
	
	
	public Integer getMaxItemsAsInt()
		{
		Integer val;
		try { val = Integer.parseInt(getMaxItems()); }
		catch (Exception e) { return 50; }
		return val;
		}
		
	
	public void setMaxItems(Integer n)
		{
		setMaxItems(n.toString());
		}
	
	public void setMaxItems(String n)
		{
		String tmp = getMaxItems();
	
		try
			{
			Double.parseDouble(n);
			Integer val = Integer.parseInt(n);
			maxItems = val.toString();
			}
		catch (Exception e)
			{
			maxItems = tmp;
			}
		}
	
	public String getDefaultInjectionVol()
		{
		return defaultInjectionVol;
		}
	
	public void setDefaultInjectionVol(String v)
		{
		String tmp = defaultInjectionVol;
		try 
			{			
			// Ersatz validator... 
			Double.parseDouble(v);
			this.defaultInjectionVol = v; //newValue.toString();
			}
		catch (NumberFormatException | NullPointerException e)
			{
			defaultInjectionVol = tmp;
			System.out.println("Cant set injection volume to " + v + ". Change cancelled - Volume is " + getDefaultInjectionVol());
			}
		}

	public String getDefaultMethodFileName()
		{
		return defaultMethodFileName;
		}

	public void setDefaultMethodFileName(String mfn)
		{
		defaultMethodFileName = mfn;
		}
	
	// TO DO : THIS IS A HACK FOR SIMPLICITY __ ASSUMES ONLY ONE EXPERIMENT ID
	public void setDefaultExperimentId(String id)
		{
		defaultExperimentId = id;
		}	
	
	public String getDefaultExperimentId()
		{
		return defaultExperimentId;
		}
	
	public void setDefaultAssayId(String aid)
		{
		defaultAssayId = aid;
		}
	
	public String getDefaultAssayId()
		{
		return defaultAssayId;
		}
	
	public String getSelectedPlatform()
		{
		return selectedPlatform.toLowerCase();
		}
	
	
	public void setSelectedPlatform(String type)
		{
		if (type == null)
			type = "";
		
		selectedPlatform = type.trim().toLowerCase().equals("absciex") ? "ABSciex" : "Agilent";
		}
		
	
	public String getSelectedInstrument()
		{
		return selectedInstrument;
		}
	
	public void setSelectedInstrument(String s)
		{
		selectedInstrument = s;
		}
	
	
	public String getSelectedMode()
		{
		return selectedMode;
		}

	public void setSelectedMode(String m)
		{
		selectedMode = m;
		}

		
	public List <WorklistControlGroup> getControlGroupsList()
		{
		return this.controlGroupsList;
		}

	
	public List <WorklistSampleGroup> getSampleGroupsList()
		{
		return this.sampleGroupsList;
		}
	

	public String grabOutputFileName(String sampleLabel, WorklistItemSimple item)   
		{
		String tag = item.getSampleTypeTagForFilename();		
		String instrumentErrMsg = "Missing field : please select an instrument.";
		String dateErrMsg = "Please correct your entry in the default date field.  Date should be written in mm/dd/yy format.";		
		String dts, monthAsStr, yearStr;		
		try
			{
			Date date = DateUtils.dateFromDateStr(getRunDate(), "MM/dd/yy");
			String fullString = DateUtils.dateAsFullString(date);
			dts = DateUtils.grabYYYYmmddString(fullString);			
			DateUtils.dateFromDateStr(dts, "MM/dd/yy");
			monthAsStr = DateUtils.grabMonthString(getRunDate());
			yearStr = DateUtils.grabYearString(getRunDate());
			}
		catch (ParseException e)
			{
			return dateErrMsg;
			}		
		if (getSelectedInstrument() == null)
			return instrumentErrMsg;			
		String [] instrumentLine = getSelectedInstrument().split("\\s");
		String instrument =  (instrumentLine.length > 0) ? instrumentLine[0] : "Error";		
		String repeatTag = item.isSelected() ? "2" : "";
	// Error in default date field
		if ("absciex".equalsIgnoreCase(this.getSelectedPlatform()))
			return grabAbsciexOutputName(sampleLabel, tag, getDefaultExperimentId(), getDefaultAssayId(), yearStr, monthAsStr,
					dts, instrument, getSelectedMode().equals("Positive") ? "Pos" : "Neg", repeatTag);		
		if (instrument.equals("Unknown") || instrument.equalsIgnoreCase("IN0024") || instrument.equals(""))
			return instrumentErrMsg;	
		if (getUseGCOptions())
			//issue 414
			return grabNameWithoutPath(sampleLabel, "", getDefaultExperimentId(), this.getDefaultAssayId(), yearStr, monthAsStr, 
		        dts, instrument, (getSelectedMode().contains("Positive") ? "P" : "N") + repeatTag); // issue 450
			//return grabNameWithoutPath(sampleLabel, tag, getDefaultExperimentId(), this.getDefaultAssayId(), yearStr, monthAsStr, 
				//	dts, instrument, (getSelectedMode().equals("Positive") ? "P" : "N") + repeatTag);		
		return grabAgilentOutputName(sampleLabel, tag, getDefaultExperimentId(), this.getDefaultAssayId(), yearStr, monthAsStr, 
		    dts, instrument, (getSelectedMode().contains("Positive") ? "P" : "N") + repeatTag); // issue 450
		}


	// issue 432
	public String grabOutputFileNameIDDA()   
		{
				
		String instrumentErrMsg = "Missing field : please select an instrument.";
		String dateErrMsg = "Please correct your entry in the default date field.  Date should be written in mm/dd/yy format.";		
		String dts, monthAsStr, yearStr;
		String IDDAFileName = "";
		try
			{
			Date date = DateUtils.dateFromDateStr(getRunDate(), "MM/dd/yy");
			String fullString = DateUtils.dateAsFullString(date);
			dts = DateUtils.grabYYYYmmddString(fullString);			
			DateUtils.dateFromDateStr(dts, "MM/dd/yy");
			monthAsStr = DateUtils.grabMonthString(getRunDate());
			yearStr = DateUtils.grabYearString(getRunDate());
			}
		catch (ParseException e)
			{
			return dateErrMsg;
			}	
		if (getSelectedInstrument() == null)
			return instrumentErrMsg;			
		String [] instrumentLine = getSelectedInstrument().split("\\s");
		String instrument =  (instrumentLine.length > 0) ? instrumentLine[0] : "Error";		
	// Error in default date field		
		if (instrument.equals("Unknown") || instrument.equalsIgnoreCase("IN0024") || instrument.equals(""))
			return instrumentErrMsg;			
		IDDAFileName = dts + "-" + getDefaultExperimentId() + "-" +  getDefaultAssayId() + "-" + instrument  ;		
		return IDDAFileName;
		}
	
	// Neat Blank
	public String grabAbsciexOutputName(String sampleLabel, String tag, String expId, String aid, String yearStr, String monthAsStr, 
			String dts,  String instrument, String mode, String repeatTag)
		{
		String tagLabel = (tag != null && tag.length() > 0) ? tag + "_" : "";
		String modeTagForFilename = mode.equals("Pos") ? "P" : "N";
		
		return ("" + yearStr + "\\" + dts + "_" + expId + "\\" + mode + "\\" +  dts + "_" + tagLabel + sampleLabel + "_" + modeTagForFilename + repeatTag);
		}
	
	
	public String grabNameWithoutPath(String sampleLabel, String tag, String expId, String aid, String yearStr, String monthAsStr, 
			String dts,  String instrument, String mode)
		{
		String tagLabel = (tag != null && tag.length() > 0) ? tag + "-" : "";
		return dts + "-" + expId + "-" + aid + "-" + instrument + "-" + tagLabel + sampleLabel + "-" + mode;
		}

	// issue 368 and 394 
	public String grabAgilentOutputName(String sampleLabel, String tag, String expId, String aid, String yearStr, String monthAsStr, 
			String dts,  String instrument, String mode)
		{
		String tagLabel = ""; //(tag != null && tag.length() > 0) ? tag + "-" : "";
		return "D:\\MassHunter\\Data\\" + yearStr + "\\" +  monthAsStr + "\\" + expId +  "\\"  + dts + "-" + expId + "-" + aid
 					+ "-" + instrument + "-" + tagLabel + sampleLabel + "-" + mode;
		}
	

	public Boolean sampleExperimentIsSelected()
		{
		if (sampleGroupsList != null && sampleGroupsList.size() > 0)
			System.out.println("Experiment selected is " + sampleGroupsList.get(0).getExperimentId());
		
		return sampleGroupsList != null && sampleGroupsList.size() > 0 && !sampleGroupsList.get(0).getExperimentId().trim().equals("");
		}

	
	public String getWorklistName()
		{
		String dts = "00/00/00";
		String expId = this.getDefaultExperimentId();
		String aid = this.getDefaultAssayId();
		
		try
		// issue 282
			{
			Date date = DateUtils.dateFromDateStr(getRunDate(), "MM/dd/yy");
			String fullString = DateUtils.dateAsFullString(date);
			dts = DateUtils.grabYYYYmmddString(fullString);		
			}
		catch (ParseException e)
			{
			e.printStackTrace();
			return "Worklist";
			}
		
		String firstPrintedItem = "", lastPrintedItem = "";
		WorklistItemSimple itm;

		for (int i = 0; i < getItems().size(); i++)
			{
			itm = getItem(i);
			
			if (!itm.getIsDeleted())
				{
				firstPrintedItem = itm.getSampleName();
				break;
				}
			}
			
		for (int i = getItems().size() - 1; i >= 0; i--)
			{
			itm = getItem(i);
			
			if (!itm.getIsDeleted())
				{
				lastPrintedItem = itm.getSampleName();
				break;
				}
			}
		
		String mode = getSelectedMode() == null ? "" : getSelectedMode();
		String pmode = mode.equals("Positive") ? "P"  : (mode.equals("Negative") ? "N" : "PN") ; // issue 450
		String instrument = getSelectedInstrument() == null ? "" : getSelectedInstrument().replaceAll("\\s", "");
		String name = "Worklist" + dts + "-" + expId  + "-" + aid + "-" + instrument + "-" + pmode;
		return name;
		}
	
	
	public ArrayList<String> getSampleNamesArray()
		{
		return sampleNamesArray;
		}
	
	public ArrayList<String> getSampleNamesArrayWithoutGroup(WorklistGroup excludeGroup)
		{
		ArrayList<String> filteredNames = new ArrayList<String>();
		
		filteredNames.add("");
		
		if (excludeGroup == null)
			return filteredNames;
		
		for (int i =0; i < items.size(); i++)
			{
			WorklistItemSimple item = getItem(i);
			if (item.getGroup() == excludeGroup)
				continue;
			
			filteredNames.add(getItem(i).getSampleName());
			}
		
		return filteredNames;
		}
	
	
	public List<String> getColTitles()
		{
		String platform = this.getSelectedPlatform();
		
		if ("absciex".equalsIgnoreCase(platform))	
			return grabAbSciexColTitles();
		
		return grabAgilentColTitles();
		}

	
	private List<String> grabAbSciexColTitles()
		{
		return Arrays.asList(new String [] {"Order", "Sample Name", "Rack Code", "Rack Position",
				"Plate Code", "Plate Position", "Vial Position", "Acquisition Method", "Data File",
				"Injection Vol"});
		}
	
	
	private List<String> grabAgilentColTitles()
		{
		return Arrays.asList(new String [] {"Order", "Sample Name", "Sample Position", "Injection Vol",
				"Method", "Override DA", "Data File"});
		}

	public Boolean getUseGCOptions()
		{
		if (getSelectedInstrument() == null)
				return false;
		
		String si = getSelectedInstrument().trim();
		// TO DO : Replace this with db lookup...
		
		return si.startsWith("IN0025") || si.startsWith("IN0002") || si.startsWith("IN0028"); 
		}
	
	// IN0028
	public Boolean getUseCarousel()
		{
		if (getSelectedInstrument() == null)
				return false;		
		String si = getSelectedInstrument().trim();		
		//return si.startsWith("IN0025") || si.startsWith("IN0028"); 
		return si.startsWith("IN0025") ; // issue 296
		}

	public boolean isPlatformChosenAs(String platform)
		{
		return getSelectedPlatform() != null ? getSelectedPlatform().equals(platform) : false;
		}
	
	
	public void setWorklistName(String name)
		{
		worklistName = name;
		}
	
	public void setIncludeResearcherId(Boolean is) // issue 288
		{
		includeResearcherId= is;
		}
	
	public Boolean getIncludeResearcherId() // issue 288
		{
		return includeResearcherId;
		}
	
	// issue 32
	public void setIsCustomDirectoryStructure(Boolean isCustStruct) // issue 288
		{
		isCustomDirectoryStructure= isCustStruct;
		}

    public Boolean getIsCustomDirectoryStructure() // issue 288
		{
		return isCustomDirectoryStructure;
		}
    
 // issue 32
 	public void setCustomDirectoryStructureName(String vcustStructName) // issue 288
 		{
 		customDirectoryStructureName= vcustStructName;
 		}

     public String getCustomDirectoryStructureName() // issue 288
 		{
 		return customDirectoryStructureName;
 		}
    	
	public void setRandomizeByPlate(Boolean rp) // issue 416
		{
		randomizeByPlate= rp;		
		}
	
	public Boolean getRandomizeByPlate() // issue 416
		{
		return randomizeByPlate;
		}
	
	public void setAllSelected(Boolean as)
	    {
		allSelected = as;
	    }

    public Boolean getAllSelected()
	    {
	    return allSelected;
	    }

	public void setControlIds(List<String> list)
		{
		controlIds.clear();
		for (int i = 0; i < list.size(); i++)
			controlIds.add(list.get(i));
		
		}
	public List<String> getControlIds()
		{
		return controlIds;
		}
	
	public String getSelectedPlatformId()
		{
		return (this.getSelectedPlatform().trim().toLowerCase().equals("absciex") ? "PL002" : "PL001");
		}
	
	public int getNPlates()
		{
		return nPlates;
		}
	
	public void setNPlates(int nPlates)
		{
		this.nPlates = nPlates;
		}

	public void setOpenForUpdates(boolean open)
		{
		this.openForUpdates = open;
		}

	public Boolean getOpenForUpdates()
		{
		return this.openForUpdates;
		}
	
	public boolean isPlateWarningGivenTwice()
		{
			return plateWarningGivenTwice;
		}

	public void setPlateWarningGivenTwice(boolean plateWarningGivenTwice)
		{
			this.plateWarningGivenTwice = plateWarningGivenTwice;
		}

	public boolean isPlateWarningGiven()
		{
			return plateWarningGiven;
		}

	public void setPlateWarningGiven(boolean plateWarningGiven)
		{
			this.plateWarningGiven = plateWarningGiven;
		}
	
	// issue 301
    private int countDirAfterPrevItemsOfType(WorklistControlGroup grp, int groupIdx)
	    {
	    String ctrlType = grp.getControlType();
	    int groupStartIdx = 0;
	    for (int i =0; i < groupIdx; i++)
		    {
		    WorklistControlGroup precedingGrp = controlGroupsList.get(i);
		    if (precedingGrp != null && ctrlType != null)
			    if ("After".equals(precedingGrp.getDirection()))
				    if (ctrlType.equals(precedingGrp.getControlType()))
					    groupStartIdx += precedingGrp.getIntQuantity();
		    }
	
	    return groupStartIdx;
	    }
    
    public Integer getMasterPoolsBefore() 
	    {
	    return masterPoolsBefore;
	    }

    // issue 394
    public void setMasterPoolsBefore(Integer masterPoolsBefore) 
	    {
	    this.masterPoolsBefore = masterPoolsBefore;
	    }

    public Integer getMasterPoolsAfter() 
	    {
	    return masterPoolsAfter;
	    }

	public void setMasterPoolsAfter(Integer masterPoolsAfter) 
		{
		this.masterPoolsAfter = masterPoolsAfter;
		}

	public Integer getBatchPoolsBefore() 
		{
		return batchPoolsBefore;
		}

	public void setBatchPoolsBefore(Integer batchPoolsBefore) 
		{
		this.batchPoolsBefore = batchPoolsBefore;
		}
	
	public Integer getBatchPoolsAfter() 
		{
		return batchPoolsAfter;
		}
	
	public void setBatchPoolsAfter(Integer batchPoolsAfter) 
		{
		this.batchPoolsAfter = batchPoolsAfter;
		}
	
	public void updatePoolReplicates(int nMasterBefore, int nMasterAfter, int nBatchBefore, int nBatchAfter) 
		{
		this.masterPoolsBefore = nMasterBefore;
		this.masterPoolsAfter = nMasterAfter;
		this.batchPoolsBefore = nBatchBefore;
		this.batchPoolsAfter = nBatchAfter;
		}
	}
	
/*Pool

public void updateIndices()
	{
	int j = 0; 

	
	for (WorklistItemSimple itm : getItems())
//	for (int i = 0; i < getItems().size(); i++)
//		{
//		WorklistItemSimple itm = getItem(i);
		
		if (j >= this.getMaxItemsAsInt())
			{
			//fillPageWithInvisibleItems(i % (2 * this.maxItems));
			j = 0;
			}
		
		if (itm.getIsDeleted())
			itm.setRandomIdx("");
		else
			itm.setRandomIdx(++j);
		}
	}
*/




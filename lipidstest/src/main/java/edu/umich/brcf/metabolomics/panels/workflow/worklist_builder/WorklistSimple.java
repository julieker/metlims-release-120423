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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
//import org.hibernate.mapping.Set;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.service.SampleService;
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
	Map<String, String> idsVsReasearcherNameMap = new HashMap<String, String> ();
	private Integer masterPoolsBefore = 3, masterPoolsAfter = 1, batchPoolsBefore = 1, batchPoolsAfter = 1;	
	String worklistName = "values";
	private String selectedPlatform  = null, selectedInstrument = null, selectedMode =  "Positive";
	private String runDate = "";
	private String defaultInjectionVol = "5.0";
	private String startSequence = "1";
	private String defaultMethodFileName = "";
	private String defaultExperimentId = "", defaultAssayId = "";
	int maxSamples96Wells = 88;
	// issue 217
	private int maxStartPlate = 4;	
	private int maxNumberSupportedPlates = 6;
	// JAK new preview 
	private String maxItems = "96";
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
	private boolean randomizeByPlate = false; // issue 416 issue 179
	private boolean is96Well = false; // issue 212
	private boolean defaultPool = true; // issue 169
    private boolean isCircularRelationship;
    private int lastPoolBlockNumber = 0;
    private Map<String, Integer> ctrlTypeToRunningTotal = new HashMap<String, Integer>();
	private List<Integer> largestPadding = new ArrayList <Integer>();
	private String poolTypeA; //issue 13
	private boolean bothQCMPandMP = false; // issue 17
	public int amountToPad = 2; // issue 16
	private int limitNumberControls = 99;
	private String lastSample ; // issue 29
	private int startingPoint ; // issue 29
	private String chearBlankType = "Plasma";// issue 181
	private int nGastroExercise = 0, nGastroSedentary = 0,nLiverExercise = 0,
	    nLiverSedentary = 0,nAdiposeExercise = 0,nAdiposeSedentary = 0,
		nPlasmaExercise = 0,
		nPlasmaSedentary = 0,
		nRatPlasma = 0,
		nRatG = 0, 
		nRatL = 0,
		nRatA = 0;
	private int nCE10Reps, nCE20Reps, nCE40Reps;
	// issue 126
	private int nMuscleHumanMale = 0;
	private int nMuscleHumanFemale = 0;
	private int nHumanMuscleCntrl = 0;
	private int nRefStdA = 0;
	private int nRefStdB = 0;
	private int nRefStdC = 0;
	private int nRefStdD = 0;
	private int nRefStdE = 0;
	
	
	
	private int nKidneyExercise = 0;
	private int nKidneySedentary = 0;
	private int nHeartExercise = 0;
	private int nHeartSedentary = 0;
	private int nBrownAdiposeExercise  = 0;
	private int nBrownAdiposeSedentary = 0;
	private int nHippoCampusExercise = 0;
	private int nHippoCampusSedentary = 0;  
	private boolean chosenOtherSample = false;
	private boolean chosenOtherSampleMotrPAC = false;
	private int nLungExercise = 0;
	private int nLungSedentary = 0;
	public int nPlasmaHumanFemale  = 0;
	public int nPlasmaHumanMale  = 0;
	private int startPlateControls = 0;
	private String startPlate = "1";
	List <WorklistItemSimple> itemsMovedNewPositions = new ArrayList <WorklistItemSimple> ();
	Map<String, String> controlsMovedMap = new HashMap<String, String> ();
	// issue 229 
    private List <String> iddaStrList = new ArrayList <String> ();
 // issue 229 
    Map<String, String> controlCommentsMap = new HashMap<String, String> ();
    
    
    
    // issue 229
	public List<String> getIddaStrList()
		{
		return iddaStrList;
		}
	
    // issue 229
	public void setIddaStrList(List <String> iddaStrList )
		{
		this.iddaStrList =  iddaStrList;
		}
	
	// issue 179	
	private boolean changeDefaultInjVolume = false;
	// issue 186
	

	// issue 205
	public Map<String, String> getControlsMovedMap()
		{
		return controlsMovedMap;
		}
	
	// issue 205
	public void setControlsMovedMap (Map<String, String> controlsMovedMap) 
		{
		this.controlsMovedMap = controlsMovedMap;
		}
	
	// issue 205
	public Map<String, String> getControlCommentsMap()
		{
		return controlCommentsMap;
		}
	
	// issue 205
	public void setControlCommentsMapp (Map<String, String> controlCommentsMap) 
		{
		this.controlCommentsMap = controlCommentsMap;
		}
	
	// issue 205
	public List <WorklistItemSimple>  getItemsMovedNewPositions ()
		{
		return itemsMovedNewPositions;
		}
	
	// issue 205
	public void setItemsMovedNewPositions (List <WorklistItemSimple> itemsMovedNewPositions)
		{
		this.itemsMovedNewPositions = itemsMovedNewPositions;
		}
	
	
	// issue 153
	public String getStartPlate ()
		{
		return startPlate;
		}
	
	public void setStartPlate (String startPlate)
		{
		this.startPlate = startPlate;
		}
	
	// issue 186
	public String getChearBlankType ()
		{
		return chearBlankType;
		}
	
	public void setChearBlankType  (String chearBlankType)
		{
		this.chearBlankType = chearBlankType;
		}
		
	// issue 126
	public int getNHumanMuscleCntrl ()
		{
		return nHumanMuscleCntrl;
		}
	public void setNHumanMuscleCntrl (int nHumanMuscleCntrl)
		{
		this.nHumanMuscleCntrl = nHumanMuscleCntrl;
		}
	
	// issue 235
	/********************************************************/
	public int getNRefStdA ()
		{
		return nRefStdA;
		}
	public void setNRefStdA (int nRefStdA)
		{
		this.nRefStdA = nRefStdA;
		}
	
	public int getNRefStdB ()
		{
		return nRefStdB;
		}
	public void setNRefStdB (int nRefStdB)
		{
		this.nRefStdB = nRefStdB;
		}
	
	public int getNRefStdC ()
		{
		return nRefStdC;
		}
	public void setNRefStdC (int nRefStdC)
		{
		this.nRefStdC = nRefStdC;
		}
	
	public int getNRefStdD ()
		{
		return nRefStdD;
		}
	public void setNRefStdD (int nRefStdD)
		{
		this.nRefStdD = nRefStdD;
		}
	
	public int getNRefStdE ()
		{
		return nRefStdE;
		}
	public void setNRefStdE (int nRefStdE)
		{
		this.nRefStdE = nRefStdE;
		}
	
	
	
	/*********************************************************/
	
	// issue 217
	public int getMaxStartPlate ()
		{
		return maxStartPlate;
		}
	public void setMaxStartPlate (int maxStartPlate)
		{
		this.maxStartPlate = maxStartPlate;
		}	
	// issue 229
	public int getMaxNumberSupportedPlates ()
		{
		return maxNumberSupportedPlates;
		}
	public void setmaxNumberSupportedPlates (int maxNumberSupportedPlates)
		{
		this.maxNumberSupportedPlates = maxNumberSupportedPlates;
		}	
	// issue 6
	public boolean getChosenOtherSample ()
		{
		return chosenOtherSample;
		}
	public void setChosenOtherSample (boolean vchosenOtherSample)
		{
		chosenOtherSample = vchosenOtherSample;
		}
	
	// issue 126
	public int getNMuscleHumanMale ()
		{
		return nMuscleHumanMale;
		}
	
	public void setNMuscleHumanMale (int nMuscleHumanMale)
		{
		this.nMuscleHumanMale = nMuscleHumanMale;	
		}
	
	// issue 193
	public int getNPlasmaHumanMale ()
		{
		return nPlasmaHumanMale;
		}
   // issue 193
	public void setNPlasmaHumanMale (int nPlasmaHumanMale)
		{
		this.nPlasmaHumanMale = nPlasmaHumanMale;	
		}
	
	// issue 193
	public int getNPlasmaHumanFemale ()
		{
		return nPlasmaHumanFemale;
		}
   // issue 193
	public void setNPlasmaHumanFemale (int nPlasmaHumanFemale)
		{
		this.nPlasmaHumanFemale = nPlasmaHumanFemale;	
		}
	// issue 212
	
	public int getNMuscleHumanFemale ()
		{
		return nMuscleHumanFemale;
		}
	public void setNMuscleHumanFemale (int nMuscleHumanFemale)
		{
		this.nMuscleHumanFemale = nMuscleHumanFemale;
		}
	
	
	
	// issue 212
	public int getMaxSamples96Wells ()
		{
		return maxSamples96Wells;
		}
	public void setMaxSamples96Wells (int maxSamples96Wells)
		{
		this.maxSamples96Wells = maxSamples96Wells;
		}
	public boolean getChosenOtherSampleMotrPAC ()
		{
		return chosenOtherSampleMotrPAC;
		}
	public void setChosenOtherSampleMotrPAC (boolean vchosenOtherSampleMotrPAC)
		{
		chosenOtherSampleMotrPAC =vchosenOtherSampleMotrPAC;
		}
	
	// issue 6
	public int getNGastroExercise()
		{
		return 	nGastroExercise;
		}
	
	public int getNGastroSedentary()
		{
		return 	nGastroSedentary;
		}
	
	public int getNLiverExercise()
		{
		return 	nLiverExercise;
		}
	
	public int getNLiverSedentary()
		{
		return 	nLiverSedentary;
		}

	public int getNAdiposeExercise()
		{
		return 	nAdiposeExercise;
		}

	public int getNAdiposeSedentary()
		{
		return 	nAdiposeSedentary;
		}
	
	public int getNPlasmaExercise()
		{
		return 	nPlasmaExercise;
		}

	public int getNPlasmaSedentary()
		{
		return 	nPlasmaSedentary;
		}

	public int getNRatPlasma()
		{
		return 	nRatPlasma;
		}
	
	public int getNRatG()
		{
		return 	nRatG;
		}

	public int getNRatL()
		{
		return 	nRatL;
		}
	
	public int getNRatA()
		{
		return 	nRatA;
		}
	
	// issue 56
	public int getNCE10Reps()
		{
		return 	nCE10Reps;
		}
	
	public int getNCE20Reps()
		{
		return 	nCE20Reps;
		}
	
	public int getNCE40Reps()
		{
		return 	nCE40Reps;
		}
	
	public void setNCE10Reps(int vnce10reps)
		{
		this.nCE10Reps = vnce10reps;
		}

	public void setNCE20Reps(int vnce20reps)
		{
		this.nCE20Reps = vnce20reps;
		}

	public void setNCE40Reps(int vnce40reps)
		{
		this.nCE40Reps = vnce40reps;
		}
	
	// issue 6
	
	public int getNLungExercise()
		{
		return 	nLungExercise;
		}

	public int getNLungSedentary()
		{
		return 	nLungSedentary;
		}
	
	
	
	public int getNKidneyExercise()
		{
		return 	nKidneyExercise;
		}
	
	public int getNKidneySedentary()
		{
		return 	nKidneySedentary;
		}

	public int getNHeartExercise()
		{
		return 	nHeartExercise;
		}
	
	public int getNHeartSedentary()
		{
		return 	nHeartSedentary;
		}
	
	public int getNBrownAdiposeExercise()
		{
		return 	nBrownAdiposeExercise;
		}

	public int getNBrownAdiposeSedentary ()
		{
		return 	nBrownAdiposeSedentary ;
		}

	public int getNHippoCampusExercise()
		{
		return 	nHippoCampusExercise;
		}
	
	public int getNHippoCampusSedentary()
		{
		return 	nHippoCampusSedentary;
		}
	
	// issue 6 
	public void setNGastroExercise(int nGastroExercise)
		{
		this.nGastroExercise = nGastroExercise;
		}
	
	public void setNGastroSedentary(int nGastroSedentary)
		{
		this.nGastroSedentary = nGastroSedentary;
		}
	
	public void setNLiverExercise(int nLiverExercise)
		{
		this.nLiverExercise = nLiverExercise;
		}
	
	public void setNLiverSedentary(int nLiverSedentary)
		{
		this.nLiverSedentary = nLiverSedentary;
		}

	public void setNAdiposeExercise(int nAdiposeExercise)
		{
		this.nAdiposeExercise = nAdiposeExercise;
		}

	public void setNAdiposeSedentary(int nAdiposeSedentary)
		{
		this.nAdiposeSedentary = nAdiposeSedentary;
		}
	
	public void setNPlasmaExercise(int nPlasmaExercise)
		{
		this.nPlasmaExercise = nPlasmaExercise;
		}

	public void setNPlasmaSedentary(int nPlasmaSedentary)
		{
		this.nPlasmaSedentary = nPlasmaSedentary;
		}

	public void setNRatPlasma (int nRatPlasma)
		{
		this.nRatPlasma = nRatPlasma;
		}
	
	public void setNRatG (int nRatG)
		{
		this.nRatG = nRatG;
		}
	
	public void setNRatL (int nRatL)
		{
		this.nRatL = nRatL;
		}
		
	public void setNRatA (int nRatA)
		{
		this.nRatA = nRatA;
		}
	
	public void setNLungExercise (int nLungExercise)
		{
		this.nLungExercise = nLungExercise;
		}

	public void setNLungSedentary (int nLungSedentary)
		{
		this.nLungSedentary = nLungSedentary;
		}
	
	public void setNKidneyExercise (int nKidneyExercise)
		{
		this.nKidneyExercise = nKidneyExercise;
		}

	public void setNKidneySedentary (int nKidneySedentary)
		{
		this.nKidneySedentary = nKidneySedentary;
		}
	
	public void setNHeartExercise (int nHeartExercise)
		{
		this.nHeartExercise = nHeartExercise;
		}
	
	public void setNHeartSedentary(int nHeartSedentary)
		{
		this.nHeartSedentary = nHeartSedentary;
		}
	
	public void setNBrownAdiposeExercise (int nBrownAdiposeExercise)
		{
		this.nBrownAdiposeExercise = nBrownAdiposeExercise;
		}
	
	public void setNBrownAdiposeSedentary (int nBrownAdiposeSedentary)
		{
		this.nBrownAdiposeSedentary = nBrownAdiposeSedentary;
		}
	
	public void setNHippoCampusExercise (int nHippoCampusExercise)
		{
		this.nHippoCampusExercise = nHippoCampusExercise;
		}
	
	public void setNHippoCampusSedentary (int nHippoCampusSedentary)
		{
		this.nHippoCampusSedentary = nHippoCampusSedentary;
		}
	
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
    // issue 56
    public void clearOutPoolIDDAControls ()
		{
    	setMasterPoolsBefore(0);
    	setMasterPoolsAfter(0);
    	setBatchPoolsBefore(0);
    	setBatchPoolsAfter(0);
    	setNCE10Reps(0);
    	setNCE20Reps(0);
    	setNCE40Reps(0);	
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
		//	System.out.println("here is outname:" + outname + " here is outputname:" + item.getOutputFileName() + " " + item.getOutputFileDir());
		//	System.out.println("here is outnamewithdir:" + item.getO;
			item.setOutputFileNameWithDir(item.getGroup().getParent().getIsCustomDirectoryStructure() ?  item.grabDataFileWithCustomDirectory() : outname);
			item.setOutputFileName(outname);	
		//	System.out.println("Here is outname:" + outname + " here is outputfilenamwithdir:" + item.getOutputFileNameWithDir());
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
		Map <String, String> commentMap = new HashMap <String, String> ();		
		List <WorklistItemSimple>  originalItems  = new ArrayList <WorklistItemSimple> ();		
		
		// issue 128
		originalItems = populateOriginalItems(originalItems);
		if (this.getSelectedPlatform().equals("agilent"))
			commentMap = buildCommentMap(originalItems);
		clearAllItems();
        // issue 426
        List <WorklistItemSimple> controlItemsToAdd;
     // JAK new preview
       // colsPerPlate = (isPlatformChosenAs("absciex") ? 12 : 12);
        //rowsPerPlate = (isPlatformChosenAs("absciex") ? 8 : 8);  
        
        colsPerPlate = (isPlatformChosenAs("absciex") ? 12 : ( is96Well ?   12 : 9));
        rowsPerPlate = (isPlatformChosenAs("absciex") ? 8 : ( is96Well ?   8 : 6));
        
        
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
						// issue 46
						if (this.getPoolTypeA() != null)
							{
							if (item.getSampleName().contains(this.getPoolTypeA()) && item.getRelatedSample().equals(this.lastSample) && item.getDirection() == Constants.AFTER)
	                            this.setStartingPoint(Integer.parseInt(iSuffixStr));
							}
		    	    	}
		        	}
	            }
            // issue 128
            if (this.getSelectedPlatform().equals("agilent"))
                populateComments(getItems(),commentMap);
            nPlates = updatePlatePositions();
        }       
	
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
		// issue 205
		return sampleGroupsList.size() > 0 ? sampleGroupsList.get(i) : new WorklistSampleGroup();
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
	
	// issue 128
	public void setItems(List<WorklistItemSimple> items)
		{
		this.items = items;
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
// issue 212 check 96well
	public String getMaxItems()
		{
		if (is96Well)
			return "96";
		else
			return "54";
				
		//return maxItems;
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
	
	// issue 166
	public String getStartSequence()
		{
		return startSequence;
		}
	/////////////////////////////////////////////

	////////////////////////////////////////////
	
	// issue 166
	public void setStartSequence (String startSequence)
		{
		String tmp = this.startSequence;
		try
			{
			Integer.parseInt(startSequence);
			this.startSequence = startSequence;
			}
		catch (NumberFormatException | NullPointerException e)
			{
			this.startSequence = tmp;
			System.out.println("Can't set start sequence to " + startSequence + ". Change cancelled - start sequence is " + getStartSequence());
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
	
	// issue 233
	public String grabOutputFileName(String sampleLabel, WorklistItemSimple item)   
	{
	String repeatTag;
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
	if (item.getGroup().getParent().getSelectedPlatform().equals("agilent"))
		repeatTag = item.getRepresentsControl() && item.isSelected() ? "Unused_Inj" : "";
	else
		repeatTag = item.isSelected() ? "2" : "";
// Error in default date field
	if ("absciex".equalsIgnoreCase(this.getSelectedPlatform()))
		return grabAbsciexOutputName(sampleLabel, tag, getDefaultExperimentId(), getDefaultAssayId(), yearStr, monthAsStr,
				dts, instrument, getSelectedMode().equals("Positive") ? "Pos" : "Neg", repeatTag);		
	if (instrument.equals("Unknown") || instrument.equalsIgnoreCase("IN0024") || instrument.equals(""))
		return instrumentErrMsg;	
	if (getUseGCOptions())
		//issue 414
		return grabNameWithoutPath(sampleLabel, "", getDefaultExperimentId(), this.getDefaultAssayId(), yearStr, monthAsStr, 
	        dts, instrument,  repeatTag); // issue 450		
	return grabAgilentOutputName(sampleLabel, tag, getDefaultExperimentId(), this.getDefaultAssayId(), yearStr, monthAsStr, 
	    dts, instrument,  repeatTag); // issue 450
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
	
	// issue 233
	public String grabNameWithoutPath(String sampleLabel, String tag, String expId, String aid, String yearStr, String monthAsStr, 
			String dts,  String instrument, String mode)
		{
		String tagLabel = (tag != null && tag.length() > 0) ? tag + "-" : "";
		return (StringUtils.isEmptyOrNull(mode) ? "" : "\\" + mode + "\\") + dts + "-" + expId + "-" + aid + "-" + instrument + "-" + tagLabel + sampleLabel + "-" + (getSelectedMode().contains("Positive") ? "P" : "N");
		}

	// issue 368 and 394 
	public String grabAgilentOutputName(String sampleLabel, String tag, String expId, String aid, String yearStr, String monthAsStr, 
			String dts,  String instrument, String mode)
		//issue 233
	    {
		String tagLabel = ""; //(tag != null && tag.length() > 0) ? tag + "-" : "";
		return "D:\\MassHunter\\Data\\"   +  yearStr + "\\" +  monthAsStr + "\\" + expId +  "\\" +  mode   +  (StringUtils.isNullOrEmpty(mode) ? "" : "\\")  +   dts + "-" + expId + "-" + aid
 					+ "-" + instrument + "-" + tagLabel + sampleLabel + "-"  + (getSelectedMode().contains("Positive") ? "P" : "N");
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
	
	// issue 166
	// issue 179
	private List<String> grabAgilentColTitles()
		{
		// issue 209
		return Arrays.asList(new String [] { "Sample Name", "Sample Position", "Method", "Data File", "Sample Type", "Level Name",  "Injection Vol",
					"Comment"});	
	//	return Arrays.asList(new String [] { "Sample Name", "Sample Position", "Method", "Data File", "Sample Type", "Level Name",  "Inj Vol (" + "\u00B5"  + "l)",
	//	"Comment"});
	//	return Arrays.asList(new String [] { "Sample Name", "Sample Position", "Method", "Data File", "Sample Type", "Level Name",  "Injection Volume",
	//	"Comment" , "Barcode", "Sample Group", "Info."});
		}

	//issue 126
	public Boolean getUseGCOptions()
		{
		if (getSelectedInstrument() == null)
				return false;		
		String si = getSelectedInstrument().trim();
		// TO DO : Replace this with db lookup...		
		return si.startsWith("IN0025") || si.startsWith("IN0002") || si.startsWith("IN0027") ;  // issue 128
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

 	
	// issue 212
	public Boolean getIs96Well() // issue 416
		{
		return is96Well;
		}
    	
	// issue 212
	public void setIs96Well(Boolean is96Well) // issue 416
		{
		this.is96Well= is96Well;		
		}
	
	public Boolean getRandomizeByPlate() // issue 416
		{
		return randomizeByPlate;
		}
	
	// issue 179
	public void setChangeDefaultInjVolume(Boolean changeDefaultInjVolume) // issue 416
		{
		this.changeDefaultInjVolume= changeDefaultInjVolume;		
		}

	public Boolean getChangeDefaultInjVolume() // issue 416
		{
		return changeDefaultInjVolume;
		}
	
	// issue 169
	public void setDefaultPool(Boolean defaultPool) // issue 416
		{
		this.defaultPool =  defaultPool;		
		}

	// issue 169
	public Boolean getDefaultPool() // issue 416
		{
		return defaultPool;
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
	
	// issue 146
	public int getStartPlateControls()
		{
		return startPlateControls;
		}
	
	public void setStartPlateControls(int startPlateControls)
		{
		this.startPlateControls = startPlateControls;
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
	
	public void updatePoolReplicates(int nMasterBefore, int nMasterAfter, int nBatchBefore, int nBatchAfter, int ne10, int ne20, int ne40) 
		{
		this.masterPoolsBefore = nMasterBefore;
		this.masterPoolsAfter = nMasterAfter;
		this.batchPoolsBefore = nBatchBefore;
		this.batchPoolsAfter = nBatchAfter;
		this.nCE10Reps = ne10;
		this.nCE20Reps = ne20;
		this.nCE40Reps = ne40;
		}
	
	public void clearOutMotorPacControls ()
		{
		setNGastroExercise(0);	
		setNGastroSedentary(0);	
		setNLiverExercise(0);	
		setNLiverSedentary(0);	
		setNAdiposeExercise(0);	
		setNAdiposeSedentary(0);	
		setNPlasmaExercise(0);	
		setNPlasmaSedentary(0);
		setNRatPlasma(0);
		setNRatA(0);
		setNRatG(0);
		setNRatL(0);		
		// issue 22
		setNLungExercise(0);
		setNLungSedentary(0);
		setNHeartExercise(0);
		setNHeartSedentary(0);
		setNKidneyExercise(0);
		setNKidneySedentary(0);
		setNBrownAdiposeExercise(0);
		setNBrownAdiposeSedentary(0);
		setNHippoCampusExercise(0);
		setNHippoCampusSedentary(0);
		setNMuscleHumanFemale(0);
		setNMuscleHumanMale(0);
		setNPlasmaHumanFemale(0);
		setNPlasmaHumanMale(0);
		setNHumanMuscleCntrl(0);
		}	
	
	// issue 128
	private Map <String, String> buildCommentMap (List <WorklistItemSimple> workListItems)
		{
		Map <String, String> itemCommentMap = new HashMap <String, String> ();
		for (WorklistItemSimple item : workListItems)	
			itemCommentMap.put(item.getSampleName(), item.getComments());
		return itemCommentMap;
		}

	// issue 128
	private List <WorklistItemSimple> populateComments (List <WorklistItemSimple> workListItems, Map <String, String> commentMap)
		{
		for (WorklistItemSimple item : workListItems)
		    {
			if (commentMap.containsKey(item.getSampleName()))
				item.setComments(commentMap.get(item.getSampleName()));
		    }
		return workListItems;
		}
	
	// issue 153
    public int countOfSamplesForItems (List <WorklistItemSimple> worklistItems)
	    {
	    int i = 0;
	    for (WorklistItemSimple witem : worklistItems)
	    	{
	    	if (witem.getRepresentsControl())
	    		continue;
	    	i++;
	    	}
	    return i;	
	    }
    
    // issue 166
	public void populateSampleName (WorklistSimple ws, Map<String, String> idsVsReasearcherNameMap)
		{
		int idx =Integer.parseInt(ws.getStartSequence());
		
		for (WorklistItemSimple item : ws.getItems())
			{
			if (item.getRepresentsControl())
				continue;
			item.setResearcherName(idsVsReasearcherNameMap.get(item.getSampleName()));
			item.setSampleIndex(String.valueOf(idx));
			idx++;
			}
		}
	}
	




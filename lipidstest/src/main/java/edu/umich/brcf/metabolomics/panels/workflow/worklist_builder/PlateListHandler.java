////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PlateListHandler.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.comparator.WorklistItemBySampleIdComparator;
import edu.umich.brcf.shared.util.comparator.WorklistItemSimpleByPlatePosComparator;

public class PlateListHandler implements Serializable
	{
	int thePlateIdx = 0;
	int nRows, nCols, nPositions;
	String startPos = "A1", endPos = "A1";
	Integer startIdx = 0, endIdx = 53;
	Boolean useCarousel = false;
	int startOfStandards = 45;
	int startOfOtherControls = 36;
	private int nPlates = 1;
	String masterPoolMP = "Master Pool   (CS00000MP)"; // issue 146
    String masterPoolQCMP = "Master Pool.QCMP (CS000QCMP)"; // issue 146
	List<String> possiblePlatePositions = new ArrayList<String>();
	// cycle plate
	//issue 153
	List<String> thePlateList = new ArrayList <String> ();
	//List<String> thePlateList = Arrays.asList(new String[] {"4", "1", "2", "3"});
	 
	// issue 391 issue 403 issue 418
	// issue 422
	// issue 146
	public static List <String> STANDARD_CONTROL_TYPES = Arrays.asList(new String []{ "CS000STD0",   "CS000STD1", "CS000STD2", "CS000STD3",   "CS000STD4",
			"CS000STD5",   "CS000STD6", "CS000STD7", "CS000STD8",   "CS000STD9",
			"CS00STD10",   "CS00STD11", "CS00STD12",});
	public static List <String> POOL_CHEAR_CONTROL_TYPES = Arrays.asList(new String [] {
			"CS00000MP", 
			"CS000QCMP", 
			"CS000BPM1", "CS000BPM2", // issue 17
			"CS000BPM3",   "CS000BPM4", "CS000BPM5", "CS0000OP0",
			"CS0000OP1",   "CS0000OP2", "CS0000OP3", "CS0000OP4",   "CS0000OP5", "CS0000OP6","R00CHRUR1",   "R00CHRUR2", "R00CHRPL1", "R00CHRPL2",
			"CS0UMRG01", "CS0UMRL01", "CS0UMRA01","CS0UMRP01", "CSOUMHM03","CSMR80008","CSMR80009", "CSMR80010","CSMR80011", "CSMR80012","CSMR80013", 
			"CSMR80014", "CSMR80015","CSMR80016","CSMR80017","CSMR80018","CSMR80019","CSMR80020","CSMR80021","CSMR80022", "CSMR80023","CSMR80024",
			 "CSMR80025","CSMR81010", "CSMR81020", 
			"CS00000RC","CS00000SB","CS00000PB", "CS00000NB", "CS00000QC" // issue 151
	});
	
	public static List <String> CONTROL_TYPES_IN_END_TO_START_ORDER = Arrays.asList(new String []
		     { "R00CHRUR1",   "R00CHRUR2", "R00CHRPL1", "R00CHRPL2",
		    // issue 126
		    "CSMR81020", "CSMR81010", 
		    // issue 22
		    "CSMR80025","CSMR80024","CSMR80023","CSMR80022", "CSMR80021",
		    "CSMR80020","CSMR80019","CSMR80018","CSMR80017", "CSMR80016",
			"CSMR80015",   "CSMR80014", "CSMR80013", "CSMR80012",
			// issue 126
			"CSMR80011",   "CSMR80010", "CSMR80009", "CSMR80008","CSOUMHM03", "CS0UMRP01", "CS0UMRA01","CS0UMRL01", "CS0UMRG01", 
			"CS00000RC",   "CS00000MP", 
			"CS000QCMP", 
			"CS000BPM1", "CS000BPM2", // issue 17
			"CS000BPM3",   "CS000BPM4", "CS000BPM5", "CS0000OP0",
			"CS0000OP1",   "CS0000OP2", "CS0000OP3", "CS0000OP4",   "CS0000OP5", "CS0000OP6",
		/*	"CS000STD0",   "CS000STD1", "CS000STD2", "CS000STD3",   "CS000STD4",
			"CS000STD5",   "CS000STD6", "CS000STD7", "CS000STD8",   "CS000STD9",
			"CS00STD10",   "CS00STD11", "CS00STD12", */
			"CS00STD12",   "CS00STD11", "CS00STD10", "CS000STD9",   "CS000STD8",
			"CS000STD7",   "CS000STD6", "CS000STD5", "CS000STD4",   "CS000STD3",
			"CS000STD2",   "CS000STD1", "CS000STD0",
			// issue 146
			"CS00000PB",   "CS00000SB", "CS00000NB", "CS00000QC",		     
		     } // issue 450  
				);
	
	PlateListHandler(int nRows, int nCols, boolean useCarousel)
		{
		this.useCarousel = useCarousel;		
		this.nRows = useCarousel ? 10 : nRows;
		this.nCols = useCarousel ? 10 : nCols;		
		this.nPositions = useCarousel ? 100 : nRows * nCols;		
		this.possiblePlatePositions = this.getPossiblePlatePositions();
		setStartIdx(0);
		setEndIdx(nPositions - 1);
		}
	
	public List<WorklistItemSimple> condenseSortAndSpace(List<WorklistItemSimple> items)
		{
		// Group list there's only one control item per unique type assumes items already labeled by plate up front..
	   
		List <WorklistItemSimple> uniqueItems = grabUniqueItems(items); 
	    List <WorklistItemSimple> spacedItems = new ArrayList <WorklistItemSimple> ();
		spacedItems = buildSpacedSortedList(uniqueItems);
	    return spacedItems;
		}
		
	List<WorklistItemSimple> grabUniqueItems(List <WorklistItemSimple> items)
		{
		List <WorklistItemSimple> uniqueItems = new ArrayList<WorklistItemSimple>();		
		Map<String, WorklistItemSimple> plateMap = new HashMap<String, WorklistItemSimple>();
		for (int i = 0; i < items.size(); i++)
			plateMap.put(items.get(i).getSamplePosition(), items.get(i));
		Collection <WorklistItemSimple> col =  plateMap.values();
		for (WorklistItemSimple item  : col)
			{
			if (item.getRepresentsControl())
				{
				String [] tokens= item.getSampleName().split("\\-");
				if (tokens.length > 0)
					item.setSampleName(tokens[0]);
				}
			uniqueItems.add(item);		
			}
		return uniqueItems;
		}
	
	private List<WorklistItemSimple> buildSpacedSortedList (List <WorklistItemSimple> uniqueItems)
		{
		int itemsAdded = 0, j = 0, whatPlate = 1;
		String lastPlate = null, currPlate = null;
		 
		Collections.sort(uniqueItems, new WorklistItemSimpleByPlatePosComparator());
		List<WorklistItemSimple> spacedList = new ArrayList<WorklistItemSimple>();
		while (itemsAdded < uniqueItems.size())
			{
			WorklistItemSimple item = uniqueItems.get(itemsAdded);
			
			String samplePos = item.getSamplePosition();
			if (!(samplePos == null || "".equals(samplePos)))
				{
				String [] tokens = samplePos.split("-");
				if (tokens.length > 1) 
					{
					samplePos = tokens[1];
			        currPlate = tokens[0];
					}
				}
			//// issue 153 add fillers where ever they need to be:
			
			int calcit = calcNumericalPosition(samplePos, currPlate);
			if (calcit == j)
				{
				spacedList.add(uniqueItems.get(itemsAdded++));
				}
			else
				{
				while (j < calcit)
					{
					spacedList.add(new WorklistItemSimple());
					j++;
					}
				continue;
				}
			j++;
			}
		return spacedList;
		}
	
	// issue 391
	//issue 153
   
	

	///////////////
	
	int calcNumericalPosition(String pos, String plate)
		{
		String row;
		row = pos.substring(0,1);
		int rowInt = 0;
		int posInt = 0;
		int platePosInt = 0;
		int calculatedNumericalPosition;		
		switch (row)
			{
			case "A" : rowInt = 0; break;
			case "B" : rowInt = 9; break;
			case "C" : rowInt = 18; break;
			case "D" : rowInt = 27; break;
			case "E" : rowInt = 36; break;
			case "F" : rowInt = 45; break;
			}
		platePosInt = (Integer.parseInt(plate.substring(1,2))) - 1;
		platePosInt = platePosInt*54;
		posInt = Integer.parseInt(pos.substring(1,2));
		calculatedNumericalPosition = ((posInt + rowInt) - 1) + platePosInt;
		return calculatedNumericalPosition;
		}	

	String getExpectedPosition(int i)
		{
		if (useCarousel)
			return getCarouselExpectedPosition(i);
		i %= nPositions; 
		int col = i % nCols;
	    int row = (int) (Math.floor(i/nCols));
	    char rowLabel =  (char) ('A' + row);
	    return ("" + rowLabel + (col + 1));
		}

	String getCarouselExpectedPosition(int pos)
		{
		Integer I = pos % nPositions;
		return I.toString();
		}
	
	Integer getExpectedIdx(String pos)
		{
		return getPossiblePlatePositions().indexOf(pos);
		}
		
	List<String> getPossiblePlatePositions()
		{
		if (possiblePlatePositions != null && possiblePlatePositions.size() > 0)
			return possiblePlatePositions;
		
		List <String> availablePlatePositions = new ArrayList<String>();
		for (int i = startIdx; i <= endIdx; i++)
			availablePlatePositions.add(getExpectedPosition(i));
				
		return availablePlatePositions;
		}

	// issue 350
	// issue 325
	
	// issue 391 327
	 Map<String, Integer> buildControlPositionIdxByTypeMap(List<WorklistItemSimple> items, WorklistSimple worklist) 
		{
		int standardsIndex = 0;
	    int prevIdx;
	    int nextIdx;
	    boolean standardsExist = false;
	    boolean startedAfterStandards = false;
		Map<String, Integer> controlTypeCountsMap = new HashMap<String, Integer>();
		Map<String, Integer> controlTypeToPositionIndexMap = new HashMap<String, Integer>();
		for (int i = 0; i < items.size(); i++)
			{
			WorklistItemSimple item = items.get(i);			
			if (!item.getRepresentsControl()) continue;			
			String controlType = (item.getRepresentsUserDefinedControl() ? item.getNameForUserControlGroup() : ((WorklistControlGroup) item.getGroup()).getControlType());
			controlType = StringParser.parseId(controlType);
			// issue 17
			if (controlType.indexOf("CS000QCMP") > -1  && worklist.getBothQCMPandMP())
				continue;			
			if (!controlTypeCountsMap.containsKey(controlType))
				controlTypeCountsMap.put(controlType, 0);
			int nOfType = controlTypeCountsMap.get(controlType);
			controlTypeCountsMap.put(controlType, ++nOfType);
			}		
		prevIdx = nPositions - 1;
		// issue 146
		nextIdx = startOfStandards;
		for (int i = 0; i < STANDARD_CONTROL_TYPES.size(); i++)
			{			
			String controlType = PlateListHandler.STANDARD_CONTROL_TYPES.get(i);
			if (nextIdx >= worklist.getMaxItemsAsInt())
				nextIdx = startOfOtherControls;
			if (controlTypeCountsMap.get(controlType) != null)
			    {
				standardsExist = true;
				standardsIndex++;
				controlTypeToPositionIndexMap.put(controlType,  nextIdx++); 
			    }
			}
		// issue 146 take care of case where QCMP and MP need to be counted as 1 control to avoid skipping a row
		int countControls = worklist.buildControlTypeMap().size();
		if (worklist.buildControlTypeMap().get(masterPoolMP) != null && worklist.buildControlTypeMap().get(masterPoolQCMP) != null)
			countControls--;
		// issue 151
		if (worklist.buildControlTypeMap().get(null) != null)
			countControls--;
		int indexForOtherControls = calculateOtherControlsStartIndex(countControls,standardsIndex);
		nextIdx = indexForOtherControls;
		boolean movedPassStandards = false;
		// issue 146 don't override standards slot if standards > 9
		for (int i = 0; i < POOL_CHEAR_CONTROL_TYPES.size(); i++)
			{
			if (standardsIndex > 9 && !startedAfterStandards && nextIdx == 36)
				{
				nextIdx = nextIdx + (standardsIndex - 9); 
				startedAfterStandards = true;
				}
			// issue 146 take care of case where you have to use standards slot last row
			if (nextIdx >= 45 && standardsIndex > 0 && !movedPassStandards)
				{
				nextIdx = nextIdx + standardsIndex;
				movedPassStandards= true;
				}
			String controlType = PlateListHandler.POOL_CHEAR_CONTROL_TYPES.get(i);	
			if (controlTypeCountsMap.get(controlType) != null) 
				controlTypeToPositionIndexMap.put(controlType,  nextIdx++); 
			}	
		return controlTypeToPositionIndexMap;
		}
	
	
	 // issue 391 327 
	 // issue 409
	 private int placeControlsByTypeOnPlate(WorklistSimple worklist,  Map<Integer, String> map, int plate) throws METWorksException 
		{
		List<WorklistItemSimple> items = worklist.getItems();
		Map<String, Integer> controlPositionIdxByTypeMap =  buildControlPositionIdxByTypeMap(items, worklist);
		Map<String, String> foundControlTypesMap = new HashMap<String, String>();		
		int  idx = 0, targetIdx = 0, spotsLeft  = nPositions;
		String plateStr = "P" + plate; 	
		for (int i = 0; i < items.size(); i++)
			{		
			WorklistItemSimple item = items.get(i);			
			if (!item.getRepresentsControl()) continue;			
			String controlType = (item.getRepresentsUserDefinedControl() ? item.getNameForUserControlGroup() : ((WorklistControlGroup) item.getGroup()).getControlType());			
			// issue 17
			if (controlType.indexOf("CS000QCMP") > -1 && worklist.getBothQCMPandMP())
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS00000MP");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
			if (spotsLeft < 0) 
				throw new METWorksException("Error while placing controls");			
			if (!foundControlTypesMap.containsKey(controlType))
				spotsLeft--;				
			foundControlTypesMap.put(controlType, null);				
			item.belongsToPlate = plate - 1; ///(plate - 1);
			item.setRandomIdx(idx++);
			if (item.getRepresentsUserDefinedControl())
				targetIdx = controlPositionIdxByTypeMap.get(controlType);
			else
			    targetIdx = controlPositionIdxByTypeMap.get(StringParser.parseId(controlType));
			item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
			item.setRackPosition(plateStr + "-" + map.get(targetIdx));
			}
	    return spotsLeft;
	    }
	 
	 // issue 391 327
	 // issue 409
	 public int updatePlatePositionsForAgilent(WorklistSimple worklist) throws METWorksException
		{
		
		List<WorklistItemSimple> pageItemsArray = new ArrayList<WorklistItemSimple>();
		List<WorklistItemSimple> items = worklist.getItems();
		// issue 153
		constructThePlateList(worklist);
		Boolean orderWasUploaded = worklist.wasCustomOrdered();		
		Map<Integer, String> map = buildPositionMap();	
		int pIdx = 0;
		//issue 153
		int plate = 1;
		int idxForPage = 0, spotsLeft =  nRows * nCols;
		Integer targetIdx = 0; 		
		String plateStr = "";
		int idx = 1;
		int nSpotsLeft;		
		// issue 146
		// issue 151
		// cycle plate 
		//issue 153
		
		// issue 153 do not do plate cycling
		if (worklist.countOfSamplesForItems(items) + (worklist.buildControlTypeMap().get(null) != null ? worklist.buildControlTypeMap().size()-1 : worklist.buildControlTypeMap().size()  )  > (worklist.getCyclePlateLimit() * worklist.getMaxItemsAsInt()))
			worklist.setStartPlateControls((int) calculatePlate  (worklist.countOfSamplesForItems(items), worklist.buildControlTypeMap() , worklist.getMaxItemsAsInt()   )); // issue 146
		else 
			{
			thePlateIdx = (int) calculatePlate  (worklist.countOfSamplesForItems(items), worklist.buildControlTypeMap() , worklist.getMaxItemsAsInt()   );
			worklist.setStartPlateControls(Integer.parseInt(thePlateList.get(thePlateIdx-1 < 0 ? 0 : thePlateIdx-1)));
			}
		plate = Integer.parseInt(thePlateList.get(0));
		nSpotsLeft = placeControlsByTypeOnPlate(worklist, map, worklist.getStartPlateControls());
		for (int i = 0; i < items.size(); i++)
			{
			WorklistItemSimple item = items.get(i);		
			if (item.getRepresentsControl())
		        continue;
			item.setRandomIdx(idx++);			
			if (spotsLeft == 0 )
				{
				updatePageItemPositions(pageItemsArray, map, plateStr, orderWasUploaded);
				pageItemsArray.clear();
				idxForPage = 0;
				// cycle plate
				//issue 153
			//	plate = plateCycling ? Integer.parseInt(thePlateList.get(++pIdx)) : plate++;;
				plate = Integer.parseInt(thePlateList.get(++pIdx));
				//plate = Integer.parseInt(thePlateList.get(++pIdx));
				// issue 146 put back
				spotsLeft = nPositions;
				idx = 1;
				}
			plateStr = "";
			if (!useCarousel)
				plateStr = ( "P" + plate + "-");
			targetIdx = idxForPage % nPositions;
			if (item.getSampleName().indexOf("CS000QCMP") > 0)
			    {
				idxForPage++;
			    continue;
			    }
			item.setSamplePosition(plateStr + map.get(targetIdx));
			item.belongsToPlate = (plate - 1);
			pageItemsArray.add(item);			
			idxForPage++;
			spotsLeft--;			
			}
		nPlates = plate;	
		int i = 0;		
		updatePageItemPositions(pageItemsArray, map, plateStr, orderWasUploaded);
		return nPlates + 1;
		}
	 
	private void ignorePagePositions(List <WorklistItemSimple> items)
		{
		for (int i = 0; i < items.size(); i++)
			items.get(i).setSamplePosition("");
		}

	private void updatePageItemPositions(List <WorklistItemSimple> pageItemsArray, Map<Integer, String> map, String plateStr, boolean orderWasUploaded)
		{
		//issue 350
		if (!orderWasUploaded)
	        Collections.sort(pageItemsArray, new WorklistItemBySampleIdComparator());	    
	    for (int i = 0; i < pageItemsArray.size(); i++)
	    	    pageItemsArray.get(i).setSamplePosition(plateStr + map.get(i));
		}

	private Map<Integer, String> buildPositionMap()
		{
		Map<Integer, String> map = new HashMap<Integer, String>();		
		String rowLabel = "", colLabel = "";		
		if (this.useCarousel)
			return buildCarouselPositionMap();		
		for (int row = 0; row < this.nRows; row++) 
			{
			for (int col = 0; col < nCols; col++)
				{
				switch (row)
					{
					case 0 :  rowLabel = "A"; break;
					case 1 :  rowLabel = "B"; break;
					case 2 :  rowLabel = "C"; break;
					case 3 :  rowLabel = "D"; break;
					case 4 :  rowLabel = "E"; break;
					case 5 :  rowLabel = "F"; break;
					}
				colLabel = "" + (col + 1);
				Integer idx = row * nCols + col;
				map.put(idx, rowLabel + colLabel);
				}
			}		
		return map;
		}

	private Map<Integer, String> buildCarouselPositionMap()
		{
		Map<Integer, String> map = new HashMap<Integer, String>();	
		for (int i = 0; i < 100; i ++)
			{
			Integer I = i + 1;
			map.put(i, I.toString());
			}		
		return map;
		}

	public Integer getStartIdx() 
		{
		return startIdx;
		}
	
	public void setStartIdx(Integer startIdx) 
		{
		this.startIdx = startIdx;
		this.startPos = this.getExpectedPosition(startIdx);
		}

	public Integer getEndIdx() 
		{
		return endIdx;
		}

	public void setEndIdx(Integer endIdx) 
		{
		this.endIdx = endIdx;
		this.endPos = this.getExpectedPosition(endIdx);
		}

	public String getStartPos() 
		{
		return startPos;
		}

	public void setStartPos(String startPos) 
		{
		this.startPos = startPos;
		this.startIdx = this.getExpectedIdx(startPos);
		}

	public String getEndPos() 
		{
		return endPos;
		}

	public void setEndPos(String endPos) 
		{
		this.endPos = endPos;
		this.endIdx = this.getExpectedIdx(endPos);
		}
	
	public int getNPlates()
		{
		return nPlates;
		}
	
	// issue 146
	public double calculatePlate  (int countSamples, Map<String, Integer> controlTypeMap, int maxItems)
		{
		int countControls = controlTypeMap.size();
		// issue 146 take care of case where QCMP and MP need to be counted as 1 control to avoid skipping a row
		if (controlTypeMap.get(masterPoolMP) != null && controlTypeMap.get(masterPoolQCMP) != null)
			countControls--;
		// issue 151
		if (controlTypeMap.get(null) != null)
			countControls--;
		int countStandards = grabNumberStandards(controlTypeMap);
		double nPlates = Math.floor(countSamples/maxItems) ;
		if (countSamples%maxItems  > 0)
			nPlates ++;
		double squaresLeft = (maxItems * nPlates) - countSamples;
		squaresLeft = squaresLeft - (squaresLeft%9);
		double rowsNeededControls = Math.floor((countControls-countStandards)/9) + ((countControls-countStandards)%9 > 0 ?1 : 0) + (countStandards > 0 ? 1 : 0) ;
		double rowsLeft = squaresLeft/9;
		if ((squaresLeft <  countControls) || (rowsLeft < rowsNeededControls))
			nPlates ++;
		return nPlates;		
		}
	
	public int grabNumberStandards (Map<String, Integer> controlTypeMap)
		{
		int numberStandards = 0;
		if ( controlTypeMap == null || controlTypeMap.size() == 0)
			return 0;				
		for(String key : controlTypeMap.keySet()) 
		    {
			 if (key == null || key.indexOf("STD") == -1) 
			       continue;
			 numberStandards ++;
		    }
		return numberStandards;
		}

	// issue 146
	public int calculateOtherControlsStartIndex (int countControls, int countStandards)
		{
		String doubleStr;
		double rowsAboveStandards = Math.floor((countControls - (countStandards >9 ? 9 : countStandards))/9);
		double remainder = countControls - (countStandards >9 ? 9 : countStandards)%9;
		double countCalc = (countControls - (countStandards >9 ? 9 : countStandards));
		remainder = countCalc%9;
		if (remainder > 0 )
			rowsAboveStandards ++;
		// issue 146 take care of case where you have < 54 controls but have to use bottom standards row
		if (rowsAboveStandards > 5 && countControls <= 54)
			rowsAboveStandards = 5;
		return  (int) (countStandards == 0 ? (45 - (9*(rowsAboveStandards -1))) :   (45-(9*rowsAboveStandards))   );
		}	
    
    // issue 153
    public void constructThePlateList (WorklistSimple worklist)
    	{
    	double numPlates = 0;
    	if (worklist.countOfSamplesForItems(worklist.getItems())+  (worklist.buildControlTypeMap().get(null) != null ? worklist.buildControlTypeMap().size()-1 : worklist.buildControlTypeMap().size()  ) <= (worklist.getCyclePlateLimit() * worklist.getMaxItemsAsInt()))
    		{
	    	if (worklist.getStartPlate().equals( "1" ))
				thePlateList = Arrays.asList(new String[] {"1", "2", "3", "4"});
			else if (worklist.getStartPlate().equals( "2" ))
				thePlateList = Arrays.asList(new String[] { "2", "3", "4", "1"});
			else if (worklist.getStartPlate().equals( "3" ))
				thePlateList = Arrays.asList(new String[] { "3", "4", "1", "2"});
			else 
				thePlateList = Arrays.asList(new String[] { "4", "1", "2", "3"});
    		}
    	else 
    		{
    		numPlates =  Math.floor( (worklist.countOfSamplesForItems(worklist.getItems()) + worklist.buildControlTypeMap().size())/54) ;
    		if  ( (worklist.countOfSamplesForItems(worklist.getItems()) + worklist.buildControlTypeMap().size())%54 > 0)
    			numPlates ++;
    		for (int i = 1;i<= numPlates; i++)
    			{
    			thePlateList.add(String.valueOf(i));
    			}
    		}
    	}
	}
		

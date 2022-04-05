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
import java.util.TreeMap;

import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.comparator.WorklistItemByCustomLoadOrderComparator;
import edu.umich.brcf.shared.util.comparator.WorklistItemBySampleIdComparator;
import edu.umich.brcf.shared.util.comparator.WorklistItemSimpleByPlatePosComparator;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;

public class PlateListHandler implements Serializable
	{
	public int maxStartPlate = 4;
	int thePlateIdx = 0;
	String plateStr = "P1";
	int plateOn = 1;
	boolean bothChearAndInjection = false;
	int vStandards, vNextIdx ;
	int nRows, nCols, nPositions;
	String startPos = "A1", endPos = "A1";
	Integer startIdx = 0, endIdx = 95;
	Boolean useCarousel = false;
	int startOfStandards = 85;
	int startOfOtherControls = 72;
	private int nPlates = 1;
	int idxForSamples = 0;
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
	Map<String, String> namePositionMap = new HashMap<String, String> ();
	
	
	public static List <String> STANDARD_CONTROL_TYPES = Arrays.asList(new String []{ "CS000STD0",   "CS000STD1", "CS000STD2", "CS000STD3",   "CS000STD4",
			"CS000STD5",   "CS000STD6", "CS000STD7", "CS000STD8",   "CS000STD9",
			"CS00STD10",   "CS00STD11", "CS00STD12",});
	/* public static List <String> POOL_CHEAR_CONTROL_TYPES = Arrays.asList(new String [] {
			// issue 201
			"CS00000MP-Pre",
			"CS000BPM1-Pre", 
			"CS000BPM2-Pre",
			"CS000BPM3-Pre",
			"CS000BPM4-Pre",
			"CS000BPM5-Pre",
			"R00CHRPL1-Pre",
			"R00CHRUR1-Pre",
			"CS00000SB-Pre",   // issue 207		
			"CS00000MP", 
			"CS000QCMP", 
			"CS000BPM1", "CS000BPM2", // issue 17
			"CS000BPM3",   "CS000BPM4", "CS000BPM5", "CS0000OP0",
			"CS0000OP1",   "CS0000OP2", "CS0000OP3", "CS0000OP4",   "CS0000OP5", "CS0000OP6","R00CHRUR1",   "R00CHRUR2", "R00CHRPL1", "R00CHRPL2",
			"CS0UMRG01", "CS0UMRL01", "CS0UMRA01","CS0UMRP01", "CSOUMHM03","CSMR80008","CSMR80009", "CSMR80010","CSMR80011", "CSMR80012","CSMR80013", 
			"CSMR80014", "CSMR80015","CSMR80016","CSMR80017","CSMR80018","CSMR80019","CSMR80020","CSMR80021","CSMR80022", "CSMR80023","CSMR80024",
			 "CSMR80025","CSMR81010", "CSMR81020","CSMR81040", "CSMR81030", // issue 193
			"CS00000RC","CS00000PB", "CS00000SB","CS00000NB", "CS00000QC",  // issue 151 issue 179
	});*/
	
	public static List <String> POOL_CHEAR_CONTROL_TYPES = Arrays.asList(new String [] {
			// issue 201
			// issue 207
			"CS00000MP-Pre",
			"CS000BPM1-Pre", 
			"CS000BPM2-Pre",
			"CS000BPM3-Pre",
			"CS000BPM4-Pre",
			"CS000BPM5-Pre",		
			"CS00000MP", 
			"CS000QCMP", 
			"CS000BPM1", "CS000BPM2", // issue 17
			"CS000BPM3",   "CS000BPM4", "CS000BPM5", "CS0000OP0",
			"CS0000OP1",   "CS0000OP2", "CS0000OP3", "CS0000OP4",   "CS0000OP5", "CS0000OP6","R00CHRPL1-Pre",
			"R00CHRUR1-Pre","R00CHRUR1",   "R00CHRUR2", "R00CHRPL1", "R00CHRPL2",
			"CS0UMRG01", "CS0UMRL01", "CS0UMRA01","CS0UMRP01", "CSOUMHM03","CSMR80008","CSMR80009", "CSMR80010","CSMR80011", "CSMR80012","CSMR80013", 
			"CSMR80014", "CSMR80015","CSMR80016","CSMR80017","CSMR80018","CSMR80019","CSMR80020","CSMR80021","CSMR80022", "CSMR80023","CSMR80024",
			 "CSMR80025","CSMR81010", "CSMR81020","CSMR81040", "CSMR81030", // issue 193
			"CS00000RC","CS00000PB", "CS00000SB-Pre", "CS00000SB","CS00000NB", "CS00000QC",  // issue 151 issue 179
	 });
	
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
		List <WorklistItemSimple> uniqueItems = grabUniqueItems(items); 
		
	    List <WorklistItemSimple> spacedItems = new ArrayList <WorklistItemSimple> ();
		if (items.get(0).getGroup().getParent().getIs96Well())
			spacedItems = buildSpacedSortedList(uniqueItems);
		else
			spacedItems = buildSpacedSortedListOriginal(uniqueItems);
	
	    return spacedItems;
		}
	
	// issue 212
	List<WorklistItemSimple> grabUniqueItems(List <WorklistItemSimple> items)
		{
		
		List <WorklistItemSimple> uniqueItems = new ArrayList<WorklistItemSimple>();		
		Map<String, WorklistItemSimple> plateMap = new HashMap<String, WorklistItemSimple>();
		for (WorklistItemSimple item : items)
			{
			if (item.getRepresentsControl())
				continue;
			item.setCustomLoadOrder(items.get(0).getGroup().getParent().getSampleNamesArray().indexOf(item.getSampleName()));
			uniqueItems.add(item);				
			}
		
		for (int i = 0; i < items.size(); i++)
			// issue 242
			plateMap.put(items.get(i).getSampleName().substring(0,9), items.get(i));
		
		Collection <WorklistItemSimple> col =  plateMap.values();
		for (WorklistItemSimple item  : col)
			{
			if (item.getRepresentsControl())
				{
				String [] tokens= item.getSampleName().split("\\-");
				if (item.getSampleName().contains("CS000QCMP") && items.get(0).getGroup().getParent().getIs96Well())
					continue;
				item.setCustomLoadOrder(items.get(0).getGroup().getParent().getSampleNamesArray().indexOf(item.getSampleName()));
				uniqueItems.add(item);	
				}				
			}
		
	/////	items.get(0).getGroup().getParent().rebuildEverything();
		
		return uniqueItems;
		}
	
	private String letterGivenNumeric (int numericIndex)
		{
		String rowLabel = "";
		switch (numericIndex)
			{
			case 0 :  rowLabel = "A"; break;
			case 1 :  rowLabel = "B"; break;
			case 2 :  rowLabel = "C"; break;
			case 3 :  rowLabel = "D"; break;
			case 4 :  rowLabel = "E"; break;
			case 5 :  rowLabel = "F"; break;
			case 6 :  rowLabel = "G"; break;
			case 7 :  rowLabel = "H"; break;
			}
		return rowLabel;
		}
	

		private List<WorklistItemSimple> buildSpacedSortedListOriginal (List <WorklistItemSimple> uniqueItems)
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
				int calcit = calcNumericalPositionOriginal(samplePos, currPlate);
				if (calcit == j)
					spacedList.add(uniqueItems.get(itemsAdded++));
				else
				    {
					while (j < calcit)					
						{
						spacedList.add(new WorklistItemSimple());
						j++;
						}
					spacedList.add(uniqueItems.get(itemsAdded++)); 
					} 
				j++;
				}
			return spacedList;
			}
		
	private List<WorklistItemSimple> buildSpacedSortedList (List <WorklistItemSimple> uniqueItems)
		{
		int i = 0;
		int idx = 0;
		int iStandards  = 0;
		int iSamples = 0;
		int lastNumPos = 2;
		int j=0;
		List<WorklistItemSimple> spacedList = new ArrayList<WorklistItemSimple>();
		int maxSamplesFor96well = uniqueItems.get(0).getGroup().getParent().getMaxSamples96Wells();

		while (i<= uniqueItems.size()-1)
			{
			if (uniqueItems.get(i).getSamplePosition().contains("10"))
				uniqueItems.get(i).setSamplePosition(uniqueItems.get(i).getSamplePosition().replace("10",  "910"));
			if (uniqueItems.get(i).getSamplePosition().contains("11"))
				uniqueItems.get(i).setSamplePosition(uniqueItems.get(i).getSamplePosition().replace("11",  "911"));
			if (uniqueItems.get(i).getSamplePosition().contains("12"))
				uniqueItems.get(i).setSamplePosition(uniqueItems.get(i).getSamplePosition().replace("12",  "912"));
			i++;
			}
		
		/////////////////////////////////////////////////////
		i=0;
		idxForSamples = 0;
		
         
		Collections.sort(uniqueItems, new WorklistItemByCustomLoadOrderComparator());
		while (i<= uniqueItems.size()-1)
			{	
			if (uniqueItems.get(i).getSampleName().startsWith("S000"))
				{
				Double plateOn = Math.floor(idxForSamples/maxSamplesFor96well) + 1 ;
				if (idxForSamples%maxSamplesFor96well == 0)
					{
					iSamples = 0;
					lastNumPos = 2;
					}

				uniqueItems.get(i).setSamplePosition("P" + plateOn.intValue() + "-"  + letterGivenNumeric(iSamples) + (lastNumPos > 9 ? "9" + lastNumPos : lastNumPos) );				
				namePositionMap.put(uniqueItems.get(i).getSampleName(), uniqueItems.get(i).getSamplePosition());
				iSamples ++;
				if (iSamples >= 8)
					{
					iSamples = 0;
					lastNumPos ++;
					}
				spacedList.add(uniqueItems.get(i)) ;
				idxForSamples++;
				}
			i++;
			
			}
		
	    i = 0;		
		Collections.sort(uniqueItems, new WorklistItemSimpleByPlatePosComparator());
		while (i<= uniqueItems.size()-1)
			{
			if (uniqueItems.get(i).getSampleName().contains("STD"))
				{
				 int  dashIndex = uniqueItems.get(i).getSampleName().lastIndexOf("-");
				 int stdIndex  =  uniqueItems.get(i).getSampleName().indexOf("STD") + 3;
				 String stdIntString = uniqueItems.get(i).getSampleName().substring(stdIndex, dashIndex);
				 
				 if (Integer.valueOf(stdIntString) > 5)
					 {
					 i++;
					 continue;
					 }
				 
			     uniqueItems.get(i).setSamplePosition("P1-" + letterGivenNumeric(iStandards) + "1" );
			     if (uniqueItems.get(i).getSampleName().indexOf("-") >= 0)
			    	 namePositionMap.put(uniqueItems.get(i).getSampleName().substring(0, uniqueItems.get(i).getSampleName().lastIndexOf("-")), uniqueItems.get(i).getSamplePosition());
			     else 
			    	 namePositionMap.put(uniqueItems.get(i).getSampleName(), uniqueItems.get(i).getSamplePosition());
			     spacedList.add(uniqueItems.get(i)) ;
			     iStandards ++;
				}
			i++;
			}
		i = 0;	
		// JAK new preview
		// Load chear
		boolean alreadyCHR = false;
		boolean alreadyPool = false;
				
		while (i<= uniqueItems.size()-1)
			{	
			//System.out.println(".....OKAY HERE we go here is pool type a and unique items i and alreadypool:" +  uniqueItems.get(0).getGroup().getParent().getPoolTypeA() + " " +  uniqueItems.get(i).getSampleName() + " " + alreadyPool);
			if (uniqueItems.get(i).getSampleName().contains("CHR") && !alreadyCHR)
				{
				uniqueItems.get(i).setSamplePosition("P1-G1");
				if (uniqueItems.get(i).getSampleName().indexOf("-") >= 0)
		    		namePositionMap.put(uniqueItems.get(i).getSampleName().substring(0, uniqueItems.get(i).getSampleName().lastIndexOf("-")), uniqueItems.get(i).getSamplePosition());
		        else 
		    	    namePositionMap.put(uniqueItems.get(i).getSampleName(), uniqueItems.get(i).getSamplePosition());
				spacedList.add(uniqueItems.get(i)) ;
				alreadyCHR = true;
				}
			else if (uniqueItems.get(i).getSampleName().contains("SB"))
				{
				uniqueItems.get(i).setSamplePosition("Vial 1");
				if (uniqueItems.get(i).getSampleName().indexOf("-") >= 0)
		    		namePositionMap.put(uniqueItems.get(i).getSampleName().substring(0, uniqueItems.get(i).getSampleName().lastIndexOf("-")), uniqueItems.get(i).getSamplePosition());
		        else 
		    	    namePositionMap.put(uniqueItems.get(i).getSampleName(), uniqueItems.get(i).getSamplePosition());
				spacedList.add(uniqueItems.get(i)) ;
				}
			else if (uniqueItems.get(0).getGroup().getParent().getPoolTypeA() != null && !alreadyPool && (uniqueItems.get(i).getSampleName().contains(uniqueItems.get(0).getGroup().getParent().getPoolTypeA())) && (uniqueItems.get(i).getSampleName().contains("MP") || uniqueItems.get(i).getSampleName().contains("BPM")))
				{
				uniqueItems.get(i).setSamplePosition("P1-H1");
				if (uniqueItems.get(i).getSampleName().indexOf("-") >= 0)
		    		namePositionMap.put(uniqueItems.get(i).getSampleName().substring(0, uniqueItems.get(i).getSampleName().lastIndexOf("-")), uniqueItems.get(i).getSamplePosition());
		        else 
		    	    namePositionMap.put(uniqueItems.get(i).getSampleName(), uniqueItems.get(i).getSamplePosition());
				spacedList.add(uniqueItems.get(i)) ;
				alreadyPool = true;
				}	
			i++;
			}
	
	   i = 0;
				
		Collections.sort(spacedList, new WorklistItemSimpleByPlatePosComparator());
		i = 0;
		int sizeSpaced =  spacedList.size();
		List <WorklistItemSimple> tspacedList = new ArrayList <WorklistItemSimple> ();
		
		while (i <= sizeSpaced -1)
			{
			tspacedList.add(spacedList.get(i));
			i++;
			}
		i = 0;
        while (i <= sizeSpaced -1 )
       		{
        	String samplePos = spacedList.get(i).getSamplePosition().replace("910", "10").replace("911",  "11").replace("912", "12");
        	String currPlate = "";
        	if (!(samplePos == null || "".equals(samplePos)))
				{
				String [] tokens = samplePos.split("-");
				if (tokens.length > 1) 
					{
					samplePos = tokens[1];
			        currPlate = tokens[0];
					}
				}
        	int calcit = calcNumericalPosition(samplePos, currPlate);
        	if (j == 0)
        		j=i;
        	
        			
        	while (j < calcit )
				{
        		tspacedList.add(j, new WorklistItemSimple());
        		j++;
				}   	
    	    i++;
    	    j++;
       		}
        spacedList.clear();
        spacedList.addAll(tspacedList);
        namePositionMap.put("R00CHRPL1-Pre", "P1-G1");
        namePositionMap.put("R00CHRUR1-Pre", "P1-G1");
        namePositionMap.put("CS00000MP-Pre", "P1-H1");
        namePositionMap.put("CS000BPM1-Pre", "P1-H1");
        namePositionMap.put("CS000BPM2-Pre", "P1-H1");
        namePositionMap.put("CS000BPM3-Pre", "P1-H1");
        namePositionMap.put("CS000BPM4-Pre", "P1-H1");
        namePositionMap.put("CS000BPM5-Pre", "P1-H1");
        namePositionMap.put("CS00000SB-Pre", "Vial 1"); // issue 215
    	return spacedList;
		}
	
	// issue 391
	//issue 153
   
	int calcNumericalPosition(String pos, String plate)
		{
		String row;
		row = pos.substring(0,1);
		int rowInt = 0;
		int posInt = 0;
		int platePosInt = 0;
		int calculatedNumericalPosition;	
		if (pos.equals("Vial 1"))
			return -1;
		switch (row)
			{
		// JAK new preview
			case "A" : rowInt = 0; break;
			case "B" : rowInt = 12; break;
			case "C" : rowInt = 24; break;
			case "D" : rowInt = 36; break;
			case "E" : rowInt = 48; break;
			case "F" : rowInt = 60; break;
			case "G" : rowInt = 72; break;
			case "H" : rowInt = 84; break;
			}
		platePosInt = (Integer.parseInt(plate.substring(1,2))) - 1;		
	// issue 212
		platePosInt = platePosInt*96;
		
		posInt = Integer.parseInt(pos.substring(1,pos.length()));
		calculatedNumericalPosition = ((posInt + rowInt) - 1) + platePosInt;
		return calculatedNumericalPosition;
		}	
	// issue 212

	int calcNumericalPositionOriginal(String pos, String plate)
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
			default : rowInt = 45; break;
			}
		
		platePosInt = (Integer.parseInt(plate.substring(1,2))) - 1;
		platePosInt = platePosInt*54;
		if (StringUtils.isEmptyOrNull(pos))
			return 0;
		if (pos.equals("null"))
			return 0;
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
	
	boolean bothPreinjectionsAndControl ( List<WorklistItemSimple> items, String preInjection, String control )
		{
		int totalControlandPre = 0;
	
		for (WorklistItemSimple item : items)
			{
			if (item.getSampleName().equals(preInjection) )
				{
				totalControlandPre++;
				break;
				}
			}
		
		for (WorklistItemSimple item : items)
			{
			if (item.getSampleName().equals(control))
				{
				totalControlandPre++;
				break;
				}
			}
		if (totalControlandPre >= 2)
			return true;
			
        return false;
		}
	

	// issue 391 327
	 Map<String, Integer> buildControlPositionIdxByTypeMap(List<WorklistItemSimple> items, WorklistSimple worklist) 
		{
		int standardsIndex = 0;
	    int prevIdx;
	    int nextIdx;
	    boolean standardsExist = false;
	    boolean startedAfterStandards = false;
	    // issue 212
	    if (worklist.getIs96Well())
	    	{
	    	vStandards = 12;
	    	vNextIdx = 72;
	    	}
	    else 
	    	{
	    	vStandards = 9;
	    	vNextIdx = 36;
	    	}
	    
		Map<String, Integer> controlTypeCountsMap = new HashMap<String, Integer>();
		Map<String, Integer> controlTypeToPositionIndexMap = new HashMap<String, Integer>();
		Map<String, Integer> subtractInjectionCountMap = new HashMap<String, Integer>();
		for (int i = 0; i < items.size(); i++)
			{	
			WorklistItemSimple item = items.get(i);				
			if (!item.getRepresentsControl()) continue;	
			String controlType = (item.getRepresentsUserDefinedControl() ? item.getNameForUserControlGroup() : ((WorklistControlGroup) item.getGroup()).getControlType());
			controlType = StringParser.parseId(controlType);
			// issue 201
			// issue 17
			if (controlType.indexOf("CS000QCMP") > -1  && worklist.getBothQCMPandMP())
				continue;
			
			// issue 201
			if (controlType.indexOf("R00CHRPL1-Pre") > -1 &&  bothPreinjectionsAndControl(items,"R00CHRPL1-Pre-01", "R00CHRPL1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("R00CHRUR1-Pre") > -1 && bothPreinjectionsAndControl(items,"R00CHRUR1-Pre-01", "R00CHRUR1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
					
			// issue 201
			if (controlType.indexOf("CS00000MP-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000MP-Pre-01", "CS00000MP-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM1-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM1-Pre-01", "CS000BPM1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM2-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM2-Pre-01", "CS000BPM2-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM3-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM3-Pre-01", "CS000BPM3-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM4-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM4-Pre-01", "CS000BPM4-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM5-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM5-Pre-01", "CS000BPM5-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 207
			if (controlType.indexOf("CS00000SB-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000SB-Pre-01", "CS00000SB-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
		
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
		if (subtractInjectionCountMap.size() > 0)
			countControls= countControls - subtractInjectionCountMap.size();
		int indexForOtherControls = calculateOtherControlsStartIndex(countControls,standardsIndex, worklist);
		nextIdx = indexForOtherControls;
		boolean movedPassStandards = false;
		// issue 146 don't override standards slot if standards > 9
		
		// JAK 212 check 96well
		for (int i = 0; i < POOL_CHEAR_CONTROL_TYPES.size(); i++)
			{
			if (standardsIndex > vStandards && !startedAfterStandards && nextIdx == vNextIdx)
				{
				nextIdx = nextIdx + (standardsIndex - vStandards); 
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
		Map<String, Integer> controlPositionIdxByTypeMap = (worklist.getIs96Well() ? buildControlPositionIdxByTypeMap(items, worklist) : buildControlPositionIdxByTypeMapOriginal(items, worklist));
		Map<String, String> foundControlTypesMap = new HashMap<String, String>();		
		int  idx = 0, targetIdx = 0, spotsLeft  = nPositions;
		String plateStr = "P" + plate; 	
		// issue 199
		for (int i = 0; i < items.size(); i++)
			{		
			
			WorklistItemSimple item = items.get(i);			
			if (!item.getRepresentsControl()) continue;			
			String controlType = (item.getRepresentsUserDefinedControl() ? item.getNameForUserControlGroup() : ((WorklistControlGroup) item.getGroup()).getControlType());			
			
			// issue 17
			if (controlType.indexOf("CS000QCMP") > -1 && worklist.getBothQCMPandMP())
				{
				if (controlPositionIdxByTypeMap.containsKey("CS00000MP"))
					targetIdx = controlPositionIdxByTypeMap.get("CS00000MP");
				else if (controlPositionIdxByTypeMap.containsKey("CS00000MP-Pre"))
					targetIdx = controlPositionIdxByTypeMap.get("CS00000MP-Pre");
				else 
					targetIdx = controlPositionIdxByTypeMap.get("CS00000QCMP");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				
				continue;
				}	
			// issue 201
			if (controlType.indexOf("R00CHRPL1-Pre") > -1 && bothPreinjectionsAndControl(items,"R00CHRPL1-Pre-01", "R00CHRPL1-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("R00CHRPL1");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
			if (controlType.indexOf("R00CHRUR1-Pre") > -1 && bothPreinjectionsAndControl(items,"R00CHRUR1-Pre-01", "R00CHRUR1-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("R00CHRUR1");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
			// issue 201
			if (controlType.indexOf("CS00000MP-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000MP-Pre-01", "CS00000MP-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS00000MP");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM1-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM1-Pre-01", "CS000BPM1-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS000BPM1");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
						
			// issue 201
			if (controlType.indexOf("CS000BPM2-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM2-Pre-01", "CS000BPM2-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS000BPM2");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
						
			// issue 201
			if (controlType.indexOf("CS000BPM3-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM3-Pre-01", "CS000BPM3-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS000BPM3");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
						
			// issue 201
			if (controlType.indexOf("CS000BPM4-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM4-Pre-01", "CS000BPM4-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS000BPM4");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
						
			// issue 201
			if (controlType.indexOf("CS000BPM5-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM5-Pre-01", "CS000BPM5-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS000BPM5");
				item.setSamplePosition(plateStr + "-" + map.get(targetIdx));
				item.setRackPosition(plateStr + "-" + map.get(targetIdx));
				continue;
				}
			
			// issue 207
			if (controlType.indexOf("CS00000SB-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000SB-Pre-01", "CS00000SB-01") )
				{
				targetIdx = controlPositionIdxByTypeMap.get("CS00000SB");
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
		constructThePlateList(worklist, maxStartPlate);
		Boolean orderWasUploaded = worklist.wasCustomOrdered();		
		Map<Integer, String> map = buildPositionMap(worklist.getIs96Well());	
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

	private Map<Integer, String> buildPositionMap(boolean v96Well)
		{
		Map<Integer, String> map = new HashMap<Integer, String>();		
		String rowLabel = "", colLabel = "";		
		if (this.useCarousel)
			return buildCarouselPositionMap();	
		if (v96Well)
			{
			this.nRows = 8;
		    this.nCols = 12;
			}
		else
			{
			this.nRows = 6;
		    this.nCols = 9;
			}
			
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
					case 6 :  rowLabel = "G"; break;
					case 7 :  rowLabel = "H"; break;
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
		// JAK new preview
		//squaresLeft = squaresLeft - (squaresLeft%9);
		squaresLeft = squaresLeft - (squaresLeft%12);
		// JAK new preview
		double rowsNeededControls = Math.floor((countControls-countStandards)/12) + ((countControls-countStandards)%12 > 0 ?1 : 0) + (countStandards > 0 ? 1 : 0) ;
		double rowsLeft = squaresLeft/12;
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
	public int calculateOtherControlsStartIndex (int countControls, int countStandards, WorklistSimple ws)
		{
		String doubleStr;
		// JAK new preview
		double rowsAboveStandards = Math.floor((countControls - (countStandards >12 ? 12 : countStandards))/12);
		double remainder = countControls - (countStandards >12 ? 12 : countStandards)%12;
		double countCalc = (countControls - (countStandards >12 ? 12 : countStandards));
		remainder = countCalc%12;
		if (remainder > 0 )
			rowsAboveStandards ++;
		// issue 146 take care of case where you have < 54 controls but have to use bottom standards row
		// JAK new preview		
		/*if (rowsAboveStandards > 5 && countControls <= 54)
			rowsAboveStandards = 5;*/
		if (ws.getIs96Well())
			{
			if (rowsAboveStandards > 7 && countControls <= 96)
				rowsAboveStandards = 7;
			return  (int) (countStandards == 0 ? (87 - (12*(rowsAboveStandards -1))) :   (87-(12*rowsAboveStandards))   );
			}
		
		else
			{
			if (rowsAboveStandards > 5 && countControls <= 54)
				rowsAboveStandards = 5;
			return  (int) (countStandards == 0 ? (45 - (9*(rowsAboveStandards -1))) :   (45-(9*rowsAboveStandards))   );
			} 
		
		}

	///////////////////////////////  issue 212 Original routines.... //////////////////
	List<WorklistItemSimple> grabUniqueItemsOriginal(List <WorklistItemSimple> items)
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
	
	
	public List<WorklistItemSimple> condenseSortAndSpaceOriginal(List<WorklistItemSimple> items)
		{
		// Group list there's only one control item per unique type assumes items already labeled by plate up front..
	   
		List <WorklistItemSimple> uniqueItems = grabUniqueItemsOriginal(items); 
	    List <WorklistItemSimple> spacedItems = new ArrayList <WorklistItemSimple> ();
		spacedItems = buildSpacedSortedListOriginal(uniqueItems);
	    return spacedItems;
		}
	
	public int calculateOtherControlsStartIndexOriginal (int countControls, int countStandards)
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
	
	
	
	Map<String, Integer> buildControlPositionIdxByTypeMapOriginal(List<WorklistItemSimple> items, WorklistSimple worklist) 
		{
		int standardsIndex = 0;
	    int prevIdx;
	    int nextIdx;
	    boolean standardsExist = false;
	    startOfStandards = 45; // 212
	    startOfOtherControls = 36;
	    startIdx = 0;
	    endIdx = 53;
	    
	    boolean startedAfterStandards = false;
		Map<String, Integer> controlTypeCountsMap = new HashMap<String, Integer>();
		Map<String, Integer> controlTypeToPositionIndexMap = new HashMap<String, Integer>();
		Map<String, Integer> subtractInjectionCountMap = new HashMap<String, Integer>();
		for (int i = 0; i < items.size(); i++)
			{	
			WorklistItemSimple item = items.get(i);				
			if (!item.getRepresentsControl()) continue;	
			String controlType = (item.getRepresentsUserDefinedControl() ? item.getNameForUserControlGroup() : ((WorklistControlGroup) item.getGroup()).getControlType());
			controlType = StringParser.parseId(controlType);
			// issue 201
			// issue 17
			if (controlType.indexOf("CS000QCMP") > -1  && worklist.getBothQCMPandMP())
				continue;
			
			// issue 201
			if (controlType.indexOf("R00CHRPL1-Pre") > -1 &&  bothPreinjectionsAndControl(items,"R00CHRPL1-Pre-01", "R00CHRPL1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("R00CHRUR1-Pre") > -1 && bothPreinjectionsAndControl(items,"R00CHRUR1-Pre-01", "R00CHRUR1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
					
			// issue 201
			if (controlType.indexOf("CS00000MP-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000MP-Pre-01", "CS00000MP-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM1-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM1-Pre-01", "CS000BPM1-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM2-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM2-Pre-01", "CS000BPM2-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM3-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM3-Pre-01", "CS000BPM3-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM4-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM4-Pre-01", "CS000BPM4-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 201
			if (controlType.indexOf("CS000BPM5-Pre") > -1 && bothPreinjectionsAndControl(items,"CS000BPM5-Pre-01", "CS000BPM5-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
			// issue 207
			if (controlType.indexOf("CS00000SB-Pre") > -1 && bothPreinjectionsAndControl(items,"CS00000SB-Pre-01", "CS00000SB-01") )
				{
				subtractInjectionCountMap.put(controlType, 1);
				continue;
				}
			
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
		if (subtractInjectionCountMap.size() > 0)
			countControls= countControls - subtractInjectionCountMap.size();
		int indexForOtherControls = 0;;
	    indexForOtherControls = calculateOtherControlsStartIndexOriginal(countControls,standardsIndex);
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
				{
				controlTypeToPositionIndexMap.put(controlType,  nextIdx++); 
				}
			}
		return controlTypeToPositionIndexMap;
		}

    
    // issue 153
    public void constructThePlateList (WorklistSimple worklist, int maxStartPlate)
    	{
    	double numPlates = 0;
    	int pltIdx = 0;
    	thePlateList = new ArrayList <String> (); // issue 212
    	pltIdx = Integer.parseInt(worklist.getStartPlate());
    	// issue 217
    
    	if (worklist.countOfSamplesForItems(worklist.getItems())+  (worklist.buildControlTypeMap().get(null) != null ? worklist.buildControlTypeMap().size()-1 : worklist.buildControlTypeMap().size()  ) <= (worklist.getCyclePlateLimit() * worklist.getMaxItemsAsInt()))
    		{
    		pltIdx = Integer.parseInt(worklist.getStartPlate());
    		for (int i = 0; i< worklist.getMaxStartPlate(); i++)
	    		{
	    		thePlateList.add(String.valueOf(pltIdx));
	    		if (pltIdx== worklist.getMaxStartPlate())
	    			pltIdx = 0;
	    		pltIdx ++;
	    		}
    		}
    	else 
    		{
    		//issue 212
    		numPlates =  Math.floor( (worklist.countOfSamplesForItems(worklist.getItems()) + worklist.buildControlTypeMap().size())/worklist.getMaxItemsAsInt()) ;
    		if  ( (worklist.countOfSamplesForItems(worklist.getItems()) + worklist.buildControlTypeMap().size())%worklist.getMaxItemsAsInt() > 0)
    		    numPlates ++;
    		for (int i = 1;i<= numPlates; i++)
    			thePlateList.add(String.valueOf(i));
    		}
    	}
     
    public void check96WellsUpdate(List <WorklistItemSimple> items)
		{
		if (items.get(0).getGroup().getParent().getIs96Well())
			{
			 List <WorklistItemSimple> lgetItems = new ArrayList <WorklistItemSimple>  ();
			condenseSortAndSpace(items);
			for (WorklistItemSimple lItemSimple : items.get(0).getGroup().getParent().getItems())
	        	{
	        	String sampleNamewoDash = lItemSimple.getSampleName().lastIndexOf("-" ) >= 0 ? lItemSimple.getSampleName().substring(0,lItemSimple.getSampleName().lastIndexOf("-" )) : lItemSimple.getSampleName() ;
	           	if (namePositionMap.get(sampleNamewoDash) == null)
	    	        lgetItems.add(lItemSimple);
	        	else
	        		lItemSimple.setSamplePosition(namePositionMap.get(sampleNamewoDash).replace("910",  "10").replace("911",  "11").replace("912",  "12"));
	        	}
			items.get(0).getGroup().getParent().getItems().removeAll(lgetItems);	
			} 	
		}

	}
		

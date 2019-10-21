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
	int nRows, nCols, nPositions;
	String startPos = "A1", endPos = "A1";
	Integer startIdx = 0, endIdx = 53;
	Boolean useCarousel = false;
	
	private int nPlates = 1;
	List<String> possiblePlatePositions = new ArrayList<String>();
	
	// issue 391 issue 403 issue 418
	// issue 422
	public static List <String> CONTROL_TYPES_IN_END_TO_START_ORDER = Arrays.asList(new String []
		     { "R00CHRUR1",   "R00CHRUR2", "R00CHRPL1", "R00CHRPL2",
		    // issue 22
		    "CSMR80025","CSMR80024","CSMR80023","CSMR80022", "CSMR80021",
		    "CSMR80020","CSMR80019","CSMR80018","CSMR80017", "CSMR80016",
			"CSMR80015",   "CSMR80014", "CSMR80013", "CSMR80012",
			"CSMR80011",   "CSMR80010", "CSMR80009", "CSMR80008","CS0UMRP01", "CS0UMRA01","CS0UMRL01", "CS0UMRG01", 
			"CS00000RC",   "CS00000MP", 
			"CS000QCMP", 
			"CS000BPM1", "CS000BPM2", // issue 17
			"CS000BPM3",   "CS000BPM4", "CS000BPM5", "CS0000OP0",
			"CS0000OP1",   "CS0000OP2", "CS0000OP3", "CS0000OP4",   "CS0000OP5", "CS0000OP6",
			"CS000STD0",   "CS000STD1", "CS000STD2", "CS000STD3",   "CS000STD4",
			"CS000STD5",   "CS000STD6", "CS000STD7", "CS000STD8",   "CS000STD9",
			"CS00STD10",   "CS00STD11", "CS00STD12", 
			"CS00000PB",   "CS00000SB", "CS00000NB", "CS00000QC" } // issue 450  
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
	    List <WorklistItemSimple> spacedItems = buildSpacedSortedList(uniqueItems);
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
	
	// issue 391
	private List<WorklistItemSimple> buildSpacedSortedList(List <WorklistItemSimple> uniqueItems)
		{
		Collections.sort(uniqueItems, new WorklistItemSimpleByPlatePosComparator());
		List<WorklistItemSimple> spacedList = new ArrayList<WorklistItemSimple>();		
		int itemsAdded = 0, j = 0;		
		String lastPlate = null, currPlate = null;
		boolean movedToNextPage = false;			
		while (itemsAdded < uniqueItems.size())
			{		
			String targetLabel = getExpectedPosition(j);
	               if (j%nPositions == 0 )
	                   lastPlate = null;
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
			
			// issue 391 issue 410
			if  (uniqueItems.size() < nPositions && uniqueItems.get(itemsAdded).getRepresentsControl() && !movedToNextPage ) 
			    {
				if (j > nPositions)
					movedToNextPage= true;
				spacedList.add(new WorklistItemSimple());
			    }			
			else if (samplePos != null && samplePos.trim().equals(targetLabel) && (lastPlate == null || currPlate.equals(lastPlate)) )			  				  
				spacedList.add(uniqueItems.get(itemsAdded++));
			else 
				if (uniqueItems.get(itemsAdded).getRepresentsControl())
				    spacedList.add(new WorklistItemSimple());
			lastPlate = currPlate;
			j++;			
			if (j > 20000)
				break;
			}
		return spacedList;
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
		int prevIdx = nPositions - 1;
		for (int i = 0; i < CONTROL_TYPES_IN_END_TO_START_ORDER.size(); i++)
			{
			String controlType = PlateListHandler.CONTROL_TYPES_IN_END_TO_START_ORDER.get(i);
			if (controlTypeCountsMap.get(controlType) != null) 
				controlTypeToPositionIndexMap.put(controlType,  prevIdx--);
			}
		for (int i = 0; i < items.size(); i++)
			{
			WorklistItemSimple item = items.get(i);
			if (!item.getRepresentsUserDefinedControl()) continue;
			
			String controlType = item.getNameForUserControlGroup();
	        if (!controlTypeToPositionIndexMap.containsKey(controlType) )
			    controlTypeToPositionIndexMap.put(controlType, prevIdx--);
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
			//System.out.println("here is plate string:" + item.getSampleName() + " " +  item.getSamplePosition());
			}
	    return spotsLeft;
	    }
	 
	 // issue 391 327
	 // issue 409
	 public int updatePlatePositionsForAgilent(WorklistSimple worklist) throws METWorksException
		{
		List<WorklistItemSimple> pageItemsArray = new ArrayList<WorklistItemSimple>();
		List<WorklistItemSimple> items = worklist.getItems();
		Boolean orderWasUploaded = worklist.wasCustomOrdered();		
		Map<Integer, String> map = buildPositionMap();	

		int idxForPage = 0, plate = 1, spotsLeft =  nRows * nCols;
		Integer targetIdx = 0; 		
		String plateStr = "";
		int idx = 1;	
		int nSpotsLeftOn2 = placeControlsByTypeOnPlate(worklist, map, 2);
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
				plate++;
				spotsLeft = (plate == 2 ? nSpotsLeftOn2 : nPositions); 
				// issue 404
				if (plate ==2 && nSpotsLeftOn2 == 0 ) 
				    {
				    plate ++;
				    spotsLeft = nPositions;
				    }
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
	}
	
	
	

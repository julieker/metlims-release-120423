//PlateWiseRandomizer.java
//Written by Jan Wigginton, November 2015

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.util.FormatVerifier;


public class PlateWiseRandomizer
	{
	
	// issue 401
 	public static void randomizeByPlate(List<WorklistItemSimple> original)
		{
		List<Integer> plateMembershipSequence = buildPlateMembershipSequence(original); 
		
		int lastPlateIdx = original.get(0).belongsToPlate;
		for (int i = 0; i < original.size(); i++)
			lastPlateIdx = Math.max(lastPlateIdx, original.get(i).belongsToPlate);
		
	        Map<Integer, List<WorklistItemSimple>> randomizedSampleByPlateMap = new HashMap<Integer, List<WorklistItemSimple>>();	    
		for (int plate = 0;  plate <= lastPlateIdx; plate++)
			{
	    	List<WorklistItemSimple> block = buildPageList(plate, original);
	                   // don't randomize for control plate
	      // issue 409
	    	//  if (plate == 1) 
	        //    {
			//	randomizedSampleByPlateMap.put(plate, block);
			 //   continue;
	         //   }	
	                  // otherwise we randomize
			List<WorklistItemSimple> randomizedBlock = randomizeBlockSamples(block);
		    randomizedSampleByPlateMap.put(plate, randomizedBlock);
			}		
		List<Integer> nextIndexByPlate = new ArrayList<Integer>();
		for (int i = 0; i <= lastPlateIdx; i++)
			nextIndexByPlate.add(0);		
		List<WorklistItemSimple> randomizedList = new ArrayList<WorklistItemSimple>();		
		List<WorklistItemSimple> listToSample = null;
		for (int i = 0; i < plateMembershipSequence.size(); i++) {
			int plateForNextItem = plateMembershipSequence.get(i);
			listToSample = randomizedSampleByPlateMap.get(plateForNextItem);
			int listIdxToSample = nextIndexByPlate.get(plateForNextItem);			
			randomizedList.add(listToSample.get(listIdxToSample));
			nextIndexByPlate.set(plateForNextItem, ++listIdxToSample);
		}			
		original.clear();
		for (int i = 0; i < randomizedList.size(); i++)
			original.add(randomizedList.get(i));
		}
	
 	// issue 416
	public static void randomizeByWorklist(List<WorklistItemSimple> original)
		{
		if (original.get(0).getGroup().getParent().getIs96Well())
	    	{
			int nPlateRows = 8, nPlateCols = 12;
		    PlateListHandler plateListHandler = new PlateListHandler(nPlateRows, nPlateCols,false);	
		    plateListHandler.check96WellsUpdate(original);
	    	}
		List<WorklistItemSimple> block = original;	
		List<WorklistItemSimple> randomizedBlock = randomizeBlockSamples(block);
		List<WorklistItemSimple> randomizedList = new ArrayList<WorklistItemSimple>();		
		List<WorklistItemSimple> listToSample = null;		
		for (WorklistItemSimple item : randomizedBlock)
		     randomizedList.add(item);					
		original.clear();
		for (int i = 0; i < randomizedList.size(); i++)
			original.add(randomizedList.get(i));
		}
		
	
	private static List<Integer> buildPlateMembershipSequence(List<WorklistItemSimple> original) 
        {
		List<Integer> plateMembershipSequence = new ArrayList<Integer>();
		for (int i = 0; i < original.size(); i++)
			plateMembershipSequence.add(original.get(i).belongsToPlate);		
		return plateMembershipSequence;
        }
	
	
/****************/
	private static List<WorklistItemSimple> buildPageList(final int target, final List<WorklistItemSimple> original)
		{
		List<WorklistItemSimple> blockList = new ArrayList<WorklistItemSimple>();

		for (int i = 0; i < original.size(); i++)
			{
			if (original.get(i).belongsToPlate == target)
				blockList.add(original.get(i));
			}

		return blockList;
		}

	
	// issue 311
	private static List<WorklistItemSimple> randomizeBlockSamples( List<WorklistItemSimple> list)
	    {
	    List<WorklistItemSimple> randomized = new ArrayList<WorklistItemSimple>();
	    int lastIdx = 0;
	    for (int i = 0; i < list.size(); i++)
		    {
		    WorklistItemSimple item = list.get(i);
		    if (item.getRepresentsControl() || !FormatVerifier.verifyFormat(Sample._2019Format, item.getSampleName()))
		    	continue;
		    addItemToListRandomly(item, randomized, lastIdx++);
		    }
	    List<WorklistItemSimple> finalList = new ArrayList<WorklistItemSimple>();
	    int nSamplesPlaced = 0; // randomized.size();
	    for (int i = 0; i < list.size(); i++)
		    {
		    if (list.get(i).getRepresentsControl() || !FormatVerifier.verifyFormat(Sample._2019Format, list.get(i).getSampleName()))
			    finalList.add(list.get(i));
		    else
			    finalList.add(randomized.get(nSamplesPlaced++));
		    }
	    return finalList;
	    }

	
	private static void addItemToListRandomly(WorklistItemSimple w, List<WorklistItemSimple> list, int lastIdx)
		{
		int slot_for_new = (int) Math.floor(Math.random() * (lastIdx + 1));

		list.add(w);

		list.get(slot_for_new).setRandomIdx(lastIdx + 1);
		w.setRandomIdx(slot_for_new + 1);

		list.set(lastIdx, list.get(slot_for_new));

		list.set(slot_for_new, w);
		// lastIdx++;
		}
	}

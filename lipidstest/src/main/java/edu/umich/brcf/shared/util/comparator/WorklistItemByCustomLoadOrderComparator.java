////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemBySCustomLoadOrderComparator.java
//  Written by julie Keros 
//  March 2022
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;

// split(
public class WorklistItemByCustomLoadOrderComparator implements
		Comparator<WorklistItemSimple>
	{
	public int compare(WorklistItemSimple o1, WorklistItemSimple o2)
		{
		if (o1.getCustomLoadOrder() == (o2.getCustomLoadOrder()))
			return -1;

		return o1.getCustomLoadOrder().compareTo(o2.getCustomLoadOrder());
		}
	}

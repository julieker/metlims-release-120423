////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemBySampleIdComparator.java
//  Written by Jan Wigginton
//  May 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;

// split(
public class WorklistItemBySampleIdComparator implements
		Comparator<WorklistItemSimple>
	{
	public int compare(WorklistItemSimple o1, WorklistItemSimple o2)
		{
		if (o1.getSampleName().equals(o2.getSampleName()))
			return -1;

		return o1.getSampleName().compareTo(o2.getSampleName());
		}
	}

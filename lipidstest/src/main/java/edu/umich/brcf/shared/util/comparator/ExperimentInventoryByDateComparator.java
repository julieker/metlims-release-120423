///////////////////////////////////////
//ExperimentInventoryByDateComparator.java
//Written by Jan Wigginton July 2015
///////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.ExperimentInventoryInfo;


public class ExperimentInventoryByDateComparator implements Comparator<ExperimentInventoryInfo> 
	{
	@Override
	public int compare(ExperimentInventoryInfo arg0, ExperimentInventoryInfo arg1) 
		{
		return arg1.getCompletionDate().compareTo(arg0.getCompletionDate());
		}
	}
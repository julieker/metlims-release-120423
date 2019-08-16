package edu.umich.brcf.shared.util.comparator;

////////////////////////////////////////////////////////////////////
//Mrc2ExperimentalDesignItem.java
//Written by Jan Wigginton September 2015
////////////////////////////////////////////////////////////////////

import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.Mrc2ExperimentalDesignItem;


public class Mrc2ExperimentalDesignItemComparator implements Comparator<Mrc2ExperimentalDesignItem>
	{
	@Override
	public int compare(Mrc2ExperimentalDesignItem o1, Mrc2ExperimentalDesignItem o2)
		{
		if (o1 != null && o1.getSampleId() != null && o2 != null
				&& o2.getSampleId() != null)
			return o1.getSampleId().compareTo(o2.getSampleId());

		return -1;
		}
	}

////////////////////////////////////////////////////
// Mrc2TransitionalExperimentDesignItemComparator.java
// Written by Jan Wigginton, Jun 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.Mrc2TransitionalExperimentDesignItem;


public class Mrc2TransitionalExperimentDesignItemComparator implements Comparator<Mrc2TransitionalExperimentDesignItem>
	{
	@Override
	public int compare(Mrc2TransitionalExperimentDesignItem o1, Mrc2TransitionalExperimentDesignItem o2)
		{
		if (o1 != null && o1.getSampleId() != null && o2 != null && o2.getSampleId() != null)
			return o1.getSampleId().compareTo(o2.getSampleId());

		return -1;
		}
	}

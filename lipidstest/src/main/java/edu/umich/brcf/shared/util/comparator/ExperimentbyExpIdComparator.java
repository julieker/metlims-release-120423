////////////////////////////////////////////////////
// ExperimentbyExpIdComparator.java
// Written by Jan Wigginton, Jun 12, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.layers.domain.Experiment;


public class ExperimentbyExpIdComparator implements  Comparator<Experiment>
	{
	@Override
	public int compare(Experiment o1, Experiment o2)
		{
		if (o1 != null && o1.getExpID() != null && o2 != null
				&& o2.getExpID() != null)
			
			return o1.getExpID().compareTo(o2.getExpID());

		return -1;
		}
	}

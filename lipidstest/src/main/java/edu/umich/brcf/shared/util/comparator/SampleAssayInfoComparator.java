////////////////////////////////////////////////////
// SampleAssayInfoComparator.java
// Written by Jan Wigginton, Mar 22, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.SampleAssayInfo;


public class SampleAssayInfoComparator implements Comparator<SampleAssayInfo>, Serializable 
	{
	public int compare(SampleAssayInfo o1, SampleAssayInfo o2) 
		{
		return o1.getSampleId().compareTo(o2.getSampleId());
		}

	}


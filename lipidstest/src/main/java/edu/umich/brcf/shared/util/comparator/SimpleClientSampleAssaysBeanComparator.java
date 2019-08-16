////////////////////////////////////////////////////
// SimpleClientSampleAssayssBeanComparator.java
// Written by Jan Wigginton, Mar 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.SimpleClientSampleAssaysBean;


public class SimpleClientSampleAssaysBeanComparator implements Comparator<SimpleClientSampleAssaysBean> 
	{
	@Override
	public int compare(SimpleClientSampleAssaysBean o1, SimpleClientSampleAssaysBean o2) 
		{
		if (o1 != null && o1.getSampleId() != null && o2 != null && o2.getSampleId() != null)
		return o1.getSampleId().compareTo(o2.getSampleId());
		return -1;
		}
	}

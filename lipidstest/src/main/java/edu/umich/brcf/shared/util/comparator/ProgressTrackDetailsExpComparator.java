////////////////////////////////////////////////////
// ProgressTrackDetailsExpComparator.java
// Written by Jan Wigginton, Jun 12, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.layers.domain.ProcessTrackingDetails;


public class ProgressTrackDetailsExpComparator implements  Comparator<ProcessTrackingDetails>
	{
	@Override
	public int compare(ProcessTrackingDetails o1, ProcessTrackingDetails o2)
		{
		/*
		String strFirstDO = "";
		String strSecondDO = "";
		if (o1 != null && o1.getDetailOrder() != null && o2 != null
				&& o2.getDetailOrder() != null)
			
			return o1.getDetailOrder().compareTo(o2.getDetailOrder());

		return -1;
	*/
	/*	if (o1 != null && o1.getDetailOrder() != null && o2 != null
				&& o2.getDetailOrder() != null)
			{
			
			strFirstDO = o1.getWorkflow().getWfID() + " " + o1.getExperiment().getExpID()  + " " + o1.getAssay().getAssayId() + " " + String.format("%08d", o1.getDetailOrder());
			strSecondDO = o2.getWorkflow().getWfID() + " " + o2.getExperiment().getExpID()  + " " + o2.getAssay().getAssayId() + " " + String.format("%08d", o2.getDetailOrder());
			System.out.println("here is first and second string:" + strFirstDO + " " + strSecondDO + " " + (strFirstDO.compareTo(strSecondDO)));
			
			return strFirstDO.compareTo(strSecondDO); 

			}
		return -1; */
		
		if (o1 != null && o1.getExperiment().getExpID() != null && o2 != null
				&& o2.getExperiment().getExpID() != null)			
			return o1.getExperiment().getExpID().compareTo(o2.getExperiment().getExpID());
		return -1;		
		}
	}

package edu.umich.brcf.shared.util.comparator;
///////////////////////////////////////////////////////////////////
// WorklistItemSimpleByPlatePosComparator.java
// Written by Jan Wigginton June 2015
////////////////////////////////////////////////////////////////////


import java.io.Serializable;
import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;




public class WorklistItemSimpleByPlatePosComparator implements Comparator<WorklistItemSimple>, Serializable {

	
	public int compare(WorklistItemSimple o1, WorklistItemSimple o2) {
		// Issue 283
		String plateStr1 =   o1.getSamplePosition().indexOf("-") == -1 ? "" :o1.getSamplePosition().substring(1,o1.getSamplePosition().indexOf("-"));
		String plateStr2 =   o2.getSamplePosition().indexOf("-") == -1 ? "" :o2.getSamplePosition().substring(1,o2.getSamplePosition().indexOf("-"));
		if (plateStr1 != "" && plateStr2 != "")
	        if (Integer.parseInt(plateStr1) > Integer.parseInt(plateStr2))
			    return 1;		   
	        else if (Integer.parseInt(plateStr1) < Integer.parseInt(plateStr2))
	        	return -1;
	        else	
	            {
		        if (o1.getSamplePosition().equals(o2.getSamplePosition()))
			        return 1;
		        return o1.getSamplePosition().compareTo(o2.getSamplePosition());
	            }
		else 
		    {
			if (o1.getSamplePosition().equals(o2.getSamplePosition()))
	            return 1;
            return o1.getSamplePosition().compareTo(o2.getSamplePosition());
		    }	
		}
			   
	}


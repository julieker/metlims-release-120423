////////////////////////////////////////////////////////////////////
//SubjectInfoComparator.java
//Written by Jan Wigginton June 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;


import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoItem;

//import edu.umich.brcf.shared.panels.datacollectors.SubjectInfoItem;




public class SubjectInfoComparator implements Comparator<SubjectInfoItem> 
	{

	@Override
	public int compare(SubjectInfoItem o1, SubjectInfoItem o2) 
		{
		return o1.getSubjectId().compareTo(o2.getSubjectId());
		}
	}

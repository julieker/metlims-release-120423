////////////////////////////////////////////////////////////////////
// DrccSampleInfoComparator.java
// Written by Jan Wigginton June 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.lipidshome.drcc.DrccSubjectInfoItem;




public class DrccSubjectInfoComparator implements Comparator<DrccSubjectInfoItem> {
	
	@Override
	public int compare(DrccSubjectInfoItem o1, DrccSubjectInfoItem o2) {
		return o1.getSubjectId().compareTo(o2.getSubjectId());
	}

}

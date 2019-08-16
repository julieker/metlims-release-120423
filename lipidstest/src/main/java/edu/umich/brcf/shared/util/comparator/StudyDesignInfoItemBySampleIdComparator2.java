////////////////////////////////////////////////////
// StudyDesignInfoItemBySampleIdComparator2.java
// Written by Jan Wigginton, Sep 9, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.StudyDesignInfoItem;



//import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.StudyDesignInfoItem;


public class StudyDesignInfoItemBySampleIdComparator2 implements Comparator<StudyDesignInfoItem>
		{
		@Override
		public int compare(StudyDesignInfoItem o1, StudyDesignInfoItem o2)
			{
			if (o1 != null && o1.getSampleId() != null && o2 != null
			&& o2.getSampleId() != null)
			return o1.getSampleId().compareTo(o2.getSampleId());
			return -1;
			}
		}
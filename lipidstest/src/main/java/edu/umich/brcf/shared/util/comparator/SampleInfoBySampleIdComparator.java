package edu.umich.brcf.shared.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.SampleInfoItem;


public class SampleInfoBySampleIdComparator implements
		Comparator<SampleInfoItem>, Serializable
	{
	public int compare(SampleInfoItem o1, SampleInfoItem o2)
		{
		return o1.getSampleId().compareTo(o2.getSampleId());
		}
	}

/*
 * ////////////////////////////////////////////////////////////////////
 * //DrccSampleInfoComparator.java //Written by Jan Wigginton June 2015
 * ////////////////////////////////////////////////////////////////////
 * 
 * package edu.umich.metworks.lims.comparator;
 * 
 * import java.util.Comparator;
 * 
 * import edu.umich.metworks.web.panels.analysis.drcc.DrccStudyDesignInfoItem;
 * 
 * 
 * public class DrccSampleInfoComparator implements
 * Comparator<DrccStudyDesignInfoItem> {
 * 
 * @Override public int compare(DrccStudyDesignInfoItem o1,
 * DrccStudyDesignInfoItem o2) { if (o1 != null && o1.getSampleId() != null &&
 * o2 != null && o2.getSampleId() != null) return
 * o1.getSampleId().compareTo(o2.getSampleId()); return -1; }
 * 
 * }
 */
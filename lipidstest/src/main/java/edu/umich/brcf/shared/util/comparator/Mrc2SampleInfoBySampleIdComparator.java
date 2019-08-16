package edu.umich.brcf.shared.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.Mrc2SampleInfoItem;



public class Mrc2SampleInfoBySampleIdComparator implements Comparator<Mrc2SampleInfoItem>, Serializable {

	public int compare(Mrc2SampleInfoItem o1, Mrc2SampleInfoItem o2) {
		return o1.getSampleId().compareTo(o2.getSampleId());
	}

}
//Mrc2SamplenfoComparator
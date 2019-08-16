package edu.umich.brcf.shared.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import edu.umich.brcf.shared.layers.domain.Sample;

public class SampleBySampleIdComparator implements Comparator<Sample>,
		Serializable
	{

	public int compare(Sample o1, Sample o2)
		{
		return o1.getSampleID().compareTo(o2.getSampleID());
		}

	}

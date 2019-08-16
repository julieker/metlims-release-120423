package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.util.datacollectors.ExperimentalDesignItem;


public class ExperimentalDesignItemComparator implements
		Comparator<ExperimentalDesignItem>
	{
	@Override
	public int compare(ExperimentalDesignItem o1, ExperimentalDesignItem o2)
		{
		if (o1 != null && o1.getSampleId() != null && o2 != null
				&& o2.getSampleId() != null)
			return o1.getSampleId().compareTo(o2.getSampleId());

		return -1;
		}
	}

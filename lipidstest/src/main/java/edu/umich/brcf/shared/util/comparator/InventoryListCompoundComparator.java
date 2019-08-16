package edu.umich.brcf.shared.util.comparator;

import java.util.Comparator;

import edu.umich.brcf.shared.layers.domain.Inventory;


/**
 * compares two inventory items on location and primary name of a compound.
 */
public class InventoryListCompoundComparator implements Comparator<Inventory> {
	public int compare(Inventory o1, Inventory o2) {
		if (o1.getLocation().getLocationId().equals(o2.getLocation().getLocationId()))
			return o1.getCompound().getPrimaryName().compareToIgnoreCase(o2.getCompound().getPrimaryName());
		else
			return o1.getLocation().getLocationId().compareTo(o2.getLocation().getLocationId());
	}

}

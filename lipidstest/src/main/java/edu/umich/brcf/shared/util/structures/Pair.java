package edu.umich.brcf.shared.util.structures;

import java.io.Serializable;

import edu.umich.brcf.shared.util.interfaces.ISampleItem;

public class Pair implements Serializable, ISampleItem
	{
	String id = "";
	String value = "";
	
	public Pair(String id, String value)
		{
		this.id = id;
		this.value = value;
		}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString()
		{
		return id + ", " + value;
		}

	@Override
	public String getSampleId()
		{
		return id;
		}
	}

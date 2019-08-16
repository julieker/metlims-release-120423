package edu.umich.brcf.shared.util.structures;

import java.io.Serializable;

public class SelectableObject implements Serializable
	{
	private Object selectionObject;
	private boolean selected = false;

	private Integer indexSlot = 0;

	public SelectableObject(Object selectionObject)
		{
		this.selectionObject = selectionObject;
		}

	public SelectableObject()
		{
		selectionObject = null;
		// TODO Auto-generated constructor stub
		}

	public Integer getIndexSlot()
		{
		return indexSlot;
		}

	public void setIndexSlot(Integer idx)
		{
		this.indexSlot = idx;
		}

	public String getIndexSlotStr()
		{
		String strIdx;
		if (indexSlot < 10)
			strIdx = "  " + indexSlot;
		else if (indexSlot < 100)
			strIdx = " " + indexSlot;

		else
			strIdx = indexSlot.toString();

		return strIdx;
		}

	public Object getSelectionObject()
		{
		return selectionObject;
		}

	public void setSelectionObject(Object selectionObject)
		{
		this.selectionObject = selectionObject;
		}

	public boolean isSelected()
		{
		return selected;
		}

	public void setSelected(boolean selected)
		{
		this.selected = selected;
		}
	}

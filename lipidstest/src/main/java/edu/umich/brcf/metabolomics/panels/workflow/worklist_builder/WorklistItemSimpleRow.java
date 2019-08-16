////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemSimpleRow.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
//import org.springframework.util.Assert;


public class WorklistItemSimpleRow implements Serializable
	{
	int nItemsPerRow;
	int rowIndex = -1;

	List<WorklistItemSimple> rowItems = new ArrayList<WorklistItemSimple>();

	WorklistItemSimpleRow(int nItems, int rowIndex)
		{
		this.rowIndex = rowIndex;
		this.nItemsPerRow = nItems;
		for (int i = 0; i < nItems; i++)
			rowItems.add(new WorklistItemSimple());
		}

	public void setItemAt(int i, WorklistItemSimple item)
		{
		Assert.assertTrue(i >= 0 && i < nItemsPerRow);

		rowItems.set(i, item);
		}

	public void printNames()
		{
		for (int i = 0; i < rowItems.size(); i++)
			System.out.println(rowItems.get(i).getSampleName() + "\t");
		}

	public WorklistItemSimple getItem(int i)
		{
		return rowItems.get(i);
		}

	public String getName(int i)
		{
		return rowItems.get(i).getSampleName();
		}

	public int getRowIndex()
		{
		return rowIndex;
		}
	}

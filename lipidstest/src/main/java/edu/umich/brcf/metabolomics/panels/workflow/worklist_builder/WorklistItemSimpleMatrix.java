////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  WorklistItemSimpeMatrix.java
//  Written by Jan Wigginton
//  July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorklistItemSimpleMatrix implements Serializable
	{
	int nRows = 0, nItemsPerRow = 0;
	List<WorklistItemSimpleRow> rows = new ArrayList<WorklistItemSimpleRow>();
	
	public WorklistItemSimpleMatrix() {   }
	
	
	public WorklistItemSimpleMatrix(int nItemsPerRow, int nItemsPerCol)
		{
		this.nItemsPerRow = nItemsPerRow;
		this.nRows = nItemsPerCol;
		
		for (int i = 0; i < nRows; i++)
			{
			WorklistItemSimpleRow row = new WorklistItemSimpleRow(nItemsPerRow, i);
			rows.add(row);
			}
		}
	
	
	public WorklistItemSimpleMatrix(int nRows, int nCols, List<WorklistItemSimple> worklist)
		{
		this(nCols, nRows);
		
		for (int i = 0; i < worklist.size(); i++)
			{
			int col = i % nCols;
		    int row = (int) (Math.floor(i/nCols));
		    rows.get(row).setItemAt(col, worklist.get(i));
			}
		}
	
	
	public List<WorklistItemSimpleRow> getRows()
		{
		return rows;
		}
	
	public void setRows(List<WorklistItemSimpleRow> rows)
		{
		this.rows = rows;
		}
	
	
	public void printNames()
		{
		for (int i = 0; i < rows.size(); i++)
			rows.get(i).printNames();
		}
	}
	
	
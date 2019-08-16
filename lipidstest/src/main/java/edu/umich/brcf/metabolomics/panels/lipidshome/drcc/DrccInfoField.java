// DrccInfoField.java
// Written by Jan Wigginton July 2015

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

import java.io.Serializable;
import java.util.ArrayList;

import junit.framework.Assert;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



// DrccMainPage
public class DrccInfoField implements Serializable, IWriteConvertable
	{	
	private ArrayList<String> fieldValues = new ArrayList<String>();
	public ArrayList <Boolean> colsToPrint = new ArrayList<Boolean>();

	//private String fieldValue;
	private String fieldLabel;
	private String fieldTag;
	
	public DrccInfoField(String label, String value)
		{
		this(label, "", value, 1);
		}

	
	public DrccInfoField(String label, String tag, String value)
		{
		this(label, tag, value, 1);
		}
	
	public DrccInfoField(String label, String tag, String value, int reps)
		{
		this.fieldTag = tag;
		this.fieldLabel = label;
		
		for (int i = 0; i < reps; i++)
			{
			fieldValues.add(value);
			colsToPrint.add(true);
			}
		}
	
	public void setColPrinted(int i, Boolean print)
		{
		Assert.assertFalse(i < 0 || i >= colsToPrint.size());

		colsToPrint.set(i, print);
		}
	
	
	public DrccInfoField(String label, String tag, String value1, String value2)
		{
		this.fieldTag = tag;
		this.fieldLabel = label;
		
		fieldValues.add(value1);
		colsToPrint.add(true);
		colsToPrint.add(true);
		fieldValues.add(value2);
		}
	
	
	public String getFieldTag() {
		return fieldTag;
	}

	public void setFieldTag(String fieldTag) {
		this.fieldTag = fieldTag;
	}

	public String getFieldLabel() 
		{
		return fieldLabel;
		}

	public void setFieldLabel(String fieldLabel) 
		{
		this.fieldLabel = fieldLabel;
		}

	public ArrayList<String> getFieldValues()
		{
		return fieldValues;
		}
		
	public void setFieldValues(ArrayList <String> values)
		{
		this.fieldValues = values;
		}
	
	public String getFieldValues(int i)
		{
		return fieldValues.get(i);
		}
	
	public void setFieldValues(int i, String value)
		{
		Assert.assertTrue(i >= 0 && i < fieldValues.size());
		
		fieldValues.set(i, value);
		}


	@Override
	public String toCharDelimited(String delimiter) 
		{
		StringBuilder builder = new StringBuilder();
		builder.append(fieldLabel + delimiter);
		
		for (int i = 0; i < fieldValues.size(); i++)
			{
			if (colsToPrint.get(i).equals(true))
				builder.append(fieldValues.get(i) + delimiter);
			}
		
		return builder.toString();
		}	

	
	@Override
	public String toExcelRow() {
		// TODO Auto-generated method stub
		return null;
	}
	}

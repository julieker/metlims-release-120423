///////////////////////////////////////
// InfoLine.java
// Written by Jan Wigginton June 2015
///////////////////////////////////////

package edu.umich.brcf.shared.util;

import java.io.Serializable;

import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



public class InfoLine implements IWriteConvertable, Serializable 
	{
	private String label;
	private PropertyModel <String> model;
	private boolean removeCommas;
	
	public InfoLine(String label, PropertyModel<String> model)
		{
		this(label, model, true);
		}
		
	
	public InfoLine(String label, PropertyModel <String> model, boolean removeCommas)
		{
		this.removeCommas = removeCommas;
		this.label = label;
		this.model = model;
		}
	
	
	
	@Override
	public String toCharDelimited(String separator)
		{
		if (model == null || model.getObject() == null)
			return (label == null ? "" : label.replace(",",  ""));
		
		if (removeCommas)
			return label.replace(",", " ") + separator + model.getObject().replace(",", " ");
		
		return label + separator + model.getObject();
		}	

	
	@Override
	public String toExcelRow() 
		{
		return "";
		}
	}

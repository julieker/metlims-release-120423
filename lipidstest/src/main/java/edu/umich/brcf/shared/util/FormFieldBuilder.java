////////////////////////////////////////////////////
// FormFieldBuilder.java
// Written by Jan Wigginton, Jun 17, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.shared.util.interfaces.ISampleItem;


public class FormFieldBuilder implements Serializable
	{
	private Map<String, Integer> fieldIndex;
	
	public FormFieldBuilder(List<? extends ISampleItem> items, List<String> fieldTags)
		{
		fieldIndex = buildFieldIndex(items, fieldTags);
		}

	
	public TextField<String> buildTabbedStringField(String id, final ISampleItem entry, final String property, String label)
		{
		TextField<String> fld = buildTabbedStringField(id, entry, property);
		fld.setLabel(new Model<String>(label));
		return fld;
		}
	

	public TextField<String> buildTabbedStringField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld = buildGenericTextField(id, entry, property);
		
		if (FieldLengths.grabFieldLength(property) != -1)
			fld.add(StringValidator.maximumLength(FieldLengths.grabFieldLength(property)));
		
		return fld;
		}
	

	public TextField<String> buildTabbedBigDecimalField(String id, final ISampleItem entry, final String property, String label)
		{
		TextField<String> fld = buildTabbedBigDecimalField(id, entry, property);
		fld.setLabel(new Model<String>(label));
		return fld;
		}
	
	public TextField<String> buildTabbedLongField(String id, final ISampleItem entry, final String property, String label)
		{
		TextField<String> fld = buildTabbedLongField(id, entry, property);
		fld.setLabel(new Model<String>(label));
		return fld;
		}

	
	public TextField<String> buildTabbedBooleanField(String id, final ISampleItem entry, final String property, String label)
		{
		TextField<String> fld = buildTabbedBooleanField(id, entry, property);
		fld.setLabel(new Model<String>(label));
		return fld;
		}
	
	
	public TextField<String> buildTabbedIntegerField(String id, final ISampleItem entry, final String property, String label)
		{
		TextField<String> fld = buildTabbedIntegerField(id, entry, property);
		fld.setLabel(new Model<String>(label));
		return fld;
		}

	
	public TextField<String> buildTabbedLongField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld = buildGenericTextField(id, entry, property);
		
		fld.setType(Long.class);
		return fld;
		}

	
	public TextField<String> buildTabbedBigDecimalField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld = buildGenericTextField(id, entry, property);
		
		fld.setType(BigDecimal.class);
		return fld;
		}
	
	
	public TextField<String> buildTabbedIntegerField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld = buildGenericTextField(id, entry, property);
		
		fld.setType(Integer.class);
		return fld;
		}
	
	
	public TextField<String> buildTabbedBooleanField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld = buildGenericTextField(id, entry, property);
		
		fld.setType(Boolean.class);
		return fld;
		}
	
	
	private TextField buildGenericTextField(String id, final ISampleItem entry, final String property)
		{
		TextField<String> fld =  new TextField<String>(id, new PropertyModel<String>(entry, property))
			{
			@Override
			protected void onComponentTag(ComponentTag tag)
				{
				super.onComponentTag(tag);
				
				Integer idxForPair = fieldIndex.get(property + "." + entry.getSampleId());
				if (idxForPair != null)
					idxForPair++;
				
				String label = "" + idxForPair;
				tag.put("tabIndex", label);
				}
			};

		return fld;
		}
	
	
	public Map<String, Integer> buildFieldIndex(List<? extends ISampleItem> items, List<String> fieldTags)
		{
		if (items == null) return null;
		
		Map<String, Integer> fieldIndex = new HashMap<String, Integer>();
		
		int fldIdx = 0;
		for (String tag : fieldTags)
			for (ISampleItem item : items)
				{
				String key = tag + "." + item.getSampleId();
				fieldIndex.put(key, fldIdx++);
				}
		
		return fieldIndex;
		}
	
	
	public Map<String, Integer> getFieldIndex()
		{
		return fieldIndex;
		}
	}

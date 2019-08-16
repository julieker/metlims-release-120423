package edu.umich.brcf.shared.util.widgets;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.PropertyModel;


public abstract class METWorksAjaxUpdatingDateTextField extends DateTextField 
	{
	DatePicker picker;
	private boolean isFieldVisible = true, isPickerVisible = true;
	private String defaultStringFormat = "MM/dd/yyyy";
	

	public METWorksAjaxUpdatingDateTextField(String id, PropertyModel propertyModel, final String change) 
		{
		super(id, propertyModel, new StyleDateConverter("S-", true));
		
		add(picker = new DatePicker()
			{
			@Override
			public void onComponentTag(Component c, ComponentTag tag)
				{
				super.onComponentTag(c, tag);
				tag.put("visibility", isPickerVisible ? "visible" : "none");
	        	}
			});
		
		picker.setShowOnFieldClick(true);
	//	add(new AjaxFormComponentUpdatingBehavior(change) {
	//		protected void onUpdate(AjaxRequestTarget target) {
	//			METWorksAjaxUpdatingDateTextField.this.onUpdate(target);
	//		}
		
	//	});
	}


	public String toString()
		{
		return grabValueAsString();
		}
	
	@Override
	public boolean isVisible()
		{
		return isFieldVisible;
		}
	
	public void setFieldVisible(boolean b)
		{
		isFieldVisible = b; 
		isPickerVisible = b;	
		setVisible(b);
		}
	
	public String grabValueAsString()
		{
		Date date = grabValueAsDate();
		SimpleDateFormat sdf = new SimpleDateFormat(defaultStringFormat);
		return sdf.format(date);
		}		
	

	public Date grabValueAsDate()
		{
		String dateStr = getModelObject().toString();
	
		Date date = new Date();
		String format = "EEE MMM dd HH:mm:ss zzz yyyy";
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	    try {
			date = (Date)formatter.parse(dateStr);
			}
	    catch (ParseException e) 
	    	{
	    	try
	    		{
	    		formatter = new SimpleDateFormat("MM/dd/yyyy");
	    		date = (Date) formatter.parse(dateStr);
	    		return date;
	    		}
	    	catch (ParseException f)
	    		{
	    		f.printStackTrace();
	    		}
	    	}

	    return date;
		}
	    
	
	public Calendar grabValueAsCalendar()
		{
		Calendar dateCal = Calendar.getInstance();
		Date date;
		String dateStr = getModelObject().toString();
		//EEE MMM dd HH:mm:ss zzz yyyy
	
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	    try {
			date = (Date)formatter.parse(dateStr);
			dateCal.setTime(date);
			}
	    catch (ParseException e) 
	    	{
			e.printStackTrace();
			return dateCal;
			}
	    
	    return dateCal;
	    }
	
	
	public String getDefaultStringFormat()
		{
		return defaultStringFormat;
		}


	public void setDefaultStringFormat(String defaultStringFormat)
		{
		this.defaultStringFormat = defaultStringFormat;
		}


	protected abstract void onUpdate(AjaxRequestTarget target);
}
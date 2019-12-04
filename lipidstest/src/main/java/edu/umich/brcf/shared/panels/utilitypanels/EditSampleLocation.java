package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;


public abstract class EditSampleLocation extends WebPage
	{
	@SpringBean 
	LocationService locationService;
	
	private String location,  oldLocation;
	
	public EditSampleLocation(Page backPage, final ModalWindow modal)
		{
		add(new FeedbackPanel("feedback"));
		add(new EditSampleLocationForm("editSampleLocationForm", modal));
		location = "";
		}
	
	public final class EditSampleLocationForm extends Form 
		{
		String unit= "";
		 
		public EditSampleLocationForm(final String id, ModalWindow modal)
			{
			super(id);
		
			List<String> locationChoices = new ArrayList<String>();	
			final DropDownChoice locationsDD= new DropDownChoice("locationsDD",  new PropertyModel(this, "location"),  locationChoices)
				{
				//new LoadableDetachableModel<List< 
				//{
	        	///@Override
	        	//protected List<String> load() 
	        	//	{ 
	    		//	String unit = getUnit();
	    		//	return (unit != null ? ((ArrayList <String>) locationService.getLocationNamesByUnit(unit))
	    		//	  : new ArrayList <String> ());
	        	//	}
	        	//})
				};
				
			
			locationsDD.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target) {  }
				});
			
			locationsDD.setRequired(true);
			locationsDD.setOutputMarkupId(true);
			add(locationsDD);
			
			final DropDownChoice unitsDD=new DropDownChoice("unitsDD",  new PropertyModel(this, "unit"), locationService.getDistinctUnitsForSamples());

			setUnit("-80 freezer");
			locationsDD.setChoices(unit != null ? ((ArrayList <String>) locationService.getSampleLocationNamesByUnit(unit)) :  new ArrayList <String> ());
			
			unitsDD.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					String unit = getUnit();
					locationsDD.setChoices(unit != null ? ((ArrayList <String>) locationService.getSampleLocationNamesByUnit(unit)) : new ArrayList <String> ());
					target.add(locationsDD);
					}
				});
			unitsDD.setRequired(true);
			add(unitsDD);
				
			add(new IndicatingAjaxLink <Void> ("saveChanges")
			// issue 39
				{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					try { 
						EditSampleLocation.this.onSave(getLocation(), target); 
						
					//	EditSampleLocation.this.error("Sample location(s) updated");
					//	target.add(EditSampleLocation.this.get("feedback"));
						}
					catch (Exception e) { EditSampleLocation.this.error("Save unsuccessful. Please re-check values entered."); }
					}
				});
				
			add(new AjaxCancelLink("close", modal));
			}
	
		
	public String getUnit()
		{
		return unit;
		}

	public void setUnit(String u)
		{
		unit = u;
		}
		
	public String getLocation()
		{
		return location;
		}
	
	public void setLocation(String loc)
		{
		location = loc;
		}
	}

	protected abstract void onSave( String status, AjaxRequestTarget target);
	}

// EditSampleStatus

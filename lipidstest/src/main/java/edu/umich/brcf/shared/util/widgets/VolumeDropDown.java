package edu.umich.brcf.shared.util.widgets;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.TableAccessService;


public abstract class VolumeDropDown extends DropDownChoice
	{

	@SpringBean
	TableAccessService tableAccessService;
	
	List<String> volumeUnits = null;
	
	public VolumeDropDown(String id, Object object, final String propertyName)
		{
		super(id, new PropertyModel(object, propertyName));
				
		volumeUnits = getVolumeUnits();
		
		setChoices(new LoadableDetachableModel<List<String>>() 
			{
			@Override
			protected List<String> load() 
				{ 
				if (volumeUnits == null)
					volumeUnits = getVolumeUnits();
				
				return volumeUnits;
				}
			});
	
		add(buildUpdateBehavior("change", "updateForVolumeDrop"));			
		}
	
	
	private List <String> getVolumeUnits()
		{
		return tableAccessService.getVolumeUnits();
		}
		
	
	public boolean isEnabled()
		{
		return true;
		}
	
	
	protected boolean wantOnSelectionChangedNotifications() 
		{
		return true;
		}
		
	
	protected AjaxFormComponentUpdatingBehavior buildUpdateBehavior(String event, String eventTag)
		{
		return new AjaxFormComponentUpdatingBehavior(event)
			{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
				doUpdateBehavior(target);
				}	
			};
		}
		
	protected abstract void doUpdateBehavior(AjaxRequestTarget target);
	}

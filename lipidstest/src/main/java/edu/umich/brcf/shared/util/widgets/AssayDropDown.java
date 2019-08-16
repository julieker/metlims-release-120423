////////////////////////////////////////////////////////////////////
//AssayDropDown.java
//Written by Jan Wigginton May 2015
////////////////////////////////////////////////////////////////////

package edu.umich.brcf.shared.util.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.AssayService;

public abstract class AssayDropDown extends DropDownChoice
	{
	@SpringBean
	AssayService assayService;

	static List<String> availableAssays = null;

	public AssayDropDown(String id, Object object, final String propertyName,
			final String platform)
		{
		super(id, new PropertyModel(object, propertyName),
				availableAssays = new ArrayList<String>());
		availableAssays = getAssayList(platform);
		// Issue 249
		availableAssays.add(0, "Choose One");		
		setChoices(new LoadableDetachableModel<List<String>>()
			{
				@Override
				protected List<String> load()
					{
					if (availableAssays == null)
						availableAssays = getAssayList(platform);

					return availableAssays;
					}
			});
		add(buildUpdateBehavior("change", "updateForAssayDrop"));
		}

	private List<String> getAssayList(String platform)
		{
		return assayService.allAssayNames();
		}

	
	public boolean isEnabled()
		{
		return true;
		}


	protected AjaxFormComponentUpdatingBehavior buildUpdateBehavior(
			String event, String eventTag)
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

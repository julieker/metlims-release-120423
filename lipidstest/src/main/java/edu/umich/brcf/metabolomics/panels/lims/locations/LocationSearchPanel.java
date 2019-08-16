// LocationSearchPanel.java
// Rewritten from original by Jan Wigginton, May 2015
package edu.umich.brcf.metabolomics.panels.lims.locations;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.util.io.StringUtils;


public class LocationSearchPanel extends Panel
	{
	@SpringBean
	LocationService locService;
	LocationTablePanel ltp;

	public LocationSearchPanel(String id) 
		{
		super(id);
		add(new FeedbackPanel("feedback"));
		add(new LocationSearchForm("locationSearchForm"));
		}

	public final class LocationSearchForm extends Form 
		{
		String location = "shelf";
		public LocationSearchForm(String id)
			{
			super(id);
			final DropDownChoice <String> locUnitsDD = new DropDownChoice <String>("locUnitsDD",  new PropertyModel<String>(this, "location"),  locService.getDistinctUnits());
				
			locUnitsDD.add(new AjaxFormComponentUpdatingBehavior("change")
				{
				@Override
				protected void onUpdate(AjaxRequestTarget target)
					{
					if (!StringUtils.isEmptyOrNull(getLocation()))
						{
						try {
							updatePanels(); 
							target.add(ltp); 
							}
						catch(Exception e) 
							{ 
							updatePanels();
							LocationSearchPanel.this.error("Location Unit not found. Please try again......");
							}
						}
					}
				});

			add(locUnitsDD);
			
			ltp=new LocationTablePanel("locationTablePanel", new ArrayList<Location>());
			ltp.setOutputMarkupId(true);
			add(ltp);
			updatePanels();
			}
		
		public String getLocation()
			{
			return location;
			}
		
		public void setLocation(String loc)
			{
			this.location = loc;
			}
		
		private void updatePanels()
			{
			String unit = getLocation();
			ArrayList <Location> locations = (unit != null ? ((ArrayList <Location>) locService.getLocationsByUnit(unit)) :  new ArrayList <Location> ());
			ltp.setLocations(locations);
			ltp.setVisible(unit!=null);
			}
		}
	}

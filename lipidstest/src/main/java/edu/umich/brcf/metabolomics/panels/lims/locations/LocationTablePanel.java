// LocationTablePanel.java
// Written by Jan Wigginton May 2015

package edu.umich.brcf.metabolomics.panels.lims.locations;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.Location;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class LocationTablePanel extends Panel
	{
	public LocationTablePanel(String id,  List<Location> locations) 
		{
		super(id);
		setLocations(locations);
		
		add(new ListView("locations", new PropertyModel(this, "locations"))
			{
			public void populateItem(final ListItem listItem)
				{
				final Location loc = (Location)listItem.getModelObject();
				listItem.add(new Label("locId", new Model<String>(loc.getLocationId())));
				listItem.add(new Label("locDesc", new Model<String>(loc.getDescription())));
				listItem.add(new Label("room", new Model<String>(loc.getRoom())));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
		}	
	
	@Override
	public boolean isVisible()
		{
		return (locations != null && locations.size() > 0);
		}
	
	public List<Location> getLocations()
		{
		return locations;
		}
	
	public void setLocations(List<Location> locations)
		{
		this.locations=locations;
		}
	
	private List<Location> locations;
}

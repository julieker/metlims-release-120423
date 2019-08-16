///////////////////////////////////////
//SampleLocationsPage.java
//Written by Jan Wigginton May 2015
///////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.SampleLocation;
import edu.umich.brcf.shared.layers.service.LocationService;
import edu.umich.brcf.shared.layers.service.SampleLocationService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;

public class SampleLocationsPanel extends Panel
	{
	List<SampleLocation> sampleLocations;

	@SpringBean
	private SampleLocationService sampleLocationService;

	@SpringBean
	LocationService locationService;

	@SpringBean
	UserService userService;

	public SampleLocationsPanel(String id, String sampleId,
			final ModalWindow modal)
		{
		super(id);
		setOutputMarkupId(true);

		sampleLocations = sampleLocationService
				.getLocationHistoryForSample(sampleId);
		final Map<String, String> locationDescriptions = grabLocationDescriptions(sampleLocations);
		final Map<String, String> userNamesById = grabUserNamesForIds(sampleLocations);

		add(new Label("sampleId", sampleId));

		String oldSampleWarning = "Note : Location shown for samples registered prior to 06/20/15 is last recorded.";
		Label warningLabel = new Label("oldSampleWarning", new Model(
				oldSampleWarning))
			{
				public boolean isVisible()
					{
					return (sampleLocations.size() < 2);
					}
			};

		add(warningLabel);

		add(new ListView<SampleLocation>("sampleLocationsList",
				new PropertyModel(this, "sampleLocations"))
			{
				public void populateItem(final ListItem listItem)
					{
					SampleLocation loc = (SampleLocation) listItem
							.getModelObject();

					String oldLocId = loc.getOldLocationId();
					String oldLocationDescription = locationDescriptions
							.get(oldLocId);

					String newLocId = loc.getLocationId();
					String newLocationDescription = locationDescriptions
							.get(newLocId);

					String userName = userNamesById.get(loc.getUpdatedBy());

					listItem.add(new Label("oldLocationId", oldLocId));
					listItem.add(new Label("oldLocationDescription",
							new Model<String>(oldLocationDescription)));
					listItem.add(new Label("newLocationId", newLocId));
					listItem.add(new Label("newLocationDescription",
							new Model<String>(newLocationDescription)));
					listItem.add(new Label("updateDate", DateUtils
							.dateStrFromCalendar("MM/dd/yy",
									loc.getUpdateDate())));
					listItem.add(new Label("updatedBy", new Model<String>(
							userName)));

					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
			});

		add(new AjaxCancelLink("cancelButton", modal));
		}

	public Map<String, String> grabLocationDescriptions(
			List<SampleLocation> locs)
		{
		List<String> lst = new ArrayList<String>();
		for (int i = 0; i < locs.size(); i++)
			{
			lst.add(locs.get(i).getLocationId());
			lst.add(locs.get(i).getOldLocationId());
			}

		return locationService.getNamesForLocationIds(lst);
		}

	public Map<String, String> grabUserNamesForIds(List<SampleLocation> locs)
		{
		Map<String, String> lst = new HashMap<String, String>();
		for (int i = 0; i < locs.size(); i++)
			{
			String userName = "";
			String userId = locs.get(i).getUpdatedBy();
			if (userId == null || userId.trim().equals(""))
				{
				lst.put("", "");
				continue;
				}
			try
				{
				userName = userService.getUserNameByUserId(userId);
				} catch (Exception e)
				{
				userName = "";
				}

			lst.put(userId, userName);
			}

		return lst;
		}

	public List<SampleLocation> getSampleLocations()
		{
		return sampleLocations;
		}

	public void setSampleLocations(List<SampleLocation> locations)
		{
		sampleLocations = locations;
		}
	}

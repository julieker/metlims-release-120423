////////////////////////////////////////////////////
// SampleSelectorPage.java
// Written by Jan Wigginton, Apr 14, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;


import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.structures.SelectableObject;


public class SampleSelectorPage extends WebPage
	{
	public SampleSelectorPage(String id, final WebPage backPage, List<? extends ISampleItem> list, final String batchId, String title)
		{
		add(new SampleSelectorPanel("sampleList", backPage, list, batchId, title)
			{
			@Override
			public void doSubmit(AjaxRequestTarget target, List<SelectableObject> list)
				{
				String userId = ((MedWorksSession) getSession()).getCurrentUserId();
				
				List<String> sampleIds = new ArrayList<String>();
				for (SelectableObject obj : list)
					{
					if (obj.isSelected())
						{
						ISampleItem sample = ((ISampleItem) obj.getSelectionObject());
						if (sample != null)
							sampleIds.add(sample.getSampleId());
						}
					}
					
				//setResponsePage(new BatchDetailPage("batchDetail", backPage, batchId, sampleIds));
				}

	
			});
		}
	}

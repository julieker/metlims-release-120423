////////////////////////////////////////////////////
// BarcodeSelectorPage.java
// Written by Jan Wigginton, Jun 15, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.panels.utilitypanels.discard.PrintBarcodesPage2;
import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.structures.SelectableObject;



@SuppressWarnings("serial")
public class BarcodeSelectorPage extends WebPage
	{
	//@SpringBean
	//SampleCheckinBatchService sampleCheckinBatchService;

	public BarcodeSelectorPage(String id, final WebPage backPage, List<? extends ISampleItem> list, final String batchId, String title)
		{
		this(id, backPage, list, batchId, title, null);
		}
	
	public BarcodeSelectorPage(String id, final ModalWindow modal, List<? extends ISampleItem> list, 
			final String batchId, String title)
		{
		this(id, null, list, batchId, title, modal);
		}
	
	
	public BarcodeSelectorPage(String id, final WebPage backPage, List<? extends ISampleItem> list, final String batchId, String title, final ModalWindow modal)
		{
		SampleSelectorPanel panel;
		add(panel = new SampleSelectorPanel("sampleList", backPage, list, batchId, title, modal)
			{
			@Override
			public void doSubmit(AjaxRequestTarget target, List<SelectableObject> list)
				{
			//	String userId = ((MedWorksSession) getSession()).getCurrentUserId();
				System.out.println("ON SUBMITT...");
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
				System.out.println("Just before set reponse page...");
				setResponsePage(new PrintBarcodesPage(getPage(), sampleIds));
				}
			});
		
		panel.setButtonLabel("Select printer...");
		panel.setActionLabel("Select ids for printing");
		panel.setFilterLabel(null);
		}
	}

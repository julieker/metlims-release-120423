////////////////////////////////////////////////////
// OptimizedBarcodeSelectorPage.java
// Written by Jan Wigginton, Jun 23, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;

import edu.umich.brcf.shared.util.interfaces.ISampleItem;
import edu.umich.brcf.shared.util.structures.Pair;
import edu.umich.brcf.shared.util.structures.SelectableObject;


@SuppressWarnings("serial")
public class OptimizedBarcodeSelectorPage extends WebPage
	{
	private String header1Label, header2Label, selectedExperiment = null;
	private OptimizedBarcodeSelectorPanel panel;
	
	
	public OptimizedBarcodeSelectorPage(String id, final WebPage backPage, List<? extends ISampleItem> list, final String batchId, String title)
		{
		this(id, backPage, list, batchId, title, null);
		}
	
	public OptimizedBarcodeSelectorPage(String id, final ModalWindow modal, List<? extends ISampleItem> list, 
			final String batchId, String title)
		{
		this(id, null, list, batchId, title, modal);
		}
	
	public OptimizedBarcodeSelectorPage(String id, final WebPage backPage, List<? extends ISampleItem> list, final String batchId, String title, final ModalWindow modal)
		{
		add(panel = new OptimizedBarcodeSelectorPanel("sampleList", backPage, list, batchId, title, modal)
			{
			@Override
			public void doSubmit(AjaxRequestTarget target, List<SelectableObject> list)
				{
				List<String> sampleIds = new ArrayList<String>();
				for (SelectableObject obj : list)
					if (obj.isSelected())
						{
						Pair sample = ((Pair) obj.getSelectionObject());
						if (sample != null)
							sampleIds.add(sample.getId());
						}
				setResponsePage(new PrintBarcodesPage(getPage(), sampleIds, selectedExperiment ));
				}
			});
		
		panel.setButtonLabel("Select printer...");
		panel.setActionLabel("Select ids for printing");
		panel.setCriteriaLabel("Select");
		panel.setFilterLabel(null);
		
		panel.setHeader1Label(getHeader1Label());
		panel.setHeader2Label(getHeader2Label());
		}

	public String getHeader1Label()
		{
		return panel.getHeader1Label();
		}

	public String getHeader2Label()
		{
		return panel.getHeader2Label();
		}

	public void setHeader1Label(String header1Label)
		{
		panel.setHeader1Label(header1Label);
		}

	public void setHeader2Label(String header2Label)
		{
		panel.setHeader2Label(header2Label);
		}

	public String getSelectedExperiment()
		{
		return selectedExperiment;
		}

	public void setSelectedExperiment(String selectedExperiment)
		{
		this.selectedExperiment = selectedExperiment;
		}
	}

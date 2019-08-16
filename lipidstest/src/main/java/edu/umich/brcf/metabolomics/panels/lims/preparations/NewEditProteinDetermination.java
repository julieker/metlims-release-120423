package edu.umich.brcf.metabolomics.panels.lims.preparations;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;
import edu.umich.brcf.metabolomics.layers.dto.ProteinDeterminationDTO;
import edu.umich.brcf.metabolomics.panels.lims.prep.SelectablePlateMapPanel;
import edu.umich.brcf.shared.layers.service.SamplePrepService;




public abstract class NewEditProteinDetermination extends WebPage
	{
	@SpringBean
	SamplePrepService samplePrepService;
	
	public NewEditProteinDetermination(Page backPage, SelectablePlateMapPanel pMap, ProteinDeterminationDTO pdto)
		{
		add(new FeedbackPanel("feedback"));
		add(new NewProteinDeterminationForm("pdForm", pMap, pdto));
		}

	public final class NewProteinDeterminationForm extends Form 
		{
		public NewProteinDeterminationForm(final String id, final SelectablePlateMapPanel pMap, ProteinDeterminationDTO pdto)
			{
			super(id); 
			TabbedPanel tabbedPanel; 
			
			final List tabs=new ArrayList();
			
			tabs.add(new AbstractTab(new Model("Plate Map")) 
				{
		 		public Panel getPanel(String panelId)
		 			{
		 			return new PDPlatePanel(panelId, pMap.getPreparation());
		 			}
		 		});  
			
			tabs.add(new AbstractTab(new Model("Notes")) 
				{
	 		   	public Panel getPanel(String panelId)
	 		   		{
	 			   	return (new PDNotesPanel(panelId));
	 		   		}
		 		});
			
			tabs.add(new AbstractTab(new Model("Files")) 
				{
		 		public Panel getPanel(String panelId)
		 		   	{
		 			return (new PDNotesPanel(panelId));
		 		   	}
				});
			
			 add(tabbedPanel=new TabbedPanel("tabs", tabs));
		     tabbedPanel.setSelectedTab(0);
		     tabbedPanel.setOutputMarkupId(true);
		    
		     //plateMap.setPlateCols(12);
			//plateMap.setPlateRows(9);
			//add(plateMap);
			//add(new RequiredTextField("sampleVolume"));
			//add(new RequiredTextField("bradfordAgent"));
			//add(new RequiredTextField("wavelength"));
			//add(new RequiredTextField("incubationTime"));
			//add(plateMap);
			
			add(new Button("save")
				{
				@Override
				public void onSubmit() 
					{
					//ProtienDeterminationDTO prepDto = (ProtienDeterminationDTO) getForm().getModelObject();
					try
						{
						//ProtienDeterminationSOP sop=samplePrepService.saveProtienDetermination(prepDto);
						//NewEditProteinDetermination.this.info("Save Successful!");
						//NewEditProteinDetermination.this.onSave(sop);
						}
					catch (Exception e){ NewEditProteinDetermination.this.error("Save unsuccessful. Please re-check values entered!"); }
				}
			});
		}
	}
	protected abstract void onSave(ProtienDeterminationSOP sop);
}


////////////////////////////////////////////////////
// MetabBatchSampleEdit.java
// Written by Jan Wigginton, Jun 19, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.database_utility;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.FormFieldBuilder;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;



public abstract class Mrc2BatchSampleEdit extends WebPage
	{
	@SpringBean
	SampleService sampleService;
	
	private String selectedExperiment; 
	private FeedbackPanel feedbackPanel;
	
	
	public Mrc2BatchSampleEdit(String id, String selectedExperiment, ModalWindow modal)
		{
		this.selectedExperiment = selectedExperiment;	

		add(feedbackPanel = new FeedbackPanel("feedback"));
		feedbackPanel.setOutputMarkupId(true);
		
		add(new BatchEditForm("batchSampleEditForm" , true, modal));
		}

	
	public final class BatchEditForm extends Form 
		{
		private List<SampleDTO> sampleDetailItems;
	
		private FormFieldBuilder fieldBuilder;
		
		private List<String> fieldTags = Arrays.asList(new String [] {"sampleId",  "researcherSampleId",
				"subjectId", "researcherSubjectId",  "volume", "curVolume",  "volUnits",
				"locID", "userDefGOS","genusOrSpeciesID", "userDefSampleType", "sampleTypeId" 
				});				
			
		
		public BatchEditForm(final String id, boolean ifNew, ModalWindow modal) 
			{
			super(id);
			final WebMarkupContainer container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);
			add(container);
			
			sampleDetailItems = sampleService.loadDTOsForExpId(selectedExperiment);
			fieldBuilder = new FormFieldBuilder(sampleDetailItems, fieldTags);
		
			container.add(buildDetailItemsListView("sampleItems"));
			container.add(new AjaxCancelLink("cancelButton", modal));
			container.add(buildSubmitLink("submitButton"));	
			}
		
	
		public List<SampleDTO> getSampleDetailItems()
			{
			return sampleDetailItems;
			}

		
		public void setSampleDetailItems(List<SampleDTO> items)
			{
			sampleDetailItems = items;
			}

		
		private ListView buildDetailItemsListView(String id)
			{
			ListView listView = new ListView(id, new PropertyModel<List<SampleDTO>>(this, "sampleDetailItems"))
				{
				@Override
				protected void populateItem(ListItem item) 
					{
					SampleDTO dto = (SampleDTO) item.getModelObject();
					
					item.add(fieldBuilder.buildTabbedStringField("sampleId", dto, "sampleId").setEnabled(false));
					item.add(fieldBuilder.buildTabbedStringField("researcherSampleId", dto, "researcherSampleId", "Researcher Sample Id").setRequired(true));
					item.add(fieldBuilder.buildTabbedStringField("subjectId", dto, "subjectId").setEnabled(false));
					item.add(fieldBuilder.buildTabbedStringField("researcherSubjectId", dto, "researcherSubjectId").setEnabled(false));
					
					item.add(fieldBuilder.buildTabbedBigDecimalField("volume", dto, "volume", "Volume"));
					item.add(fieldBuilder.buildTabbedBigDecimalField("currVolume", dto, "currVolume", "Curr. Volume"));
					item.add(fieldBuilder.buildTabbedStringField("volUnits", dto, "volUnits"));
					item.add(fieldBuilder.buildTabbedStringField("locID", dto, "locID"));
					item.add(fieldBuilder.buildTabbedStringField("userDefGOS", dto, "userDefGOS"));
					item.add(fieldBuilder.buildTabbedLongField("genusOrSpeciesID", dto, "genusOrSpeciesID"));
					item.add(fieldBuilder.buildTabbedStringField("userDefSampleType", dto, "userDefSampleType"));
					item.add(fieldBuilder.buildTabbedStringField("sampleTypeId", dto, "sampleTypeId"));
					
					item.add(OddEvenAttributeModifier.create(item));
					}
				};
			
			return listView;
			}
		
		
		public AjaxSubmitLink buildSubmitLink(String id)
			{
			return new AjaxSubmitLink(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						int nSaved = sampleService.updateExistingSamples(sampleDetailItems);
						Mrc2BatchSampleEdit.this.onSave(selectedExperiment, target);
					//	Mrc2BatchSampleEdit.this.error("Successfully saved " + nSaved + " items");
						}
					catch (Exception e)
						{
						e.printStackTrace();
						if (e.getMessage() != null && e.getMessage().startsWith("Duplicate"))
							Mrc2BatchSampleEdit.this.error(e.getMessage());
						else
							Mrc2BatchSampleEdit.this.error("Save unsuccessful. Please re-check values entered.");
						}
					
					target.add(Mrc2BatchSampleEdit.this.get("feedback")); 
					}
				
				@Override
				protected void onError(AjaxRequestTarget target) // issue 464
					{
					target.add(Mrc2BatchSampleEdit.this.get("feedback"));
					}
				};
			}
		}
	
	
	protected abstract void onSave(String selectedExperiment, AjaxRequestTarget target);
	}



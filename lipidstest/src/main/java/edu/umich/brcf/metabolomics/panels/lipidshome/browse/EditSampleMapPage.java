//////////////////////////////////////
// EditSampleMapPage.java
// Written by Jan Wigginton 05/02/15
//////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.domain.Ms2Peak;
import edu.umich.brcf.metabolomics.layers.domain.Ms2SampleMap;
import edu.umich.brcf.metabolomics.layers.service.Ms2DataSetService;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakService;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;
import edu.umich.brcf.metabolomics.layers.service.Ms2SampleMapService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;



public class EditSampleMapPage extends WebPage
	{
	@SpringBean
	Ms2DataSetService ms2DataSetService;
	
	@SpringBean 
	Ms2PeakSetService ms2PeakSetService;
	
	@SpringBean 
	Ms2PeakService ms2PeakService;
	
	@SpringBean 
	SampleService sampleService;
	
	@SpringBean
	Ms2SampleMapService ms2SampleMapService;
	
	
	
	public EditSampleMapPage(String id, WebPage backPage, String dataSetId)
		{
		super();
		
		add(new FeedbackPanel("feedback"));
		
		EditSampleMapForm esmf = new EditSampleMapForm("editSampleMapForm", dataSetId);
		esmf.setMultiPart(true);
		add(esmf);
		}

	
	public final class EditSampleMapForm extends Form 
		{
		private WebMarkupContainer container;
		protected ListView mappingListView;
		List <Ms2SampleMap> sampleMappings = new ArrayList <Ms2SampleMap>();
		String pageTitle;

		List <String> possibleSampleIds;
		DropDownChoice <String>possibleSampleIdsDrop;
		

		public EditSampleMapForm(String id,  String dataSetId)
			{
			super(id);
			
			Ms2DataSet dataSet = ms2DataSetService.loadById(dataSetId);
			String expId = dataSet.getExpId();
			possibleSampleIds = sampleService.sampleIdsForExpId(expId);
			
			sampleMappings = buildSampleMapping(dataSetId);
			
			pageTitle =  "Edit Sample Information for MS2 Data Set " + dataSetId;
			add(new Label("pageTitle", new PropertyModel <String>(this, "pageTitle")));
			
			container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);	
			add(container);
	
			container.add(mappingListView = buildListView("mappingListView"));
			mappingListView.setOutputMarkupId(true);
			
			container.add(buildSaveButton("saveButton"));
			container.add(buildCancelButton("cancelButton"));
			}
		
		
		List <Ms2SampleMap> buildSampleMapping(String dataSetId)
			{
			Ms2DataSet dataSet = ms2DataSetService.loadById(dataSetId);	
			dataSet.setPeakSets(ms2PeakSetService.loadInitializedForDataSetId(dataSetId));

			String peakSetId = dataSet.getPeakSets().get(0).getPeakSetId();
			List <Ms2Peak> peaks = ms2PeakService.loadForPeakSetId(peakSetId);
			
			sampleMappings = new ArrayList<Ms2SampleMap>();
			for (int i = 0; i < peaks.size(); i++)
				{
				Ms2Peak pk = peaks.get(i);
				String sampleMapId = pk.getSampleMapId();
				Ms2SampleMap map = ms2SampleMapService.loadById(sampleMapId);
				sampleMappings.add(map);
				}
			return sampleMappings;	
			}
		
		
		public ListView buildListView(String id)
			{
			return new ListView(id, new PropertyModel(this, "sampleMappings"))
				{
				public void populateItem(ListItem listItem) 
					{
					final Ms2SampleMap item =  (Ms2SampleMap) listItem.getModelObject();
					
					listItem.add(buildSampleIdDropdown("sampleIdDropdown", item));
					listItem.add(new Label("sampleTag", new PropertyModel<String>(item, "sampleTag")));
					listItem.add(new TextField("injectionComment", new PropertyModel<String>(item, "injectionComment")));
					listItem.add(OddEvenAttributeModifier.create(listItem));
					
					}
				};
			}
		
		
		private DropDownChoice buildSampleIdDropdown(final String id, final Ms2SampleMap map)
			{
			return new DropDownChoice(id,  new PropertyModel(map, "sampleId"), 
				possibleSampleIds);
				
			}
		
		
		private AjaxSubmitLink buildSaveButton(String id)
			{
			return new AjaxSubmitLink (id, this)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) 
					{
					
					//ClientDTO clientDto = (ClientDTO) form.getModelObject();
					try
						{
					/*	if(!FormatVerifier.verifyFormat(Organization.idFormat, clientDto.getOrganizationID()))
							clientDto.setOrganizationID(StringParser.parseId(clientDto.getOrganizationID()));
						if(!FormatVerifier.verifyFormat(User.fullIdFormat, clientDto.getContact()))
							clientDto.setContact(StringParser.parseId(clientDto.getContact()));
						if(!FormatVerifier.verifyFormat(User.fullIdFormat, clientDto.getInvestigatorID()))
							clientDto.setInvestigatorID(StringParser.parseId(clientDto.getInvestigatorID()));
						Client client = clientService.save(clientDto);
						EditClient.this.info("Client "+client.getClientID()+" saved successfully.");
						target.add(EditClient.this.get("feedback"));
						EditClient.this.onSave(client, target);  */
						}
					catch (Exception e)
						{
						//e.printStackTrace();
						//EditClient.this.error("Save unsuccessful! Please make sure that Organization, Contact and Investigator exist in the database.");
						//target.add(EditClient.this.get("feedback"));
						}
					}
				};
			}	
		
		private AjaxSubmitLink buildCancelButton(String id)
			{
			return new AjaxSubmitLink(id, this) 
				{
		        public void onSubmit(AjaxRequestTarget target, Form form) 
		        	{
		    		
		        	}
			 	};
			}
		}
	}	


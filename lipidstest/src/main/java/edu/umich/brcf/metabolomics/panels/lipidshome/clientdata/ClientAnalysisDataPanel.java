// ClientAnalysisDataPanel.java
// Written by Jan Wigginton April 2015, based on AnalysisDataPanel.java

package edu.umich.brcf.metabolomics.panels.lipidshome.clientdata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2DataSet;
import edu.umich.brcf.metabolomics.layers.service.Ms2DataSetService;
import edu.umich.brcf.metabolomics.panels.lipidshome.browse.Ms2DataSetPage;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;



public class ClientAnalysisDataPanel extends Panel
	{
	@SpringBean
	Ms2DataSetService ms2DataSetService;
	
	@SpringBean 
	ExperimentService experimentService;
	
	@SpringBean
	ClientService clientService;
	
	Client client;
	
	public ClientAnalysisDataPanel(String id, final boolean testOnly)
		{
		super(id);
		
		//add(new FeedbackPanel("feedback"));
		
		String clientId = "CL0004";
		client = clientService.loadById(clientId);
		
		add(new ClientHeaderPanel("header", client)
			{
			@Override
			public boolean isVisible()
				{
				return !testOnly;
				}
			});
		
		ClientReportForm luf = new ClientReportForm("clientReportsForm");
		luf.setMultiPart(true);
		add(luf);
		}
	
		
	public final class ClientReportForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;
		List <ClientDataSetInfo> dataSetInfo = new ArrayList <ClientDataSetInfo>();
		
		String clientName = client.getContactNameForTable();
		Boolean ifEdit;

		public ClientReportForm(String id)
			{
			super(id);
			
			this.ifEdit = ifEdit;
			
			add(container = new WebMarkupContainer("container"));
			container.setOutputMarkupId(true);	
			
			dataSetInfo = grabDataSetInfo();
			
			container.add(dataSetView = buildListView("dataSetListView"));
			dataSetView.setOutputMarkupId(true);
			}
		
		
		List <ClientDataSetInfo> grabDataSetInfo()
			{
			List<Ms2DataSet> clientDataSets =  ms2DataSetService.loadAll();
			
			List <ClientDataSetInfo> dataSetInfo = new ArrayList <ClientDataSetInfo>();
			
			for (int i = 0; i < clientDataSets.size(); i++)
				{
				dataSetInfo.add(new ClientDataSetInfo(clientDataSets.get(i)));
				}
			
			return dataSetInfo;
			}
		
		
		public PageableListView buildListView(String id)
			{
			return new PageableListView(id, new PropertyModel(this, "dataSetInfo"), 600)
				{	
				public void populateItem(ListItem listItem) 
					{
					final ClientDataSetInfo item =  (ClientDataSetInfo) listItem.getModelObject();
					
				//	listItem.add(new Label("dataSetId", new PropertyModel(item, "dataSetId")));
				//	listItem.add(new Label("expLabel", new PropertyModel(item, "expLabel")) );
					listItem.add(new Label("nCompounds", new PropertyModel <Integer>(item, "nCompounds")));
					listItem.add(new Label("ionMode", new PropertyModel<String>(item, "ionMode")));
					listItem.add(new Label("expLabel", new PropertyModel<String>(item, "expLabel")));
					listItem.add(new Label("dataNotation", new PropertyModel<String>(item, "dataNotation")));
					listItem.add(new Label("runDate", new PropertyModel<Calendar>(item, "runDate")));
					listItem.add(new Label("uploadDate", new PropertyModel<Calendar>(item, "uploadDate")));
					listItem.add(new Label("nSamples", new PropertyModel<String>(item, "nSamples")));
					listItem.add(new Label("nControls", new PropertyModel<String>(item, "nControls")));
					
					listItem.add(buildLinkToData("dataSetLink", item.getDataSetId()));
						
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
		
		private IndicatingAjaxButton buildLinkToData(String id, final String dataSetId)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onComponentTag(ComponentTag tag)
		    		{
		    		super.onComponentTag(tag);
		    		String displayTitle = "  View Data...  ";
		    		tag.put("value", displayTitle);
		    		}
	
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
					{
					setResponsePage(new Ms2DataSetPage("dataResults", (WebPage) this.getPage(), dataSetId));
					}
				};
			}
		
		
		public List<ClientDataSetInfo> getDataSets()
			{
			return this.dataSetInfo;
			}
	
		public void setDataSetInfo(List<ClientDataSetInfo> dataInfo)
			{
			this.dataSetInfo = dataInfo;
			}
		
		public String getSearchTitle()
			{
			return clientName;
			}
		}
	}	








	




// DataReportPanel.java
// Written by Jan Wigginton 05/09/15

package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.util.HashMap;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Ms2PeakSet;
import edu.umich.brcf.metabolomics.layers.service.Ms2PeakSetService;


public class DataReportPanel extends Panel
	{
	@SpringBean
	Ms2PeakSetService ms2PeakSetService;
	
	DataReportPanel(String id)
		{
		super(id);
		}
	
	public DataReportPanel(String id, WebPage backPage, String dataSetId, List <Ms2PeakSet> selectedPeaks)
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		DataReportForm luf = new DataReportForm("dataReportForm", backPage, dataSetId, selectedPeaks);
		luf.setMultiPart(true);
		
		add(luf);
		}
		
	
	public final class DataReportForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataView;
		
		List <Ms2PeakSet> dataList;
		HashMap<String, LipidBlastEntry> lipidDetails;  
		String panelTitle;

		public DataReportForm(String id, WebPage backPage, String dataSetId, List <Ms2PeakSet> selectedPeaks)
			{
			super(id);
			
			dataList = selectedPeaks;
			lipidDetails = grabLipidDetails(dataList);
			
			panelTitle =  "Lipid Peak Areas : " + dataSetId;
			add(new Label("panelTitle", new PropertyModel <String>(this, "panelTitle")));
			
			container = new WebMarkupContainer("container");
			add(container);
			container.setOutputMarkupId(true);	
			}
		

		private HashMap<String, LipidBlastEntry> grabLipidDetails(List <Ms2PeakSet> peaksOfInterest)
			{
			return new HashMap<String, LipidBlastEntry>();
			}
		
		private PageableListView buildListView(String id)
			{
			dataView = new PageableListView(id, new PropertyModel(this, "dataList"), 500)
				{
				@Override
				protected void populateItem(ListItem listItem)
					{
					final Ms2PeakSet item =  (Ms2PeakSet) listItem.getModelObject();
					}
				};
				
			return dataView;
			}
		
		
		public String getPanelTitle()
			{
			return panelTitle;
			}
		
		public List <Ms2PeakSet> getDataList()
			{
			return dataList;
			}
		}
	}

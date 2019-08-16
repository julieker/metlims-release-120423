////////////////////////////////////////////////////
// ExperimentCompletionReport.java
// Written by Jan Wigginton, Nov 12, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.admin.sample_submission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ClientReportService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.datacollectors.ExperimentInventoryInfo;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.MyReportLink;


public class ExperimentCompletionReport extends WebPage
	{
	@SpringBean
	ClientReportService clientReportService;
	
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	UserService userService;
	
	
	public ExperimentCompletionReport(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar)
		{
		add(new FeedbackPanel("feedback"));
		
		SubmittedReportsForm luf = new SubmittedReportsForm("reportsForm", backPage, fromDateCalendar, toDateCalendar);
		
		luf.setMultiPart(true);
		add(luf);
		}
	
	public final class SubmittedReportsForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;

		List <ExperimentInventoryInfo> completionList = null; //new ArrayList <ExperimentInventoryInfo>();
		
		String searchTitle, expId;
		Boolean ifEdit;
		ModalWindow modal1;
		
		
		public SubmittedReportsForm(String id,  WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar)
			{
			super(id);
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			add(modal1);

			completionList = experimentService.completedExperimentsInRange(fromDateCalendar, toDateCalendar);
			searchTitle = "Experiments with data analysis completed  : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			
		//	reportLists = grabExampleDataSets(expId, fromDateCalendar, toDateCalendar, useRunDate);
			
			add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			add(new AjaxBackButton("backButton", backPage));
	
			add(dataSetView = buildListView("reportLists"));
		//	dataSetView.setOutputMarkupId(true);
			}
		

		
		public void setCompletionList(List<ExperimentInventoryInfo> completionList)
			{
			this.completionList = completionList;
			}


		List <ClientReportDTO> grabExampleDataSets(String expId, Calendar fromDate, Calendar toDate, Boolean useRunDate)
			{
			List <ClientReportDTO> lst = new ArrayList<ClientReportDTO> ();
			
			if (fromDate != null)
				return clientReportService.loadInfoForUploadDateRange(fromDate, toDate);
			
			return clientReportService.loadInfoForUploadDateRange(fromDate, toDate);
			}
		
		
		public PageableListView buildListView(String id)
			{
			return new PageableListView(id, new PropertyModel(this, "completionList"), 600)
				{	
				public void populateItem(ListItem listItem) 
					{
					final ExperimentInventoryInfo item =  (ExperimentInventoryInfo) listItem.getModelObject();
					
					listItem.add(new Label("expId", new PropertyModel<String>(item, "expId")));
					listItem.add(new Label("completionDate", new PropertyModel<String>(item, "completionDateAsStr")));
					listItem.add(new Label("sampleCount", new PropertyModel<String>(item, "sampleCount")));
					listItem.add(new Label("samplesDescriptor", new PropertyModel<String>(item, "samplesDescriptor")));
					listItem.add(new Label("clientId", new PropertyModel<String>(item, "clientId")));
					listItem.add(new Label("piName", new PropertyModel<String>(item, "piName")));
					listItem.add(new Label("piPhone", new PropertyModel<String>(item, "piPhone")));
					
					listItem.add(new Label("contactName", new PropertyModel<String>(item, "contactName")));
					listItem.add(new Label("contactPhone", new PropertyModel<String>(item, "contactPhone")));
					
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
		
		private Link buildLinkToReport(String id, final String reportId)
			{
			ClientReport report = clientReportService.loadByReportId(reportId);
			Link link = new MyReportLink("reportLink", new Model(report)); 
			
			return link;
			}
		
				
		private IndicatingAjaxButton buildBackButton(String id, final WebPage backPage)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget arg0)  { setResponsePage(backPage);  } // issue 464
				};
			}
	
		
		public List<ExperimentInventoryInfo> getCompletionList()
			{
			return completionList;
			}
	
		
		public void setReportLists(List<ExperimentInventoryInfo> reportLists)
			{
			this.completionList = reportLists;
			}
		
		
		public WebMarkupContainer getContainer()
			{
			return container;
			}
	
		
		public void setDataSetView(PageableListView view)
			{
			dataSetView = view;
			}
		
		
		public PageableListView getDataSetView()
			{
			return dataSetView;
			}
		
		
		public String getSearchTitle()
			{
			return searchTitle;
			}
		}
	}	








	



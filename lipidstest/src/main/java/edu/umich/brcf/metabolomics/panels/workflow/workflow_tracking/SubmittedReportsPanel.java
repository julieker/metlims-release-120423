////////////////////////////////////////////////////
//SubmittedReportsPanel.java
//Written by Jan Wigginton September 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.workflow.workflow_tracking;


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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ClientReportService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.MyReportLink;



public class SubmittedReportsPanel extends Panel
	{
	@SpringBean
	ClientReportService clientReportService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	UserService userService;
	
	
	public SubmittedReportsPanel(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		SubmittedReportsForm luf = new SubmittedReportsForm("reportsForm", backPage, null, fromDateCalendar, toDateCalendar, ifEdit, useRunDate);
		luf.setMultiPart(true);
		add(luf);
		}
	
	
	public SubmittedReportsPanel(String id, WebPage backPage, String expId, Boolean ifEdit, Boolean useRunDate)
		{
		super(id);
		
		add(new FeedbackPanel("feedback"));
		
		SubmittedReportsForm luf = new SubmittedReportsForm( "reportsForm", backPage, expId, null, null, ifEdit, useRunDate);
		luf.setMultiPart(true);
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	public final class SubmittedReportsForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;

		// We use DTO to avoid memory load associated with loading the ClientReport which contains a large byte array of file content
		List <ClientReportDTO> reportLists = new ArrayList <ClientReportDTO>();
		
		String searchTitle, expId;
		Boolean ifEdit;
		ModalWindow modal1;
		
		
		public SubmittedReportsForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			super(id);
			
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			add(modal1);

			searchTitle = "Client Reports Uploaded : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			
			reportLists = grabExampleDataSets(expId, fromDateCalendar, toDateCalendar, useRunDate);
			
			add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			add(new AjaxBackButton("backButton", backPage));
	
			add(dataSetView = buildListView("reportLists"));
			dataSetView.setOutputMarkupId(true);
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
			return new PageableListView(id, new PropertyModel(this, "reportLists"), 600)
				{	
				public void populateItem(ListItem listItem) 
					{
					final ClientReportDTO item =  (ClientReportDTO) listItem.getModelObject();
					
					String assayId = StringUtils.isNonEmpty(item.getAssayId()) ? item.getAssayId() : "";

					String assayName = "";
					if (StringUtils.isNonEmpty(item.getAssayId()))
							assayName = assayService.getNameForAssayId(item.getAssayId());
					
					String uploaderName = userService.getFullNameByUserId(item.getLoadedBy());
					
					listItem.add(new Label("dateCreated", DateUtils.dateStrFromCalendar("MM/dd/yyyy", item.getDateCreated())));
					listItem.add(new Label("fileName", item.getFileName()));
					listItem.add(new Label("expId", item.getExpId()));
					listItem.add(new Label("assayId", assayName));
					listItem.add(new Label("uploadedBy", uploaderName));
					
					listItem.add(buildLinkToReport("reportLink", item.getReportId()));
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
				@Override // issue 464
				protected void onSubmit(AjaxRequestTarget arg0)   { setResponsePage(backPage);  }
				};
			}
	
		
		public List<ClientReportDTO> getReportLists()
			{
			return reportLists;
			}
	
		
		public void setReportLists(List<ClientReportDTO> reportLists)
			{
			this.reportLists = reportLists;
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








	




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

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.panels.lims.compounds.EditAliquot;
import edu.umich.brcf.metabolomics.panels.lims.compounds.InventoryDetailPanel;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.ExperimentDetail;
import edu.umich.brcf.shared.layers.domain.Aliquot;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.layers.dto.ProtocolReportDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ClientReportService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProtocolReportService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.MyProtocolLink;
import edu.umich.brcf.shared.util.widgets.MyReportLink;



public class SubmittedReportsPanel extends Panel
	{
	@SpringBean
	ClientReportService clientReportService;
	
	@SpringBean
	ProtocolReportService protocolReportService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	ExperimentService experimentService;
	
	WebMarkupContainer protocolContainer;
	WebMarkupContainer clientContainer;
	
	// issue 176
	public SubmittedReportsPanel(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
		{
		this ( id,  backPage,  fromDateCalendar,  toDateCalendar,  ifEdit,  useRunDate, true);
		}
	
	public SubmittedReportsPanel(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate, boolean useClientReport)
		{
		super(id);
		add(new FeedbackPanel("feedback"));		
		SubmittedReportsForm luf = new SubmittedReportsForm("reportsForm", backPage, null, fromDateCalendar, toDateCalendar, ifEdit, useRunDate, useClientReport);
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
		List <ProtocolReportDTO> reportListsProtocol = new ArrayList <ProtocolReportDTO>(); // issue 176
		List <Object[]> reportListsMissingProtocol = new ArrayList <Object[]> ();
		
		String searchTitle,searchProtocolTitle, expId, missingProtocolResultTitle;
		Boolean ifEdit;
		ModalWindow modal1;
		
		public SubmittedReportsForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			this(id, backPage, expId,  fromDateCalendar,  toDateCalendar, ifEdit,  useRunDate, true);
			}
		public SubmittedReportsForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate, boolean useClientReport)
			{
			super(id);
			Calendar tmpToDate  = Calendar.getInstance();
			tmpToDate.setTime(toDateCalendar.getTime());
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			add(modal1);
			//searchTitle = (useClientReport ? "Client Reports Uploaded : " : "Protocol Reports Uploaded : " )+ DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
			//	+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			searchTitle = "Client Reports Uploaded : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			searchProtocolTitle = "Protocol Reports Uploaded: " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
			+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			missingProtocolResultTitle = "Experiments with Missing Protocols with the following create date range: " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) + "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			reportLists = grabExampleDataSets(expId, fromDateCalendar, toDateCalendar, useRunDate);
			reportListsProtocol = grabExampleDataSetsProtocol(expId, fromDateCalendar, toDateCalendar, useRunDate, useClientReport);
			toDateCalendar = tmpToDate;
			reportListsMissingProtocol= protocolReportService.getMissingProtocols(fromDateCalendar, toDateCalendar); // issue 176			
			// issue 176
			protocolContainer = new WebMarkupContainer("protocolContainer")
				{
				public boolean isVisible()
					{
					return !useClientReport;
					}
			     };
		    add(protocolContainer);	
		    
			clientContainer = new WebMarkupContainer("clientContainer")
				{
				public boolean isVisible()
					{
					return useClientReport;
					}
			     };
		     add(clientContainer);
	    
			clientContainer.add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			protocolContainer.add(new Label("searchResultProtocolTitle", new PropertyModel <String>(this, "searchProtocolTitle")));
			protocolContainer.add(new Label("missingProtocolResultTitle", new PropertyModel <String>(this, "missingProtocolResultTitle")));
			clientContainer.add(new AjaxBackButton("backButton", backPage));
			protocolContainer.add(new AjaxBackButton("backButtonProtocol", backPage));
	
			clientContainer.add(dataSetView = buildListView("reportLists",useClientReport));
			protocolContainer.add(dataSetView = buildListViewProtocol("reportListsProtocol", useClientReport));// issue 173
			protocolContainer.add(dataSetView = buildListViewMissingProtols("reportListsMissingProtocol", useClientReport));// issue 173
			dataSetView.setOutputMarkupId(true);
			}
		
		// issue 176
		List <ClientReportDTO> grabExampleDataSets(String expId, Calendar fromDate, Calendar toDate, Boolean useRunDate)
			{
			return grabExampleDataSets(expId, fromDate, toDate,  useRunDate,  true);
			}
		
		// issue 176
		List <ClientReportDTO> grabExampleDataSets(String expId, Calendar fromDate, Calendar toDate, Boolean useRunDate, boolean useClientReport)
			{
			List <ClientReportDTO> lst = new ArrayList<ClientReportDTO> ();
			if (fromDate != null)
				return clientReportService.loadInfoForUploadDateRange(fromDate, toDate);
			
			return clientReportService.loadInfoForUploadDateRange(fromDate, toDate);
			}
		
		// issue 176
		List <ProtocolReportDTO> grabExampleDataSetsProtocol(String expId, Calendar fromDate, Calendar toDate, Boolean useRunDate, boolean useClientReport)
			{
			List <ProtocolReportDTO> lst = new ArrayList<ProtocolReportDTO> ();
			
			if (fromDate != null)
				return protocolReportService.loadInfoForUploadDateRange(fromDate, toDate);
			
			return protocolReportService.loadInfoForUploadDateRange(fromDate, toDate);
			}
		
		
		public PageableListView buildListView(String id, boolean useClientReport)
			{
			return new PageableListView(id, new PropertyModel(this, "reportLists"), 600)
				{
				@Override
				public boolean isVisible ()
					{
					return useClientReport;
					}
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
		
		// issue 176
		public PageableListView buildListViewMissingProtols(String id, boolean useClientReport)
			{
			return new PageableListView(id, new PropertyModel(this, "reportListsMissingProtocol"), 600)
				{
				@Override
				public boolean isVisible ()
					{
					return !useClientReport;
					}
				public void populateItem(ListItem listItem) 
					{
					final Object[] item =  (Object[]) listItem.getModelObject();
					listItem.add(new Label("expId", item[0].toString()));
					listItem.add(new Label("expName", item[1].toString()));
					listItem.add(new Label("dateCreated", item[2].toString()));
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
		
		
		
		// issue 173
		public PageableListView buildListViewProtocol(String id, boolean useClientReport)
			{
			return new PageableListView(id, new PropertyModel(this, "reportListsProtocol"), 600)
				{	
				@Override
				public boolean isVisible ()
					{
					return !useClientReport;
					}
				
				public void populateItem(ListItem listItem) 
					{
					final ProtocolReportDTO item =  (ProtocolReportDTO) listItem.getModelObject();
					
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
					
					// issue 176
					listItem.add(buildLinkToReportProtocol("reportLinkProtocol", item.getReportId()));
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
		
		// issue 176
		private Link buildLinkToReportProtocol(String id, final String reportId)
			{
			ProtocolReport report = protocolReportService.loadByReportId(reportId);
			Link link = new MyProtocolLink("reportLinkProtocol", new Model(report)); 			
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
		
		// issue 176
		public List<ProtocolReportDTO> getReportListsProtocol()
			{
			return reportListsProtocol;
			}
		
		// issue 176
		public List<Object[]> getReportListsMissingProtocol()
			{
			return reportListsMissingProtocol;
			}
	
		
		public void setReportLists(List<ClientReportDTO> reportLists)
			{
			this.reportLists = reportLists;
			}
		
		// issue 176
		public void setReportListsProtocol(List<ProtocolReportDTO> reportLists)
			{
			this.reportListsProtocol = reportListsProtocol;
			}
		
		// issue 176
		public void setReportListsMissingProtocol(List<Object[]> reportListsMissingProtocol)
			{
			this.reportListsMissingProtocol = reportListsMissingProtocol;
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
		
		public String getSearchProtocolTitle()
			{
			return searchProtocolTitle;
			}
		
		public String getMissingProtocolResultTitle()
			{
			return missingProtocolResultTitle;
			}
		
		// issue 167

		}
	}	








	




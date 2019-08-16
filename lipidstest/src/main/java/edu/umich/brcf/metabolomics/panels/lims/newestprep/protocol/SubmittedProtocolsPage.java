////////////////////////////////////////////////////
// SubmittedProtocolsPage.java
// Written by Jan Wigginton, Dec 3, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol;


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

import edu.umich.brcf.shared.layers.domain.StandardProtocol;
import edu.umich.brcf.shared.layers.dto.ClientReportDTO;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;
import edu.umich.brcf.shared.util.widgets.MyStandardProtocolLink;



public class SubmittedProtocolsPage extends WebPage
	{
	@SpringBean
	StandardProtocolService standardProtocolService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	UserService userService;
	
	
	public SubmittedProtocolsPage(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
		{
		add(new FeedbackPanel("feedback"));
	
		SubmittedProtocolsForm luf = new SubmittedProtocolsForm("reportsForm", backPage, null, fromDateCalendar, toDateCalendar, ifEdit, useRunDate);
		luf.setMultiPart(true);
		add(luf);
		}

	
	public SubmittedProtocolsPage(String id, WebPage backPage, String expId, Boolean ifEdit, Boolean useRunDate)
		{
		add(new FeedbackPanel("feedback"));
		
		SubmittedProtocolsForm luf = new SubmittedProtocolsForm( "reportsForm", backPage, expId, null, null, ifEdit, useRunDate);
		luf.setMultiPart(true);
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	public final class SubmittedProtocolsForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;

		// We use DTO to avoid memory load associated with loading the ClientReport which contains a large byte array of file content
		List <StandardProtocolDTO> reportLists = new ArrayList <StandardProtocolDTO>();
		
		String searchTitle, expId;
		Boolean ifEdit;
		ModalWindow modal1;
		
		
		public SubmittedProtocolsForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			super(id);
			
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			add(modal1);

			System.out.println("in submitted protocol form");
			searchTitle = "Standard Protocols Uploaded : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);

			reportLists = grabUploadedDocs(fromDateCalendar, toDateCalendar, useRunDate);
			System.out.println("in submitted protocol form. Grabbed data");

			add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			add(new AjaxBackButton("backButton", backPage));
	
			add(dataSetView = buildListView("reportLists"));
			dataSetView.setOutputMarkupId(true);
			}
		
		
		List <StandardProtocolDTO> grabUploadedDocs(Calendar fromDate, Calendar toDate, Boolean useRunDate)
			{
			List <ClientReportDTO> lst = new ArrayList<ClientReportDTO> ();
			
			if (fromDate != null)
				return standardProtocolService.loadInfoForUploadDateRange(fromDate, toDate);
			
			return standardProtocolService.loadInfoForUploadDateRange(fromDate, toDate);
			}
		

		
		public PageableListView buildListView(String id)
			{
			return new PageableListView(id, new PropertyModel(this, "reportLists"), 600)
				{	
				public void populateItem(ListItem listItem) 
					{
					final StandardProtocolDTO item =  (StandardProtocolDTO) listItem.getModelObject();
					
					listItem.add(new Label("protocolId", item.getProtocolId()));
					
					String assayId = StringUtils.isNonEmpty(item.getAssayId()) ? item.getAssayId() : "";
					String assayName = "";
					if (StringUtils.isNonEmpty(item.getAssayId()))
							assayName = assayService.getNameForAssayId(item.getAssayId());
					listItem.add(new Label("assayId", assayId));
					listItem.add(new Label("assayName", assayName));
					
					listItem.add(new Label("sampleType", item.getSampleType()));
					
					String uploaderName = userService.getFullNameByUserId(item.getLoadedBy());
					listItem.add(new Label("uploadedBy", uploaderName));
					
					
					listItem.add(new Label("uploadDate", DateUtils.dateStrFromCalendar("MM/dd/yyyy", item.getStartDate())));
					listItem.add(new Label("retirementDate", DateUtils.dateStrFromCalendar("MM/dd/yyyy", item.getStartDate())));
					
					listItem.add(new Label("fileName", item.getFileName()));
					
					
					
					listItem.add(buildLinkToReport("reportLink", item.getProtocolId()));
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
		
		private Link buildLinkToReport(String id, final String protocolID)
			{
			StandardProtocol report = standardProtocolService.loadByProtocolId(protocolID);

			Link link = new MyStandardProtocolLink("reportLink", new Model(report)); 
			
			return link;
			}
		
				
		private IndicatingAjaxButton buildBackButton(String id, final WebPage backPage)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget arg0)
				{ setResponsePage(backPage);  }
				};
			}
	
		
		public List<StandardProtocolDTO> getReportLists()
			{
			return reportLists;
			}
	
		
		public void setReportLists(List<StandardProtocolDTO> reportLists)
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

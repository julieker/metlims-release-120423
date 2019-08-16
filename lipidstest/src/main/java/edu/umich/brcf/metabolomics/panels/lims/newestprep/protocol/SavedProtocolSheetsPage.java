////////////////////////////////////////////////////
// SavedProtocolSheets.java
// Written by Jan Wigginton, Jun 4, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ProtocolSheetService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.widgets.AjaxBackButton;



public class SavedProtocolSheetsPage extends WebPage
	{
	@SpringBean 
	ProtocolSheetService protocolSheetService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	UserService userService;
	
	Calendar gFromCal;
	Calendar gToCal;
	boolean gUseRun;
	
	public SavedProtocolSheetsPage(String id, WebPage backPage, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
		{
		add(new FeedbackPanel("feedback"));
	
		SavedProtocolSheetsForm luf = new SavedProtocolSheetsForm("reportsForm", backPage, null, fromDateCalendar, toDateCalendar, ifEdit, useRunDate);
		luf.setMultiPart(true);
		add(luf);
		}

	
	public SavedProtocolSheetsPage(String id, WebPage backPage, String expId, Boolean ifEdit, Boolean useRunDate)
		{
		add(new FeedbackPanel("feedback"));
		
		SavedProtocolSheetsForm luf = new SavedProtocolSheetsForm( "reportsForm", backPage, expId, null, null, ifEdit, useRunDate);
		luf.setMultiPart(true);
		//luf.add(new UploadProgressBar("progress", luf));
		add(luf);
		}

	
	public final class SavedProtocolSheetsForm extends Form 
		{
		private WebMarkupContainer container; 
		protected PageableListView dataSetView;

		// We use DTO to avoid memory load associated with loading the ClientReport which contains a large byte array of file content
		List <ProtocolSheetDTO> reportLists = new ArrayList <ProtocolSheetDTO>();
		
		String searchTitle, expId;
		Boolean ifEdit;
		ModalWindow modal1;
		
		
		public SavedProtocolSheetsForm(String id,  WebPage backPage, String expId, Calendar fromDateCalendar, Calendar toDateCalendar, Boolean ifEdit, Boolean useRunDate)
			{
			super(id);
			gFromCal = fromDateCalendar;
			gToCal = toDateCalendar;
			gUseRun = useRunDate;
			modal1 = ModalCreator.createModalWindow("modal1", 500, 300);
			modal1.setOutputMarkupId(true);
			add(modal1);

			searchTitle = "Protocol Sheets Uploaded : " + DateUtils.dateStrFromCalendar("MM/dd/yy", fromDateCalendar) 
				+ "-" + DateUtils.dateStrFromCalendar("MM/dd/yy", toDateCalendar);
			reportLists = grabExampleDataSets(fromDateCalendar, toDateCalendar, useRunDate);
			add(new Label("searchResultTitle", new PropertyModel <String>(this, "searchTitle")));
			add(new AjaxBackButton("backButton", backPage));
	
			add(dataSetView = buildListView("reportLists", modal1));
			dataSetView.setOutputMarkupId(true);
			}
		
		
		List <ProtocolSheetDTO> grabExampleDataSets(Calendar fromDate, Calendar toDate, Boolean useRunDate)
			{
			if (fromDate != null)
				return protocolSheetService.loadDTOsForUploadDateRange(fromDate, toDate);
			
			return protocolSheetService.loadDTOsForUploadDateRange(fromDate, toDate);
			}
		
		
		public PageableListView buildListView(String id, final ModalWindow modal1)
			{
			return new PageableListView(id, new PropertyModel(this, "reportLists"), 1000)
				{	
				public void populateItem(ListItem listItem) 
					{
					final ProtocolSheetDTO item =  (ProtocolSheetDTO) listItem.getModelObject();
					
					listItem.add(new Label("protocolId", item.getProtocolDocumentId()));
					listItem.add(new Label("sheetId", item.getId()));
					
					String assayId = StringUtils.isNonEmpty(item.getAssayId()) ? item.getAssayId() : "";
					String assayName = "";
					if (StringUtils.isNonEmpty(item.getAssayId()))
							assayName = assayService.getNameForAssayId(item.getAssayId());
					listItem.add(new Label("assayName", assayName));
					listItem.add(new Label("assayId", assayId));
					
					listItem.add(new Label("expId", item.getExperimentId()));
					
					listItem.add(new Label("sampleType", item.getSampleType()));
					
					String uploaderName = userService.getFullNameByUserId(item.getRecordedBy());
					listItem.add(new Label("uploadedBy", uploaderName));
					
					
					listItem.add(new Label("uploadDate", DateUtils.dateStrFromCalendar("MM/dd/yyyy", item.getRecordedDate())));
					
					
					listItem.add(buildProtocolSheetLink("sheetLink", item, modal1));
					listItem.add(OddEvenAttributeModifier.create(listItem));
					}
				};
			}
				
				
		private IndicatingAjaxButton buildProtocolSheetLink(String id, final ProtocolSheetDTO dto, final ModalWindow modal2)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget target) // issue 464
					{
					try
						{
						modal2.setInitialHeight(900);
						modal2.setInitialWidth(1100);
						modal2.setOutputMarkupId(true);
						modal2.setPageCreator(new ModalWindow.PageCreator()
							{
							public Page createPage()
								{
								return new EditProtocolSheet("protocolSheet", dto, (WebPage) getPage(), modal2)
									{
									@Override
									protected void onSave( ProtocolSheet sheet, AjaxRequestTarget target) { }
									};
								}		
							});
						
						// issue 224							
						modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
						    {
						    @Override
						    public void onClose(AjaxRequestTarget target)  
							    {   							
							    reportLists = grabExampleDataSets(gFromCal, gToCal, gUseRun);
							    target.add(modal2.getParent());				
							    }
						    });						
						modal2.show(target);												
						}
					catch (Exception e) {  }
					}
				};
				
			}
		
		
				
		private IndicatingAjaxButton buildBackButton(String id, final WebPage backPage)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				protected void onSubmit(AjaxRequestTarget arg0) // issue 464
				{ setResponsePage(backPage);  }
				};
			}
	
		
		public List<ProtocolSheetDTO> getReportLists()
			{
			return reportLists;
			}
	
		
		public void setReportLists(List<ProtocolSheetDTO> reportLists)
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

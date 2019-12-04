////////////////////////////////////////////////////
// LaunchPrepTools.java
// Written by Jan Wigginton, Oct 28, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newestprep;

import java.util.Calendar;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMailMessage;
import edu.umich.brcf.metabolomics.panels.admin.messaging.METWorksMessageMailer;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.PrepSearchByAnythingPage;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol.EditProtocolSheet;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol.SavedProtocolSheetsPage;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol.SubmittedProtocolsPage;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewDataEntryTypePage;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewEditPrepPlate;
import edu.umich.brcf.metabolomics.panels.lims.preparations.NewPrepSheetUpload;
import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.layers.dto.StandardProtocolDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.StandardProtocolService;
import edu.umich.brcf.shared.layers.service.SystemConfigService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.AssayAndLabelUploaderPanel;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentAssaySampleTypeSelectorPanel;
import edu.umich.brcf.shared.panels.utilitypanels.ExperimentOrDateSearchPanel;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;


public class LaunchPrepTools extends Panel
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	AssayService assayService;
	
	@SpringBean
	 StandardProtocolService standardProtocolService;
	
	@SpringBean 
	 DocumentService documentService;
	
	@SpringBean
	 SystemConfigService systemConfigService;
	
	@SpringBean
	SampleAssayService sampleAssayService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	METWorksMessageMailer mailer;
	
	public LaunchPrepTools ()
	{
		
		this("PREP");
		
	}
	public LaunchPrepTools(String id) 
		{
		super(id);
		
		LaunchPrepToolsForm lde = new LaunchPrepToolsForm("launchPrepToolsForm");
		lde.setMultiPart(true);
		add(lde);
		}
	
	
	public final class LaunchPrepToolsForm extends Form 
		{	
		LaunchPrepToolsForm(String id)
			{
			super(id);

			final ModalWindow modal1= new METWorksPctSizableModal("modal1", 0.9, 0.9);
			add(modal1);
			
			final METWorksPctSizableModal modal2 = new METWorksPctSizableModal("modal2", 0.99, 0.99);
			add(modal2);

			add(buildProtocolStartPanel("createProtocolSheet", modal2));
			//add(buildProtocolStartPanel("viewProtocolSheets", modal2));
			add(buildSubmittedSheetsPanel("viewProtocolSheets", true, modal2));
			add(buildLinkToModal("createPrepLink", modal2));
			add(buildReportsDateExpSelector("allProtocolsLink", true, modal2));
			add(buildProtocolUploadPanel("protocolUploadLink"));
			}	
		}
	
	
	private AssayAndLabelUploaderPanel buildProtocolUploadPanel(String id)
		{
		return new AssayAndLabelUploaderPanel(id)
			{
			@Override
			public void processFile(FileUpload upload, String selectedAssay, String selectedLabel)		
				{
				StandardProtocolDTO docDto = new StandardProtocolDTO();
		          
				docDto.setFileContents(upload.getBytes());
	            docDto.setFileName(upload.getClientFileName());    
	            docDto.setFileType(upload.getContentType());
	            docDto.setSampleType(selectedLabel);
	            String assayId = StringParser.parseId(selectedAssay);
	            docDto.setAssayId(assayId);
	            docDto.setStartDate(Calendar.getInstance());
	            String userId = (((MedWorksSession) getSession()).getCurrentUserId());
	            docDto.setLoadedBy(userId);
	           
	        	documentService.saveStandardProtocol(docDto);
				
				assayId = docDto.getAssayId();
				final String assayName = assayId == null ? "Unknown" : assayService.getNameForAssayId(assayId);
				final String userName = userService.getFullNameByUserId(((MedWorksSession) getSession()).getCurrentUserId());
				
				String msg = mailer.getStandardProtocolUploadMessage(userName, assayName, docDto.getSampleType());
				List<String> email_contacts = (List<String>) (systemConfigService.getSystemConfigMap())
						.get("protocol_change_notification_contact");
	     		
				if (email_contacts != null)
					for (String email_contact : email_contacts)
						mailer.sendMessage(new METWorksMailMessage("metabolomics@med.umich.edu", email_contact, "METLIMS New Standard Protocol Upload Message", msg));
		     		
	           }
			};
		}

	
	private ExperimentOrDateSearchPanel buildSubmittedSheetsPanel(final String id, final boolean isEnabled, ModalWindow modal1)
		{
		ExperimentOrDateSearchPanel panel = new ExperimentOrDateSearchPanel(id)
			{
			@Override
			public void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate)
				{
				if (StringUtils.isEmptyOrNull(searchType))
					return;

				Boolean searchByRunDate = (searchType == null ? false : searchType.equals("Run Date"));

				try
					{
					setResponsePage(new SavedProtocolSheetsPage("searchResults", (WebPage) this.getPage(),fromDate, toDate, false, searchByRunDate));
					} 
				catch (Exception e) {  }
				}
			};

		panel.limitToSearchType("Upload Date");
		return panel;
		}
	
	
	private ExperimentOrDateSearchPanel buildReportsDateExpSelector(final String id, final boolean isEnabled, ModalWindow modal1)
		{
		ExperimentOrDateSearchPanel panel = new ExperimentOrDateSearchPanel(id)
			{
			@Override
			public void doSubmit(String searchType, String selectedExperiment, Calendar fromDate, Calendar toDate)
				{
				if (StringUtils.isEmptyOrNull(searchType))
					return;

				Boolean searchByRunDate = (searchType == null ? false : searchType.equals("Run Date"));

				try
					{
					setResponsePage(new SubmittedProtocolsPage("searchResults", (WebPage) this.getPage(),fromDate, toDate, false, searchByRunDate));
					} 
				catch (Exception e) {  }
				}
			};

		panel.limitToSearchType("Upload Date");
		return panel;
		}
	

	private AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modal1) 
		{
		// issue 39
		return new AjaxLink <Void>(linkID)
			{
			@Override
			public void onClick(final AjaxRequestTarget target)
				{
				doClick(modal1, target, linkID);
				}
			};
		}
	
	
	public void doClick(final METWorksPctSizableModal modal1, AjaxRequestTarget target, final String linkID)
		{
		modal1.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
				{
				switch (linkID)
					{
					case "newPrepSearch" : 
						return new PrepSearchByAnythingPage(getPage()); 
					case "createPrepLink" :
					default :
	           			return buildCreatePrep(modal1);
					}
				}
			});

		modal1.show(target);
		}
	
	
	NewDataEntryTypePage buildCreatePrep(final ModalWindow modal1)
		{
		return (new NewDataEntryTypePage(getPage(), modal1)
			{
			@Override
			protected void onSave(String choice1, String choice2, String prepTitle) 
				{
				if ("Manual".equals(choice1))
					setResponsePage(new NewEditPrepPlate(getPage(), prepTitle, null, choice2));
				else
					setResponsePage(new NewPrepSheetUpload(getPage(), prepTitle, null, choice2));
				}
			});
		}	
	
	
	private ExperimentAssaySampleTypeSelectorPanel buildProtocolStartPanel(final String id, 
			final ModalWindow modal1)
		{
		
		return new ExperimentAssaySampleTypeSelectorPanel(id)
			{
			@Override 
			public boolean isEnabled() { return true; } 
			
			@Override
			public WebPage getResponsePage(String id2, WebPage backPage, String selectedExperiment, String selectedAssay, 
					String sampleType)
				{
				ProtocolSheetDTO dto = new ProtocolSheetDTO(); //Issue 227
				if ("createProtocolSheet".equals(id))
					{
					dto.setAssayId(selectedAssay);
					dto.setExperimentId(selectedExperiment);
					dto.setSampleType(sampleType);
					return ((WebPage) new EditProtocolSheet("protocolSheet", dto, (WebPage) getPage())
						{
						@Override
						protected void onSave(ProtocolSheet sheet, AjaxRequestTarget target) {  }
						});
					}

				return ((WebPage) new EditDocumentPage(getPage(), selectedExperiment, selectedAssay, modal1, true, false));
				}
			};
		}
	}

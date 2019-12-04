package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.metabolomics.panels.lims.newestprep.protocol.EditProtocolSheet;
import edu.umich.brcf.metabolomics.panels.lims.project.ProjectDetail2;
import edu.umich.brcf.shared.layers.domain.Assay;
import edu.umich.brcf.shared.layers.domain.ClientReport;
import edu.umich.brcf.shared.layers.domain.Document;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.ExperimentDocument;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.ProtocolReport;
import edu.umich.brcf.shared.layers.domain.ProtocolSheet;
import edu.umich.brcf.shared.layers.dto.ProtocolSheetDTO;
import edu.umich.brcf.shared.layers.dto.UserDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ClientReportService;
import edu.umich.brcf.shared.layers.service.DocumentService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.ProtocolReportService;
import edu.umich.brcf.shared.layers.service.ProtocolSheetService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.panels.utilitypanels.ConfirmBox;
import edu.umich.brcf.shared.panels.utilitypanels.DocumentDownloadPage;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.widgets.METWorksPctSizableModal;
import edu.umich.brcf.shared.util.widgets.MyFileLink;
import edu.umich.brcf.shared.util.widgets.MyProtocolLink;
import edu.umich.brcf.shared.util.widgets.MyReportLink;
import edu.umich.brcf.shared.panels.login.MedWorksSession;



@SuppressWarnings("serial")
public class ExperimentDetail extends Panel
	{
	@SpringBean 
	private ExperimentService experimentService;
	
	@SpringBean
	UserService userService;
	
	@SpringBean
	DocumentService documentService;
	
	@SpringBean 
	AssayService assayService;
	
	@SpringBean 
	ClientReportService clientReportService;

	@SpringBean 
	ProtocolReportService protocolReportService;
	
	@SpringBean 
	ProjectService projectService;
	
	@SpringBean
	ProtocolSheetService protocolSheetService;
	
	AjaxLink editLink;
	Experiment experiment;
	final METWorksPctSizableModal modalEdit, modalDoc, modalProtocol;
	private final ModalWindow modalwin;
	// issue 441 LoadableDetachableModel<List <ExperimentDocument>> documentItems;
    LoadableDetachableModel <List<String>> allDocIdsModel;
	LoadableDetachableModel<List<String>> clientItems;
	LoadableDetachableModel<List <ProtocolSheet>> protocolSheetItems;
	// issue 441
	LoadableDetachableModel<List<String>> protocolReportItems;
	private Map<String, ExperimentDocument> smallDocsByIdMap = new HashMap<String, ExperimentDocument>();
	private Map<String, String> allDocNamesByIdMap = new HashMap<String, String>();
	private Map<String, ClientReport> smallDocsClientReportByIdMap = new HashMap<String, ClientReport>();
	private Map<String, String> allDocNamesClientReportByIdMap = new HashMap<String, String>();
	// issue 441
	private Map<String, ProtocolReport> smallDocsProtocolReportByIdMap = new HashMap<String, ProtocolReport>();
	private Map<String, String> allDocNamesProtocolReportByIdMap = new HashMap<String, String>();
	private Map<String, String> allRepDescriptorsByIdMap = new HashMap <String, String> ();
	private Map<String, String> allLoadedByIdMap = new HashMap <String, String> ();
	private Map<String, String> allProtocolDescriptorsByIdMap = new HashMap <String, String> ();
	private Map<String, String> allProtocolLoadedByIdMap = new HashMap <String, String> ();
	
	public ExperimentDetail(String id, Experiment experiment) 
		{  
		super(id);	
		setOutputMarkupId(true);
		setExperiment(experiment);
		setDefaultModel(new CompoundPropertyModel<Experiment>(getExperiment()));
		modalwin = ModalCreator.createModalWindow("modalwin", 650, 480);
		modalwin.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        {
	        public void onClose(AjaxRequestTarget target)
	        	{
	        	updateData(experimentService.loadById(getExperiment().getExpID()));
	        	target.add(modalwin.getParent());
	        	target.add(modalwin.getParent().getParent());
	        	}
	        });
		add(modalwin);
		modalwin.setOutputMarkupId(true);
		// Issue 245
		allDocIdsModel = new LoadableDetachableModel<List<String>>()
		    {
			// issue 441
			@Override
			protected List<String> load()   
		        { 
				if (getExperiment() != null)
		        	{
		        	allDocNamesByIdMap = documentService.loadIdNameMapByExpIdNotDeleted(getExperiment().getExpID());
		        	smallDocsByIdMap =  documentService.loadSmallDocByByIdForExpIdNotDeleted(getExperiment().getExpID()) ;
		        	return ListUtils.makeListFromObjectCollection(allDocNamesByIdMap.keySet()); //documentService.loadIdsByExpIdNotDeleted(experiment.getExperimentID());
		        	}
		        allDocNamesByIdMap = new HashMap<String, String>();
		        smallDocsByIdMap = new HashMap<String, ExperimentDocument>();
		        return new ArrayList<String>();			
				}
			};
					
		// Issue 245
		clientItems = new LoadableDetachableModel<List<String>>()
		    {
			// issue 441
			@Override
			protected List<String> load()   
		        { 
				if (getExperiment() != null)
		        	{
					allDocNamesClientReportByIdMap = clientReportService.loadIdNameMapByExpIdNotDeleted(getExperiment().getExpID());
					smallDocsClientReportByIdMap =  clientReportService.loadSmallDocByByIdForExpIdNotDeleted(getExperiment().getExpID()) ;
					List <String> lstRepIds  = new ArrayList<>(allDocNamesClientReportByIdMap.keySet());
					allRepDescriptorsByIdMap = clientReportService.getRepDescriptors(lstRepIds);
					allLoadedByIdMap = clientReportService.getLoadedBy(lstRepIds);
					return ListUtils.makeListFromObjectCollection(allDocNamesClientReportByIdMap.keySet()); //documentService.loadIdsByExpIdNotDeleted(experiment.getExperimentID());
		        	}
				allDocNamesClientReportByIdMap = new HashMap<String, String>();
				smallDocsClientReportByIdMap = new HashMap<String, ClientReport>();
				return new ArrayList<String>();			
				}
			}; 
			
		// Issue 245
		protocolSheetItems = new LoadableDetachableModel<List<ProtocolSheet>>()
		    {
			@Override
			protected List<ProtocolSheet> load()  
				{ 
				if (getExperiment() != null)
					return protocolSheetService.loadForExpId(getExperiment().getExpID(), true); 
				return new ArrayList<ProtocolSheet> ();
				}
			};
		
			
		// Issue 245
		protocolReportItems = new LoadableDetachableModel<List<String>>()
		   {
			// issue 441
			@Override
			protected List<String> load()   
		        { 
				if (getExperiment() != null)
		        	{
					allDocNamesProtocolReportByIdMap = protocolReportService.loadIdNameMapByExpIdNotDeleted(getExperiment().getExpID(), false);
					smallDocsProtocolReportByIdMap =  protocolReportService.loadSmallDocByByIdForExpIdNotDeleted(getExperiment().getExpID()) ;
					List <String> lstRepIds  = new ArrayList<>(allDocNamesProtocolReportByIdMap.keySet());
					allProtocolDescriptorsByIdMap = protocolReportService.getProtocolDescriptors(lstRepIds);
					allProtocolLoadedByIdMap = protocolReportService.getLoadedBy(lstRepIds);
					return ListUtils.makeListFromObjectCollection(allDocNamesProtocolReportByIdMap.keySet()); //documentService.loadIdsByExpIdNotDeleted(experiment.getExperimentID());
		        	}
				allDocNamesProtocolReportByIdMap = new HashMap<String, String>();
				smallDocsProtocolReportByIdMap = new HashMap<String, ProtocolReport>();
		        return new ArrayList<String>();			
				}
			}; 													
    	modalDoc = buildModalWindow("modalDoc", .7, .32);
    	modalEdit = buildModalWindow("modalEdit", .5, .85);
    	modalProtocol = buildModalWindow("modalProtocol",1.0, 0.85);  	
    	add(modalDoc);
    	add(modalEdit);
    	add(modalProtocol);    	
		add(new Label("expID"));
		add(new Label("creationDateAsStr"));
		add(new Label("expName"));
		add(new Label("expDescription"));
		add(new Label("project.projectName"));	   
		// Issue 206
		AjaxCheckBox box = new AjaxCheckBox("isChear" )
	        {
	        @Override
	        public void onUpdate(AjaxRequestTarget target)
		        {
		
		        }
	        };	    
	    add (box.setEnabled(false));		
		PropertyModel<String> projIdModel = new PropertyModel<String>(experiment, "project.projectID");
		AjaxLink projDetailLink;
		add(projDetailLink = buildLinkToProjectDetails("projDetails", modalEdit, projIdModel.getObject()));
		projDetailLink.add(new Label("project.projectID"));
		
		add(new Label("priority.shortName"));
		add(new Label("notes"));
		add(new Label("serviceRequest").setVisible(true));
		
		// Issue 245
		add(new ListView("docList", allDocIdsModel ) 
			{
			@Override
			protected void populateItem(ListItem  item) 
				{
				// issue 441	
				final String docId = (String) item.getModelObject();
				if (smallDocsByIdMap.keySet().contains(docId))
					item.add(buildFileLink(smallDocsByIdMap.get(docId)).setOutputMarkupId(true));
				else 
					{
					String docName = allDocNamesByIdMap.get(docId);
					item.add(buildLinkToFileDownloadModal("fileLink", modalwin,  docId, null, docName));
					}				
				item.add(buildDeleteButton("deleteButton", docId, false, modalwin).setOutputMarkupId(true));
				}
			});
		
		// Issue 245
		// issue 441
		add(new ListView("reports",clientItems ) 
			{
			@Override
			protected void populateItem(ListItem item) 
			{
				// issue 441	
				final String docId = (String) item.getModelObject();
				if (smallDocsClientReportByIdMap.keySet().contains(docId))
					item.add(buildReportLink(smallDocsClientReportByIdMap.get(docId)).setOutputMarkupId(true));
				else 
					{
					String docName = allDocNamesClientReportByIdMap.get(docId);
					item.add(buildLinkToFileDownloadModal("repLink", modalwin,  docId, null, docName));
					}	
				item.add(new Label("reportDescriptor",allRepDescriptorsByIdMap.get(docId)));				
				String strLoadedBy = allLoadedByIdMap.get(docId);
				item.add(buildDeleteClientButton("deleteReportButton", Long.parseLong(docId), strLoadedBy).setOutputMarkupId(true));
				}
			});
		
		// Issue 245
		add(new ListView("protocols",protocolReportItems ) 
			{
			@Override
			protected void populateItem(ListItem item) 
			{
				// issue 441	
				final String docId = (String) item.getModelObject();
				if (smallDocsProtocolReportByIdMap.keySet().contains(docId))
					item.add(buildProtocolLink(smallDocsProtocolReportByIdMap.get(docId)).setOutputMarkupId(true));
				else 
					{
					String docName = allDocNamesProtocolReportByIdMap.get(docId);
					item.add(buildLinkToFileDownloadModal("protocolLink", modalwin,  docId, null, docName));
					}	
				item.add(new Label("protocolDescriptor",allProtocolDescriptorsByIdMap.get(docId)));
				String strLoadedBy =allProtocolLoadedByIdMap.get(docId);
				item.add(buildDeleteProtocolButton("deleteProtocolButton", Long.parseLong(docId), strLoadedBy).setOutputMarkupId(true));
				}
			});
		
		// Issue 245
		/* PUT BACK */
		add(new ListView<ProtocolSheet>("protocolSheets", protocolSheetItems) 
			{
			@Override
			public void populateItem(ListItem listItem) 
				{
				final ProtocolSheet sheet =  (ProtocolSheet) listItem.getModelObject();				
				listItem.add(buildProtocolSheetLink("sheetLink", sheet, modalProtocol));
			//	listItem.add(new Label("sheetDescriptor", protocolSheetService.getDescriptorString(dto)));
				listItem.add(buildDeleteSheetButton("deleteSheetButton", sheet).setOutputMarkupId(true));				
				}
			});
	 
			
		boolean isClient=(((MedWorksSession) getSession()).getCurrentUserViewPoint().getName().equals("Client"));
		
		add(buildLinkToModal("edit", modalEdit).setVisible(!isClient));
		add(buildLinkToModal("uploadDoc", modalDoc).setVisible(!isClient));
		add(buildLinkToModal("clientReport", modalDoc).setVisible(!isClient));
		add(buildLinkToModal("protocolReport", modalDoc).setVisible(!isClient));
		}

	
	private METWorksPctSizableModal buildModalWindow(String id, double widthPct, double heightPct)
		{
		final METWorksPctSizableModal modalwin = new METWorksPctSizableModal(id, widthPct, heightPct);
		modalwin.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        {
	        public void onClose(AjaxRequestTarget target)
	        	{
	        	updateData(experimentService.loadById(getExperiment().getExpID()));
	        	target.add(modalwin.getParent());
	        	target.add(modalwin.getParent().getParent());
	        	}
	        });
		
		return modalwin;
		}
	
	
	private IndicatingAjaxLink buildDeleteButton(String id, final String documentId, final Boolean forPrep, final ModalWindow modalwin)
	{
		// issue 39
	IndicatingAjaxLink lnk  = new IndicatingAjaxLink <Void>(id)
		{
		@Override
		public void onClick(AjaxRequestTarget target) 
			{
			modalwin.setInitialWidth(400);
			modalwin.setInitialHeight(200);
        	
        	modalwin.setPageCreator(new ModalWindow.PageCreator()
                 {
        	     public Page createPage()
                 	{
                	return new ConfirmBox("confirmBox", "Are you sure that you would like to delete this document?", modalwin)
                		{
						@Override
						protected void doAction(AjaxRequestTarget target) 
							{							
							documentService.deleteDocument(documentId);									
							updateData(experimentService.loadById(getExperiment().getExpID()));
							}
                		};
                	}
				});
        	 modalwin.show(target);
			 }
    	};
	return lnk;	
	}
	
	
	// issue 245 
	private Link<?> buildDeleteClientButton(String id, final long documentId, final String strLoadedBy)
	    {
	    final Link lnk  = new Link<Object>(id)
		    {
		    @Override
		    public void onClick()
			    {
		    	if (strLoadedBy.equals(((MedWorksSession) getSession()).getCurrentUserId()))
		    	     {
		             clientReportService.deleteClientReport(documentId);
			         updateData(experimentService.loadById(getExperiment().getExpID()));
		    	     }
			    }				    
		    };	
		if (!strLoadedBy.equals(((MedWorksSession) getSession()).getCurrentUserId()))    
		    {
			lnk.add(new AttributeModifier("onclick", "return alert('Only the owner of the client report is allowed to delete the report.');" ));	
			return lnk;	
		    }
	    String confirmMsg = "Are you sure that you would like to delete this client report?";
	    lnk.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));	
	    return lnk;	
	    }
	
	// issue 245 
	private Link<?> buildDeleteProtocolButton(String id, final long documentId, final String strLoadedBy)
	    {
	    final Link lnk  = new Link<Object>(id)
		    {
		    @Override
		    public void onClick()
			    {
		    	// issue 253
		    	if (strLoadedBy.equals(((MedWorksSession) getSession()).getCurrentUserId()))
		    	    {	
		            protocolReportService.deleteProtocolReport(documentId);
			        updateData(experimentService.loadById(getExperiment().getExpID()));
		    	    }
		    	}		
		 		    
		    };	
		    
		    // issue 253
	    if (!strLoadedBy.equals(((MedWorksSession) getSession()).getCurrentUserId()))    
		    {
			lnk.add(new AttributeModifier("onclick", "return alert('Only the owner of the protocol report is allowed to delete the report.');" ));	
			return lnk;	
		    }    
	    String confirmMsg = "Are you sure that you would like to delete this protocol report?";
	    lnk.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));	
	    return lnk;	
	    }
	
	private Link<?> buildDeleteSheetButton(String id, final ProtocolSheet sheet)
		{
		Link lnk  = new Link<Object>(id)
			{
			@Override
			public void onClick()
				{
				protocolSheetService.deleteSheet(sheet.getId());
				updateData(experimentService.loadById(getExperiment().getExpID()));
				}
			
			public boolean isEnabled() { return true; }
			public boolean isVisible() { return !sheet.isDeleted(); }
			};
			
		String confirmMsg = "Are you sure that you would like to delete this document?";
		lnk.add(new AttributeModifier("onclick", "return confirm('" + confirmMsg + "');" ));
		
		return lnk;	
		}
	
	
	private Link buildProtocolLink(final ProtocolReport rep) 
		{
		Assay assay = null;
	
		try {  assay = assayService.loadAssayByID(rep.getAssayId()); }
		catch (Exception e) {   }
		
		final String assayName = assay == null ? "Unknown" : assay.getAssayName();
		final String userName = userService.getFullNameByUserId(rep.getLoadedBy());
		
		Link link = new MyProtocolLink("protocolLink", new Model(rep)) 
			{
			@Override
			protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		tag.put("title", protocolReportService.getDescriptorString(rep));
	    		}
			};
		
		link.add(new Label("protocolName", documentService.getNiceReportName(rep)));
		return link;
		}
	
	
	private Link buildReportLink(final ClientReport rep) 
		{
		Assay assay = null;
	
		try {  assay = assayService.loadAssayByID(rep.getAssayId()); }
		catch (Exception e) {   }
		
		final String assayName = assay == null ? "Unknown" : assay.getAssayName();
		final String userName = userService.getFullNameByUserId(rep.getLoadedBy());
		
		// Issue 245
		Link link = new MyReportLink("repLink", new Model(rep)) 
			{
			public boolean isVisible()
			    {
				return !rep.isDeleted();
			    }
			 @Override
			 protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		tag.put("title", clientReportService.getDescriptorString(rep));   
	    		} 
			};
		
	    link.add(new Label("repName", documentService.getNiceReportName(rep)));
		return link;
		}
	
	
	private Link buildFileLink(final ExperimentDocument doc) 
		{
		Link link = new MyFileLink("fileLink", new Model<ExperimentDocument>(doc))
			/*{
			public boolean isVisible()
				{
				return !doc.isDeleted();
				}
			}*/;
		link.add(new Label("fileName", documentService.getNiceDocumentName(doc)));
		
		return link;
		}
	
	
	private AjaxLink buildLinkToModal(final String linkID, final METWorksPctSizableModal modalwin) 
		{
		// issue 39
		editLink = new AjaxLink <Void>(linkID)
        	{
			@Override
            public void onClick(final AjaxRequestTarget target)
            	{
		        modalwin.setPageWidthPct(linkID.startsWith("e") ? 0.25 : .3);
	            modalwin.setPageHeightPct(linkID.startsWith("e") ? 0.15 : .8);
            	modalwin.setPageCreator(new ModalWindow.PageCreator()
	                 {
                     public Page createPage()
                     	{
                    	if(linkID.startsWith("e"))
                    		return new EditExperiment(getPage(), new Model<Experiment>(getExperiment()), modalwin)
	                    		{
								@Override
								protected void onSave(Experiment exp, AjaxRequestTarget target)  { updateData(exp); }
								};
                			
                    	return new EditDocumentPage(getPage(), getExperiment().getExpID(), modalwin, linkID.startsWith("client"), 
                    			linkID.startsWith("upload"), linkID.startsWith("protocol"));
                    	}
					 });
                // issue 441 remove extra closeCallback 	
            	modalwin.show(target);
            	}
			
			@Override
			public boolean isEnabled() { return  getExperiment()!= null; }
        	};
        	
		return editLink;
		}
	
	

	// issue 441
	private IndicatingAjaxLink<?> buildLinkToFileDownloadModal(final String linkID, final ModalWindow modalwin, 
			final String docId, final BigDecimal longDocId,  final String docName) 
			{
			IndicatingAjaxLink<?> downloadModalLink = new IndicatingAjaxLink<Object>(linkID)
		    	{
				@Override
		        public void onClick(final AjaxRequestTarget target)
		        	{
					modalwin.setInitialWidth(800);
					modalwin.setInitialHeight(200);
		        	modalwin.setPageCreator(new ModalWindow.PageCreator()
		                 {
		                 public Page createPage()
		                 	{		               	
		                		 return new DocumentDownloadPage(docId, modalwin,linkID );		                	
		                	}
						 });
		        	modalwin.show(target);
		        	}
		    	};
		    if (linkID.equals("fileLink"))
		    	downloadModalLink.add(new Label("fileName", docName));
		    else if (linkID.equals("repLink"))	
		    	downloadModalLink.add(new Label("repName", docName));
		    // issue 441
		    else if (linkID.equals("protocolLink"))
		    	downloadModalLink.add(new Label("protocolName", docName));
		    return downloadModalLink;
			}
	
	public AjaxLink buildLinkToProjectDetails(final String linkID, final ModalWindow modal1, final String projectId)
		{
		// issue 39
		return new AjaxLink <Void> (linkID)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setInitialWidth(625);
			    modal1.setInitialHeight(600);

				final Project withDocs = projectService.loadById(projectId);

			    modal1.setPageCreator(new ModalWindow.PageCreator()
		    		{
		            public Page createPage()
		             	{
		            	return (new ProjectDetail2(new Model(withDocs)));
		             	}
		    		});
		    	
		    	modal1.show(target); 
				}
			};
		}
	
	
	public void updateData(final Experiment experiment) 
		{
		setExperiment(experiment);
		ExperimentDetail.this.setDefaultModel(new CompoundPropertyModel(getExperiment()));
		}
	
	private AjaxLink buildProtocolSheetLink(String id, final ProtocolSheet sheet, final ModalWindow modal2)
		{
		AjaxLink link;
		
		// Issue 237
		// issue 39
	    link =  new AjaxLink <Void>(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target) 
				{
				try
					{
					modal2.setInitialHeight(900);
					modal2.setInitialWidth(1100);
					modal2.setPageCreator(new ModalWindow.PageCreator()
						{
						public Page createPage()
							{
							ProtocolSheetDTO dto = ProtocolSheetDTO.instance(sheet);
							
							return new EditProtocolSheet("protocolSheet", dto, (WebPage) getPage(), modal2)
								{
								@Override
								protected void onSave( ProtocolSheet sheet, AjaxRequestTarget target) { }
								};
							}
						});
					//issue 239
					modal2.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
					    {
					    @Override
					    public void onClose(AjaxRequestTarget target)  
					        {    
					    	target.add(modal2.getParent());				    	
					        }
					    });
					modal2.show(target);
					}
				catch (Exception e) {  }
				}
			@Override
			protected void onComponentTag(ComponentTag tag)
	    		{
	    		super.onComponentTag(tag);
	    		tag.put("title", protocolSheetService.getDescriptorString(sheet));
	    		}
			};
			
		link.add(new Label("sheetName", protocolSheetService.getDescriptorString(sheet)));
		return link;
		}
	
	public Experiment getExperiment() { return experiment; }
	public void setExperiment(Experiment experiment) { this.experiment = experiment;
	}
}

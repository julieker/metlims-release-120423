////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  BaseWorklistPanel.java
//  Written by Jan Wigginton
//  February 2015, Last update : July 2015
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.workflow.worklist_builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import edu.umich.brcf.metabolomics.layers.service.GeneratedWorklistService;
import edu.umich.brcf.metabolomics.panels.admin.accounts.UserRegistrationPage;
import edu.umich.brcf.shared.layers.service.SampleAssayService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.panels.utilitypanels.AddNotesPage;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.interfaces.ICommentObject;
import edu.umich.brcf.shared.util.io.StringUtils;



public abstract class BaseWorklistPanel extends Panel
	{
	@SpringBean
	GeneratedWorklistService generatedWorklistService;

	@SpringBean
	SampleAssayService sampleAssayService;

	@SpringBean
	SampleService sampleService;

	protected WorklistSimple worklist;
	private   WebMarkupContainer container;
	protected PageableListView worklistView;
	protected PagingNavigator pagingNavigator;
	protected ModalWindow modal1;
	protected List<WebMarkupContainer> sibContainers = new ArrayList<WebMarkupContainer>();
    protected ICommentObject commentObject;
    private int maxLength = 90;
	public BaseWorklistPanel(String id)
		{
		this(id, null);
		}

	public BaseWorklistPanel(String id, WorklistSimple w)
		{
		super(id);
		
		
		container = new WebMarkupContainer("container");
		add(container);

		modal1 = ModalCreator.createModalWindow("modal1", 800, 320);
		add(modal1);

		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
		
			@Override
			public void onClose(AjaxRequestTarget target)  
				{
				String notes = (String) ((MedWorksSession) Session.get()).getSaveValue();
				commentObject.setComments(notes);
				}
			});
		
		this.worklist = w;

		container.add(this.buildSelectAllBox(id, container));
		container.add(this.buildIncludeSampleName(id, container));// issue 288
		// issue 32 
		container.add(this.buildCustomDirectoryStructure( container));
	    // issue 32	
		container.add(this.buildCustomDirectoryName( container));
		container.add(this.buildCustomDirectoryLabel( container));
		
		
		container.add(worklistView = buildListView());
		worklistView.setOutputMarkupId(true);
		container.add(new Label("tableLabel", new Model("Worklist Preview")));
		container.add(pagingNavigator = buildPagingNavigator("navigator",worklistView));
		pagingNavigator.setOutputMarkupId(true);

		container.setOutputMarkupId(true);
		}
	
	public PageableListView buildListView()
		{
		return new PageableListView("worklist", new PropertyModel(worklist, "items"), 2000)
			{
			public void populateItem(ListItem listItem)
				{
				WorklistItemSimple item = (WorklistItemSimple) listItem.getModelObject();
				initListItem(listItem, item);
				}
			};
		}
	
	protected AjaxLink buildCommentsButton(String id, final WorklistItemSimple item)
		{
		return new AjaxLink(id)
			{
			@Override
			public void onClick(AjaxRequestTarget target)
				{
				modal1.setInitialHeight(280);
				modal1.setInitialWidth(700);				
				commentObject = item;
				((MedWorksSession) Session.get()).setSaveValue(commentObject.getComments()); // issue 295
				modal1.setPageCreator(new ModalWindow.PageCreator()
					{
					public Page createPage()
						{
						String commentTitle = "Comments for "+ item.getSampleName();

						return (new AddNotesPage((Page) getPage(),modal1, new PropertyModel<String>(item, "comments"), commentTitle)
							{
							@Override
							protected void onSave(String notes)
								{
								((MedWorksSession) Session.get()).setSaveValue(notes);								
								///BaseWorklistPanel.this.info("Comments recorded.  They will be written to the database when you download your worklist.");
								}
							});
						}
					});
				    modal1.show(target);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};
		}

	protected AjaxLink buildDeleteButton(String id, final WorklistItemSimple item, final WebMarkupContainer container)
		{  // issue 464
		AjaxLink lnk = new AjaxLink(id)
			{
			public boolean isVisible() {
				return item.getIsDeleted() == false; // item.getRepresentsControl()
				}

			public boolean isEnabled()
				{
				return true; // (fileUploadField.getFileUpload().getClientFileName()  != null); getSelected().equals("Client Report");
				}

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				worklist.deleteItem(item);
				target.add(container);
				}

			@Override
			public MarkupContainer setDefaultModel(IModel arg0) {
				// TODO Auto-generated method stub
				return (MarkupContainer) this.getDefaultModel();
			}
			};

		return lnk;
		}

	
	protected AjaxLink buildUnDeleteButton(String id, final WorklistItemSimple item, final WebMarkupContainer container)
		{
		AjaxLink lnk = new AjaxLink(id)
			{
			public boolean isVisible() { return false; }

			@Override
			public void onClick(AjaxRequestTarget target)
				{
				worklist.unDeleteItem(item);
				target.add(container);
				}
			@Override // issue 464
			public MarkupContainer setDefaultModel(IModel model) 
			    {
				// TODO Auto-generated method stub
				return this;
			    }
			};

		return lnk;
		}


	protected AjaxCheckBox buildRedoCheckbox(String id, final ListItem<WorklistItemSimple> item, final WebMarkupContainer container)
		{
		final WorklistItemSimple so = (WorklistItemSimple) item.getModelObject();

		AjaxCheckBox box = new AjaxCheckBox("selected", new PropertyModel(so, "selected"))
			{
			@Override
			public void onUpdate(AjaxRequestTarget target)
				{
				worklist.updateOutputFileNames();
				target.add(container);
				}
			};

		return box;
		}

	
	protected AjaxCheckBox buildSelectAllBox(String id, final WebMarkupContainer container)
		{
		AjaxCheckBox box = new AjaxCheckBox("allSelected", new PropertyModel(worklist, "allSelected"))
			{
			@Override
			public void onUpdate(AjaxRequestTarget target)
				{
				worklist.updateRedoStatus();
				worklist.updateOutputFileNames();
				target.add(container);
				}
			};

		return box;
		}
	
	
	// issue 288 
	// issue 32
	protected AjaxCheckBox buildCustomDirectoryStructure( final WebMarkupContainer container)
	    {
	    AjaxCheckBox box = new AjaxCheckBox("customDirectoryStructure", new PropertyModel(worklist, "isCustomDirectoryStructure"))
		    {
	    	@Override
		    public boolean isEnabled()
			    {
			    return worklist.getItems().size() > 0;	
			    }
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
		    	// issue 32
		    	if (worklist.getIsCustomDirectoryStructure())
		    		target.appendJavaScript("alert('Please enter in a non-blank custom IDDA directory');");
				target.add(container);
			    }
		    };
	    return box;
	    }
	
	// issue 32
	protected TextField buildCustomDirectoryName( final WebMarkupContainer container)
	    {
	    TextField <String> custDirectoryFld = new TextField<String>("customFileName", new PropertyModel(worklist, "customDirectoryStructureName"))
		    {
		    @Override
		    public boolean isVisible()
			    {
			    return worklist.getIsCustomDirectoryStructure();	
			    }
		    };
		custDirectoryFld.add(this.buildStandardFormComponentUpdateBehavior("change", "updateCustomDirectory"));    
		custDirectoryFld.add(StringValidator.maximumLength(maxLength));
		return custDirectoryFld;
	    }
	
	// issue 32
	protected Label buildCustomDirectoryLabel( final WebMarkupContainer container)
	    {
		Label customDirLabel = new Label("customDirectoryLabel", new Model("Custom Directory for IDDA:"))
	        {
		    @Override
		    public boolean isVisible()
			    {
			    return worklist.getIsCustomDirectoryStructure();	
			    }
		    };	
		return customDirLabel;
	    }
	
	/// issue 32
	private AjaxFormComponentUpdatingBehavior buildStandardFormComponentUpdateBehavior(final String event, final String response)
	    {
	    return new AjaxFormComponentUpdatingBehavior(event)
		    {
	    	@Override
			protected void onUpdate(AjaxRequestTarget target)
				{
	    		switch (response)
					{
					case "updateCustomDirectory":
						target.add(container);
						break;
					}
				}
		    };
		}
	
	// issue 32
	protected AjaxCheckBox buildIncludeSampleName(String id, final WebMarkupContainer container)
	    {
	    AjaxCheckBox box = new AjaxCheckBox("includeResearcherId", new PropertyModel(worklist, "includeResearcherId"))
		    {
		    @Override
		    public void onUpdate(AjaxRequestTarget target)
			    {
				worklist.updateOutputFileNames();
				target.add(container);
			    }
		    };
	    return box;
	    }


	protected PagingNavigator buildPagingNavigator(String id, PageableListView listview)
		{
		return new PagingNavigator(id, listview, new IPagingLabelProvider()
			{
			@Override
			public String getPageLabel(long i)
				{
				return "    WL[" + (i + 1) + "]    ";
				}
			});
		}


	public WebMarkupContainer getContainer()
		{
		return container;
		}

	
	public PageableListView getWorklistView()
		{
		return worklistView;
		}


	// TO DO : Move to resizable dialog box
	private void setPageDimensions(ModalWindow modal1, double pctWidth, double pctHeight)
		{
		int pageHeight = ((MedWorksSession) getSession()).getClientProperties().getBrowserHeight();
		modal1.setInitialHeight(((int) Math.round(pageHeight * pctHeight)));
		int pageWidth = ((MedWorksSession) getSession()).getClientProperties().getBrowserWidth();
		modal1.setInitialWidth(((int) Math.round(pageWidth * pctWidth)));
		}

	
	public void addSibContainer(WebMarkupContainer c)
		{
		sibContainers.add(c);
		}

	
	abstract void initListItem(ListItem listItem, WorklistItemSimple item);
	}


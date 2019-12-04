///////////////////////////////////////
// SortableProjectPanel.java
// Written by Jan Wigginton August 2015
// Revisited : October 2016 (JW)
///////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.project.ProjectDetail2;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.panels.utilitypanels.ButtonColumn3;
import edu.umich.brcf.shared.panels.utilitypanels.ClickablePropertyColumn;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;



public class SortableProjectPanel extends Panel 
	{
	@SpringBean
	private ProjectService projectService;

	ModalWindow modal1;
	IModel projects = new LoadableDetachableModel() 
		{
		protected Object load() { return ((List<Project>)  projectService.allProjectsSmall()); }
		};
	
		
	public SortableProjectPanel(String id) 
		{
		super(id);
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		modal1 = new ModalWindow("modal1");
		add(modal1);
		
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
	        {
	        public void onClose(AjaxRequestTarget target) { target.add(container); }
	        });
		
		add(buildLinkToCreateModal("createProject",null, modal1));
		container.add(buildDataTable("table", modal1));
		}


	private DefaultDataTable <Project, String>  buildDataTable(String id, final ModalWindow modal)
		{	
		SortableProjectDataProvider cmpdProvider = new SortableProjectDataProvider(getProjects());
		
		List<IColumn<?, ?>> columns = new ArrayList<IColumn<?, ?>>();
		columns.add(getPropertyColumn("Project Name", "projectName", "projectName"));
		columns.add(getPropertyColumn("Project ID", "projectID", "projectID"));
		
		columns.add(new ButtonColumn3(new Model(""), "   Details...   ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget target)
				{
				doDetailClick(modal, clicked, target);
				}
			
			@Override 
			public String getCssClass() { return "borderColumn3"; }
			});
		
		columns.add(new ButtonColumn3(new Model(""), "   Edit...  ")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget arg0) 
				{
				doAttachClick(modal, clicked, arg0);
				}
			
			@Override
			public String getCssClass() { return "borderColumn3"; }
			});
		
		columns.add(new ButtonColumn3(new Model(""), "Attach Doc...")
			{
			@Override
			protected void onClick(IModel clicked, AjaxRequestTarget arg0) 
				{
				doUploadClick(modal, clicked, arg0);
				}
			@Override
			public String getCssClass() { return "borderColumn3"; }
			});
		
		DefaultDataTable table =  new DefaultDataTable(id, columns, cmpdProvider, 8000);
		//table.getTopToolbars().getBody().setVisible(false);
		//	table.addBottomToolbar(new NavigationToolbar(table));
		//	 table.addTopToolbar(new HeadersToolbar(table, cmpdProvider));
		
		return table;
		}

	
	private void doUploadClick(final ModalWindow modal, final IModel <Project>clicked, AjaxRequestTarget target)
		{
		modal.setInitialHeight(215);
		modal.setInitialWidth(820);
		
		modal.setPageCreator(new ModalWindow.PageCreator()
			{
			public Page createPage()
	        	{
	        	return new EditDocumentPage(getPage(), clicked.getObject().getProjectID(), modal, false, true);
	        	}
			});
	
		if (modal != null) modal.show(target);
		}
	
	
	private void doDetailClick(final ModalWindow modal, final IModel <Project>clicked, AjaxRequestTarget target)
		{
		modal1.setInitialWidth(625);
	    modal1.setInitialHeight(600);
	    
	    final Project withDocs = projectService.loadById(clicked.getObject().getProjectID());

	    modal1.setPageCreator(new ModalWindow.PageCreator()
    		{
            public Page createPage()
             	{
            	return (new ProjectDetail2(new Model(withDocs)));
             	}
    		});
    	
    	modal1.show(target);
		}
	
	
	private void doAttachClick(final ModalWindow modal1, final IModel <Project>clicked, AjaxRequestTarget target)
		{
		modal1.setInitialWidth(625);
	    modal1.setInitialHeight(660);
    
	    modal1.setPageCreator(new ModalWindow.PageCreator()
    		{
            public Page createPage()
             	{
             	Project wholeProject = projectService.loadById(clicked.getObject().getProjectID());
             	
             	IModel largeMod = new Model(wholeProject);
             	
            	return (new EditProject2(getPage(), largeMod, modal1)
	    		 	{
					@Override
					protected void onSave(Project project, AjaxRequestTarget target1) {   }
	    		 	});
             	}
    		});
    	
    	modal1.show(target);
    	}
	
	
	private AjaxLink buildLinkToCreateModal(final String linkID, final String projectId, final ModalWindow modal1) 
		{
		final Project project = ( projectId!=null ? projectService.loadById(projectId) : null);
		// issue 39
		return new AjaxLink <Void> (linkID)
        	{
            @Override
            public void onClick(final AjaxRequestTarget target)
            	{
            	modal1.setInitialWidth(625);
        	    modal1.setInitialHeight(600);

                modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                     	{
                		return (new EditProject2(getPage(), modal1)
                		 	{
 							@Override
							protected void onSave(Project project, AjaxRequestTarget target1) { }
                		 	});
	                 	}
                    
            		});
            	
            	modal1.show(target);
            	}
        	};
		}
	
	
	public IModel<List<Project>> getProjects() 
		{
		return projects;
		}
	
	
	
	public ClickablePropertyColumn <Project, ?> buildClickableProjectCol(String colTitle, final String property)
		{
		return new ClickablePropertyColumn<Project, String> (Model.of(colTitle), property, property)
			{
		    @Override
		    protected void onClick(final IModel<Project> project, AjaxRequestTarget target)
	        	{
	        	modal1.setInitialWidth(625);
	    	    modal1.setInitialHeight(600);
	    	    
	    	    final Project withDocs = projectService.loadById(project.getObject().getProjectID());
	 
	    	    modal1.setPageCreator(new ModalWindow.PageCreator()
	        		{
	                public Page createPage() { return (new ProjectDetail2(new Model(withDocs))); }
	        		});
	        	
	        	modal1.show(target);
	        	}
	   
			@Override
			public String getCssClass() { return "borderColumn3"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","width : 100%;  padding-top : 7px; font-size : 90%; color : blue; background : transparent; text-align : center; border-bottom : 1px groove white")); 
				return header;
				}
			};
		}
	

	private <T, S> PropertyColumn<?, ?> getPropertyColumn(final String label, final String  propertyExpression, String sortVar)
		{
		if (sortVar.equals(""))
			return new PropertyColumn(new Model<String>(label), propertyExpression)
				{
				@Override
				public String getCssClass() { return "borderColumn"; }
			
				@Override
				public Component getHeader(String componentId) 
					{
					Component header=super.getHeader(componentId);
					header.add(new AttributeModifier("style","color : text-decoration : underline; blue; background: red; width:100%; text-align : left;")); 
					return header;
					}
				};
				
	
		return new PropertyColumn(new Model<String>(label), propertyExpression, sortVar)
			{
			@Override
			public String getCssClass() { return "borderColumn"; }
			
			@Override
			public Component getHeader(String componentId) 
				{
				Component header=super.getHeader(componentId);
				header.add(new AttributeModifier("style","width: 100%;  padding-top : 7px; font-weight ; 700;  text-decoration : underline; color : blue; background : transparent ; text-align : left; font-size : 110%")); 
				return header;
				}
			};	
		}
	}



















/*
 * 
 * PageableListView listView;
		add(listView = new PageableListView("projects", new PropertyModel(this, "projects"), 20)
			{
			public void populateItem(final ListItem listItem)
				{
				final String projectStr = (String)listItem.getModelObject();
				String projectName = StringParser.parseName(projectStr);
				String projectId = StringParser.parseId(projectStr);
				
				listItem.add(buildLinkToModal("projectLink",  projectId, modal1, null).add(new Label("projectLbl", 
						new Model(projectName))));
				listItem.add(buildLinkToModal("projectLink2",  projectId, modal1, null).add(new Label("projectLbl2", 
						new Model(projectId))));
				
				listItem.add(DialogLinkBuilder.buildLinkToDocumentModal("uploadDoc", projectId, modal1));
				
				// SLOP CODE -- FIGURE OUT A BETTER WAY....
				listItem.add(buildLinkToModal("edit", projectId, modal1, (SortableProjectPanel) getParent()));
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
		add(new PagingNavigator("navigator", listView));

 *	//ClickablePropertyColumn<Ms2PeakSet> clickableLipid;
	//columns.add(clickableLipid = buildClickableLipidCol());
	
	//columns.add(getPropertyColumn("Start_Mass", "startMass", this.dataHandler.hasStartMass ? "startMass" : ""));
	
	//if (dataHandler.hasEndMass)
	//	columns.add(getPropertyColumn("End_Mass", "endMass", dataHandler.hasEndMass ? "endMass" : ""));
	
	//if (dataHandler.hasRt)
	//	columns.add(getPropertyColumn("Expected_RT", "expectedRt", dataHandler.hasRt ?  "expectedRt" : ""));
	//columns.add(getPropertyColumn("Lipid_Class", "lipidClass", "lipidClass"));
	//columns.add(getPropertyColumn("Known_Status", "knownStatus", "knownStatus"));
	
	//for (int j = 0; j < colNames.size();  j++)
	//	{
	//	String propertyExpression =  "samplePeaks." + j + ".peakArea";
	//	columns.add(getPropertyColumn(dataHandler.tableColumnLabels.get(j) + "", propertyExpression, propertyExpression));
	//	}

 *
 /*
	public DefaultDataTable <Ms2PeakSet, String>  buildDataTable(String id, List<String> colNames, List<String> varNames)
	{	
	SortableMs2ResultDataProvider cmpdProvider = new SortableMs2ResultDataProvider(dataHandler.getDataSet().getPeakSets());
	List<IColumn<?, ?>> columns = new ArrayList<IColumn<?, ?>>();
	ClickablePropertyColumn<Ms2PeakSet, ?> clickablePeakSet;
	
	if (dataAlreadyLoaded)
		columns.add(clickablePeakSet = buildClickablePeakSetCol());
	
	ClickablePropertyColumn<Ms2PeakSet, ?> clickableLipid;
	columns.add(clickableLipid = buildClickableLipidCol());
	
	if (dataHandler.hasStartMass)
		columns.add(getPropertyColumn("Start_Mass", "startMass", this.dataHandler.hasStartMass ? "startMass" : ""));
	
	if (dataHandler.hasEndMass)
		columns.add(getPropertyColumn("End_Mass", "endMass", dataHandler.hasEndMass ? "endMass" : ""));
	
	if (dataHandler.hasRt)
		columns.add(getPropertyColumn("Expected_RT", "expectedRt", dataHandler.hasRt ?  "expectedRt" : ""));
	
	columns.add(getPropertyColumn("Lipid_Class", "lipidClass", "lipidClass"));
	columns.add(getPropertyColumn("Known_Status", "knownStatus", "knownStatus"));
	
	for (int j = 0; j < colNames.size();  j++)
		{
		String propertyExpression =  "samplePeaks." + j + ".peakArea";
		columns.add(getPropertyColumn(dataHandler.tableColumnLabels.get(j) + "", propertyExpression, propertyExpression));
		}
	
	DefaultDataTable table = new DefaultDataTable(id, columns, cmpdProvider, 8000);
	
	//table.addTopToolbar(new NavigationToolbar(table));
	return table;
	}
	//ClickablePropertyColumn<Ms2PeakSet, ?> clickablePeakSet;
	//columns.add(clickablePeakSet = buildClickablePeakSetCol()); */
 


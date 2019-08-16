////////////////////////////////////////////
// Written by Anu Janga
// Rewritten January 2015  (JW)
////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.project.obsolete;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.panels.utilitypanels.EditDocumentPage;
import edu.umich.brcf.shared.util.ModalSizes;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class ProjectPanel extends Panel 
	{
	@SpringBean
	private ProjectService projectService;
	
	
	public ProjectPanel(String id) 
		{
		super(id);
		}}
/*
		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.setOutputMarkupId(true);
		add(container);
		
		final ModalWindow modal1= new ModalWindow("modal1"); 
		modal1.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() 
			{
			@Override
			public void onClose(AjaxRequestTarget target)  {  target.add(container);  }
			});
		add(modal1);
	    
		setOutputMarkupId(true);
		
		PageableListView listView;
		container.add(listView = new PageableListView("projects", new PropertyModel(this, "projects"), 28)
			{
			public void populateItem(final ListItem listItem)
				{
				final String projectStr = (String)listItem.getModelObject();
				String projectName = StringParser.parseName(projectStr);
				String projectId = StringParser.parseId(projectStr);
				
				listItem.add(buildLinkToModal("projectLink",  projectId, modal1, null).add(new Label("projectLbl",  new Model(projectName))));
				listItem.add(buildLinkToModal("projectLink2",  projectId, modal1, null).add(new Label("projectLbl2", new Model(projectId))));
				
				//listItem.add(DialogLinkBuilder.buildLinkToDocumentModal("uploadDoc", projectId, modal1));
				listItem.add(buildLinkToModal("uploadDoc", projectId, modal1, (ProjectPanel) getParent().getParent()));
				listItem.add(buildLinkToModal("edit", projectId, modal1, (ProjectPanel) getParent().getParent()));
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}
			});
	
		container.add(new PagingNavigator("navigator", listView));
		container.add(buildLinkToModal("createProject",null, modal1, this));
		}


	private AjaxLink buildLinkToModal(final String linkID, final String projectId, final ModalWindow modal1,  final ProjectPanel panel) 
		{
		final Project project = ( projectId!=null ? projectService.loadById(projectId) : null);
		
		return new AjaxLink(linkID)
        	{
            @Override
            public void onClick(final AjaxRequestTarget target)
            	{
            	modal1.setInitialWidth(625);
        	    modal1.setInitialHeight((linkID.startsWith("ed") || linkID.startsWith("cr")) ?  600 : 360);
        	    if (linkID.startsWith("upload)"))
        	    	{
    	    		modal1.setInitialWidth(ModalSizes.DOC_DIALOG_WIDTH);
    	    		modal1.setInitialHeight(ModalSizes.DOC_DIALOG_HEIGHT);
        	    	}
        	    
                modal1.setPageCreator(new ModalWindow.PageCreator()
            		{
                    public Page createPage()
                     	{
                    	if(linkID.startsWith("cr"))
                    		{
                    		return (new EditProject2(getPage(), modal1)
	                		 	{
	 							@Override
								protected void onSave(Project project, AjaxRequestTarget target1)  {   } 
	                		 	});
	                		}
	                	 
                    	if (linkID.startsWith("ed"))
                    		{
                    		return (new EditProject2(getPage(), new Model(project), modal1)
                    			{
     							@Override
    							protected void onSave(Project project, AjaxRequestTarget target1)  {  } 
                    			});
                    		}
                    	
                    	if (linkID.startsWith("upload"))
                        	return new EditDocumentPage(getPage(),projectId, modal1, false, true);
                    	
                    	 
                    	return (new ProjectDetail2(new Model(project)));
                     	}
            		});
            	
            	modal1.show(target);
            	}
        	};
		}
	
	public List<String> getProjects() 
		{
		return projectService.allProjectNamesByName();
		}
	}




*/
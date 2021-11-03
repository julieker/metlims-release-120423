////////////////////////////////////////////////////
// ProjectExperimentsPanel.java
// Revisited by Jan Wigginton, August 7, 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.util.ArrayList;
import java.util.List;


import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.panels.login.MedWorksSession;



public abstract class ProjectExperimentsPanel extends Panel
	{
	ListView listView;
	List<Project> projectList;
	// issue 187
	List <Experiment> expAssayDateCritList;
	Project project;
	public ProjectExperimentsPanel(String id) 
		{
		super(id);
		setOutputMarkupId(true);
		listView = new ListView("projectList",new PropertyModel(this, "projectList")) 
			{
			// issue 187
			public List <Experiment> getExpAssayDateCritList ()
				{
				List <Experiment> experiments = project.getExperimentList();
				List <Experiment> expToReturn = new ArrayList <Experiment> ();
				if (experiments.size() > 0)
					expToReturn.add(experiments.get(0));
				return expToReturn;
				}
		   // issue 187
			public void setExpAssayDateCritList (List <Experiment> vexpAssayDateCritList)
				{
				expAssayDateCritList = vexpAssayDateCritList;
				}
			
			public void populateItem(final ListItem listItem) 
				{
				// issue 39
				project = (Project) listItem.getModelObject();
				
				listItem.add(new Label("projname",project.getProjectName() + " (" + project.getStartDateString() + ")"));
				
				// issue 187
				if (((MedWorksSession) Session.get()).getExpProjmap().size() > 0)
					project.setExpAssayDateCritList( ((MedWorksSession) Session.get()).getExpProjmap().get(project.getProjectID()));
				else 
					project.setExpAssayDateCritList( project.getExperimentList());		
				ListView childListView = new ListView("expList",new PropertyModel(project, "expAssayDateCritList")) 
								
				    {
					public void populateItem(final ListItem childListItem) {
					final Experiment exp = (Experiment) childListItem.getModelObject();
					childListItem.add(new IndicatingAjaxLink<Void>("expLink")
						{
						@Override
						public void onClick(AjaxRequestTarget target)
							{
							ProjectExperimentsPanel.this.onExpLinkClicked(exp.getExpID(), target);
							}
						}.add(new Label("expname", exp.getExpName())));
					}
				};
			listItem.add(childListView);
			}};

		add(listView);
		}
	public List<Project> getProjectList()
		{
		return projectList;
		}
	
	public void setProjectList(List<Project> projectList)
		{
		this.projectList=projectList;
		}
	
	protected abstract void onExpLinkClicked(String expId, AjaxRequestTarget target);
	}

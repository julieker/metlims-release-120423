////////////////////////////////////////////////////
// ProjectExperimentsPanel.java
// Revisited by Jan Wigginton, August 7, 2015
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;



public abstract class ProjectExperimentsPanel extends Panel
	{
	ListView listView;
	List<Project> projectList;
	
	public ProjectExperimentsPanel(String id) 
		{
		super(id);
		setOutputMarkupId(true);

		listView = new ListView("projectList",new PropertyModel(this, "projectList")) 
			{
			public void populateItem(final ListItem listItem) 
				{
				// issue 39
				final Project project = (Project) listItem.getModelObject();
				
				listItem.add(new Label("projname",project.getProjectName() + " (" + project.getStartDateString() + ")"));
				ListView childListView = new ListView("expList",new PropertyModel(project, "experimentList")) 
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

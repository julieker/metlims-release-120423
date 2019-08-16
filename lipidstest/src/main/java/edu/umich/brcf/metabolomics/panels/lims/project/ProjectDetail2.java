////////////////////////////////////////////
// Written by Anu Janga
// Cleaned up for upgrade August 2016  (JW)
////////////////////////////////////////////

package edu.umich.brcf.metabolomics.panels.lims.project;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.ProjectDocument;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.widgets.MyFileLink;



public class ProjectDetail2 extends WebPage
	{
	@SpringBean
	private ExperimentService experimentService;

	public ProjectDetail2(IModel projectModel) 
		{
		Project project = (Project) projectModel.getObject();
		
		add(new Label("id2", project.getProjectID()));
		add(new Label("projectName", project.getProjectName()));
		add(new Label("description", project.getDescription()));
		add(new Label("client", project.getClient().getLab()));
		add(new Label("contactPerson", project.getContactPerson().getFullName()));
		add(new Label("statusID", project.getStatusID()));
		
		Calendar stDate = project.getStartDate();

		String StDateStr = "";
		if (stDate != null)
			{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			StDateStr = sdf.format(stDate.getTime());
			}
		
		add(new Label("startDate", StDateStr));
		add(new Label("notes", project.getNotes()));
		add(new ListView("docs", project.getDocList()) 
			{
			@Override
			protected void populateItem(ListItem item) 
				{
				final ProjectDocument doc = (ProjectDocument) item.getModelObject();
				
				MyFileLink link = new MyFileLink("fileLink", new Model(doc));
				link.add(new Label("fileName", doc.getFileName()));
				item.add(link);
				}
			});
		}
	
	public List<Experiment> getExperiments(Project project) 
		{
		return experimentService.loadExperimentByProject(project);
		}
	}

////////////////////////////////////////////////////
// GenericSearchByAnythingPanel.java
// Written by Jan Wigginton, Sep 8, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.utilitypanels;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.SearchTypeDropDown;



public abstract class GenericSearchByAnythingPanel extends Panel
	{ 
	public GenericSearchByAnythingPanel(String id)
		{
		super(id);
		}
	
	}
	/*
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	SampleService sampleService;
	
	private String searchTypeOuter = "Experiment", searchLabel = "";
	WebPage backPage;
	
	public GenericSearchByAnythingPanel(String id, WebPage backPage, String searchLabel)
		{
		super(id);
		this.searchLabel = searchLabel;
		this.backPage = backPage;
		add(new Label("searchLabel", new PropertyModel<String>(this, "searchLabel")));
		add(new GenericSearchForm("experimentSearchForm", new Model(this)));
		}
	
	public class GenericSearchForm extends Form 
		{
		Model<ExperimentSearchByAnythingPanel> panelModel;
		
		private Experiment experiment;
		private List<Project> projectList;
		
		NewSearchForExperimentPanel expPanel;
		NewSearchForProjectPanel  projPanel;
		NewSearchForContactPanel contactPanel;
		NewSearchForSamplePanel samplePanel;
		NewSearchForOrganizationPanel orgPanel;
		
		public GenericSearchForm(final String id, final Model<ExperimentSearchByAnythingPanel> panelModel)
			{
			super(id);
			setOutputMarkupId(true);
			
			this.panelModel = panelModel;
			
			final WebMarkupContainer container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);
			add(container);
			
			SearchTypeDropDown searchDrop = buildSearchTypeDrop("searchType", container);
			searchDrop.setOutputMarkupId(true);
			container.add(searchDrop);
			
			final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 650, 400);
			container.add(modal1); 
			container.add(buildLinkToModal("details",modal1).setOutputMarkupId(true));
			
			expPanel = buildSearchForExperimentPanel("searchForExperimentPanel");
			expPanel.setOutputMarkupId(true);
			expPanel.setShowButton(false);
			container.add(expPanel);
			
			contactPanel = buildSearchForContactPanel("searchForContactPanel");
			contactPanel.setOutputMarkupId(true);
			container.add(contactPanel);
			
			projPanel = buildSearchForProjPanel("searchForProjectPanel");
			projPanel.setOutputMarkupId(true);
			container.add(projPanel);
			
			orgPanel = buildSearchForOrganizationPanel("searchForOrganizationPanel");
			container.add(orgPanel);
			orgPanel.setOutputMarkupId(true);
			
			samplePanel = buildSearchForSamplePanel("searchForSamplePanel");
			container.add(samplePanel);
			samplePanel.setOutputMarkupId(true);
			
			container.add(new AjaxCancelLink("cancelButton", modal1));
			}
		
		
		public SearchTypeDropDown buildSearchTypeDrop(String id, final WebMarkupContainer container)
			{
			return new SearchTypeDropDown("searchType",this, "searchTypeOuter")
				{
				@Override
				protected void doUpdateBehavior(AjaxRequestTarget target)
					{
					switch(searchTypeOuter)
						{
						case "Experiment Id" : expPanel.setSearchType("Id"); target.add(expPanel); break;
						case "Experiment Name" : expPanel.setSearchType("Name"); target.add(expPanel);  break;
						case "Project Id" : projPanel.setSearchType("Id"); target.add(projPanel); break; 
						case "Project Name" : projPanel.setSearchType("Name"); target.add(projPanel);  break;
						case "Contact Id" : contactPanel.setSearchType("Id"); target.add(contactPanel); break;
						case "Contact Name" : contactPanel.setSearchType("Name"); target.add(contactPanel);  break;
						case "Organization Id" : orgPanel.setSearchType("Id"); target.add(orgPanel);  break;
						case "Organization Name" : orgPanel.setSearchType("Name"); target.add(orgPanel);  break;
						case "Sample Id" : samplePanel.setSearchType("Id"); target.add(samplePanel);break;
						default : 
						}
						
					target.add(expPanel);
					target.add(projPanel);
					target.add(projPanel.getContainer());
					
					target.add(contactPanel);
					target.add(orgPanel);
					target.add(samplePanel);
					target.add(this);
					target.add(container);
					}
				};
			}	
		
		
		public NewSearchForExperimentPanel buildSearchForExperimentPanel(String id)
			{
			return new  NewSearchForExperimentPanel(id)
				{
				@Override
				protected void onSelect(String expId, AjaxRequestTarget target)
					{
					doExperimentSelect(expId, target);
					}
			
				@Override 
				public boolean isVisible() {  return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Experiment"));   }
				};
			}
		
		
		NewSearchForContactPanel buildSearchForContactPanel(String id)
			{
			return new NewSearchForContactPanel(id)
				{
				@Override
				protected void onSelect(String contact, AjaxRequestTarget target)
					{
					doContactSelect(contact, target);
					}
				
				@Override 
				public boolean isVisible()
					{
					return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Contact"));
					}
				};
			}
		
		
		NewSearchForProjectPanel buildSearchForProjPanel(String id)
			{
			return new NewSearchForProjectPanel(id)
				{
				
				
				@Override 
				public boolean isVisible() { return getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Project");}

				@Override
				protected void doError(String msg, AjaxRequestTarget target)
					{
					// TODO Auto-generated method stub
					
					}

				@Override
				protected void onSelect(String project, AjaxRequestTarget target)
					{
					doProjectSelect(project, target);
					}
				};
			}
		
		
		public NewSearchForSamplePanel buildSearchForSamplePanel(String id)
			{
			return new NewSearchForSamplePanel(id)
				{
				@Override
				protected void onSelect(String sample, AjaxRequestTarget target)
					{
					doSampleSelect(sample, target);
					}
				
				@Override 
				public boolean isVisible() { return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Sample")); }
				};
			}
		
		
		public NewSearchForOrganizationPanel buildSearchForOrganizationPanel(String id)
			{
			return new NewSearchForOrganizationPanel(id)
				{
				@Override
				protected void onSelect(String orgId, AjaxRequestTarget target)
					{
					doOrganizationSelect(orgId, target);
					}
					
				@Override 
				public boolean isVisible() { return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Organization")); }
				};
			}
		
		
		public Experiment getExperiment()
			{
			return experiment;
			}
		
		
		public void setExperiment(Experiment experiment)
			{
			this.experiment = experiment;
			}
		
		
		public List<Project> getProjectList()
			{
			return projectList;
			}
			
		
		public void setProjectList(List<Project> projectList)
			{
			this.projectList = projectList;
			}
		
		
		public String getSearchTypeOuter()
			{
			return searchTypeOuter;
			}
		
		
		public void setSearchTypeOuter(String st)
			{
			searchTypeOuter = st;
			}
		
		
		private IndicatingAjaxLink buildLinkToModal(final String linkID, final ModalWindow modal1)
			{
			return new IndicatingAjaxLink(linkID)
				{
				@Override
				public void onClick(AjaxRequestTarget target)
					{
					modal1.setInitialWidth(1400);
					modal1.setInitialHeight(1300); 
					
					modal1.setPageCreator(new ModalWindow.PageCreator()
						{
						public Page createPage()
							{
							return grabResponsePage(modal1);
							}
					});
			
					modal1.show(target);
					}	
				};
			}
	
		
		
		@Override
		public void onSubmit()
			{ 
			doOverallSubmit();
			}
		
		public String getSearchLabel()
			{
			return searchLabel;
			}
		public void setSearchLabel(String sl)
			{
			searchLabel = sl;
			}
		}
	
	
	public String getSearchLabel()
		{
		return searchLabel;
		}
	public void setSearchLabel(String searchLabel)
		{
		this.searchLabel = searchLabel;
		}
	
	protected abstract void doOverallSubmit();
	protected abstract void doExperimentSelect(String expId, AjaxRequestTarget target);
	protected abstract void doContactSelect(String contact, AjaxRequestTarget target);
	protected abstract void doProjectSelect(String projId, AjaxRequestTarget target);
	protected abstract void doSampleSelect(String sampleId, AjaxRequestTarget target);
	protected abstract void doOrganizationSelect(String orgId, AjaxRequestTarget target);
	protected abstract Page grabResponsePage(final ModalWindow modal);
	}


/*grabResponsePage
 * 
 * switch(linkID)
								{
								case "details" :  
									default : return new NewExperimentPage("testpage", experiment != null ? experiment.getExpID() : "EX00413", getProjIds(), modal1); 
								}
 * */
/*
 * doOrganizationSelect
 * List<Project> lst = projectService.getProjectExperimentsForOrganization(orgId);
					Experiment exp = null;
					for (Project proj : lst)
						{
						List<Experiment> expList = proj != null ? proj.getExperimentList() : null;
						if (ListUtils.isNonEmpty(expList))
							{
							exp = expList.get(0);
							break;
							}
						}
					
					setProjectList(lst);
					setExperiment(exp);
 * 
 * doProjectSelect
 * 	projectList = new ArrayList<Project>();
					projectList.add(projectService.getProject(project));
					setProjectList(projectList);
					
					List<Experiment> expList = projectList.get(0).getExperimentList();
					setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
				
				*/
/* doCOntactSelect : List<Project> projList = projectService.loadProjectExperimentByClientContact(contact);
					setProjectList(projList);
					if (ListUtils.isNonEmpty(projList))
					{
					List<Experiment> expList = projectList.get(0).getExperimentList();
					setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);

/*doExperimentSelect
 * 	experiment =  experimentService.loadById(expId);
					projectList = new ArrayList<Project>();
					projectList.add(projectService.getProject(experiment.getProject().getProjectID()));
					setExperiment(experimentService.loadById(experiment.getExpID()));
				*/

/*doSampleSelect
 * 	projectList = new ArrayList<Project>();
					Experiment e = (sampleService.loadById(sample)).getExp();
					projectList.add(projectService.getProject(e.getProject().getProjectID()));
					setProjectList(projectList);
					setExperiment(experimentService.loadById(e.getExpID()));
				
 * 
 * */

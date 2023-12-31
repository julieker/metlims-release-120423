////////////////////////////////////////////////////
// ExperimentSearchByAnythingPage.java
// Written by Jan Wigginton, Aug 30, 2016
////////////////////////////////////////////////////

package edu.umich.brcf.shared.panels.utilitypanels.experimentsearch;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.utilitypanels.ModalCreator;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;
import edu.umich.brcf.shared.util.widgets.AjaxCancelLink;
import edu.umich.brcf.shared.util.widgets.SearchTypeDropDown;
public abstract class ExperimentSearchByAnythingPage extends  WebPage
	{
	@SpringBean
	ExperimentService experimentService;
	
	@SpringBean
	ProjectService projectService;
	
	@SpringBean
	SampleService sampleService;
	
	@SpringBean
	OrganizationService organizationService;
	
	@SpringBean
	ClientService clientService;
	
	@SpringBean
	AssayService assayService;	
	private Boolean isValid = false;
	private String searchTypeOuter = "Experiment";
	private Page backPage;
	private FeedbackPanel feedback;
	private AjaxSubmitLink searchLink;
	public ExperimentSearchForm experimentSearchForm;
	public ExperimentSearchByAnythingPage experimentSearchByAnythingPage = this;
	public GrabAssayLabelPanel assayPanel;
    WebMarkupContainer spinContainer; // issue 187
    WebMarkupContainer spinner; // issue 187
    
//	boolean isSpinner = false;
	public ExperimentSearchByAnythingPage(Page webPage)
		{
		this.backPage = webPage;
		add(experimentSearchForm = new ExperimentSearchForm("experimentSearchForm")); 
		}
	
	public final class ExperimentSearchForm extends Form 
		{
		Boolean searchIsValid = false;
		
		private Experiment experiment;
		private List<Project> projectList;
	
		GrabExperimentLabelPanel expPanel;
		GrabProjectLabelPanel  projPanel;
		GrabContactLabelPanel contactPanel;
		
		GrabPILabelPanel piPanel;
		GrabOrganizationLabelPanel orgPanel;
		AjaxCancelLink cancelLink;
		
		
		public ExperimentSearchForm(final String id) 
			{
			super(id);
			setOutputMarkupId(true);
			
			spinContainer = new WebMarkupContainer("spinContainer")
				{
				/*public boolean isVisible()
					{
					return isSpinner;
					}*/
			     };
		    add(spinContainer);
			spinContainer.setOutputMarkupId(true);
			spinContainer.setOutputMarkupPlaceholderTag(true);	
			
			spinner= new WebMarkupContainer("spinner")
			{
			/*public boolean isVisible()
				{
				return isSpinner;
				}*/
		     };
		    spinContainer.add(spinner);
			spinner.setOutputMarkupId(true);
			spinner.setOutputMarkupPlaceholderTag(true);	
			
			final WebMarkupContainer container = new WebMarkupContainer("container");
			container.setOutputMarkupId(true);
			add(container);
			
			container.add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
			
			SearchTypeDropDown searchDrop = buildSearchTypeDrop("searchType", container);
			searchDrop.setOutputMarkupId(true);
			container.add(searchDrop);
			
			final ModalWindow modal1= ModalCreator.createModalWindow("modal1", 650, 400);
			container.add(modal1); 
		
			container.add(searchLink = buildSearchLink("searchLink",modal1));
			searchLink.setOutputMarkupId(true);
			// issue 187
		    searchLink.add(( new AttributeModifier("onclick", buildHtmlProgress())));	
						
		    expPanel = buildSearchByExperimentPanel("searchForExperimentPanel");
			expPanel.setOutputMarkupId(true);
			container.add(expPanel);
				
			contactPanel = buildSearchByContactPanel("searchForContactPanel");
			contactPanel.setOutputMarkupId(true);
			container.add(contactPanel);
				
			projPanel = buildSearchByProjPanel("searchForProjectPanel");
			projPanel.setOutputMarkupId(true);
			container.add(projPanel);
			
			orgPanel = buildSearchByOrganizationPanel("searchForOrganizationPanel");
			container.add(orgPanel);
			orgPanel.setOutputMarkupId(true);
			
			// issue 181
			piPanel = buildSearchByPIPanel("searchForPIPanel");
			container.add(piPanel);
			piPanel.setOutputMarkupId(true);
			
			assayPanel = buildSearchByAssayPanel("searchForAssayPanel");
			container.add(assayPanel);
			assayPanel.setOutputMarkupId(true);		
			
	//		samplePanel = buildSearchForSamplePanel("searchForSamplePanel");
	//		container.add(samplePanel);
	//		samplePanel.setOutputMarkupId(true);
			
			container.add(cancelLink = new AjaxCancelLink("cancelButton", modal1)
				{
				public boolean isVisible()
					{
					return searchIsValid; 
					}
				});
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
						case "Contact Name" : contactPanel.setSearchType("Name"); target.add(contactPanel);  break;
						case "Organization Name" : orgPanel.setSearchType("Name"); target.add(orgPanel);  break;
						case "Assay Id" : assayPanel.setSearchType("Id");
						default : 
						}
				
					target.add(expPanel);
					target.add(projPanel);
					target.add(contactPanel);
					target.add(orgPanel);
					target.add(this);
					target.add(container);
					}
				};
			}	
			
	
		public GrabExperimentLabelPanel buildSearchByExperimentPanel(String id)
			{
			return new  GrabExperimentLabelPanel(id)
				{
				@Override
				protected void onSelect(String str, AjaxRequestTarget target)
					{
					try
						{
						String expId = experimentService.getExperimentIdForSearchString(str, "with name");
						experiment =  experimentService.loadByIdWithProject(expId);
						isValid = true;
						target.add(searchLink);
						projectList = new ArrayList<Project>();
						projectList.add(projectService.getProject(experiment.getProject().getProjectID()));
						}	
					catch (Exception e) 
						{ 
						doError(e.getMessage(), target);
						}
					}
				@Override 
				public boolean isVisible() {  return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Experiment"));   }
				};
			}
		
		// issue 187
		public GrabAssayLabelPanel buildSearchByAssayPanel(String id)
			{
			return new  GrabAssayLabelPanel(id)
				{
				@Override
				protected void onSelect(String str, String createDate, String createDateTo, AjaxRequestTarget target)
					{
					try
						{
						isValid=true;
						List <Project> projList = projectService.loadProjectExperimentByAssay(str);
						setProjectList(projList);
						
						if (ListUtils.isNonEmpty(projList))
							{
							List<Experiment> expList = projectList.get(0).getExperimentList();
							setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
							}
						target.add(searchLink);
						}	
					catch (Exception e) 
						{ 
						doError(e.getMessage(), target);
						}
					}
				@Override 
				public boolean isVisible() {  return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Assay"));   }
				};
			}
		
		
		GrabContactLabelPanel buildSearchByContactPanel(String id)
			{
			return new GrabContactLabelPanel(id)
				{
				@Override
				protected void onSelect(String contact, AjaxRequestTarget target)
					{
					try
						{
						isValid = true; //clientService.verifyContactExists(contact);
						target.add(searchLink);
						// issue 181
						List<Project> projList = projectService.loadProjectExperimentByContact(contact);
						setProjectList(projList);
						if (ListUtils.isNonEmpty(projList))
							{
							List<Experiment> expList = projectList.get(0).getExperimentList();
							setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
							}
						}
					catch (Exception e) { doError(e.getMessage(), target); }
					}
				
				@Override 
				public boolean isVisible()
					{
					return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Contact"));
					}
				};
			}
	
		//// issue 181
		GrabPILabelPanel buildSearchByPIPanel(String id)
			{
			return new GrabPILabelPanel(id)
				{
				@Override
				protected void onSelect(String contact, AjaxRequestTarget target)
					{
					try
						{
						isValid = true; //clientService.verifyContactExists(contact);
						target.add(searchLink);
						// issue 181
						List<Project> projList = projectService.loadProjectExperimentByClientContact(contact);
						setProjectList(projList);
						if (ListUtils.isNonEmpty(projList))
							{
							List<Experiment> expList = projectList.get(0).getExperimentList();
							setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
							}
						}
					catch (Exception e) { doError(e.getMessage(), target); }
					}
				
				@Override 
				public boolean isVisible()
					{
					return (getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Principal"));
					}
				};
			}
		
		GrabProjectLabelPanel buildSearchByProjPanel(String id)
			{
			return new GrabProjectLabelPanel(id)
				{
				@Override
				protected void onSelect(String input, AjaxRequestTarget target)
					{
					try
						{
					//	String projectId  = projectService.getProjectIdForSearchString(input, "with name");
						isValid = true;
						target.add(searchLink);
						String project = projectService.getProjectIdForSearchString(input, "project name");
						projectList = new ArrayList<Project>();
						projectList.add(projectService.getProject(project));
						setProjectList(projectList);
						
						List<Experiment>  expList = null;
						if (projectList.size() > 0)
							expList = projectList.get(0).getExperimentList();
						
						setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
						}
					catch (Exception e) { doError(e.getMessage(), target); }
					}
					
				@Override 
				public boolean isVisible() { return getSearchTypeOuter() != null && getSearchTypeOuter().startsWith("Project");}
				};
			}
		
		public GrabOrganizationLabelPanel buildSearchByOrganizationPanel(String id)
			{
			return new GrabOrganizationLabelPanel(id)
				{
				@Override
				protected void onSelect(String input, AjaxRequestTarget target)
					{
					String orgId  = organizationService.getOrganizationIdForSearchString(input, "with name");
					isValid = true;
					target.add(searchLink);
					
					List<Project> lst = projectService.getProjectExperimentsForOrganization(orgId);
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
		
		
		private AjaxSubmitLink buildSearchLink(final String linkID, final ModalWindow modal1)
			{
			return new AjaxSubmitLink(linkID)
				{
			    @Override
		       public void onSubmit(AjaxRequestTarget target)// issue 464
			       	{	
			    	try
				    	{
				    	List <Project> projList = new ArrayList <Project> ();
				    	if (searchTypeOuter.equals("Assay Id"))
				    		{
				    		if (!assayPanel.grabAssayLabelForm.getUseDate())
				    			{
				    			projList = projectService.loadProjectExperimentByAssay(assayPanel.grabAssayLabelForm.getAssay());
				    			}
				    		else 
				    			{
				    			if (StringUtils.isEmptyOrNull(assayPanel.grabAssayLabelForm.getCreateDate()) || StringUtils.isEmptyOrNull(assayPanel.grabAssayLabelForm.getCreateDateTo()))
				    				{
				    			    target.add(experimentSearchByAnythingPage);
				    			    ExperimentSearchByAnythingPage.this.info("Enter in Start and End Date");
				    			    return;
				    				}
				    			projList = projectService.loadProjectExperimentByAssay(assayPanel.grabAssayLabelForm.getAssay(),   assayPanel.grabAssayLabelForm.getCreateDate() ,assayPanel.grabAssayLabelForm.getCreateDateTo());
				    			}
				    		
				    		if (projList.size() == 0)
				    			{
				    			target.add(experimentSearchByAnythingPage);
				    			ExperimentSearchByAnythingPage.this.info("No records match your criteria.  Please try another date range or Assay");
			    				return;
				    			}
				    		setProjectList(projList);
							if (ListUtils.isNonEmpty(projList))
								{
								List<Experiment> expList = projectList.get(0).getExperimentList();
								setExperiment(ListUtils.isNonEmpty(expList) ?  expList.get(0) : null);
								}	
				    		}			    			
				    	if (experiment == null)
			      			{
			      			isValid = false;
			      			target.add(searchLink);
			      			return;
			      			}		      	
			      		isValid = true;	 
			      		target.add(searchLink);		      	    
			      		ExperimentSearchByAnythingPage.this.doBusiness(experiment.getExpID() , getProjIds());
						experiment = null;
						modal1.close(target);
				    	}
			    	catch (Exception e)
				    	{
				    	e.printStackTrace();	
				    	}
			       	}			    
			    @Override
				 public boolean isEnabled()
					 {
					 return isValid;
					 }
			    @Override
				 public void onError(AjaxRequestTarget target)
					 {
			    	 target.add(experimentSearchByAnythingPage);
	    			 ExperimentSearchByAnythingPage.this.info("There was an error.  Please make sure you have entered a valid date range....");;
    				 return;
					 }
				};				 
			}
		
		private List<String> getProjIds()
			{
			List<String> pids = new ArrayList<String>();
			if (getProjectList() == null)
				return pids;
			
			for (Project proj : getProjectList())
				pids.add(proj.getProjectID());
			
			return pids;
			}
		
		
		protected void doSearchingUpdate(AjaxRequestTarget target)
			{
			ExperimentSearchByAnythingPage.this.info("Looking for item");
			target.add(feedback);
			}
		
		protected void doError(String msg, AjaxRequestTarget target)
			{
			isValid = false;
			experiment = null;
			ExperimentSearchByAnythingPage.this.error(msg);
			//target.add(feedback);
			}
		
		private String buildHtmlProgress()
			{
			String htmlStr = "";
			htmlStr = htmlStr + " document.getElementById(" + "\"" + "spinner" + "\")" + ".style.display = 'block';";
			htmlStr = htmlStr + " document.getElementById(" + "\"" + "spinnertext" + "\")" + ".style.display = 'block';";
			return htmlStr;
			}
		}
		
	protected abstract void doBusiness(String expId, List<String> projIds);
	}






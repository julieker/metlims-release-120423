////////////////////////////////////////////////////
// NewSearchForProjectPanel.java
// Written by Jan Wigginton, Aug 6, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.shared.panels.experimentsearch.obsolete;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.widgets.AjaxExperimentField;
import edu.umich.brcf.shared.util.widgets.AjaxProjectField;


public abstract class NewSearchForProjectPanel extends Panel
{
@SpringBean 
private ProjectService projectService;

private String project, input, searchType = "Id";
private Boolean showButton = false;
WebMarkupContainer container;

public NewSearchForProjectPanel(String id)
	{
	super(id);
	
	add(new FeedbackPanel("feedback").setOutputMarkupId(true));
	add(new NewSearchForProjectForm("searchByProjectForm"));
	}



public final class NewSearchForProjectForm extends Form 
	{
	final IndicatingAjaxButton selectButton;
	public NewSearchForProjectForm(final String id)
		{
		super(id);
	
		container = new WebMarkupContainer("container"); 
		container.setOutputMarkupId(true);
		add(container);
		
		final AjaxProjectField projNameField;
		projNameField = new AjaxProjectField("project")
			{
			@Override
			public boolean isVisible()  { return "Name".equals(getSearchType()); }
			};
			
		projNameField.setOutputMarkupId(true);
		projNameField.add(buildFormSubmitBehavior(false, projNameField));
		container.add(projNameField);
		
		container.add(selectButton = buildSelectButton("selectButton", projNameField));
		
		DropDownChoice<String> projDrop;
		LoadableDetachableModel <List<String>> projListModel = new LoadableDetachableModel<List<String>>() 
			{
			@Override
			protected List<String> load() 
				{
				return projectService.projectIdsByStartDate();
				}
			};
							
		container.add(projDrop = new DropDownChoice<String>("projDrop", new PropertyModel<String>(this, "project"), projListModel)
			{
			@Override
			public boolean isVisible()  { return "Id".equals(getSearchType()); }
			});

		projDrop.setOutputMarkupId(true);
		projDrop.add(buildFormSubmitBehavior(true, projNameField));
		}
	

	private IndicatingAjaxButton buildSelectButton(String id, final AjaxProjectField nameField)
		{
		return new IndicatingAjaxButton(id)
			{
			@Override
			public boolean isVisible() { return showButton; }
			
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> Form) 
				{
				System.out.println("In the on submit button for search panel");
				Boolean isDrop = "Id".equals(getSearchType());
				input = isDrop ? project : ((AutoCompleteTextField)(nameField)).getInput();
					
				try 
					{
					System.out.println("Try search for search panel");

					project = projectService.getProjectIdForSearchString(input);
					setProject(project);
					NewSearchForProjectPanel.this.onSelect(project, target);
					}
				catch (Exception e) 
					{
					//System.out.println("In the exception for search panel");
					//NewSearchForProjectPanel.this.error(e.getMessage());
					doError(e.getMessage(), target);
				//	target.add(NewSearchForProjectPanel.this.get("feedback"));
					}
				}
			
			
			@Override
			public void onError(AjaxRequestTarget target, Form<?> Form) 
				{
				doError("Cant find project", target);
	//			NewSearchForProjectPanel.this.error("Cant find project");
	//			target.add(NewSearchForProjectPanel.this.get("feedback"));
				}
			};
		}
	

	private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop, final AjaxProjectField nameField)
		{
		return new AjaxFormSubmitBehavior(this, "change") 
			{
			protected void onSubmit(AjaxRequestTarget target) 
				{
				Boolean isDrop = "Id".equals(getSearchType());
				input = isDrop ? project : ((AutoCompleteTextField)(nameField)).getInput();
				//String projId = projectService.getProjectIdForSearchString(input);
				NewSearchForProjectPanel.this.onSelect(input, target);
				
				/*try 
					{
					System.out.println("Try search for search panel");

					project = projectService.getProjectIdForSearchString(input);
					setProject(project);
					NewSearchForProjectPanel.this.onSelect(project, target);
					} 
				catch (Exception e) 
					{
					System.out.println("In the exception for search panel");
					doError(e.getMessage(), target);
					
				//	NewSearchForProjectPanel.this.error(e.getMessage());
				//	target.add(NewSearchForProjectPanel.this.get("feedback"));
			
					} */
				}
			
			@Override
			protected void onError(AjaxRequestTarget target) { 
				//NewSearchForProjectPanel.this.error("Cant find project");
				//target.add(NewSearchForProjectPanel.this.get("feedback")); }
				System.out.println("In the exception for search panel");
				doError("Can't find project", target);
				}
			};
		}
	
	
/*

public abstract class NewSearchForProjectPanel extends Panel
	{
	@SpringBean 
	private ProjectService projectService;

	private String project, input, searchType = "Id";
	private Boolean showButton = false;
	WebMarkupContainer container;
	
	public NewSearchForProjectPanel(String id)
		{
		super(id);
		
		add(new FeedbackPanel("feedback").setOutputMarkupId(true));
		add(new NewSearchForProjectForm("searchByProjectForm"));
		}

	
	public final class NewSearchForProjectForm extends Form 
		{
		final IndicatingAjaxButton selectButton;
		public NewSearchForProjectForm(final String id)
			{
			super(id);
		
			container = new WebMarkupContainer("container"); 
			container.setOutputMarkupId(true);
			add(container);
			
			AjaxProjectField projNameField;
			projNameField = new AjaxProjectField("project")
				{
				@Override
				public boolean isVisible()  { return "Name".equals(getSearchType()); }
				};
				
			projNameField.setOutputMarkupId(true);
			projNameField.add(buildFormSubmitBehavior(false));
			container.add(projNameField);
			
			container.add(selectButton = buildSelectButton("selectButton"));
			
			DropDownChoice<String> projDrop;
			LoadableDetachableModel <List<String>> projListModel = new LoadableDetachableModel<List<String>>() 
				{
				@Override
				protected List<String> load() 
					{
					return projectService.projectIdsByStartDate();
					}
				};
								
			container.add(projDrop = new DropDownChoice<String>("projDrop", new PropertyModel<String>(this, "project"), projListModel)
				{
				@Override
				public boolean isVisible()  { return "Id".equals(getSearchType()); }
				});

			projDrop.setOutputMarkupId(true);
			projDrop.add(buildFormSubmitBehavior(true));
			}
		

		private IndicatingAjaxButton buildSelectButton(String id)
			{
			return new IndicatingAjaxButton(id)
				{
				@Override
				public boolean isVisible() { return showButton; }
				
				@Override
				public void onSubmit(AjaxRequestTarget target, Form form) 
					{
					try
						{
						if(projectService.isValidProjectSearch(project))
							NewSearchForProjectPanel.this.onSelect(project, target);
						else
							doError(target);
						}
					catch (Exception e)  { doError(target); }
					}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> arg1) 
					{
					doError(target);
					}	
				};
			}

	
		private AjaxFormSubmitBehavior buildFormSubmitBehavior(final boolean isDrop)
			{
			return new AjaxFormSubmitBehavior(this, "change") 
				{
				protected void onSubmit(AjaxRequestTarget target) 
					{
					input = isDrop ? project : ((AutoCompleteTextField)(this.getComponent())).getInput();
					
					System.out.println("Project search is " + input);
					if(projectService.isValidProjectSearch(input))
						{
						String projId = isDrop ? input : StringParser.parseId(input);
						
						setProject(projId);
						target.add(this.getComponent());
						if (selectButton != null)
							target.add(selectButton);
						onSelect(projId, target);
						}
					else
						System.out.println("Not a valid project search" + input);
				
					}
				
				@Override
				protected void onError(AjaxRequestTarget arg0) { doError(arg0); }
				};
			}
			
	*/
		//private void doError(AjaxRequestTarget target)
		//	{
		//	String output = (input == null ? "" : input);
	//		NewSearchForProjectPanel.this.error("Can't find project (" + output + "). Please verify that the  search id (or name) is valid.");
	//		target.add(NewSearchForProjectPanel.this.get("feedback"));
	//		}
		
		public String getSearchType()
			{
			return searchType;
			}
		
		public void setSearchType(String st)
			{
			searchType = st;
			}
		
		public void setProject(String pr) 
			{
			project = pr;
			}
		
		public String getProject() 
			{
			return project;
			}
		}
	
	public void setProject(String input) 
		{
		this.project=input;
		}
	
	public String getProject() 
		{
		return project;
		}
	
	public Boolean getShowButton()
		{
		return showButton;
		}

	public void setShowButton(Boolean showButton)
		{
		this.showButton = showButton;
		}

	
	public String getSearchType()
		{
		return searchType;
		}


	public void setSearchType(String searchType)
		{
		this.searchType = searchType;
		}

	
	public WebMarkupContainer getContainer()
		{
		return container;
		}

	public void setContainer(WebMarkupContainer container)
		{
		this.container = container;
		}

	protected abstract void onSelect(String project, AjaxRequestTarget target);
	protected abstract void doError(String msg, AjaxRequestTarget target);
	}

	

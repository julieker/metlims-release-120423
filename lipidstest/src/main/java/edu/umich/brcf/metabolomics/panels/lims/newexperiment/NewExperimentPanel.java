////////////////////////////////////////////////////
// NewExperimentPanel.java
// Written by Jan Wigginton, Aug 7, 2016
////////////////////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lims.newexperiment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.StudyDesignInfoSet;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoSet;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.comparator.ProjectByStartDateComparator;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;


//Test Client Report
public class NewExperimentPanel extends Panel 
	{
	@SpringBean
	SampleService sampleService;
	
	@SpringBean 
	private ExperimentService experimentService;
	
	@SpringBean 
	private ProjectService projectService;
	
	ProjectExperimentsPanel pePanel;
	ExperimentDetail edsPanel;
	ClientSampleAssaysPanelNew saNewPanel;
	NewExperimentStudyDesignPanel edPanel;
	NewExperimentSubjectInfoPanel esuPanel;
	//ExperimentAliquotsPanel saqPanel;
	NewExperimentSamplesPanel samplesPanel;
	TabbedPanel tabbedPanel; 
	List<Project> projectList;

	Experiment experiment;
	
	StudyDesignInfoSet studyDesignSet = new StudyDesignInfoSet(); //new StudyDesignInfoSet("EX00406", "mrc2");
	SubjectInfoSet subjectInfoSet = new SubjectInfoSet(); //"", "mrc2");
	
	
	public NewExperimentPanel(String id) 
		{
		this (id, null, null);
		}
	
	
	public NewExperimentPanel(String id, String expId, ModalWindow modal)
		{
		this(id, expId, null, modal);
		}
	
	
	public NewExperimentPanel(String id, String eid, List<String> projectIds, ModalWindow modal)
		{
		super(id);
		((MedWorksSession) Session.get()).setExpProjmap(new  HashMap<String, List <Experiment> >());
		setOutputMarkupId(true);
		add(new FeedbackPanel("feedback"));
		
		if (eid != null)
			{
			updateProjectList(eid, projectIds);
	    	setExperiment(experimentService.loadById(experiment.getExpID()));
			}
		
		final List tabs = buildTabs();
		add(tabbedPanel = new TabbedPanel("tabs", tabs));
	
		//	tabbedPanel.setSelectedTab(5);
		tabbedPanel.setSelectedTab(4);
		tabbedPanel.setSelectedTab(3);
		tabbedPanel.setSelectedTab(2);
		tabbedPanel.setSelectedTab(1);
	    tabbedPanel.setSelectedTab(0);
	    tabbedPanel.setOutputMarkupId(true);
	    
	    add(pePanel = buildProjectExperimentsPanel());
	    updatePanels(getProjectList());
		}
	
	// issue 441
	public void updateProjectList(String expId, List<String> projectIds)
		{
		this.experiment =  expId == null ? null : experimentService.loadById(expId);
	
		projectList = new ArrayList<Project>();
		if (ListUtils.isNonEmpty(projectIds))
			for (String pid : projectIds)
				projectList.add(projectService.getProjectWithOrderedExperiments(pid));
		else if (experiment != null)
			{
			String pid = experiment.getProject().getProjectID();
			projectList.add(projectService.getProjectWithOrderedExperiments(pid));
			}
		}
	
	private ProjectExperimentsPanel buildProjectExperimentsPanel()
		{
		return new ProjectExperimentsPanel("pePanel")
			{
			@Override
			protected void onExpLinkClicked(String expId, AjaxRequestTarget target) 
				{
				try { renewPanels(target, expId);  }
				catch (Exception e)
					{
					target.appendJavaScript("alert('Experiment " + expId + " has missing sample information and cannot be accessed at this time');");
					}
				}
			};
		}
	
	
	private List<AbstractTab> buildTabs()
		{
	    List tabs = new ArrayList<AbstractTab>();
	    
	    tabs.add(new AbstractTab(new Model("Experiment Detail")) 
			{
	    	public Panel getPanel(String panelId)
	    		{
	    		return (edsPanel=new ExperimentDetail(panelId, getExperiment()));
	    		}
			});
	    
	    tabs.add(new AbstractTab(new Model("Samples")) 
			{
	    	public Panel getPanel(String panelId)
	    		{
	    		return (samplesPanel = new NewExperimentSamplesPanel(panelId, getExperiment()));
	    		}
			});
	    
		tabs.add(new AbstractTab(new Model("Study Design")) 
			{
			public Panel getPanel(String panelId) 
				{
				edPanel = new NewExperimentStudyDesignPanel(panelId, getExperiment() , null, getBasePanel());
				edPanel.setOutputMarkupId(true);
				return edPanel;
				}
			});
			
		
		tabs.add(new AbstractTab(new Model("Subject Info")) 
			{
			public Panel getPanel(String panelId) 
				{
				esuPanel = new NewExperimentSubjectInfoPanel(panelId,  getExperiment(), null, getBasePanel());
				esuPanel.setOutputMarkupId(true);
				return esuPanel;
				}
			});	  
		
		
		tabs.add(new AbstractTab(new Model("Assays")) 
		 	{
			public Panel getPanel(String panelId)
		   		{
			   	return (saNewPanel=new ClientSampleAssaysPanelNew(panelId, getExperiment()));
		   		}
		 	});
	
	
	   	return tabs;
		}
	
	
	public NewExperimentPanel getBasePanel()
		{
		return this;
		}
	
	
	public void renewPanels(AjaxRequestTarget target, String expId)
		{
		updatePanels(getProjectList());
		// issue 441
		setExperiment(expId == null ? null : experimentService.loadById(expId));
		
		target.add(pePanel);
		target.add(edsPanel);
		target.add(esuPanel);
    	target.add(edPanel);
		target.add(samplesPanel);
	
		tabbedPanel.setSelectedTab(tabbedPanel.getSelectedTab());
		target.add(tabbedPanel);
    	target.add(pePanel.getParent());
		}
	

	public void renewPage(AjaxRequestTarget target)
		{
		updatePanels(getProjectList());
		target.add(pePanel);
		target.add(edsPanel);
		tabbedPanel.setSelectedTab(0);
		target.add(tabbedPanel);
		target.add(pePanel.getParent());
		}
	
	private void updatePanels(List<Project> projectList)
		{
		pePanel.setProjectList(projectList);
		pePanel.setVisible(projectList!=null);
		}
	
	
	public Experiment getExperiment() 
		{
		return experiment;
		}
	
	
	public void setExperiment(Experiment experiment) 
		{
		String expId = (this.experiment == null ? "" : this.experiment.getExpID());
		this.experiment = experiment;
		
		if(this.experiment!=null && !(expId.equals(this.experiment.getExpID())))
			{
			studyDesignSet = null; //new StudyDesignInfoSet(experiment.getExpID(), "mrc2");	
			subjectInfoSet = null; //new SubjectInfoSet(experiment, "mrc2");
			edsPanel.setExperiment(experiment);
			}
		
		tabbedPanel.setVisible(experiment!=null);
		}
	
	
	public List<Project> getProjectList()
		{
		return projectList;
		}
	
	
	public SubjectInfoSet getSubjectInfoSet()
		{
		return subjectInfoSet;
		}


	public void setSubjectInfoSet(SubjectInfoSet subjectInfoSet)
		{
		this.subjectInfoSet = subjectInfoSet;
		}


	public StudyDesignInfoSet getStudyDesignSet()
		{
		return studyDesignSet;
		}


	public void setStudyDesignSet(StudyDesignInfoSet studyDesignSet)
		{
		this.studyDesignSet = studyDesignSet;
		}


	public void setProjectList(List<Project> pList)
		{
		Collections.sort(pList, new ProjectByStartDateComparator());
		this.projectList=pList;
		}
	}
	
	

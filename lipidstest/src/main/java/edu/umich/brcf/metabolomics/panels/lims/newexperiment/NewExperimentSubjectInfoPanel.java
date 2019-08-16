// ExperimentSubjectInfoPanel.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lims.newexperiment;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoItem;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoSet;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class NewExperimentSubjectInfoPanel extends Panel 
	{
	@SpringBean
	ExperimentService experimentService;
	
	private String expId;
	private SubjectInfoSet subjectInfoSet;
	private List<String> possibleSubjectTypes = Arrays.asList(new String [] {"Human", "Animal", "Plant", "Cells", "Unknown"});
	private Experiment experiment;
	
	/*public NewExperimentSubjectInfoPanel(String id, Experiment experiment, WebPage backPage, 
			final SubjectInfoSet subjectInfoSet) 
		{
		super(id);
		
		this.experiment = experiment;
		expId = (experiment == null ? "" : experiment.getExpID());
		this.subjectInfoSet = subjectInfoSet;
		
		add(new Label("expId", getExperimentName()));	
	//hover
		add(new ListView("subjectInfo", new PropertyModel<List<SubjectInfoItem>>(subjectInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				SubjectInfoItem info = (SubjectInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectId", new PropertyModel<String>(info, "subjectId")));
				listItem.add(new Label("subjectSpecies", new PropertyModel<String>(info, "subjectSpecies")));
				
				listItem.add(buildSubjectTypeDropdown("subjectType", "subjectType", info));
				listItem.add(new Label("taxonomyId", new PropertyModel<String>(info, "taxonomyId")));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		} */
	
	public NewExperimentSubjectInfoPanel(String id, Experiment experiment, WebPage backPage,  final NewExperimentPanel parent) 
		{
		super(id);
		
		this.experiment = experiment;
		expId = (experiment == null ? "" : experiment.getExpID());
	
		if (parent.getSubjectInfoSet() == null)
			parent.setSubjectInfoSet(new SubjectInfoSet(expId));
		
		this.subjectInfoSet = parent.getSubjectInfoSet();
		
		add(new Label("expId", getExperimentName()));	
	
		add(new ListView("subjectInfo", new PropertyModel<List<SubjectInfoItem>>(subjectInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				SubjectInfoItem info = (SubjectInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectId", new PropertyModel<String>(info, "subjectId")));
				listItem.add(new Label("subjectSpecies", new PropertyModel<String>(info, "subjectSpecies")));
				listItem.add(buildSubjectTypeDropdown("subjectType", "subjectType", info));
				listItem.add(new Label("taxonomyId", new PropertyModel<String>(info, "taxonomyId")));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}

	
	SubjectInfoSet buildSubjectInfoSet(String expId)
		{
		SubjectInfoSet s = new SubjectInfoSet();
		s.setMode("drcc");
		s.setInfoFields(experimentService.loadOptimizedForSubjectTracking(expId));
		return s;
		}
	
	
	public Experiment getExperiment()
		{
		return experiment;
		}
	
	private String getExperimentName() 
		{
		return getExperiment() != null ?  getExperiment().getExpName()+" ("+experiment.getExpID()+")" : "";
		}
	
	
	private DropDownChoice buildSubjectTypeDropdown(String id, String propertyName, SubjectInfoItem item)
		{
		return  new DropDownChoice(id,  new PropertyModel(item, propertyName), 
		  new LoadableDetachableModel<List<String>>() 
			{
        	@Override
        	protected List<String> load()  {  return possibleSubjectTypes; }
        	
        	})
			{
			@Override 
        	public boolean isEnabled() { return false; }
			
			};
		}

	
	public void setExperiment(Experiment experiment)
		{
		this.expId = (experiment == null ? "EX00417 " : experiment.getExpID()); 
		this.subjectInfoSet = new SubjectInfoSet(expId);
		}
	
	
	public  String getOutputFileName()
		{
		return "DrccSubjectInfo." + expId + ".tsv";
		}

	
	public List <String> getColTitles()
		{
		return Arrays.asList(new String[] {"Subject Id", "Subject Type", "Subject Species", "Taxonomy Id"});
		}
	}

/*
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoItem;
import edu.umich.brcf.metabolomics.panels.lims.newexperiment.datacollectors.SubjectInfoSet;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;


public class NewExperimentSubjectInfoPanel extends Panel 
	{
	@SpringBean
	ExperimentService experimentService;
	
	private String expId;
	private SubjectInfoSet subjectInfoSet;
	private List<String> possibleSubjectTypes = Arrays.asList(new String [] {"Human", "Animal", "Plant", "Cells"});
	private Experiment experiment;
	
	public NewExperimentSubjectInfoPanel(String id, Experiment experiment, WebPage backPage,  
			final NewExperimentPanel parent) 
		{
		super(id);
		
		this.experiment = experiment;
		expId = (experiment == null ? "" : experiment.getExpID());
	
		if (parent.getSubjectInfoSet() == null)
			parent.setSubjectInfoSet(new SubjectInfoSet(expId));
		
		this.subjectInfoSet = parent.getSubjectInfoSet();
		
		add(new Label("expId", getExperimentName()));	
	
		add(new ListView("subjectInfo", new PropertyModel<List<SubjectInfoItem>>(subjectInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				SubjectInfoItem info = (SubjectInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectId", new PropertyModel<String>(info, "subjectId")));
				listItem.add(new Label("subjectSpecies", new PropertyModel<String>(info, "subjectSpecies")));
				listItem.add(buildSubjectTypeDropdown("subjectType", "subjectType", info));
				listItem.add(new Label("taxonomyId", new PropertyModel<String>(info, "taxonomyId")));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}
	
	SubjectInfoSet buildSubjectInfoSet(String expId)
		{
		SubjectInfoSet s = new SubjectInfoSet();
		s.setMode("drcc");
		s.setInfoFields(experimentService.loadOptimizedForSubjectTracking(expId));
		return s;
		}
	
	public Experiment getExperiment()
		{
		return experiment;
		}
	
	private String getExperimentName() 
		{
		return getExperiment() != null ?  getExperiment().getExpName()+" ("+experiment.getExpID()+")" : "";
		}
	
	
	private DropDownChoice buildSubjectTypeDropdown(String id, String propertyName, SubjectInfoItem item)
		{
		return  new DropDownChoice(id,  new PropertyModel(item, propertyName), 
		  new LoadableDetachableModel<List<String>>() 
			{
           	@Override
           	protected List<String> load()  {  return possibleSubjectTypes; }
			})
			{
			};
		}

	
	public void setExperiment(Experiment experiment)
		{
		this.expId = (experiment == null ? "EX00417 " : experiment.getExpID()); 
		this.subjectInfoSet = new SubjectInfoSet(expId);
		}
	
	
	public  String getOutputFileName()
		{
		return "DrccSubjectInfo." + expId + ".tsv";
		}

	
	public List <String> getColTitles()
		{
		return Arrays.asList(new String[] {"Subject Id", "Subject Type", "Subject Species", "Taxonomy Id"});
		}
	}*/
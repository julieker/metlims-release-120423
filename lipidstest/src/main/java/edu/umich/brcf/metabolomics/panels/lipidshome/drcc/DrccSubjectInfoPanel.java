// DrccSubjectInfoPanel.java
// Written by Jan Wigginton 06/01/15

// DrccSubjectInfoPanel.java
// Written by Jan Wigginton 06/01/15

package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;


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

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.behavior.OddEvenAttributeModifier;



public class DrccSubjectInfoPanel extends Panel 
	{
	String expId;
	
	DrccSubjectInfoSet subjectInfoSet;
	
	List<String> possibleSubjectTypes = Arrays.asList(new String [] {"Human", "Animal", "Plant", "Cells"});
	
	public DrccSubjectInfoPanel(String id, String selectedExperiment, WebPage backPage, 
			final DrccSubjectInfoSet subjectInfoSet) 
		{
		super(id);
		
		expId = selectedExperiment;
		
		this.subjectInfoSet = subjectInfoSet;
		
		String tabTitle = subjectInfoSet.getMode().equals("drcc") ? "D.  Subjects" : "Subject Info for " + selectedExperiment ;
		add(new Label("tabTitle", tabTitle));

		String header = subjectInfoSet.getMode().equals("drcc") ? "Metabolomics Core - DRCC Data Reporting Tool" : "";
		add(new Label("header", header)
			{
			@Override
			public boolean isVisible()
				{
				return subjectInfoSet.getMode().equals("drcc");
				}
			});
	
	
		add(new ListView("subjectInfo", new PropertyModel<List<DrccSubjectInfoItem>>(subjectInfoSet, "infoFields"))
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				DrccSubjectInfoItem info = (DrccSubjectInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectId", new PropertyModel<String>(info, "subjectId")));
				listItem.add(new Label("subjectSpecies", new PropertyModel<String>(info, "subjectSpecies")));
				//listItem.add(new Label("subjectType", new PropertyModel<String>(info, "subjectType")));
				
				listItem.add(buildSubjectTypeDropdown("subjectType", "subjectType", info));
				listItem.add(new Label("taxonomyId", new PropertyModel<String>(info, "taxonomyId")));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}

	
	private DropDownChoice buildSubjectTypeDropdown(String id, String propertyName, DrccSubjectInfoItem item)
		{
		DropDownChoice drp =  new DropDownChoice(id,  new PropertyModel(item, propertyName), 
		  new LoadableDetachableModel<List<String>>() 
			{
           	@Override
           	protected List<String> load() 
           		{ 
            	return possibleSubjectTypes;
            	}
			})
			{
			protected boolean wantOnSelectionChangedNotifications() 
				{
			    return true;
				}
			};
				
		return drp;
		}
		
	public void setExperiment(Experiment experiment)
		{
		//System.out.println("Experiment is " + experiment == null ? " null" : " not null");
		this.expId = (experiment == null ? "EX00417 " : experiment.getExpID()); 
		///System.out.println("Setting experiment to  " + expId);

		this.subjectInfoSet = new DrccSubjectInfoSet(expId);
		}
	
	
	
	public  String getOutputFileName()
		{
		return "DrccSubjectInfo." + expId + ".tsv";
		}

	public List <String> getColTitles()
		{
		return Arrays.asList(new String[] {"Subject Id", "Subject Type", "Subject Species", "Taxonomy Id"});
		}

	/*
	protected ResourceLink buildDownloadLink(String linkId, List <IWriteConvertable> list)
		{	
		final WebFileDownloadResource resource = new WebFileDownloadResource(list);
		resource.setColTitlesListModel(new PropertyModel(this, "colTitles"));
		resource.setOutfileName(new PropertyModel<String> (this, "outputFileName"));
		resource.setCacheable(false);		
		resource.setMimeType("text/tsv");
			
		ResourceLink rl = new ResourceLink(linkId, resource)
			{
			public void onClick()
				{
				resource.setOutfileName("dogs");
				}
			};
			
		return rl;
		}*/
	}



/*
package edu.umich.metworks.web.panels.analysis.drcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;
import edu.umich.metworks.lims.comparator.DrccSubjectInfoComparator;
import edu.umich.metworks.lims.domain.Experiment;
import edu.umich.metworks.lims.domain.Sample;
import edu.umich.metworks.lims.domain.Subject;
import edu.umich.metworks.lims.interfaces.IWriteConvertable;
import edu.umich.metworks.lims.service.ExperimentService;
import edu.umich.metworks.lims.service.GenusSpeciesService;
import edu.umich.metworks.lims.service.SampleService;
import edu.umich.metworks.util.WebFileDownloadResource;
import edu.umich.metworks.web.utils.OddEvenAttributeModifier;
import edu.umich.metworks.web.utils.widget.AjaxBackButton;

public class DrccSubjectInfoPanel extends Panel 
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	String expId;
	
	List<String> possibleSubjectTypes = Arrays.asList(new String [] {"Human", "Animal", "Plant", "Cells"});
	
	public DrccSubjectInfoPanel(String id, String selectedExperiment, WebPage backPage) 
		{
		super(id);
		
		expId = ((selectedExperiment != null && !selectedExperiment.trim().equals("")) ? selectedExperiment : "EX00417");
		Experiment exp = expService.loadExperimentWithInfoForDrcc(expId);
		if (expId.charAt(0) != 'E' || expId.charAt(1) != 'X')
			expId = "EX00417";
		
		List <IWriteConvertable> subjectInfo = buildSubjectInfoList(exp);
		
		ResourceLink downloadLink;
		add(downloadLink = buildDownloadLink("downloadLink", subjectInfo));
		
		add(new AjaxBackButton("backButton", backPage));
		add(new ListView("subjectInfo", subjectInfo)
			{
			@Override
			protected void populateItem(ListItem listItem) 
				{
				DrccSubjectInfoItem info = (DrccSubjectInfoItem) listItem.getModelObject();
				
				listItem.add(new Label("subjectId", new PropertyModel<String>(info, "subjectId")));
				listItem.add(new Label("subjectSpecies", new PropertyModel<String>(info, "subjectSpecies")));
				//listItem.add(new Label("subjectType", new PropertyModel<String>(info, "subjectType")));
				
				listItem.add(buildSubjectTypeDropdown("subjectType", "subjectType", info));
				listItem.add(new Label("taxonomyId", new PropertyModel<String>(info, "taxonomyId")));
				
				listItem.add(OddEvenAttributeModifier.create(listItem));
				}	
			});
		}

	List <IWriteConvertable> buildSubjectInfoList(Experiment exp)
		{
		List<Sample> sampleList = exp.getSampleList();
		
		Map <String, DrccSubjectInfoItem> subjects = new HashMap<String, DrccSubjectInfoItem>();
		Subject subj;
		List <IWriteConvertable> subjectInfo = new ArrayList<IWriteConvertable>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample sample = sampleList.get(i);
			subj = sample.getSubject();
			
			if (subj == null) 
				continue;
			
			String genusSpeciesName = ((sample != null && sample.getGenusOrSpecies() != null) ? sample.getGenusOrSpecies().getGenusName() : "");
			String genusSpeciesId = ((sample != null && sample.getGenusOrSpecies() != null ) ? sample.getGenusOrSpecies().getGsID().toString() : "");
			String taxId = ((subj == null || subj.getTaxId() ==null) ? "" : subj.getTaxId().toString());
			String userSubjId = (subj == null ? "" : subj.getUserSubjectId());
			String mrc2SubjId = (subj == null ? "" : subj.getSubjectId());
			
			String subjId = userSubjId + " / " + mrc2SubjId;
			
			//String genusSpecies = sampleList.get(i).getGenusOrSpecies().getGenusName();
	
			String subjectType = lookupSubjectType(taxId);
			String subjectSpecies = lookupSubjectSpecies(taxId);
			DrccSubjectInfoItem info = new DrccSubjectInfoItem(subjId, subjectType, genusSpeciesId, subjectSpecies, taxId);
			if (subj != null)
				subjects.put(subj.getSubjectId(), info);
			}
		
		Set<String> keys = subjects.keySet();
		for (String key : keys)
			{			
			subjectInfo.add((DrccSubjectInfoItem) subjects.get(key));
			}
		
		Collections.sort(subjectInfo, new DrccSubjectInfoComparator());
	
		return subjectInfo;
		}

	private DropDownChoice buildSubjectTypeDropdown(String id, String propertyName, DrccSubjectInfoItem item)
		{
		DropDownChoice drp =  new DropDownChoice(id,  new PropertyModel(item, propertyName), 
		  new LoadableDetachableModel<List<String>>() 
			{
           	@Override
           	protected List<String> load() 
           		{ 
            	return possibleSubjectTypes;
            	}
			})
			{
			protected boolean wantOnSelectionChangedNotifications() 
				{
			    return true;
				}
			};
				
		return drp;
		}
		
	
	private String lookupSubjectType(String taxId)
		{
		switch (taxId)
			{
			case "9606" : return "Human"; 
			default : return "";
			}
		}
	
	private String lookupSubjectSpecies(String taxId)
		{
		List <String> speciesNames = genusSpeciesService.getSubjectSpeciesForTaxId(taxId);
		return (speciesNames == null ? "" : speciesNames.toString());
		}
	
	public  String getOutputFileName()
		{
		return "DrccSubjectInfo." + expId + ".tsv";
		}

	public List <String> getColTitles()
		{
		return Arrays.asList(new String[] {"Subject Id", "Subject Type", "Subject Species", "Taxonomy Id"});
		}

	protected ResourceLink buildDownloadLink(String linkId, List <IWriteConvertable> list)
		{	
		final WebFileDownloadResource resource = new WebFileDownloadResource(list);
		resource.setColTitlesListModel(new PropertyModel(this, "colTitles"));
		resource.setOutfileName(new PropertyModel<String> (this, "outputFileName"));
		resource.setCacheable(false);		
		resource.setMimeType("text/tsv");
			
		ResourceLink rl = new ResourceLink(linkId, resource)
			{
			public void onClick()
				{
				resource.setOutfileName("dogs");
				}
			};
			
		return rl;
		}
	}
	*/


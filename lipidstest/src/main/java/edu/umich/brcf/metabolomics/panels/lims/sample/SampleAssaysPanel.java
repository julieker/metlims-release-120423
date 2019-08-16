package edu.umich.brcf.metabolomics.panels.lims.sample;

import java.util.List;



//import org.apache.struts.util.LabelValueBean;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.dto.SampleAssaysBean;
import edu.umich.brcf.shared.layers.service.ExperimentService;


public class SampleAssaysPanel extends Panel{
	
	Experiment experiment;
	List<SampleAssaysBean> sampleAssayList;
	
	@SpringBean 
	private ExperimentService experimentService;
	
	public SampleAssaysPanel(String id, Experiment experiment) {
		super(id);
		setOutputMarkupId(true);
		setExperiment(experiment);
		add(new Label("head", new Model(getExperimentName())));
		add(new ListView("sampleAssayList", new PropertyModel(this, "sampleAssayList")) {
			public void populateItem(final ListItem listItem) {
				SampleAssaysBean sab=(SampleAssaysBean) listItem.getModelObject();
				listItem.add(new Label("sampleID", sab.getSampleId()));
				listItem.add(new ListView("assays", sab.getAssays()) {
					public void populateItem(final ListItem listItem2) {
						String assay=(String) listItem2.getModelObject();
						listItem2.add(new Label("assay", assay));
					}}	);	
			}});
	}

	private String getExperimentName() {
		if (getExperiment()!=null)
			return (getExperiment().getExpName()+" ("+experiment.getExpID()+")");
		else
			return "";
	}
	
	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
		if ((experiment!=null)&&(!experiment.equals(null)))
			setSampleAssayList(experimentService.getAssaysForExperiment(experiment));
	}

	public List<SampleAssaysBean> getSampleAssayList() {
		return sampleAssayList;
	}

	public void setSampleAssayList(List<SampleAssaysBean> sampleAssayList) {
		this.sampleAssayList = sampleAssayList;
	}
}

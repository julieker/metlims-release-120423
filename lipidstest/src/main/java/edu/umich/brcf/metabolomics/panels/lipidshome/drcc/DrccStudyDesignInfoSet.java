
package edu.umich.brcf.metabolomics.panels.lipidshome.drcc;

// Sample Details
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.service.ClientService;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.OrganizationService;
import edu.umich.brcf.shared.layers.service.ProjectService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.layers.service.UserService;
import edu.umich.brcf.shared.layers.domain.Client;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Organization;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.domain.User;
import edu.umich.brcf.shared.util.comparator.DrccSampleInfoComparator;


public class DrccStudyDesignInfoSet implements Serializable
	{
	@SpringBean
	ExperimentService expService;
	
	@SpringBean 
	SampleService sampleService; 
	
	@SpringBean
	GenusSpeciesService genusSpeciesService;
	
	@SpringBean
	FactorService factorService;
	

	public List <DrccStudyDesignInfoItem> infoFields = new ArrayList<DrccStudyDesignInfoItem>();

	private List <String> factorLabels; 
	
	static final int nFactorsTracked = 5;
	String mode = "drcc";


	public DrccStudyDesignInfoSet(String expId)
		{
		this(expId, "drcc");
		}

	public DrccStudyDesignInfoSet(String expId, String mode)
		{
		Injector.get().inject(this);
		setMode(mode);
		initializeForExperiment(expId);
		}
	

	public void initializeForExperiment(String expId)
		{
		Experiment exp = expService.loadById(expId);
		factorLabels = new ArrayList<String>();
		for (int i = 0; i < exp.getFactors().size(); i++)
			factorLabels.add(exp.getFactors().get(i).getFactorName());
	
		Collections.sort(factorLabels);
		buildInfoList(exp);
		}
	
	public void buildInfoList(Experiment exp)
		{
		List<Sample> sampleList = exp.getSampleList();
		List <DrccStudyDesignInfoItem> sampleInfo = new ArrayList<DrccStudyDesignInfoItem>();
		
		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			DrccStudyDesignInfoItem info = new DrccStudyDesignInfoItem();
			info.setMode(mode);
			String sampleId = s.getSampleID();
			
			info.setSampleId(sampleId);
			info.setSampleName(s.getSampleName());
			info.setResearcherSampleName(mode.equals("drcc") ? s.getSampleName() : sampleId);
			info.setSubjectName(sampleService.getUserSubjectId(sampleId));
			info.setFactorValues(sampleService.getFactorValuesByIdSortedByName(sampleId));
			info.setUserDefinedSampleType(s.getUserDefSampleType());
			info.setUserSampleId(s.getSampleName());
			sampleInfo.add(info);
			}
		
		//Collections.sort(sampleInfo, new DrccSampleInfoComparator());
		infoFields = sampleInfo;
		}


	public List<DrccStudyDesignInfoItem> getInfoFields() {
		return infoFields;
	}


	public void setInfoFields(List<DrccStudyDesignInfoItem> infoFields) {
		this.infoFields = infoFields;
	}


	public List<String> getFactorLabels() {
		return factorLabels;
	}


	public void setFactorLabels(List<String> factorLabels) {
		this.factorLabels = factorLabels;
	}
	
	public String getFactorLabels(int i) {
		return factorLabels.get(i);
	}

	public void setFactorLabels(int i, String value)
		{
		this.factorLabels.set(i, value);
		}
	

	public static int getNfactorstracked() {
		return nFactorsTracked;
	}
	
	public void setMode(String mode)
		{
		this.mode = mode;
		}
	
	public String getMode()
		{
		return mode;
		}
	}

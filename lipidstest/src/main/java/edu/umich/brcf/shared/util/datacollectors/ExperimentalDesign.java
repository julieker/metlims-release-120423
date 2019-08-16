//////////////////////////////////////////
//ExperimentalDesign.java/
//Written by Jan Wigginton, December 2015
////////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SampleAssay;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.FactorService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.util.comparator.ExperimentalDesignItemComparator;



public class ExperimentalDesign implements Serializable
	{
	@SpringBean
	ExperimentService expService;

	@SpringBean
	SampleService sampleService;

	@SpringBean
	GenusSpeciesService genusSpeciesService;

	@SpringBean
	FactorService factorService;

	protected List<ExperimentalDesignItem> infoFields; // = new
														// ArrayList<ExperimentalDesignItem>();
	protected List<String> factorLabels = new ArrayList<String>();

	public static int SUBMISSION_SHEET_NFACTORS = 5;
	public static int SUBMISSION_SHEET_NASSAYS = 6;
	public static int SHORT_LABEL_LEN = 35;

	public ExperimentalDesign()
		{
		Injector.get().inject(this);
		}

	public ExperimentalDesign(String expId)
		{
		this();
		initializeForExperiment(expId);
		}

	public ExperimentalDesign(List<SampleDTO> sampleList,
			Map<String, List<String>> factor_map, List<String> factorNames,
			Map<String, List<String>> assay_map)
		{
		this();
		initializeFromSheetRead(sampleList, factor_map, factorNames, assay_map);
		}

	public void initializeFromSheetRead(List<SampleDTO> sampleList,
			Map<String, List<String>> factorMap, List<String> factorNames,
			Map<String, List<String>> assayMap)
		{
		infoFields = new ArrayList<ExperimentalDesignItem>();
		// new ArrayList<ExperimentalDesignItem>();

		for (int i = 0; i < sampleList.size(); i++)
			{
			SampleDTO s = sampleList.get(i);
			ExperimentalDesignItem info = new ExperimentalDesignItem();
			String sampleId = s.getSampleID();

			info.setSampleId(sampleId);
			info.setUserSampleId(s.getSampleName());

			List<String> sampleAssayNames = assayMap.get(sampleId);
			info.setAssayNames(sampleAssayNames);
			info.setDisplayAssayNames(sampleAssayNames);

			List<String> sampleFactorValues = new ArrayList<String>();

			for (int j = 0; j < factorNames.size(); j++)
				{
				String factorName = factorNames.get(j);
				List<String> valuesForFactor = factorMap.get(factorName);
				sampleFactorValues.add(valuesForFactor.get(i));
				}

			info.setFactorValues(sampleFactorValues);
			infoFields.add(info);
			}

		Collections.sort(infoFields, new ExperimentalDesignItemComparator());
		}
	

	public void initializeForExperiment(String expId)
		{
		Experiment exp = expService.loadById(expId);

		factorLabels = new ArrayList<String>();
		for (int i = 0; i < exp.getFactors().size(); i++)
			factorLabels.add(exp.getFactors().get(i).getFactorName());

		infoFields = new ArrayList<ExperimentalDesignItem>();

		List<Sample> sampleList = sampleService
				.loadSampleForAssayStatusTracking(expId);

		for (int i = 0; i < sampleList.size(); i++)
			{
			Sample s = sampleList.get(i);
			if (s == null)
				continue;

			ExperimentalDesignItem info = new ExperimentalDesignItem();

			info.setSampleId(s.getSampleID());
			info.setUserSampleId(s.getSampleName());

			List<String> sampleAssayNames = new ArrayList<String>();
			List<SampleAssay> assays = s.getSampleAssays();

			for (int j = 0; j < assays.size(); j++)
				{
				String assayName = assays.get(j).getAssay().getAssayName();
				String assayNameShort = assayName.substring(0,
						Math.min(SHORT_LABEL_LEN, assayName.length()));
				String assayId = assays.get(j).getAssay().getAssayId();
				String fullName = assayNameShort + " (" + assayId + ")";
				sampleAssayNames.add(j, fullName);
				}

			info.setAssayNames(sampleAssayNames);
			infoFields.add(info);
			}

		Collections.sort(infoFields, new ExperimentalDesignItemComparator());
		}

	public List<ExperimentalDesignItem> getInfoFields()
		{
		return infoFields;
		}

	public void setInfoFields(List<ExperimentalDesignItem> infoFields)
		{
		this.infoFields = infoFields;
		}

	public List<String> getFactorLabels()
		{
		return factorLabels;
		}

	public String getFactorLabels(int i)
		{
		return factorLabels.get(i);
		}

	public void setFactorLabels(int i, String value)
		{
		this.factorLabels.set(i, value);
		}

	public void setFactorLabels(List<String> labels)
		{
		factorLabels = new ArrayList<String>();
		for (int i = 0; i < labels.size(); i++)
			factorLabels.add(labels.get(i));
		}
	}


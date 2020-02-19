////////////////////////////////////////////////////
// Mrc2TransitionalSubmissionSheetData.java
// Written by Jan Wigginton, Jun 14, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.interfaces.ICheckinSampleItem;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;



public class Mrc2TransitionalSubmissionSheetData implements Serializable, ISavableSampleData
	{
	@SpringBean
	AssayService assayService;

	private ClientDataInfo clientInfo;
	public Mrc2TransitionalExperimentDesign expDesign;
	public Mrc2SamplesMetadata samplesMetadata;

	public static final int SHORT_LABEL_LEN = 35;

	// Initialize from the database

	public Mrc2TransitionalSubmissionSheetData()
		{
		Injector.get().inject(this);
		clientInfo = new Mrc2ClientDataInfo();
		expDesign = new Mrc2TransitionalExperimentDesign();
		samplesMetadata = new Mrc2SamplesMetadata();
		}

	//SCHEMA
// epigenomics.
	public Mrc2TransitionalSubmissionSheetData(String expId)
		{
		Injector.get().inject(this);
		clientInfo = new ClientDataInfo();
		expDesign = new Mrc2TransitionalExperimentDesign(expId);
		samplesMetadata = new Mrc2SamplesMetadata(expId);
		}
	
	
	public Map<String, String> pullValuesByLabelForFactorName(String name)
		{
		Map<String, String> valuesByLabelForFactorName = new HashMap<String, String>();
		
		for(Mrc2TransitionalExperimentDesignItem itm : expDesign.getInfoFields())
			valuesByLabelForFactorName.put(itm.getSampleId(), itm.getValueForFactor(name));
		
		return valuesByLabelForFactorName;
		}
	
	
	public Map<String, List<String>> pullAssayMap()
		{
		return pullAssayMap(true);
		}

	
	public Map<String, List<String>> pullAssayMap(boolean translate)
		{
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (int i = 0; i < this.expDesign.infoFields.size(); i++)
			{
			Mrc2TransitionalExperimentDesignItem item = expDesign.infoFields.get(i);
			String sampleId = item.getSampleId();

			List<String> translatedNames = new ArrayList<String>();
			for (String name : item.getAssaysForSample())
				{
				String translated = name;
				if (translate)
					translated = assayService.getStandardNameForAssayName(name);

				translatedNames.add(translated);
				}
			map.put(sampleId, translatedNames);
			}

		return map;
		}
	
	// deleted pullSampleDTOs for successful mvn build

	/*
	public List<? extends ICheckinSampleItem> pullSampleDTOs()
		{
		return (List<? extends ICheckinSampleItem>) this.getSamplesMetadata().getInfoFields();
		}
	*/
	
	//JAK put there for successful mvn build issue 19.. never used
	
	public List<? extends ICheckinSampleItem> pullSampleDTOs()
		{
		return null;
		}
	
	public Map<String, List<String>> pullFactorMap()
		{
		Map<String, List<String>> factor_map = new HashMap<String, List<String>>();
		List<String> factors = expDesign.getFactorLabels();
		List<String> uniqueFactors = ListUtils.makeEntriesUniqueIfNeeded(factors);
	
		expDesign.setFactorLabels(uniqueFactors);

		for (int j = 0; j < uniqueFactors.size(); j++)
			{
			String name = uniqueFactors.get(j);

			List<String> valuesForName = new ArrayList<String>();
			for (int i = 0; i < expDesign.infoFields.size(); i++)
				valuesForName.add(expDesign.infoFields.get(i).getValueForFactor(name));

			factor_map.put(name, valuesForName);
			}

		return factor_map;
		}

	
	public String getExpId()
		{
		return this.clientInfo.getExperimentId();
		}

	
	@Override
	public int writeToDataBase() { return 0; } 

	
	@Override
	public int getSampleCount()
		{
		return samplesMetadata.getInfoFields().size();
		}


	public ClientDataInfo getClientInfo()
		{
		return clientInfo;
		}


	public Mrc2TransitionalExperimentDesign getExpDesign()
		{
		return expDesign;
		}


	public Mrc2SamplesMetadata getSamplesMetadata()
		{
		return samplesMetadata;
		}


	public void setClientInfo(ClientDataInfo clientInfo)
		{
		this.clientInfo = clientInfo;
		}


	public void setExpDesign(Mrc2TransitionalExperimentDesign expDesign)
		{
		this.expDesign = expDesign;
		}


/// JAK
	public void setSamplesMetadata(Mrc2SamplesMetadata samplesMetadata)
		{
		this.samplesMetadata = samplesMetadata;
		}
	
	/* public void setSamplesMetadata(Mrc2TransitionalSamplesMetadata samplesMetadata)
		{
		this.samplesMetadata = samplesMetadata;
		}
		
		*/
	
	}


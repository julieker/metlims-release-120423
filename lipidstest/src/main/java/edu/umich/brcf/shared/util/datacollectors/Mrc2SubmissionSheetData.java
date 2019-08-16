//Mrc2SubmissionSheetData.java
//Written by Jan Wigginton

package edu.umich.brcf.shared.util.datacollectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.METWorksException;
import edu.umich.brcf.shared.util.interfaces.ICheckinSampleItem;
import edu.umich.brcf.shared.util.interfaces.ISavableSampleData;
import edu.umich.brcf.shared.util.utilpackages.ListUtils;

public class Mrc2SubmissionSheetData implements Serializable, ISavableSampleData
	{
	@SpringBean
	AssayService assayService;

	private ClientDataInfo clientInfo;
	public Mrc2ExperimentalDesign expDesign;
	public Mrc2SamplesMetadata samplesMetadata;

	public static final int SHORT_LABEL_LEN = 35;

	// Initialize from the database

	public Mrc2SubmissionSheetData()
		{
		Injector.get().inject(this);
		clientInfo = new Mrc2ClientDataInfo();
		expDesign = new Mrc2ExperimentalDesign();
		samplesMetadata = new Mrc2SamplesMetadata();
		}
	

	public Mrc2SubmissionSheetData(String expId)
		{
		Injector.get().inject(this);
		clientInfo = new ClientDataInfo();
		expDesign = new Mrc2ExperimentalDesign(expId);
		samplesMetadata = new Mrc2SamplesMetadata(expId);
		}
	

	public Mrc2SubmissionSheetData(String expId, Map<String, List<String>> factor_map, Map<String, List<String>> assay_map, List<SampleDTO> samples,
			List<String> factorNames, FileUpload upload, Map<String, String> experiment_assays) throws METWorksException
		{
		Injector.get().inject(this);
		clientInfo = new ClientDataInfo();
		clientInfo.setExperimentId(expId);

		expDesign = new Mrc2ExperimentalDesign(samples, factor_map, factorNames, assay_map);
		samplesMetadata = new Mrc2SamplesMetadata(samples);
		}

	
	public Map<String, List<String>> pullAssayMap()
		{
		return pullAssayMap(false);
		}

	
	public Map<String, List<String>> pullAssayMap(boolean translate)
		{
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (int i = 0; i < this.expDesign.infoFields.size(); i++)
			{
			Mrc2ExperimentalDesignItem item = expDesign.infoFields.get(i);
			String sampleId = item.getSampleId();

			List<String> translatedNames = new ArrayList<String>();
			for (String name : item.getAssayNames())
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

	
	public List<? extends ICheckinSampleItem> pullSampleDTOs()
		{
		List<SampleDTO> list = new ArrayList<SampleDTO>();
		for (int i = 0; i < this.samplesMetadata.infoFields.size(); i++)
			{
			SampleDTO dto = samplesMetadata.infoFields.get(i).toIncompleteSampleDTO();
			dto.setExpID(this.clientInfo.getExperimentId());
			list.add(dto);
			}

		return (List<? extends ICheckinSampleItem>) list;
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
				valuesForName.add(expDesign.infoFields.get(i)
						.getFactorValues(j));

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


	public Mrc2ExperimentalDesign getExpDesign()
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


	public void setExpDesign(Mrc2ExperimentalDesign expDesign)
		{
		this.expDesign = expDesign;
		}


	public void setSamplesMetadata(Mrc2SamplesMetadata samplesMetadata)
		{
		this.samplesMetadata = samplesMetadata;
		}
	
	
	}


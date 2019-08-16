//////////////////////////////////////////////
// Mrc2SamplesMetadata.java
// Written by Jan Wigginton, September 2015
//////////////////////////////////////////////


package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.Subject;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.comparator.Mrc2SampleInfoBySampleIdComparator;
import edu.umich.brcf.shared.util.comparator.SampleBySampleIdComparator;


public class Mrc2SamplesMetadata implements Serializable
	{
	@SpringBean
	ExperimentService expService;

	@SpringBean
	SampleService sampleService;

	@SpringBean
	GenusSpeciesService genusSpeciesService;

	@SpringBean
	SubjectService subjectService;

	@SpringBean
	SampleTypeService sampleTypeService;

	public List<Mrc2SampleInfoItem> infoFields = new ArrayList<Mrc2SampleInfoItem>();

	public Mrc2SamplesMetadata()
		{
		}

	public Mrc2SamplesMetadata(String expId)
		{
		Injector.get().inject(this);
		initializeFromExperimentId(expId);
		}

	public Mrc2SamplesMetadata(List<SampleDTO> dtos)
		{
		Injector.get().inject(this);
		initializeFromDtos(dtos);
		}

	// Assuming subject id stored as in submission sheet load (dto.subjectId =
	// sample.userSubjectId)
	public void initializeFromDtos(List<SampleDTO> dtos)
		{
		List<Mrc2SampleInfoItem> sampleInfoList = new ArrayList<Mrc2SampleInfoItem>();
		Map<String, Mrc2SampleInfoItem> samples = new HashMap<String, Mrc2SampleInfoItem>();

		for (int i = 0; i < dtos.size(); i++)
			{
			SampleDTO dto = dtos.get(i);

			String sampleId = dtos.get(i).getSampleID();
			String researcherSampleId = dto.getSampleName();

			// SubjectId stores user subject id after submission sheet read
			String researcherSubjId = dto.getSubjectId(); // (subj == null ? ""
															// :
															// subj.getUserSubjectId());

			String stId = dto.getSampleTypeId();
			// SampleType sampleType = sampleTypeService.loadById(stId);
			// String sampleTypeDescription = sampleType.getDescription();
			String userDefinedSampleType = dto.getUserDefSampleType();

			Long gsId = dto.getGenusOrSpeciesID();
			String userDefinedGOS = dto.getUserDefGOS();
		
			
			// GenusSpecies gs = genusSpeciesService.loadById(gsId);
			// String genusSpeciesName = gs.getGenusName();
			String genusSpeciesId = (gsId == null ? "" : gsId.toString());

			String volume = dto.getVolume().toString();
			String units = dto.getVolUnits().toString();

			String locationId = dto.getLocID();

			Mrc2SampleInfoItem info = new Mrc2SampleInfoItem(sampleId,
					researcherSampleId, researcherSubjId,
					userDefinedSampleType, userDefinedGOS, volume, units, stId,
					genusSpeciesId, locationId, "");

			infoFields.add(info);
			if (dto != null)
				samples.put(dto.getSampleID(), info);
			}
		}

	
	public List<SampleDTO> grabDTOList()
		{
		List<SampleDTO> dtos = new ArrayList<SampleDTO>();

		for (int i = 0; i < this.infoFields.size(); i++)
		    {
			dtos.add(infoFields.get(i).toIncompleteSampleDTO());
		    }

		return dtos;
		}

	
	public Map<String, String> pullSampleIdMap()
		{
		Map<String, String> map = new HashMap<String, String>();
		
		for (Mrc2SampleInfoItem item : infoFields)
			map.put(item.getSampleId(), item.getResearcherSampleId());
		
		return map;
		}
	
	
	public void initializeFromExperimentId(String expId)
		{
		Experiment exp = expService.loadExperimentWithInfoForDrcc(expId);

		List<Sample> sampleList = exp.getSampleList();
		Collections.sort(sampleList, new SampleBySampleIdComparator());

		Map<String, Mrc2SampleInfoItem> samples = new HashMap<String, Mrc2SampleInfoItem>();

		List<Mrc2SampleInfoItem> sampleInfoList = new ArrayList<Mrc2SampleInfoItem>();
		for (int i = 0; i < sampleList.size(); i++)
			{
			String sampleId = sampleList.get(i).getSampleID();

			Sample sample = sampleService.loadSampleAlongWithExpById(sampleId);
			Subject subj = sample.getSubject();

			if (subj == null)
				continue;

			String researcherSampleId = sample.getSampleName();
			String researcherSubjId = (subj == null ? "" : subj
					.getUserSubjectId());
			String sampleType = sample.getSampleType().getDescription();
			String sampleTypeId = sample.getSampleType().getSampleTypeId();
			// String genusSpeciesName = ((sample != null &&
			// sample.getGenusOrSpecies() != null) ?
			// sample.getGenusOrSpecies().getGenusName() : "");
			String genusSpeciesId = ((sample != null && sample
					.getGenusOrSpecies() != null) ? sample.getGenusOrSpecies()
					.getGsID().toString() : "");
			String volume = sample.getVolume().toString();
			String units = sample.getVolUnits();
			String locationId = sample.getLocID();
			String userDefinedGOS = sample.getUserDefGOS();
			String userDefinedSampleType = sample.getUserDefSampleType();

			Mrc2SampleInfoItem info = new Mrc2SampleInfoItem(sampleId,
					researcherSampleId, researcherSubjId,
					userDefinedSampleType, userDefinedGOS, volume, units,
					sampleTypeId, genusSpeciesId, locationId, "");

			if (sample != null)
				samples.put(sample.getSampleID(), info);
			}

		Set<String> keys = samples.keySet();
		for (String key : keys)
			sampleInfoList.add((Mrc2SampleInfoItem) samples.get(key));

		Collections.sort(sampleInfoList,
				new Mrc2SampleInfoBySampleIdComparator());

		this.infoFields = sampleInfoList;
		}

	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		for (Mrc2SampleInfoItem item : this.getInfoFields())
			sb.append(item.toString());

		return sb.toString();
		}

	public List<Mrc2SampleInfoItem> getInfoFields()
		{
		return infoFields;
		}

	public void setInfoFields(ArrayList<Mrc2SampleInfoItem> infoFields)
		{
		this.infoFields = infoFields;
		}
	}

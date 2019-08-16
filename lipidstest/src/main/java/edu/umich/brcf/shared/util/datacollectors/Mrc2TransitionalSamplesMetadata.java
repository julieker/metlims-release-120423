////////////////////////////////////////////////////
// Mrc2TransitionalSamplesMetadata.java
// Written by Jan Wigginton, Jun 22, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.dto.SampleDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;
import edu.umich.brcf.shared.layers.service.SubjectService;
import edu.umich.brcf.shared.util.interfaces.ICheckinSampleItem;

public class Mrc2TransitionalSamplesMetadata implements Serializable
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

	public List<SampleDTO> infoFields = new ArrayList<SampleDTO>();

	public Mrc2TransitionalSamplesMetadata()
		{
		}

	public Mrc2TransitionalSamplesMetadata(String expId)
		{
		Injector.get().inject(this);
		initializeFromExperimentId(expId);
		}

	public void initializeFromExperimentId(String expId)
		{
		}

	public List<SampleDTO> getInfoFields()
		{
		return infoFields;
		}

	public void setInfoFields(List<SampleDTO> infoFields)
		{
		this.infoFields = infoFields;
		}

	public List<? extends ICheckinSampleItem> grabDTOList()
		{
		return infoFields;
		}
	}

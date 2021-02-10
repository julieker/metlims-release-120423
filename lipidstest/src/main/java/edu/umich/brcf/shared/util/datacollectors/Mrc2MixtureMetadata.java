//////////////////////////////////////////////
// Mrc2MixtureMetadata.java
// Written by Julie Keros dec 7 2020
//////////////////////////////////////////////


package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.metabolomics.layers.service.GenusSpeciesService;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.layers.service.ExperimentService;
import edu.umich.brcf.shared.layers.service.SampleService;
import edu.umich.brcf.shared.layers.service.SampleTypeService;
import edu.umich.brcf.shared.layers.service.SubjectService;

public class Mrc2MixtureMetadata implements Serializable
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

	public List<Mrc2MixtureInfoItem> infoFields = new ArrayList<Mrc2MixtureInfoItem>();

	public Mrc2MixtureMetadata()
		{
		}
	
	public Mrc2MixtureMetadata(List<MixtureDTO> dtos)
		{
		Injector.get().inject(this);
		initializeFromDtos(dtos);
		}
	
	public void initializeFromDtos(List<MixtureDTO> dtos)
		{
		for (int i = 0; i < dtos.size(); i++)
		    {
			MixtureDTO dto = dtos.get(i);
		    String createDate = dto.getCreateDate();
		    String createdBy = dto.getCreatedBy();
		    String finalVolume = dto.getDesiredFinalVolume();
		    String volumeSolventToAdd            = dto.getVolumeSolventToAdd();
			List <String> dtoList = new ArrayList<String> ();
	        dtoList.addAll(dto.getAliquotList());  
	        // issue 110
			// JAK add in the mixture here for MixtureInfo
	        // issue 118
	        Mrc2MixtureInfoItem info = new Mrc2MixtureInfoItem(createDate, createdBy,  volumeSolventToAdd,finalVolume, dtoList, dto.getAliquotVolumeList(), dto.getAliquotConcentrationList(),  dto.getMixtureList(), dto.getMixtureVolumeList(), dto.getMixtureConcentrationList() , dto.getMixtureName());
			infoFields.add(info);
			}
		}
	
	public List<MixtureDTO> grabDTOList()
		{
		List<MixtureDTO> dtos = new ArrayList<MixtureDTO>();
		for (int i = 0; i < this.infoFields.size(); i++)
		    {
			dtos.add(infoFields.get(i).toIncompleteMixtureDTO());
		    }
		return dtos;
		}
	}

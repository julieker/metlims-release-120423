
////////////////////////////////////////////////////
// MixtureDTO.java

// Created by by Julie Keros May 28, 2020
////////////////////////////////////////////////////
// Updated by Julie Keros May 28, 2020

package edu.umich.brcf.shared.layers.dto;

// issue 61 2020
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umich.brcf.metabolomics.panels.lims.mixtures.MixAliquotInfo;
import edu.umich.brcf.shared.layers.domain.Mixture;

// issue 61 2020
// issue 31 2020
public class MixtureDTO implements Serializable
	{
	public static MixtureDTO instance(String mixtureId,  String createDate, String createdBy, String volumeSolventToAdd, String desiredFinalVolume, String mixtureName) 
	    {
	    return new MixtureDTO(mixtureId, createDate, createdBy, volumeSolventToAdd, desiredFinalVolume, mixtureName);
		}
	
	public static MixtureDTO instance(Mixture mixture)
		{
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yy");
		String formatted = format1.format(mixture.getCreateDate().getTime());
		// Output "Wed Sep 26 14:23:28 EST 2012"		
		return new MixtureDTO(mixture.getMixtureId(), formatted,  mixture.getCreatedBy().getId(), mixture.getVolSolvent().toString(), mixture.getDesiredFinalVol().toString(), mixture.getMixtureName());
		}	
	// issue 61 2020
	private String finalVolumeUnits ; // issue 196
	private String mixtureId;
	private String createDate;
	private List<String> aliquotList;
	private List<String> aliquotNoAssayMultipleChoiceList;
	private List<String> aliquotNoAssayMultipleChoiceListDry; // issue 196
	private List<String> mixtureList;
	private List<String> aliquotVolumeList;
	private List<String> aliquotConcentrationList;
	private List<String> mixtureVolumeList;
	private List<String> mixtureConcentrationList;
	private String createdBy ;
	private String aliquotsChoice; // issue 94
	private String mixturesChoice; // issue 94
    private String volumeSolventToAdd; // issue 94
    private String desiredFinalVolume;
    private String mixtureName;  // issue 118
    private String volumeAliquotUnits; //issue 196
    private List <String> aliquotVolumeUnitList; // issue 196
    private List<String> mixtureVolumeUnitList;  // issue 196
    
    
    // issue 123
    Map<String, List<String>> mixtureAliquotMap = new HashMap<String, List<String>>(); 
    Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap = new HashMap<String, List<MixAliquotInfo>>();
    // issue 116
	private MixtureDTO(String mixtureId, String createDate,   String createdBy, String volumeSolventToAdd, String desiredFinalVolume, String mixtureName)
		{
		this.mixtureId = mixtureId;		
		this.createDate = createDate;
		this.createdBy = createdBy;		
		} 
	
	
	
	public MixtureDTO() { }	
	
	public List<String> getAliquotVolumeUnitList ()
		{
		return aliquotVolumeUnitList;
		}

	public void setAliquotVolumeUnitList (List<String> aliquotVolumeUnitList)
		{
		this.aliquotVolumeUnitList = aliquotVolumeUnitList;
		}
		
	// issue 196
	public String getAliquotsChoice()
		{
		return aliquotsChoice;
		}

	// issue 94
	public void setAliquotsChoice(String aliquotsChoice)
		{
		this.aliquotsChoice= aliquotsChoice;
		}
		
	// issue 196
	public String getFinalVolumeUnits()
		{
		return finalVolumeUnits;
		}

	// issue 196
	public void setFinalVolumeUnits(String finalVolumeUnits)
		{
		this.finalVolumeUnits= finalVolumeUnits;
		}
	
	// issue 123
	public String getMixturesChoice()
		{
		return mixturesChoice;
		}

	public void setMixturesChoice(String mixturesChoice)
		{
		this.mixturesChoice= mixturesChoice;
		}

	public String getCreateDate()
		{
		return createDate;
		}

// issue 94
	public void setCreateDate(String createDate)
		{
		this.createDate= aliquotsChoice;
		}
	
// issue 94	
	public String getVolumeSolventToAdd()
		{
		return volumeSolventToAdd;
		}

//issue 94
	public void setVolumeSolventToAdd(String volumeSolventToAdd)
		{
		this.volumeSolventToAdd = volumeSolventToAdd;
		}
	
// issue 94	
	public String getDesiredFinalVolume()
		{
		return desiredFinalVolume;
		}

//issue 94
	public void setDesiredFinalVolume(String desiredFinalVolume)
		{
		this.desiredFinalVolume = desiredFinalVolume;
		}
	
    // issue 118
	public String getMixtureName()
		{
		return mixtureName;
		}

	//issue 118
	public void setMixtureName(String mixtureName)
		{
		this.mixtureName = mixtureName;
		}
	
	// issue 94	
	public String getCreatedBy()
		{
		return createdBy;
		}

//issue 94
	public void setCreatedBy(String createdBy)
		{
		this.createdBy = createdBy;
		}
	
	public List<String> getAliquotList ()
		{
		return aliquotList;
		}
	// issue 123
	public List<String> getAliquotNoAssayMultipleChoiceList ()
		{
		return aliquotNoAssayMultipleChoiceList;
		}
	
	// issue 196
	public List<String> getAliquotNoAssayMultipleChoiceListDry ()
		{
		return aliquotNoAssayMultipleChoiceListDry;
		}
	
	public void setAliquotList (List<String> aliquotList)
		{
		this.aliquotList = aliquotList;
		}
	
	// issue 123
	public void setAliquotNoAssayMultipleChoiceList(List<String> aliquotNoAssayMultipleChoiceList)
		{
		this.aliquotNoAssayMultipleChoiceList = aliquotNoAssayMultipleChoiceList;
		}
	
	// issue 196
	public void setAliquotNoAssayMultipleChoiceListDry(List<String> aliquotNoAssayMultipleChoiceListDry)
		{
		this.aliquotNoAssayMultipleChoiceListDry = aliquotNoAssayMultipleChoiceListDry;
		}
	
	// issue 110
	public List<String> getMixtureList ()
		{
		return mixtureList;
		}

	public void setMixtureList (List<String> mixtureList)
		{
		this.mixtureList = mixtureList;
		}
	
	
	public List<String> getAliquotVolumeList ()
		{
		return aliquotVolumeList;
		}

	public void setAliquotVolumeList (List<String> aliquotVolumeList)
		{
		this.aliquotVolumeList = aliquotVolumeList;
		}

	public List<String> getAliquotConcentrationList ()
		{
		return aliquotConcentrationList;
		}

	public void setAliquotConcentrationList (List<String> aliquotConcentrationList)
		{
		this.aliquotConcentrationList = aliquotConcentrationList;
		}
	
	// issue 110
	public List<String> getMixtureVolumeList ()
		{
		return mixtureVolumeList;
		}

	public void setMixtureVolumeList (List<String> mixtureVolumeList)
		{
		this.mixtureVolumeList = mixtureVolumeList;
		}
	
	// issue 196
	public List<String> getMixtureVolumeUnitList ()
		{
		return mixtureVolumeUnitList;
		}

	public void setMixtureVolumeUnitList (List<String> mixtureVolumeUnitList)
		{
		this.mixtureVolumeUnitList = mixtureVolumeUnitList;
		}
	
	
	// issue 110
	public List<String> getMixtureConcentrationList ()
		{
		return mixtureConcentrationList;
		}

	public void setMixtureConcentrationList (List<String> mixtureConcentrationList)
		{
		this.mixtureConcentrationList = mixtureConcentrationList;
		}

	public String getMixtureId ()
		{
		return mixtureId;
		}

	public void setMixtureId (String mixtureId)
		{
		this.mixtureId = mixtureId;
		}
	
	// issue 123
	public Map<String, List<String>> getMixtureAliquotMap ()
		{
		return mixtureAliquotMap;
		}

	// issue 123
	public void setMixtureAliquotMap (Map<String, List<String>> mixtureAliquotMap)
		{
		this.mixtureAliquotMap = mixtureAliquotMap;
		}
	
	// issue 123
	public Map<String, List<MixAliquotInfo>> getMixtureAliquotInfoMap ()
		{
		return mixtureAliquotInfoMap;
		}

	// issue 123
	public void setMixtureAliquotInfoMap (Map<String, List<MixAliquotInfo>> mixtureAliquotInfoMap)
		{
		this.mixtureAliquotInfoMap = mixtureAliquotInfoMap;
		}
	
	}

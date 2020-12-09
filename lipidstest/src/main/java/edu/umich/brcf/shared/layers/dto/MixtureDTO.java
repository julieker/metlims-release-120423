
////////////////////////////////////////////////////
// MixtureDTO.java

// Created by by Julie Keros May 28, 2020
////////////////////////////////////////////////////
// Updated by Julie Keros May 28, 2020

package edu.umich.brcf.shared.layers.dto;

// issue 61 2020
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import edu.umich.brcf.shared.layers.domain.Mixture;

// issue 61 2020
// issue 31 2020
public class MixtureDTO implements Serializable
	{
	public static MixtureDTO instance(String mixtureId,  String createDate, String createdBy, String volumeSolventToAdd, String desiredFinalVolume) 
	    {
	    return new MixtureDTO(mixtureId, createDate, createdBy, volumeSolventToAdd, desiredFinalVolume);
		}
	
	public static MixtureDTO instance(Mixture mixture)
		{
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yy");
		String formatted = format1.format(mixture.getCreateDate().getTime());
		// Output "Wed Sep 26 14:23:28 EST 2012"
		
		return new MixtureDTO(mixture.getMixtureId(), formatted,  mixture.getCreatedBy().getId(), mixture.getVolSolvent().toString(), mixture.getDesiredFinalVol().toString());
		}	
	// issue 61 2020
	private String mixtureId;
	private String createDate;
	private List<String> aliquotList;
	private List<String> aliquotVolumeList;
	private List<String> aliquotConcentrationList;
	private String createdBy ;
	private String aliquotsChoice; // issue 94
    private String volumeSolventToAdd; // issue 94
    private String desiredFinalVolume;

	private MixtureDTO(String mixtureId, String createDate,   String createdBy, String volumeSolventToAdd, String desiredFinalVolume)
		{
		this.mixtureId = mixtureId;		
		this.createDate = createDate;
		this.createdBy = createdBy;		
		} 
	
	public MixtureDTO() { }	
		
	// issue 94
	public String getAliquotsChoice()
		{
		return aliquotsChoice;
		}

	// issue 94
	public void setAliquotsChoice(String aliquotsChoice)
		{
		this.aliquotsChoice= aliquotsChoice;
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
	
	public void setAliquotList (List<String> aliquotList)
		{
		this.aliquotList = aliquotList;
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

	public String getMixtureId ()
		{
		return mixtureId;
		}

	public void setMixtureId (String mixtureId)
		{
		this.mixtureId = mixtureId;
		}
	
	}

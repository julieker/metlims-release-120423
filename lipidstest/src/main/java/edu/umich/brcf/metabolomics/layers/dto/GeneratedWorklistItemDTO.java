// GeneratedWorklistItemDTO.java
// Written by Jan Wigginton June 2015

package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;

import edu.umich.brcf.metabolomics.layers.domain.GeneratedWorklistItem;
import edu.umich.brcf.metabolomics.panels.workflow.worklist_builder.WorklistItemSimple;

public class GeneratedWorklistItemDTO implements Serializable
	{
	public static GeneratedWorklistItemDTO instance(String worklistId,
			String fileName, String sampleName, String sampleOrControlId,
			String comments, String injVolume, String platePos, String plateCode)
		{
		return new GeneratedWorklistItemDTO(null, worklistId, fileName,
				sampleName, sampleOrControlId, comments, injVolume, platePos,
				plateCode);
		}

	public static GeneratedWorklistItemDTO instance(GeneratedWorklistItem item)
		{
		return new GeneratedWorklistItemDTO(item.getItemId(), item
				.getWorklist().getWorklistId(), item.getFileName(),
				item.getSampleName(), item.getSampleOrControlId(),
				item.getComments(), item.getInjVol(), item.getPlatePos(),
				item.getPlateCode());
		}

	public static GeneratedWorklistItemDTO instance(WorklistItemSimple item)
		{
		return new GeneratedWorklistItemDTO(null, null,
				item.getOutputFileName(), item.getSampleName(),
				item.getSampleOrControlId(), item.getComments(),
				item.getInjectionVolume(), item.getSamplePosition(),
				item.getPlateCode());
		}

	private Long itemId;
	private String worklistId;
	private String fileName;
	private String sampleName;
	private String sampleOrControlId;
	private String comments;
	private String injVolume;
	private String platePos;
	private String plateCode;
	private String randomizationType;
	private String randomizationFileName;

	public GeneratedWorklistItemDTO()
		{
		}

	public GeneratedWorklistItemDTO(Long itemId, String worklistId,
			String fileName, String sampleName, String sampleOrControlId,
			String comments, String injVolume, String platePos, String plateCode)
		{
		this.itemId = itemId;
		this.worklistId = worklistId;
		this.fileName = fileName;
		this.sampleName = sampleName;
		this.sampleOrControlId = sampleOrControlId;
		this.comments = comments;
		this.injVolume = injVolume;
		this.platePos = platePos;
		this.plateCode = plateCode;
		}

	// Getters/setters///////////////////////////////////////////////////////////////

	public Long getItemId()
		{
		return itemId;
		}

	public void setItemId(Long itemId)
		{
		this.itemId = itemId;
		}

	public String getWorklistId()
		{
		return worklistId;
		}

	public void setWorklistId(String worklistId)
		{
		this.worklistId = worklistId;
		}

	public String getFileName()
		{
		return fileName;
		}

	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}

	public String getSampleName()
		{
		return sampleName;
		}

	public void setSampleName(String sampleName)
		{
		this.sampleName = sampleName;
		}

	public String getSampleOrControlId()
		{
		return sampleOrControlId;
		}

	public void setSampleOrControlId(String sampleOrControlId)
		{
		this.sampleOrControlId = sampleOrControlId;
		}

	public String getComments()
		{
		return comments;
		}

	public void setComments(String comments)
		{
		this.comments = comments;
		}

	public String getInjVolume()
		{
		return injVolume;
		}

	public void setInjVolume(String injVolume)
		{
		this.injVolume = injVolume;
		}

	public String getPlatePos()
		{
		return platePos;
		}

	public void setPlatePos(String platePos)
		{
		this.platePos = platePos;
		}

	public String getPlateCode()
		{
		return plateCode;
		}

	public void setPlateCode(String plateCode)
		{
		this.plateCode = plateCode;
		}

	}

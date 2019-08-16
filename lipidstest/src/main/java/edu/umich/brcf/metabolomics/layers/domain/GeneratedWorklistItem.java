///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	GeneratedWorklistItem.java
// 	Written by Jan Wigginton 06/2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
 * DROP TABLE METLIMS.GENERATOR_WORKLIST_ITEM
CREATE TABLE METLIMS.GENERATOR_WORKLIST_ITEM (
ITEM_ID NUMBER(22),
WORKLIST_ID  CHAR(10),
FILENAME VARCHAR2(150),
SAMPLENAME VARCHAR2(50),
SAMPLE_OR_CONTROL_ID CHAR(9),
COMMENTS VARCHAR2(1000),
INJ_VOLUME VARCHAR2(100),
PLATE_POS VARCHAR2(20),
PLATE_CODE VARCHAR2(20),
CONSTRAINT generator_worklist_item_pk PRIMARY KEY (ITEM_ID)

CREATE SEQUENCE GENERATED_WORKLIST_ITEM_ID_SEQ increment by 1 start with 1 ) */

package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistItemDTO;


@Entity()
@Table(name = "GENERATOR_WORKLIST_ITEM")
public class GeneratedWorklistItem implements Serializable
	{
	public static GeneratedWorklistItem instance(String fileName, String sampleName, String sampleOrControlId,
			String comments, String injVol, String platePos, String plateCode, GeneratedWorklist worklist) 
		{
		return new GeneratedWorklistItem(null, fileName, sampleName, sampleOrControlId, comments, injVol, platePos, plateCode, worklist);
		}
	
	
	public static GeneratedWorklistItem instance(GeneratedWorklistItemDTO dto, GeneratedWorklist worklist) 
		{
		return new GeneratedWorklistItem(dto.getItemId(), dto.getFileName(), dto.getSampleName(), dto.getSampleOrControlId(),
				dto.getComments(), dto.getInjVolume(), dto.getPlatePos(), dto.getPlateCode(), worklist);	
		}
	
	
	@Id()
	@SequenceGenerator(name = "generatedWorklistItemIdGenerator", sequenceName = "GENERATED_WORKLIST_ITEM_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generatedWorklistItemIdGenerator")
	@Column(name = "ITEM_ID", unique = true, nullable = false, precision = 12)
	private Long itemId;
	
	@Basic()
	@Column(name = "FILENAME", nullable = true, columnDefinition = "VARCHAR2(150)")
	private String fileName;
	
	@Basic()
	@Column(name = "SAMPLENAME", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String sampleName;
	
	@Basic()
	@Column(name = "SAMPLE_OR_CONTROL_ID", nullable = true, columnDefinition = "CHAR(9)")
	private String sampleOrControlId;
	
	@Basic()
	@Column(name = "COMMENTS", nullable = true, columnDefinition = "VARCHAR2(1000)")
	private String comments;
	
	@Basic()
	@Column(name = "INJ_VOLUME", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String injVol;
	
	@Basic()
	@Column(name = "PLATE_POS", nullable = true, columnDefinition = "VARCHAR2(20)")
	private String platePos;
	
	@Basic()
	@Column(name = "PLATE_CODE", nullable = true, columnDefinition = "VARCHAR2(20)")
	private String plateCode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKLIST_ID", referencedColumnName = "WORKLIST_ID", nullable = false)
	private GeneratedWorklist worklist;

	
	
	public GeneratedWorklistItem()
		{
		}
	
	public GeneratedWorklistItem(Long itemId,  String fileName, String sampleName, String sampleOrControlId,
	String comments, String injVol, String platePos, String plateCode, GeneratedWorklist worklist) 
		{
		this.itemId = itemId;
		this.fileName = fileName;
		this.sampleName = sampleName;
		this.sampleOrControlId = sampleOrControlId;
		this.comments = comments;
		this.injVol = injVol;
		this.platePos = platePos;
		this.plateCode = plateCode;
		this.worklist = worklist;
		}
	
	
	public void update(GeneratedWorklistItemDTO dto, GeneratedWorklist worklist)
		{
		this.itemId = dto.getItemId();
		this.fileName = dto.getFileName();
		this.sampleName = dto.getSampleName();
		this.sampleOrControlId = dto.getSampleOrControlId();
		this.comments = dto.getComments();
		
		this.injVol = dto.getInjVolume();
		this.platePos = dto.getPlatePos();
		this.plateCode = dto.getPlateCode();
		
		this.worklist = worklist;
		}
	
	///////////////  Getters/Setters //////////////////////////////////////////////////////////
	
	public Long getItemId() 
		{
		return itemId;
		}

	public void setItemId(Long itemId) 
		{
		this.itemId = itemId;
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

	public GeneratedWorklist getWorklist() 
		{
		return worklist;
		}

	public void setWorklist(GeneratedWorklist worklist) 
		{
		this.worklist = worklist;
		}


	public String getInjVol() 
		{
		return injVol;
		}


	public void setInjVol(String injVol) 
		{
		this.injVol = injVol;
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
	
	public String getWorklistIdForComment()
		{
		return getWorklist().getWorklistId();
		}
	}

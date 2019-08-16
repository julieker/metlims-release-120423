///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	GeneratedWorklist.java
// 	Written by Jan Wigginton 06/2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.dto.GeneratedWorklistDTO;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;


/* WORKLIST_ID CHAR(10),
PLATFORM_ID CHAR(5),
EXP_ID CHAR(7),
ASSAY_ID CHAR(4), 
INSTRUMENT_ID CHAR(6), 
INJECTION_MODE VARCHAR2(8), 
RANDOMIZATION_TYPE VARCHAR2(10),
RANDOMIZATION_FILENAME VARCHAR2(50),
GENERATED_BY CHAR(6),
DATE_GENERATED DATE,
FILENAME VARCHAR2(100), */


@Entity()
@Table(name = "GENERATOR_WORKLIST")
public class GeneratedWorklist implements Serializable
	{
	public static GeneratedWorklist instance(String platformId, String expId,
			String assayId, String instrumentId,  String injectionMode, String randomizationType, 
			String randomizationFileName, String generatedBy, Calendar dateGenerated, String fileName) 
		{
		return new GeneratedWorklist(null, platformId, expId, assayId, instrumentId, injectionMode,
				randomizationType, randomizationFileName, generatedBy, dateGenerated, fileName);
		}
	
	public static GeneratedWorklist instance(GeneratedWorklistDTO dto) 
		{
		return new GeneratedWorklist(null, dto.getPlatformId(), dto.getExpId(), dto.getAssayId(), 
			dto.getInstrumentId(), dto.getInjectionMode(), dto.getRandomizationType(),
			dto.getRandomizationFileName(), dto.getGeneratedBy(), Calendar.getInstance(), dto.getFileName());
		}
	
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "GeneratedWorklist"), @Parameter(name = "width", value = "10") })
	@Column(name = "WORKLIST_ID", unique = true, nullable = false, length = 10, columnDefinition = "CHAR(10)")
	private String worklistId;
	
	@Basic()
	@Column(name = "PLATFORM_ID", nullable = true, columnDefinition = "CHAR(5)")
	private String platformId;
	
	@Basic()
	@Column(name = "EXP_ID", nullable = true, columnDefinition = "CHAR(7)")
	private String expId;

	@Basic()
	@Column(name = "ASSAY_ID", nullable = true, columnDefinition = "CHAR(4)")
	private String assayId;

	@Basic()
	@Column(name = "INSTRUMENT_ID", nullable = true, columnDefinition = "CHAR(6)")
	private String instrumentId;

	@Basic()
	@Column(name = "INJECTION_MODE", nullable = true, columnDefinition = "VARCHAR2(8)")
	private String injectionMode;

	@Basic()
	@Column(name = "RANDOMIZATION_TYPE", nullable = true, columnDefinition = "VARCHAR2(10)")
	private String randomizationType;
	
	@Basic()
	@Column(name = "RANDOMIZATION_FILENAME", nullable = true, columnDefinition = "VARCHAR2(50)")
	private String randomizationFileName;

	@Basic()
	@Column(name = "GENERATED_BY", nullable = true, columnDefinition = "CHAR(6)")
	private String generatedBy;

	@Basic()
	@Column(name = "DATE_GENERATED", nullable = true, columnDefinition = "DATE")
	private Calendar dateGenerated;

	@Basic()
	@Column(name = "FILENAME", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String fileName;
	
	@OneToMany(mappedBy = "worklist", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List <GeneratedWorklistItem> items;
	
	
	public GeneratedWorklist(String worklistId, String platformId, String expId,String assayId, 
		String instrumentId,  String injectionMode, String randomizationType, String randomizationFileName, 
		String generatedBy, Calendar dateGenerated, String fileName) 
		{
		this.worklistId = worklistId;
		this.platformId = platformId;
		this.expId = expId;
		this.assayId = assayId;
		this.instrumentId = instrumentId;
		this.injectionMode = injectionMode;
		this.randomizationType = randomizationType;
		this.randomizationFileName = randomizationFileName;
		this.generatedBy = generatedBy;
		this.dateGenerated = dateGenerated;
		this.fileName = fileName; 
		this.items = new ArrayList<GeneratedWorklistItem>();
		}

	
	public GeneratedWorklist() {  }

	
	public void update(GeneratedWorklistDTO dto)
		{
		this.worklistId = dto.getWorklistId();
		this.platformId = dto.getPlatformId();
		this.expId = dto.getExpId();
		this.assayId = dto.getAssayId();
		this.instrumentId = dto.getInstrumentId();
		this.injectionMode = dto.getInjectionMode();
		this.randomizationType = dto.getRandomizationType();
		this.randomizationFileName = dto.getRandomizationFileName();
		this.generatedBy = dto.getGeneratedBy();
		try
			{
			this.dateGenerated = DateUtils.calendarFromDateStr(dto.getDateGenerated());
			}
		catch (Exception e)
			{
			this.dateGenerated = Calendar.getInstance();
			}
		
		this.fileName = dto.getFileName(); 
		}
		
	
	///// Getters/setters ////////////////////////////////////////////
	public String getWorklistId() {
		return worklistId;
	}

	public void setWorklistId(String worklistId) {
		this.worklistId = worklistId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getExpId() {
		return expId;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public String getAssayId() {
		return assayId;
	}

	public void setAssayId(String assayId) {
		this.assayId = assayId;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public String getInjectionMode() {
		return injectionMode;
	}

	public void setInjectionMode(String injectionMode) {
		this.injectionMode = injectionMode;
	}

	public String getRandomizationType() {
		return randomizationType;
	}

	public void setRandomizationType(String randomizationType) {
		this.randomizationType = randomizationType;
	}

	public String getRandomizationFilename() {
		return randomizationFileName;
	}

	public void setRandomizationFilename(String randomizationFilename) {
		this.randomizationFileName = randomizationFilename;
	}

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public Calendar getDateGenerated() {
		return dateGenerated;
	}

	public void setDateGenerated(Calendar dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getDateGeneratedAsStr()
		{
		return DateUtils.dateStrFromCalendar("MM/dd/yyyy hh:mm", dateGenerated);
		}
	
	public String getDateGeneratedAsStr(String format)
	{
		return DateUtils.dateStrFromCalendar(format, dateGenerated);
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<GeneratedWorklistItem> getItems() {
		return items;
	}

	public void setItems(List<GeneratedWorklistItem> items) 
		{
		this.items = items;
		}
	}

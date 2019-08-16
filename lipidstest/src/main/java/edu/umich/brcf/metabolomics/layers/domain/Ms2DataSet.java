package edu.umich.brcf.metabolomics.layers.domain;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Ms2DataSet.java
//Written by Jan Wigginton 04/29/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

import edu.umich.brcf.metabolomics.layers.dto.Ms2DataSetDTO;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;
import edu.umich.brcf.shared.util.utilpackages.DateUtils;

/*CREATE TABLE METLIMS.MS2_DATA_SETS
 (
 DATA_SET_ID CHAR(8),
 EXP_ID CHAR(7),
 ION_MODE VARCHAR2(8), 
 DATA_NOTATION VARCHAR2(50),
 REPLICATE NUMBER(3),
 RUN_DATE DATE,
 UPLOAD_DATE DATE,
 UPLOADED_BY CHAR(6),
 CONSTRAINT ms2_data_sets_pk PRIMARY KEY (DATA_SET_ID)
 )
 */

@Entity
@Table(name = "MS2_DATA_SETS")
public class Ms2DataSet implements Serializable, IWriteConvertable
	{
	public static String idFormat = "(M)(D)\\d{6}";

	public static Ms2DataSet instance(String eid, Calendar rDate,
			Calendar uDate, Integer rep, String upBy, String ionMode,
			String note)
		{
		return new Ms2DataSet(null, eid, rDate, uDate, rep, upBy, ionMode, note);
		}

	
	@Id
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Ms2DataSet"),
			@Parameter(name = "width", value = "8") })
	@Column(name = "DATA_SET_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	String dataSetId;

	@Basic()
	@Column(name = "EXP_ID", length = 7, columnDefinition = "CHAR(7)")
	private String expId;

	@Basic()
	@Column(name = "RUN_DATE")
	private Calendar runDate;

	@Basic()
	@Column(name = "UPLOAD_DATE")
	private Calendar uploadDate;

	// TIMESTAMP
	@Basic()
	@Column(name = "REPLICATE", length = 3, columnDefinition = "NUMBER(3)")
	private int replicate;

	@Basic()
	@Column(name = "UPLOADED_BY", length = 6, columnDefinition = "CHAR2(6)")
	private String uploadedBy;

	@Basic()
	@Column(name = "ION_MODE", length = 8, columnDefinition = "CHAR(8)")
	private String ionMode;

	@Basic()
	@Column(name = "DATA_NOTATION", length = 50, columnDefinition = "VARCHAR2(50)")
	private String dataNotation;

	@OneToMany(mappedBy = "dataSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<Ms2PeakSet> peakSets;

	// @OneToMany(mappedBy = "dataSet", fetch = FetchType.LAZY, cascade =
	// CascadeType.ALL)
	// @org.hibernate.annotations.Cascade(value =
	// org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	// public List <Ms2SampleMap> sampleMappings;

	// public String inputFileName;

	public Ms2DataSet()
		{
		}

	public Ms2DataSet(String dsid, String eid, Calendar rDate, Calendar uDate,
			int rep, String upBy, String ionMode, String note)
		{
		this.dataSetId = dsid;
		this.expId = eid;
		runDate = rDate;
		uploadDate = uDate;
		replicate = rep;
		uploadedBy = upBy;
		this.ionMode = ionMode;
		this.dataNotation = note;
		peakSets = new ArrayList<Ms2PeakSet>();
		// sampleMappings = new ArrayList<Ms2SampleMap>();
		// compoundMappings = new ArrayList<Ms2CompoundMap();
		}

	public void update(Ms2DataSetDTO dto)
		{
		this.dataSetId = dto.getDataSetId();

		this.expId = dto.getExpId();

		this.runDate = dto.getRunDate();
		this.uploadDate = dto.getUploadDate();
		this.replicate = dto.getReplicate();
		this.uploadedBy = dto.getUploadedBy();
		this.ionMode = dto.getIonMode();
		this.dataNotation = dto.getDataNotation();
		}

	public String getDataSetId()
		{
		return dataSetId;
		}

	public void setDataSetId(String dataSetId)
		{
		this.dataSetId = dataSetId;
		}

	public String getExpId()
		{
		return expId;
		}

	public void setExpId(Experiment exp)
		{
		this.expId = expId;
		}

	public Calendar getRunDate()
		{
		return runDate;
		}

	public void setRunDate(Calendar runDate)
		{
		this.runDate = runDate;
		}

	public Calendar getUploadDate()
		{
		return uploadDate;
		}

	public void setUploadDate(Calendar uploadDate)
		{
		this.uploadDate = uploadDate;
		}

	public int getReplicate()
		{
		return replicate;
		}

	public void setReplicate(int replicate)
		{
		this.replicate = replicate;
		}

	public List<Ms2PeakSet> getPeakSets()
		{
		return peakSets;
		}

	public void setPeakSets(List<Ms2PeakSet> peakSets)
		{
		this.peakSets = peakSets;
		}

	public String getRunDateAsStr()
		{
		if (this.runDate != null)
			return DateUtils.dateStrFromCalendar("MM/dd/yy", this.runDate);
		return "";
		}

	public String getUploadDateAsStr()
		{
		return DateUtils.dateStrFromCalendar("MM/dd/yy", this.uploadDate);
		}

	public Integer getNumCompounds()
		{
		return peakSets != null ? peakSets.size() : 0;
		}

	public String getUploadedBy()
		{
		return uploadedBy;
		}

	public void setUploadedBy(String uploadedBy)
		{
		this.uploadedBy = uploadedBy;
		}

	public String getIonMode()
		{
		return ionMode;
		}

	public void setIonMode(String mode)
		{
		if (mode == null || mode.trim().equals(""))
			return;
		if (!"NEGATIVE".equals(mode.toUpperCase())
				&& !"POSITIVE".equals(mode.toUpperCase()))
			return;

		ionMode = mode;
		}

	public String getDataNotation()
		{
		return this.dataNotation;
		}

	public void setDataNotation(String note)
		{
		this.dataNotation = note;
		}

	@Override
	public String toCharDelimited(String delimiter)
		{
		// TODO Auto-generated method stub
		return null;
		}

	@Override
	public String toExcelRow()
		{
		// TODO Auto-generated method stub
		return null;
		}

	}

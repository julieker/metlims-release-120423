package edu.umich.brcf.metabolomics.layers.domain;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2PeakSet.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakSetDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;

/*
 CREATE TABLE METLIMS.MS2_PEAK_SETS3
 (
 PEAK_SET_ID CHAR(11),
 DATA_SET_ID CHAR(8),
 COMPOUND_NAME VARCHAR2(400), 
 LIPID_CLASS VARCHAR2(50),
 KNOWN_STATUS_ID CHAR(1),
 START_MASS NUMBER(12, 5), 
 END_MASS NUMBER(12, 5),
 RETENTION_TIME NUMBER(12, 5), 
 CONSTRAINT ms_peak_sets3_pk PRIMARY KEY (PEAK_SET_ID)
 ) */

@Entity
@Table(name = "MS2_PEAK_SETS")
public class Ms2PeakSet implements Serializable, IWriteConvertable
	{
	public static Ms2PeakSet instance(String ln, Double rt, Double sm,
			Double em, String lc, String ks, Ms2DataSet dSet)
		{
		return new Ms2PeakSet(null, ln, rt, sm, em, lc, ks, dSet);
		}

	@Id
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Ms2PeakSet"),
			@Parameter(name = "width", value = "11") })
	@Column(name = "PEAK_SET_ID", unique = true, nullable = false, length = 11, columnDefinition = "CHAR(11)")
	String peakSetId;

	@Basic()
	@Column(name = "COMPOUND_NAME", length = 400, nullable = false, columnDefinition = "VARCHAR2(400)")
	private String lipidName;

	@Basic()
	@Column(name = "START_MASS", length = 12, columnDefinition = "NUMBER(12, 5)")
	private Double startMass;

	@Basic()
	@Column(name = "END_MASS", length = 12, columnDefinition = "NUMBER(12, 5)")
	private Double endMass;

	@Basic()
	@Column(name = "RETENTION_TIME", length = 12, columnDefinition = "NUMBER(12, 5)")
	private Double expectedRt;

	@Basic()
	@Column(name = "KNOWN_STATUS_ID", length = 1, columnDefinition = "CHAR(1)")
	private String knownStatus;

	@Basic()
	@Column(name = "LIPID_CLASS", length = 50, columnDefinition = "VARCHAR2(50)")
	private String lipidClass;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DATA_SET_ID", referencedColumnName = "DATA_SET_ID", nullable = false)
	private Ms2DataSet dataSet;

	@OneToMany(mappedBy = "peakSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<Ms2Peak> samplePeaks;

	public Ms2PeakSet()
		{
		}

	public Ms2PeakSet(String psid, String ln, Double rt, Double sm, Double em,
			String lc, String ks, Ms2DataSet dSet)
		{
		peakSetId = psid;
		lipidName = ln;
		expectedRt = rt;
		startMass = sm;
		endMass = em;
		lipidClass = lc;
		knownStatus = ks;
		dataSet = dSet;
		samplePeaks = new ArrayList<Ms2Peak>();
		}

	public void update(Ms2PeakSetDTO dto, Ms2DataSet dSet)
		{
		this.peakSetId = dto.getPeakSetId();
		this.lipidName = dto.getLipidName();
		this.expectedRt = dto.getExpectedRt();
		this.startMass = dto.getStartMass();
		this.endMass = dto.getEndMass();
		this.lipidClass = dto.getLipidClass();
		this.knownStatus = dto.getKnownStatus();
		this.dataSet = dSet;
		}

	// IWriteConvertable
	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder builder = new StringBuilder();
		String missingValue = "-" + separator;

		builder.append((peakSetId != null ? peakSetId + separator
				: missingValue));
		builder.append((lipidName != null ? lipidName + separator
				: missingValue));
		builder.append(((startMass != null && startMass != Double.NaN) ? startMass
				+ separator
				: missingValue));
		builder.append(((endMass != null && endMass != Double.NaN) ? endMass
				+ separator : missingValue));
		builder.append(((expectedRt != null && expectedRt != Double.NaN) ? expectedRt
				+ separator
				: missingValue));
		builder.append((lipidClass != null ? lipidClass + separator
				: missingValue));
		builder.append((knownStatus != null ? knownStatus + separator
				: missingValue));
		Double peak;
		for (int i = 0; i < samplePeaks.size(); i++)
			{
			peak = samplePeaks.get(i).getPeakArea();
			builder.append((peak != null && peak != Double.NaN) ? samplePeaks
					.get(i).getPeakArea() + separator : missingValue);
			}

		builder.append(IWriteConvertable.lineSeparator);

		return builder.toString();
		}

	@Override
	public String toExcelRow()
		{
		// TODO Auto-generated method stub
		return null;
		}

	// Getters/Setters////////////////////////////////////////////////

	public String getKnownStatus()
		{
		return knownStatus;
		}

	public void setKnownStatus(String ks)
		{
		knownStatus = ks;
		}

	public String getPeakSetId()
		{
		return peakSetId;
		}

	public void setPeakSetId(String peakSetId)
		{
		this.peakSetId = peakSetId;
		}

	public String getLipidName()
		{
		return lipidName;
		}

	public void setLipidName(String lipidName)
		{
		this.lipidName = lipidName;
		}

	public Double getStartMass()
		{
		return startMass;
		}

	public void setStartMass(Double startMass)
		{
		this.startMass = startMass;
		}

	public Double getEndMass()
		{
		return endMass;
		}

	public void setEndMass(Double endMass)
		{
		this.endMass = endMass;
		}

	public Double getExpectedRt()
		{
		return expectedRt;
		}

	public void setExpectedRt(Double expectedRt)
		{
		this.expectedRt = expectedRt;
		}

	public String getLipidClass()
		{
		return lipidClass;
		}

	public void setLipidClass(String lipidClass)
		{
		this.lipidClass = lipidClass;
		}

	public List<Ms2Peak> getSamplePeaks()
		{
		return samplePeaks;
		}

	public Ms2Peak getSamplePeaks(int i)
		{
		return samplePeaks.get(i);
		}

	public void setSamplePeaks(List<Ms2Peak> samplePeaks)
		{
		this.samplePeaks = samplePeaks;
		}

	public Ms2DataSet getDataSet()
		{
		return this.dataSet;
		}

	public void setDataSet(Ms2DataSet dSet)
		{
		this.dataSet = dSet;
		}
	}

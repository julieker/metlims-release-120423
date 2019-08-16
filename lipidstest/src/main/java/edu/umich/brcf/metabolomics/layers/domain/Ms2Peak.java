package edu.umich.brcf.metabolomics.layers.domain;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MS2Peak.java
//Written by Jan Wigginton 04/10/15
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



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
import javax.persistence.Transient;

import edu.umich.brcf.metabolomics.layers.dto.Ms2PeakDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



/*
CREATE TABLE METLIMS.MS2_PEAKS3
(
PEAK_ID NUMBER(20),
PEAK_SET_ID CHAR(11), 
PEAK_AREA NUMBER(15, 5),
SAMPLE_MAP_ID CHAR(12),
CONSTRAINT  ms_peaks_pk PRIMARY KEY (PEAK_ID)
)
*/

@Entity
@Table(name = "MS2_PEAKS")
public class Ms2Peak implements Serializable, IWriteConvertable
{
public static Ms2Peak instance(Ms2PeakSet peakSet, String sampleMapId, Double peakArea) 
{
return new Ms2Peak(null, peakSet, sampleMapId, peakArea);
}

public void update(Ms2PeakDTO dto, Ms2PeakSet set)
{
this.peakId = dto.getPeakId();
this.peakSet = set;
this.sampleMapId = dto.getSampleMapId();
this.peakArea = dto.getPeakArea();
}

// SWITCHOVER FROM PRODUCTION TO STAGING
@Id()
@SequenceGenerator(name = "lipidIdGenerator", sequenceName = "MS2_LIPID_PEAK_ID_SEQ")
//@SequenceGenerator(name = "lipidIdGenerator", sequenceName = "METLIMS_LIBRARY.MS_PEAK_ID_SEQ")
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lipidIdGenerator")
@Column(name = "PEAK_ID", unique = true, nullable = false, precision = 12)
private Long peakId;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "PEAK_SET_ID", referencedColumnName = "PEAK_SET_ID", nullable = false)
private Ms2PeakSet peakSet;


@Basic()
@Column(name = "SAMPLE_MAP_ID", length = 12, columnDefinition = "CHAR(12)")
private String sampleMapId;


@Basic()
@Column(name = "PEAK_AREA", length = 15, columnDefinition = "NUMBER(15, 5)")
private Double peakArea;

// Convenience variable for populating tables.
@Transient
private String tempSampleClientTag = "cat";
@Transient 
private String tempSampleTag = "dog";
@Transient
private String tempSampleId;


public Ms2Peak()
{
}

public Ms2Peak(Long pid,  Ms2PeakSet peakSet, String smid, Double pa)
{
peakId = pid;
this.peakSet = peakSet;
sampleMapId = smid;
peakArea = pa;
}



/////////Getters/Setters////////////////////////////////////////////////////




public Long getPeakId() 
{
return peakId;
}

public void setPeakId(Long peakId) 
{
this.peakId = peakId;
}

public Ms2PeakSet getPeakSet()
{
return peakSet;
}

public void setPeakSet(Ms2PeakSet set)
{
this.peakSet = set;
}

public String getSampleMapId() 
{
return sampleMapId;
}

public void setSampleMapId(String id) 
{
sampleMapId = id;
}

public Double getPeakArea() 
{
return peakArea;
}

public void setPeakArea(Double peakArea) 
{
this.peakArea = peakArea;
}


@Override
public String toCharDelimited(String separator)
{
StringBuilder sb = new StringBuilder();

if (this.tempSampleId != null && this.tempSampleId != "cat")
sb.append(this.tempSampleId + separator);
else
sb.append("-" + separator);

//if (this.sampleMapId != null)
//	sb.append(this.sampleMapId + separator);
//else 
//	sb.append("-" + separator);

//if (this.sampleLabel != null)
//	sb.append(this.sampleLabel + separator);
//else
//	sb.append("-" + separator);



if (this.tempSampleTag != null && this.tempSampleTag != "cat")
sb.append(this.tempSampleTag + separator);
else
sb.append("-" + separator);

if (this.tempSampleClientTag != null && this.tempSampleClientTag != "cat")
sb.append(this.tempSampleClientTag + separator);
else
sb.append("-" + separator);

if (this.peakArea != null)
sb.append(this.peakArea + separator);
else
sb.append("-" + separator);

return sb.toString();
}
@Override
public String toExcelRow() 
{
// TODO Auto-generated method stub
return null;
}


public String getTempSampleId()
{
return this.tempSampleId;
}

public void setTempSampleId(String id)
{
tempSampleId = id;
}	

public String getTempSampleTag()
{
return tempSampleTag;
}

public void setTempSampleTag(String lab)
{
tempSampleTag = lab;
}	

public void setTempSampleClientTag(String tag)
{
this.tempSampleClientTag = tag;
}

public String getTempSampleClientTag()
{
return this.tempSampleClientTag;
}
}




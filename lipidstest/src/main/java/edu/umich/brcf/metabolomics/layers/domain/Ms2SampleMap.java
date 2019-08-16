package edu.umich.brcf.metabolomics.layers.domain;

////////////////////////////////////////////////////////////////////
//Ms2SampleMap.java
//Written by Jan Wigginton
////////////////////////////////////////////////////////////////////



import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.dto.Ms2SampleMapDTO;
import edu.umich.brcf.shared.util.utilpackages.StringUtils;



/*
*   CREATE TABLE METLIMS.MS2_SAMPLE_MAPS
(
SAMPLE_MAP_ID  CHAR(12),
DATA_SET_ID CHAR(8),
SAMPLE_ID CHAR(9),
SAMPLE_TAG VARCHAR2(120),
RUN_ORDER NUMBER(5),
OTHER_ID CHAR(15),
MAP_COMMENT VARCHAR(300),
CONSTRAINT ms2_sample_maps_pk PRIMARY KEY (SAMPLE_MAP_ID)
)
*/

@Entity
@Table(name = "MS2_SAMPLE_MAPS")
public class Ms2SampleMap implements Serializable
{
public static Ms2SampleMap instance(String smid, String sid, String sTag, String dsid, Integer roi, String iid, String comment) 
{
return new Ms2SampleMap(smid, sid, sTag, dsid, roi, iid, comment);
}

@Id
@GeneratedValue(generator = "IdGeneratorDAO")
@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", 
parameters = {@Parameter(name = "idClass", value = "Ms2SampleMap"), @Parameter(name = "width", value = "12") })
@Column(name = "SAMPLE_MAP_ID", unique = true, nullable = false, length = 12, columnDefinition = "CHAR(12)")
private String sampleMapId;

@Basic
@Column(name = "SAMPLE_ID", length = 9, columnDefinition = "CHAR(9)")
private String sampleId;

@Basic
@Column(name = "SAMPLE_TAG", length = 120, columnDefinition = "VARCHAR2(120)")
private String sampleTag;

@Basic()
@Column(name = "DATA_SET_ID", length = 8, columnDefinition = "CHAR(8)")
private String dataSetId;

@Basic()
@Column(name = "RUN_ORDER", length = 5, columnDefinition = "NUMBER(5)")
private Integer runOrderIdx;

@Basic()
@Column(name = "OTHER_ID", length = 15, columnDefinition = "CHAR(15)")
private String otherId;

@Basic()
@Column(name = "MAP_COMMENT", length = 300, columnDefinition = "VARCHAR2(300)")
private String comment;



private Ms2SampleMap(String smid, String sid, String sTag, String dsid, int ro,  String iid, String comment)
{
this.sampleMapId = smid;
this.sampleId = sid;
this.sampleTag = sTag;
this.dataSetId = dsid;
this.runOrderIdx = ro;
this.otherId = iid;
this.comment = comment;
}

public Ms2SampleMap()
{
}

public void update(Ms2SampleMapDTO dto)
{
this.dataSetId = dto.getDataSetId();
this.sampleMapId = dto.getSampleMapId();
this.sampleId = dto.getSampleId();
this.sampleTag = dto.getSampleTag();
this.runOrderIdx = dto.getRunOrderIdx();
this.otherId = dto.getOtherId();
this.comment = dto.getComment();
}

public void updateMissing(Ms2SampleMapDTO dto)
{
if (StringUtils.checkEmptyOrNull(dataSetId))
this.dataSetId = dto.getDataSetId();

if (StringUtils.checkEmptyOrNull(sampleId))
this.sampleId = dto.getSampleId();

if (StringUtils.checkEmptyOrNull(sampleTag))
this.sampleTag = dto.getSampleTag();

if (this.runOrderIdx == null || this.runOrderIdx == 1)
this.runOrderIdx = dto.getRunOrderIdx();

if (StringUtils.checkEmptyOrNull(this.otherId))
this.otherId = dto.getOtherId();

if (StringUtils.checkEmptyOrNull(this.comment))
this.comment = dto.getComment();
}


public String getSampleMapId()
{
return sampleMapId;
}

public void setSampleMapId(String sampleMapId)
{
this.sampleMapId = sampleMapId;
}

public String getSampleId()
{
return sampleId;
}

public void setSampleId(String sampleId)
{
this.sampleId = sampleId;
}

public String getSampleTag()
{
return sampleTag;
}

public void setSampleTag(String sampleTag)
{
this.sampleTag = sampleTag;
}

public String getDataSetId()
{
return this.dataSetId;
}

public void setDataSetId(String dsid)
{
this.dataSetId = dsid;
}

public Integer getRunOrderIdx() 
{
return runOrderIdx;
}

public void setRunOrderIdx(Integer runOrderIdx) 
{
this.runOrderIdx = runOrderIdx;
}

public String getOtherId() 
{
return otherId;
}

public void setOtherId(String otherId) 
{
this.otherId = otherId;
}

// Sample

public String getComment()
{
return this.comment;
}

public void setComment(String comment)
{
this.comment = comment;
}
}

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "WELLS")
public class PrepWell implements Serializable
	{

	public static PrepWell instance(Integer index, String location, String plateFormat)
		{
		return new PrepWell(null, index, location, plateFormat);
		}

	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "PrepWell"),
			@Parameter(name = "width", value = "4") })
	@Column(name = "WELL_ID", unique = true, nullable = false, length = 4, columnDefinition = "CHAR(4)")
	private String wellID;

	@Basic()
	@Column(name = "HAMILTON_INDEX", nullable = true, columnDefinition = "NUMBER(2)")
	private Integer index;

	@Basic()
	@Column(name = "LOCATION", nullable = true, columnDefinition = "VARCHAR2(3)")
	private String location;

	@Basic()
	@Column(name = "PLATE_FORMAT_ID", nullable = true, columnDefinition = "CHAR(4)")
	private String plateFormat;

	
	private PrepWell(String wellID, Integer index, String location,
			String plateFormat)
		{
		this.wellID = wellID;
		this.index = index;
		this.plateFormat = plateFormat;
		this.location = location;
		}

	public PrepWell() {  } 

	public String getWellID()
		{
		return wellID;
		}

	public void setWellID(String wellID)
		{
		this.wellID = wellID;
		}

	public Integer getIndex()
		{
		return index;
		}

	public void setIndex(Integer index)
		{
		this.index = index;
		}

	public String getLocation()
		{
		return location;
		}

	public void setLocation(String location)
		{
		this.location = location;
		}

	public String getPlateFormat()
		{
		return plateFormat;
		}

	public void setPlateFormat(String plateFormat)
		{
		this.plateFormat = plateFormat;
		}
	}

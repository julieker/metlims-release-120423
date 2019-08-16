package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity()
@Table(name = "LOCATIONS")
public class Location implements Serializable
	{
	public static Location instance(String locationId, String description, String room, String unit)
		{
		return new Location(locationId, description, room, unit);
		}

	@Id()
	@Column(name = "LOCATIONID", nullable = false, unique = true, length = 6, columnDefinition = "CHAR(6)")
	private String locationId;

	@Basic()
	@Column(name = "LOCDESCRIPTION", nullable = false, length = 100, columnDefinition = "VARCHAR2(100)")
	private String description;

	@Basic()
	@Column(name = "ROOM", nullable = false, length = 50, columnDefinition = "VARCHAR2(50)")
	private String room;

	@Basic()
	@Column(name = "UNIT", nullable = false, length = 50, columnDefinition = "VARCHAR2(50)")
	private String unit;

	private Location(String locationId, String description, String room, String unit)
		{
		this.description = description;
		this.locationId = locationId;
		this.room = room;
		this.unit = unit;
		}

	public  Location()  {  } 

	
	public String getLocationId()
		{
		return locationId;
		}

	public String getDescription()
		{
		return description;
		}

	public String getRoom()
		{
		return room;
		}

	public String getUnit()
		{
		return unit;
		}
	}

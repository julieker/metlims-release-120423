package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "PRIORITY")
public class Priority implements Serializable 
	{
	public static String LOW = "LOW";
	public static String NORMAL = "NORM";
	public static String MEDIUM = "MED";
	public static String HIGH = "HIGH";
	public static String URGENT = "URGENT";
	public static List<String> PRIORITIES = Arrays.asList(new String[] { LOW, NORMAL, MEDIUM, HIGH, URGENT });
	
	public static Priority instance(String id) 
		{
		return new Priority(id, null, null, null);
		}

	@Id()
	@Column(name = "PRIORITY_ID", unique = true, nullable = false, length = 10, columnDefinition = "VARCHAR2(10)")
	private String id;

	@Basic()
	@Column(name = "SHORT_NAME", nullable = false, length = 16, columnDefinition = "VARCHAR2(16)")
	private String shortName;

	@Basic()
	@Column(name = "DESCRIPTION", nullable = true, length = 64, columnDefinition = "VARCHAR2(64)")
	private String description;

	@Basic()
	@Column(name = "SORT_VALUE", nullable = false, precision = 2, scale = 0, columnDefinition = "NUMBER(2,0)")
	private Integer sortValue;

	
	public Priority() {  }

	
	private Priority(String id, String shortName, String description, Integer sortValue) {
		this.id = id;
		this.shortName = shortName;
		this.description = description;
		this.sortValue = sortValue;
	}

	public String getId() {
		return id;
	}

	public String getShortName() {
		return shortName;
	}

	public String getDescription() {
		return description;
	}

	public Integer getSortValue() {
		return sortValue;
	}
}

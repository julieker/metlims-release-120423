///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	LipidMapsClass.java
// 	Written by Jan Wigginton July 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity()
@Table(name = "EXTERNAL_DB.LIPIDMAPS_CLASSES")
public class LipidMapsClass implements Serializable
	{

	public static LipidMapsClass instance(String classId, String classLevel, String className) {
		return new LipidMapsClass(classId, classLevel, className);
	}
		
	@Id()
	@Column(name = "CLASS_ID", nullable = false, unique = true, columnDefinition = "VARCHAR2(30)")
	private String classId;

	@Basic()
	@Column(name = "CLASS_LEVEL", nullable = true, columnDefinition = "VARCHAR2(30)")
	private String classLevel;

	@Basic()
	@Column(name = "NAME", nullable = true, columnDefinition = "VARCHAR2(4000)")
	private String className;

	
	public LipidMapsClass() 
		{
		}
	
	public LipidMapsClass(String classId, String classLevel, String className)
		{
		this.classId = classId;
		this.classLevel= classLevel;
		this.className = className;
		}

	public String getClassId()
		{
		return classId;
		}

	public void setClassId(String classId)
		{
		this.classId = classId;
		}

	public String getClassLevel()
		{
		return classLevel;
		}

	public void setClassLevel(String classLevel)
		{
		this.classLevel = classLevel;
		}

	public String getClassName()
		{
		return className;
		}

	public void setClassName(String className)
		{
		this.className = className;
		}
	}

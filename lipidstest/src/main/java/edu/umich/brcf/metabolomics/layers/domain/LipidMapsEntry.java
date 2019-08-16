///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	LipidMapsEntry.java
// 	Written by Jan Wigginton July 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


@Entity
@Table(name = "EXTERNAL_DB.LIPIDMAPS_DATA")
public class LipidMapsEntry implements Serializable, IWriteConvertable
	{
	
	@Id()
	@Column(name = "LMID", unique = true, nullable = false, columnDefinition = "VARCHAR2(30)")
	String lipidMapsId;

	@Basic
	@Column(name = "SYSTEMATIC_NAME", unique = false, nullable  = true, columnDefinition = "VARCHAR2(1000)")
	String systematicName;
	
	@Basic
	@Column(name = "COMMON_NAME", unique = false, nullable  = true, columnDefinition = "VARCHAR2(1000)")
	String commonName;
	
	@Basic
	@Column(name = "MAIN_CLASS", unique = false, nullable = true, columnDefinition = "VARCHAR2(30)")
	String mainClass;
	

	@Basic
	@Column(name = "SUB_CLASS", unique = false, nullable = true, columnDefinition = "VARCHAR2(30)")
	String subClass;

	@Basic
	@Column(name = "CLASS_LEVEL4", unique = false, nullable = true, columnDefinition = "VARCHAR2(30)")
	String classLevel4;

	@Basic
	@Column(name = "MOLECULAR_FORMULA", unique = false, nullable = true, columnDefinition = "VARCHAR2(300)")
	String molecularFormula;

	@Basic
	@Column(name = "EXACT_MASS", unique = false, nullable = true, columnDefinition = "NUMBER")
	String exactMass;

	@Basic
	@Column(name = "INCHI_KEY", unique = false, nullable = true, columnDefinition = "VARCHAR2(1000)")
	String inchiKey;

	@Basic
	@Column(name = "CATEGORY", unique = false, nullable = true, columnDefinition = "VARCHAR2(30)")
	String category;

	@Basic
	@Column(name = "SMILES", unique = false, nullable = true, columnDefinition = "VARCHAR2(2000)")
	String smiles;

	public LipidMapsEntry()
		{
		}
	
	public LipidMapsEntry(String lmid, String systematicName, String commonName, String mainClass, String subClass,
			String classLevel4, String molecularFormula, String exactMass, String inchiKey, String category,
			String smiles)
		{
		this.lipidMapsId = lmid;
		this.systematicName = systematicName;
		this.commonName = commonName;
		this.mainClass = mainClass;
		this.subClass = subClass;
		this.classLevel4 = classLevel4;
		this.molecularFormula = molecularFormula;
		this.exactMass = exactMass;
		this.inchiKey = inchiKey;
		this.category  = category;
		this.smiles = smiles;
		}
			
	
	public String getLipidMapsId()
		{
		return lipidMapsId;
		}


	public void setLipidMapsId(String lipidMapsId)
		{
		this.lipidMapsId = lipidMapsId;
		}

	
	public String getSystematicName()
		{
		return systematicName;
		}

	
	public void setSystematicName(String systematicName)
		{
		this.systematicName = systematicName;
		}

	
	public String getCommonName()
		{
		return commonName;
		}

	
	public void setCommonName(String commonName)
		{
		this.commonName = commonName;
		}

	
	public String getMainClass()
		{
		return mainClass;
		}

	
	public void setMainClass(String mainClass)
		{
		this.mainClass = mainClass;
		}

	
	public String getSubClass()
		{
		return subClass;
		}

	
	public void setSubClass(String subClass)
		{
		this.subClass = subClass;
		}

	
	public String getClassLevel4()
		{
		return classLevel4;
		}

	
	public void setClassLevel4(String classLevel4)
		{
		this.classLevel4 = classLevel4;
		}

	
	public String getMolecularFormula()
		{
		return molecularFormula;
		}

	
	public void setMolecularFormula(String molecularFormula)
		{
		this.molecularFormula = molecularFormula;
		}

	
	public String getExactMass()
		{
		return exactMass;
		}

	
	public void setExactMass(String exactMass)
		{
		this.exactMass = exactMass;
		}

	
	public String getInchiKey()
		{
		return inchiKey;
		}

	
	public void setInchiKey(String inchiKey)
		{
		this.inchiKey = inchiKey;
		}

	
	public String getCategory()
		{
		return category;
		}

	
	public void setCategory(String category)
		{
		this.category = category;
		}

	
	public String getSmiles()
		{
		return smiles;
		}

	
	public void setSmiles(String smiles)
		{
		this.smiles = smiles;
		}

	
	
	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.lipidMapsId + separator);
		sb.append(this.systematicName + separator);
		sb.append(this.commonName + separator);
		sb.append(this.mainClass + separator);
		sb.append(this.subClass + separator);
		sb.append(this.classLevel4 + separator);
		sb.append(this.molecularFormula + separator);

		sb.append(this.exactMass + separator);
		sb.append(this.inchiKey + separator);
		sb.append(this.category + separator);
		sb.append(this.smiles + separator);

		return sb.toString();
		}
	

	@Override
	public String toExcelRow() 
		{
		// TODO Auto-generated method stub
		return null;
		}	
	}
	
	

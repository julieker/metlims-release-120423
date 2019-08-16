///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 	LipidBlastPrecursor.java
// 	Written by Jan Wigginton July 2015
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;



@Entity
@Table(name = "EXTERNAL_DB.LIPID_BLAST_PRECURSOR")
public class LipidBlastPrecursor implements Serializable, IWriteConvertable
	{
	@Id()
	@Column(name = "LB_ID", unique = true, nullable = false, columnDefinition = "CHAR(10)")
	private String lipidId;
	
	@Basic
	@Column(name = "CODE_NAME", unique = true, nullable = false, columnDefinition = "VARCHAR2(120)")
	private String codeName;
	
	@Basic
	@Column(name = "FULL_NAME", unique = true, nullable = false, columnDefinition = "VARCHAR2(2000)")
	private String fullName;
	
	@Basic
	@Column(name = "MOL_FORMULA", unique = false, nullable = true, columnDefinition = "VARCHAR2(50)")
	private String molecularFormula;

	//Need to verify number formats here

	@Basic
	@Column(name = "PRECURSOR_MZ", unique = false, nullable = true, columnDefinition = "NUMBER(22, 5)")
	private String precursorMz;
	
	@Basic
	@Column(name = "ADDUCT", unique = true, nullable = false, columnDefinition = "VARCHAR2(120)")
	private String adduct;
	 
	@Basic
	@Column(name = "MS_MODE", unique = false, nullable = true, columnDefinition = "CHAR(1)")
	private String msMode;
	
	@Basic
	@Column(name = "CLASS_CODE", unique = false, nullable = true, columnDefinition = "VARCHAR2(120)")
	private String classCode;
	

	//Need to verify number formats here

	@Basic
	@Column(name = "FORMULA_MASS", unique = false, nullable = true, columnDefinition = "NUMBER(30)")
	private String formulaMass;
	
	///
	//@OneToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "PARENT_ID", referencedColumnName = "SAMPLE_ID", nullable = true, columnDefinition = "CHAR(9)")
	//private Sample parent;
	//
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LIPID_MAPS_CLASS", referencedColumnName = "CLASS_ID", nullable = true, columnDefinition = "VARCHAR2(30)")
	private LipidMapsClass lipidMapsClass;
	
	
	public LipidBlastPrecursor()
		{
		}

	public LipidBlastPrecursor(String lid, String codeName, String fullName, String mFormula, String pMass, 
			String adduct, String msMode, String fMass,  LipidMapsClass lmClass)
		{
		this.lipidId = lid;
		this.codeName = codeName;
		this.fullName = fullName;
		this.molecularFormula = mFormula;
		this.precursorMz = pMass;
		this.adduct = adduct;
		this.msMode = msMode;
		this.formulaMass = fMass;
		this.lipidMapsClass = lmClass;
		}
	public String getMolecularFormula()
		{
		return molecularFormula;
		}

	public void setMolecularFormula(String molecularFormula)
		{
		this.molecularFormula = molecularFormula;
		}
	
	public String getLipidId()
		{
		return lipidId;
		}
	public void setLipidId(String lipidId)
		{
		this.lipidId = lipidId;
		}
	public String getPrecursorMz()
		{
		return precursorMz;
		}
	public void setPrecursorMz(String precursorMz)
		{
		this.precursorMz = precursorMz;
		}
	public String getMsMode()
		{
		return msMode;
		}
	public void setMsMode(String msMode)
		{
		this.msMode = msMode;
		}
	public String getFormulaMass()
		{
		return formulaMass;
		}
	public void setFormulaMass(String formulaMass)
		{
		this.formulaMass = formulaMass;
		}
	public LipidMapsClass getLipidMapsClass()
		{
		return lipidMapsClass;
		}
	public void setLipidMapsClass(LipidMapsClass lipidMapsClass)
		{
		this.lipidMapsClass = lipidMapsClass;
		}

	public String getFullName()
		{
		return fullName;
		}

	public void setFullName(String fullName)
		{
		this.fullName = fullName;
		}

	public String getClassCode()
		{
		return classCode;
		}

	public void setClassCode(String classCode)
		{
		this.classCode = classCode;
		}



	@Override
	public String toCharDelimited(String separator)
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.lipidId + separator);
		sb.append(this.fullName + separator);
		sb.append(this.molecularFormula + separator);
		sb.append(this.precursorMz + separator);
		sb.append(this.msMode + separator);
		sb.append(this.classCode + separator);
		sb.append(this.formulaMass + separator);
		sb.append(this.lipidMapsClass.getClassId() + separator);
		String s = sb.toString();
		System.out.println(s);
		return sb.toString();
		}
	
	@Override
	public String toExcelRow()
		{
		// TODO Auto-generated method stub
		return null;
		}
	}

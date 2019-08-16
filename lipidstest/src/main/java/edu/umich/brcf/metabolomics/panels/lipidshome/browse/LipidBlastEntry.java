///////////////////////////////////////
// LipidBlastEntry.java
// Written by Jan Wigginton May 2015
///////////////////////////////////////
package edu.umich.brcf.metabolomics.panels.lipidshome.browse;

import java.io.Serializable;


public class LipidBlastEntry implements Serializable
	{
	String lipidId;
	String fullName;
	String molecularFormula;
	String precursorMz;
	String msMode;
	String classCode;
	String formulaMass;
	String lipidMapsClass;
	
	public LipidBlastEntry()   {  } 
	
	public LipidBlastEntry(String lid, String mFormula, String pMass, String msMode, String fMass,  String lmClass)
		{
		lipidId = lid;
		molecularFormula = mFormula;
		pMass = precursorMz;
		this.msMode = msMode;
		formulaMass = fMass;
		lipidMapsClass = lmClass;
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
	
	public String getLipidMapsClass()
		{
		return lipidMapsClass;
		}
	
	public void setLipidMapsClass(String lipidMapsClass)
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
	}

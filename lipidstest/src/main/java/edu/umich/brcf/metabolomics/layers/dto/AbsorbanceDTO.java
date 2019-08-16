package edu.umich.brcf.metabolomics.layers.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class AbsorbanceDTO implements Serializable{
	
	private String wellIndex;
	private BigDecimal absorbance1;
	private BigDecimal absorbance2;
	private BigDecimal concentration;

	public static AbsorbanceDTO instance(String wellIndex, BigDecimal absorbance1, BigDecimal absorbance2, BigDecimal concentration) {
		return new AbsorbanceDTO(wellIndex, absorbance1, absorbance2, concentration);
	}
	
	private AbsorbanceDTO(String wellIndex, BigDecimal absorbance1, BigDecimal absorbance2, BigDecimal concentration){
		this.wellIndex = wellIndex;
		this.absorbance1 = absorbance1;
		this.absorbance2 = absorbance2;
		this.concentration = concentration;
	}
	
	public AbsorbanceDTO() {
	}

	public String getWellIndex() {
		return wellIndex;
	}

	public void setWellIndex(String wellIndex) {
		this.wellIndex = wellIndex;
	}

	public BigDecimal getAbsorbance1() {
		return absorbance1;
	}

	public void setAbsorbance1(BigDecimal absorbance1) {
		this.absorbance1 = absorbance1;
	}

	public BigDecimal getAbsorbance2() {
		return absorbance2;
	}

	public void setAbsorbance2(BigDecimal absorbance2) {
		this.absorbance2 = absorbance2;
	}

	public BigDecimal getConcentration() {
		return concentration;
	}

	public void setConcentration(BigDecimal concentration) {
		this.concentration = concentration;
	}
	
	public String toString()
    {
		try{
			return "[WellIndex=" + wellIndex + ", Absorbance1=" + absorbance1 + 
			", Absorbance2=" + absorbance2 + ", Concentration=" + concentration +"]";
		}catch (Exception ex){return "";}
    }
}

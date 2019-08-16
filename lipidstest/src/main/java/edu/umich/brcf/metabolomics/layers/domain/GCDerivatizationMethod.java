package edu.umich.brcf.metabolomics.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity()
@Table(name = "GC_DERIVATIZATION_SOP")
public class GCDerivatizationMethod implements Serializable{
	
	public final static String DEFAULT_SOP="GD000001";

	public static GCDerivatizationMethod instance(String reagentComposition,String incubationConditions ,
			BigDecimal derivatizationVolume){
		return new GCDerivatizationMethod(null, reagentComposition, incubationConditions, derivatizationVolume);		
	}
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "GCDerivatization"), @Parameter(name = "width", value = "8") })
	@Column(name = "GCD_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String derivatizationID;
	
	@Basic()
	@Column(name = "REAGENT_COMPOSITION", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String reagentComposition;

	@Basic()
	@Column(name = "INCUBATION_CONDITIONS", nullable = true, columnDefinition = "VARCHAR2(100)")
	private String incubationConditions;
	
	@Basic()
	@Column(name = "DERIVATIZATION_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal derivatizationVolume;

	private GCDerivatizationMethod(String derivatizationID,String reagentComposition,String incubationConditions ,
			BigDecimal derivatizationVolume){
		this.derivatizationID=derivatizationID;
		this.reagentComposition=reagentComposition;
		this.incubationConditions=incubationConditions;
		this.derivatizationVolume=derivatizationVolume;
	}

	public GCDerivatizationMethod() {
	}
	
	public String getDerivatizationID() {
		return derivatizationID;
	}

	public String getReagentComposition() {
		return reagentComposition;
	}

	public String getIncubationConditions() {
		return incubationConditions;
	}

	public BigDecimal getDerivatizationVolume() {
		return derivatizationVolume;
	}
	public String toString()
    {
		try{
			return "[DerivatizationID=" + derivatizationID + ", ReagentComposition=" + reagentComposition + 
			", IncubationConditions=" + incubationConditions + ", DerivatizationVolume=" + derivatizationVolume +"]";
		}catch (Exception ex){return "";}
    }
}

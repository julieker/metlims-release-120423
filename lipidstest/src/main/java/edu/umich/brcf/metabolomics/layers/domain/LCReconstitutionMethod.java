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
@Table(name = "LC_RECONSTITUTION_SOP")
public class LCReconstitutionMethod implements Serializable{
	
	public final static String DEFAULT_SOP="LR000001";
	
	public static LCReconstitutionMethod instance(String reconSolvent,	BigDecimal reconVolume){
		return new LCReconstitutionMethod(null, reconSolvent, reconVolume);		
	}
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "LCReconstitution"), @Parameter(name = "width", value = "8") })
	@Column(name = "LCR_ID", unique = true, nullable = false, length = 8, columnDefinition = "CHAR(8)")
	private String reconstitutionID;
	
	@Basic()
	@Column(name = "RECON_SOLVENT", nullable = true, columnDefinition = "VARCHAR2(30)")
	private String reconSolvent;
	
	@Basic()
	@Column(name = "RECON_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal reconVolume;
	
	private LCReconstitutionMethod(String reconstitutionID,String reconSolvent,	BigDecimal reconVolume){
		this.reconstitutionID=reconstitutionID;
		this.reconSolvent=reconSolvent;
		this.reconVolume=reconVolume;
	}
	
	public LCReconstitutionMethod() {
	}
	
	public String getReconstitutionID() {
		return reconstitutionID;
	}

	public String getReconSolvent() {
		return reconSolvent;
	}

	public BigDecimal getReconVolume() {
		return reconVolume;
	}
	public String toString()
    {
		try{
			return "[ReconstitutionID=" + reconstitutionID + ", ReconSolvent=" + reconSolvent + 
			", ReconVolume=" + reconVolume +"]";
		}catch (Exception ex){return "";}
    }
}

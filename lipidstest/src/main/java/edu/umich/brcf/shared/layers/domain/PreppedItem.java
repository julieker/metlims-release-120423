package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;


@Entity()
@Table(name = "PREPPED_SAMPLES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class PreppedItem implements Serializable
	{
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "PreppedSample"), @Parameter(name = "width", value = "9") })
	@Column(name = "PREP_ITEM_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	private String itemID;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_PREP_ID", referencedColumnName = "SAMPLE_PREP_ID", nullable = true, columnDefinition = "CHAR(7)")
	private Preparation samplePrep;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WELL_ID", referencedColumnName = "WELL_ID", nullable = true, columnDefinition = "CHAR(4)")
	private PrepWell well;

	@Basic()
	@Column(name = "ALIQUOT_TYPE", nullable = true, columnDefinition = "VARCHAR2(10)")
	private String aliquotType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GENERAL_PREP_METHOD", referencedColumnName = "PREP_ID", nullable = true)
	private GeneralPrepSOP generalPrepSOP;
	
	@Basic()
	@Column(name = "VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal volume;
	
	@Basic()
	@Column(name = "VOLUME_UNITS", nullable = true, columnDefinition = "CHAR(2)")
	private String volUnits;
	
	@Basic()
	@Column(name = "BUFFER_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal bufferVolume;
	
	@Basic()
	@Column(name = "BUFFER_TYPE", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String bufferType;
	
	@Basic()
	@Column(name = "SAMPLE_DILUTED", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal sampleDiluted;
	
	@Basic()
	@Column(name = "DILUTANT", nullable = true, columnDefinition = "VARCHAR2(15)")
	private String dilutant;
	
	@Basic()
	@Column(name = "DILUTANT_VOLUME", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal dilutantVolume;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HOMOGENIZATION_SOP", referencedColumnName = "ID", nullable = true, columnDefinition = "CHAR(8)")
	private HomogenizationSOP homogenization;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROTIENDETERMINATION_SOP", referencedColumnName = "ID", nullable = true, columnDefinition = "CHAR(8)")
	private ProtienDeterminationSOP protienDetermination;
	
	@Basic()
	@Column(name = "PROTIEN_READING", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal protienReading;
	
	@Basic()
	@Column(name = "ABSORBANCE1", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal absorbance1;
	
	@Basic()
	@Column(name = "ABSORBANCE2", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal absorbance2;
	
	@Basic()
	@Column(name = "VOLUME_TRANSFERRED", nullable = true, columnDefinition = "NUMBER(8,3)")
	private BigDecimal volumeTransferred;
	
//	@OneToMany(mappedBy = "preppedItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//	List<Injections> injectionList;
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "MATRIX_PREP_METHOD", referencedColumnName = "PREP_ID", nullable = true, columnDefinition = "CHAR(8)")
//	private MatrixPrepSOP matrixPrepSOP;
	
	protected PreppedItem(String itemID, Preparation samplePrep, PrepWell well, String aliquotType, 
			GeneralPrepSOP generalPrepSOP, BigDecimal volume, String volUnits){//, MatrixPrepSOP matrixPrepSOP){
		this.itemID=itemID;
		this.samplePrep=samplePrep;
//		this.aliquot=aliquot;
//		this.well=well;
		this.aliquotType=aliquotType;
		this.generalPrepSOP=generalPrepSOP;
		this.volume=volume;
		this.volUnits=volUnits;
//		this.matrixPrepSOP=matrixPrepSOP;
	}
	
	public PreppedItem(){  } 
		

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public Preparation getSamplePrep() {
		return samplePrep;
	}

	public void setSamplePrep(Preparation samplePrep) {
		this.samplePrep = samplePrep;
	}

//	public Aliquot getAliquot() {
//		return aliquot;
//	}
//
//	public void setAliquot(Aliquot aliquot) {
//		this.aliquot = aliquot;
//	}

	public String getAliquotType() {
		return aliquotType;
	}

	public void setAliquotType(String aliquotType) {
		this.aliquotType = aliquotType;
	}

	public PrepWell getWell() {
		return well;
	}

	public void setWell(PrepWell well) {
		this.well = well;
	}

	public GeneralPrepSOP getGeneralPrepSOP() {
		return generalPrepSOP;
	}

	public void setGeneralPrepSOP(GeneralPrepSOP generalPrepSOP) {
		this.generalPrepSOP = generalPrepSOP;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getVolUnits() {
		return volUnits;
	}

	public void setVolUnits(String volUnits) {
		this.volUnits = volUnits;
	}

	public BigDecimal getBufferVolume() {
		return bufferVolume;
	}

	public void setBufferVolume(BigDecimal bufferVolume) {
		this.bufferVolume = bufferVolume;
	}

	public HomogenizationSOP getHomogenization() {
		return homogenization;
	}

	public void setHomogenization(HomogenizationSOP homogenization) {
		this.homogenization = homogenization;
	}

	public ProtienDeterminationSOP getProtienDetermination() {
		return protienDetermination;
	}

	public void setProtienDetermination(ProtienDeterminationSOP protienDetermination) {
		this.protienDetermination = protienDetermination;
	}

	public BigDecimal getProtienReading() {
		return protienReading;
	}

	public void setProtienReading(BigDecimal protienReading) {
		this.protienReading = protienReading;
	}

	public BigDecimal getVolumeTransferred() {
		return volumeTransferred;
	}

	public void setVolumeTransferred(BigDecimal volumeTransferred) {
		this.volumeTransferred = volumeTransferred;
	}

	public String getBufferType() {
		return bufferType;
	}

	public void setBufferType(String bufferType) {
		this.bufferType = bufferType;
	}

	public BigDecimal getSampleDiluted() {
		return sampleDiluted;
	}

	public void setSampleDiluted(BigDecimal sampleDiluted) {
		this.sampleDiluted = sampleDiluted;
	}

	public String getDilutant() {
		return dilutant;
	}

	public void setDilutant(String dilutant) {
		this.dilutant = dilutant;
	}

	public BigDecimal getDilutantVolume() {
		return dilutantVolume;
	}

	public void setDilutantVolume(BigDecimal dilutantVolume) {
		this.dilutantVolume = dilutantVolume;
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
	}

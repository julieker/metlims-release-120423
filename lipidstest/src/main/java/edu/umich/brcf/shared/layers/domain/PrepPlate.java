package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

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

import edu.umich.brcf.metabolomics.layers.domain.Instrument;



@Entity()
@Table(name = "PLATES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class PrepPlate implements Serializable 
	{
	public static String idFormat = "(PP)\\d{1}|(PP)\\d{2}|(PP)\\d{3}|(PP)\\d{4}|(PP)\\d{5}|(PP)\\d{6}|(PP)\\d{7}|\\d{3}|\\d{4}|\\d{5}";
	public static String FORMAT_96_WELL = "PF01";
	public static String FORMAT_56_WELL = "PF02";
	
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "PrepPlate"), @Parameter(name = "width", value = "9") })
	@Column(name = "PLATE_ID", unique = true, nullable = false, length = 9, columnDefinition = "CHAR(9)")
	protected String plateID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMPLE_PREP_ID", referencedColumnName = "SAMPLE_PREP_ID", nullable = true, columnDefinition = "CHAR(7)")
	protected Preparation samplePrep;

	@Basic()
	@Column(name = "PLATE_FORMAT_ID", nullable = true, columnDefinition = "CHAR(4)")
	protected String plateFormat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INSTRUMENT_ID", referencedColumnName = "INSTRUMENT_ID", nullable = true, columnDefinition = "CHAR(7)")
	protected Instrument instrument;

	protected PrepPlate(String plateID, Preparation samplePrep, String plateFormat, Instrument instrument) {
		this.plateID = plateID;
		this.samplePrep = samplePrep;
		this.plateFormat = plateFormat;
		this.instrument = instrument;
	}

	public String getNodeObjectName() {
		return instrument.getType() + " - " + plateID;
	}


	public PrepPlate() { }	

	public String getPlateID() {
		return plateID;
	}

	public void setPlateID(String plateID) {
		this.plateID = plateID;
	}

	public Preparation getSamplePrep() {
		return samplePrep;
	}

	public void setSamplePrep(Preparation samplePrep) {
		this.samplePrep = samplePrep;
	}

	public String getPlateFormat() {
		return plateFormat;
	}

	public void setPlateFormat(String plateFormat) {
		this.plateFormat = plateFormat;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
}

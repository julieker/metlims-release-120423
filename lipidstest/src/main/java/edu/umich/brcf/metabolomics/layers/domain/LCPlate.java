package edu.umich.brcf.metabolomics.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umich.brcf.metabolomics.layers.domain.Instrument;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;


@Entity()
@DiscriminatorValue(value = "LC")
public class LCPlate extends PrepPlate{

	public static LCPlate instance(Preparation samplePrep,
			String plateFormat, Instrument instrument, LCReconstitutionMethod reconstitutionMethod) {
		return new LCPlate(null, samplePrep,	plateFormat, instrument, reconstitutionMethod);

	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PREP_METHOD", referencedColumnName = "LCR_ID", nullable = true)
	protected LCReconstitutionMethod reconstitutionMethod;
	
	private LCPlate(String plateID, Preparation samplePrep,
			String plateFormat, Instrument instrument, LCReconstitutionMethod reconstitutionMethod) {
		super(plateID, samplePrep, plateFormat, instrument);
		this.reconstitutionMethod = reconstitutionMethod;
	}

	public LCPlate() {
	}

	public LCReconstitutionMethod getReconstitutionMethod() {
		return reconstitutionMethod;
	}

	public void setReconstitutionMethod(LCReconstitutionMethod reconstitutionMethod) {
		this.reconstitutionMethod = reconstitutionMethod;
	}
}

package edu.umich.brcf.shared.layers.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class RequiredStandardPK implements Serializable {
	public static RequiredStandardPK instance(String instrument, String standardType, String name) {
		return new RequiredStandardPK(instrument, standardType, name);
	}

	@Column(name = "INSTRUMENT_TYPE", length = 10, nullable = false)
	private String instrumentType;

	// @Enumerated(EnumType.STRING)
	@Column(name = "STANDARD_TYPE", length = 10, nullable = false)
	private String standardType;

	@Column(name = "NAME", length = 64, nullable = false)
	private String name;

	public RequiredStandardPK() {

	}

	private RequiredStandardPK(String instrument, String standardType, String name) {
		this.instrumentType = instrumentType;
		this.standardType = standardType;
		this.name = name;
	}

	public boolean equals(Object obj) {
		if (obj instanceof RequiredStandardPK) {
			RequiredStandardPK that = (RequiredStandardPK) obj;
			return this.instrumentType.equals(that.instrumentType) && this.name.equals(that.name)
					&& this.standardType.equals(that.standardType);
		}
		return false;
	}

	public int hashCode() {
		return instrumentType.hashCode() + standardType.hashCode() + name.hashCode();
	}

	public String getInstrumentType() {
		return instrumentType;
	}

	public String getStandardType() {
		return standardType;
	}

	public String getName() {
		return name;
	}
}

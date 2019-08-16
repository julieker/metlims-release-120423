package edu.umich.brcf.shared.util;


public enum METWorksStatusEnum {
	GOOD("green"), CAUTION("yellow"), ERROR("red");

	private String value;

	METWorksStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

package edu.umich.brcf.metabolomics.layers.domain;

import org.springframework.util.Assert;

public enum InstrumentClass
	{
	ANALYTICAL("ANALYTICAL"), //
	COMPUTER("COMPUTER"); //

	InstrumentClass(String value)
		{
		this.value = value;
		}

	String value;

	public String getValue()
		{
		return value;
		}

	public static InstrumentClass getEnumValue(String stringValue)
		{
		Assert.notNull(stringValue);

		for (InstrumentClass classValue : InstrumentClass.values())
			if (stringValue.equals(classValue.getValue()))
				return classValue;

		return null;
		}

	}

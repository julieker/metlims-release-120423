package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity()
@DiscriminatorValue(value = "S")
public class SamplePreparation extends Preparation
	{
	public static SamplePreparation instance(String title,User creator) {
		return new SamplePreparation(null, title, creator);
	}
	
	private SamplePreparation(String prepID, String title,User creator) {
		super(prepID, title, creator);
	}

	public SamplePreparation() {
	}
}

package edu.umich.brcf.metabolomics.layers.domain;

// Plate positions for multiple plates
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.User;


@Entity()
@DiscriminatorValue(value = "F")
public class FractionPreparation  extends Preparation{
	
	public static FractionPreparation instance(String title,User creator) {
		return new FractionPreparation(null, title, creator);
	}
	
	private FractionPreparation(String prepID, String title,User creator) {
		super(prepID, title, creator);
	}

	public FractionPreparation() {
	}
}

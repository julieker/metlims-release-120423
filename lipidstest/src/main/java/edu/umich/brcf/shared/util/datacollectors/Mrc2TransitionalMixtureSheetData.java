////////////////////////////////////////////////////
// Mrc2TransitionalMixtureSheetData.java
// Written by Julie Keros oct 2020  issue 94
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import edu.umich.brcf.shared.layers.service.AssayService;
import edu.umich.brcf.shared.util.interfaces.ISavableMixtureData;

public class Mrc2TransitionalMixtureSheetData implements Serializable, ISavableMixtureData
	{
	@SpringBean
	AssayService assayService;
	public Mrc2TransitionalExperimentDesign expDesign;
	public Mrc2MixtureMetadata mixtureMetadata;

	public static final int SHORT_LABEL_LEN = 35;

	// Initialize from the database

	public Mrc2TransitionalMixtureSheetData()
		{
		mixtureMetadata = new Mrc2MixtureMetadata();		
		}

	public Mrc2TransitionalMixtureSheetData(String expId)
		{
		Injector.get().inject(this);
		}

	@Override
	public int writeToDataBase() { return 0; } 

	@Override
	public int getSampleCount()
		{
		return 5;
		}
	public void setMixtureMetadata(Mrc2MixtureMetadata mixtureMetadata)
		{
		this.mixtureMetadata = mixtureMetadata;
		} 
	}


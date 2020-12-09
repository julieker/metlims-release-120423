//////////////////////////////////////////////
// Mrc2MixtureInfoItem.java
// Written by Jan Wigginton, September 2015
//////////////////////////////////////////////

package edu.umich.brcf.shared.util.datacollectors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import edu.umich.brcf.shared.layers.dto.MixtureDTO;
import edu.umich.brcf.shared.util.interfaces.IWriteConvertable;


public class Mrc2MixtureInfoItem implements Serializable, IWriteConvertable
	{
    private String volumeSolventToAdd; // issue 94
    private String desiredFinalVolume;
    private String createDate;
    private String createdBy;
    // issue 94
    private List<String> aliquotList;
    private List<String> aliquotVolumeList;
    private List<String> aliquotConcentrationList;
   // issue 94 
	public Mrc2MixtureInfoItem(String createDate, String createdBy, String volumeSolventToAdd, String desiredFinalVolume, List <String> aliquotList, List <String> aliquotVolumeList, List <String> aliquotConcentrationList)
		{
		this.volumeSolventToAdd  = volumeSolventToAdd;
		this.desiredFinalVolume = desiredFinalVolume;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.aliquotList = new ArrayList<String> ();
		this.aliquotList.addAll(aliquotList);
		this.aliquotVolumeList = new ArrayList<String> ();
		this.aliquotVolumeList.addAll(aliquotVolumeList);
		this.aliquotConcentrationList = new ArrayList<String> ();
		this.aliquotConcentrationList.addAll(aliquotConcentrationList);
		}
	
	public MixtureDTO toIncompleteSampleDTO()
		{
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		((DecimalFormat) format).setParseBigDecimal(true);
	
		BigDecimal volumeAsBigDecimal = null;
		BigDecimal desiredFinalVolumeasBigDecimal = null;
		try
			{
			volumeAsBigDecimal = (BigDecimal) format.parse(this.volumeSolventToAdd);
			desiredFinalVolumeasBigDecimal = (BigDecimal) format.parse(this.desiredFinalVolume);		
			}
		catch (ParseException e)
			{
			}		
		// Issue 244
		MixtureDTO dto = new MixtureDTO(); 
		dto.setDesiredFinalVolume(desiredFinalVolumeasBigDecimal== null? null :desiredFinalVolumeasBigDecimal.toString());
		dto.setVolumeSolventToAdd(volumeAsBigDecimal == null? null : volumeAsBigDecimal.toString());
		dto.setAliquotList(new ArrayList <String> ());
		dto.getAliquotList().addAll(this.aliquotList);
		dto.setAliquotVolumeList(new ArrayList <String> ());
		dto.getAliquotVolumeList().addAll(this.aliquotVolumeList);
		dto.setAliquotConcentrationList(new ArrayList <String> ());
		dto.getAliquotConcentrationList().addAll(this.aliquotConcentrationList);		
		return dto;
		}
	
	public String toString()
		{
		return toTokens().toString();
		//return toCharDelimited(",");
		}
	
	public List<String> toTokens()
		{
		List <String> lst = new ArrayList<String>();
		lst.add(this.desiredFinalVolume);
		lst.add(this.volumeSolventToAdd);		
		return lst;
		}

	@Override
	public String toCharDelimited(String delimiter) 
	    {
		// TODO Auto-generated method stub
		return null;
	    }

	@Override
	public String toExcelRow() 
	    {
		// TODO Auto-generated method stub
		return null;
	    }

}

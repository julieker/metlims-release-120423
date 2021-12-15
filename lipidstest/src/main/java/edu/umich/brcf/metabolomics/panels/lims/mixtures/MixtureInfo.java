package edu.umich.brcf.metabolomics.panels.lims.mixtures;
/* ___________________________
 * Created by Julie Keros March 18 2021
 */

import java.io.Serializable;
import java.util.List;


import java.util.ArrayList;



import org.apache.wicket.spring.injection.annot.SpringBean;



import edu.umich.brcf.shared.layers.dao.MixtureDAO;
import edu.umich.brcf.shared.layers.service.AliquotService;
import edu.umich.brcf.shared.layers.service.MixtureService;


public class MixtureInfo implements Serializable
    {
	String mixtureId;
 	String mixtureVolumeTxt;
 	String mixtureConcentrationTxt;
 	String volumeMixtureUnits;
 	List <MixAliquotInfo> mAliquotList;
 	List <Object[]> listObject = new ArrayList <Object[]> ();
 	MixtureDAO mixtureDao;
 	List <MixAliquotInfo> listMixAliquotInfo = new ArrayList <MixAliquotInfo>();
 	@SpringBean
	MixtureService mixtureService;
 	@SpringBean
 	AliquotService aliquotService;
 	List <String> aliquotDryList = new ArrayList <String> (); // issue 196 	
 	List <String> listAliquots = new ArrayList <String> ();
 	String expandText = "+";
 	
 	// issue 196
 	public List <String> getAliquotDryList ()
 		{
 		return this.aliquotDryList;
 		}
 	
 	public void setAliquotDryList (List <String> aliquotDryList)
		{
		this.aliquotDryList = aliquotDryList;
		}
 	
 	public String getExpandText ()
		{
		return this.expandText;
		}
	
	public void setExpandText (String expandText)
		{
		this.expandText =expandText; ;
		}
 	
	// issue 196
 	public String getVolumeMixtureUnits ()
		{
		return this.volumeMixtureUnits;
		}
	
 	// issue 196
	public void setVolumeMixtureUnits (String volumeMixtureUnits)
		{
		this.volumeMixtureUnits =volumeMixtureUnits; ;
		}
	
	
 	
	public List <Object[]> getListObject ()
		{
		return this.listObject;
		}
	
	public void setListObject (List <Object[]> listObject)
		{
		this.listObject = listObject;
		}
 	
 	public List <MixAliquotInfo> getListMixAliquotInfo ()
		{
		return this.listMixAliquotInfo;
		}
	
	public void setListMixAliquotInfo (List <MixAliquotInfo> listMixAliquotInfo)
		{
		this.listMixAliquotInfo = listMixAliquotInfo;
		}
 	
 	// issue 123
 	public List <String> getListAliquots ()
		{
		return this.listAliquots;
		}
	
	public void setListAliquots (List <String> listAliquots)
		{
		this.listAliquots = listAliquots;
		}
 	
	public void setMAliquotList (List<MixAliquotInfo> mAliquotList)
		{
		this.mAliquotList = mAliquotList;
		}
 	public List<MixAliquotInfo> getMAliquotList ()
		{
		listAliquots.clear();
		List <String> aliquotDryList = new ArrayList <String> ();
	
		if (listMixAliquotInfo.size() == 0)
			{
			for (Object[] result : listObject)
				{
				MixAliquotInfo mAliquotInfo = new MixAliquotInfo();
				mAliquotInfo.setAliquotId ((String) result[2]);
				mAliquotInfo.setMixtureId(result[0].toString());
				mAliquotInfo.setMixAliquotConcentration ( result[3].toString());
				mAliquotInfo.setMixAliquotConUnits(result[4] == null ? "" : result[4].toString());
				mAliquotInfo.setMixAliquotConcentrationFinal(" ");
				if (this.aliquotDryList.contains(mAliquotInfo.aliquotId))
				    {   
					mAliquotInfo.setWeightedAmountMix(result[7].toString());
					
					mAliquotInfo.setMolecularWeightMix(result[8].toString());
					mAliquotInfo.setWeightedAmountMixUnit(result[10].toString());//issue 196
				    }
				listMixAliquotInfo.add(mAliquotInfo);
				listAliquots.add (mAliquotInfo.getAliquotId());	
				}    
			return listMixAliquotInfo;
			}
		else
			{
			for (MixAliquotInfo mixali : listMixAliquotInfo)
				{
				}
			return listMixAliquotInfo;
			}
		}
 	//////
 	public String  getMixtureId()
		{
		return this.mixtureId;
		}
	public void  setMixtureId(String mixtureId)
		{
		this.mixtureId = mixtureId;
		}
	public String  getMixtureVolumeTxt()
		{
		return this.mixtureVolumeTxt;
		}
	public void  setMixtureVolumeTxt(String mixtureVolumeTxt)
		{
		this.mixtureVolumeTxt = mixtureVolumeTxt;
		}
	
	public String  getMixtureConcentrationTxt()
		{
		return this.mixtureConcentrationTxt;
		}
	
	public void  setMixtureConcentrationTxt(String mixtureConcentrationTxt)
		{
		this.mixtureConcentrationTxt = mixtureConcentrationTxt;
		}
	
	public void setMixtureDao(MixtureDAO mixtureDao) 
		{
		this.mixtureDao = mixtureDao;
		}
	public MixtureDAO getMixtureDao()
		{
		return mixtureDao;
		}
    }


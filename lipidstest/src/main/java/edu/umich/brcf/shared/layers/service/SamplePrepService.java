package edu.umich.brcf.shared.layers.service;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import edu.umich.brcf.metabolomics.layers.dao.InstrumentDAO;
import edu.umich.brcf.metabolomics.layers.domain.GCDerivatizationMethod;
import edu.umich.brcf.metabolomics.layers.domain.GCPlate;
import edu.umich.brcf.metabolomics.layers.domain.GeneralPrepSOP;
import edu.umich.brcf.metabolomics.layers.domain.HomogenizationSOP;
import edu.umich.brcf.metabolomics.layers.domain.LCPlate;
import edu.umich.brcf.metabolomics.layers.domain.LCReconstitutionMethod;
import edu.umich.brcf.metabolomics.layers.domain.ProtienDeterminationSOP;
import edu.umich.brcf.metabolomics.layers.dto.AbsorbanceDTO;
import edu.umich.brcf.metabolomics.layers.dto.GCDerivatizationDTO;
import edu.umich.brcf.metabolomics.layers.dto.GeneralPrepDTO;
import edu.umich.brcf.metabolomics.layers.dto.HomogenizationDTO;
import edu.umich.brcf.metabolomics.layers.dto.LCReconstitutionDTO;
import edu.umich.brcf.metabolomics.layers.dto.ProteinDeterminationDTO;
import edu.umich.brcf.metabolomics.panels.lims.prep.PrepMethodsPanel;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.SamplePrepDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.BiologicalSample;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.Observation;
import edu.umich.brcf.shared.layers.domain.PlatePrepObservation;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.PreppedItem;
import edu.umich.brcf.shared.layers.domain.PreppedSample;
import edu.umich.brcf.shared.layers.domain.Project;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.domain.SamplePrepObservation;
import edu.umich.brcf.shared.layers.domain.SamplePreparation;
import edu.umich.brcf.shared.layers.dto.BufferBean;
import edu.umich.brcf.shared.layers.dto.DilutionBean;
import edu.umich.brcf.shared.layers.dto.PrepInfoBean;
import edu.umich.brcf.shared.layers.dto.PreppedSampleDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;
import edu.umich.brcf.shared.util.FormatVerifier;
import edu.umich.brcf.shared.util.StringParser;
import edu.umich.brcf.shared.util.io.StringUtils;
import edu.umich.brcf.shared.util.structures.SelectableObject;
import edu.umich.brcf.shared.util.structures.ValueLabelBean;



@Transactional
public class SamplePrepService 
	{
	SamplePrepDAO samplePrepDao;
	UserDAO userDao;
	SampleDAO sampleDao;
	ExperimentDAO experimentDao;
	InstrumentDAO instrumentDao;
	
	
	public String savePrepData(String title, List<String> instruments, String[][] thisValues, String volume,  String volUnits, int cols, int rows) 
		{
		String plateFrmt = (cols==12 ? "PF01" : "PF02");
		Integer index = 0;
		String creator = ((MedWorksSession) Session.get()).getCurrentUserId();
		Preparation prep = samplePrepDao.saveSamplePrep(SamplePreparation.instance(title, userDao.loadById(creator)));
		
		for (Iterator<String> it = instruments.iterator(); it.hasNext();)
			{
			String instName=(String) it.next();
			PrepPlate prepPlate = 
				instName.startsWith("G") ?  GCPlate.instance(prep, plateFrmt, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null) 
										:   LCPlate.instance(prep, plateFrmt, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null);
			samplePrepDao.savePrepPlate(prepPlate);
			}	
		
		for (int j=0;j<cols;j++) 
			for (int i=0;i<rows;i++)
				{
				if (thisValues[i][j] == null) continue;
			
				++index;
				String id=thisValues[i][j];

				BiologicalSample sample=(BiologicalSample) sampleDao.loadSampleById(id);
				
				PreppedSample item=PreppedSample.instance(prep, sample, samplePrepDao.loadPrepWellByIndex(index, plateFrmt), 
						sample.getSampleType().getDescription(), null, new BigDecimal(volume), volUnits);
				samplePrepDao.savePreppedItem(item);
				sample.setPrepStatus();
				}
		
		return prep.getPrepID();
		}

	
	public GeneralPrepSOP loadGeneralPrepSOPByID(String id){
		return samplePrepDao.loadGeneralPrepSOPByID(id);
	}
	
	public GCDerivatizationMethod loadGCDerivatizationByID(String id){
		return samplePrepDao.loadGCDerivatizationByID(id);
	}
	
	public LCReconstitutionMethod loadLCReconstitutionByID(String id){
		return samplePrepDao.loadLCReconstitutionByID(id);
	}

	public void updateAliquotVolume(PreppedSampleDTO prepSample) 	
		{
		PreppedSample preppedSample = loadPreppedSampleByID(prepSample.getId());
		Sample sample = preppedSample.getSample();

		double sampleCurVol = sample.getCur_Volume().doubleValue();
		double aliquotVolume = Double.parseDouble(prepSample.getVolume());
		
		sampleCurVol +=  preppedSample.getVolume().doubleValue();
		if(!sample.getVolUnits().equals(preppedSample.getVolUnits()))
			{
			sampleCurVol=sampleCurVol+preppedSample.getVolume().doubleValue()/1000;	
			if(sample.getVolUnits().equals("ul"))
				sampleCurVol=sampleCurVol+preppedSample.getVolume().doubleValue()*1000;
			}
		
		sampleCurVol=sampleCurVol-aliquotVolume;
		if(!sample.getVolUnits().equals(prepSample.getVolUnits()))
			{
			sampleCurVol=sampleCurVol-aliquotVolume/1000;
			if(sample.getVolUnits().equals("ul"))
				sampleCurVol=sampleCurVol-aliquotVolume*1000;
			}
			
		sample.updateCurrentVolume(new BigDecimal(sampleCurVol));

		preppedSample.setVolume(new BigDecimal(aliquotVolume));
		preppedSample.setVolUnits(prepSample.getVolUnits().trim());
		preppedSample.setBufferType(prepSample.getBufferType());
		preppedSample.setBufferVolume(prepSample.getBufferVolume());
		preppedSample.setSampleDiluted(prepSample.getSampleDiluted());
		preppedSample.setDilutant(prepSample.getDilutant());
		preppedSample.setDilutantVolume(prepSample.getDilutantVolume());
		preppedSample.setVolumeTransferred(prepSample.getVolumeTransferred());
		}

	
	public void assignSamplePrepMethods(SelectableObject[][] samples, GeneralPrepSOP generalPrep, int cols, int rows) 
		{
		if (generalPrep == null) return;
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
				{
				PreppedSample prepSample=loadPreppedSampleByID((String)samples[i][j].getSelectionObject());
				prepSample.setGeneralPrepSOP(generalPrep);
				}
		}
	

	public List<Preparation> loadByCreatorId(String creatorId)
		{
		return samplePrepDao.loadyByCreatorId(creatorId);
		}
	
	public Preparation loadPreparationByID(String id) 
		{
		return samplePrepDao.loadById(id);
		}
	
	public PreppedSample loadPreppedSampleByID(String sample) {
		return samplePrepDao.loadPreppedSampleByID(sample);
	}

	public PreppedItem loadPreppedItemByID(String sample) {
		return samplePrepDao.loadPreppedItemByID(sample);
	}

	public List<PreppedSample> loadPreppedSamples(String preparation) {
		return samplePrepDao.loadPreppedSamples(preparation);
	}

	public List<PreppedSample> loadPreppedSamplesInNewOrder(String preparation) {
		return samplePrepDao.loadPreppedSamplesInNewOrder(preparation);
	}
	
	public List<Preparation> allPreparations() {
		return samplePrepDao.allPreparations();
	}
	
	public List<Preparation> allSamplePreparations() {
		return samplePrepDao.allSamplePreparations();
	}
	
	public List<String> allSamplePreparationsSortedByDate() {
		return samplePrepDao.allSamplePreparationsSortedByDate();
	}
	
	public List<String> allSamplePreparationsSortedByDateButShort() 
		{
		return samplePrepDao.allSamplePreparationsSortedByDateButShort();
		}
	
	
	public PrepPlate loadPlateByID(String id)
		{
		return samplePrepDao.loadPlateByID(id);
		}
	
	
	public String parsePrepID(String input)
		{
		String prep=input;
		
		if (FormatVerifier.verifyFormat(PrepPlate.idFormat, input))
			return samplePrepDao.loadPlateByID(input).getSamplePrep().getPrepID();
		
		if ((!FormatVerifier.verifyFormat(Preparation.idFormat, input))&&(FormatVerifier.matchFormat(Preparation.idFormat, input)))
			return StringParser.parseId(input);
		
		if(!FormatVerifier.verifyFormat(Preparation.idFormat, input))
			return null;
		
		return prep;
		}

	
	public List<Project> loadProjectExperimentByPrep(String prep) 
		{
		prep=parsePrepID(prep);
		return (prep == null ? null : experimentDao.loadProjectExperimentByPrep(prep));
		}

	
	public List<Experiment> loadExperimentsByPrep(String prep) 
		{
		prep=parsePrepID(prep);
		return (prep == null ? null : experimentDao.loadExperimentsByPrep(prep));
		}
	
	
	public GCDerivatizationMethod saveDerivatizationMethod(GCDerivatizationDTO prepDto) 
		{
		String prepID;
		
		if (prepDto.equals(loadGCDerivatizationByID(GCDerivatizationMethod.DEFAULT_SOP)))
			prepID=GCDerivatizationMethod.DEFAULT_SOP;
		else
			prepID= samplePrepDao.saveDerivatization(GCDerivatizationMethod.instance(prepDto.getReagentComposition(), 
					prepDto.getIncubationConditions(), prepDto.getDerivatizationVolume()));
		
		return samplePrepDao.loadGCDerivatizationByID(prepID);
		}

	
	public LCReconstitutionMethod saveReconstitutionMethod( LCReconstitutionDTO prepDto) 
		{
		String prepID;
		
		if (prepDto.equals(loadLCReconstitutionByID(LCReconstitutionMethod.DEFAULT_SOP)))
			prepID=LCReconstitutionMethod.DEFAULT_SOP;
		else
			prepID=samplePrepDao.saveReconstitution(LCReconstitutionMethod.instance(prepDto.getReconSolvent(), 
					prepDto.getReconVolume()));
		
		return samplePrepDao.loadLCReconstitutionByID(prepID);
		}

	
	public void assignPlatePrepMethods(GCDerivatizationMethod gcPrep, LCReconstitutionMethod lcPrep, String preparation) 
		{
		for (PrepPlate plate : loadPlatesByPreparation(preparation)) 
			if (plate instanceof GCPlate)
				((GCPlate) plate).setDerivatizationMethod(gcPrep);
			else
				((LCPlate) plate).setReconstitutionMethod(lcPrep);
		}
	
	
	public List<PrepPlate> loadPlatesByPreparation(String preparation) 
		{
		return samplePrepDao.loadPlatesByPreparation(preparation);
		}

	
	public void attachNotes(SelectableObject[][] samples, String notes, int cols, int rows) 
		{
		if (StringUtils.isEmptyOrNull(notes)) return;
		
		Observation observation=samplePrepDao.saveObservation(Observation.instance(notes));
		
		for (int i=0;i<rows;i++)	{
			for (int j=0;j<cols;j++)	{
				if(samples[i][j].isSelected())
					{
					PreppedItem prepSample=loadPreppedItemByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
					SamplePrepObservation sampleObservation=SamplePrepObservation.instance(prepSample, observation);
					samplePrepDao.saveObservationMap(sampleObservation);
					}
				}
			}
		}

	
	public void attachNotes(String preparation, String plateType, String notes) 
		{
		List<PrepPlate> plateLst=loadPlatesByPreparation(preparation);
		
		if (plateLst.size() <= 0) return;
		
		Observation observation=samplePrepDao.saveObservation(Observation.instance(notes));
		for (PrepPlate plate : plateLst) 
			{
			if ( plateType.equals("GC") && plate instanceof GCPlate || plateType.equals("LC") && plate instanceof LCPlate) 
				{
				PlatePrepObservation plateObservation=PlatePrepObservation.instance(plate, observation);
				samplePrepDao.saveObservationMap(plateObservation);}
				}
		}
	
	
	public List<ValueLabelBean> loadSampleObservationByPrepID(String prep) 
		{
		return samplePrepDao.loadSampleObservationByPrepID(prep);
		}
	
	
	public List<ValueLabelBean> loadPlateObservationByPrepID(String prep) 
		{
		return samplePrepDao.loadPlateObservationByPrepID(prep);
		}
	
	
	public List<String> getAllowedDuplicates()
		{
		return samplePrepDao.getAllowedDuplicates();
		}

	
	public GeneralPrepSOP saveGeneralPrep(GeneralPrepDTO prepDto) 
		{
		String prepID;
		if (prepDto.equals(loadGeneralPrepSOPByID(GeneralPrepSOP.DEFAULT_SOP)))
			prepID=GeneralPrepSOP.DEFAULT_SOP;
		else
			prepID= samplePrepDao.saveGeneralPrep(GeneralPrepSOP.instance(prepDto.getSampleVolume(), prepDto.getCrashSolvent(), prepDto.getRecoveryStandardContent(), 
							prepDto.getCrashVolume(), prepDto.getVortex(), prepDto.getSpin(), 
							prepDto.getNitrogenBlowdownTime(), prepDto.getLyophilizerTime(), 
							prepDto.getGcVolume(), prepDto.getLcVolume()));
		
		return loadGeneralPrepSOPByID(prepID);
		}
		
	
	public HomogenizationSOP saveHomogenization(HomogenizationDTO prepDto)
		{
		String prepID;
		if (prepDto.equals(loadHomogenizationByID(HomogenizationSOP.DEFAULT_SOP)))
			prepID=HomogenizationSOP.DEFAULT_SOP;
		else
			prepID=samplePrepDao.saveHomogenization(HomogenizationSOP.instance(prepDto.getBeadType(), 
					prepDto.getBeadSize(), prepDto.getBeadVolume(), prepDto.getVortex(),
					prepDto.getTime(), prepDto.getTemp()));
		
		return samplePrepDao.loadHomogenizationByID(prepID);
		}
	
	
	public ProtienDeterminationSOP saveProtienDetermination(ProteinDeterminationDTO prepDto) 
		{
		String prepID;
		if (prepDto.equals(loadProtienDeterminationSOPByID(ProtienDeterminationSOP.DEFAULT_SOP)))
			prepID=ProtienDeterminationSOP.DEFAULT_SOP;
		else
			prepID=samplePrepDao.saveProtienDetermination(ProtienDeterminationSOP.instance(
					prepDto.getBradfordAgent(), prepDto.getWavelength(), prepDto.getIncubationTime()));
		return samplePrepDao.loadProtienDeterminationSOPByID(prepID);
		}
	
	
	public HomogenizationSOP loadHomogenizationByID(String id)
		{
		return samplePrepDao.loadHomogenizationByID(id);
		}
	
	
	public ProtienDeterminationSOP loadProtienDeterminationSOPByID(String id)
		{
		return samplePrepDao.loadProtienDeterminationSOPByID(id);
		}

	
	public void assignSamplePrepMethods(SelectableObject[][] samples, PrepInfoBean bean, int cols, int rows) 
		{ 
		if (bean == null) return;
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
					{	
					PreppedSample prepSample=loadPreppedSampleByID((String)samples[i][j].getSelectionObject());
					prepSample.setBufferType(bean.getBufferType());
					prepSample.setBufferVolume(bean.getBufferVolume());
					prepSample.setGeneralPrepSOP(bean.getGeneralPrepMethod());
					prepSample.setHomogenization(bean.getHomogenization());
					prepSample.setProtienDetermination(bean.getProtienDetermination());
					prepSample.setSampleDiluted(bean.getSampleDiluted());
					prepSample.setDilutant(bean.getDilutant());
					prepSample.setDilutantVolume(bean.getDilutantVolume());
					}
		}

	public String assignProtienReadings(SelectableObject[][] samples, BigDecimal[][] proteinReadings, int cols, int rows) 
		{
		int count=0;
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				{
				BigDecimal val = proteinReadings[i][j];

				if(val  ==null) continue;
		
				try
					{
					PreppedSample prepSample=loadPreppedSampleByID((String)samples[i][j].getSelectionObject());
					prepSample.setProtienReading(val);
					double volTransferred= val.doubleValue()==0 ? 0 : ((0.55/val.doubleValue())*100);
					prepSample.setVolumeTransferred(new BigDecimal(volTransferred));
					count++;
					}
				catch (Exception e){ return (PrepMethodsPanel.rowIdentity[i]+(j+1)); }
				}
		
		return (count==0 ?  "0" : "none");
		}



	public String assignProtienReadings(String preparation, AbsorbanceDTO[] protienReadings) 
		{
		List<PreppedSample> samples=samplePrepDao.loadPreppedSamples(preparation);
		
		int count=0;
		for (int i=0;i<samples.size();i++) 
			{
			if(protienReadings[i] == null)
				continue;
			
			try{
				PreppedSample prepSample=samples.get(i);
				
				String wi=protienReadings[i].getWellIndex();
				wi = wi.substring(0, wi.indexOf("."));
				
				if(!(prepSample.getWell().getIndex().intValue()==Integer.parseInt(wi)))
					return i+1+"";

				prepSample.setAbsorbance1(protienReadings[i].getAbsorbance1());
				prepSample.setAbsorbance2(protienReadings[i].getAbsorbance2());
				prepSample.setProtienReading(protienReadings[i].getConcentration());
				count++;
				}
			catch (Exception e){  e.printStackTrace(); return (i+1+""); }
			}
		
		return (count==0 ?  "0" : "none");
		}
	
	
	public void assignGeneralPrep(SelectableObject[][] samples, GeneralPrepSOP sop, int cols, int rows) 
		{
		if (sop == null) return;
	
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
					{
					PreppedSample prepSample=loadPreppedSampleByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
					prepSample.setGeneralPrepSOP(sop);
					}
		}
	

	public void assignHomogenization(SelectableObject[][] samples, HomogenizationSOP sop, int cols, int rows) 
		{
		if (sop == null) return;
		
		for (int i=0;i<rows;i++) 
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
					{
					PreppedSample prepSample = loadPreppedSampleByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
					prepSample.setHomogenization(sop);
					}
		}

	
	public void assignProtienDetermination(SelectableObject[][] samples, ProtienDeterminationSOP sop, int cols, int rows) 
		{
		if (sop == null) return;
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				if(samples[i][j].isSelected())
					{
					PreppedSample prepSample=loadPreppedSampleByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
					prepSample.setProtienDetermination(sop);
					}
		}	

	
	public void assignDilution(SelectableObject[][] samples, DilutionBean bean, boolean isSamplePrep, int cols, int rows) 
		{
		if (bean == null) return;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) 
				{
				if(!samples[i][j].isSelected())
					continue; 
				
				PreppedItem prepItem=loadPreppedItemByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
				if(!isSamplePrep)
					{
					Sample fraction=((PreppedFraction) prepItem).getFraction();
					double sampleCurVol=fraction.getCur_Volume().doubleValue();
					if(prepItem.getSampleDiluted()!=null)
						sampleCurVol=sampleCurVol+prepItem.getSampleDiluted().doubleValue();
					sampleCurVol=sampleCurVol-bean.getSampleDiluted().doubleValue();
					fraction.updateCurrentVolume(new BigDecimal(sampleCurVol));
					}
				prepItem.setSampleDiluted(bean.getSampleDiluted());
				prepItem.setDilutant(bean.getDilutant());
				prepItem.setDilutantVolume(bean.getDilutantVolume());
				}
			}
		}		
		

	
	public void assignBuffer(SelectableObject[][] samples, BufferBean bean, int cols, int rows) 
		{
		if (bean == null) return;
		
		for (int i=0;i<rows;i++){
			for (int j=0;j<cols;j++){
				if(samples[i][j].isSelected())
					{
					PreppedSample prepSample=loadPreppedSampleByID(((PreppedSampleDTO)samples[i][j].getSelectionObject()).getId());
					prepSample.setBufferType(bean.getBufferType());
					prepSample.setBufferVolume(bean.getBufferVolume());
					}
				}
			}
		}

	
	public void eraseHomogenization(PreppedSampleDTO dto) 
		{
		PreppedSample prepSample=loadPreppedSampleByID(dto.getId());
		prepSample.setHomogenization(null);
		}


	public void assignDerivatization(String preparation, GCDerivatizationMethod gcPrep) 
		{
		for (PrepPlate plate : loadPlatesByPreparation(preparation)) 
			if (plate instanceof GCPlate)
				((GCPlate) plate).setDerivatizationMethod(gcPrep);
		}

	
	public void assignReconstitution(String preparation, LCReconstitutionMethod lcPrep) 
		{
		for (PrepPlate plate : loadPlatesByPreparation(preparation)) 
			if (plate instanceof LCPlate)
				((LCPlate) plate).setReconstitutionMethod(lcPrep);
		}
	
	
	private boolean verifyFormat(String format, String input) 
		{
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(input);
        return (matcher.find()&& input.length()==9);
		}
	

	
	public String validateAndSave(String title, List<String> instruments, String[][] thisValues, String volume, String volUnits, int cols, int rows)
		{
		if (existsTitle(title))
			return "A worklist already exists with the title - '"+title+"'!!!";
		
		String incorrectFormat="", retStr = "", txtFormat = "(S)\\d{8}" ; 
		
		int index = 0;
		for (int j=0;j<cols;j++)
			for (int i=0;i<rows;i++)
				{
				if (thisValues[i][j] == null) continue;
				++index;
				if(!verifyFormat(txtFormat,thisValues[i][j])) 
					incorrectFormat += thisValues[i][j] + ",";
				}
		
		
		if (incorrectFormat.length()>0)
			return  ("_Following samples have incorrect format: "+incorrectFormat.substring(0, incorrectFormat.lastIndexOf(",")));
					
		if (index==0)
			return retStr + ("_Cannot create a plate with 0 samples, please scan sample bar codes to create a plate!!");
	
		String msg = createAndSavePrepInfo(title, instruments, thisValues, volume, volUnits, cols, rows);
		if(msg.startsWith("Error"))
			return msg;
		
		return (retStr+ ("Save successful!!!_"+ msg));
		}
	
	
	public String validateForExperimentAndSave(String title, List<String> instruments, String[][] thisValues, String volume, String volUnits, int cols, int rows)
		{
		if (existsTitle(title))
			return "A worklist already exists with the title - '"+title+"'!!!";
		
		final String txtFormat = "(S)\\d{8}";
		String incorrectFormat="", retStr = "" ; //illegalSamples = "", retStr="";
		
		int index = 0;
		for (int j = 0; j < cols;j++)
			for (int i=0; i < rows; i++)
				if(thisValues[i][j]!=null)
					{
					++index;
					if(!verifyFormat(txtFormat,thisValues[i][j]))
						incorrectFormat += thisValues[i][j] + ",";
					}
		
		
		if (incorrectFormat.length()>0)
			return  ("_Following samples have incorrect format: "+incorrectFormat.substring(0, incorrectFormat.lastIndexOf(",")));
		
		if (index==0)
			return retStr + ("_Cannot create a plate with 0 samples, please scan sample bar codes to create a plate!!");
	
		String msg = createAndSavePrepInfo(title, instruments, thisValues, volume, volUnits, cols, rows);
		if(msg.startsWith("Error"))
			return msg;
		
		return (retStr+ ("Save successful!!!_"+ msg));
		}

	
	public String createAndSavePrepInfo(String title, List<String> instruments, String[][] thisValues, String volume, String volUnits, int cols, int rows) 
		{
		return savePrepData(title, instruments, thisValues, volume, volUnits, cols, rows);
		}
	
	private boolean existsTitle(String title) 
		{
        return samplePrepDao.existsTitle(title);
		}
	
	public double getUpdatedVol(double vol1, double vol2, String volUnits1, String volUnits2)
		{
		if (volUnits1.equals(volUnits2))
			return (vol1 - vol2);
		
		if(volUnits1.equals("ul"))
			return vol1-vol2*1000;
		
		return vol1-vol2/1000;	
		}

	public void updateFractionVolume(PreppedSampleDTO prepFraction) 
		{
		PreppedItem preppedItem=loadPreppedItemByID(prepFraction.getId());
		Sample fraction=((PreppedFraction) preppedItem).getFraction();
		
		double sampleCurVol=fraction.getCur_Volume().doubleValue();
		double fractionVolume=prepFraction.getSampleDiluted().doubleValue();
		if(preppedItem.getSampleDiluted()!=null)
			sampleCurVol=sampleCurVol+preppedItem.getSampleDiluted().doubleValue();
		sampleCurVol -= fractionVolume;
		
		fraction.updateCurrentVolume(new BigDecimal(sampleCurVol));
		preppedItem.setVolume(new BigDecimal(prepFraction.getVolume()));
		preppedItem.setVolUnits(prepFraction.getVolUnits().trim());
		preppedItem.setSampleDiluted(prepFraction.getSampleDiluted());
		preppedItem.setDilutant(prepFraction.getDilutant());
		preppedItem.setDilutantVolume(prepFraction.getDilutantVolume());
		}

	public String uploadFile(FileUpload upload, String title, String plateFrmt) 
		{
		final String sampleFormat = "(S)\\d{8}";
		int wells=plateFrmt.equals("96 Well")?96:54;
		List<PreppedSampleDTO> samples=new ArrayList<PreppedSampleDTO>();
		List<String> instruments=new ArrayList<String>();
		
		if (upload == null)
			return ("Please use the browse button and select a file to upload data from!!");
			
		if (! upload.getContentType().equalsIgnoreCase("application/vnd.ms-excel"))
			return ("Unable to upload file, unsupported file type!!");
			
        File newFile = new File(getUploadFolder(), upload.getClientFileName());
        checkFileExists(newFile);
        try
        	{
            newFile.createNewFile();
            upload.writeTo(newFile);
        	}
        catch (Exception e) { return ("Unable to write file!");  }
        
        int rowCount=0,cellCount=0,sheetNum=0;
        try
        	{
    		HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(newFile));
            HSSFSheet sheet = workbook.getSheetAt(0);
            ++sheetNum;
            Row row;
            PreppedSampleDTO dto;
            Iterator<Row> rowsIt = sheet.rowIterator();
            rowCount=0;
            String msg=null;
            
            while(rowsIt.hasNext())
            	{
            	++rowCount;
            	cellCount=0;
            	row=rowsIt.next();
            	if (rowCount>1 && rowCount<wells+2)
            		{
            		dto=new PreppedSampleDTO();
        			
            		if((row.getCell((short)0)==null)||(row.getCell((short)0).toString()==null)||(row.getCell((short)0).toString().trim().length()==0))
                		break;
        			 String sid=row.getCell((short)0).toString().trim();
        			 if(StringUtils.isEmptyOrNull(sid))
      		 			{ msg=("Error at line: "+rowCount+"! Sample ID cannot be blank!"); break;}
 
            			 if(!verifyFormat(sampleFormat,sid))
            				 return "Error in Sample ID format at line: "+rowCount+", cell 1";

        			 dto.setId(sid);++cellCount;
            		 String vol=row.getCell((short)1).toString().trim();
            		 if (StringUtils.isEmptyOrNull(vol))
            		 	return "Error at line: "+rowCount+". Volume cannot be blank.";
            		 
            		 dto.setVolume(vol);++cellCount;
            		 
            		 String volUnits=row.getCell((short)2).toString().trim();
            		 if (StringUtils.isEmptyOrNull(volUnits))
            				 return "Error at line: "+rowCount+". Volume Units cannot be blank.";
            		 
            		 dto.setVolUnits(volUnits);
            		 samples.add(dto);
            		}
            
            	else
            		{
                    sheet = workbook.getSheetAt(1); 
                    ++sheetNum;
                    rowsIt = sheet.rowIterator(); 
                    rowCount=0;
                    while(rowsIt.hasNext())
                    	{
                    	++rowCount;
                    	cellCount=0;
                    	row=rowsIt.next();
                    	if (rowCount <= 1)
                    		continue; 
                    	
                    	Cell cell = row.getCell(((short) 0));
                    	String val = cell == null ? "" : cell.toString();
                    	if(StringUtils.isEmptyOrNull(val))
	                    	break;
                    		
                    	String inst = row.getCell((short)0).toString().trim();
                    	if(StringUtils.isEmptyOrNull(inst))
                    		return "Error at line: "+rowCount+". Instrument cannot be blank.";
                    		
                    	if(!inst.equals("GC") && !inst.equals("LC") && !inst.equals("LC2"))
                    		return "Error at line: "+rowCount+"! Instrument value incorrect";
                    		
                    	instruments.add(inst);
                    	}
            		}
            	}
                
                if (samples.size()==0)
                	return("0 Samples were saved! Please review the data being uploaded!!");
                
                if (instruments.size()==0)
                	return("Please specify atleast one instrument to run the samples on!!");

                String creator = ((MedWorksSession) Session.get()).getCurrentUserId();
                String plateFormat=wells==96?"PF01":"PF02";
        		Preparation prep=samplePrepDao.saveSamplePrep(SamplePreparation.instance(title, userDao.loadById(creator)));
        		
        		for (Iterator<String> it = instruments.iterator(); it.hasNext();)
        			{
        			String instName=(String) it.next();
        			PrepPlate prepPlate=(instName.startsWith("G"))?
        					 GCPlate.instance(prep, plateFormat, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null): 
        					 LCPlate.instance(prep, plateFormat, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null);
        			samplePrepDao.savePrepPlate(prepPlate);
        			}
        		
        		int index=0;
        		for (PreppedSampleDTO preppedSample: samples)
        			{
            		++index;
            		
            		if(preppedSample == null)
            			return "Error creating item "+ index;
            			
        			String id = preppedSample.getId();
        			
					BiologicalSample sample=(BiologicalSample) sampleDao.loadSampleById(id);

					PreppedSample item = PreppedSample.instance(prep, sample, samplePrepDao.loadPrepWellByIndex(index, plateFormat), 
							sample.getSampleType().getDescription(), null, new BigDecimal(preppedSample.getVolume()), preppedSample.getVolUnits());
					samplePrepDao.savePreppedItem(item);
					sample.setPrepStatus();
					}
        		
        		return "Save successful. "+prep.getPrepID();
            	}
            catch (Exception e) { return ("Unable to upload file, error in sheet "+sheetNum +" at line: "+rowCount+", cell:"+cellCount); }
          
        }
       
	
	private void checkFileExists(File newFile)
		{
        if (newFile.exists() && !Files.remove(newFile))
        		throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
        }

    private Folder getUploadFolder()
    	{
    	Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "sample-uploads");
        uploadFolder.mkdirs();
        return (uploadFolder);
	    }
    

	public void createPlates(String prepId, List<String> instrumentName) 
		{
		Preparation prep=samplePrepDao.loadById(prepId);
		for (Iterator<String> it = instrumentName.iterator(); it.hasNext();){
			String instName=(String) it.next();
			List<PrepPlate> prepPlates = prep.getPlateList();
			String pltFrmt = prepPlates.get(0).getPlateFormat();
			PrepPlate prepPlate=(instName.startsWith("G"))?
					 GCPlate.instance(prep, pltFrmt, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null): 
					 LCPlate.instance(prep, pltFrmt, instrumentDao.getInstrumentById(Preparation.INSTRUMENTS.get(instName)),null);
			samplePrepDao.savePrepPlate(prepPlate);
		}
	}

	public List<String> getMatchingPreps(String input) 
		{
		return samplePrepDao.getMatchingPreps(input);
		}

	public List<String> getMatchingPlates(String input) 
		{
		return samplePrepDao.getMatchingPlates(input);
		}
	
	public boolean isValidPrepSearch(String p) 
		{
		Preparation prep;
		if (p == null || p.trim().equals(""))
			return false;
		
		if (FormatVerifier.verifyFormat(Preparation.idFormat,p.toUpperCase()))
			try{ prep = this.loadPreparationByID(p); }
			catch(EmptyResultDataAccessException e){ prep = null; }
		else
			try{ prep= this.loadPreparationByID(StringParser.parseId(p)); }
			catch(EmptyResultDataAccessException e) { prep = null; }
		
		return (prep!=null);
		}


	public void setSamplePrepDao(SamplePrepDAO samplePrepDao) { this.samplePrepDao = samplePrepDao; }
	public void setUserDao(UserDAO userDao) { 	this.userDao = userDao; }
	public void setSampleDao(SampleDAO sampleDao) {this.sampleDao = sampleDao;  }
	public void setExperimentDao(ExperimentDAO experimentDao) { this.experimentDao = experimentDao; }	
	public void setInstrumentDao(InstrumentDAO instrumentDao) { this.instrumentDao = instrumentDao; }

	public SamplePrepDAO getSamplePrepDao()  { return samplePrepDao; } 
	public UserDAO getUserDao() { return userDao; }
	public SampleDAO getSampleDao() { return sampleDao; }
	public ExperimentDAO getExperimentDao() { return experimentDao; } 
	public InstrumentDAO getInstrumentDao() { return instrumentDao; } 
	}



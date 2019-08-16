package edu.umich.brcf.metabolomics.layers.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.upload.FileUpload;
//import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.jfree.data.xy.XYDataset;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.umich.brcf.metabolomics.layers.dao.FractionationDAO;
import edu.umich.brcf.metabolomics.layers.dao.InjectionsDAO;
import edu.umich.brcf.metabolomics.layers.dao.InstrumentDAO;
import edu.umich.brcf.metabolomics.layers.domain.FractionPreparation;
import edu.umich.brcf.metabolomics.layers.domain.FractionSample;
import edu.umich.brcf.metabolomics.layers.domain.GCPlate;
import edu.umich.brcf.metabolomics.layers.domain.Injections;
import edu.umich.brcf.metabolomics.layers.domain.LCPlate;
import edu.umich.brcf.metabolomics.layers.dto.FractionDTO;
import edu.umich.brcf.shared.layers.dao.ExperimentDAO;
import edu.umich.brcf.shared.layers.dao.IdGeneratorDAO;
import edu.umich.brcf.shared.layers.dao.SampleDAO;
import edu.umich.brcf.shared.layers.dao.SamplePrepDAO;
import edu.umich.brcf.shared.layers.dao.SampleStatusDAO;
import edu.umich.brcf.shared.layers.dao.SystemConfigDAO;
import edu.umich.brcf.shared.layers.dao.UserDAO;
import edu.umich.brcf.shared.layers.domain.Experiment;
import edu.umich.brcf.shared.layers.domain.PrepPlate;
import edu.umich.brcf.shared.layers.domain.Preparation;
import edu.umich.brcf.shared.layers.domain.PreppedFraction;
import edu.umich.brcf.shared.layers.domain.Sample;
import edu.umich.brcf.shared.layers.dto.PreppedFractionDTO;
import edu.umich.brcf.shared.panels.login.MedWorksSession;


@Transactional
public class FractionationService 
	{
	ExperimentDAO expDao;
	FractionationDAO fractionationDao;
	UserDAO userDao;
	InstrumentDAO instrumentDao;
	SamplePrepDAO samplePrepDao;
	SampleDAO sampleDao;
	IdGeneratorDAO idGeneratorDao;
	SampleStatusDAO statusDao;
	InjectionsDAO injectionsDao;
	SystemConfigDAO systemConfigDao;

	public FractionPreparation loadById(String prepId) {
		return fractionationDao.loadById(prepId);
	}

	public String getFractionPrepIdByName(String fname) {
		return fractionationDao.getFractionPrepIdByName(fname);
	}

	public String uploadFile(FileUpload upload) 
		{
		final String expIdFormat = "(EX)\\d{5}";
		final String fractionFormat = "(S)\\d{8}";
		// String errStr="";
		List<FractionDTO> fractions = new ArrayList<FractionDTO>();
		if (upload != null) {
			if (upload.getContentType().equalsIgnoreCase(
					"application/vnd.ms-excel")) {
				File newFile = new File(getUploadFolder(),
						upload.getClientFileName());
				checkFileExists(newFile);
				try {
					newFile.createNewFile();
					upload.writeTo(newFile);
				} catch (Exception e) {
					return ("Unable to write file!");
				}
				int rowCount = 0, cellCount = 0, sheetNum = 0;
				try {
					String user = ((MedWorksSession) Session.get())
							.getCurrentUserName();
					HSSFWorkbook workbook = new HSSFWorkbook(
							new FileInputStream(newFile));
					HSSFSheet sheet = workbook.getSheetAt(0);
					++sheetNum;
					Row row;
					FractionDTO fdto;
					Iterator<Row> rows = sheet.rowIterator();
					rowCount = 0;
					String msg = null;
					while (rows.hasNext()) {
						++rowCount;
						cellCount = 0;
						row = rows.next();
						if (rowCount > 1) {
							fdto = new FractionDTO();
							if ((row.getCell((short) 0) == null)
									|| (row.getCell((short) 0).toString() == null)
									|| (row.getCell((short) 0).toString()
											.trim().length() == 0))
								break;
							String fid = row.getCell((short) 0).toString()
									.trim();
							if (fid == null || fid.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Fraction ID cannot be blank!");
								break;
							}
							if (!verifyFormat(fractionFormat, fid)) {
								msg = ("Error in Fraction ID format at line: "
										+ rowCount + ", cell 1");
								break;
							}
							fdto.setFractionId(fid);
							++cellCount;
							String pid = row.getCell((short) 1).toString()
									.trim();
							if (pid == null || pid.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Parent ID cannot be blank!");
								break;
							}
							if (!verifyFormat(fractionFormat, pid)) {
								msg = ("Error in Parent ID format at line: " + rowCount);
								break;
							}
							fdto.setParentId(pid);
							++cellCount;
							String expID = row.getCell((short) 2).toString()
									.trim();
							Experiment exp;
							if (verifyFormat(expIdFormat, expID)) {
								try {
									exp = expDao.loadById(expID);
								} catch (Exception ex) {
									return ("File upload failed: Experiment "
											+ expID + " does not exist!");
								}
							} else
								return ("File upload failed: Experiment "
										+ expID + " does not exist!");
							fdto.setExp(exp);
							++cellCount;
							String fname = row.getCell((short) 3).toString()
									.trim();
							if (fname == null || fname.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Fraction Name cannot be blank!");
								break;
							}
							fdto.setFractionName(fname);
							++cellCount;
							String vol = row.getCell((short) 4).toString()
									.trim();
							if (vol == null || vol.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Volume cannot be blank!");
								break;
							}
							fdto.setVolume(new BigDecimal(vol));
							++cellCount;
							String locId = ((row.getCell((short) 5) == null) || (row
									.getCell((short) 5).toString().trim()
									.length() == 0)) ? "LC0000" : row
									.getCell((short) 5).toString().trim();
							fdto.setLocId(locId);
							++cellCount;
							String notes = ((row.getCell((short) 6) == null) || (row
									.getCell((short) 6).toString().trim()
									.length() == 0)) ? null : row
									.getCell((short) 6).toString().trim();
							fdto.setNotes((notes != null && notes.trim()
									.length() != 0) ? (user + ": " + notes)
									: "");
							fdto.setVolUnits("mg");
							// fdto.setMass(new
							// BigDecimal(row.getCell((short)7).toString().trim()));++cellCount;
							fractions.add(fdto);
						}
					}
					if (msg != null)
						return msg;
					else if (fractions.size() == 0)
						return ("0 Fractions were saved! Please review the data being uploaded!!");
					else {
						int count = saveFractions(fractions);
						if (count != 0) {
							msg = ("Database error occured when uploading data in line: "
									+ count + 1 + "! Please review the data being uploaded!!");
						} else
							msg = ("Successfully saved " + fractions.size() + " Fractions!");
					}
					return msg;
				} catch (Exception e) {
					return ("Unable to upload file, error in sheet " + sheetNum
							+ " at line: " + rowCount + ", cell:" + cellCount);
				}
				// catch (IOException e)
				// {
				// return ("Unable to upload file, error in sheet "+sheetNum
				// +" at line: "+rowCount+", cell:"+cellCount);
				// }
			} else
				return ("Unable to upload file, unsupported file type!!");
		} else
			return ("Please use the browse button and select a file to upload data from!!");
	}

	private boolean verifyFormat(String format, String input) {
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	private void checkFileExists(File newFile) {
		if (newFile.exists()) {
			// Try to delete the file
			if (!Files.remove(newFile)) {
				throw new IllegalStateException("Unable to overwrite "
						+ newFile.getAbsolutePath());
			}
		}
	}

	private Folder getUploadFolder() {
		Folder uploadFolder = new Folder(System.getProperty("java.io.tmpdir"),
				"sample-uploads");
		// Ensure folder exists
		uploadFolder.mkdirs();
		return (uploadFolder);
	}

	public int saveFractions(List<FractionDTO> fractions) {
		int count = 0;
		int errLine = 0;
		try {
			for (FractionDTO fdto : fractions) {
				++count;
				FractionSample fraction = null;
				Sample parentSample = null;
				if (fdto != null)
					if (fdto.getParentId() != null)
						parentSample = sampleDao.loadById(fdto.getParentId());
				fraction = FractionSample.instance(fdto.getFractionId(),
						fdto.getFractionName(), fdto.getExp(), fdto.getNotes(),
						parentSample.getGenusOrSpecies(), fdto.getLocId(),
						"Fraction", fdto.getVolume(), fdto.getVolUnits(),
						statusDao.loadById('S'), false, Calendar.getInstance(),
						parentSample.getSampleType(), null, parentSample);

				// }
				// else {
				// Fraction
				// parentFraction=fractionationDao.loadFractionById(fdto.getParentId());
				// fraction=NodeFraction.instance(fdto.getFractionId(),
				// fdto.getFractionName(), parentFraction,
				// fdto.getVolume(), fdto.getVolUnits(), fdto.getVialTare(),
				// fdto.getMass(),
				// "mg", fdto.getExp());
				// }
				fractionationDao.saveFraction(fraction);
				parentSample.updateCurrentVolume(new BigDecimal(parentSample
						.getCur_Volume().doubleValue()
						- fraction.getVolume().doubleValue()));
			}

		} catch (JpaSystemException e) {
			e.printStackTrace();
			errLine = count;
		}
		// catch(NonUniqueObjectException e1){
		// e1.printStackTrace();
		// errLine=count;
		// }
		return errLine;
	}

	public String onPrepSave(String title, List<String> instruments,
			List<PreppedFractionDTO> inputSamples) {
		// int cols=10;
		// int rows=10;
		String retStr = "";
		int index = 0;
		final String txtFormat = "(S)\\d{8}";
		String incorrectFormat = "", scannedFractions = "", repeatedFractions = "";
		for (PreppedFractionDTO dto : inputSamples) {
			if (dto.getId() != null) {
				++index;
				if (verifyFormat(txtFormat, dto.getId())) {
					// if(scannedFractions.indexOf(dto.getId())>(-1))
					// repeatedFractions+=dto.getId()+",";
					// else{
					scannedFractions += dto.getId() + ",";
					// }
				} else
					incorrectFormat += dto.getId() + ",";
			}
		}
		if (incorrectFormat.length() > 0 || repeatedFractions.length() > 0
				|| index == 0 || index > 96) {
			if (repeatedFractions.length() > 0)
				retStr = retStr
						+ ("_Duplicate fractions found: " + repeatedFractions
								.substring(0,
										repeatedFractions.lastIndexOf(",")));
			if (incorrectFormat.length() > 0)
				retStr = retStr
						+ ("_Following fractions have incorrect format: " + incorrectFormat
								.substring(0, incorrectFormat.lastIndexOf(",")));
			if (index == 0)
				retStr = retStr
						+ ("_Cannot create a plate with 0 samples, please scan sample bar codes to create a plate!!");
			if (index > 96)
				retStr = retStr
						+ ("_This plate cannot take more than 96 values!");
		} else {
			String prep = (savePrepData(title, instruments, inputSamples));
		
			retStr = ("Save successful!!!_" + prep);
		}
		return retStr;
	}

	public String savePrepData(String title, List<String> instruments,
			List<PreppedFractionDTO> inputSamples) {
		String creator = ((MedWorksSession) Session.get()).getCurrentUserId();
		Preparation prep = fractionationDao.saveSamplePrep(FractionPreparation
				.instance(title, userDao.loadById(creator)));
		for (Iterator<String> it = instruments.iterator(); it.hasNext();) {
			String instName = (String) it.next();
			// String
			// prepMethod=(instName.startsWith("G"))?"GD000001":"LR000001";
			PrepPlate prepPlate = (instName.startsWith("G")) ? GCPlate
					.instance(prep, "PF01", instrumentDao
							.getInstrumentById(Preparation.INSTRUMENTS
									.get(instName)), null) : LCPlate.instance(
					prep, "PF01", instrumentDao
							.getInstrumentById(Preparation.INSTRUMENTS
									.get(instName)), null);
			samplePrepDao.savePrepPlate(prepPlate);
		}
		Integer index = 0;
		for (PreppedFractionDTO dto : inputSamples) {
			++index;
			if (dto.getId() != null && index <= inputSamples.size()
					&& index <= 96) {
				String id = dto.getId();
				FractionSample fraction = fractionationDao.loadFractionById(id);
				PreppedFraction item = PreppedFraction.instance(prep, fraction,
						samplePrepDao.loadPrepWellByIndex(index, "PF01"), "",
						null, new BigDecimal(dto.getVolume()),
						dto.getVolUnits());
				fractionationDao.savePreppedItem(item);
				System.out.println("index: " + index + " " + dto.getId());
			}
		}
		return prep.getPrepID();
	}

	public void addFractionToPrep(PreppedFractionDTO dto, String preparation) {
		Preparation prep = loadById(preparation);
		List<PreppedFraction> lst = fractionationDao
				.loadPreppedFractions(preparation);
		int index = lst.size() + 1;
		if (dto.getId() != null && index <= 96) {
			String id = dto.getId();
			FractionSample fraction = fractionationDao.loadFractionById(id);
			PreppedFraction item = PreppedFraction.instance(prep, fraction,
					samplePrepDao.loadPrepWellByIndex(index, "PF01"), "", null,
					new BigDecimal(dto.getVolume()), dto.getVolUnits());
			fractionationDao.savePreppedItem(item);
			System.out.println("index: " + index + " " + dto.getId());
		}
	}

	public void updateFractionNotes(String sid, String notes) {
		if (notes != null && notes.trim().length() != 0) {
			String user = ((MedWorksSession) Session.get())
					.getCurrentUserName();
			Sample uSample = sampleDao.loadById(sid);
			uSample.setUserDescription(uSample.getUserDescription() + System.getProperty("line.separator")
					+ user + ": " + notes);
		}
	}

	public String uploadPrepFile(FileUpload upload, String title) {
		final String sampleFormat = "(S)\\d{8}";
		List<PreppedFractionDTO> samples = new ArrayList<PreppedFractionDTO>();
		List<String> instruments = new ArrayList<String>();
		if (upload != null) {
			if (upload.getContentType().equalsIgnoreCase(
					"application/vnd.ms-excel")) {
				File newFile = new File(getUploadFolder(),
						upload.getClientFileName());
				checkFileExists(newFile);
				try {
					newFile.createNewFile();
					upload.writeTo(newFile);
				} catch (Exception e) {
					return ("Unable to write file!");
				}
				int rowCount = 0, cellCount = 0, sheetNum = 0;
				try {
					// String user=((METWorksSession)
					// Session.get()).getCurrentUserName();
					HSSFWorkbook workbook = new HSSFWorkbook(
							new FileInputStream(newFile));
					HSSFSheet sheet = workbook.getSheetAt(0);
					++sheetNum;
					Row row;
					PreppedFractionDTO dto;
					Iterator<Row> rowsIt = sheet.rowIterator();
					rowCount = 0;
					String msg = null;
					while (rowsIt.hasNext()) {
						++rowCount;
						cellCount = 0;
						row = rowsIt.next();
						if (rowCount > 1 && rowCount < 98) {
							dto = new PreppedFractionDTO();
							if ((row.getCell((short) 0) == null)
									|| (row.getCell((short) 0).toString() == null)
									|| (row.getCell((short) 0).toString()
											.trim().length() == 0))
								break;
							String sid = row.getCell((short) 0).toString()
									.trim();
							if (sid == null || sid.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Fraction ID cannot be blank!");
								break;
							}
							if (!verifyFormat(sampleFormat, sid)) {
								msg = ("Error in Fraction ID format at line: "
										+ rowCount + ", cell 1");
								break;
							}
							dto.setId(sid);
							++cellCount;
							//
							String vol = row.getCell((short) 1).toString()
									.trim();
							if (vol == null || vol.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Amount cannot be blank!");
								break;
							}
							dto.setVolume(vol);
							++cellCount;
							String volUnits = row.getCell((short) 2).toString()
									.trim();
							if (volUnits == null || volUnits.length() == 0) {
								msg = ("Error at line: " + rowCount + "! Units for Amount cannot be blank!");
								break;
							}
							dto.setVolUnits(volUnits);
							samples.add(dto);
						}
					}
					if (msg != null)
						return msg;
					else {
						sheet = workbook.getSheetAt(1);
						++sheetNum;
						rowsIt = sheet.rowIterator();
						rowCount = 0;
						while (rowsIt.hasNext()) {
							++rowCount;
							cellCount = 0;
							row = rowsIt.next();
							if (rowCount > 1) {
								if ((row.getCell((short) 0) == null)
										|| (row.getCell((short) 0).toString() == null)
										|| (row.getCell((short) 0).toString()
												.trim().length() == 0))
									break;
								String inst = row.getCell((short) 0).toString()
										.trim();
								if (inst == null || inst.length() == 0) {
									msg = ("Error at line: " + rowCount + "! Instrument cannot be blank!");
									break;
								}
								if (!inst.equals("GC") && !inst.equals("LC1")
										&& !inst.equals("LC2")) {
									msg = ("Error at line: " + rowCount + "! Instrument value incorrect");
									break;
								}
								instruments.add(inst);
							}
						}
					}
					if (msg != null)
						return msg;
					else if (samples.size() == 0)
						return ("0 Fractions were saved! Please review the data being uploaded!!");
					else if (instruments.size() == 0)
						return ("Please specify atleast one instrument to run the fractions on!!");
					else {
						msg = onPrepSave(title, instruments, samples);
						return msg;
					}
				} catch (Exception e) {
					return ("Unable to upload file, error in sheet " + sheetNum
							+ " at line: " + rowCount + ", cell:" + cellCount);
				}
			} else
				return ("Unable to upload file, unsupported file type!!");
		} else
			return ("Please use the browse button and select a file to upload data from!!");
		// return null;
	}

	public List<PreppedFraction> loadPreppedFractions(String preparation) {
		return fractionationDao.loadPreppedFractions(preparation);
	}

	public List<PrepPlate> loadPlatesByPreparation(String preparation) {
		return fractionationDao.loadPlatesByPreparation(preparation);
	}

	public List<Preparation> allFractionPreparations() {
		return fractionationDao.allFractionPreparations();
	}

	public String getNextFractionID() {
		return ((String) idGeneratorDao.getNextValue("Sample"));
	}

	public Sample loadCompleteFractionsTree(String id) {
		String parentId = (id != null) ? getRootParentID(id) : null;
		return fractionationDao.loadCompleteFractionsTree(parentId);
	}

	public String getOrgChartData() {
		return fractionationDao.getOrgChartData();
	}

	public Injections loadInjectionById(Long injectionId) {
		return injectionsDao.loadInjectionById(injectionId);
	}

	private String getRootParentID(String id) 
		{
		return "";
		
		/*Sample s = sampleDao.loadByIdForTree(id);
		if (s.getParent() != null)
			return getRootParentID(s.getParent().getSampleID());
		else
			return s.getSampleID(); */
	}

	public List<Injections> getInjectionforFraction(Sample fraction) {
		return fractionationDao.getInjectionforFraction(fraction);
	}

	public List<String> getMatchingFractions(String input) 
		{
		return fractionationDao.getMatchingFractions(input);
		}

	public XYDataset get_Mass_RT_Data(Injections injection,
			ArrayList<String> toolTips, ArrayList<String> cAreas) {
		return fractionationDao.get_Mass_RT_Data(injection, toolTips, cAreas);
	}

	public List<String> get_Mass_RT_DataArray(Injections injection) {
		return fractionationDao.get_Mass_RT_DataArray(injection);
	}

	private static int BUFFER_SIZE = 8388608;

	public ArrayList<String> unZip(ZipInputStream zipInputStream, File extractTo)
			throws FileNotFoundException, IOException {
		Assert.isTrue(extractTo.isDirectory());
		BufferedOutputStream destination = null;
		ZipEntry entry;
		ArrayList<String> nmrs = new ArrayList<String>();
		// String nmr_1r=null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			byte[] data = new byte[BUFFER_SIZE];
			int count;
			File extractFile = new File(extractTo, entry.getName());
			if (entry.isDirectory()) {
				if (!extractFile.exists())
					extractFile.mkdirs();
			} else {
				if (!extractFile.getParentFile().exists())
					extractFile.getParentFile().mkdirs();
				if (!extractFile.exists()) {
					extractFile.createNewFile();
					destination = new BufferedOutputStream(
							new FileOutputStream(extractFile, true),
							BUFFER_SIZE);
					while ((count = zipInputStream.read(data)) != -1)
						destination.write(data, 0, count);
					destination.flush();
					destination.close();
					extractFile.setReadable(true);
					extractFile.setWritable(true);
					extractFile.setExecutable(true);
				}
			}
			if (extractFile.getName().equals("1r")
					|| extractFile.getName().equals("2rr")) {
				String pathStr = extractFile.getAbsolutePath();
				String foreslash = "/";
				String regex = "\\\\";
				pathStr = pathStr.replaceAll(regex, foreslash);
				nmrs.add("http://"
						+ (String) systemConfigDao.getSystemConfigurationMap()
								.get("app_server") + "/"
						+ pathStr.substring(pathStr.indexOf("nmr")));// ,
																		// pathStr.lastIndexOf("/")+1
				// if(extractFile.getName().equals("1r")&&nmr_1r==null)
				// nmr_1r=("http://"+(String)
				// systemConfigDao.getSystemConfigurationMap().get("app_server")+"/"+pathStr.substring(pathStr.indexOf("nmr")));//,
				// pathStr.lastIndexOf("/")+1
			}
		}
		zipInputStream.close();
		return nmrs;
	}

	public Folder getNMRUploadFolder() {
		Folder uploadFolder = new Folder((String) systemConfigDao
				.getSystemConfigurationMap().get("nmr_upload_path"));
		// Ensure folder exists
		if (!uploadFolder.exists())
			uploadFolder.mkdirs();
		return (uploadFolder);
	}

	public void setExpDao(ExperimentDAO expDao) {
		this.expDao = expDao;
	}

	public void setFractionationDao(FractionationDAO fractionationDao) {
		this.fractionationDao = fractionationDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	public void setInstrumentDao(InstrumentDAO instrumentDao) {
		this.instrumentDao = instrumentDao;
	}

	public void setSamplePrepDao(SamplePrepDAO samplePrepDao) {
		this.samplePrepDao = samplePrepDao;
	}

	public void setSampleDao(SampleDAO sampleDao) {
		this.sampleDao = sampleDao;
	}

	public void setIdGeneratorDao(IdGeneratorDAO idGeneratorDao) {
		this.idGeneratorDao = idGeneratorDao;
	}

	public void setStatusDao(SampleStatusDAO statusDao) {
		this.statusDao = statusDao;
	}

	public void setInjectionsDao(InjectionsDAO injectionsDao) {
		this.injectionsDao = injectionsDao;
	}

	public void setSystemConfigDao(SystemConfigDAO systemConfigDao) {
		this.systemConfigDao = systemConfigDao;
	}
}

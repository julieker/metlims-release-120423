/////////////////////////////////////////////
// ClientInfoReader.java
// Written by Jan Wigginton, February 2016
/////////////////////////////////////////////

package edu.umich.brcf.shared.util.sheetreaders;

import org.apache.poi.ss.usermodel.Sheet;

import edu.umich.brcf.shared.util.SampleSheetIOException;
import edu.umich.brcf.shared.util.datacollectors.ClientDataInfo;
import edu.umich.brcf.shared.util.io.SpreadSheetReader;



public class ClientInfoReader extends SpreadSheetReader 
	{
	public ClientInfoReader() {}
	int firstCol = 0, firstRow = 0;
	
	int orgRow = 2, orgAddressRow = 3,  labRow = 4, deptRow = 3;
	int contactNameRow = 5, contactEmailRow = 6, contactPhoneRow = 7; 
	int piNameRow = 8, piEmailRow = 9, piPhoneRow = 10; 
	int dateRow = 11;
	int projNameRow = 12, projDescriptionRow = 13, expNameRow = 14, expIdRow = 15,  expDescriptionRow = 16;
	int shortCodeRow = 18, nihGrantNumberRow = 19, serviceRequestIdRow = -1;
	
	boolean report = false;
	
	
	public ClientDataInfo read(Sheet sheet) throws SampleSheetIOException
		{
		ClientDataInfo  data = new ClientDataInfo();
		
		data.setOrganizationName(readOrganization(sheet, report));
		data.setOrganizationAddress(readOrgAddress(sheet, report));
		data.setLabName(readLab(sheet, report));
		data.setDeptName(readDepartment(sheet, report));
		data.setContactPerson(readContactPerson(sheet, report));
		data.setContactEmail(readContactEmail(sheet, report));
		data.setContactPhone(readContactPhone(sheet, report));
		data.setPiName(readPiName(sheet, report));
		data.setPiEMail(readPiEmail(sheet, report));
		data.setPiPhone(readPiPhone(sheet, report));
		data.setExperimentDate(readExperimentDate(sheet, report));
		data.setProjectName(readProjectName(sheet, report));
		data.setProjectDescription(readProjectDescription(sheet, report));
		data.setExperimentName(readExperimentName(sheet, report));
		data.setExperimentId(readExperimentId(sheet, report));
		data.setShortCode(readShortcode(sheet, report));
		data.setNihGrantNumber(readNihGrantNumber(sheet, report));
		data.setUmServiceRequestId(readUmServiceRequestId(sheet, report));
		data.setExperimentDescription(readExperimentDescription(sheet, report));
	
		return data;	
		}


	private String readOrganization(Sheet sheet, boolean report)
		{
		return getDataAt(orgRow + firstRow ,1 + firstCol, sheet, report);
		}
	

	private String readOrgAddress(Sheet sheet, boolean report)
		{
		return getDataAt(orgAddressRow + firstRow ,1 + firstCol, sheet, report);
		}
	
	private String readLab(Sheet sheet, boolean report)
		{
		return getDataAt(labRow + firstRow,1 + firstCol, sheet, report);
		}

	
	private String readDepartment(Sheet sheet, boolean report)
		{
		return getDataAt(deptRow + firstRow,1 + firstCol, sheet, report);
		}

	
	private String readContactPerson(Sheet sheet, boolean report)
		{
		return getDataAt(contactNameRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readContactEmail(Sheet sheet, boolean report)
		{
		return getDataAt(contactEmailRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readContactPhone(Sheet sheet, boolean report)
		{
		return getDataAt(contactPhoneRow + firstRow, 1 + firstCol, sheet, report);
		}

	
	private String readPiName(Sheet sheet, boolean report)
		{
		return getDataAt(piNameRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readPiEmail(Sheet sheet, boolean report)
		{
		return getDataAt(piEmailRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readPiPhone(Sheet sheet, boolean report)	
		{
		return getDataAt(piPhoneRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readExperimentDate(Sheet sheet, boolean report)
		{
		return getDataAt(dateRow + firstRow, 1 + firstCol, sheet, report);
		}

	
	private String readExperimentDescription(Sheet sheet, boolean report)
		{
		return getDataAt(expDescriptionRow + firstRow, 1 + firstCol, sheet, report);
		}
	
	
	private String readExperimentName(Sheet sheet, boolean report)
		{
		return getDataAt(expNameRow + firstRow, 1 + firstCol, sheet, report);
		}

	
	private String readExperimentId(Sheet sheet, boolean report)
		{
		return getDataAt(expIdRow + firstRow,1 + firstCol, sheet, report);
		}

	
	private String readProjectName(Sheet sheet, boolean report)
		{
		return getDataAt(projNameRow + firstRow,1 + firstCol, sheet, report);
		}
	
	
	private String readProjectDescription(Sheet sheet, boolean report)
		{
		return getDataAt(projDescriptionRow + firstRow,1 + firstCol, sheet, report);
		}
	
	
	private String readShortcode(Sheet sheet, boolean report)
		{
		return getDataAt(shortCodeRow + firstRow,1 + firstCol, sheet, report);
		}

	
	private String readNihGrantNumber(Sheet sheet, boolean report)
		{
		return getDataAt(nihGrantNumberRow + firstRow,1 + firstCol, sheet, report);
		}

	
	private String readUmServiceRequestId(Sheet sheet, boolean report)
		{
		return getDataAt(serviceRequestIdRow + firstRow, 1 + firstCol, sheet, report);
		}


	public int getFirstCol()
		{
		return firstCol;
		}


	public int getFirstRow()
		{
		return firstRow;
		}


	public int getOrgRow()
		{
		return orgRow;
		}


	public int getLabRow()
		{
		return labRow;
		}


	public int getDeptRow()
		{
		return deptRow;
		}


	public int getContactNameRow()
		{
		return contactNameRow;
		}


	public int getContactEmailRow()
		{
		return contactEmailRow;
		}


	public int getContactPhoneRow()
		{
		return contactPhoneRow;
		}


	public int getPiNameRow()
		{
		return piNameRow;
		}


	public int getPiEmailRow()
		{
		return piEmailRow;
		}


	public int getPiPhoneRow()
		{
		return piPhoneRow;
		}


	public int getDateRow()
		{
		return dateRow;
		}


	public int getProjNameRow()
		{
		return projNameRow;
		}


	public int getProjDescriptionRow()
		{
		return projDescriptionRow;
		}


	public int getExpNameRow()
		{
		return expNameRow;
		}


	public int getExpIdRow()
		{
		return expIdRow;
		}


	public int getExpDescriptionRow()
		{
		return expDescriptionRow;
		}


	public int getShortCodeRow()
		{
		return shortCodeRow;
		}


	public int getNihGrantNumberRow()
		{
		return nihGrantNumberRow;
		}


	public int getServiceRequestIdRow()
		{
		return serviceRequestIdRow;
		}


	public void setFirstCol(int firstCol)
		{
		this.firstCol = firstCol;
		}


	public void setFirstRow(int firstRow)
		{
		this.firstRow = firstRow;
		}


	public void setOrgRow(int orgRow)
		{
		this.orgRow = orgRow;
		}


	public void setOrgAddressRow(int orgAddressRow)
		{
		this.orgAddressRow = orgAddressRow;
		}
	
	
	public void setLabRow(int labRow)
		{
		this.labRow = labRow;
		}


	public void setDeptRow(int deptRow)
		{
		this.deptRow = deptRow;
		}


	public void setContactNameRow(int contactNameRow)
		{
		this.contactNameRow = contactNameRow;
		}


	public void setContactEmailRow(int contactEmailRow)
		{
		this.contactEmailRow = contactEmailRow;
		}


	public void setContactPhoneRow(int contactPhoneRow)
		{
		this.contactPhoneRow = contactPhoneRow;
		}


	public void setPiNameRow(int piNameRow)
		{
		this.piNameRow = piNameRow;
		}


	public void setPiEmailRow(int piEmailRow)
		{
		this.piEmailRow = piEmailRow;
		}


	public void setPiPhoneRow(int piPhoneRow)
		{
		this.piPhoneRow = piPhoneRow;
		}


	public void setDateRow(int dateRow)
		{
		this.dateRow = dateRow;
		}


	public void setProjNameRow(int projNameRow)
		{
		this.projNameRow = projNameRow;
		}


	public void setProjDescriptionRow(int projDescriptionRow)
		{
		this.projDescriptionRow = projDescriptionRow;
		}


	public void setExpNameRow(int expNameRow)
		{
		this.expNameRow = expNameRow;
		}


	public void setExpIdRow(int expIdRow)
		{
		this.expIdRow = expIdRow;
		}


	public void setExpDescriptionRow(int expDescriptionRow)
		{
		this.expDescriptionRow = expDescriptionRow;
		}


	public void setShortCodeRow(int shortCodeRow)
		{
		this.shortCodeRow = shortCodeRow;
		}


	public void setNihGrantNumberRow(int nihGrantNumberRow)
		{
		this.nihGrantNumberRow = nihGrantNumberRow;
		}


	public void setServiceRequestIdRow(int serviceRequestIdRow)
		{
		this.serviceRequestIdRow = serviceRequestIdRow;
		}


	public boolean isReport()
		{
		return report;
		}


	public void setReport(boolean report)
		{
		this.report = report;
		}
	}


/*
public class ClientInfoReader extends SpreadSheetReader
	{
	ClientInfoReader()
		{
		}

	public ClientDataInfo read(Sheet sheet) throws SampleSheetIOException
		{
		ClientDataInfo data = new ClientDataInfo();

		data.setOrganizationName(readOrganization(sheet, false));
		data.setLabName(readLab(sheet, false));
		data.setDeptName(readDepartment(sheet, false));
		data.setContactPerson(readContactPerson(sheet, false));
		data.setContactEmail(readContactEmail(sheet, false));
		data.setContactPhone(readContactPhone(sheet, false));
		data.setExperimentDate(readExperimentDate(sheet, false));
		data.setExperimentName(readExperimentName(sheet, false));
		data.setExperimentId(readExperimentId(sheet, false));
		data.setProjectName(readProjectName(sheet, false));
		data.setProjectDescription(readProjectDescription(sheet, false));
		data.setShortCode(readShortcode(sheet, false));
		data.setNihGrantNumber(readNihGrantNumber(sheet, false));
		data.setUmServiceRequestId(readUmServiceRequestId(sheet, false));

		return data;
		}

	private String readOrganization(Sheet sheet, boolean report)
		{
		return getDataAt(3, 2, sheet, report);
		}

	private String readLab(Sheet sheet, boolean report)
		{
		return getDataAt(4, 2, sheet, report);
		}

	private String readDepartment(Sheet sheet, boolean report)
		{
		return getDataAt(5, 2, sheet, report);
		}

	private String readContactPerson(Sheet sheet, boolean report)
		{
		return getDataAt(6, 2, sheet, report);
		}

	private String readContactEmail(Sheet sheet, boolean report)
		{
		return getDataAt(7, 2, sheet, report);
		}

	private String readContactPhone(Sheet sheet, boolean report)
		{
		return getDataAt(8, 2, sheet, report);
		}

	private String readPiName(Sheet sheet, boolean report)
		{
		return getDataAt(9, 2, sheet, report);
		}

	private String readExperimentDate(Sheet sheet, boolean report)
		{
		return getDataAt(10, 2, sheet, report);
		}

	private String readExperimentName(Sheet sheet, boolean report)
		{
		return getDataAt(11, 2, sheet, report);
		}

	private String readExperimentId(Sheet sheet, boolean report)
		{
		return getDataAt(12, 2, sheet, report);
		}

	private String readProjectName(Sheet sheet, boolean report)
		{
		return getDataAt(13, 2, sheet, report);
		}

	private String readProjectDescription(Sheet sheet, boolean report)
		{
		return getDataAt(14, 2, sheet, report);
		}

	private String readShortcode(Sheet sheet, boolean report)
		{
		return getDataAt(15, 2, sheet, report);
		}

	private String readNihGrantNumber(Sheet sheet, boolean report)
		{
		return getDataAt(16, 2, sheet, report);
		}

	private String readUmServiceRequestId(Sheet sheet, boolean report)
		{
		return getDataAt(17, 2, sheet, report);
		}
	}  */

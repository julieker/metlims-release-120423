////////////////////////////////////////////////////
// FieldLengths.java
// Written by Jan Wigginton, Apr 17, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util;

public class FieldLengths
	{
	public static Integer SMALL_DOCUMENT_LIMIT = 1000000; // issue 441
	public static Integer PROJECT_NAME_FIELD_LENGTH = 120;
	public static Integer PROJECT_DESCRIPTION_FIELD_LENGTH = 1000;
	public static Integer PROJECT_NOTES_FIELD_LENGTH = 500;
	
	public static Integer EXPERIMENT_NAME_FIELD_LENGTH = 150;
	public static Integer EXPERIMENT_DESCRIPTION_FIELD_LENGTH = 1000;
	public static Integer EXPERIMENT_NOTES_FIELD_LENGTH = 1000;
	
	public static Integer ORACLE_MAX_LIST_LENGTH = 1000;
	
	// JAK fix issue 134
	public static Integer METLIMS_SAMPLE_ID_LENGTH = 9;
	public static Integer METLIMS_RESEARCHER_SAMPLE_ID_LENGTH = 120;
	public static Integer METLIMS_RESEARCHER_SUBJECT_ID_LENGTH = 100;
	public static Integer METLIMS_SAMPLE_TYPE_LENGTH = 100;
	public static Integer METLIMS_GENUS_OR_SPECIES_LENGTH = 50;
	public static Integer METLIMS_VOLUME_LENGTH = 100;
	public static Integer METLIMS_VOLUME_UNITS_LENGTH = 26;
	public static Integer METLIMS_SAMPLE_TYPE_ID_LENGTH = 7;
	public static Integer METLIMS_GENUS_OR_SPECIES_ID_LENGTH = 8;
	public static Integer METLIMS_LOCATION_ID_LENGTH = 6;
	
	public static Integer MRC2_SAMPLE_ID_LENGTH = 9;
	public static Integer MRC2_RESEARCHER_SAMPLE_ID_LENGTH = 120;
	public static Integer MRC2_RESEARCHER_SUBJECT_ID_LENGTH = 100;
	public static Integer MRC2_SUBJECT_ID_LENGTH = 9;
	public static Integer MRC2_VOLUME_LENGTH = 100;
	public static Integer MRC2_VOLUME_UNITS_LENGTH = 26;
	public static Integer MRC2_GENUS_OR_SPECIES_LENGTH = 50;
	public static Integer MRC2_GENUS_OR_SPECIES_ID_LENGTH = 8;
	public static Integer MRC2_SAMPLE_TYPE_LENGTH = 100;
	public static Integer MRC2_SAMPLE_TYPE_ID_LENGTH = 7;
	public static Integer MRC2_LOCATION_ID_LENGTH = 6;	
	public static Integer MRC2_FACTOR_NAME_LENGTH = 120; 
	public static Integer MRC2_FACTOR_VALUE_LENGTH = 40;
	public static Integer MRC2_ASSAY_NAME_LENGTH = 200;
	public static Integer MRC2_MIXTURE_NAME = 50; // issue 118
	public static Integer COMPOUND_ID_LENGTH = 6; // issue 144
	
	public static Integer grabFieldLength(String property) 
		{
		switch (property) 
			{
			case "sampleId" : return  FieldLengths.MRC2_SAMPLE_ID_LENGTH;
			case "researcherSampleId" : return  MRC2_RESEARCHER_SAMPLE_ID_LENGTH;
			case "researcherSubjectId" : return  MRC2_RESEARCHER_SUBJECT_ID_LENGTH;
			case "subjectId" : return  MRC2_SUBJECT_ID_LENGTH;
			case "currVolume" : 
			case "volume" : return FieldLengths.MRC2_VOLUME_LENGTH;
			case "volUnits" : return FieldLengths.MRC2_VOLUME_UNITS_LENGTH;
			case "locID " :
			case "locId" : return FieldLengths.MRC2_LOCATION_ID_LENGTH;
			case "userDefGOS" : return FieldLengths.MRC2_GENUS_OR_SPECIES_LENGTH;
			case "genusOrSpeciesID" : return FieldLengths.MRC2_GENUS_OR_SPECIES_ID_LENGTH;
			case "userDefSampleType" : return FieldLengths.MRC2_SAMPLE_TYPE_LENGTH; 
			case "sampleTypeId" : return FieldLengths.MRC2_SAMPLE_TYPE_ID_LENGTH;
			}
		return -1;
		}
	
	}	


/*
 * EXP_ID
EXP_NAME
PROJECT_ID
EXP_DESCRIPTION
CREATIONDATE
PRIORITY_TYPE
NOTES
CREATOR
SERVICE_REQUEST_ID
 * 
 * 
CHAR	(null)	(null)	7
VARCHAR2	(null)	(null)	150
CHAR	(null)	(null)	6
VARCHAR2	(null)	(null)	1000
DATE	(null)	(null)	7
VARCHAR2	(null)	(null)	10
VARCHAR2	(null)	(null)	1000
CHAR	(null)	(null)	6
VARCHAR2	(null)	(null)	20
*/
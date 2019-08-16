CREATE TABLE ASSAY_INFO_REQUIREMENTS 
(
ASSAY_ID  
NEED_SAMPLE_ID BOOLEAN,
NEED_SUBJECT_ID BOOLEAN,
NEED_RESEARCHER_SAMPLE_ID BOOLEAN,
NEED_RESEARCHER_SUBJECT_ID BOOLEAN,
NEED_EXP_ID BOOLEAN,
NEED_USER_DEFINED_SAMPLE_TYPE BOOLEAN,
NEED_SAMPLE_TYPE_ID BOOLEAN,
NEED_STATUS	BOOLEAN,
NEED_LOCID	BOOLEAN,
NEED_DATE_CREATED BOOLEAN,
NEED_IS_FROZEN BOOLEAN,
NEED_EVER_FROZEN BOOLEAN 
NEED_N_FREEZE_THAW_CYCLES BOOLEAN,
NEED_BUFFER_TYPE BOOLEAN, 
NEED_TUBE_TYPE BOOLEAN,
NEED_PRESERVATION_METHOD BOOLEAN,
NEED_COLLECTION_METHOD BOOLEAN,
NEED_RNA_INTEGRITY BOOLEAN,
NEED_SOURCE_TISSUE BOOLEAN,
NEED_CELL_TYPE BOOLEAN,
NEED_CELL_COUNT BOOLEAN
NEED_VOLUME BOOLEAN, 
NEED_VOL_UNITS BOOLEAN, 
NEED_CONCENTRATION BOOLEAN, 
NEED_CONC_UNITS BOOLEAN,
NEED_WEIGHT BOOLEAN,
NEED_WEIGHT_UNITS BOOLEAN,
NEED_NANODROP_CONCENTRATION BOOLEAN,
NEED_NANODROP_CONC_UNITS BOOLEAN, 
NEED_TWO60280_CONCENTRATION BOOLEAN,
NEED_TWO60280_CONC_UNITS BOOLEAN,
NEED_TWO60230_CONCENTRATION BOOLEAN,
NEED_TWO60230_CONC_UNITS BOOLEAN,
NEED_FLUOROMETRIC_CONCENTRATION BOOLEAN,
NEED_FLUOROMETRIC_CONC_UNITS BOOLEAN(,	
NEED_AB_CHOICE BOOLEAN,
NEED_READ_LENGTH BOOLEAN,
NEED_SEQUENCER BOOLEAN,
NEED_INDEX_NUMBER BOOLEAN,
NEED_LIBRARY_PREP_BY  BOOLEAN,
NEED_LIBRARY_PREP_DATE BOOLEAN, 
NEED_FLOW_CELL_PREP_BY BOOLEAN, 
NEED_SERIAL_RUN_ID BOOLEAN,
NEED_LANE_NUM BOOLEAN), 
NEED_COMMENTS BOOLEAN,
)

CREATE TABLE SHEET_HEADERS
(
HEADER_ID CHAR(4),
HEADER_TITLE VARCHAR(40), 
USE_BLANK BOOLEAN,
USE_FILLED BOOLEAN
);

H001   Sample ID    T   T
H002   Subject ID   F    ?
H003   Researcher Sample Id   T   T 
H004   Researcher Subject Id  T   T
H005   


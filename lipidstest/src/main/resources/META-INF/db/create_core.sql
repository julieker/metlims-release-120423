CREATE TABLE CORE
(
CORE_ID	CHAR(7),
CORE_AREA_ID CHAR(6),
DESCRIPTION	VARCHAR(100),
CONSTRAINT  core_table_pk PRIMARY KEY (CORE_ID)
);

		create table ID_CONTROL
			(
			ID_CLASS VARCHAR(20), 
			WIDTH INTEGER(3), 
			FIRST_LETTER CHAR(1), 
			SECOND_LETTER CHAR(1), 
			SEQUENCE INTEGER(10),
			CONSTRAINT  id_control_pk PRIMARY KEY (ID_CLASS)
			);
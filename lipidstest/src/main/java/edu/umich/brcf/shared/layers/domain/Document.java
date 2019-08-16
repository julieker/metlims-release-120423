package edu.umich.brcf.shared.layers.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.wicket.util.io.IClusterable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "DOCUMENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
public abstract class Document implements IClusterable
	{
	@Id()
	@GeneratedValue(generator = "IdGeneratorDAO")
	@GenericGenerator(name = "IdGeneratorDAO", strategy = "edu.umich.brcf.shared.layers.dao.IdGeneratorDAO", parameters = {
			@Parameter(name = "idClass", value = "Document"),
			@Parameter(name = "width", value = "10") })
	@Column(name = "DOCUMENT_ID", unique = true, nullable = false, length = 10, columnDefinition = "CHAR(10)")
	protected String documentId;

	// @Basic()
	// @Column(name = "ASSOCIATED", nullable = false, columnDefinition =
	// "VARCHAR2(20)")
	// protected String associated;

	@Basic()
	@Column(name = "FILE_NAME", nullable = false, columnDefinition = "VARCHAR2(150)")
	protected String fileName;

	@Basic()
	@Column(name = "FILE_TYPE", nullable = false, columnDefinition = "VARCHAR2(80)")
	protected String fileType;

	@Lob()
	@Column(name = "contents", nullable = false, columnDefinition = "BLOB(4000)")
	protected byte[] contents;

	
	@Basic()
	@Column(name = "DELETED", length = 1)
	private Boolean deletedFlag;

	protected Document(String documentId, byte[] contents, String fileName,
			String fileType)
		{
		// this.associated = associated;
		this.contents = contents;
		this.documentId = documentId;
		this.fileName = fileName;
		this.fileType = fileType;
		}

	public Document()
		{
		}

	public String getDocumentId()
		{
		return documentId;
		}

	// public String getAssociated() {
	// return associated;
	// }

	public String getFileName()
		{
		return fileName;
		}

	public String getFileType()
		{
		return fileType;
		}

	public byte[] getContents()
		{
		return contents;
		}

	public Boolean isDeleted()
		{
		if (deletedFlag == null) 
			return false;
		
		if (deletedFlag)
			return true;
		
		return false;
		}

	public void setDeleted()
		{
		this.deletedFlag = true;
		}
	}

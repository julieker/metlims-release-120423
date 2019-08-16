package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity()
@DiscriminatorValue(value = "PROJECT")
public class ProjectDocument extends Document
	{	
	public static ProjectDocument instance(String documentId, Project proj, byte[] contents, String fileName, String fileType) 
		{
		return new ProjectDocument(documentId, proj, contents, fileName, fileType);
		}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSOCIATED", referencedColumnName = "PROJECT_ID", nullable = false)
	protected Project associated;
	
	private ProjectDocument(String documentId, Project proj, byte[] contents, String fileName, String fileType) 
		{
		super(documentId, contents, fileName, fileType);
		this.associated = proj;
		}

	public ProjectDocument() {    } 

	public Project getAssociated() 
		{
		return associated;
		}
	}

package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity()
@DiscriminatorValue(value = "EXPERIMENT")
public class ExperimentDocument extends Document
	{

	public static ExperimentDocument instance(String documentId, Experiment exp, byte[] contents, 
		String fileName, String fileType)
		{
		return new ExperimentDocument(documentId, exp, contents, fileName, fileType);
		}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSOCIATED", referencedColumnName = "EXP_ID", nullable = false)
	protected Experiment associated;

	private ExperimentDocument(String documentId, Experiment exp, byte[] contents, String fileName, String fileType)
		{
		super(documentId, contents, fileName, fileType);
		this.associated = exp;
		}

	public ExperimentDocument()  {  }

	
	public Experiment getAssociated()
		{
		return associated;
		}
	}

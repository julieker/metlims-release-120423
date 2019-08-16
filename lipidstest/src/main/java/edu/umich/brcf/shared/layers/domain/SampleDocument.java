package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity()
@DiscriminatorValue(value = "SAMPLE")
public class SampleDocument extends Document
	{

	public static SampleDocument instance(String documentId, Sample sample,
			byte[] contents, String fileName, String fileType)
		{
		return new SampleDocument(documentId, sample, contents, fileName,
				fileType);

		}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSOCIATED", referencedColumnName = "SAMPLE_ID", nullable = false)
	protected Sample associated;

	private SampleDocument(String documentId, Sample sample, byte[] contents,
			String fileName, String fileType)
		{
		super(documentId, contents, fileName, fileType);
		this.associated = sample;

		}

	public SampleDocument()
		{
		}

	public Sample getAssociated()
		{
		return associated;
		}
	}

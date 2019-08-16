
package edu.umich.brcf.shared.layers.domain;



import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import edu.umich.brcf.metabolomics.layers.domain.Compound;


@Entity()
@DiscriminatorValue(value = "COMPOUND")
public class CompoundDocument extends Document
	{
	public static CompoundDocument instance(String documentId, Compound compound, byte[] contents,
		String fileName, String fileType) 
		{
		return new CompoundDocument(documentId, compound, contents, fileName, fileType);
		}
	
	// has missing sample information
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSOCIATED", referencedColumnName = "CID", nullable = false)
	protected Compound associated;
	
	private CompoundDocument(String documentId, Compound compound, byte[] contents, String fileName, String fileType) 
		{
		super(documentId, contents, fileName, fileType);
		this.associated = compound;
		}

	public CompoundDocument() {    }

	public Compound getAssociated() {
		return associated;
	}
}

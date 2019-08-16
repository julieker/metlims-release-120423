package edu.umich.brcf.shared.layers.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity()
@DiscriminatorValue(value = "CLIENT")
public class ClientDocument extends Document
	{
	public static ClientDocument instance(String documentId, Client client,
			byte[] contents, String fileName, String fileType)
		{
		return new ClientDocument(documentId, client, contents, fileName, fileType);
		}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSOCIATED", referencedColumnName = "CLIENT_ID", nullable = false)
	protected Client associated;

	private ClientDocument(String documentId, Client client, byte[] contents, String fileName, String fileType)
		{
		super(documentId, contents, fileName, fileType);
		this.associated = client;
		}

	public ClientDocument() {  }
	
	public Client getAssociated()
		{
		return associated;
		}
	}

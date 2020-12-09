/******************
 * Created by Julie Keros
 * Date: Nov 30 2020 
 * To store mixtures and aliquots
 * 
 * 
 */
// issue 100


package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

	@Embeddable
	public  class MixtureAliquotPK implements Serializable
		{
		public static MixtureAliquotPK instance(Mixture mixture, Aliquot aliquot)
			{
			return new MixtureAliquotPK(mixture, aliquot);
			}

		@Column(name = "MIXTURE_ID")
		private String mixtureid;

		@Column(name = "ALIQUOT_ID")
		private String aliquotId;

		private MixtureAliquotPK(Mixture mixture, Aliquot aliquot)
			{
			this.mixtureid= mixture.getMixtureId();
			this.aliquotId = aliquot.getAliquotId();
			}

		public MixtureAliquotPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof MixtureAliquotPK)
				{
				MixtureAliquotPK that = (MixtureAliquotPK) o;
				return this.mixtureid.equals(that.mixtureid)
						&& this.aliquotId.equals(that.aliquotId);
				} else
				return false;
			}

		public int hashCode()
			{
			return mixtureid.hashCode() + aliquotId.hashCode();
			}
		}






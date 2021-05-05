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
	public  class MixtureChildrenAliquotPK implements Serializable
		{
		public static MixtureChildrenAliquotPK instance(Mixture mixture, Mixture parentMixture, Aliquot aliquot)
			{
			return new MixtureChildrenAliquotPK(mixture, parentMixture, aliquot);
			}

		@Column(name = "MIXTURE_ID")
		private String mixtureid;

		@Column(name = "PARENT_MIXTURE_ID")
		private String parentMixtureid;
		
		@Column(name = "ALIQUOT_ID")
		private String aliquotId;

		private MixtureChildrenAliquotPK(Mixture mixture, Mixture parentMixture, Aliquot aliquot)
			{
			this.mixtureid= mixture.getMixtureId();
			this.parentMixtureid = parentMixture.getMixtureId();
			this.aliquotId = aliquot.getAliquotId();
			}

		public MixtureChildrenAliquotPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof MixtureChildrenAliquotPK)
				{
				MixtureChildrenAliquotPK that = (MixtureChildrenAliquotPK) o;
				return this.mixtureid.equals(that.mixtureid)
						&& this.parentMixtureid.equals(that.parentMixtureid)
						&& this.aliquotId.equals(that.aliquotId);
				} else
				return false;
			}

		public int hashCode()
			{
			return mixtureid.hashCode() + parentMixtureid.hashCode();
			}
		}






package edu.umich.brcf.shared.layers.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

	@Embeddable
	public  class ExperimentAliquotPK implements Serializable
		{
		public static ExperimentAliquotPK instance(Experiment experiment, Aliquot aliquot)
			{
			return new ExperimentAliquotPK(experiment, aliquot);
			}

		@Column(name = "EXP_ID")
		private String experimentId;

		@Column(name = "ALIQUOT_ID")
		private String aliquotId;

		private ExperimentAliquotPK(Experiment experiment, Aliquot aliquot)
			{
			this.experimentId = experiment.getExpID();
			this.aliquotId = aliquot.getAliquotId();
			}

		public ExperimentAliquotPK()
			{

			}

		public boolean equals(Object o)
			{
			if (o != null && o instanceof ExperimentAliquotPK)
				{
				ExperimentAliquotPK that = (ExperimentAliquotPK) o;
				return this.experimentId.equals(that.experimentId)
						&& this.aliquotId.equals(that.aliquotId);
				} else
				return false;
			}

		public int hashCode()
			{
			return experimentId.hashCode() + aliquotId.hashCode();
			}
		}






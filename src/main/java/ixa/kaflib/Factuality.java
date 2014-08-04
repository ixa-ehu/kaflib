package ixa.kaflib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Factuality layer
 */
public class Factuality {

	Term word;
	ArrayList<FactualityPart> factualityParts = new ArrayList<FactualityPart>();

	public Factuality(Term word) {
		this.word = word;
	}

	public Term getWord() {
		return word;
	}

	public void setWord(Term word) {
		this.word = word;
	}

	public ArrayList<FactualityPart> getFactualityParts() {
		return factualityParts;
	}

	public void addFactualityPart(FactualityPart part) {
		this.factualityParts.add(part);
	}

	public void addFactualityPart(String prediction, double confidence) {
		this.addFactualityPart(new FactualityPart(prediction, confidence));
	}

	public String getId() {
		return word.getWFs().get(0).getId();
	}

	public List<WF> getWFs() {
		return word.getWFs();
	}

	public FactualityPart getMaxPart() {
		FactualityPart ret = null;
		double base = 0;

		for (FactualityPart p : factualityParts) {
			if (p.getConfidence() > base) {
				ret = p;
				base = p.getConfidence();
			}
		}

		return ret;
	}

	class FactualityPart {

		String prediction;
		double confidence;

		FactualityPart(String prediction, double confidence) {
			this.prediction = prediction;
			this.confidence = confidence;
		}

		String getPrediction() {
			return prediction;
		}

		void setPrediction(String prediction) {
			this.prediction = prediction;
		}

		double getConfidence() {
			return confidence;
		}

		void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		@Override
		public String toString() {
			return "FactualityPart{" +
					"prediction='" + prediction + '\'' +
					", confidence=" + confidence +
					'}';
		}
	}

}

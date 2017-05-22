package emotionmining;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emotionmining.model.Tweet;

/**
 * 
 * @author AysoltanGravina
 * 
 *         Calculates: TP, FN and FP for each Label. Calculates: Precision,
 *         Recall and FScore for each Label. Calculates: macro and micro
 *         accuracy for each Label.
 *
 */
public class Evaluator {

	// Attributes
	private int tp;
	private int fp;
	private int fn;

	// Getter and Setter
	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getFp() {
		return fp;
	}

	public void setFp(int fp) {
		this.fp = fp;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	/**
	 * Get Confusion matrix with TP,FP,FN for each label. This object method was
	 * developed on the basis of:
	 * https://github.com/naveen2507/EmotionAnalysis/blob/master/src/com/ims/
	 * evaluation/process/Evaluation.java
	 * 
	 * @param tweetsList
	 * @return HashMap with <Label_i: <TP_i, FN_i, FP_i>>
	 */
	public Map<String, Evaluator> getTP_FN_FP(List<Tweet> tweetsList) {

		// Necessary for storing of each label and their TP_FN_FP-values
		// e.g.: < happy: <TP=10, FN=2, FP=5> >
		Map<String, Evaluator> confusionMatrix = new HashMap<String, Evaluator>();

		// Iterates through the all tweets and
		for (int i = 0; i < tweetsList.size(); i++) {
			// extracts gold and predicted labels for each tweet.
			Tweet tweet = tweetsList.get(i);
			String predictedLabel = tweet.getPredictedLabel();
			String goldLabel = tweet.getGoldLabel();

			// Computes TP.
			// If gold and predicted labels are the same, than increase TP
			if (goldLabel.equalsIgnoreCase(predictedLabel)) {
				// if the label IS in the HashMap, than
				if (confusionMatrix.containsKey(goldLabel)) {
					// initialize with the given TP_FN_FP-values
					Evaluator tpObject = confusionMatrix.get(goldLabel);
					tpObject.setTp(tpObject.getTp() + 1);
					confusionMatrix.put(goldLabel, tpObject);
				} else {
					// otherwise create new object for the TP_FN_FP-values
					Evaluator tpObject = new Evaluator();
					tpObject.setTp(tpObject.getTp() + 1);
					confusionMatrix.put(goldLabel, tpObject);

				}

			}

			// If gold and predicted labels are not the same,
			if (!(goldLabel.equalsIgnoreCase(predictedLabel))) {

				// Computes FN.
				// than increase FN if a label is in the gold
				if (confusionMatrix.containsKey(goldLabel)) {
					Evaluator fnObject = confusionMatrix.get(goldLabel);
					fnObject.setFn(fnObject.getFn() + 1);
					confusionMatrix.put(goldLabel, fnObject);
				} else {
					Evaluator fnObject = new Evaluator();
					fnObject.setFn(fnObject.getFn() + 1);
					confusionMatrix.put(goldLabel, fnObject);
				}

				// Computes FP
				// or increase the FP if the label is in the model.
				if (confusionMatrix.containsKey(predictedLabel)) {
					Evaluator fpObject = confusionMatrix.get(predictedLabel);
					fpObject.setFp(fpObject.getFp() + 1);
					confusionMatrix.put(predictedLabel, fpObject);
				} else {
					Evaluator fpObject = new Evaluator();
					fpObject.setFp(fpObject.getFp() + 1);
					confusionMatrix.put(predictedLabel, fpObject);
				}
			}

		}
		return confusionMatrix;

	}

	/**
	 * Returns Precision
	 * 
	 * @param tp
	 * @param fp
	 */
	public double getPrecision(int tp, int fp) {
		double precision = (double) tp / (tp + fp);
		return precision;

	}

	/**
	 * Returns Recall
	 * 
	 * @param tp
	 * @param fn
	 */
	public double getRecall(int tp, int fn) {
		double recall = (double) tp / (tp + fn);
		return recall;

	}

	/**
	 * Returns FScore
	 * 
	 * @param precision
	 * @param recall
	 */
	public double getFScore(double precision, double recall) {

		if (precision == 0 || recall == 0) {
			return 0.0;
		}
		double fscore = (2 * precision * recall) / (precision + recall);
		return fscore;

	}

	/**
	 * Returns Accuracy
	 * 
	 * @param tp
	 * @param fn
	 * @param fp
	 */
	public double getAccuracy(int tp, int fn, int fp) {
		// TNs were skipped in order to avoid the influence on calculation.
		double accuracy = (double) tp / (tp + fn + fp);
		return accuracy;

	}
}

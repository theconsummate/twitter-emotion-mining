package emotionmining;

import emotionmining.model.EvalObjects;
import emotionmining.model.ResultObjects;
import emotionmining.model.Tweet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates: TP, FN and FP for each Label. Calculates: Precision, Recall and
 * FScore for each Label. Calculates: Macro and Micro for each Label.
 * Open-Calculates: Accuracy for all Labels
 */
public class Evaluator {

	private static Evaluator eval;

	public static synchronized Evaluator getInstance() {
		if (eval == null)
			eval = new Evaluator();
		return eval;
	}

	/**
	 * Get Confusion matrix with TP,FP,FN for each class label
	 * 
	 * @param tweetsList
	 * @return TP, FN, FP
	 */
	public Map<String, EvalObjects> getTP_FN_FP(List<Tweet> tweetsList) {

		Map<String, EvalObjects> valueMatrix = new HashMap<String, EvalObjects>();

		for (int i = 0; i < tweetsList.size(); i++) {

			Tweet tweet = tweetsList.get(i);
			String predictedLabel = tweet.getPredictedLabel();
			String goldLabel = tweet.getGoldLabel();

			if (goldLabel.equalsIgnoreCase(predictedLabel)) {

				if (valueMatrix.containsKey(goldLabel)) {
					EvalObjects values = valueMatrix.get(goldLabel);
					values.setTp(values.getTp() + 1);
					valueMatrix.put(goldLabel, values);
				} else {
					EvalObjects values = EvalObjects.getInstance();
					values.setTp(values.getTp() + 1);
					valueMatrix.put(goldLabel, values);

				}

			}

			if (!(goldLabel.equalsIgnoreCase(predictedLabel))) {

				if (valueMatrix.containsKey(goldLabel)) {
					EvalObjects values = valueMatrix.get(goldLabel);
					values.setFn(values.getFn() + 1);
					valueMatrix.put(goldLabel, values);
				} else {
					EvalObjects values = EvalObjects.getInstance();
					values.setFn(values.getFn() + 1);
					valueMatrix.put(goldLabel, values);
				}

				if (valueMatrix.containsKey(predictedLabel)) {
					EvalObjects values = valueMatrix.get(predictedLabel);
					values.setFp(values.getFp() + 1);
					valueMatrix.put(predictedLabel, values);
				} else {
					EvalObjects values = EvalObjects.getInstance();
					values.setFp(values.getFp() + 1);
					valueMatrix.put(predictedLabel, values);
				}
			}

		}
		return valueMatrix;

	}

	

	/**
	 * Returns Precision
	 * 
	 * @param tp
	 * @param fp
	 * @return Precision
	 */
	public double getPrecision(double tp, double fp) {
		double precision = (double) tp / (tp + fp);
		return precision;

	}

	/**
	 * Returns Recall
	 * 
	 * @param tp
	 * @param fn
	 * @return Recall
	 */
	public double getRecall(double tp, double fn) {
		double recall = (double) tp / (tp + fn);
		return recall;

	}
	
	/**
	 * Returns F Score
	 * 
	 * @param precision
	 * @param recall
	 * @return FScore
	 */
	public double getFScore(double precision, double recall) {

		if (precision == 0 || recall == 0) {
			return 0.0;
		}
		double fscore = (2 * precision * recall) / (precision + recall);
		return fscore;

	}

	/**
	 * Get precision,recall and fscore
	 * 
	 * @param TP_FN_FP
	 * @return
	 */
	public EvalObjects getEvalautionValues(EvalObjects TP_FN_FP) {

		double precision = getPrecision(TP_FN_FP.getTp(), TP_FN_FP.getFp());
		double recall = getRecall(TP_FN_FP.getTp(), TP_FN_FP.getFn());
		TP_FN_FP.setPrecision(precision);
		TP_FN_FP.setRecall(recall);
		TP_FN_FP.setFscore(getFScore(precision, recall));
		return TP_FN_FP;

	}

	/**
	 * Get Macro Fscore
	 * 
	 * @param metric
	 * @param totalClass
	 * @return
	 */
	public ResultObjects getMacroFScore(ResultObjects metric, int totalClass) {
		metric.setMacrofscore(metric.getAllFScore() / totalClass);
		return metric;
	}

	/**
	 * Get Micro Fscore
	 * 
	 * @param metric
	 * @return
	 */
	public ResultObjects getMicroFScore(ResultObjects metric) {
		double aggPrecision = getPrecision(metric.getAllTP(), metric.getAllFP());
		double aggRecall = getRecall(metric.getAllTP(), metric.getAllFN());
		metric.setMicrofscore(getFScore(aggPrecision, aggRecall));
		return metric;
	}
}
package emotionmining;

import java.util.List;
import java.util.Map;

public class Results {

	/**
	 *
	 * Class Description :
	 * 
	 * This is main class for result evaluation. Input : Data File tab separated
	 * Data Format : PredictedClass, GoldClass , and rest fields as per in
	 * orginal file Output : Prints evaluation metrics for each class and print
	 * macro and micro F-score.
	 * 
	 */

	private static Results result;

	public static synchronized Results getInstance() {
		if (result == null)
			result = new Results();
		return result;
	}

	/**
	 * Read data from corpus
	 * 
	 * @param corpus
	 * @return List<TweetVO>
	 */
	public List<Tweet> getData(Corpus corpus) {
		ReadData obj = new ReadData();
		List<Tweet> listTweetDo = obj.getDataValues(corpus).getListTweetVO();
		return listTweetDo;
	}

	/**
	 * Get confusion Matrix depending upon the configuration for Ranking or not
	 * 
	 * @param listTweetVO
	 * @return
	 */
	public ResultObjects getConfusionMatrix(List<Tweet> listTweetVO) {
		Evaluator eval = Evaluator.getInstance();
		Map<String, EvalObjects> confusionMatrix;
		confusionMatrix = eval.getTP_FN_FP(listTweetVO);
		ResultObjects metric = ResultObjects.getInstance();
		metric.setEvaluationMatrix(confusionMatrix);
		return metric;

	}

	/**
	 * Print results
	 * 
	 * @param metric
	 */
	public void getResult(ResultObjects metric) {

		Map<String, EvalObjects> confusionMatrix = metric.getEvaluationMatrix();
		Evaluator eval = Evaluator.getInstance();

		for (String key : confusionMatrix.keySet()) {

			EvalObjects evalValue = eval.getEvalautionValues(confusionMatrix.get(key));

			// System.out.println("True Positive for " + key + " : " +
			// evalValue.getTp());
			metric.setAllTP(metric.getAllTP() + evalValue.getTp());

			// System.out.println("False Positive for " + key + " : " +
			// evalValue.getFp());
			metric.setAllFP(metric.getAllFP() + evalValue.getFp());

			// System.out.println("True Negative for " + key + " : " +
			// evalValue.getTn());
			metric.setAllTN(metric.getAllTN() + evalValue.getTn());

			// System.out.println("False Negative for " + key + " : " +
			// evalValue.getFn());
			metric.setAllFN(metric.getAllFN() + evalValue.getFn());

			// System.out.println("Recall for " + key + " : " +
			// evalValue.getRecall());
			// System.out.println("Precision for " + key + " : " +
			// evalValue.getPrecision());
			System.out.println("FScore for " + key + " : " + evalValue.getFscore());
			metric.setAllFScore(metric.getAllFScore() + evalValue.getFscore());
		}

		// System.out.println("---------------Micro FScore-------------------");
		metric = eval.getMicroFScore(metric);
		System.out.println("Micro FScore  : " + metric.getMicrofscore());

		// System.out.println("---------------Macro FScore-------------------");
		metric = eval.getMacroFScore(metric, confusionMatrix.size());
		System.out.println("Macro FScore  : " + metric.getMacrofscore());

		System.out.println("Total Tp :" + metric.getAllTP());

	}

	/**
	 * 
	 * 
	 * @param arg
	 * 
	 * 
	 */

	public static void main(String arg[]) {

		Results result = Results.getInstance();
		Corpus corpus = new Corpus();
		corpus.setFileName("data/dev.csv");
		List<Tweet> listTweetVO = result.getData(corpus);
		ResultObjects evalAggDO = result.getConfusionMatrix(listTweetVO);

		result.getResult(evalAggDO);

	}
}
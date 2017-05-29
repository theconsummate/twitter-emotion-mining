package emotionmining;

import java.util.*;

import emotionmining.model.Corpus;
import emotionmining.model.NaiveBayesKnowledgeBase;
import emotionmining.model.Tweet;
import emotionmining.naivebayes.NaiveBayes;

/**
 * 
 * @author AysoltanGravina
 * 
 *         This is the class with main method. It outputs the TP, FP, FN
 *         Precision, Recall and FScore for each label. And it outputs the macro
 *         and micro accuracy (for all labels).
 *
 */
public class Main {

	public static void main(String args[]) {
		// Set the file names for the gold and predicted data.
		Corpus corpus = new Corpus();
		corpus.setGoldFileName("data/dev.csv");
		corpus.setPredictedFileName("data/dev-predicted.csv");
		// Get tweets list with their given gold and predicted labels.
		corpus.getEvaluationData();
		List<Tweet> tweetsList = corpus.getTweetsList();

		// Get the TP, FN, FP values for each tweet.
		Evaluator eval = new Evaluator();
		Map<String, Evaluator> confusionMatrix = new HashMap<String, Evaluator>();
		confusionMatrix.putAll(eval.getTP_FN_FP(tweetsList));

		// Variables for calculation of the micro and macro accuracy.
		int allTp = 0;
		int allFn = 0;
		int allFp = 0;
		double macroAccuracy = 0.0;

		
		// System.out.println(confusionMatrix.keySet());
		// Outputting the evaluation for each label.
		for (String key : confusionMatrix.keySet()) {
			int tp = confusionMatrix.get(key).getTp();
			allTp = allTp + tp;
			int fn = confusionMatrix.get(key).getFn();
			allFn = allFn + fn;
			int fp = confusionMatrix.get(key).getFp();
			allFp = allFp + fp;

			double precision = confusionMatrix.get(key).getPrecision(tp, fp);
			double recall = confusionMatrix.get(key).getRecall(tp, fn);
			macroAccuracy = macroAccuracy + confusionMatrix.get(key).getAccuracy(tp, fn, fp);

			System.out.println("********* Outputting the evaluation results for " + key.toUpperCase() + " *********");
			System.out.println(key + "\t\t TP: " + tp + "\t FN: " + fn + "\t FP: " + fp);
			System.out.println("\t\t Precision: " + precision);
			System.out.println("\t\t Recall: " + recall);
			System.out.println("\t\t FScore: " + confusionMatrix.get(key).getFScore(precision, recall));
			System.out.println("\n");
		}
		// Outputting the accuracy for all labels.
		// TNs were skipped in order to avoid the influence on calculation.
		System.out.println("********* Outputting the macro/micro accuracy for all labels *********");
		System.out.println("Micro-Accuracy: " + eval.getAccuracy(allTp, allFn, allFp));
		System.out.println("Macro-Accuracy: " + macroAccuracy / confusionMatrix.size());

//		Naive Bayes Classifier.
		Map<String, List<String>> trainingEx = new HashMap<String, List<String>>();
		for(Tweet tweet: tweetsList){
			List<String> tws = trainingEx.get(tweet.getGoldLabel());
			if(tws == null){
				tws = new ArrayList<String>();
			}
			tws.add(tweet.getTweet());
			trainingEx.put(tweet.getGoldLabel(), tws);
		}

		Map<String, String[]> trainingExamples = new HashMap<String, String[]>();
		for (Map.Entry<String, List<String>> entry : trainingEx.entrySet()) {
			List<String> ss = entry.getValue();
			String[] ss1 = new String[ss.size()];
			ss1 = ss.toArray(ss1);
			trainingExamples.put(entry.getKey(), ss1);
		}

		//train classifier
		NaiveBayes nb = new NaiveBayes();
		nb.train(trainingExamples);

		//get trained classifier knowledgeBase
		NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();

		nb = null;
		trainingExamples = null;


		//Use classifier
		nb = new NaiveBayes(knowledgeBase);
		String exampleEn = "I am sad";
		String outputEn = nb.predict(exampleEn);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleEn, outputEn);

		String exampleFr = "I am happy";
		String outputFr = nb.predict(exampleFr);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleFr, outputFr);

		String exampleDe = "I am in love";
		String outputDe = nb.predict(exampleDe);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleDe, outputDe);
	}

}
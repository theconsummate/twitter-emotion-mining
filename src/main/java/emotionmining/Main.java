package emotionmining;

import java.util.*;

import emotionmining.model.Corpus;
import emotionmining.model.Labels;
import emotionmining.model.NaiveBayesKnowledgeBase;
import emotionmining.model.Tweet;
import emotionmining.naivebayes.NaiveBayes;

/**
 * 
 *
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

		evaluation(tweetsList);

//		Naive Bayes Classifier.
		naiveBayes(tweetsList);

//		perceptron invocation
		perceptron(tweetsList);
	}

	public static void perceptron(List<Tweet> tweetsList){

	}

	public static void naiveBayes(List<Tweet> tweetsList){
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
		/*String exampleEn = "I am sad";
		String outputEn = nb.test(exampleEn);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleEn, outputEn);

		String exampleFr = "I am happy";
		String outputFr = nb.test(exampleFr);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleFr, outputFr);

		String exampleDe = "I am in angry";
		String outputDe = nb.test(exampleDe);
		System.out.format("The sentense \"%s\" was classified as \"%s\".%n", exampleDe, outputDe);*/

        for(Tweet tweet: tweetsList){
            tweet.setPredictedLabel(nb.test(tweet.getTweet()));
        }
        System.out.println("Evaluation for Naive Bayes");
        evaluation(tweetsList);
	}


	public static void evaluation(List<Tweet> tweets){

		// Get the TP, FN, FP values for each tweet.
		Evaluator eval = new Evaluator();
		int[][] confusionMatrix = eval.computeConfusionMatrix(tweets);

		// Variables for calculation of the micro and macro accuracy.
		int allTp = 0;
		int allFn = 0;
		int allFp = 0;
		int allTn = 0;
		double macroAccuracy = 0.0;
        double macroPrecision = 0.0;
        double macroRecall = 0.0;
        double macroFscore = 0.0;


		// System.out.println(confusionMatrix.keySet());
		// Outputting the evaluation for each label.
		for (Labels label : Labels.values()) {
			int tp = eval.computeTP(confusionMatrix, label);
			allTp = allTp + tp;
			int fn = eval.computeFN(confusionMatrix, label);
			allFn = allFn + fn;
			int fp = eval.computeFP(confusionMatrix, label);
			allFp = allFp + fp;
			int tn = eval.computeTN(confusionMatrix, label);
			allTn = allTn + tn;

			double precision = eval.getPrecision(tp, fp);
			double recall = eval.getRecall(tp, fn);
            double fscore = eval.getFScore(precision, recall);
			macroAccuracy = macroAccuracy + eval.getAccuracy(tp, fn, fp, tn);

            macroPrecision += precision;
            macroRecall += recall;
            macroFscore += fscore;

			System.out.println("********* Outputting the evaluation results for " + label.toString() + " *********");
			System.out.println(label.toString() + "\t\t TP: " + tp + "\t FN: " + fn + "\t FP: " + fp + "\t TN: " + tn);
			System.out.println("\t\t Precision: " + precision);
			System.out.println("\t\t Recall: " + recall);
			System.out.println("\t\t FScore: " + fscore);
			System.out.println("\t\t Accuracy: " + eval.getAccuracy(tp, fn, fp, tn));
			System.out.println("\n");
		}
		// Outputting the accuracy for all labels.
		System.out.println("********* Outputting the macro/micro accuracy for all labels *********");
		System.out.println("Macro-Precision: " + macroPrecision/Labels.getSize());
        System.out.println("Macro-Recall: " + macroRecall/Labels.getSize());
        System.out.println("Macro-Fscore: " + macroFscore/Labels.getSize());
		System.out.println("Macro-Accuracy: " + macroAccuracy / Labels.getSize());
	}

}
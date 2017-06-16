package emotionmining;

import emotionmining.model.Corpus;
import emotionmining.model.Labels;
import emotionmining.model.NaiveBayesKnowledgeBase;
import emotionmining.model.Tweet;
import emotionmining.naivebayes.NaiveBayes;
import emotionmining.perceptron.MultiClassPerceptron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *         This is the class with main method. It outputs the TP, FP, FN
 *         Precision, Recall and FScore for each label. And it outputs the macro
 *         and micro accuracy (for all labels).
 *
 */
public class Main {

	public static void main(String args[]) {
		// Set the file names for the gold and predicted data.
		Corpus corpus = new Corpus();
//		corpus.setGoldFileName("data/dev.csv");
		corpus.setGoldFileName("data/train.csv");
		corpus.setPredictedFileName("data/dev-predicted.csv");
		// Get tweets list with their given gold and predicted labels.
		corpus.getEvaluationData();
		List<Tweet> tweetsList = corpus.getTweetsList();
//                .subList(0, 10);

//		evaluation(tweetsList);

//		Naive Bayes Classifier.
//		naiveBayes(tweetsList);

//		perceptron invocation
		String modelfileName = "data/snowballStemModel.csv";
		String modelfileName1 = "data/stanfordStemModel.csv";

        try {
//            perceptronTrain(tweetsList, modelfileName);
			System.out.println("Evaluating Snowball model");
			System.out.println("Computing features using snowball stemmer");
			tweetsList = FeatureExtraction.snowballStemmer(tweetsList, "data/snowball_train_stems.csv");
            perceptronTest(tweetsList, modelfileName);
			System.out.println("\n\nEvaluating Stanford model");
			System.out.println("Computing features using stanford parser");
			tweetsList = FeatureExtraction.posTaggingAndStemming(tweetsList, "data/stanford_train_stems.csv");
			perceptronTest(tweetsList, modelfileName1);
		} catch (IOException e) {
            e.printStackTrace();
        }
//        FeatureExtraction.posTaggingAndStemming(tweetsList);

//		FeatureExtraction.snowballStemmer(tweetsList);
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

	public static void perceptronTrain(List<Tweet> tweetsList, String modelfileName) throws IOException {
		//Set Features for each Tweet
		/*for (int i = 0; i < tweetsList.size(); i++) {
			Tweet tweet = tweetsList.get(i);
			Map<String, Double> featureVector = new HashMap<String, Double>();
			featureVector.put("1", 0.2);
			featureVector.put("2", 0.3);
			tweet.setFeatures(featureVector);
		}*/
        System.out.println("********* Extracting features *********");
//        tweetsList = FeatureExtraction.posTaggingAndStemming(tweetsList, "data/stanford_stems.csv");
        tweetsList = FeatureExtraction.snowballStemmer(tweetsList, "data/snowball_stems.csv");

        System.out.println("********* Starting tranining *********");

		// Define the number of maximum epoches or iterations
		int MAX_ITER = 100;

		// Initialize the weight vector
		Map<String, Map<String, Double>> weightMap = new HashMap<String, Map<String, Double>>();
		// Create multi-class perceptron
		MultiClassPerceptron perceptron = new MultiClassPerceptron();
		// train the model
		weightMap = perceptron.trainModel(tweetsList, MAX_ITER);
		// writes the weights into the file
		writeWeights(modelfileName, weightMap);
	}

	public static void perceptronTest(List<Tweet> tweetsList, String weightFileName) throws IOException {
		Map<String, Map<String, Double>> weightMap = readWeights(weightFileName);
		MultiClassPerceptron perceptron = new MultiClassPerceptron();
		List<Tweet> listPredictedTweet = new ArrayList<Tweet>();
		for (Tweet tweetInstance : tweetsList) {

			tweetInstance = perceptron.testModel(tweetInstance, weightMap);
			listPredictedTweet.add(tweetInstance);
		}

		System.out.println("Evaluation for Perceptron");
		evaluation(listPredictedTweet);
	}

	/*
	* utility functions
	*/
	private static void writeWeights(String fileName, Map<String, Map<String, Double>> weightMap) throws IOException {

		FileWriter writer = new FileWriter(fileName);

		for (String label : weightMap.keySet()) {
			writer.append(label);
			writer.append("\n");
			Map<String, Double> weight = weightMap.get(label);
			for (String feature : weight.keySet()) {
				writer.append(feature + "------:::::::::::::::::::::::::::::------" + weight.get(feature));
				writer.append("\n");
			}
			writer.append("------------------------------------");
			writer.append("\n");
		}
		writer.flush();
		writer.close();

	}

	private static Map<String, Map<String, Double>> readWeights(String fileName) throws IOException {

		// int featuresNumber = ApplicationDetails.numOfFeatures + 1;
		Map<String, Map<String, Double>> weightMap = new HashMap<String, Map<String, Double>>();

		String line;
		BufferedReader br;
		try {
			Map<String, Double> weight = new HashMap<String, Double>();
			String tempCategory = "";

			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {

				if (line.isEmpty()) {
					continue;
				} else if (line.contains("------:::::::::::::::::::::::::::::------")) {
					String elem[] = line.split("------:::::::::::::::::::::::::::::------");
					try {
						weight.put(elem[0], Double.parseDouble(elem[1]));
					} catch (Exception e) {
						System.out.println(line);

						System.out.println(elem[0] + "--------" + elem[1]);

						break;
					}
				} else if (line.equalsIgnoreCase("------------------------------------")) {
					weightMap.put(tempCategory, weight);
					weight = new HashMap<String, Double>();
					tempCategory = "";
				} else {
					tempCategory = line;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return weightMap;

	}

}
package emotionmining;

import emotionmining.model.*;
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

import org.apache.commons.cli.*;

/**
 *         This is the class with main method. It outputs the TP, FP, FN
 *         Precision, Recall and FScore for each label. And it outputs the macro
 *         and micro accuracy (for all labels).
 *
 */
public class Main {

//	private


	public static void main(String args[]) {
		Options options = initCLI(new Options());
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		String stemmer = "";
		boolean useCachedStems = false;
		try {
			cmd = parser.parse( options, args);

			if(cmd.hasOption("h")){
				help(options);
			}
			if(cmd.hasOption("cs")){
				useCachedStems = true;
			}
			if(cmd.hasOption("s")){
				stemmer = cmd.getOptionValue("s").toLowerCase();
				if(stemmer.equals("stanford") || stemmer.equals("snowball")
						|| stemmer.equals("twokenize") || stemmer.equals("naivebayes")){
// correct stemmer chosen, do nothing.
				} else {
					System.out.println("Please select a valid stemmer from STANFORD, SNOWBALL, TWOKENIZE");
					help(options);
				}
			} else{
				System.out.println("select a stemmer/tokeniser");
				help(options);
			}
			if(cmd.hasOption("neg")){
				System.out.println("Using Negations features");
				FeatureExtraction.useNegationfeatures = true;
			} else{
				FeatureExtraction.useNegationfeatures = false;
			}
			if(cmd.hasOption("nrc")){
				System.out.println("Using NRC features");
				FeatureExtraction.useNRCfeatures = true;
			} else{
				FeatureExtraction.useNRCfeatures = false;
			}

			if(cmd.hasOption("tr")){
//				training
				if(cmd.hasOption("tf")){
					if(cmd.hasOption("mf")){
						if(stemmer.equalsIgnoreCase("naivebayes")){
							runNaiveBayes(cmd.getOptionValue("tf"), cmd.getOptionValue("df"));
						} else {
							System.out.println("Training using " + stemmer);
							List<Tweet> tweetsList = getTweetList(cmd.getOptionValue("tf"));
							String modelFileName = cmd.getOptionValue("mf");
							perceptronTrain(tweetsList, modelFileName, stemmer, useCachedStems);
							System.out.println("Training: Evaluating " + stemmer + " model");
							perceptronTest(tweetsList, modelFileName);
						}
					} else{
						System.out.println("Model filename not given");
						help(options);
					}
				} else{
					System.out.println("training filename not given");
					help(options);
				}
			}
			if(cmd.hasOption("te")){
				if(cmd.hasOption("df")){
					if(cmd.hasOption("mf")){
						if(cmd.hasOption("csf")) {
							System.out.println("Testing: Evaluating " + stemmer + " model");
							List<Tweet> tweetsList = getTweetList(cmd.getOptionValue("df"));
							tweetsList = extractDevFeatures(tweetsList, stemmer, cmd.getOptionValue("csf"), useCachedStems);
							perceptronTest(tweetsList, cmd.getOptionValue("mf"));
						} else {
							System.out.println("filename not provided for caching stems");
							help(options);
						}
					} else{
						System.out.println("Model filename not give");
						help(options);
					}
				} else {
					System.out.println("Dev filename not give");
					help(options);
				}
			}
		} catch (ParseException e) {
			help(options);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static List<Tweet> getTweetList(String filename){
		Corpus corpus = new Corpus();
		corpus.setGoldFileName(filename);
		corpus.setPredictedFileName("data/dev-predicted.csv");
		corpus.getEvaluationData();
		return corpus.getTweetsList();
//		                .subList(0, 10);

	}


//	FeatureExtraction.snowballStemmer(tweetsList, "data/snowball_dev_stems.csv", true);
	private static List<Tweet> extractDevFeatures(List<Tweet> tweetsList, String stemmer, String stemCacheFilename, boolean usecachedStems) throws IOException {
//		"data/stanford_train_stems.csv"
//		"data/snowball_dev_stems.csv"
		if(stemmer.equals("stanford")){
			return FeatureExtraction.posTaggingAndStemming(tweetsList, stemCacheFilename, usecachedStems);
		} else if(stemmer.equals("snowball")){
			return FeatureExtraction.snowballStemmer(tweetsList, stemCacheFilename, usecachedStems);
		} else if(stemmer.equals("twokenize")){
			return FeatureExtraction.twokenizeLib(tweetsList);
		}
//		default case
		return tweetsList;
	}

	public static void runNaiveBayes(String trainfile, String devfile){
//		Naive Bayes Classifier.
		Corpus corpus = new Corpus();
		corpus.setPredictedFileName("data/dev-predicted.csv");
//		corpus.setGoldFileName("data/dev.csv");
		corpus.setGoldFileName(trainfile);
		corpus.getEvaluationData();
		List<Tweet> tweetsList = corpus.getTweetsList();
		System.out.println("Training model");
		NaiveBayesModel naiveBayesModel = naiveBayesTrain(tweetsList);
		System.out.println("evaluating model on training data");
		naiveBayesTest(naiveBayesModel, tweetsList);
		corpus.setGoldFileName(devfile);
		corpus.getEvaluationData();
		tweetsList = corpus.getTweetsList();
		System.out.println("evaluating model on testing data");
		naiveBayesTest(naiveBayesModel, tweetsList);
	}


	private static Options initCLI(Options options){
		options.addOption("h", "help", false, "show help.");
		options.addOption("s", "stemmer", true, "Choose which stemmer to choose.");
		options.addOption("neg", "negation", false, "use negation features");
		options.addOption("nrc", "nrc", false, "use nrc features");
		options.addOption("tr", "train", false, "train the model");
		options.addOption("te", "test", false, "train the model");
		options.addOption("mf", "model-file", true, "model file");
		options.addOption("tf", "train-file", true, "training file");
		options.addOption("df", "dev-file", true, "dev file");
		options.addOption("cs", "cached-stems", false, "use cached stems");
		options.addOption("csf", "cached-stems-file", true, "file to read/write cache stems");
		return options;
	}

	private static void help(Options options) {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("Main", options);
		System.exit(0);

	}

	public static NaiveBayesModel naiveBayesTrain(List<Tweet> tweetsList){
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
		NaiveBayesModel knowledgeBase = nb.getNaiveBayesModel();
		return knowledgeBase;
	}

	public static void naiveBayesTest(NaiveBayesModel knowledgeBase, List<Tweet> tweetsList){
		//Use classifier
		NaiveBayes nb = new NaiveBayes(knowledgeBase);

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

	public static void perceptronTrain(List<Tweet> tweetsList, String modelfileName, String stemmerType, boolean readStemsFromFile) throws IOException {
		//Set Features for each Tweet
		/*for (int i = 0; i < tweetsList.size(); i++) {
			Tweet tweet = tweetsList.get(i);
			Map<String, Double> featureVector = new HashMap<String, Double>();
			featureVector.put("1", 0.2);
			featureVector.put("2", 0.3);
			tweet.setFeatures(featureVector);
		}*/

		if(stemmerType.equalsIgnoreCase("stanford")) {
			System.out.println("********* Extracting features using Stanford Stemmer *********");
			tweetsList = FeatureExtraction.posTaggingAndStemming(tweetsList, "data/stanford_train_stems.csv", readStemsFromFile);
		} else if(stemmerType.equalsIgnoreCase("snowball")) {
			System.out.println("********* Extracting features user Snowball Stemmer *********");
			tweetsList = FeatureExtraction.snowballStemmer(tweetsList, "data/snowball_train_stems.csv", readStemsFromFile);
		}
		else if (stemmerType.equalsIgnoreCase("twokenize")){
			System.out.println("********* Extracting features user Twokenize *********");
			tweetsList = FeatureExtraction.twokenizeLib(tweetsList);
		}
		else{
			System.out.println("No stemmer/tokeniser provided");
			System.exit(0);
		}

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

			if(tweetInstance.getFeatures() == null){
				continue;
			}

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
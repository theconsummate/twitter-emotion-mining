package emotionmining.perceptron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emotionmining.model.Tweet;

public class Perceptron {

	String modelfileName = "modelfileName.csv";

	public void perceptronTrain(List<Tweet> tweetsList) throws IOException {

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

	public List<Tweet> perceptronTest(String weightFileName, List<Tweet> tweetsList) throws IOException {

		Map<String, Map<String, Double>> weightMap = readWeights(weightFileName);
		MultiClassPerceptron perceptron = new MultiClassPerceptron();
		List<Tweet> listPredictedTweet = new ArrayList<Tweet>();
		for (Tweet tweetInstance : tweetsList) {

			tweetInstance = perceptron.testModel(tweetInstance, weightMap);
			listPredictedTweet.add(tweetInstance);
		}

		return listPredictedTweet;

	}

	public void writeWeights(String fileName, Map<String, Map<String, Double>> weightMap) throws IOException {

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

	public Map<String, Map<String, Double>> readWeights(String fileName) throws IOException {

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
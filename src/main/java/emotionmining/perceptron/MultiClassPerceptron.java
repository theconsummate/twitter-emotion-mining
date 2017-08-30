package emotionmining.perceptron;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import emotionmining.model.Tweet;


public class MultiClassPerceptron {

	public Map<String, Map<String, Double>> initializeWeightVecor() {

		Map<String, Map<String, Double>> weightVector = new HashMap<String, Map<String, Double>>();
		List<String> labels = Arrays.asList("anger", "disgust", "fear", "happy", "love", "sad", "surprise", "trust");
		// Initialize weight vectors for each class with bias 0.1
		for (String label : labels) {
			Map<String, Double> initWeightVector = new HashMap<String, Double>();
			initWeightVector.put("0", 0.1);
			weightVector.put(label, initWeightVector);
		}
		return weightVector;
	}

	public Map<String, Map<String, Double>> trainModel(List<Tweet> tweetsList, int MAX_ITER) {

		// Evaluator eval = new Evaluator();
		Map<String, Double> tempWeights;
		List<Tweet> tweetsListEval;

		System.out.println("********** Train The Model ***********");

		Map<String, Map<String, Double>> weightVector = initializeWeightVecor();

		// ADD: MAX_ITER oder bis alle Instanzen richtig klassifiziert sind
		// Number of epochs
		for (int i = 0; i < MAX_ITER; i++) {

			tweetsListEval = new ArrayList<Tweet>();

			// for each point = for each tweet instance or each line in the
			// training data
			for (Tweet tweetInstance : tweetsList) {
				// Get the features for each instance (in form <FeatureName :
				// FeatureValue>)
				Map<String, Double> featureVector = tweetInstance.getFeatures();
				if(featureVector == null){
					continue;
				}
				// Get the weights for each instance (in form <Label:
				// <FeatureName : FeatureValue>>)
				weightVector = getWeightVecors(featureVector.keySet(), weightVector);
				featureVector = putBiasInFeatureVector(featureVector);
				tweetInstance.setFeatures(featureVector);
				tweetInstance = getWinningPerceptron(tweetInstance, weightVector);

				if (!(tweetInstance.getGoldLabel().equalsIgnoreCase(tweetInstance.getPredictedLabel()))) {
					tempWeights = getAdjustedWeight(weightVector.get(tweetInstance.getGoldLabel()), true,
							featureVector);
					weightVector.put(tweetInstance.getGoldLabel(), tempWeights);
					tempWeights = getAdjustedWeight(weightVector.get(tweetInstance.getPredictedLabel()), false,
							featureVector);
					weightVector.put(tweetInstance.getPredictedLabel(), tempWeights);

				}
				tweetsListEval.add(tweetInstance);

			}
			// System.out.println("Number of epochs is: " + i);
		}
		return weightVector;
	}

	public Map<String, Map<String, Double>> getWeightVecors(Set<String> featureKeys,
			Map<String, Map<String, Double>> weightMap) {

		// anger, disgust, fear, happy, love, sad, surprise, trust
		List<String> labels = Arrays.asList("anger", "disgust", "fear", "happy", "love", "sad", "surprise", "trust");

		for (String label : labels) {
			// In order to handle sparse data "existing" features will be added
			Map<String, Double> labelWeightVector = weightMap.get(label);
			for (String featureStr : featureKeys) {
				if (labelWeightVector.containsKey(featureStr)) {
					continue;
				} else {
					labelWeightVector.put(featureStr, 0.1);
				}
			}
			weightMap.put(label, labelWeightVector);
		}
		return weightMap;

	}

	// überarbeite
	public Map<String, Double> putBiasInFeatureVector(Map<String, Double> featureVector) {
		// Put at the first position
		featureVector.put("0", 0.1);
		return featureVector;

	}

	public Tweet getWinningPerceptron(Tweet tweetInstance, Map<String, Map<String, Double>> weightMap) {

		double argmax = 0.0;
		Map<String, Double> featureVector = tweetInstance.getFeatures();
		for (String label : weightMap.keySet()) {
			double argmax_y = argMaxY(weightMap.get(label), featureVector);
			if (argmax_y > argmax) {
				argmax = argmax_y;
				tweetInstance.setPredictedLabel(label);
			}
		}
		return tweetInstance;

	}

	public Double argMaxY(Map<String, Double> weights, Map<String, Double> featureVector) {

		double argmax_y = 0.0;
		for (String feature : featureVector.keySet()) {
			// This case can appear during the test
			if (!weights.containsKey(feature)) {
				weights.put(feature, 0.1);
			}
			if (weights.containsKey(feature)) {
				double w = weights.get(feature);
				double x = featureVector.get(feature);
				argmax_y = argmax_y + (w * x);
			}

		}

		return argmax_y;

	}

	public Map<String, Double> getAdjustedWeight(Map<String, Double> weightVector, boolean action,
			Map<String, Double> featureVector) {

		// Map<String, Double> adjWeightVector = new HashMap<String, Double>();
		for (String feature : featureVector.keySet()) {
			double w = 0;
			if(weightVector != null){
				if(feature == null){
					System.out.println("feature is null");
				}
				w = weightVector.get(feature);
			} else{
				System.out.println("weight vector was null");
				weightVector = new HashMap<String, Double>();
			}
			double x = featureVector.get(feature);
			if (action) {
				w = w + x;
			} else {
				w = w - x;
			}
			weightVector.put(feature, w);

		}

		return weightVector;

	}

	public Tweet testModel(Tweet tweetInstance, Map<String, Map<String, Double>> weightMap) {
		Map<String, Double> featureVector = tweetInstance.getFeatures();
		featureVector = putBiasInFeatureVector(featureVector);
		tweetInstance = getWinningPerceptron(tweetInstance, weightMap);
		return tweetInstance;
	}
}


package emotionmining.perceptron;

public class PerceprtronTest {

	/*
	public static final int NUM_FEATURES = 10;

	public Map<String, List<FeatureTuple>> initializeWeightVector() {

		Map<String, List<FeatureTuple>> weightVector = new HashMap<String, List<FeatureTuple>>();
		List<String> labels = Arrays.asList("anger", "disgust", "fear", "happy", "love", "sad", "surprise", "trust");
		// Initialize weight vectors for each class with bias 0.1
		for (String label : labels) {
			List<FeatureTuple> bias = new LinkedList<FeatureTuple>();
			for(int i = 0; i < NUM_FEATURES; i++){
				bias.add(new FeatureTuple(i, 0.1));
			}
			weightVector.put(label, bias);
		}
		return weightVector;
	}

	public Map<String, List<FeatureTuple>> trainModel(List<Tweet> tweetsList, int MAX_ITER) {

		// Evaluator eval = new Evaluator();
        List<FeatureTuple> tempWeights;
		List<Tweet> tweetsListEval;

		System.out.println("********** Train The Model ***********");

		Map<String, List<FeatureTuple>> weightVector = initializeWeightVector();

		// ADD: MAX_ITER oder bis alle Instanzen richtig klassifiziert sind
		// Number of epochs
		for (int i = 0; i < MAX_ITER; i++) {

//			tweetsListEval = new ArrayList<Tweet>();

			// for each point = for each tweet instance or each line in the
			// training data
			for (Tweet tweetInstance : tweetsList) {
				// Get the features for each instance (in form <FeatureName :
				// FeatureValue>)
//                add 0.1 at index 0 for bias
				List<FeatureTuple> featureVector = new LinkedList<FeatureTuple>();
                featureVector.add(new FeatureTuple(0, 0.1));
                featureVector.addAll(tweetInstance.getFeatures());
				// Get the weights for each instance (in form <Label:
				// <FeatureName : FeatureValue>>)
				tweetInstance.setFeatures(featureVector);
				tweetInstance = getWinningPerceptron(tweetInstance, weightVector);

				if (tweetInstance.getMaxScoreCategory() == 0.0) {
					tempWeights = vectorArtimetic(weightVector.get(tweetInstance.getGoldLabel()),
							featureVector, true);
					weightVector.put(tweetInstance.getGoldLabel(), tempWeights);
					continue;
				}
				if (!(tweetInstance.getGoldLabel().equalsIgnoreCase(tweetInstance.getPredictedLabel()))) {
					tempWeights = vectorArtimetic(weightVector.get(tweetInstance.getGoldLabel()),
							featureVector, true);
					weightVector.put(tweetInstance.getGoldLabel(), tempWeights);
					tempWeights = vectorArtimetic(weightVector.get(tweetInstance.getPredictedLabel()),
							featureVector, false);
					weightVector.put(tweetInstance.getPredictedLabel(), tempWeights);

				}
//				tweetsListEval.add(tweetInstance);

			}
			System.out.println("Number of epochs is: " + i);
		}
		return weightVector;
	}

	private double computeDotProductofTuples(List<FeatureTuple> v1, List<FeatureTuple> v2){
		double product = 0.0;
		Iterator v1_it = v1.iterator();
		Iterator v2_it = v2.iterator();
		// get the first element of both lists
		FeatureTuple v11 = null, v22 = null;
		if(v1_it.hasNext()){
			v11 = (FeatureTuple) v1_it.next();
		}
		if(v2_it.hasNext()){
			v22 = (FeatureTuple) v2_it.next();
		}
		if (v11 == null || v22 == null){
//			one or both vector is only 0's
			return 0.0;
		}
//		compute product at first item
		if(v11.getIndex() == v22.getIndex()){
			product += v11.getFeatureValue() * v22.getFeatureValue();
		}
		while (v1_it.hasNext() && v2_it.hasNext()) {
//			move to next item in the list where we are at the lower index.
			if(v11.getIndex() < v22.getIndex()){
				v11 = (FeatureTuple) v1_it.next();
			} else{
				v22 = (FeatureTuple) v2_it.next();
			}

			if(v11.getIndex() == v22.getIndex()){
				product += v11.getFeatureValue() * v22.getFeatureValue();
			}
		}

		return product;

	}

	public Tweet getWinningPerceptron(Tweet tweetInstance, Map<String, List<FeatureTuple>> weightMap) {

		double argmax = 0.0;
		String weighStr = "";
		for (String label : weightMap.keySet()) {
			// System.out.println("Label ::: "+label);
			double sum = computeDotProductofTuples(tweetInstance.getFeatures(), weightMap.get(label));
			weighStr = weighStr + label + ":" + sum + ",";
			if (sum > argmax) {
				argmax = sum;
				tweetInstance.setPredictedLabel(label);
				tweetInstance.setMaxScoreCategory(sum);
			}
		}

		weighStr = weighStr.substring(0, weighStr.length() - 1);
		tweetInstance.setWeightStr(weighStr);
		return tweetInstance;

	}

	public Double sumFunktion(Map<String, Double> weights, Map<String, Double> featureVector) {

		double sum = 0.0;
		for (String feature : featureVector.keySet()) {
			if (weights.containsKey(feature)) {
				double w = weights.get(feature);
				double x = featureVector.get(feature);
				sum = sum + (w * x);
			}

		}

		return sum;

	}

    private double addSubtract(double a, double b, boolean isSum){
        if(isSum){
            return a + b;
        }
        return a - b;
    }

    private List<FeatureTuple> vectorArtimetic(List<FeatureTuple> v1, List<FeatureTuple> v2, boolean isSum){
        List<FeatureTuple> sum = new LinkedList<FeatureTuple>();
        Iterator v1_it = v1.iterator();
        Iterator v2_it = v2.iterator();
        // get the first element of both lists
        FeatureTuple v11 = null, v22 = null;
        if(v1_it.hasNext()){
            v11 = (FeatureTuple) v1_it.next();
        }
        if(v2_it.hasNext()){
            v22 = (FeatureTuple) v2_it.next();
        }
        if (v11 == null || v22 == null){
//			one or both vector is only 0's
            return sum;
        }
//		compute product at first item
        if(v11.getIndex() == v22.getIndex()){
            sum.add(new FeatureTuple(v11.getIndex(), addSubtract(v11.getFeatureValue(), v22.getFeatureValue(), isSum)));
        }
        while (v1_it.hasNext() && v2_it.hasNext()) {
//			move to next item in the list where we are at the lower index.
            if(v11.getIndex() < v22.getIndex()){
                sum.add(v11);
                v11 = (FeatureTuple) v1_it.next();
            } else{
                sum.add(v22);
                v22 = (FeatureTuple) v2_it.next();
            }

            if(v11.getIndex() == v22.getIndex()){
                sum.add(new FeatureTuple(v11.getIndex(), addSubtract(v11.getFeatureValue(), v22.getFeatureValue(), isSum)));
            }
        }

//        either of the list has ended. add the remainning items
        while(v1_it.hasNext()){
            sum.add((FeatureTuple) v1_it.next());
        }
        while(v2_it.hasNext()){
            sum.add((FeatureTuple) v2_it.next());
        }

        return sum;
    }

	public Map<String, Double> getAdjustedWeight(List<FeatureTuple> weights, boolean action, List<FeatureTuple> featureVector) {

		// Map<String, Double> adjWeightVector = new HashMap<String, Double>();
        if (action) {
            w = w + x;
        } else {
            w = w - x;
        }

	}*/
}

package emotionmining.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by dhruv on 27/05/17.
 */
public class NaiveBayesModel {
    /**
     * number of training observations
     */
    public int n=0;

    /**
     * number of categories
     */
    public int c=0;

    /**
     * number of features
     */
    public int d=0;

    /**
     * log priors for log( P(c) )
     */
    public Map<String, Double> logPriors = new HashMap<String, Double>();

    /**
     * log likelihood for log( P(x|c) )
     */
    public Map<String, Map<String, Double>> logLikelihoods = new HashMap<String, Map<String, Double>>();
}
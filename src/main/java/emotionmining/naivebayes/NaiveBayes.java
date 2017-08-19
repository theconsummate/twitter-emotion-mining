package emotionmining.naivebayes;

import emotionmining.FeatureExtraction;
import emotionmining.model.Document;
import emotionmining.model.FeatureStats;
import emotionmining.model.NaiveBayesModel;

import java.util.*;

/**
 * Created by dhruv on 20/05/17.
 */

public class NaiveBayes {
    private NaiveBayesModel naiveBayesModel;

    // load a classifier which has been already trained.
    public NaiveBayes(NaiveBayesModel knowledgeBase) {
        this.naiveBayesModel = knowledgeBase;
    }

    public NaiveBayes() {
        this(null);
    }

    public NaiveBayesModel getNaiveBayesModel() {
        return naiveBayesModel;
    }

    private FeatureStats initModel(Map<String, String[]> trainingDataset) {
        //preprocess the given dataset
        List<Document> dataset = FeatureExtraction.preprocessNaiveBayes(trainingDataset);
        //produce the feature stats and select the best features
        FeatureStats featureStats = FeatureExtraction.extractFeatureStats(dataset);
        //intiliaze the naiveBayesModel of the classifier
        naiveBayesModel = new NaiveBayesModel();
        naiveBayesModel.n = featureStats.n; //number of observations
        naiveBayesModel.f = featureStats.featureCategoryJointCount.size(); //number of features

        // estimate the priors from the sample
        naiveBayesModel.c = featureStats.categoryCounts.size(); //number of cateogries

        naiveBayesModel.logPriors = new HashMap<String, Double>();
        String category;
        int count;
        for (Map.Entry<String, Integer> entry : featureStats.categoryCounts.entrySet()) {
            category = entry.getKey();
            count = entry.getValue();
            naiveBayesModel.logPriors.put(category, Math.log((double) count / naiveBayesModel.n));
        }

        return featureStats;

    }

    /**
     * Trains a Naive Bayes classifier by using the Multinomial Model by passing
     * the trainingDataset.
     *
     * @param trainingDataset
     */
    public void train(Map<String, String[]> trainingDataset) {

        FeatureStats featureStats = this.initModel(trainingDataset);

        //We are performing laplace smoothing (also known as add-1). This requires to estimate the total feature occurrences in each category
        Map<String, Double> featureOccurrencesInCategory = new HashMap<String, Double>();
        Integer occurrences;
        Double featureOccSum;
        for (String category : naiveBayesModel.logPriors.keySet()) {
            featureOccSum = 0.0;
            for (Map<String, Integer> categoryListOccurrences : featureStats.featureCategoryJointCount.values()) {
                occurrences = categoryListOccurrences.get(category);
                if (occurrences != null) {
                    featureOccSum += occurrences;
                }
            }
            featureOccurrencesInCategory.put(category, featureOccSum);
        }

        //estimate log likelihoods
        String feature;
        Integer count;
        Map<String, Integer> featureCategoryCounts;
        double logLikelihood;
        for (String category : naiveBayesModel.logPriors.keySet()) {
            for (Map.Entry<String, Map<String, Integer>> entry : featureStats.featureCategoryJointCount.entrySet()) {
                feature = entry.getKey();
                featureCategoryCounts = entry.getValue();
                count = featureCategoryCounts.get(category);
                if (count == null) {
                    count = 0;
                }
                logLikelihood = Math.log((count + 1.0) / (featureOccurrencesInCategory.get(category) + naiveBayesModel.f));
                if (naiveBayesModel.logLikelihoods.containsKey(feature) == false) {
                    naiveBayesModel.logLikelihoods.put(feature, new HashMap<String, Double>());
                }
                naiveBayesModel.logLikelihoods.get(feature).put(category, logLikelihood);
            }
        }
        featureOccurrencesInCategory = null;
    }

    /**
     * Predicts the category of a text by using an already trained classifier
     * and returns its category.
     *
     * @param text
     * @return
     * @throws IllegalArgumentException
     */
    public String test(String text) throws IllegalArgumentException {
        if (naiveBayesModel == null) {
            throw new IllegalArgumentException("Knowledge Bases missing: Make sure you train first a classifier before you use it.");
        }
//Tokenizes the text and creates a new document
        Document doc = TextTokenizer.tokenize(text);
        String category;
        String feature;
        Integer occurrences;
        Double logprob;
        String maxScoreCategory = null;
        Double maxScore = Double.NEGATIVE_INFINITY;
//Map<String, Double> predictionScores = new HashMap<>();
        for (Map.Entry<String, Double> entry1 : naiveBayesModel.logPriors.entrySet()) {
            category = entry1.getKey();
            logprob = entry1.getValue(); //intialize the scores with the priors
//foreach feature of the document
            for (Map.Entry<String, Integer> entry2 : doc.tokens.entrySet()) {
                feature = entry2.getKey();
                if (!naiveBayesModel.logLikelihoods.containsKey(feature)) {
                    continue; //if the feature does not exist in the knowledge base skip it
                }
                occurrences = entry2.getValue(); //get its occurrences in text
                logprob += occurrences * naiveBayesModel.logLikelihoods.get(feature).get(category); //multiply loglikelihood score with occurrences
            }
//predictionScores.put(category, logprob);
            if (logprob > maxScore) {
                maxScore = logprob;
                maxScoreCategory = category;
            }
        }
        return maxScoreCategory; //return the category with heighest score
    }
}
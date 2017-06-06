package emotionmining.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 *         This class define the data structure Tweet which contains tweet
 *         itself, gold and predicted labels. Additional there is the object
 *         method which converts the tweet into the list of tokens.
 *
 */
public class Tweet {

	// Attributes
	private String tweet;
	private List<Token> tokensList; // necessary for later feature extraction
	private String goldLabel;
	private String predictedLabel;

	private String weightStr;
	/*
	* 0 index is bias, add all other features from index 1 onwards
	* list is sorted by index
	* */
	private List<FeatureTuple> features;
	private Double maxScoreCategory = 0.0;

	// Getters and Setters
	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public List<Token> getTokensList() {
		return tokensList;
	}

	public void setTokensList(List<Token> tokensList) {
		this.tokensList = tokensList;
	}

	public String getGoldLabel() {
		return goldLabel;
	}

	public void setGoldLabel(String goldLabel) {
		this.goldLabel = goldLabel;
	}

	public String getPredictedLabel() {
		return predictedLabel;
	}

	public void setPredictedLabel(String predictedLabel) {
		this.predictedLabel = predictedLabel;
	}

	public String getWeightStr() {
		return weightStr;
	}

	public void setWeightStr(String weightStr) {
		this.weightStr = weightStr;
	}

	public List<FeatureTuple> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureTuple> features) {
		this.features = features;
	}

	public Double getMaxScoreCategory() {
		return maxScoreCategory;
	}

	public void setMaxScoreCategory(Double maxScoreCategory) {
		this.maxScoreCategory = maxScoreCategory;
	}

	// Object method of Tweet: tokenizes the tweet into Token data type
	// necessary for later feature extraction
	public void getTokensList(String tweet) {
		List<Token> tokensList = new ArrayList<Token>();
		String tokens[] = tweet.split(" ");
		for (String key : tokens) {
			Token token = new Token();
			token.setToken(key);
			tokensList.add(token);
			setTokensList(tokensList);
		}
	}
	
}

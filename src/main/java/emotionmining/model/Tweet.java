package emotionmining.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Twokenize;
import corpus.Token;

/**
 * 
 * @author AysoltanGravina
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

	private Map<String, Double> features;

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

	public Map<String, Double> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Double> features) {
		this.features = features;
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
	
	public void tokenize(String content) {

		List<Token> tokensList = new ArrayList<Token>();

		for (String word : Twokenize.tokenizeRawTweetText(content)) {

			String cleanWord = word;

			// Replace URLs to a generic URL
			if (word.matches("http.*|ww\\..*")) {
				cleanWord = "http://www.url.com";
			}

			// Replaces user mentions to a generic user
			else if (word.matches("@.*")) {
				cleanWord = "@user";
			}

			Token token = new Token();
			token.setToken(cleanWord);
			tokensList.add(token);
		}
		setTokensList(tokensList);
	}
}
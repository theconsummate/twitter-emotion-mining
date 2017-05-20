package emotionmining.model;

/**
 * 
 *
 * Class Description : It stores the value of the dev.csv file as an object for
 * each row/instance. It includes tweet,goldLabel,predictedLabe.
 * 
 */
public class Tweet {

	// do List<Token>
	private String tweet;
	private String goldLabel;
	private String predictedLabel;

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
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

}
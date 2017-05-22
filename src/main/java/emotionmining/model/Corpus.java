package emotionmining.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author AysoltanGravina
 * 
 *         This class reads the corpus data and extracts the values necessary
 *         for the evaluation.
 *
 */
public class Corpus {

	// Attributes
	List<Tweet> tweetsList; // necessary for the iteration through all tweets
							// during the evaluation calculation.
	private String goldFileName;
	private String predictedFileName;

	// Getters Setters
	public List<Tweet> getTweetsList() {
		return tweetsList;
	}

	public void setTweetsList(List<Tweet> tweetsList) {
		this.tweetsList = tweetsList;
	}

	public String getGoldFileName() {
		return goldFileName;
	}

	public void setGoldFileName(String goldFileName) {
		this.goldFileName = goldFileName;
	}

	public String getPredictedFileName() {
		return predictedFileName;
	}

	public void setPredictedFileName(String predictedFileName) {
		this.predictedFileName = predictedFileName;
	}

	/*
	 * Object method of Corpus which extracts the data necessary for evaluation.
	 * It reads from the given files and extracts tweet itself as a string,
	 * their gold and predicted labels.
	 */
	public void getEvaluationData() {

		BufferedReader brGold;
		BufferedReader brPredicted;

		List<Tweet> tweetsList = new ArrayList<Tweet>();
		String line;
		try {
			// Open buffered reader.
			brGold = new BufferedReader(new FileReader(getGoldFileName()));
			brPredicted = new BufferedReader(new FileReader(getPredictedFileName()));

			while ((line = brGold.readLine()) != null) {
				Tweet tweet = new Tweet();
				String elem[] = line.split("\t");
				// Extract and set raw tweet into Tweet data structure
				if (elem.length < 9) {
					// if tweet is empty
					tweet.setTweet("");
				} else {
					tweet.setTweet(elem[8]);
				}
				// Set the gold label into Tweet data structure.
				tweet.setGoldLabel(elem[0]);
				// Set the predicted label into Tweet data structure.
				tweet.setPredictedLabel(brPredicted.readLine());
				// Add all tweets with Labels into List, in order to be abele to
				// store it into attribute variable.
				tweetsList.add(tweet);
			}
			// Finally set the tweets list object into attribute variable.
			setTweetsList(tweetsList);

			// Close buffered reader.
			brGold.close();
			brPredicted.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

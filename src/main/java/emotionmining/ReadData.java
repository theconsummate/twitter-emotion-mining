package emotionmining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReadData {

	/**
	 * Reads tweets from file and store in List<TweetVO>
	 * 
	 * @param corpus
	 * @return CorpusVO object with List<TweetVO>
	 */
	public Corpus getDataValues(Corpus corpus) {

		List<Tweet> listTweetDO = new ArrayList<Tweet>();
		BufferedReader br;

		String line;
		try {
			br = new BufferedReader(new FileReader(corpus.getFileName()));
			int i = 0;
			while ((line = br.readLine()) != null) {
				Tweet tweetDO = new Tweet();
				String elem[] = line.split("	", 6);
				tweetDO.setGoldLabel(elem[0]);
				// tokenize and setTweet

				listTweetDO.add(tweetDO);
				i++;

			}
			corpus.setListTweetVO(listTweetDO);
			System.out.println("total :" + i);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return corpus;

	}

}

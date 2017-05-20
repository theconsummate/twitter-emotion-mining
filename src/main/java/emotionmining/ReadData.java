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
		BufferedReader br, br2;

		String line;
		try {
			br = new BufferedReader(new FileReader(corpus.getFileName()));
			br2 = new BufferedReader(new FileReader(corpus.getPredictedFileName()));
			int i = 0;

			while ((line = br.readLine()) != null) {
				Tweet tweetDO = new Tweet();
				String elem[] = line.split("\t");
				if(elem.length < 9){
					// tweet is empty
					tweetDO.setTweet("");
				} else{
//					System.out.println(elem[8]);
					tweetDO.setTweet(elem[8]);
				}
//				System.out.println(elem.length);
				tweetDO.setGoldLabel(elem[0]);
				
				
				// tokenize and setTweet
				listTweetDO.add(tweetDO);
				i++;
				
				tweetDO.setPredictedLabel(br2.readLine());

				//if(i > 10) break;
			}
			br.close();
			br2.close();
			corpus.setListTweetVO(listTweetDO);
			// System.out.println("total :" + i);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return corpus;

	}

}

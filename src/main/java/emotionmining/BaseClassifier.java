package emotionmining;

import emotionmining.model.Tweet;

import java.util.List;

/**
 * Created by dhruv on 20/05/17.
 */
public interface BaseClassifier {
    public void train();
    public void test();
    public List<Tweet> getTweetWithFeatures();
}

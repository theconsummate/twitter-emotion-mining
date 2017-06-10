package emotionmining;

import emotionmining.model.Tweet;
import emotionmining.model.TweetFeatures;

import java.util.List;

/**
 * Created by dhruv on 22/05/17.
 */
public abstract class BaseClassifierImplementation implements BaseClassifier{
    public abstract void train() ;

    public abstract void test();

    public List<Tweet> getTweetWithFeatures(){
//        assumed that TweetFeature instance of Tweet class is not null here
//        TODO add actual implementation here.
        return null;
    }
}

package emotionmining.model;


public class TweetFeatures {
//    TODO
	    public String[] getFeaturesList(){
//        This is the header row of our csv file.
        String[] s = {"hasAdjective", "hadWordX"};
        return s;
        

    }
//    TODO
    public void extractFeatures(){

    }

//    Write Features to a csv file for storage using the headers above
    public void exportToCSVFile(){
    String filename = "data/extracted_features.csv";
//        TODO add implementation
    }
}

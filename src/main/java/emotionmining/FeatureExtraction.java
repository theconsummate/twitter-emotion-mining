package emotionmining;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.*;
import emotionmining.model.*;
import emotionmining.naivebayes.TextTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.*;
import java.util.*;

/**
 * Created by dhruv on 27/05/17.
 */
public class FeatureExtraction {

    private static List<String> negationDictionary;
    private static Map<String, List<String>> nrcMap;

    public static FeatureStats extractFeatureStats(List<Document> dataset) {
        FeatureStats stats = new FeatureStats();

        Integer categoryCount;
        String category;
        Integer featureCategoryCount;
        String feature;
        Map<String, Integer> featureCategoryCounts;
        for(Document doc : dataset) {
            ++stats.n; //increase the number of observations
            category = doc.category;


            //increase the category counter by one
            categoryCount = stats.categoryCounts.get(category);
            if(categoryCount==null) {
                stats.categoryCounts.put(category, 1);
            }
            else {
                stats.categoryCounts.put(category, categoryCount+1);
            }

            for(Map.Entry<String, Integer> entry : doc.tokens.entrySet()) {
                feature = entry.getKey();

                //get the counts of the feature in the categories
                featureCategoryCounts = stats.featureCategoryJointCount.get(feature);
                if(featureCategoryCounts==null) {
                    //initialize it if it does not exist
                    stats.featureCategoryJointCount.put(feature, new HashMap<String, Integer>());
                }

                featureCategoryCount=stats.featureCategoryJointCount.get(feature).get(category);
                if(featureCategoryCount==null) {
                    featureCategoryCount=0;
                }

                //increase the number of occurrences of the feature in the category
                stats.featureCategoryJointCount.get(feature).put(category, ++featureCategoryCount);
            }
        }

        return stats;
    }


    /**
     * Performs POS Tagging and Stemming using the Stanford Lexical Parser.
     * @param tweetsList
     * @return
     * */
    public static List<Tweet> posTaggingAndStemming(List<Tweet> tweetsList, String filename) throws IOException {

        LexicalizedParser lp = LexicalizedParser.loadModel("data/englishPCFG.ser.gz"); // Create new parser
        //lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"}); // set max sentence length if you want

        // Call parser on files, and tokenize the contents
        StringReader sr; // we need to re-read each line into its own reader because the tokenizer is over-complicated garbage
        PTBTokenizer tkzr; // tokenizer object
        WordStemmer ls = new WordStemmer(); // stemmer/lemmatizer object

        // Read File Line By Line
        FileWriter writer = new FileWriter(filename);
        for(Tweet tweet: tweetsList) {
//            System.out.println ("Processing: "+tweet.getTweet()); // print current line to console
            String text = preprocess(tweet.getTweet());
//            System.out.println ("Tokenizing and Parsing: "+text); // print current line to console

            // do all the standard java over-complication to use the stanford parser tokenizer
            sr = new StringReader(text);
            tkzr = PTBTokenizer.newPTBTokenizer(sr);
            List toks = tkzr.tokenize();
//            System.out.println ("tokens: "+toks);

            Tree parse = (Tree) lp.apply(toks); // finally, we actually get to parse something

            // Output Option 1: Printing out various data by accessing it programmatically

            // Get words, stemmed words and POS tags

            ArrayList<String> stems = new ArrayList();
            /*ArrayList<String> words = new ArrayList();
            ArrayList<String> tags = new ArrayList();

            // Get words and Tags
            for (TaggedWord tw : parse.taggedYield()){
                words.add(tw.word());
                tags.add(tw.tag());
            }*/

            // Get stems
            ls.visitTree(parse); // apply the stemmer to the tree
            for (TaggedWord tw : parse.taggedYield()){
                stems.add(tw.word());
            }

            // Get dependency tree
            /*TreebankLanguagePack tlp = new PennTreebankLanguagePack();
            GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
            GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
            Collection tdl = gs.typedDependenciesCollapsed();
*/
            // And print!
            /*System.out.println("words: "+words);
            System.out.println("POStags: "+tags);*/
//            System.out.println("stemmedWordsAndTags: "+stems);
//            System.out.println("typedDependencies: "+tdl);

            // Output Option 2: Printing out various data using TreePrint

            // Various TreePrint options
            //	    "penn", // constituency parse
            //	    "oneline",
            //	    rootLabelOnlyFormat,
            //	    "words",
            //	    "wordsAndTags", // unstemmed words and pos tags
            //	    "dependencies", // unlabeled dependency parse
            //	    "typedDependencies", // dependency parse
            //	    "typedDependenciesCollapsed",
            //	    "latexTree",
            //	    "collocations",
            //	    "semanticGraph"

            // Print using TreePrint with various options
            //TreePrint tp = new TreePrint("wordsAndTags,typedDependencies");
            //tp.printTree(parse);


            HashMap<String,Double> frequencymap = new HashMap<String,Double>();
            double sum = 0;
            String str = "";
            for(String a : stems) {
                str += a + ",";
                if(frequencymap.containsKey(a)) {
                    frequencymap.put(a, frequencymap.get(a)+ (1/stems.size()) );
                    sum += frequencymap.get(a);
                }
                else{ frequencymap.put(a, (double) 1/stems.size()); sum += frequencymap.get(a);}
            }

//            System.out.println("frequency map: "+frequencymap);
//            System.out.println("frequency sum: "+sum);

//            System.out.println(); // separate output lines
//            Save to file
            if (str != null && str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            writer.append(tweet.getTweet() + ":::::::" + tweet.getGoldLabel() + ":::::::" + str + "\n");
            tweet.setFeatures(frequencymap);
        }
        writer.flush();
        writer.close();
        return tweetsList;
    }

    /**
     * Performs POS Tagging and Stemming using the Snowball stemmer.
     *
     * @param tweetsList
     * @return
     */
    public static List<Tweet> snowballStemmer(List<Tweet> tweetsList, String filename, boolean readFromFile) throws IOException {
        nrcMap = Corpus.getNrcDict();
        negationDictionary = Corpus.getNegationDict();
        englishStemmer englishStemmer = new englishStemmer();
        FileWriter writer  = null;
        BufferedReader reader = null;

        if(readFromFile){
//            don't read from file, write new
            reader = new BufferedReader(new FileReader(filename));
            reader.read();
        }
        else {
            writer = new FileWriter(filename);
        }
        for (Tweet tweet : tweetsList) {
            List<String> stems = new ArrayList();
            String processedTweet = preprocess(tweet.getTweet());

            if(reader != null){
//                read from file
                String line = reader.readLine();
                if(line != null){
                    stems = Arrays.asList(line.split(":::::::")[2].split(","));
                }
            }
            else {
                for (String word : processedTweet.split(" ")) {
                    englishStemmer.setCurrent(word.trim());
                    if (englishStemmer.stem()) {
//                System.out.println("word:" + word);
                        stems.add(englishStemmer.getCurrent());
                    }
                }
            }

            Map<String,Double> featureVector = new HashMap<String,Double>();
            double sum = 0;
            String str = "";
            for(String a : stems) {
                str += a + ",";
                if(featureVector.containsKey(a)) {
                    featureVector.put(a, featureVector.get(a)+ (1/stems.size()) );
                    sum += featureVector.get(a);
                }
                else{ featureVector.put(a, (double) 1/stems.size()); sum += featureVector.get(a);}
                /*Other Features*/
                featureVector = nrcEmotionFeatures(a, featureVector);
            }
            featureVector = negationFeatures(processedTweet, featureVector);

//            System.out.println("frequency map: "+frequencymap);
//            System.out.println("frequency sum: "+sum);

//            System.out.println(); // separate output lines
//            Save to file
            if (str != null && str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            if(writer != null) {
                writer.append(tweet.getTweet() + ":::::::" + tweet.getGoldLabel() + ":::::::" + str + "\n");
            }
            tweet.setFeatures(featureVector);


        }
        if(writer != null) {
            writer.flush();
            writer.close();
        }
        if(reader != null){
            reader.close();
        }
        return tweetsList;
    }

    /**
     * Performs POS Tagging and Stemming using the Snowball stemmer.
     *
     * @param tweetsList
     * @return
     */
    public static List<Tweet> twokenizeLib(List<Tweet> tweetsList) {
        for (Tweet tweet: tweetsList) {
            // Tokenize a Tweet
            tweet.tokenize(tweet.getTweet());

            // Put each Token of Tweet into Feature Vector with value 1.0
            Map<String, Double> featureVector = new HashMap<String, Double>();
            // System.out.println("Tweet Tokens:");
            for (Token token : tweet.getTokensList()) {
                // System.out.println("\t" + token.getToken());
                featureVector.put(token.toString(), 1.0);
                tweet.setFeatures(featureVector);
            }

        }

        return tweetsList;
    }

    private static Map<String, Double> nrcEmotionFeatures(String stem, Map<String, Double> featureVector){
        if (nrcMap.containsKey(stem)) {
            List<String> emotions = nrcMap.get(stem);
            for (String emotion : emotions) {
                String featureNameStr = "nrc-" + emotion.trim();

                if (featureVector.containsKey(featureNameStr))
                    featureVector.put(featureNameStr, featureVector.get(featureNameStr) + 1.0);
                else
                    featureVector.put(featureNameStr, 1.0);
            }
        }
        return featureVector;

    }

    /**
     * @param tweet
     * @param featureVector
     * @throws IOException
     */
    public static Map<String, Double> negationFeatures(String tweet, Map<String, Double> featureVector) throws IOException {

        for (String negation : negationDictionary) {
            if (tweet.contains(negation)) {
                featureVector.put("negation", -1.0);
            }
        }
        return featureVector;
    }

    /**
     * Preprocess the text by removing punctuation, duplicate spaces and
     * lowercasing it.
     *
     * @param text
     * @return
     */
    private static String preprocess(String text) {
        return text.replaceAll("(http|ftp|https)://[^\\s]+", "").
                replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault()).trim();
    }

    public static List<Document> preprocessNaiveBayes(Map<String, String[]> data) {
        List<Document> dataset = new ArrayList<Document>();
        String category;
        String[] examples;
        Document doc;
        Iterator<Map.Entry<String, String[]>> it = data.entrySet().iterator();
//loop through all the categories and training examples
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            category = entry.getKey();
            examples = entry.getValue();
            for (int i = 0; i < examples.length; ++i) {
//for each example in the category tokenize its text and convert it into a Document object.
                doc = TextTokenizer.tokenize(examples[i]);
                doc.category = category;
                dataset.add(doc);
//examples[i] = null; //try freeing some memory
            }
//it.remove(); //try freeing some memory
        }
        return dataset;

    }
}

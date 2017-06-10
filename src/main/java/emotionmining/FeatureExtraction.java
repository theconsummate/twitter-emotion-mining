package emotionmining;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.*;
import emotionmining.model.Document;
import emotionmining.model.FeatureStats;
import emotionmining.model.Tweet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by dhruv on 27/05/17.
 */
public class FeatureExtraction {
    /**
     * Generates a FeatureStats Object with metrics about he occurrences of the
     * keywords in categories, the number of category counts and the total number
     * of observations. These stats are used by the feature selection algorithm.
     *
     * @param dataset
     * @return
     */
    public FeatureStats extractFeatureStats(List<Document> dataset) {
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
    public static List<Tweet> posTaggingAndStemming(List<Tweet> tweetsList) throws IOException {

        LexicalizedParser lp = LexicalizedParser.loadModel("data/englishPCFG.ser.gz"); // Create new parser
        //lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"}); // set max sentence length if you want

        // Call parser on files, and tokenize the contents
        StringReader sr; // we need to re-read each line into its own reader because the tokenizer is over-complicated garbage
        PTBTokenizer tkzr; // tokenizer object
        WordStemmer ls = new WordStemmer(); // stemmer/lemmatizer object

        // Read File Line By Line
        String strLine;
        FileWriter writer = new FileWriter("data/stems.csv");
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
     * Preprocess the text by removing punctuation, duplicate spaces and
     * lowercasing it.
     *
     * @param text
     * @return
     */
    private static String preprocess(String text) {
        return text.replaceAll("(http|ftp|https)://[^\\s]+", "").
                replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());
    }
}

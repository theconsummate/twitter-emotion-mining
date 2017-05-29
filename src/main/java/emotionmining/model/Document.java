package emotionmining.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhruv on 27/05/17.
 */
public class Document {

    /**
     * List of token counts
     */
    public Map<String, Integer> tokens;

    /**
     * The class of the document
     */
    public String category;

    /**
     * Document constructor
     */
    public Document() {
        tokens = new HashMap<String, Integer>();
    }
}

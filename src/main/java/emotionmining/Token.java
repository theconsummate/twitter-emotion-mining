package emotionmining;

//For Tokens: add normalizing
public class Token {

	// attribute
	private String text;

	// constructor
	public Token(String text) {
		// add normalizing
		this.text = text;
	}
	
	// getter
	public String getText() {
		return text;
	}
	// setter
	public void setText(String text) {
		this.text = text;
	}

}

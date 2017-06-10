package emotionmining.model;

/**
* 
* @author AysoltanGravina
* 
*         This class sets the each token of tweet as a string. It has the
*         object method normalizeToken() which trims the white spaces at the
*         begin and the end of the token and converts it into upper case.
*
*/
public class Token {

	// Attributes
	private String token;

	// Getter and Setter
	public String getToken() {
		return token;
	}

	// edit normalizing
	public void setToken(String token) {
		this.token = normalizeToken(token);
	}

	// Object method for normalizing the Token
	// add necessary normalizations
	public String normalizeToken(String token) {
		return token.trim().toUpperCase();
	}

}

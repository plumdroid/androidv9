package fr.unilim.javance.projet.view.authentication;

/**
 * This listener is useful for the WebView in order to 
 * send back the result of the authentication
 * 
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public interface AuthResults {
	
	/**
	 * This function is called when the user has refused 
	 * the authentication
	 */
    public void authenticationRefused();
	
	/**
	 * This function is called when the user has agreed 
	 * the authentication
	 * 
	 * @param code the result code given by google
	 */
    public void authenticationSuccessful(String code);
}

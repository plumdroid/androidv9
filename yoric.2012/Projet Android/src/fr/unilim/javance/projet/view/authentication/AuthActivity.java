package fr.unilim.javance.projet.view.authentication;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;
import fr.unilim.javance.projet.internal.Backend;
import fr.unilim.javance.projet.internal.TodoManagerFactory;
import fr.unilim.javance.projet.model.gtasks.GTasksTodoManager;
import fr.unilim.javance.projet.model.gtasks.cache.GTasksSQLite;

import android.app.Activity;
import android.os.Bundle;


public class AuthActivity extends Activity implements AuthResults {
	private GoogleAccountHandler gaHandler;
	        
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.gaHandler = new GoogleAccountHandler(this);
	}

	/**
	 * This function is called when the user has refused  the authentication.
	 * The GTasks manager is warned that the authentication failed
	 */
	public void authenticationRefused() {
		GTasksTodoManager gtasksManager = (GTasksTodoManager) TodoManagerFactory.getTodoManager(Backend.GTASKS);
		gtasksManager.setAuthenticationStatus(false);
		finish();
	}

	/**
	 * This function, called when the user has agreed the authentication, try
	 * to get the authentication token and other information. After that, the
	 * GTasks manager is warned that the authentication was successful.
	 * 
	 * @param code the result code given by google in the WebView
	 */
	public void authenticationSuccessful(String code) {		
		GTasksTodoManager gtasksManager = (GTasksTodoManager) TodoManagerFactory.getTodoManager(Backend.GTASKS);
		String tokenInfos[] = this.gaHandler.getTokenInfos();
		gtasksManager.setInfosToken(tokenInfos[0], tokenInfos[1]);
		gtasksManager.setAuthenticationStatus(true);
		finish();
	}
}

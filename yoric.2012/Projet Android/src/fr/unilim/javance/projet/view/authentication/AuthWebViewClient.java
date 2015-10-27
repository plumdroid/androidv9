package fr.unilim.javance.projet.view.authentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A class that extends WebViewClient in order to
 * be able to know if the user allows the authentication
 * or not. The observer is notified when these events are
 * known.
 * 
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class AuthWebViewClient extends WebViewClient {

	private AuthResults observer;

	public AuthWebViewClient(AuthResults o) {
		super();
		
		this.observer = o;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if (url.startsWith("https://accounts.google.com/o/oauth2/approval")) {
			String pageTitle = view.getTitle();
			
			Pattern accessRefusedPattern = Pattern.compile("access_denied");
			Pattern accessGivenPattern   = Pattern.compile("code=");
			
			Matcher mRefused = accessRefusedPattern.matcher(pageTitle);
			Matcher mOk      = accessGivenPattern.matcher(pageTitle);
			
			if(mRefused.find()) {
				// Le client refuse la jointure avec google
				observer.authenticationRefused();
			}
			else if(mOk.find()) {
				// Le client donne son accord, on récupère le code
				String[] tab = pageTitle.split("=");
				                            
				if(tab.length == 2) {
					observer.authenticationSuccessful(tab[1]);
				}
			}
		}
	}
}

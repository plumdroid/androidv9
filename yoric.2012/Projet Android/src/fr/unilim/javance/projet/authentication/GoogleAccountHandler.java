package fr.unilim.javance.projet.authentication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;

import fr.unilim.javance.projet.httpconnection.HttpConnection;
import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.model.gtasks.RemoteManager;
import fr.unilim.javance.projet.sync.SyncAdapter;
import fr.unilim.javance.projet.view.authentication.AuthResults;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * A class used to manage a Google account
 * 
 * This class is instantiated when an account
 * is needed by the application. It's responsible
 * off all the management of the account 
 * (getting tokens and creating it).
 * 
 * @author Thibault Desmoulins
 * @author Yorick Lesecque
 * 
 * @see AccountManager
 * @see RemoteManager
 * @see AccountManagerCallback
 */
public class GoogleAccountHandler {
    private static final String TAG = "GoogleAccountHandler";
	private final String AUTH_TOKEN_TYPE = "Manage your tasks";
	private final int REQUEST_AUTHENTICATE = 0;
	
    public static final String ACCOUNT_TYPE = "com.google";
    
	private Object observer;
	private Activity activity;
	private Context context;
	private String token;
	private AccountManager manager;
	private boolean sync;
	

	/**
     * Constructor called by the <code>AuthActivity</code>
	 * 
	 * Called when the <code>AuthActivity</code> needs an
	 * account manager.
	 * 
	 * @param activity	The activity that needs the manager
	 * 
	 * @see AuthActivity
	 */
	public GoogleAccountHandler(Activity activity) {
		Log.d(TAG, "Managing Google account from Activity");
		
		this.activity = activity;
		this.manager = AccountManager.get(this.activity);
		this.sync = false;
		
		this.observer = (AuthResults) activity;
		
		if(!this.haveAccount()) {
			this.addAccount();
		} else {
			this.gotAccount(true);
		}
	}

	/**
     * Constructor called by the <code>SyncAdapter</code>
	 * 
	 * Called when the <code>SyncAdapter</code> needs an
	 * account manager.
	 * 
	 * @param context	The context that needs the manager
	 * 
	 * @see SyncAdapter
	 */
	public GoogleAccountHandler(Context context) {
		Log.d(TAG, "Managing Google account from Context");
		
		this.context = context;
		this.manager = AccountManager.get(this.context);
		this.sync = true;
	}
	
	/**
	 * Function used to use the <code>RemoteManager</code>
	 * as an observer of this class.
	 * 
	 * This observer is used to simplify callbacks operations
	 * when a thread is running.
	 * 
	 * @param rManager	The <code>RemoteManager</code> used as
	 * 					observer
	 */
	public void setRemoteManager(RemoteManager rManager) {
		this.observer = rManager;
	}
	
	/**
	 * Function used to look if a "com.google" account exists.
	 * 
	 * @return Returns true if an account already exists false otherwise
	 */
	private boolean haveAccount() {
	    Account account[] = this.manager.getAccountsByType(this.ACCOUNT_TYPE);
	    
	    if(account.length > 0) return true;
	    else return false;
	}
	
	/**
	 * Function used to create a "com.google" account.
	 * 
	 * This function is called at the first start of the
	 * application if there are no Google accounts registered
	 * on the phone.
	 * 
	 * This function uses the <code>addAccount(String accountType, 
	 * String authTokenType, String[] requiredFeatures, Bundle addAccountOptions, 
	 * Activity activity, AccountManagerCallback<Bundle> callback, Handler handler)</code>
	 * method of the <code>AccountManager</code> class. The creation is
	 * done in a thread launched by the system and needs a callback
	 * to return its results. Calling this function results in popping an
	 * activity handled by Android. This activity is dedicated in the creation
	 * of a Google account.
	 */
	private void addAccount() {
		this.manager.addAccount(this.ACCOUNT_TYPE, this.AUTH_TOKEN_TYPE, 
				null, null, this.activity, new CreateAccountCallback(), null);
	}
	
	/**
	 * Function used to get token infos
	 * 
	 * @return 	An array of string containing, respectively, 
	 * 			the token and its type
	 */
	public String[] getTokenInfos() {
		return new String[]{this.token, this.AUTH_TOKEN_TYPE};
	}
	
	/**
	 * Function used to get a token for a specified account.
	 * 
	 * This function is called each time a new token is needed
	 * by the application. 
	 * 
	 * This function uses <code>getAuthToken(Account account, 
	 * String authTokenType, boolean notifyAuthFailure, 
	 * AccountManagerCallback<Bundle> callback, Handler handler)</code>
	 * from the <code>AccountManager</code> class. As the function
	 * needs to be launched in a thread, one is created before its
	 * use. Then a callback is used to obtain the token.
	 * 
	 * @param account	The account for which we need to obtain a token
	 */
	private void gotAccount(final Account account) {
        Log.v(TAG, "gotAccout (account)");
        Log.v(TAG, "HandleToken");
        
        new Thread(new Runnable() {
			@Override
			public void run() {
		        Log.d(TAG, "Running in background thread");

		        Bundle bundle;
				try {
					bundle = manager.getAuthToken(account, AUTH_TOKEN_TYPE, true, 
							new GetAuthTokenCallback(), null).getResult();
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }).start();
	}
	
	/**
	 * Function used to invalidate the token.
	 * 
	 * This function is called whenever a request to the remote
	 * server is not authorized because of an expired token. 
	 * 
	 * As the token is cached by Android, sometimes, it expires
	 * so it needs to be invalidated (delete from the cache) thanks
	 * to the <code>invalidateAuthToken(String accountType, 
	 * String authToken)</code> from the <code>AccountManager</code>
	 * class. Then the  <code>gotAccount(final Account account)</code>
	 * function is used to require a new token.
	 * 
	 * @param tokenExpired	A boolean that tells if the token has expired or not
	 */
	public void gotAccount(boolean tokenExpired) {
        Log.v(TAG, "gotAccout (boolean)");
        
	    Account account = this.manager.getAccountsByType(ACCOUNT_TYPE)[0];
	    
	    if (tokenExpired) {
	        Log.v(TAG, "Token expired");
	        
	        this.manager.invalidateAuthToken(ACCOUNT_TYPE, this.token);
	    }
	    
	    this.gotAccount(account);
	}
	
	/**
	 * A private class implementing <code>AccountManagerCallback</code>
	 * 
	 * This class is used as a callback for the <code>createAccount</code>
	 * function. Then, it calls the <code>gotAccount(boolean)</code> function
	 * to set up a new token for the newly created account.
	 */
	private class CreateAccountCallback implements AccountManagerCallback {
		@Override
		public void run(AccountManagerFuture future) {
			try {
				Bundle bundle = (Bundle) future.getResult();
				Log.e(TAG, "CreateAccountCallback");
				Log.e(TAG, "Account created: " + bundle.getString(AccountManager.KEY_ACCOUNT_NAME));

				gotAccount(true);
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * A private class implementing <code>AccountManagerCallback</code>
	 * 
	 * This class is used as a callback for the <code>getAuthToken</code>
	 * function. Using the bundle given by <code>getAuthToken</code>, 
	 * the class is able to call the <code>authenticationSuccessful</code>
	 * function of the right observer to notify the good obtaining of the token. 
	 */
	private class GetAuthTokenCallback implements AccountManagerCallback {
		@Override
		public void run(AccountManagerFuture future) {
			try {
				Bundle bundle = (Bundle) future.getResult();
				Log.e(TAG, "GetAuthTokenCallback");

			    if (bundle.containsKey(AccountManager.KEY_INTENT)) {
			        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
			        int flags = intent.getFlags();
			        flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
			        intent.setFlags(flags);
			        activity.startActivityForResult(intent, REQUEST_AUTHENTICATE);
			    } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
			        Log.v(TAG, bundle.getString(AccountManager.KEY_AUTHTOKEN));
			        
			        token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			    }
		    	
			    if(!sync) {
			    	((AuthResults) observer).authenticationSuccessful(token);
			    } else {
			    	((RemoteManager) observer).authenticationSuccessful();
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}

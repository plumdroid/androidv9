package fr.unilim.javance.projet.sync;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.auth.AuthenticationException;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;
import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TaskStatus;
import fr.unilim.javance.projet.internal.TodoListener;
import fr.unilim.javance.projet.internal.TodoManager;
import fr.unilim.javance.projet.model.gtasks.RemoteManager;
import fr.unilim.javance.projet.model.gtasks.cache.GTasksSQLite;
import fr.unilim.javance.projet.model.mock.cache.MockFolderCache;
import fr.unilim.javance.projet.model.mock.cache.MockTaskCache;
import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/**
 * An <code>AbstractThreadedSyncAdapter</code> implementation
 * 
 * This class is called directly by Android system when
 * a sync is performed or needed. 
 * 
 * It's initialized at the first use of the application 
 * and called whenever it's needed. This works directly 
 * with a content provider (<code>GTasksContentProvider</code>)
 * and uses the <code>GoogleAccountHandler</code> class
 * to handle the account on which we have to perform a sync.
 * 
 * @author Thibault Desmoulins
 * @author Yorick Lesecque
 * 
 * @see AbstractThreadedSyncAdapter
 * @see GoogleAccountHandler
 * @see RemoteManager
 * @see GTasksContentProvider
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";
    private final boolean debug = true;

    private final GoogleAccountHandler gaHandler;
    private ContentProviderClient cpClient;
    private RemoteManager rManager;
	private SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

    /**
     * The default constructor
     * 
     * This function is called by the system at the first
     * use of the application or at the initialization
     * of the phone to register the sync adapter.
     * 
     * It instantiates a <code>GoogleAccountHandler</code>
     * to handle the account which we have to sync.
     * 
     * @param context 			The context of the application
     * @param autoInitialize 	A boolean declared in the
     * 							<code>syncadapter.xml</code>
     * 							(default to true)
     * 
     * @see <a href="">Sync-adapter</a>
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        
		this.gaHandler = new GoogleAccountHandler(context);
    }

    /**
     * Overrides <code>onPerformSync</code> from 
     * <code>AbstractThreadedSyncAdapter</code>.
     * 
     * This method is called by Android system whenever it's needed:
     * <li>Sync is performed</li>
     * <li><code>onStatusChanged()</code> is fired</li>
     * <li>Internet connection was activated</li>
     * <li>...</li>
     * 
     * @param account		The account for which the sync is performed
     * @param extras		A bundle in which we can store information
     * @param authority		A string containing the <code>syncadapter</code> authority
     * @param provider		The <code>ContentProviderClient</code> used to manage
     * 						the DB of the application
     * @param syncResultat	A helper to handle the current and futures sync
     * 
     * @see ContentProvider
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
    	
    		Log.d(TAG, "Sync is being performed");

			this.rManager = new RemoteManager(this);
			this.cpClient = provider;
			
    		try {
    			this.gaHandler.setRemoteManager(this.rManager);
    			
    			Log.d(TAG, "Updating remote");
    			this.rManager.updateRemote();
    		} catch(Exception e) {
    			this.handleException(e, syncResult);
    		}
    		
    }
    
    /**
     * Function used to perform the diff between the remote DB
     * and the local one. 
     * 
     * Called when all <code>Folder</code>s  and <code>Task</code>s
     * have been caught from the remote server.
     * 
     * @param folders			An <code>ArrayList</code> 
     * 							of <code>Folder</code>s containing all
     * 							folders of the remote server.
     * @throws RemoteException	Exception thrown by the use of the
     * 							<code>ContentProviderClient</code>
     */
	private void performDiff(ArrayList<Folder> folders)
			throws RemoteException {
		
		Log.d(TAG, "Diff is being performed");
		
		String[] projectionF = new String[] { GTasksSQLite.FOLDER_ID };
		

		Log.d(TAG, "for each folder");
		for(int i = 0; i < folders.size(); i++) {
			Folder f = folders.get(i);
			
    		Log.d(TAG, "Querying the same folder from the DB");
    		if(debug) Log.d(TAG, "Folder - id: " + f.getId());
    		
			Cursor c = this.cpClient.query(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI,
                    String.valueOf(f.getId())), projectionF, null, null, null);
			
			if(c.getCount() > 0) {
				c.moveToFirst();
				
				Log.d(TAG, "Folder found - id: " + c.getString(c.getColumnIndex(GTasksSQLite.FOLDER_ID)));
				
				for(Task t : f.getTasks()) {
					c = this.cpClient.query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
		                    String.valueOf(t.getId())), null, null, null, null);
	        		if(debug) Log.d(TAG, "Query getCount: " + c.getCount());
					
					if(c.getCount() > 0) {
						c.moveToFirst();
		        		Log.d(TAG, "Task found - id: " + c.getString(c.getColumnIndex("ID")));
		        		Log.d(TAG, "Task found - creation date (local): " + c.getString(c.getColumnIndex("CREATION_DATE")));
		        		Log.d(TAG, "Task found - creation date (remote): " + format.format(t.getCreationDate()));
						
						try {
							Date localDate = format.parse(c.getString(c.getColumnIndex("CREATION_DATE")));
							Date remoteDate = t.getCreationDate();
							
							if(remoteDate.compareTo(localDate) > 0) {
								this.cpClient.update(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
					                    String.valueOf(t.getId())), t.makeContentValues(), null, null);
							}
						} catch (ParseException e) { }
					} else {
		        		Log.d(TAG, "Task not found - id: " + t.getId());
		        		
		        		Log.d(TAG, "Inserting Task");
		        		this.cpClient.insert(GTasksContentProvider.TASK_CONTENT_URI, t.makeContentValues());
						
						if(debug) {
							c = this.cpClient.query(GTasksContentProvider.TASK_CONTENT_URI, null, null, null, null);
							
			        		Log.d(TAG, "Query getCount: " + c.getCount());
							c.moveToFirst();
							do {
				        		Log.d(TAG, "Task id: " + c.getString(c.getColumnIndex(GTasksSQLite.TASK_ID)));
				        		Log.d(TAG, "Task CREATION_DATE: " + c.getString(c.getColumnIndex("CREATION_DATE")));
							} while (c.moveToNext());
						}
					}
				}
			} else {
        		Log.d(TAG, "Folder not found - name: " + f.getName());

        		Log.d(TAG, "Inserting Folder");
        		this.cpClient.insert(GTasksContentProvider.FOLDER_CONTENT_URI, f.makeContentValues());
        		i--;
			}
		}
	}

	/**
	 * A simple function to handle exceptions that can
	 * be thrown during the sync.
	 * 
	 * @param e				The exception thrown
	 * @param syncResult	The helper for sync
	 */
	private void handleException(Exception e, SyncResult syncResult) {
		if (e instanceof AuthenticatorException) {
			syncResult.stats.numParseExceptions++;
			Log.e(TAG, "AuthenticatorException", e);
		} else if (e instanceof OperationCanceledException) {
			Log.e(TAG, "OperationCanceledExcepion", e);
		} else if (e instanceof IOException) {
			Log.e(TAG, "IOException", e);
			syncResult.stats.numIoExceptions++;
		} else if (e instanceof AuthenticationException) {
			gaHandler.gotAccount(true);
			syncResult.stats.numIoExceptions++;
			Log.e(TAG, "AuthenticationException", e);
		} else if (e instanceof RemoteException) {
			Log.e(TAG, "RemoteException", e);
		}
	}

	/**
	 * Get the account handler
	 * 
	 * @return The account handler
	 */
	public GoogleAccountHandler getGAHandler() {
		return this.gaHandler;
	}
	
	/**
	 * Get the <code>ContentProvider</code> to handle queries to the local DB
	 * 
	 * @return	The <code>ContentProvider</code> handler
	 */
	public ContentProviderClient getContentProviderClient() {
		return this.cpClient;
	}

	/**
	 * Get the token stored in the cache
	 * 
	 * @return The token stored
	 */
	public String getToken() {
		return this.gaHandler.getTokenInfos()[0];
	}

	/**
	 * Function used to launch the update process
	 * 
	 * This function is fired by the <code>RemoteManager</code>
	 * when all <code>Folder</code>s where caught from
	 * the remote server.
	 * 
	 * @param allFolders		All folders contained in the remote DB
     * @throws RemoteException	Exception thrown by the use of the
     * 							<code>ContentProviderClient</code>
	 */
	public void onFoldersReceived(ArrayList<Folder> allFolders) 
			throws Exception {
		
		Log.d(TAG, "Performing diff");
		this.performDiff(allFolders);
	}
	
	public void onFoldersUpdated() 
			throws Exception {
		
		Log.d(TAG, "Getting all folders ans tasks");
		this.rManager.getAll();
	}
}

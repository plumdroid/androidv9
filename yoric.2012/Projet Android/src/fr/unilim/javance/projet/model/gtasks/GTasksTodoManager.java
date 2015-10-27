package fr.unilim.javance.projet.model.gtasks;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

import fr.unilim.javance.projet.httpconnection.HttpConnection;
import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Tag;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TodoListener;
import fr.unilim.javance.projet.internal.TodoManager;
import fr.unilim.javance.projet.model.gtasks.cache.GTasksSQLite;
import fr.unilim.javance.projet.model.mock.cache.MockFolderCache;
import fr.unilim.javance.projet.sync.GTasksContentProvider;
import fr.unilim.javance.projet.view.authentication.AuthActivity;

/**
 * A <code>Google Tasks</code> implementation
 * 
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class GTasksTodoManager extends ContentObserver implements TodoManager {
	private ArrayList<TodoListener> listeners;
	private Boolean authenticationStatus = false;
	private static final String TAG = "GTasksTodoManager";
	private ContentResolver cResolver;

	//private String api_key       = "AIzaSyBhilJssD4uPFCmYyWGi4k3loXvdbQocek"; // Yorick
	private String api_key       = "AIzaSyCoelG8Wjt0tuuwVrUG9ATIkyQiIWAkQzc"; // Thibault
	
	private String token         = "";
	private String token_type    = "";
	private String expires_in    = "";
	private String refresh_token = "";
	
	private Set<Folder> folders = new HashSet<Folder>();
	private Set<Task> tasks = new HashSet<Task>();
	
	//private SQLiteDatabase bdd;
	
	/**
	 * Default constructor
	 * 
	 * Initialize the listeners list.
	 */
	public GTasksTodoManager() {
        super(new Handler());
        
		this.listeners = new ArrayList<TodoListener>();
	}
	
    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    /**
     * Function used to reload folders after a change
     * on the local DB 
     * 
     * This function is fired by the system when a call 
     * to <code>notifyChange</code> was done.
     * 
	 * @param selfChange 
	 * 
	 * @see <code>notifyChange</code>
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        
        Log.d(TAG, "Data changed. Reloading folders...");
        
        this.getAllFolders();
    }
	
	/**
	 * Launch the authentication process to access the backend
	 * 
	 * Call: <code>onAuthenticationRequest</code> and
	 *       <code>onAuthenticationStatusChanged</code>
	 */
	public void launchAuthenticationProcess(Context c) {
		this.registerObserver(c);
		
		Intent i = new Intent(c, AuthActivity.class);

		// Send the Intent to listeners
		for (TodoListener listener : this.listeners) {
			listener.onAuthenticationRequest(i);
		}
	}
	
	/**
	 * Function used to register this class as a
	 * <code>ContentObserver</code>.
	 * 
	 * This function is used to register <code>this</code> 
	 * as a <code>ContentObserver</code> to be notified
	 * as soon as a change is made on the DB.
	 * 
	 * @param c The context used to register the observer.
	 */
	private void registerObserver(Context c) {
		this.cResolver = c.getContentResolver();
		this.cResolver.registerContentObserver(
				Uri.parse("content://fr.unilim.javance.projet.sync.GTasksContentProvider"), 
				true, this);
	}

	/**
	 * Get the authentication status
	 * 
	 * Call: <code>onAuthenticationStatusChanged</code>
	 */
	public void getAuthenticationStatus() {
		for (TodoListener listener : this.listeners) {
			listener.onAuthenticationStatusChanged(authenticationStatus);
		}
	}

	/**
	 * Function used to get a precise folder from the DB.
	 * 
	 * This function is used to query the DB for a precise
	 * folder.
	 * Notifies all observers at the end.
	 * 
	 * @param id The id of the folder to look for.
	 */
	public void getFolder(String id) {
		Cursor c = this.cResolver.query(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI,
                id), null, null, null, null);
		
		if(c.getCount() > 0) {
			c.moveToFirst();
			
			Folder f = Folder.makeFolder(c);
			
			for(TodoListener l : listeners) {
				l.onGetFolder(f);
			}
		}
	}

	/**
	 * Function used to get all folders from the DB.
	 * 
	 * This function is used to query the DB for all folders.
	 * Notifies all observers at the end.
	 */
	public void getAllFolders() {
		Cursor c = this.cResolver.query(GTasksContentProvider.FOLDER_CONTENT_URI, null, null, null, null);
		
		this.folders.clear();
		
		if(c.getCount() > 0) {
			c.moveToFirst();

			do {
				Log.d(TAG, "Folder found - id: " + c.getString(c.getColumnIndex(GTasksSQLite.FOLDER_ID)));
				
				Folder f = Folder.makeFolder(c);
				
				Cursor c1 = this.cResolver.query(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI,
	                    String.valueOf(f.getId()) + "/task"), null, null, null, null);
				
				Log.d(TAG, "Query getCount: " + c1.getCount());
				
				if(c1.getCount() > 0) {
					c1.moveToFirst();
					
					this.tasks.clear();
					
					do {
		        		Log.d(TAG, "Task found - id: " + c1.getString(c.getColumnIndex("ID")));
		        		
		        		this.tasks.add(Task.makeTask(c1, f));
					} while(c1.moveToNext());
				}
				
				f.setTasks(this.tasks);
				this.folders.add(f);
			} while(c.moveToNext());
			
			for(TodoListener l : listeners) {
				l.onGetAllFolders(folders);
			}
		}
	}

	/**
	 * Function used to create a folder on the DB.
	 * 
	 * This function inserts a folder on the DB.
	 * Notifies all observers at the end and tries to
	 * sync to app to the network.
	 * 
	 * @param folder The folder to insert.
	 */
	public void fillFolder(Folder folder) {
		Cursor c = this.cResolver.query(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI,
                folder.getId()), null, null, null, null);
		
		if(c.getCount() == 0) {
			folder.setTag(Tag.INSERT);
			
			this.cResolver.insert(GTasksContentProvider.FOLDER_CONTENT_URI, folder.makeContentValues());
			this.cResolver.notifyChange(GTasksContentProvider.FOLDER_CONTENT_URI, null, true);
			
			for(TodoListener l : listeners) {
				l.onFillFolder(folder);
			}
		}
		
	}

	/**
	 * Function used to delete a folder on the DB.
	 * 
	 * This function deletes a folder on the DB.
	 * Notifies all observers at the end and tries to
	 * sync to app to the network.
	 * 
	 * @param folder The folder to delete.
	 */
	public void deleteFolder(Folder folder) {
		folder.setTag(Tag.DELETE);
		
		int nb = this.cResolver.delete(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI, 
				folder.getId()), null, null);
		this.cResolver.notifyChange(Uri.withAppendedPath(GTasksContentProvider.FOLDER_CONTENT_URI,
                folder.getId()), null, true);
		
		for(TodoListener l : listeners) {
			l.onDeleteFolder((nb > 0)?true:false);
		}
	}

	/**
	 * Function used to get a precise task from the DB.
	 * 
	 * This function is used to get a precise task from 
	 * the DB using its ID.
	 * Notifies all observers at the end.
	 * 
	 * @param id The task to get.
	 */
	public void getTask(String id) {
		Cursor c = this.cResolver.query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                id), null, null, null, null);
		
		if(c.getCount() > 0) {
			c.moveToFirst();
			
			Task t = Task.makeTask(c);
			
			for(TodoListener l : listeners) {
				l.onGetTask(t);
			}
		}
	}

	/**
	 * This function creates a task on the DB.
	 * 
	 * This function is used to create a task on the DB.
	 * Notifies all observers at the end and tries to
	 * sync to app to the network.
	 * 
	 * @param folder The folder of the task to insert.
	 * @param task The task to insert.
	 */
	public void createTask(Folder folder, Task task) {
		Set<Task> tasks = folder.getTasks();
		tasks.add(task);
		folder.setTasks(tasks);
		
		task.setTag(Tag.INSERT);
		
		this.cResolver.insert(GTasksContentProvider.TASK_CONTENT_URI, task.makeContentValues());
		this.cResolver.notifyChange(GTasksContentProvider.TASK_CONTENT_URI, null, true);
		
		for(TodoListener l : listeners) {
			l.onCreateTask(folder, task);
		}
	}

	/**
	 * Function used to update a task on the DB.
	 * 
	 * This function updates a task on the DB.
	 * Notifies all observers at the end and tries to
	 * sync to app to the network.
	 * 
	 * @param task The task to update.
	 */
	public void updateTask(Task task) {
		if(task.getTag() != Tag.INSERT) {
			task.setTag(Tag.UPDATE);
		}
		
		this.cResolver.update(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                String.valueOf(task.getId())), task.makeContentValues(), null, null);
		this.cResolver.notifyChange(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                String.valueOf(task.getId())), null, true);
		
		for(TodoListener l : listeners) {
			l.onUpdateTask(task);
		}
	}


	/**
	 * Function used to delete a task on the DB.
	 * 
	 * This function deletes a task on the DB.
	 * Notifies all observers at the end and tries to
	 * sync to app to the network.
	 * 
	 * @param id The ID of the task to delete.
	 */
	public void deleteTask(String id) {
		Task task = new Task();
		
		for(Task t : tasks) {
			if(t.getId().equals(id)) {
				task = t;
			}
		}
		
		try {
			task.getId();
			
			task.setTag(Tag.DELETE);
			
			this.cResolver.update(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
	                String.valueOf(task.getId())), task.makeContentValues(), null, null);
			this.cResolver.notifyChange(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
	                id), null, true);
			
			for(TodoListener l : listeners) {
				l.onUpdateTask(task);
			}
		} catch(NullPointerException e) {}
	}

	/**
	 * Register a new listener
	 * 
	 * @param listener the listener to add
	 */
	public void registerListener(TodoListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(TodoListener listener) {
		this.listeners.remove(listener);
	}
	
	/**
	 * This function is used to set the authentication
	 * status.
	 * 
	 * @param status	The status to set
	 */
	public void setAuthenticationStatus(Boolean status) {
		authenticationStatus = status;
		
		getAuthenticationStatus();
	}
	
	/**
	 * Function used to set token info.
	 * 
	 * @param token			The token
	 * @param token_type	The token type
	 */
	public void setInfosToken(String token, String token_type) {
		this.token         = token;
		this.token_type    = token_type;
	}
}

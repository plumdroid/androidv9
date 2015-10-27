package fr.unilim.javance.projet.model.gtasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;
import fr.unilim.javance.projet.httpconnection.HttpConnection;
import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Tag;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TaskStatus;
import fr.unilim.javance.projet.model.gtasks.cache.GTasksSQLite;
import fr.unilim.javance.projet.sync.GTasksContentProvider;
import fr.unilim.javance.projet.sync.SyncAdapter;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * A class used to manage requests to the remote server
 * 
 * This class is called by the <code>SyncAdapter</code>
 * when a sync is being performed.
 * 
 * @author Thibault Desmoulins
 * @author Yorick Lesecque
 * 
 * @see SyncAdapter
 * @see HttpConnection
 */
public class RemoteManager {
    private static final String TAG = "RemoteManager";

	//private String api_key       = "AIzaSyBhilJssD4uPFCmYyWGi4k3loXvdbQocek"; // Yorick
	private String api_key       = "AIzaSyCoelG8Wjt0tuuwVrUG9ATIkyQiIWAkQzc"; // Thibault
    private  ArrayList<Folder> folders = new  ArrayList<Folder>();
    private SyncAdapter sAdapter;
    
    /**
     * An <code>int</code> to store the current state of
     * the sync. Using this, if an error is thrown during
     * a process, it will be entirely done again. 
     * It ensures atomical operations.
     */
    private int status = 0;
	
    /**
     * Default constructor
     * 
     * Called each time a sync is performed by the system
     * 
     * @param sAdapter	The SyncAdapter used to handle sync
     */
	public RemoteManager(SyncAdapter sAdapter) {
		Log.d(TAG, "Creating manager");
		
		this.sAdapter = sAdapter;
	}
	
	/**
	 * Function used to atomically get all <code>Folder</code>s or
	 * to atomically update the remote server.
	 * 
	 * This function is fired when an authentication to
	 * the remote server was successful right after an 
	 * unsuccessful one.
	 * 
	 * @throws Exception	The exception thrown by the application
	 */
	public void authenticationSuccessful() 
			throws Exception {
		
		Log.d(TAG, "authenticationSuccessful - status: " + this.status);
		
		switch(this.status) {
			case 0: this.sAdapter.onFoldersReceived(this.getAllFolders()); break;
			case 1: this.updateRemote(); break;
			default: this.sAdapter.onFoldersReceived(this.getAllFolders());
		}
	}
	
	/**
	 * Function used to get all <code>Folder</code>s from the remote
	 * server and to notify the <code>SyncAdapter</code> that the
	 * operation was successfully performed.
	 * 
	 * @throws Exception	The exception thrown by the application
	 */
	public void getAll() 
			throws Exception {
		
		this.sAdapter.onFoldersReceived(this.getAllFolders());
	}
	
	/**
	 * Function used to perform requests to the remote server to
	 * get all <code>Folder</code>s.
	 * 
	 * @return	An <code>ArrayList</code> of the <code>Folder</code>s
	 * 			caught from the remote server
	 * @throws Exception	The exception thrown by the application
	 */
	private ArrayList<Folder> getAllFolders() 
			throws Exception {
		
		this.status = 0;
		
		ArrayList<Folder> folders = new ArrayList<Folder>();
		List<NameValuePair> infos = new ArrayList<NameValuePair>(2);
		infos.add(new BasicNameValuePair("Authorization", "OAuth " + this.sAdapter.getToken()));
		infos.add(new BasicNameValuePair("X-JavaScript-User-Agent", "Google APIs Explorer google-api-gwt-client/0.1-alpha"));
		
		Log.d(TAG, "Sending the request : getAllFolders with token: " + this.sAdapter.getToken());
		
		String retour = HttpConnection.sendURL("GET", "https://www.googleapis.com/tasks/v1/users/@me/lists?pp=1&key=" 
				+ api_key, infos, this.sAdapter.getGAHandler());

		JSONObject jObject = new JSONObject(retour);
		
		try {
			JSONArray items    = jObject.getJSONArray("items");
			
			int taille = items.length();
			
			for(int i = 0 ; i < taille ; i++) {
				JSONObject jo = items.getJSONObject(i);
				
				Folder f = new Folder();
				f.setId(jo.getString("id"));
				f.setName(jo.getString("title"));
				f.setTasks(this.getTasks(f));
				
				folders.add(f);
			}
		} catch(JSONException e) { }
		
		return folders;
	}

	/**
	 * Function used to perform requests to the remote server to
	 * get all <code>Task</code>s.
	 * 
	 * @param folder		The <code>Folder</code> in which the class
	 * 						is contained
	 * @return				A <code>Set</code> of <code>Task</code>s contained
	 * 						in the <code>Folder</code>
	 * @throws Exception	The exception thrown by the application
	 */
	private Set<Task> getTasks(Folder folder) 
			throws Exception {
		
		Set<Task> tasks = new HashSet<Task>();
		List<NameValuePair> infos = new ArrayList<NameValuePair>(2);
		infos.add(new BasicNameValuePair("Authorization", "OAuth " + this.sAdapter.getToken()));
		infos.add(new BasicNameValuePair("X-JavaScript-User-Agent", "Google APIs Explorer google-api-gwt-client/0.1-alpha"));

		Log.d(TAG, "Sending the request : getTasks with token: " + this.sAdapter.getToken());
		Log.d(TAG, "Sending the request: getTasks :: " + folder.getName() + " :: " + folder.getId());

		String url = "https://www.googleapis.com/tasks/v1/lists/" + folder.getId() + "/tasks?pp=1&key=" + api_key;
		String retour = HttpConnection.sendURL("GET", url, infos, this.sAdapter.getGAHandler());
		
		Log.d(TAG, "Request sent: " + url);

		JSONObject jObject = new JSONObject(retour);
		
		try {
			JSONArray items    = jObject.getJSONArray("items");
			
			int taille = items.length();
			
			for(int i=0 ; i<taille ; i++) {
				JSONObject jo = items.getJSONObject(i);
				
				if(!jo.getString("title").equals("")) {
	        		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
	        		
					Task t = new Task();
					t.setId(jo.getString("id"));
					t.setTitle(jo.getString("title"));
					try {
						t.setDescription(jo.getString("notes"));
					} catch(JSONException e) {
						t.setDescription("");
					}
					t.setStatus((jo.getString("status") == "needsAction")?TaskStatus.PENDING:TaskStatus.DONE);
					try {
						t.setDueDate(format.parse(jo.getString("due")));
					} catch(JSONException e) {}
					t.setCreationDate(format.parse(jo.getString("updated")));
					t.setFolder(folder);
					
					tasks.add(t);
				}
			}
		} catch(JSONException e) { }
		
		return tasks;
	}
	
	/**
	 * Function used to perform the update of the remote DB.
	 * 
	 * When this function is called, it sets the <code>status</code>
	 * to 1 for the sync to handle atomic updating of the server.
	 * If an error is fired during the process, the sync will do the
	 * entire operation again.
	 * 
	 * @throws Exception	Exception thrown by the app
	 */
	public void updateRemote() 
			throws Exception {

		Log.d(TAG, "Updating...");
		this.status = 1;
		int count = 0;
		int requestPerformed = 0;
		
		ContentProviderClient cpClient = this.sAdapter.getContentProviderClient();
		
		List<NameValuePair> infos = new ArrayList<NameValuePair>(2);
		infos.add(new BasicNameValuePair("Authorization", "OAuth " + this.sAdapter.getToken()));
		infos.add(new BasicNameValuePair("X-JavaScript-User-Agent", "Google APIs Explorer google-api-gwt-client/0.1-alpha"));
		infos.add(new BasicNameValuePair("Content-Type", "application/json"));

		Log.d(TAG, "Making query to get all tagged folders");
		
		Cursor c = cpClient.query(GTasksContentProvider.FOLDER_CONTENT_URI, null, null, null, null);

		count += c.getCount();
		
		if(c.getCount() > 0) {
			Log.d(TAG, "Listing folders");
			c.moveToFirst();
			
			Folder f = Folder.makeFolder(c);
			
			String url = "https://www.googleapis.com/tasks/v1/users/@me/lists?pp=1&key=" + api_key;
			
			do {
				Log.d(TAG, "Folder tag: " + c.getString(c.getColumnIndex("TAG")));
				
				if(c.getInt(c.getColumnIndex("TAG")) == 1) {
					Log.d(TAG, "Folder " + c.getString(c.getColumnIndex("NAME")) + " is going to be inserted");

					infos.add(new BasicNameValuePair("JSON", f.makeJSON(true).toString()));
					
					Log.d(TAG, "Request sent: " + url);
					Log.d(TAG, "With token: " + this.sAdapter.getToken());
					
					String retour = HttpConnection.sendURL("POST", url, infos, this.sAdapter.getGAHandler());

					JSONObject jObject = new JSONObject(retour);
					try {
						jObject.getString("id");
						f.setTag(Tag.NOTHING);
						
						this.sAdapter.getContentProviderClient().delete(
								Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
						                String.valueOf(f.getId())), null, null);

						requestPerformed++;
						Log.d(TAG, "Insert succeed for folder: " + f.getId());
					} catch(JSONException e) {
						Log.d(TAG, "Insert failed for folder: " + f.getId());
					}
					
					Log.d(TAG, retour.toString());
				} else if(c.getInt(c.getColumnIndex("TAG")) == 2) {
					Log.d(TAG, "Folder " + c.getString(c.getColumnIndex("NAME")) + " is going to be updated");

					infos.add(new BasicNameValuePair("JSON", f.makeJSON(false).toString()));
					
					Log.d(TAG, "Request sent: " + url);
					Log.d(TAG, "With token: " + this.sAdapter.getToken());
					
					String retour = HttpConnection.sendURL("PUT", url, infos, this.sAdapter.getGAHandler());

					JSONObject jObject = new JSONObject(retour);
					try {
						jObject.getString("id");
						f.setTag(Tag.NOTHING);
						
						this.sAdapter.getContentProviderClient().update(
								Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
						                String.valueOf(f.getId())), f.makeContentValues(), null, null);

						requestPerformed++;
						Log.d(TAG, "Update succeed for folder: " + f.getId());
					} catch(JSONException e) {
						Log.d(TAG, "Update failed for folder: " + f.getId());
					}
					
					Log.d(TAG, retour.toString());
				} else if(c.getInt(c.getColumnIndex("TAG")) == 3) {
					Log.d(TAG, "Folder " + c.getString(c.getColumnIndex("NAME")) + " is going to be deleted");
					
					Log.d(TAG, "Request sent: " + url);
					Log.d(TAG, "With token: " + this.sAdapter.getToken());
					
					String retour = HttpConnection.sendURL("DELETE", url, infos, this.sAdapter.getGAHandler());

					if(retour == "No Content") {
						this.sAdapter.getContentProviderClient().delete(
								Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
						                String.valueOf(f.getId())), null, null);
						
						requestPerformed++;
						Log.d(TAG, "Delete succeed for folder: " + f.getId());
					} else {
						Log.d(TAG, "Delete failed for folder: " + f.getId());
					}
					
					Log.d(TAG, retour.toString());
				}
			} while(c.moveToNext());
			
			Log.d(TAG, "All folders were updated");
		}

		if(requestPerformed == count) {
			Log.d(TAG, "Making query to get all tagged tasks");
			
			c = cpClient.query(GTasksContentProvider.TASK_CONTENT_URI, null, null, null, null);
	
			count += c.getCount();
			
			if(c.getCount() > 0) {
				Log.d(TAG, "Listing tasks");
				c.moveToFirst();
	
				Task t = Task.makeTask(c);
				
				String url = "https://www.googleapis.com/tasks/v1/lists/" + t.getFolder().getId() + "/tasks/" 
						+ t.getId() + "?pp=1&key=" + api_key;
				
				do {
					Log.d(TAG, "Task tag: " + c.getString(c.getColumnIndex("TAG")));
					
					if(c.getInt(c.getColumnIndex("TAG")) == 1) {
						Log.d(TAG, "Task " + c.getString(c.getColumnIndex("TITLE")) + " is going to be inserted");
	
						infos.add(new BasicNameValuePair("JSON", t.makeJSON(true).toString()));
						
						Log.d(TAG, "Request sent: " + url);
						Log.d(TAG, "With token: " + this.sAdapter.getToken());
						
						String retour = HttpConnection.sendURL("POST", url, infos, this.sAdapter.getGAHandler());
	
						JSONObject jObject = new JSONObject(retour);
						try {
							jObject.getString("id");
							t.setTag(Tag.NOTHING);
							
							this.sAdapter.getContentProviderClient().delete(
									Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
							                String.valueOf(t.getId())), null, null);
	
							requestPerformed++;
							Log.d(TAG, "Insert succeed for task: " + t.getId());
						} catch(JSONException e) {
							Log.d(TAG, "Insert failed for task: " + t.getId());
						}
						
						Log.d(TAG, retour.toString());
					} else if(c.getInt(c.getColumnIndex("TAG")) == 2) {
						Log.d(TAG, "Task " + c.getString(c.getColumnIndex("TITLE")) + " is going to be updated");
	
						infos.add(new BasicNameValuePair("JSON", t.makeJSON(false).toString()));
						
						Log.d(TAG, "Request sent: " + url);
						Log.d(TAG, "With token: " + this.sAdapter.getToken());
						
						String retour = HttpConnection.sendURL("PUT", url, infos, this.sAdapter.getGAHandler());
	
						JSONObject jObject = new JSONObject(retour);
						try {
							jObject.getString("id");
							t.setTag(Tag.NOTHING);
							
							this.sAdapter.getContentProviderClient().update(
									Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
							                String.valueOf(t.getId())), t.makeContentValues(), null, null);
	
							requestPerformed++;
							Log.d(TAG, "Update succeed for task: " + t.getId());
						} catch(JSONException e) {
							Log.d(TAG, "Update failed for task: " + t.getId());
						}
						
						Log.d(TAG, retour.toString());
					} else if(c.getInt(c.getColumnIndex("TAG")) == 3) {
						Log.d(TAG, "Task " + c.getString(c.getColumnIndex("TITLE")) + " is going to be deleted");
						
						Log.d(TAG, "Request sent: " + url);
						Log.d(TAG, "With token: " + this.sAdapter.getToken());
						
						String retour = HttpConnection.sendURL("DELETE", url, infos, this.sAdapter.getGAHandler());
	
						if(retour == "No Content") {
							this.sAdapter.getContentProviderClient().delete(
									Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
							                String.valueOf(t.getId())), null, null);
							
							requestPerformed++;
							Log.d(TAG, "Delete succeed for task: " + t.getId());
						} else {
							Log.d(TAG, "Delete failed for task: " + t.getId());
						}
						
						Log.d(TAG, retour.toString());
					}
				} while(c.moveToNext());
				
				Log.d(TAG, "All Tasks were updated");
			}
		}

		
		Log.d(TAG, "Update finished");
		
		Log.d(TAG, "Count: " + count + " - requestPerformed: " + requestPerformed);
		if(requestPerformed == count) {
			this.sAdapter.getContext().getContentResolver().notifyChange(GTasksContentProvider.TASK_CONTENT_URI, null);
			
			this.sAdapter.onFoldersUpdated();
		}
	}
}

package fr.unilim.javance.projet.model.gtasks.cache;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;
import fr.unilim.javance.projet.model.gtasks.RemoteManager;
import fr.unilim.javance.projet.sync.GTasksContentProvider;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A <code>SQLiteOpenHelper</code> implementation
 * 
 * This class is called directly by Android system when
 * a the DB needs to be created or updated. 
 * 
 * It's initialized at the first use of the application 
 * and called whenever it's needed. This works directly 
 * with a content provider (<code>GTasksContentProvider</code>).
 * 
 * @author Thibault Desmoulins
 * @author Yorick Lesecque
 * 
 * @see SQLiteOpenHelper
 */
public class GTasksSQLite extends SQLiteOpenHelper {
    private static final String TAG = "GTasksSQLite";
	
	private static final String DB_NAME = "gtasks_bdd";
	private static final int DB_VERSION = 1;
 
	public static final String TABLE_FOLDER = "folder";
	public static final String TABLE_TASK = "task";
 
	public static final String FOLDER_ID = "ID";
	public static final String FOLDER_TAG = "TAG";
	public static final String TASK_ID   = "ID";
	public static final String TASK_TAG = "TAG";
	
	private static final String CREATE_TABLE_FOLDER = "CREATE TABLE " + TABLE_FOLDER + " (" +
			FOLDER_ID +" TEXT PRIMARY KEY NOT NULL," +
			"NAME TEXT NOT NULL," +
			"TAG INT NOT NULL" +
		");";
	 
	private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASK + " (" +
			TASK_ID + " TEXT PRIMARY KEY NOT NULL," +
			"TITLE TEXT NOT NULL," +
			"DESCRIPTION TEXT," +
			"ENCRYPTED_DATE TEXT," +
			"CREATION_DATE DATE NOT NULL," +
			"DUE_DATE DATE," +
			"STATUS TEXT NOT NULL," +
			"ADDRESS TEXT," +
			"TAG INT NOT NULL," +
			"ID_FOLDER_PARENT TEXT NOT NULL" +
		");";
 
	public GTasksSQLite(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		Log.v(TAG, "Creating GTasksSQLite (calling Super)");
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Creation of the database
		
		Log.v(TAG, "Creating Database");
		
		db.execSQL(CREATE_TABLE_FOLDER);
		Log.v(TAG, "CREATE_TABLE_FOLDER created");
		
		db.execSQL(CREATE_TABLE_TASK);
		Log.v(TAG, "CREATE_TABLE_TASK created");
		
		Log.v(TAG, "Database created");
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Delete every table of the database and create a new one
		
		Log.v(TAG, "Upgrading Database");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER + ";");
		Log.v(TAG, "TABLE_FOLDER deleted");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK + ";");
		Log.v(TAG, "TABLE_TASK deleted");
		
		onCreate(db);
		Log.v(TAG, "Database upgraded");
	}
 
}
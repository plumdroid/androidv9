package fr.unilim.javance.projet.sync;

import java.util.List;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;
import fr.unilim.javance.projet.model.gtasks.RemoteManager;
import fr.unilim.javance.projet.model.gtasks.cache.GTasksSQLite;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * A <code>ContentProvider</code> implementation
 * 
 * It's initialized at the first use of the application 
 * and called whenever it's needed. It is used when
 * some operation has to be done on the DB.
 * 
 * @author Thibault Desmoulins
 * @author Yorick Lesecque
 * 
 * @see ContentProvider
 */
public class GTasksContentProvider extends ContentProvider {
	private GTasksSQLite bdd;
	private static final String TAG = "GTasksContentProvider";
	
	public static final String AUTHORITY = "fr.unilim.javance.projet.sync.GTasksContentProvider";
	
	public static final int FOLDER = 100;
	public static final int FOLDER_ID = 110;
	public static final int FOLDER_TAG = 120;
	
	public static final int TASK = 200;
	public static final int TASK_ID = 210;
	public static final int TASK_TAG = 220;
	public static final int TASK_BY_FOLDER_ID = 230;
	 
	private static final String FOLDER_BASE_PATH = "folder";
	private static final String FOLDER_TAG_PATH = "folder/tag";
	
	private static final String TASK_BASE_PATH = "task";
	private static final String TASK_TAG_PATH = "task/tag";
	private static final String TASK_BY_FOLDER_PATH = "folder/*/task";
	
	/**
	 * All URIs needed to handle the DB.
	 */
	public static final Uri FOLDER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FOLDER_BASE_PATH);
	public static final Uri FOLDER_TAG_URI = Uri.parse("content://" + AUTHORITY + "/" + FOLDER_TAG_PATH);
	
	public static final Uri TASK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TASK_BASE_PATH);
	public static final Uri TASK_TAG_URI = Uri.parse("content://" + AUTHORITY + "/" + TASK_TAG_PATH);
	public static final Uri TASK_BY_FOLDER_URI = Uri.parse("content://" + AUTHORITY + "/" + TASK_BY_FOLDER_PATH);
	
	/**
	 * The URIMatcher that helps matching URIs needed to perform queries.
	 */
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
	    sURIMatcher.addURI(AUTHORITY, FOLDER_BASE_PATH, FOLDER);
	    sURIMatcher.addURI(AUTHORITY, FOLDER_BASE_PATH + "/*", FOLDER_ID);
	    sURIMatcher.addURI(AUTHORITY, FOLDER_TAG_URI + "/*", FOLDER_TAG);
	    
	    sURIMatcher.addURI(AUTHORITY, TASK_BASE_PATH, TASK);
	    sURIMatcher.addURI(AUTHORITY, TASK_BASE_PATH + "/*", TASK_ID);
	    sURIMatcher.addURI(AUTHORITY, TASK_TAG_URI + "/*", TASK_TAG);
	    sURIMatcher.addURI(AUTHORITY, TASK_BY_FOLDER_PATH, TASK_BY_FOLDER_ID);
	    
	}
	

	@Override
	public boolean onCreate() {
		Log.v(TAG, "Creating  ContentProvider");
		
		bdd = new GTasksSQLite(getContext());
		
		/*
		SQLiteDatabase sqlDB = bdd.getWritableDatabase();
		bdd.onUpgrade(sqlDB, 0, 0);
		/**/
		
		Log.v(TAG, "ContentProvider created");
		
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = bdd.getWritableDatabase();
		int rowsAffected = 0;
		String id;
		
		switch (uriType) {
			case FOLDER:
				rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_FOLDER, selection, selectionArgs);
				
				Log.e(TAG, String.valueOf(FOLDER));
				break;
			case FOLDER_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_TASK, "ID_FOLDER_PARENT=" + id, null);
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_FOLDER, 
							GTasksSQLite.FOLDER_ID + "=\"" + id + "\"", null);
				}
				else {
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_TASK, "ID_FOLDER_PARENT=" + id, null);
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_FOLDER, selection + " and " + 
							GTasksSQLite.FOLDER_ID + "=\"" + id + "\"", selectionArgs);
				}
				
				Log.e(TAG, String.valueOf(FOLDER_ID));
				break;			
				
			case TASK:
				rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_TASK, selection, selectionArgs);
				
				Log.e(TAG, String.valueOf(TASK));
				break;
			case TASK_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_TASK, GTasksSQLite.TASK_ID + "=\"" + id + "\"", null);
				}
				else {
					rowsAffected = sqlDB.delete(GTasksSQLite.TABLE_TASK, selection + " and " + 
							GTasksSQLite.TASK_ID + "=\"" + id + "\"", selectionArgs);
				}
				
				break;
				
			default:
				throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		}
		
		return rowsAffected;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "Inserting...");
		
		SQLiteDatabase sqlDB = bdd.getWritableDatabase();
		
		long id = 0;
		
		int uriType = sURIMatcher.match(uri);
		switch(uriType) {
			case FOLDER:
				id = sqlDB.insert(GTasksSQLite.TABLE_FOLDER, "", values);
				break;
				
			case TASK:
				id = sqlDB.insert(GTasksSQLite.TABLE_TASK, "", values);
				break;
			
			default: 
				throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		}
		
		if(id > 0) {
			Log.d(TAG, "Insert succed");
			
			Uri newUri = ContentUris.withAppendedId(uri, id);
			
			return newUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Log.e(TAG, "URI for the query: " + uri);
		
		int uriType = sURIMatcher.match(uri);
		Log.e(TAG, "URI Type: " + uriType);
		switch (uriType) {
			case FOLDER_ID:
				queryBuilder.setTables(GTasksSQLite.TABLE_FOLDER);
				queryBuilder.appendWhere(GTasksSQLite.FOLDER_ID + "=\"" + uri.getLastPathSegment() + "\"");
				break;
			case TASK_ID:
				queryBuilder.setTables(GTasksSQLite.TABLE_TASK);
				queryBuilder.appendWhere(GTasksSQLite.TASK_ID + "=\"" + uri.getLastPathSegment() + "\"");
				break;
				
			case FOLDER:
				queryBuilder.setTables(GTasksSQLite.TABLE_FOLDER);
				break;
			case TASK:
				queryBuilder.setTables(GTasksSQLite.TABLE_TASK);
				break;
				
			case FOLDER_TAG:
				queryBuilder.setTables(GTasksSQLite.TABLE_FOLDER);
				queryBuilder.appendWhere(GTasksSQLite.FOLDER_TAG + "!= 0");
				break;
			case TASK_TAG:
				queryBuilder.setTables(GTasksSQLite.TABLE_TASK);
				queryBuilder.appendWhere(GTasksSQLite.TASK_TAG + "!= 0");
				break;
			
			case TASK_BY_FOLDER_ID:
				List<String> allSegments = uri.getPathSegments();
				Log.e(TAG, "ID of the Folder: " + allSegments.get(allSegments.size()-2));
				queryBuilder.setTables(GTasksSQLite.TABLE_TASK);
				queryBuilder.appendWhere("ID_FOLDER_PARENT=\"" + allSegments.get(allSegments.size()-2) + "\"");
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI - type:" + uriType);
		}
		
		Cursor cursor = queryBuilder.query(bdd.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, "Updating...");
		
		SQLiteDatabase sqlDB = bdd.getWritableDatabase();
		
		int nbRow = 0;
		
		int uriType = sURIMatcher.match(uri);
		switch(uriType) {
			case FOLDER_ID:
				nbRow = sqlDB.update(GTasksSQLite.TABLE_FOLDER, values, 
						GTasksSQLite.TASK_ID + "=\"" + uri.getLastPathSegment() + "\"", null);
				break;
				
			case TASK_ID:
				nbRow = sqlDB.update(GTasksSQLite.TABLE_TASK, values, 
						GTasksSQLite.TASK_ID + "=\"" + uri.getLastPathSegment() + "\"", null);
				break;
			
			default: 
				throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		}
		
		if(nbRow > 0) {
			Log.d(TAG, "Update succed");
			
			return nbRow;
		}
		
		throw new SQLException("Failed to update row into " + uri);
	}

}

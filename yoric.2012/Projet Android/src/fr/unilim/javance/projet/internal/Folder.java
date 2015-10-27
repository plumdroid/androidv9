package fr.unilim.javance.projet.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Collection of <code>Task</code>s
 * 
 * A <code>Folder</code> is a collection of <code>Task</code>s (ie. a folder contains
 * several tasks).
 * 
 * As properties, a folder has an explicit name and a unique identifier in addition to
 * a <code>Set</code> of <code>Task</code>s. 
 * 
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public class Folder implements Cloneable {
    private static final String TAG = "Folder";
    
	private Set<Task> tasks = new HashSet<Task>();
	private String name;
	private String id;
	private Tag tag;

	/**
	 * Get all tasks belonging to the folder
	 * 
	 * @return all tasks belonging to the folder
	 */
	public Set<Task> getTasks() {
		return tasks;
	}

	/**
	 * Modify the collection of tasks belonging to the folder
	 * 
	 * @param tasks the new collection of tasks
	 */
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	
	/**
	 * Add a new task to the collection of tasks belonging to the folder
	 * 
	 * Be careful, the collection used to store <code>Task</code> instances
	 * is a Set which do not allow duplicates (ie. there cannot be two tasks
	 * with the same identifier in such a collection).
	 * 
	 * @param task the task to add
	 * @return <code>true</code> only if the collection of tasks is modified 
	 */
	public boolean addTask(Task task) {
		if (tasks == null) {
			tasks = new HashSet<Task>();
		}
		return tasks.add(task);
	}
	
	/**
	 * Remove a task from the collection of tasks belonging to the folder
	 * 
	 * @param task the task to delete
	 * @return <code>true</code> only if the collection of tasks is modified
	 */
	public boolean removeTask(Task task) {
		if (tasks != null) {
			return tasks.remove(task);
		}
		return false;
	}
	
	/**
	 * Get the folder name
	 * 
	 * @return the folder name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the folder name
	 * 
	 * @param name the new folder name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the folder identifier
	 * 
	 * This value is unique for each folder.
	 * 
	 * @return the folder identifier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the folder identifier
	 * 
	 * This value must be unique for each folder.
	 * 
	 * @param id the new folder identifier
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Creates and returns a copy of this object
	 * 
	 * @return the copy of this object
	 */
	@Override
	public Object clone() {
		Folder clone = new Folder();
		clone.setId(this.id);
		clone.setName(this.name);
		
		Set<Task> cloneTasks = new HashSet<Task>();
		
		for (Task task : tasks) {
			Task cloneTask = (Task) task.clone();
			cloneTask.setFolder(clone);
			cloneTasks.add(cloneTask);
		}
		
		return clone;
	}
	
	public ContentValues makeContentValues() {
		ContentValues cv = new ContentValues();
		cv.put("ID", this.getId());
		cv.put("NAME", this.getName());
		Tag t = this.getTag();
		cv.put("TAG", (t == Tag.NOTHING)?"0":(t == Tag.INSERT)?"1":(t == Tag.UPDATE)?"2":"3");
		
		return cv;
	}
	
	public static Folder makeFolder(Cursor c) {
		Log.d("Folder", "Making Folder");
		Folder f = new Folder();

		f.setId(c.getString(c.getColumnIndex("ID")));
		Log.d("Folder", "ID: " + f.getId());
		f.setName(c.getString(c.getColumnIndex("NAME")));
		Log.d("Folder", "NAME: " + f.getName());
		int tag = c.getInt(c.getColumnIndex("TAG"));
		f.setTag((tag == 0)?Tag.NOTHING:(tag == 1)?Tag.INSERT:(tag == 2)?Tag.UPDATE:Tag.DELETE);
		Log.d("Folder", "TAG: " + f.getTag());
		
		return f;
	}
	
	public JSONObject makeJSON(boolean insert)
			throws JSONException {
		
        JSONObject jObject = new JSONObject();
        
        if(!insert) {
	        jObject.put("id", this.getId());
	        Log.d(TAG, this.getId());
        }
        
        jObject.put("title", this.getName());
        Log.d(TAG, this.getName());

		return jObject;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public Tag getTag() {
		return this.tag;
	}
}

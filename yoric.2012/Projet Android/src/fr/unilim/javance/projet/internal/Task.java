package fr.unilim.javance.projet.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

/**
 * A task in a "todo"
 * 
 * This class defines a task in the todo application implementation.
 * 
 * There is a set of properties. A task has:
 * <ul>
 *   <li>a unique identifier <code>id</code>. It two tasks
 *       have the same <code>id</code>, they refer to the same task,</li>
 *   <li>a title which is also the task label (a short description),</li>
 *   <li>a more complete description,</li>
 *   <li>the creation date,</li>
 *   <li>a due date,</li>
 *   <li>a status defined by the enum <code>fr.unilim.javance.project.internal.TaskStatus</code>,</li>
 *   <li>an address,</li>
 *   <li>and a folder.</li>
 * </ul>
 * 
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public class Task implements Cloneable {
    private static final String TAG = "Task";
    
	private String id;
	private String title;
	private String description;
	private String encryptedData;
	private Date creationDate;
	private Date dueDate;
	private TaskStatus status;
	private Address address;
	private Folder folder;
	private Priority priority;
	private Tag tag;
	private Cipher ecipher;
	private Cipher dcipher;
	
	/**
	 * Get the task title
	 * 
	 * @return the task title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the task title
	 * 
	 * @param title the new task title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get the task description
	 * 
	 * @return the task description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the task description
	 * 
	 * @param description the new task description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Get the task encrypted data
	 * 
	 * @return the task encrypted data
	 */
	public String getEncryptedData() {
		return encryptedData;
	}
	
	/**
	 * Set the task data to be encrypted
	 * 
	 * @param encryptedData the new data to be encrypted
	 */
	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}
	
	/**
	 * Get the task creation date
	 * 
	 * @return the task creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Set the task creation date
	 * 
	 * @param creationDate the new task creation date
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Get the task due date
	 * 
	 * @return the task due date
	 */
	public Date getDueDate() {
		return dueDate;
	}
	
	/**
	 * Set the task due date
	 * 
	 * @param dueDate the new task due date
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	/**
	 * Get the task status
	 * 
	 * @return the task status
	 */
	public TaskStatus getStatus() {
		return status;
	}
	
	/**
	 * Set the task status
	 * 
	 * @param status the new task status
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	/**
	 * Get the address associated to the task
	 * 
	 * @return the address associated to the task
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * Set the address associated to the task
	 * 
	 * @param address the new address associated to the task
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
	/**
	 * Get the folder in which the task belongs
	 * 
	 * @return the folder in which the task belongs
	 */
	public Folder getFolder() {
		return folder;
	}
	
	/**
	 * Set the folder in which the task belongs
	 * 
	 * @param folder the new folder in which the task belongs
	 */
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	
	/**
	 * Get the task identifier
	 * 
	 * This identifier must be unique for each task.
	 * 
	 * @return the task identifier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the task identifier
	 * 
	 * This identifier must be unique for each task.
	 * 
	 * @param id the new task identifier
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the task priority
	 * 
	 * @return the task priority 
	 */
	public Priority getPriority() { 
		return priority; 
	}
	
	/**
	 * Set the task priority
	 * 
	 * @param priority the new task priority 
	 */
	public void setPriority(Priority priority) { 
		this.priority = priority; 
	}
	
	/**
	 * Define equality between the <code>Task</code> instance and an object
	 * 
	 * An instance of <code>Task</code> is equal to another object if:
	 * <ul>
	 *   <li><code>this</code> and the object refer to the same instance,</li>
	 *   <li>the object is an instance of the <code>Task</code> and contains 
	 *       the same identifier (case sensitive)</li>
	 * </ul>
	 * Else, the method <code>equals</code> will return false.
	 * 
	 * @param obj the object to compare
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Task) {
			Task other = (Task) obj;
			
			if (!this.id.equals(other.getId())) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Creates and returns a copy of this object
	 * 
	 * Be careful, the folder field refers to the same
	 * parent folder (and so is not cloned).
	 * 
	 * @return the copy of this object
	 */
	@Override
	public Object clone() {
		Task clone = new Task();
		
		clone.setId(this.id);
		clone.setTitle(this.title);
		clone.setDescription(this.description);
		
		if (this.creationDate != null) {
			clone.setCreationDate((Date)this.creationDate.clone());
		}
		
		if (this.dueDate != null) {
			clone.setDueDate((Date)this.dueDate.clone());
		}
		
		clone.setStatus(this.status);
		clone.setAddress(this.address);
		clone.setFolder(this.folder);
		clone.setPriority(this.priority);
		
		return clone;
	}
	
	public ContentValues makeContentValues() {
		ContentValues cv = new ContentValues();
		cv.put("ID", this.getId());
		cv.put("TITLE", this.getTitle());
		try {
			cv.put("DESCRIPTION", this.getDescription());
		} catch (NullPointerException e) {}
		cv.put("STATUS", (this.getStatus() == TaskStatus.DONE)?"completed":"needsAction");
		try {
			cv.put("DUE_DATE", this.getDueDate().toString());
		} catch (NullPointerException e) {}
		cv.put("CREATION_DATE", this.getCreationDate().toString());
		cv.put("ID_FOLDER_PARENT", this.getFolder().getId());
		Tag t = this.getTag();
		cv.put("TAG", (t == Tag.NOTHING)?"0":(t == Tag.INSERT)?"1":(t == Tag.UPDATE)?"2":"3");
		
		return cv;
	}
	
	public static Task makeTask(Cursor c, Folder parent) {
		Log.d("Task", "Making Task");
		Task t = new Task();

		t.setId(c.getString(c.getColumnIndex("ID")));
		Log.d("Task", "ID: " + t.getId());
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		try {
			Date date = format.parse(c.getString(c.getColumnIndex("CREATION_DATE")));
			t.setCreationDate(date);
			Log.d("Task", "Creation Date: " + t.getCreationDate());
			
			if(c.getString(c.getColumnIndex("DUE_DATE")) != null) {
				date = format.parse(c.getString(c.getColumnIndex("DUE_DATE")));
				t.setDueDate(date);
				Log.d("Task", "Due date: " + t.getDueDate());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		t.setTitle(c.getString(c.getColumnIndex("TITLE")));
		Log.d("Task", "Title: " + t.getTitle());
		
		int tag = c.getInt(c.getColumnIndex("TAG"));
		t.setTag((tag == 0)?Tag.NOTHING:(tag == 1)?Tag.INSERT:(tag == 2)?Tag.UPDATE:Tag.DELETE);
		Log.d("Task", "Tag: " + t.getTag());
		
		t.setDescription(c.getString(c.getColumnIndex("DESCRIPTION")));
		
		t.setFolder(parent);

		String status = c.getString(c.getColumnIndex("STATUS"));
		t.setStatus((status == "completed")?TaskStatus.DONE:TaskStatus.PENDING);
		
		return t;
	}

	public static Task makeTask(Cursor c) {
		Log.d("Task", "Making Task");
		Task t = new Task();

		t.setId(c.getString(c.getColumnIndex("ID")));
		Log.d("Task", "ID: " + t.getId());
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		try {
			Date date = format.parse(c.getString(c.getColumnIndex("CREATION_DATE")));
			t.setCreationDate(date);
			Log.d("Task", "Creation Date: " + t.getCreationDate());
			
			if(c.getString(c.getColumnIndex("DUE_DATE")) != null) {
				date = format.parse(c.getString(c.getColumnIndex("DUE_DATE")));
				t.setDueDate(date);
				Log.d("Task", "Due date: " + t.getDueDate());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		t.setTitle(c.getString(c.getColumnIndex("TITLE")));
		Log.d("Task", "Title: " + t.getTitle());
		
		int tag = c.getInt(c.getColumnIndex("TAG"));
		t.setTag((tag == 0)?Tag.NOTHING:(tag == 1)?Tag.INSERT:(tag == 2)?Tag.UPDATE:Tag.DELETE);
		Log.d("Task", "Tag: " + t.getTag());
		
		t.setDescription(c.getString(c.getColumnIndex("DESCRIPTION")));
		
		Folder f = new Folder();
		f.setId(c.getString(c.getColumnIndex("ID_FOLDER_PARENT")));
		t.setFolder(f);

		String status = c.getString(c.getColumnIndex("STATUS"));
		t.setStatus((status == "completed")?TaskStatus.DONE:TaskStatus.PENDING);
		
		return t;
	}
	
	public JSONObject makeJSON(boolean insert) 
			throws JSONException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        JSONObject jObject = new JSONObject();
        
        if(!insert) {
	        jObject.put("id", this.getId());
	        Log.d(TAG, this.getId());
        }
        
        jObject.put("title", this.getTitle());
        Log.d(TAG, this.getTitle());
        jObject.put("updated", format.format(this.getCreationDate()));
        Log.d(TAG, format.format(this.getCreationDate()));

		try {
	        jObject.put("notes", this.getDescription());
	        Log.d(TAG, this.getDescription());
		} catch (NullPointerException e) {}
		
		try {
	        jObject.put("due", format.format(this.getDueDate()));
	        Log.d(TAG, format.format(this.getDueDate()));
		} catch (NullPointerException e) {}

        jObject.put("status", 
        		(this.getStatus() == TaskStatus.DONE)?"completed":"needsAction");
        Log.d(TAG, (this.getStatus() == TaskStatus.DONE)?"completed":"needsAction");

		return jObject;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public Tag getTag() {
		return this.tag;
	}

	
	public void init(Context c) {
		String uniqueID = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
	    
	    final SecretKey key = new SecretKeySpec(uniqueID.getBytes(), 0, 8, "DES");
	    
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes("UTF8");

            byte[] enc = ecipher.doFinal(utf8);

            return Base64.encodeToString(enc, Base64.DEFAULT);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }

    public String decrypt(String str) {
        try {
            byte[] dec = Base64.decode(str.getBytes("UTF8"), Base64.DEFAULT);

            byte[] utf8 = dcipher.doFinal(dec);

            return new String(utf8, "UTF8");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
}

package fr.unilim.javance.projet.model.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TodoListener;
import fr.unilim.javance.projet.internal.TodoManager;
import fr.unilim.javance.projet.model.mock.cache.MockFolderCache;
import fr.unilim.javance.projet.model.mock.cache.MockTaskCache;

/**
 * A <code>TodoManager</code> mock implementation
 * 
 * A naive implementation of the <code>TodoManager</code> interface. It means
 * that this implementation is not proof of bugs.
 * We encourage you to fix it and make pull requests during the project. We would
 * not take support if anything is wrong.
 * 
 * Compared to the attempted implementation, it does not use threads (objects are
 * cached in memory), inputs are not verified etc.
 * Obviously, you can extend this class to meet your requirements.
 * 
 * To use such a class, you have to register listeners to obtain the result
 * of the operation.
 * 
 * All objects are stored in memory, there is no persistence (ie. when restarting
 * the application you will have a "blank" configuration).
 * 
 * @author Amaury Gauthier
 * @author David Pequegnot
 * @see TodoManager
 * @see TodoListener
 * @see MockFolderCache
 * @see MockTaskCache
 */
public class MockTodoManager implements TodoManager {
	private ArrayList<TodoListener> listeners;
	
	/**
	 * Default constructor
	 * 
	 * Initialize the listeners list.
	 */
	public MockTodoManager() {
		this.listeners = new ArrayList<TodoListener>();
	}
	
	/**
	 * Launch the authentication process to access the backend
	 * 
	 * No authentication is needed in our schema. So listeners are notified with
	 * an empty (<code>null</code>) <code>Activity</code>.
	 * 
	 * Call: <code>onAuthenticationRequest</code> and
	 *       <code>onAuthenticationStatusChanged</code>
	 */
	public void launchAuthenticationProcess(Context context) {
		// Send null Activity to listeners
		// This step can be optional
		for (TodoListener listener : this.listeners) {
			listener.onAuthenticationRequest(null);
		}
		
		// Send the authentication status to listeners
		for (TodoListener listener : this.listeners) {
			listener.onAuthenticationStatusChanged(true);
		}
	}

	/**
	 * Get the authentication status
	 * 
	 * In this implementation, the user is already authenticated. Thus, the listener
	 * already receives <code>true</code> for the authentication status.
	 * 
	 * Call: <code>onAuthenticationStatusChanged</code>
	 */
	public void getAuthenticationStatus() {
		// Send the authentication status to listener
		for (TodoListener listener : this.listeners) {
			listener.onAuthenticationStatusChanged(true);
		}
	}

	/**
	 * Get a folder by id
	 * 
	 * Call: <code>onGetFolder</code>
	 * 
	 * @param id the folder id to find
	 * @throws IllegalArgumentException if the parameter is <code>null</code>
	 */
	public void getFolder(String id) {		
		Folder result = null;
		
		// id parameter cannot be null
		if (id == null) {
			throw new IllegalArgumentException("id parameter cannot be null");
		}
		
		// Get the Folder instance from the database
		for (Folder folder: MockFolderCache.getInstance().getCollection()) {
			if (folder.getId().equals(id)) {
				result = (Folder) folder.clone();
				break;
			}
		}
		
		// Notify listeners
		for (TodoListener listener : this.listeners) {
			listener.onGetFolder(result);
		}
	}

	/**
	 * Get all folders
	 * 
	 * Call: <code>onGetAllFolders</code>
	 */
	public void getAllFolders() {
		Set<Folder> allFolders = new HashSet<Folder>();
		
		for (Folder folder : MockFolderCache.getInstance().getCollection()) {
			allFolders.add((Folder) folder.clone());
		}
		
		for (TodoListener listener : this.listeners) {
			listener.onGetAllFolders(allFolders);
		}
	}

	/**
	 * Fill or create a folder
	 * 
	 * Create a folder if the given folder in parameter has no identifier
	 * (empty or null).
	 * Else, it will fill folder's tasks with their values.
	 * 
	 * Call: <code>onFillFolder</code>
	 * 
	 * @param folder the folder to fill or create
	 * @throws IllegalArgumentException if the folder parameter is null
	 */
	public void fillFolder(Folder folder) {
		// folder parameter cannot be null
		if (folder == null) {
			throw new IllegalArgumentException("folder parameter cannot be null");
		}
		
		// If the folder identifier was not set, we add it to the collection
		if (folder.getId() == null || folder.getId().isEmpty()) {
			int newId = 0;
			
			// Create a new instance of Folder to be stored
			folder.setTasks(new HashSet<Task>());
			Folder folderToStore = (Folder) folder.clone();
			
			// Check the max id 
			for (Folder folderItem : MockFolderCache.getInstance().getCollection()) {
				int currentId = Integer.getInteger(folderItem.getId()); 
				if (currentId > newId) {
					newId = currentId;
				}
			}
			newId += 1;
			
			// Set the id
			folder.setId(Integer.toString(newId));
			folderToStore.setId(Integer.toString(newId));
			
			// Add the folder to the collection
			if (!MockFolderCache.getInstance().getCollection().add(folderToStore)) {
				
				// Notify listeners that something was wrong 
				for (TodoListener listener : this.listeners) {
					listener.onFillFolder(null);
				}
				return;
			}
		}
		
		// Update tasks from the folder
		for (Task emptyTask : folder.getTasks()) {
			for (Task task : MockTaskCache.getInstance().getCollection()) {
				if (task.equals(emptyTask)) {
					copyTask(task, emptyTask);
					break;
				}
			}
		}
		
		// Notify listeners
		for (TodoListener listener : this.listeners) {
			listener.onFillFolder(folder);
		}
	}

	/**
	 * Delete a folder from the database
	 * 
	 * Call: onDeleteFolder
	 * 
	 * @param folder the folder to remove
	 */
	public void deleteFolder(Folder folder) {
		// Remove the folder from the database
		boolean result = MockFolderCache.getInstance().getCollection().remove(folder);
		
		// Notify listeners
		for (TodoListener listener : this.listeners) {
			listener.onDeleteFolder(result);
		}
	}

	/**
	 * Get a task from the database by id
	 * 
	 * Call: onGetTask
	 * 
	 * @param id the task id
	 * @throws IllegalArgumentException if the id is null
	 */
	public void getTask(String id) {
		Task result = null;
		
		// id parameter cannot be null
		if (id == null) {
			throw new IllegalArgumentException("id parameter cannot be null");
		}
		
		// Get the task from the database
		for (Task task : MockTaskCache.getInstance().getCollection()) {
			if (task.getId().equals(id)) {
				result = (Task) task.clone();
			}
		}
		
		// Notify listeners
		for (TodoListener listener : this.listeners) {
			listener.onGetTask(result);
		}
	}

	/**
	 * Create a task in the database
	 * 
	 * Call: onCreateTask
	 * 
	 * @param folder the folder in which the task must be added
	 * @param the task to add
	 */
	public void createTask(Folder folder, Task task) {
		Folder collectionFolder = null;
		Task collectionTask;
		
		// Obtain the corresponding folder in the collection
		for (Folder folderPick : MockFolderCache.getInstance().getCollection()) {
			if (folderPick.getId().equals(folder.getId())) {
				collectionFolder = folderPick;
				break;
			}
		}
		
		// If the folder does not exist in the database
		if (collectionFolder == null) {
			throw new IllegalArgumentException("folder does not exist in the database");
		}
		
		// Look for a unique id for the new task
		int newId = 0;
		for (Task taskPick : MockTaskCache.getInstance().getCollection()) {
			int currentId = Integer.getInteger(taskPick.getId());
			if (newId < currentId) {
				newId = currentId;
			}
		}
		newId += 1;
		
		// Create the new task to add in the collection
		task.setId(Integer.toString(newId));
		task.setFolder(folder);
		
		collectionTask = (Task) task.clone();
		collectionTask.setFolder(collectionFolder);
		
		
		// Add the task to the temporary folder and the collection 
		collectionFolder.addTask(task);
		collectionFolder.addTask(collectionTask);
		MockTaskCache.getInstance().getCollection().add(collectionTask);
		
		// Send a notification to listeners
		for (TodoListener listener : this.listeners) {
			listener.onCreateTask(folder, task);
		}
	}

	/**
	 * Update a task
	 * 
	 * Call: onUpdateTask
	 * 
	 * @param the updated task
	 */
	public void updateTask(Task task) {
		Task collectionTask = null;
		
		// Get the task in the collection
		for (Task taskPick : MockTaskCache.getInstance().getCollection()) {
			if (taskPick.equals(task)) {
				collectionTask = taskPick;
				break;
			}
		}
		
		// Task does not exit
		if (collectionTask == null) {
			throw new IllegalArgumentException("Task does not exist in the database");
		}
		
		// Update the task content
		copyTask(task, collectionTask);
		
		// Send a notification to listeners
		for (TodoListener listener : this.listeners) {
			listener.onUpdateTask(task);
		}
	}

	/**
	 * Delete a task from the database
	 * 
	 * Call: onDeleteTask
	 * 
	 * @param id the id of the task to delete
	 */
	public void deleteTask(String id) {
		Task collectionTask = null;
		Folder collectionFolder = null;
		
		// Get the task in the collection
		for (Task taskPick : MockTaskCache.getInstance().getCollection()) {
			if (taskPick.getId().equals(id)) {
				collectionTask = taskPick;
			}
		}
		
		// Task does not exist
		if (collectionTask == null) {
			throw new IllegalArgumentException("Task does not exist in the database");
		}
		
		// Remove the task from the folder
		collectionFolder = collectionTask.getFolder();
		if (collectionFolder != null) {		
			for (Task taskPick : collectionFolder.getTasks()) {
				if (taskPick.equals(collectionTask)) {
					collectionFolder.removeTask(collectionTask);
					break;
				}
			}
		}
		
		// Remove the task from the collection
		boolean result = MockTaskCache.getInstance().getCollection().remove(collectionTask);
		
		// Send notification to listeners
		for (TodoListener listener : this.listeners) {
			listener.onDeleteTask(result);
		}
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
	 * Copy task properties to another.
	 * 
	 * @param src the source <code>Task</code>
	 * @param dest the destination <code>Task</code>
	 */
	private void copyTask(Task src, Task dest) {
		if (src == null) {
			throw new IllegalArgumentException("src parameter cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("dest parameter cannot be null");
		}
		
		dest.setTitle(src.getTitle());
		dest.setDescription(src.getDescription());
		dest.setCreationDate((Date) src.getCreationDate().clone());
		dest.setDueDate((Date) src.getDueDate().clone());
		dest.setStatus(src.getStatus());
		dest.setAddress(src.getAddress());
	}
}

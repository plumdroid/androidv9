package fr.unilim.javance.projet.internal;

import android.content.Context;

/**
 * Todo management interface
 * 
 * This interface must be implemented to use a specific backend.
 * In the "Java Avance" teaching unit, you will use real APIs 
 * to implement such a backend.
 * 
 * From the user interface point of view, you have to use 
 * this interface to interact with the backend.
 * 
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public interface TodoManager {
	/**
	 * Launch the authentication process
	 * 
	 * The <code>onAuthenticationRequest</code> method from the listener will be called
	 * to give the corresponding <code>Intent</code> if needed.
	 */
	public void launchAuthenticationProcess(Context c);
	
	/**
	 * Get the authentication status
	 * 
	 * The <code>onAuthenticationStatusChanged</code> method from the listener will 
	 * be called to obtain the current authentication status (<code>true</code> if
	 * the user is authenticated, <code>false</code> if not).
	 */
	public void getAuthenticationStatus();
	
	/**
	 * Get a folder by id
	 * 
	 * Retrieve a folder from a database or a cache using its id.
	 * 
	 * The <code>onGetFolder</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param id the id of the folder to retrieve
	 */
	public void getFolder(String id);
	
	/**
	 * Get all folders from the database
	 * 
	 * The <code>onGetAllFolder</code> method from the listener will be called at the end of the
	 * operation.
	 */
	public void getAllFolders();
	
	/**
	 * Fill or create a new folder
	 * 
	 * If the folder does not exist yet (the <code>Folder</code> instance in 
	 * parameter has a null or empty identifier), it will be created and uploaded
	 * in the remote folder collection. A new folder has just a name (there is no
	 * guarantee that initialized Task would be added and/or kept during this
	 * process.
	 * 
	 * Then, the task list in the folder will be filled with
	 * the correct values. Indeed, by default the folder database contains only
	 * references to tasks (for instance a reference by id).
	 * 
	 * To retrieve all <code>Task</code> instances corresponding to a
	 * <code>Folder</code> you must use this method.
	 * 
	 * The <code>onFillFolder</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param folder the folder to fill or create
	 */
	public void fillFolder(Folder folder);
	
	/**
	 * Delete the folder given in parameter
	 * 
	 * The folder would be deleted from the database and/or cache.
	 * 
	 * The <code>onDeleteFolder</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param folder the folder to delete
	 */
	public void deleteFolder(Folder folder);
	
	/**
	 * Get a task from the database by its id
	 * 
	 * The <code>onGetTask</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param id the id of the task to retrieve
	 */
	public void getTask(String id);
	
	/**
	 * Create a new task in the specified folder.
	 *  
	 * The <code>onCreateTask</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param folder the folder in which the task will be added
	 * @param task the task to add
	 */
	public void createTask(Folder folder, Task task);
	
	/**
	 * Update a task.
	 * 
	 * The <code>onUpdateTask</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param task the task with updated values
	 */
	public void updateTask(Task task);

	/**
	 * Delete a task from the database by its id
	 * 
	 * The <code>onDeleteTask</code> method from the listener will be called at the end of the
	 * operation.
	 * 
	 * @param id the id of the Task to delete
	 */
	public void deleteTask(String id);
	
	/**
	 * Register a new listener
	 * 
	 * A listener is called after each operation performed thanks to a TodoManager.
	 * 
	 * @param listener the listener to register
	 */
	public void registerListener(TodoListener listener);
	
	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove 
	 */
	public void removeListener(TodoListener listener);
}

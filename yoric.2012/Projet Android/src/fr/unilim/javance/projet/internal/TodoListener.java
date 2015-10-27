package fr.unilim.javance.projet.internal;

import java.util.Set;

import android.content.Intent;

/**
 * Listener on todo operations
 * 
 * This listener must be implemented if you want to
 * obtain notification on <code>TodoManager</code> operations.
 * 
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public interface TodoListener {
	/**
	 * Return an <code>Intent</code> tied to the authentication process 
	 * (if needed)
	 * 
	 * It is often an <code>Activity</code> to launch.
	 * 
	 * @param intent the corresponding <code>Intent</code>
	 */
	public void onAuthenticationRequest(Intent intent);
	
	/**
	 * Return the authentication status
	 * 
	 * @param status set to <code>true</code> if the authentication is successful,
	 *               else it is set to <code>false</code>.
	 */
	public void onAuthenticationStatusChanged(boolean status);
	
	/**
	 * Return the desired folder
	 * 
	 * @param folder the desired folder
	 */
	public void onGetFolder(Folder folder);
	
	/**
	 * Return all folders
	 * 
	 * @param folders the desired folders
	 */
	public void onGetAllFolders(Set<Folder> folders);
	
	/**
	 * Return the filled or created folder
	 * 
	 * @param folder the filled or created folder
	 */
	public void onFillFolder(Folder folder);
	
	/**
	 * Called after a folder deletion
	 * 
	 * @param done <code>true</code> if the operation is a success
	 */
	public void onDeleteFolder(boolean done);
	
	/**
	 * Return the desired task
	 * 
	 * @param task the desired task
	 */
	public void onGetTask(Task task);
	
	/**
	 * Return the created the task and its folder
	 * 
	 * @param folder the folder in which the task belongs
	 * @param task the created task
	 */
	public void onCreateTask(Folder folder, Task task);
	
	/**
	 * Return the updated task
	 * 
	 * @param task the updated task
	 */
	public void onUpdateTask(Task task);

	/**
	 * Called after a task deletion
	 * 
	 * @param done <code>true</code> if the operation is a success
	 */
	public void onDeleteTask(boolean done);
}

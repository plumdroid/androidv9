package fr.unilim.javance.projet.model.mock.cache;

import java.util.HashSet;
import java.util.Set;

import fr.unilim.javance.projet.internal.Task;

/**
 * Task cache
 * 
 * Task instances will be stored in this singleton.
 * It is really naive cache implementation, like with folders.
 * 
 * It uses an <code>HashSet</code> to store <code>Task</code>
 * instances.
 *  
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public class MockTaskCache {
	private static MockTaskCache instance;
	
	private HashSet<Task> collection;

	/**
	 * Private constructor (initialize the collection)
	 */
	private MockTaskCache() {
		this.collection = new HashSet<Task>();
	}
	
	/**
	 * Get the single instance of <code>MockTaskCache</code>
	 * 
	 * @return the instance of <code>MockTaskCache</code>
	 */
	public static MockTaskCache getInstance() {
		if (MockTaskCache.instance == null) {
			MockTaskCache.instance = new MockTaskCache(); 
		}
		return MockTaskCache.instance;
	}
	
	/**
	 * Get the <code>Task</code> collection
	 * 
	 * @return the <code>Task</code> collection
	 */
	public Set<Task> getCollection() {
		return this.collection;
	}
}

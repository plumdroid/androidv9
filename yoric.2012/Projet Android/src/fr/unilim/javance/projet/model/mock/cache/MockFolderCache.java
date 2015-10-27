package fr.unilim.javance.projet.model.mock.cache;

import java.util.HashSet;
import java.util.Set;

import fr.unilim.javance.projet.internal.Folder;

/**
 * Folder cache
 * 
 * Folder instances will be stored in this singleton.
 * It is really naive cache implementation: do not try to
 * imitate such an implementation, you cannot :-P
 * 
 * It uses an <code>HashSet</code> to store <code>Folder</code>
 * instances.
 *  
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public class MockFolderCache {
	private static MockFolderCache instance;
	
	private HashSet<Folder> collection;

	/**
	 * Private constructor (initialize the collection)
	 */
	private MockFolderCache() {
		this.collection = new HashSet<Folder>();
	}
	
	/**
	 * Get the single instance of <code>MockFolderCache</code>
	 * 
	 * @return the instance of <code>MockFolderCache</code>
	 */
	public static MockFolderCache getInstance() {
		if (MockFolderCache.instance == null) {
			MockFolderCache.instance = new MockFolderCache(); 
		}
		return MockFolderCache.instance;
	}
	
	/**
	 * Get the <code>Folder</code> collection
	 * 
	 * @return the <code>Folder</code> collection
	 */
	public Set<Folder> getCollection() {
		return this.collection;
	}
}

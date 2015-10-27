package fr.unilim.javance.projet.internal;

import java.util.HashMap;

import fr.unilim.javance.projet.model.gtasks.GTasksTodoManager;
import fr.unilim.javance.projet.model.mock.MockTodoManager;

/**
 * Todo manager factory 
 * 
 * This factory aims to instantiate <code>TodoManager</code> objects
 * corresponding to the required backend.
 *  
 * @author Amaury Gauthier
 * @author David Pequegnot
 */
public class TodoManagerFactory {
	private static HashMap<Backend, TodoManager> todoManagers = new HashMap<Backend, TodoManager>();
	
	/**
	 * Return the <code>TodoManager</code> instance corresponding 
	 * to the backend given in parameter.
	 * 
	 * Each attempt to obtain a <code>TodoManager</code> will result in
	 * a new instance. 
	 * 
	 * @param backend the backend to instantiate
	 * @return the corresponding <code>TodoManager</code> instance
	 * @see Backend
	 */
	public static TodoManager getTodoManager(Backend backend) {
		switch (backend) {
			case MOCK:
				if(!TodoManagerFactory.todoManagers.containsKey(backend)) {
					TodoManagerFactory.todoManagers.put(backend, new MockTodoManager());
				}

				return TodoManagerFactory.todoManagers.get(backend);
			case GTASKS:
				if(!TodoManagerFactory.todoManagers.containsKey(backend)) {
					TodoManagerFactory.todoManagers.put(backend, new GTasksTodoManager());
				}
				
				return TodoManagerFactory.todoManagers.get(backend);
			default:
				throw new IllegalArgumentException("Not yet implemented");
		}
	}
}

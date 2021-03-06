package fr.unilim.javance.projet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.unilim.javance.projet.internal.Backend;
import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TodoListener;
import fr.unilim.javance.projet.internal.TodoManager;
import fr.unilim.javance.projet.internal.TodoManagerFactory;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;


/**
 * @author Amaury Gauthier
 * @author David Pequegnot
 *
 */
public final class MainActivity extends ListActivity implements TodoListener {
	private List<String> items = new ArrayList<String>();
	private TodoManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Add all the different backend in the ListView
		for(Backend b : Backend.values()) {
			items.add(b.toString());
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, items));
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Retrieves the Backend
				Backend b = Backend.valueOf( ((TextView) view).getText().toString() );
				manager = TodoManagerFactory.getTodoManager(b);
				manager.registerListener(MainActivity.this);
				manager.launchAuthenticationProcess(MainActivity.this);
			}
		});
	}
	
	public List<String> getItems() {
		return items;
	}

	
	
	
	@Override
	public void onAuthenticationRequest(Intent intent) {
		startActivity(intent);
	}

	@Override
	public void onAuthenticationStatusChanged(boolean status) {
		if(status) {
			// User authenticated
			manager.getAllFolders();
		}
		else {
			Log.e("MainActivity", "Authentication failed");
		}
	}

	@Override
	public void onGetFolder(Folder folder) {
		List<String> tasks = new ArrayList<String>();
		for(Task t : folder.getTasks()) {
			tasks.add(t.getTitle());
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, tasks));
		
		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Context context = getApplicationContext();
				CharSequence text = Long.toString(id) + " " + lv.getItemAtPosition(position).toString();
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				manager.getFolder(lv.getItemAtPosition(position).toString());
			}
		});
	}

	@Override
	public void onGetAllFolders(Set<Folder> folders) {
		List<String> lesF = new ArrayList<String>();
		for(Folder f : folders) {
			lesF.add(f.getName());
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main, lesF));
		
		final ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				manager.getFolder(lv.getItemAtPosition(position).toString());
			}
		});
	}

	@Override
	public void onFillFolder(Folder folder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteFolder(boolean done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetTask(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateTask(Folder folder, Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateTask(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteTask(boolean done) {
		// TODO Auto-generated method stub
		
	}
}

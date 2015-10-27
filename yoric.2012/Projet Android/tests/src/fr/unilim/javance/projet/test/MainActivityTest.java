package fr.unilim.javance.projet.test;

import fr.unilim.javance.projet.MainActivity;
import android.app.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ArrayAdapter;

public final class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	ListActivity mainActivity;
	ArrayAdapter<String> items;

	public MainActivityTest() {
		super("fr.unilim.javance.projet.MainActivity", MainActivity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		mainActivity = getActivity();
		items = (ArrayAdapter<String>) mainActivity.getListAdapter();
	}

	public void testPreConditions() {
		assertNotNull(mainActivity);
		assertNotNull(items);
		assertTrue(items.getCount() == 2);
	}

	
	public void testAddItem() {
		mainActivity.runOnUiThread(new Runnable() {
			//@Override
			public void run() {
				items.add("Mrrrrriou?");
				assertTrue(items.getCount() == 3);
			}
		});
	}

	public void testDeleteItem() {
		mainActivity.runOnUiThread(new Runnable() {
			//@Override
			public void run() {
				String c = new String("Mrrrrriou?");
				items.add(c);
				assertTrue(items.getCount() == 3);
				items.remove(c);
				assertTrue(items.getCount() == 2);
			}
		});
	}
}

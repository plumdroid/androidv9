package fr.unilim.javance.projet.test;

import java.util.Calendar;
import java.util.Date;

import fr.unilim.javance.projet.internal.Folder;
import fr.unilim.javance.projet.internal.Tag;
import fr.unilim.javance.projet.internal.Task;
import fr.unilim.javance.projet.internal.TaskStatus;
import fr.unilim.javance.projet.sync.GTasksContentProvider;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.util.Log;

public class GTasksProviderTest extends ProviderTestCase2<GTasksContentProvider> {
	private Date creationDate;

	public GTasksProviderTest() {
		super(GTasksContentProvider.class, GTasksContentProvider.AUTHORITY);
		
		this.creationDate = Calendar.getInstance().getTime();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInsertTask() {
		Folder f = this.createNeeded();
		Object[] tasks = f.getTasks().toArray();
		Task t = (Task) tasks[0];
		
		try {
			Uri u = getProvider().insert(GTasksContentProvider.TASK_CONTENT_URI, t.makeContentValues());
			assertEquals(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "1"), u);
		} catch(IllegalArgumentException e) {
			assertEquals(null, "hi");
		} catch(SQLException e) {
			assertEquals(null, "hi");
		}
	}
	
	public void testQueryTask() {
		Cursor c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                "idTask"), null, null, null, null);
		assertEquals(0, c.getCount());
		
		Folder f = this.createNeeded();
		Object[] tasks = f.getTasks().toArray();
		Task t = (Task) tasks[0];
		
		try {
			Uri u = getProvider().insert(GTasksContentProvider.TASK_CONTENT_URI, t.makeContentValues());
			assertEquals(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "1"), u);
			
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(1, c.getCount());
            
            c.moveToFirst();
            t = Task.makeTask(c, f);
            assertEquals("idTask", t.getId());
            assertEquals("title", t.getTitle());
		} catch(IllegalArgumentException e) {
			assertEquals(null, "hi");
		} catch(SQLException e) {
			assertEquals(null, "hi");
		}
	}
	
	public void testUpdateTask() {
		Cursor c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                "idTask"), null, null, null, null);
		assertEquals(0, c.getCount());
		
		Folder f = this.createNeeded();
		Object[] tasks = f.getTasks().toArray();
		Task t = (Task) tasks[0];
		
		try {
			Uri u = getProvider().insert(GTasksContentProvider.TASK_CONTENT_URI, t.makeContentValues());
			assertEquals(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "1"), u);
			
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(1, c.getCount());
            
            c.moveToFirst();
            t = Task.makeTask(c, f);
            assertEquals(Tag.INSERT, t.getTag());
            
            t.setTag(Tag.UPDATE);
            t.setTitle("updated");
            
            int n = getProvider().update(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), t.makeContentValues(), null, null);
            assertEquals(1, n);
            
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(1, c.getCount());
            
            c.moveToFirst();
            t = Task.makeTask(c, f);
            assertEquals(Tag.UPDATE, t.getTag());
            assertEquals("updated", t.getTitle());
		} catch(IllegalArgumentException e) {
			assertEquals(null, "hi");
		} catch(SQLException e) {
			assertEquals(null, "hi");
		}
	}
	
	public void testDeleteTask() {
		Cursor c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                "idTask"), null, null, null, null);
		assertEquals(0, c.getCount());
		
		Folder f = this.createNeeded();
		Object[] tasks = f.getTasks().toArray();
		Task t = (Task) tasks[0];
		
		try {
			Uri u = getProvider().insert(GTasksContentProvider.TASK_CONTENT_URI, t.makeContentValues());
			assertEquals(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "1"), u);
			
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(1, c.getCount());
            
            c.moveToFirst();
            t = Task.makeTask(c, f);
            assertEquals(Tag.INSERT, t.getTag());
            
            t.setTag(Tag.DELETE);
            t.setTitle("deleted");
            
            int n = getProvider().update(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), t.makeContentValues(), null, null);
            assertEquals(1, n);
            
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(1, c.getCount());
            
            c.moveToFirst();
            t = Task.makeTask(c, f);
            assertEquals(Tag.DELETE, t.getTag());
            assertEquals("deleted", t.getTitle());
            
            n = getProvider().delete(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null);
            assertEquals(1, n);
            
			c = getProvider().query(Uri.withAppendedPath(GTasksContentProvider.TASK_CONTENT_URI,
                    "idTask"), null, null, null, null);
            assertEquals(0, c.getCount());
		} catch(IllegalArgumentException e) {
			assertEquals(null, "hi");
		} catch(SQLException e) {
			assertEquals(null, "hi");
		}
	}
	
	private Folder createNeeded() {
		return this.createFolder();
	}
	
	private Folder createFolder() {
		Folder f = new Folder();
		f.setId("idFolder");
		f.setName("name");
		f.setTag(Tag.INSERT);
		f.addTask(this.createTask(f));
		
		return f;
	}
	
	private Task createTask(Folder f) {
		Task t = new Task();
		t.setId("idTask");
		t.setTitle("title");
		t.setStatus(TaskStatus.PENDING);
		t.setFolder(f);
		t.setTag(Tag.INSERT);
		t.setCreationDate(this.creationDate);
		
		return t;
	}
}

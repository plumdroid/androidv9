package formation.exemple.helloplumdatabasetable1;

import plum.webservice.database.PlumDataBase;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HelloPlumWebServiceDataBase extends Activity implements View.OnClickListener
,AdapterView.OnItemClickListener{
	PlumDataBase webdata;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       //http://10.0.2.2/ ou http://packardbell-TH/
		webdata=new PlumDataBase("http://btssio.boonum.fr/plumwebservice/plumWebServiceDataBase.php5");
		listTable1();

		 Button monBouton=(Button) findViewById(R.id.ajouter);
	     monBouton.setOnClickListener(this);
    }
    
    @Override
	public void onClick(View v) {
    	Intent intent = new Intent(this,AddNewTable1.class);
    	startActivity(intent);
	}
    public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
		  Intent intent = new Intent(this,ModOrSupTable1.class);
	      intent.putExtra("formation.exemple.helloplumservicedatabase.id", id);
	      startActivity(intent);
	}
    protected void onRestart(){
    	super.onRestart();
    	listTable1();
    	}
    	
    
    public void listTable1(){
    	ListView lvTable1 = (ListView) findViewById(android.R.id.list);
    	Cursor c=webdata.query("select * from table1 order by nom");

        String[] from = new String[] { c.getColumnName(0),c.getColumnName(1) };
        int[] to = new int[] {R.id.id , R.id.nom,};
    	
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter liste =
        new SimpleCursorAdapter(this, R.layout.list_row, c, from, to);
        lvTable1.setAdapter(liste);
        
		lvTable1.setOnItemClickListener(this);
    }
}
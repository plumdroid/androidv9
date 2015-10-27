package formation.exemple.HelloPlumWebServiceDataBase;

import plum.webservice.database.PlumDataBase;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HelloPlumWebServiceDataBase extends Activity implements View.OnClickListener  {
	PlumDataBase webdata;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
  
		webdata=new PlumDataBase("http://10.0.2.2/plumwebservice/plumWebServiceDataBase.php");
		listTable1();
		
		 Button monBouton=(Button) findViewById(R.id.ajouter);
	     monBouton.setOnClickListener(this);
		//webdata.execute("insert into table1 value('','Mauricette')");
    }
    
    @Override
	public void onClick(View v) {
    	Intent i = new Intent(this, addTable1.class);
    	startActivity(i);
	}
    
    public void listTable1(){
    	ListView lvTable1 = (ListView) findViewById(android.R.id.list);
    	Cursor c=webdata.query("select * from table1 where nom='Thierrry' order by nom");

        String[] from = new String[] { c.getColumnName(0),c.getColumnName(1) };
        int[] to = new int[] { R.id.id,R.id.nom };
    	
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter liste =
        new SimpleCursorAdapter(this, R.layout.list_row, c, from, to);
        lvTable1.setAdapter(liste);
    }
}
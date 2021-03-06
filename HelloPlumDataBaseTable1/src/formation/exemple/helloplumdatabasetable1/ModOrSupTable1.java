package formation.exemple.helloplumdatabasetable1;

import plum.webservice.database.PlumDataBase;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class ModOrSupTable1 extends Activity implements View.OnClickListener {
	private PlumDataBase webdata;
	private long _id;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_or_sup_table1);
        
        Button buttonMod=(Button) findViewById(R.id.mmod);
        buttonMod.setOnClickListener(this);
        Button buttonSup=(Button) findViewById(R.id.msup);
        buttonSup.setOnClickListener(this);
        Button buttonclose=(Button) findViewById(R.id.mclose);
        buttonclose.setOnClickListener(this);
        
        _id=getIntent().getLongExtra("formation.exemple.helloplumservicedatabase.id",-1);
        //http://10.0.2.2/ ou http://packardbell-th/
        webdata=new PlumDataBase("http://btssio.boonum.fr/plumwebservice/plumWebServiceDataBase.php5");
        StringBuilder sqlb=new StringBuilder("Select nom from table1 where _id=");
        sqlb.append(_id);
        Cursor c =webdata.query(sqlb.toString());
        c.moveToNext();
        String nom=c.getString(0);
        EditText myText=(EditText)findViewById(R.id.mmytext);
        myText.setText(nom);  
    }
    
    public void onClick(View v) {
		EditText myText=(EditText)findViewById(R.id.mmytext);
		StringBuilder sqlb;
		String sql;
		switch (v.getId())
		{	case R.id.mmod : 
			sqlb=new StringBuilder("update table1 Set nom='");
			sqlb.append(myText.getText());sqlb.append("' where _id=");sqlb.append(_id);
			sql=sqlb.toString();
			webdata.execute(sql);
			Toast.makeText(getApplicationContext(), "Le nom a �t� modifi�",
			Toast.LENGTH_SHORT).show();
			break;
			case R.id.msup : 
			sqlb=new StringBuilder("delete from table1 where _id=");
			sqlb.append(_id);
			sql=sqlb.toString();
			webdata.execute(sql);
			Toast.makeText(getApplicationContext(), "Le nom a �t� supprim�",
			Toast.LENGTH_SHORT).show();
			finish();
				break;
			case R.id.mclose :
				finish();
				break;
		}		
	}
    
}
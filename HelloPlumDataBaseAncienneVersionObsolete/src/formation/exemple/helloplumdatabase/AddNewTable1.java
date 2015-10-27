package formation.exemple.helloplumdatabase;
import formation.exemple.helloplumservicedatabase.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import plum.webservice.database.PlumDataBase;
public class AddNewTable1 extends Activity implements View.OnClickListener {
	PlumDataBase webdata;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtable1);
        
        /** écouteur */
        Button buttonOk=(Button) findViewById(R.id.ok);
        buttonOk.setOnClickListener(this);
        Button buttonclose=(Button) findViewById(R.id.close);
        buttonclose.setOnClickListener(this);
        webdata=new PlumDataBase("http://btssio.boonum.fr/plumwebservice/plumWebServiceDataBase.php5");
    }
    
    public void onClick(View v) {
		EditText myText=(EditText)findViewById(R.id.mytext);
		
		switch (v.getId())
		{	case R.id.ok : 
			StringBuilder sqlb=new StringBuilder("insert into table1 values('','");
			sqlb.append(myText.getText());
			sqlb.append("')");
			String sql=sqlb.toString();
			
			webdata.execute(sql);
			Toast.makeText(getApplicationContext(), "Le nom a été ajouté",
			Toast.LENGTH_SHORT).show();
			myText.setText("");
				break;
			case R.id.close :
				finish();
				break;
		}
		
	}
    
}
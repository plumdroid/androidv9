package plumoo.citation.trois.cent.soixante.cinq;

import plum.webservice.database.PlumDataBase;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView;

public class main extends Activity implements View.OnClickListener {
	
	private PlumDataBase webdata;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
   
    /** écouteur */
	Button buttonAll=(Button) findViewById(R.id.btnall);
	buttonAll.setOnClickListener(this);
	
	webdata=new PlumDataBase
	("http://www.maison.boonum.fr/365/plumwebservice/plumWebServiceDataBase.php5");

	StringBuilder sqlb=new StringBuilder("select titre,citation,datecitation,nomouvrage,nomauteur ");
	sqlb.append("from b365_citation,b365_ouvrage,b365_auteur ");
	sqlb.append("where idouvrage=b365_ouvrage._id and idauteur=b365_auteur._id order by datecitation");
	//StringBuilder sqlb=new StringBuilder("select * from b365_citation");
	Cursor c =webdata.query(sqlb.toString());
	
	c.moveToNext();
	
	String date=c.getString(2);
	String titre=c.getString(0);
	String citation=c.getString(1);
	String ouvrage=c.getString(3);
	String auteur=c.getString(4);
	
	TextView txtdate=(TextView)findViewById(R.id.txtdate);
	TextView txttitre=(TextView)findViewById(R.id.txttitre);
	TextView txtcitation=(TextView)findViewById(R.id.txtcitation);
	TextView txtouvrage=(TextView)findViewById(R.id.txtouvrage);
	TextView txtauteur=(TextView)findViewById(R.id.txtauteur);
	
	txtdate.setText(date);
	txttitre.setText(titre);
	txtcitation.setText(citation);
	txtouvrage.setText(ouvrage);
	txtauteur.setText(auteur);
	
	
    }
    
    public void onClick(View v){
    	//appel liste    
    }
}

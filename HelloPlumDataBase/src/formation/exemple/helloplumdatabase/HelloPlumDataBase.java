package formation.exemple.helloplumdatabase;

import plum.webservice.norest.*;
import plum.widget.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import formation.exemple.helloplumdatabase.R;
import android.database.Cursor;

public class HelloPlumDataBase extends Activity 
 implements View.OnClickListener
{
	
	PlumDataBase webdata; 
	
	Button button_execute;
	Button button_query;
	
	EditText edit_sql;
	EditText edit_data;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //http://10.0.2.2/plum/plum.webservice/PlumWebServiceDatabase/www/e/plumwebservice/
       //http://formation.lyc�e.boonum.fr/plum/plum.webservice/PlumWebServiceDatabase/www/e/plumwebservice/
        //http://10.0.3.2/plum/plum.webservice/PlumWebServiceDatabase/www/e/plumwebservice/
        //http://maison.boonum.fr/plum/PlumWebServiceDatabase/www/e/plumwebservice/
       // http://www.boursedirect.fr/
      	webdata=new PlumDataBase("http://10.0.2.2/plum/plum.webservice/PlumWebServiceDb/www/e/norest/");
      	
      	//-- Contact --
      	if(contact()==false) {return;}
      	
      	Toast.makeText(getApplicationContext(),"Contact ok", Toast.LENGTH_SHORT).show();
      	
      	//--Authentification --
      	if(authentification()==false) {return;}
      	
      	Toast.makeText(getApplicationContext(),"Authentification ok", Toast.LENGTH_SHORT).show();
      	
      	//--Champs
      	edit_sql=(EditText)findViewById(R.id.edit_sql);
      	edit_data=(EditText)findViewById(R.id.edit_data);
      	
      	button_execute=(Button)findViewById(R.id.button_execute);
      	button_execute.setOnClickListener((OnClickListener) this);
      	
      	//query
      	button_query=(Button)findViewById(R.id.button_query);
      	button_query.setOnClickListener((OnClickListener) this);
    }
    
  private boolean contact(){
	  String origine="CONTACT:";
	  
	  PlumDataBaseReponse pr=null;
	  
	  try{
    		pr=webdata.contact();
    	}catch(PlumDataBaseException e){
    		MessageDialog.show(this,origine+e.toString(),"Fermer");
    		return false;
    	} 	
    	
    	if(pr.etat!=0){
    		MessageDialog.show(this,origine+pr.message,"Fermer");
    		return false;
    	}
	   
    	return true;
  }
  
  private boolean authentification(){
	  String origine="AUTHENTIFICATION:";
	  
	  PlumDataBaseReponse pr=null;
	  
	  try{
    		pr=webdata.authentification("admin","admin");
    	}catch(PlumDataBaseException e){
    		MessageDialog.show(this,origine+e.toString(),"Fermer");
    		return false;
    	} 	
    	
    	if(pr.etat!=0){
    		MessageDialog.show(this,origine+pr.message,"Fermer");
    		return false;
    	}
	   
    	return true;
  }

public void onClick(View v) {
	// TODO Auto-generated method stub
	PlumDataBaseReponse pr=null;
	
	String sql;
	String data;
	
	sql=edit_sql.getText().toString();
	data=edit_data.getText().toString();
	
	String[] dataT = data.split(";");
	
	
	switch(v.getId()){
	
	case R.id.button_execute:
		
		try{
    		pr=webdata.execute(sql , dataT);
    	}catch(PlumDataBaseException e){
    		MessageDialog.show(this,e.toString(),"Fermer");
    		return;
    	} 	
		
		if(pr.pdo.error==0){
			
			Toast.makeText(getApplicationContext(),"::Execute OK::"
					+ "::SQL="+sql
					+ "::rowCount="+pr.pdo.rowCount, 
					Toast.LENGTH_LONG).show();
		}
		else{
			MessageDialog.show(this,"::Execute ERREUR::"
					+ "::SQL="+sql
					+ "::errorInfo="+pr.pdo.errorInfo
			,"Fermer");
		}
		break;
		
	case R.id.button_query:

        try{
        	pr=webdata.query(sql , dataT);
        }catch(PlumDataBaseException e){
        	MessageDialog.show(this,e.toString(),"Fermer");
        	return;
        } 	

        //erreur sur la requ�te SQL ?
        if(pr.pdo.error!=0){
        	MessageDialog.show(this,"::Query ERREUR::"
        			+ "::SQL="+sql
        			+ "::errorInfo="+pr.pdo.errorInfo
        			,"Fermer");

          break;
        }

        //Aucune ligne dans la table ?	
        if(pr.pdo.rowCount==0){
        	Toast.makeText(getApplicationContext(),
        			"QUERY : table vide", Toast.LENGTH_LONG).show();
        	break;
        }

        //afficher la liste
        Cursor c=pr.pdo.cursor;

	    String[] from = new String[] { c.getColumnName(0),c.getColumnName(1) };
	    int[] to = new int[] {R.id.id , R.id.lib,};
	
	    // Now create an array adapter and set it to display using our row
	    SimpleCursorAdapter liste =
	    new SimpleCursorAdapter(this, R.layout.row, c, from, to);
	
	    ListView listview_test = (ListView) findViewById(android.R.id.list);
	    listview_test.setAdapter(liste);
	
	    break;
	}
}
  
}
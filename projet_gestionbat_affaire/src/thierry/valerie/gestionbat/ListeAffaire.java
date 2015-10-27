package thierry.valerie.gestionbat;

import java.util.Formatter;

import plum.util.PlumCalendarFr;
import plum.webservice.database.PlumDataBase;
import android.app.Activity;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListeAffaire extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //getCursorAffaire();
        //setContentView(R.layout.mod_sup_affaire);
        //getCursorContact();
        setContentView(R.layout.ajouter_contact);
        PlumDataBase db;
        PlumCalendarFr mydate=new PlumCalendarFr();
        /*int mois=mydate.get(Calendar.MONTH);
        int annee=mydate.get(Calendar.YEAR);
        int jour=mydate.get(Calendar.DAY_OF_MONTH);
        String datej=Integer.toString(jour)+"/"+Integer.toString(mois)+"/"+Integer.toString(annee);
      	*/
        
        String datej=mydate.toHuman();
       
        String datesql=mydate.toMySql();
        TextView mytext =(TextView) findViewById(R.id.acnomsoc);
      	TextView mytext2 =(TextView) findViewById(R.id.accontact);
      	//Calendar.get(Calendar.DATE)
      	mytext.setText(datej);
        mytext2.setText(datesql);
    }
    private void getCursorContact(){
    	
		String[] column={"_id","nomsoc","contact","tel"};
		MatrixCursor cursor=new MatrixCursor(column);
		
		for(int i=0;i<3;i++)
		{	String[] row={"1","SCI truc","M. Jean","0555376655"};
			cursor.addRow(row);
		}
		 ListView lvTable1 = (ListView) findViewById(android.R.id.list);

	        String[] from = new String[] { cursor.getColumnName(1),cursor.getColumnName(2),
	        		cursor.getColumnName(3) };
	        int[] to = new int[] {R.id.liconomsoc,R.id.licocontact , R.id.licotel};
	    	
	        // Now create an array adapter and set it to display using our row
	        SimpleCursorAdapter liste =
	        new SimpleCursorAdapter(this, R.layout.liste_item_contact, cursor, from, to);
	        lvTable1.setAdapter(liste);
    }
    private void getCursorAffaire(){
    	
		String[] column={"_id","datea","typaf","prix","ville","adr"};
		MatrixCursor cursor=new MatrixCursor(column);
		
		for(int i=0;i<30;i++)
		{	String[] row={"1","21/03/2011","Maison","12000€",
				"Limoges","rue des papillons"};
			cursor.addRow(row);
		}
		 ListView lvTable1 = (ListView) findViewById(android.R.id.list);

	        String[] from = new String[] { cursor.getColumnName(1),cursor.getColumnName(2),
	        		cursor.getColumnName(3),cursor.getColumnName(4),cursor.getColumnName(5) };
	        int[] to = new int[] {R.id.liadatea,R.id.liatypeaf , R.id.liaprix,
	        		R.id.liaville,R.id.liaadr};
	    	
	        // Now create an array adapter and set it to display using our row
	        SimpleCursorAdapter liste =
	        new SimpleCursorAdapter(this, R.layout.liste_item_affaire, cursor, from, to);
	        lvTable1.setAdapter(liste);
    }
}
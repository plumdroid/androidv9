package formation.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.Toast;

public class ComboDialog implements DialogInterface.OnClickListener{
	private CharSequence items[], values[];
	private int indexSelected; private int compteur;
	private Context context;
	
	public ComboDialog (String title,Context context,CharSequence items[], CharSequence values[]) {
		this.items=items; this.values=values;this.context=context;
		this.indexSelected=-1; this.compteur=0;
		
		//AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, -1,this);
		}
	
	public void show(){;}
	
	public int getIndexSelected() {return -1;
	}
	
	public int getCompteur() { return 0;
	
	}
	
	public CharSequence item(int index) { return "";
	
	}
	
	public CharSequence value(int index) { return "";
	
	}
	
	public void selected(int index){;}
	
	//// évènements ////
	public void onClick(DialogInterface dialog, int item) {
		 Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();}
	

}

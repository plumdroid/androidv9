package plum.widget;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.widget.TextView;
import android.view.View;

public class ComboDialog implements View.OnClickListener,
DialogInterface.OnClickListener{
	private CharSequence items[], values[];
	private int indexSelected; 
	private OnClickComboDialogListener callback;
	private AlertDialog alerte;private TextView view;
	
	public ComboDialog (String title, CharSequence items[], CharSequence values[],
			TextView view, Context context) {
		this.items=items; this.values=values;
		this.callback=null;
		this.indexSelected=-1;this.view=view;
		
		//AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		//builder.setItems(items,this);
		builder.setSingleChoiceItems(items,indexSelected, this);
		alerte=builder.create();
		
		view.setOnClickListener(this);
		}
	
	public void show(){alerte.show();}
	
	public int getIndexSelected() {return indexSelected;}
	
	public int getCompteur() { return items.length;}
	
	public CharSequence item(int index) { return items[index];}
	
	public CharSequence value(int index) { return values[index];}
	
	public void selected(int index){indexSelected=index;}
	
	public void setOnClickComboDialogListener(OnClickComboDialogListener onClickListener)
	{ callback=onClickListener;}
	
	//// ------ évènements ////
	public void onClick(DialogInterface dialog, int item) {
		this.indexSelected=item;
		alerte.cancel();
		view.setText(items[item]);
		if (callback!=null) {callback.onClickComboDialog();}
	}
	public void onClick(View v) {
		this.show();
	}
		
	/// ----- interface Listener : public static interface Listener  ////	
	public static interface  OnClickComboDialogListener {
		public abstract void onClickComboDialog();}
}

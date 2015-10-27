package formation.exemple.hellocombodialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import formation.exemple.R;
import plum.widget.ComboDialog;

public class HelloComboDialog extends Activity implements
ComboDialog.OnClickComboDialogListener {
    /** Called when the activity is first created. */
	ComboDialog comboCouleur;
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** mise en lace du combo sur un TextView*/
        TextView myItem=(TextView)findViewById(R.id.item);
        final CharSequence[] items={"Rouge","Vert","Bleu"};
    	final CharSequence[] values={"1","2","3"};
    	comboCouleur = new ComboDialog("Choisir une couleur",items,values,myItem,this);
    	//activiv� l'�couteur OnClick
    	comboCouleur.setOnClickComboDialogListener(this);
    }
    
    //// Ev�nements ////
    public void onClickComboDialog()
    {	
		TextView myValue=(TextView)findViewById(R.id.value);
		myValue.setText(comboCouleur.value(comboCouleur.getIndexSelected()));
    }
}
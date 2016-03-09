package formation.exemple.hellocombodialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import formation.exemple.hellocombodialog.R;
import plum.widget.ComboDialog;

public class HelloComboDialog extends Activity implements
ComboDialog.OnClickComboDialogListener {
    /** Called when the activity is first created. */
	ComboDialog comboCouleur;
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** mise en lace du combo sur un TextView*/
        /** Le 'ComboDialog' affichera dans TextView le libell� (item) choisi par l'utilisateur*/
        TextView myTextViewItem=(TextView)findViewById(R.id.item);
        
        final CharSequence[] items={"Rouge","Vert","Bleu"};
    	final CharSequence[] values={"1","2","3"};
    	comboCouleur = new ComboDialog("Choisir une couleur",items,values,myTextViewItem,this);
    	
    	//activiv� l'�couteur OnClick
    	comboCouleur.setOnClickComboDialogListener(this);
    	
    }
    
    //// Ev�nements ////
    //sur choix dans ComboDialog on affiche l'id (value) correspondant au choix r�alis� 
    public void onClickComboDialog()
    {	
		TextView myTextViewValue=(TextView)findViewById(R.id.value);
		myTextViewValue.setText(comboCouleur.value(comboCouleur.getIndexSelected()));
    }
}
package formation.exemple.hellostartactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends Activity 
	implements View.OnClickListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Récupérer les données fournies à l'Activity par putExtra
        String text=getIntent().getStringExtra("formation.exemple.hellostartactivity.texte");
        
        int val=getIntent().getIntExtra("formation.exemple.hellostartactivity.valeur", -1);
        
        //Affichage sur le formulaire
        setContentView(R.layout.display_activity);
        
        TextView myTexte=(TextView)findViewById(R.id.mytexte);
        myTexte.setText(text+" ("+val+")");
        
        //onClick "Fermer"
        Button buttonClose=(Button) findViewById(R.id.close);
        buttonClose.setOnClickListener(this);
        
       
        
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	} 
}
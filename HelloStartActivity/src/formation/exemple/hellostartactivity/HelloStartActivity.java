package formation.exemple.hellostartactivity;
 
 
import formation.exemple.hellostartactivity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
 
public class HelloStartActivity extends Activity 
implements View.OnClickListener {
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.main);
 
        Button buttonStart=(Button) findViewById(R.id.startActivity);
        Button buttonClose=(Button) findViewById(R.id.close);
 
        buttonStart.setOnClickListener(this);
        buttonClose.setOnClickListener(this);
    }
 
    public void onClick(View v) {
 
	switch (v.getId()){
	
      case R.id.startActivity : 
 
		EditText editTexte=(EditText)findViewById(R.id.editTexte);
		String myTexte=editTexte.getText().toString();
 
		int myValeur=10;
 
		//créer une instance de "DisplayActivity"
		Intent intent = new Intent(this,DisplayActivity.class);
 
		//données fournies à l'Activity "DisplayActivity"
	    intent.putExtra("formation.exemple.hellostartactivity.texte", myTexte);//un texte
		intent.putExtra("formation.exemple.hellostartactivity.valeur", myValeur);//un nombre
 
	    startActivity(intent);//Démarrer l'activity DisplayActivity
 
	    break;
 
      case R.id.close:
    	  finish();
    	  break;
	}	 
    }
}
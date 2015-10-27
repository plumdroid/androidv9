package formation.exemple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import formation.widget.MessageDialog;

public class HelloMessageDialog extends Activity implements 
View.OnClickListener {
    /** Called when the activity is first created. */
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
    	/** écouteur */
        Button monBouton=(Button) findViewById(R.id.button1);
        monBouton.setOnClickListener(this);
    }
    
    //// Evènements ////
    public void onClick(View v) {
		MessageDialog.show(this,"Bonjour Thierry","Fermer");
		int a=1;
		}
}
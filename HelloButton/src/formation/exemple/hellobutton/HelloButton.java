package formation.exemple.hellobutton;

import formation.exemple.hellobutton.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloButton extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** �couteur */
        Button monBouton=(Button) findViewById(R.id.button1);
        monBouton.setOnClickListener(this);
    }
    
    public void onClick(View v) {
		TextView textview1=(TextView)findViewById(R.id.textview1);
		textview1.setText("Bonjour Thierry");
	}
}
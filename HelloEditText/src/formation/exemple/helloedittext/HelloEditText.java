package formation.exemple.helloedittext;

import formation.exemple.helloedittext.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

public class HelloEditText extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** écouteur */
        Button buttonOk=(Button) findViewById(R.id.ok);
        buttonOk.setOnClickListener(this);
        Button buttonCancel=(Button) findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(this);
    }
    
    public void onClick(View v) {
		EditText myText=(EditText)findViewById(R.id.mytext);
		TextView myAffiche=(TextView)findViewById(R.id.affiche);
		switch (v.getId())
		{	case R.id.ok : 
				myAffiche.setText(myText.getText());
				break;
			case R.id.cancel :
				myAffiche.setText("");
				myText.setText("");
				break;
		}
		
	}
    
}
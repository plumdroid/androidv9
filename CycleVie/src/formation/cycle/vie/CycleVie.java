package formation.cycle.vie;

import plum.widget.MessageDialog;
import android.app.Activity;
import android.os.Bundle;

public class CycleVie extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//AlertDialog ?? différence avec MessageDialog ?
        MessageDialog.show(this,"CREATE","Fermer");
    }
}
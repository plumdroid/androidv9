package formation.exemple.hellodatepicker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import formation.exemple.hellodatepicker.R;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
//// main ///
public class HelloDatePick extends Activity implements View.OnClickListener,
DatePickerDialog.OnDateSetListener{
		private TextView dateDisplay;
	    private int mYear;
	    private int mMonth;
	    private int mDay;

	    static final int DATE_DIALOG_ID = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // capture our View elements
        dateDisplay = (TextView) findViewById(R.id.dateDisplay);

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        
        //set �v�nement click sur TextView
        dateDisplay.setOnClickListener(this);
    }
    
    // updates the date in the TextView
    private void updateDisplay() {
        dateDisplay.setText(
        new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mDay).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mYear).append(" "));
    }
    
    //// �v�nement TextView ///
    public void onClick(View v) {
    	//showDialog(0);
    	 new DatePickerDialog(this,
                 this,
                 mYear, mMonth, mDay).show();
    }
    
    //// �v�nement sur DatePickerDialog
    public void onDateSet(DatePicker view, int year, 
            int monthOfYear, int dayOfMonth) {
    		mYear = year;
    		mMonth = monthOfYear;
    		mDay = dayOfMonth;
    		updateDisplay();
    }
}
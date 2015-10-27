package formation.exemple.hellolistview;

import formation.exemple.hellolistview.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HelloListView extends ListActivity implements AdapterView.OnItemClickListener {
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.main);
	  
	  String[] countries=getCountries();
	  ArrayAdapter<String> listCountries=new ArrayAdapter<String>(this, R.layout.list_item, countries);
	  setListAdapter(listCountries);

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  /** écouteur */
      lv.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
		// When clicked, show a toast with the TextView text
		  TextView item=(TextView) v;
		  CharSequence texte=item.getText();
	      Toast.makeText(getApplicationContext(), texte,
	      Toast.LENGTH_SHORT).show();
	}
	
	private String[] getCountries()
	  { return  new String[] {
		    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
		    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
		    "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
		    "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
		    "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
		    "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
		    "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
		    "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
		    "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
		    "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
		    "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
		    "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
		    "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
		    "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
		    "Former Yugoslav Republic of Macedonia", "France"};
	
	}
}
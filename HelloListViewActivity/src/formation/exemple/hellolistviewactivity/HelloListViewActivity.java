package formation.exemple.hellolistviewactivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HelloListViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        String[] countries=getCountries();
        ListView mRecetteList = (ListView) findViewById(android.R.id.list);          	  
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item,countries);
    	mRecetteList.setAdapter(adapter);

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
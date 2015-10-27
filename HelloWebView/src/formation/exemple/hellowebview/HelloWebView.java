package formation.exemple.hellowebview;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelloWebView extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	
	   WebView  mWebView = (WebView) findViewById(R.id.webview);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    String url;
	    //url="www.google.com";
	    //url="http://10.0.2.2/plum/plum.webservice/PlumWebServiceDb/www/";
	    url="http://10.0.2.2/plum/plum.webservice/PlumWebServiceDb/www/e/debug.norest/";
	    mWebView.loadUrl(url);
	   
}
}
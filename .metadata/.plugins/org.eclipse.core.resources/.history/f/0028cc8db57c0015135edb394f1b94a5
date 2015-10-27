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
	    //http://www.google.comhttp://10.0.2.2/plumwebservice/plumWebServiceDataBase.php
	    mWebView.loadUrl("http://10.0.2.2/plumwebservice/plumWebServiceDataBase.php");
}
}
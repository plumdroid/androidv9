package fr.unilim.javance.projet.httpconnection;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.util.Log;

import fr.unilim.javance.projet.authentication.GoogleAccountHandler;

/**
 * This class contains functions to simplify http connections
 * 
 * @author Yorick Lesecque
 * @author Thibault Desmoulins
 */
public class HttpConnection {
	public static String sendURL(String type, String URL, List<NameValuePair> infos, GoogleAccountHandler gaHandler) 
			throws ClientProtocolException, IOException, JSONException {
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		
		if(type.compareTo("POST") == 0) {
			HttpPost httppost = new HttpPost(URL);
			
			for(NameValuePair paire : infos) {
				if(paire.getName() != "JSON") {
					httppost.addHeader(paire.getName(), paire.getValue());
				} else {
					httppost.setEntity(new StringEntity(paire.getValue()));
				}
			}
			response = httpclient.execute(httppost);
		}
		else if(type.compareTo("GET") == 0) {
			HttpGet httpget = new HttpGet(URL);
			
			for(NameValuePair paire : infos) {
				httpget.addHeader(paire.getName(), paire.getValue());
			}
			
			response = httpclient.execute(httpget);
		}
		else if(type.compareTo("PUT") == 0) {
			HttpPut httpput = new HttpPut(URL);
			
			for(NameValuePair paire : infos) {
				if(paire.getName() != "JSON") {
					httpput.addHeader(paire.getName(), paire.getValue());
				} else {
					httpput.setEntity(new StringEntity(paire.getValue()));
				}
			}
			response = httpclient.execute(httpput);
		}
		else if(type.compareTo("DELETE") == 0) {
			HttpDelete httpdelete = new HttpDelete(URL);
			
			for(NameValuePair paire : infos) {
				httpdelete.addHeader(paire.getName(), paire.getValue());
			}
			response = httpclient.execute(httpdelete);
		}
		
		StatusLine sLine = response.getStatusLine();
		if(sLine.getStatusCode() == 401) {
			Log.d("HttpConnection", "Status code: 401");
			gaHandler.gotAccount(true);
		}
		
		HttpEntity entity     = response.getEntity();
		String responseString = EntityUtils.toString(entity);
		
		return responseString;
	}
}

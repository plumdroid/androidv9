package plum.webservice.database;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

public class PlumDataBase {
	
		private String url;
		
	public PlumDataBase(String url){
		
		this.url=new String(url);
	}
	
	/*
	 * Contacter le webservice
	 *
	 * @return 
	 */
	public PlumDataBaseReponse contact() throws PlumDataBaseException  {
		
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		
		String http=url+"contact/init/";
		JSONObject jsonReponse=httpWebService(http,params);
		
		return new PlumDataBaseReponse(jsonReponse);
	}
	
	/*
	 * Authentification utilisateur
	 *
	 * @return 
	 */
	public PlumDataBaseReponse authentification(String user, String password) throws PlumDataBaseException  {
		
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("user",user));
		 params.add(new BasicNameValuePair("password",password));
		
		String http=url+"authentification/connecter/";
		JSONObject jsonReponse=httpWebService(http,params);
		
		return new PlumDataBaseReponse(jsonReponse);
	}
	
	/*
	 * Execute SQL
	 *
	 * @return 
	 */
	public PlumDataBaseReponse execute(String sql) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));
		 
		 String http=url+"webservice/execute/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
		 
	}

	
	/*
	 * Query SQL on retourne également la liste des données lues
	 *
	 * @return 
	 */
	public PlumDataBaseReponse query(String sql) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));
		 
		 String http=url+"webservice/query/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
	}
	
	
	 
	private Cursor formatPlumToCursor(JSONObject response)
	{
		MatrixCursor cursor=null;
		String line=null;
		/*try {
			  BufferedReader reader =  new BufferedReader(new InputStreamReader(response,
			  "iso-8859-1"),8);
			  line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("getDataFormat",e.toString());
		}
		if(line==null) {return new MatrixCursor(new String[]{"_id"});}
		char[] charline=line.toCharArray();
		String[] column=getColumnFromWebService(charline,THE_NAME);
		cursor=new MatrixCursor(column);
		
		String[] row=getColumnFromWebService(charline,THE_VALUE);
		while (row!=null)
		{	cursor.addRow(row);row=getColumnFromWebService(charline,THE_VALUE);}*/
		return  cursor;
	}
	
	
	private JSONObject httpWebService(String http,ArrayList<NameValuePair> params) throws PlumDataBaseException {
		//-- appel webservice  --
		InputStream is=null;
		
		
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(http);
		        httppost.setEntity(new UrlEncodedFormEntity(params));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        //à la place de is : String str=EntityUtils.toString(entity);
		}catch(Exception e){
			throw new PlumDataBaseException("Error in http connection "+e.toString(),http,"");
		}
		
		//-- lecture réponse --
		String line="";
		try {
			  BufferedReader reader =  new BufferedReader(new InputStreamReader(is,
			  "iso-8859-1"),8);
			  line = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new PlumDataBaseException("Error in reader url "+e.toString(),http,"");
		}
		
		try{
			JSONObject jsonReponse= new JSONObject(line);
			return jsonReponse;
		}catch(JSONException e){
			throw new PlumDataBaseException("Error create JSONObject "+e.toString(),http,line);			
		}
	}
}

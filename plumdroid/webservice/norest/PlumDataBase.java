package plumandroid.webservice.norest;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownServiceException;
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
		
		String http=url+"contact/hello/";//url+"contact/hello/";//"http://www.fnac.com/";//
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
	
	/**
     * Execute SQL
     *
     *
     * @param sql	 					Une requête sql
     * @return 							objet PlumDataBaseReponse
     */
	public PlumDataBaseReponse execute(String sql) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));
		 
		 
		 String http=url+"webservice/execute/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
		 
	}
	
	/**
     * Execute SQL
     *
     *
     * @param sql	 					Une requête sql avec jeton '?' ; par exemple "insert into table VALUES(?,?)"
	 * @param data     					un tableau avec les données remplaçant chaque jeton
     * @return 							objet PlumDataBaseReponse
     */
	
	public PlumDataBaseReponse execute(String sql, String[] data) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));
		 
		 int i=0;
		 for(String unedata : data){
			 String key="data[" + i + "]"; //data[0]....
			 params.add(new BasicNameValuePair(key,unedata));
			 i++;
		 }
		 
		 String http=url+"webservice/execute/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
		 
	}

	
	/**
     * Query SQL on retourne également la liste des données lues
     *
     *
     * @param sql	 					Une requête sql
     * @return 							objet PlumDataBaseReponse
     */
	public PlumDataBaseReponse query(String sql) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));
		 
		 String http=url+"webservice/query/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
	}
	
	/**
     * Query SQL on retourne également la liste des données lues
     *
     *
     * @param sql	 					Une requête sql avec jeton '?' ; par exemple "select * from table where id=?"
	 * @param data     					un tableau avec les données remplaçant chaque jeton
     * @return 							objet PlumDataBaseReponse
     */
	public PlumDataBaseReponse query(String sql, String[] data) throws PlumDataBaseException {
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("requete",sql));

		 int i=0;
		 for(String unedata : data){
			 String key="data[" + i + "]"; //data[0]....
			 params.add(new BasicNameValuePair(key,unedata));
			 i++;
		 }
		 
		 String http=url+"webservice/query/";
		 JSONObject jsonReponse=httpWebService(http,params);
			
		 return new PlumDataBaseReponse(jsonReponse);
	}
	
	/*
	 * Acces HTTP
	 * 
	 * return JSONObject
	 */
	private JSONObject httpWebService(String urlwebservice,ArrayList<NameValuePair> params) throws PlumDataBaseException {
		//-- appel webservice  --
		HttpURLConnection http;
	
		
		try{
			URL url=new URL(urlwebservice);
			
			http = (HttpURLConnection) url.openConnection();

			http.setConnectTimeout(10000);
			http.setRequestMethod("POST");
			http.setDoInput(true);
			http.setDoOutput(true);
			
			http.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded");
			
			//send POST
			
			if(params!=null && params.size()>0){
				
				String et="";
				String paramPost="";
				for(int p=0;p<params.size();p++){
					paramPost+=et+params.get(p).getName()+"="+params.get(p).getValue();
					et="&";
				}
				
				http.setFixedLengthStreamingMode(paramPost.getBytes().length);
				
				PrintWriter out = new PrintWriter(http.getOutputStream());
		        out.print(paramPost);
	            out.close();
			}
     		
		} catch (MalformedURLException e) {
			throw new PlumDataBaseException("Error in http connection URL malformed:"+e.toString(),urlwebservice,"");
	    } catch (SocketTimeoutException e) {
	    	throw new PlumDataBaseException("Error in http connection timeout:"+e.toString(),urlwebservice,"");   
	    } catch (IOException e) {
	    	throw new PlumDataBaseException("Error in http connection io:"+e.toString(),urlwebservice,"");
	    } finally {
	        /*if (urlConnection != null) {
	            urlConnection.disconnect();*/
	    }
		       /*HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(http);
		        httppost.setEntity(new UrlEncodedFormEntity(params));
		       
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();*/
		        //à la place de is : String str=EntityUtils.toString(entity);
		/*}catch(Exception e){
			throw new PlumDataBaseException("Error in http connection "+e.toString(),http,"");
		}*/
		
		//-- lecture réponse --
		String line="";
		try {			
			  InputStream is=http.getInputStream();
			  InputStreamReader in= new InputStreamReader(is,"iso-8859-1");
			  BufferedReader reader =  new BufferedReader(in,255);
			  line = reader.readLine();
			  
		} catch (UnknownServiceException e) {
			// TODO Auto-generated catch block
			throw new PlumDataBaseException("Error in reader url Connect exception: "+e.toString(),urlwebservice,"");
		}catch (IOException e) {
			// TODO Auto-generated catch block
			throw new PlumDataBaseException("Error in reader url: "+e.toString(),urlwebservice,"");
		}
		
		try{
			JSONObject jsonReponse= new JSONObject(line);
			return jsonReponse;
		}catch(JSONException e){
			throw new PlumDataBaseException("Error create JSONObject "+e.toString(),urlwebservice,line);			
		}
	}
}

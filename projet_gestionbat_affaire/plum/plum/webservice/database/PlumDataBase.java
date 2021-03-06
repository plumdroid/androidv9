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

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
public class PlumDataBase {
		private String url;
		private static final int THE_NAME = 0;
		private static final int THE_VALUE = 1;
		private int pos;
	public PlumDataBase(String url){
		
		this.url=new String(url);
		this.pos=-1;
	
	}
	private InputStream httpWebService(ArrayList<NameValuePair> params){
		//appel du webservice
		InputStream is=null;
		String http=url;
		try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(http);
		        httppost.setEntity(new UrlEncodedFormEntity(params));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        //� la place de is : String str=EntityUtils.toString(entity);
		}catch(Exception e){
		        Log.e("log_tag", "Error in http connection "+e.toString());
		}
		return is;
	}
	public boolean isOpen(){return true;}
	
	//public Cursor query(String sql){;}
	public Cursor query(String sql){
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("webservice","query"));
		 params.add(new BasicNameValuePair("sql",sql));
		 
		 InputStream is=httpWebService(params);
		 return  formatPlumToCursor(is);
	}
	
	public boolean execute(String sql){
		 ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		 params.add(new BasicNameValuePair("webservice","execute"));
		 params.add(new BasicNameValuePair("sql",sql));
		 
		 httpWebService(params);
		 return true;
	}
	
	private Cursor formatPlumToCursor(InputStream response)
	{
		MatrixCursor cursor=null;
		String line=null;
		try {
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
		{	cursor.addRow(row);row=getColumnFromWebService(charline,THE_VALUE);}
		return  cursor;
	}
	
	private String[] getColumnFromWebService(char[] charline,int name_or_value){
		int posw=pos;
		//nombre de colonnes
		pos=1;
		String strnbcol=extraireCol(charline);
		int nbcol=new Integer(strnbcol).intValue();
		String[] arrayCol=new String[nbcol];
		//extraire les colonnes (name ou value)
		if(name_or_value==THE_NAME) {pos=pos+3;}
		if(name_or_value==THE_VALUE) {pos=posw+2;}
		String col=null;
		int i=0;
		col=extraireCol(charline);
		while(col!=null)
			{arrayCol[i]=col;i++;col=extraireCol(charline);}
		if(i==0) {return null;}
		return arrayCol;
	}
	private String extraireCol(char[] charline){
		int lg=extraireLong(charline);
		if (lg==-1){return null;}
		return extraireVal(charline,lg);
	}
	private int extraireLong(char[] charline){
		this.pos++;
		if(pos>=charline.length){return -1;}
		
		int j=0;
		char[] arrchar={'x','x','x','x','x','x','x','x','x','x'};
		while(charline[pos]!=':' && charline[pos]!='}'&& charline[pos]!=']')
			{arrchar[j]=charline[pos];pos++;j++;}
		int c=0;
		for(c=0;c<arrchar.length;c++) {if (arrchar[c]=='x') {break;}}
		
		char[] arrlong=new char[c];
	    for(int i=0;i<c;i++) {arrlong[i]=arrchar[i];}
		if (c==0){return -1;}
		return new Integer(new String(arrlong)).intValue();	
	}
	private String extraireVal(char[] charline,int lg){
		char[] val=new char[lg];
		this.pos++;
		for(int c=0;c<lg;c++)
		{val[c]=charline[pos];pos++;}
		return new String(val);
	}
}

package plumandroid.webservice.norest;

@SuppressWarnings("serial")
public class PlumDataBaseException extends Exception {
	private String e;
	private String http;
	private String reponseWebService;
	
	
	public PlumDataBaseException(String e, String url, String reponseWebService){
		super(e);
		this.e=e;
		this.http=url;
		this.reponseWebService=reponseWebService;
	}
	
	public String toString(){
		return "{\"ERREUR\":\""+e+"\"}{\"HTTP:\""+http+"\"{\"REPONSEwebSercive\":\""+reponseWebService+"\"}";
	}
}
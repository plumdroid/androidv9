package plum.util;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Formatter;

public class PlumCalendarFr {
	public static final int DAY_OF_MONTH=Calendar.DAY_OF_MONTH;
	public static final int MONTH=Calendar.MONTH;
	public static final int YEAR=Calendar.YEAR;
		Calendar gcal;	
	
	//obtenir la date du jour
	public PlumCalendarFr(){	
		this.gcal=new GregorianCalendar();	
	}
	//une date au format jj/mm/aaaa ou aaaa-mm-jj
	public PlumCalendarFr(String date){
	
		//?analyse de date et mémo si possible
	}
	
	public PlumCalendarFr(Calendar cal){
		gcal=cal;
	}
	
	public PlumCalendarFr(int year, int month, int day){
		gcal=new GregorianCalendar(year,month,day);
	}
	
	public int get(int fielddate){
		return gcal.get(fielddate);
	}	
	public int getMonth(){return get(PlumCalendarFr.MONTH);}
	public int getYear(){return get(PlumCalendarFr.YEAR);}
	public int getDay(){return get(PlumCalendarFr.DAY_OF_MONTH);}
	
	public String toString(int d1, int d2, int d3,char separateur){
        Formatter format=new Formatter();
        //aaaa-mm-dd
        String isodate=format.format("%tF", gcal).toString();
        String[] v=isodate.split("-");
        
        String vd[]=new String[6];
        vd[PlumCalendarFr.YEAR]=v[0];
        vd[PlumCalendarFr.MONTH]=v[1];
        vd[PlumCalendarFr.DAY_OF_MONTH]=v[2];
        
        String datej=vd[d1]+separateur+vd[d2]+separateur+vd[d3];
   		return datej;
   }
   //au format jj/mm/aaaa
   public String toHuman(){
   		return toString(PlumCalendarFr.DAY_OF_MONTH,PlumCalendarFr.MONTH,
   		PlumCalendarFr.YEAR,'/');
   }
   // au format aaaa/mm/jj
   public String toMySql(){
   return toString(PlumCalendarFr.YEAR,PlumCalendarFr.MONTH,
   		PlumCalendarFr.DAY_OF_MONTH,'-');
   }
   
   //0=; -1< ; 1>
   public boolean isEqual(Calendar datec) {
	   if (gcal.compareTo(datec)==0) {return true;}
	   return false;
   }
   
   public boolean isSup(Calendar datec) {
	   if (gcal.compareTo(datec)==1) {return true;}
	   return false;
   }
   
   public boolean isInf(Calendar datec) {
	   if (gcal.compareTo(datec)==-1) {return true;}
	   return false;
   }
   
   public Calendar toCalendar(){ return gcal;}
}
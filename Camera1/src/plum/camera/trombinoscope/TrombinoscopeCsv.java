package plum.camera.trombinoscope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
	
class TrombinoscopeCsv{
	private final int NEXT=1;
	private final int PREV=-1;
	
	private int pos=-1; // dernière position lecture d'une personne ds un groupe
	private String groupe=null; // groupe courant
	
	private ArrayList<String[]> trombi; // personne courante
	private ArrayList<String[]> trombi_groupe; // personnes du groupe courant
	
	/** indice des infos dans trombi */
	
	public static final int iGroupe=0; 
	public static final int iNom=1;
	public static final int iPrenom=2;
	
	public TrombinoscopeCsv(Context context){
		
		// Accès sdcard ... 
		
		File trombiStorageDir = new File(Environment.getExternalStorageDirectory(),
    			"trombinoscope");
   
		File trombiFile;
    	trombiFile = new File(trombiStorageDir.getPath() + File.separator + 
                           "trombinoscope" + ".csv");
    	
    	// création répertoire trombinoscope
    	
    	if (! trombiStorageDir.exists()){
	        if (! trombiStorageDir.mkdirs()){

	            Log.d("TrombinoscopeCsv", "failed to create directory");
	            return;
	        }
	        //ajouter trombinoscope.csv
	        try {
	        	  FileOutputStream outputStream = new FileOutputStream(trombiFile);
	        	  outputStream.write("Groupe,Nom,Prenom".getBytes());
	        	  outputStream.close();
	        } catch (Exception e) {
	        	  e.printStackTrace();
	        	}
	      // rend visible le fichier trombinoscope.csv et du même coup le répertoire
	       new MediaScannerRecord(context,trombiFile,null);
	    }
    	
    	trombi_groupe=new ArrayList<String[]>();//ne rien faire encore... openGroupe
    	
        trombi=new ArrayList<String[]>();
        try {
        	// le fichier est iso-8859-1
        	
        	FileInputStream fis=new FileInputStream(trombiFile);
        	InputStreamReader isr=new InputStreamReader(fis,"ISO-8859-1");//encodage fichier source iso-8859-1
        	BufferedReader bit = new BufferedReader(isr);
    		
    		String line;
    		bit.readLine(); //sauter la ligne d'entête
            while ((line = bit.readLine()) != null) {
            	
                 String[] info = line.split(",");
                 
                 trombi.add(info);
            }
            bit.close();
            isr.close();
            fis.close();

    	} catch (FileNotFoundException e) {
    		Log.w("TAG", "Error trombinoscope.csv: " + e.getMessage());
    		
    	} catch (IOException e) {
    		Log.w("TAG", "Error trombinoscope.csv: " + e.getMessage());
    		}
	}
    	
	
    /** Retourne la ArrayList des groupes */
	
    public String[] getAllGroupe(){
    	ArrayList<String> allGroupe=new ArrayList<String>();
    	
    	for(int i=0;i<trombi.size();i++){
    		String[] info=trombi.get(i);
    		
    		boolean t=false;
    		for(int j=0;j<allGroupe.size();j++){
    			if(info[iGroupe].equals(allGroupe.get(j))){
    				t=true;break;
    			}
    		}
    		
    		if (!t){
    			allGroupe.add(info[iGroupe]);
    		}
    	}
 
    	
    	String[] r=new String[allGroupe.size()];
    	for(int i=0;i<allGroupe.size();i++){
    		r[i]=allGroupe.get(i);
    	}
    	return r;
    }
    
    /** Sélectionner un groupe */
    
    public boolean openGroup(String openGroupe){
    	groupe=openGroupe;
    	pos=-1;
    	
    	trombi_groupe.clear();
    	for(int i=0;i<trombi.size();i++){
    		if(trombi.get(i)[iGroupe].equals(openGroupe)){
    			trombi_groupe.add(trombi.get(i));
    		}
    	}
    	
    	return true;
    }
    
    /** Retourne la personne suivante du groupe */
    
    public String[] getNextPersonne(){	
    	pos=nextOrprevPos(pos,NEXT);
    	
    	return (String[])trombi_groupe.get(pos);
    } 
    
    /** Retourne la personne précédente du groupe */
    
    public String[] getPrevPersonne(){
    	pos=nextOrprevPos(pos,PREV);
    	
    	return (String[])trombi_groupe.get(pos);
    } 
    
    /** Parcours des personne du groupe : next/previous */
    
    private int nextOrprevPos(int beginPos, int go){
    	boolean t=false;
    	int i=beginPos;
    
		switch (go){
			case NEXT : 
				i++;
				break;
			case PREV:
				i--;
				break;
		}
			
		if(i<0){//fin précédent
			i=0;
		}
			
		if(i>trombi_groupe.size()-1){//fin suivant
			i=trombi_groupe.size()-1;
		}
		
		return i;
    }
    
    public boolean isSuivant(){
    	if(pos>=trombi_groupe.size()-1 | trombi_groupe.size()==0){
    		return false;
    	}
    	return true;
    }
    
    public boolean isPrecedent(){
    	if(pos<=0 | trombi_groupe.size()==0){
    		return false;
    	}
    	return true;
    }
}
package plum.camera.trombinoscope;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
/**
 * 
 * @author thierry.bogusz
 * 
 * Gestion des fichiers photos du trombinoscope
 *
 */
class FileTrombinoscope{
	
	//Bogusz Thierry : Bogusz.Thierry
	//Dupont Durant Pierre Jacques : Dupont.Durant.Pierre.jacques
	public char option_Space_Nom='.';//remplacer les espaces par ...
	public char option_Space_Prenom='.';//
	public String option_Between=".";//caractère entre nom et prénom
	
	public FileTrombinoscope(){
		File dir=getDirTrombinoscope();
		
		if (! dir.exists()){
	        if (! dir.mkdirs()){

	            Log.d("FileTrombinoscope", "failed to create directory");
	            return;
	        }
	    }
	}
	
	/** Directory de l'pplication contenant les photos */
	
	public File getDirTrombinoscope(){
		File env =Environment.getExternalStoragePublicDirectory("trombinoscope");

		return env;
	}
	
	/** Directory contenant les photos d'une personne */
	
	public File getDirImageTrombinoscope(String[] unTrombi){
		
		return new File(getDirTrombinoscope().getPath(),
				"photos");
	}
	
	public File getFileImageTrombinoscope(String[] unTrombi){
   
    	File mediaFile=getDirImageTrombinoscope(unTrombi);
    	
    	String fileName=unTrombi[TrombinoscopeCsv.iNom].replace(' ', option_Space_Nom)
    			+option_Between
    			+unTrombi[TrombinoscopeCsv.iPrenom].replace(' ', option_Space_Prenom)
    			+".jpg";
    	
    	mediaFile = new File(mediaFile.getPath() 
    			+ File.separator  
    			+ fileName);
    	
    	
    	return mediaFile;
	}
	
	public boolean outputImageTrombinoscope(String[] unTrombi, Bitmap pictureBitmap,Context context){
    	
    	File fileImageTrombi=getDirImageTrombinoscope(unTrombi);
    	
    	if (! fileImageTrombi.exists()){
	        if (! fileImageTrombi.mkdirs()){

	            Log.d("MyCameraApp", "failed to create directory");
	            return false;
	        }
	    }
    	
        
    	fileImageTrombi=getFileImageTrombinoscope(unTrombi);
    	
        try {
        	FileOutputStream fos = new FileOutputStream(fileImageTrombi);

    		BufferedOutputStream bos = new BufferedOutputStream(fos);
    		pictureBitmap.compress(CompressFormat.JPEG, 100, bos);

    		bos.flush();
    		bos.close();
    		fos.close();

    	} catch (FileNotFoundException e) {
    		Log.w("TAG", "Error saving image file: " + e.getMessage());
    		return false;
    		
    	} catch (IOException e) {
    		Log.w("TAG", "Error saving image file: " + e.getMessage());
    		return false;}
    		
    	//enregistrer la photo dans MediaScanner:utile pour retrouver le fichier en connection USB
    	//sans avoir à attendre un Scanner Média déclenché par Android
    	new MediaScannerRecord(context,getFileImageTrombinoscope(unTrombi),null);
		return true;
	}
	
	public Bitmap getImageBitmapFile(String[] unTrombi){
		File fileImageTrombi=getFileImageTrombinoscope(unTrombi);
		
		Bitmap picture=null;
    	
    	BitmapFactory.Options o2 = new BitmapFactory.Options();
        
    	picture = BitmapFactory.decodeFile(fileImageTrombi.getPath(),o2);
		
		return picture;
	}
	
	public void renameFilesTrombinoscope(char from, char to){
		File dir=new File(getDirTrombinoscope().getPath());
				
		
		File filesT[]=dir.listFiles();
		for(int j=0;j<filesT.length;j++){
			
			if (filesT[j].isDirectory()) {
				
				File files[]=filesT[j].listFiles();
			
			
				for(int i=0;i<files.length;i++){
					String filename=files[i].getName();
					
					String newfilename=files[i].getName();
					newfilename=newfilename.replace("  ", " ");
					newfilename=newfilename.replace(' ', '.');
					newfilename=newfilename.replace("jpg", "png");
					
					File newfile=new File(files[i].getPath().replace(filename,newfilename));
				
					files[i].renameTo(newfile);
				}
		
			}
		}
	}
}
package plum.camera.trombinoscope;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

class MediaScannerRecord
	      implements MediaScannerConnection.OnScanCompletedListener {
 
	   private String mFilename;
	   private String mMimetype;
	   private MediaScannerConnection mConn;
	    
	   public MediaScannerRecord
	         (Context ctx, File dirImage, String mimetype) {
		   
	      String[] paths = {dirImage.getAbsolutePath()};
	      MediaScannerConnection.scanFile(ctx, paths, null, this);
	   }
	 
	   /*public void onMediaScannerConnected() {
	      mConn.scanFile(mFilename, mMimetype);
	   }*/
	    
	  
	   public void onScanCompleted(String path, Uri uri) {
	      
	   }
	
	}
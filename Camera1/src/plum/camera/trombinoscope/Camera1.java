package plum.camera.trombinoscope;

import java.io.IOException;
import java.util.List;

import plum.camera.trombinoscope.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Camera1 extends Activity 
 implements SurfaceHolder.Callback,
 			View.OnClickListener,
 			PictureCallback,
 			AdapterView.OnItemClickListener,
 			Camera.OnZoomChangeListener, 
 			OnTouchListener{

	private Camera camera;
	
	private SurfaceHolder holder;
	private int zoom=0;
	
	//ration 1.25 : 352*440 ou 768x1024 512*640
	//ratio 1.325 : 265/200 (imageView)
	//4.5/3.5(1.28571428571) photo identité
	private final double RATIO_HW=1.25;//(double)4.5/3.5;//4.5/3.5(1.28571428571) photo identité
	
	//w/h
	
	private Camera1.Sizex pictureCamera_size=new Camera1.Sizex(768, (int)(768*RATIO_HW));
	private Camera1.Sizex picturePreview_size=new Camera1.Sizex(768, (int)(768*RATIO_HW));
	private Camera1.Sizex surfaceView_size=new Camera1.Sizex(264,(int)(264*RATIO_HW));
	
	private Camera1.Sizex pictureFile_size=new Camera1.Sizex(264, (int)(264*RATIO_HW));
	
	
	private TrombinoscopeCsv trombi;
	private FileTrombinoscope fileTrombi;
	String[] unTrombi=null;
	
	private SurfaceView surfaceView;
	private ImageView imageView;
	private ListView listView;
	private TextView txt_groupe;
	private TextView txt_personne;
	private Button captureButton;
	private Button suivantButton;
	private Button precedentButton;
	private Button zoomButton;
	private Button moinsButton;
	
	private Button renameButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        //-------- composants View 
        
        imageView=(ImageView)findViewById(R.id.imageView);
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        listView=(ListView) findViewById(R.id.listView);
        
        txt_groupe=(TextView)findViewById(R.id.txt_groupe);
        txt_personne=(TextView)findViewById(R.id.txt_personne);
        
        captureButton = (Button) findViewById(R.id.button_capture);
        suivantButton=(Button)findViewById(R.id.button_suivant);
        precedentButton=(Button)findViewById(R.id.button_precedent);
        zoomButton = (Button) findViewById(R.id.button_zoom);
        
        renameButton=(Button) findViewById(R.id.button_rename);
        
        //-------- Préparer la listView : liste des groupes
        
        trombi=new TrombinoscopeCsv(getApplicationContext());
        
        String[] lesGroupes=trombi.getAllGroupe();
        
        ArrayAdapter<String> listGroupe=new ArrayAdapter<String>(this,
                R.layout.liste_group, lesGroupes); 
             
		listView.setAdapter(listGroupe); 
		
		listView.setTextFilterEnabled(true);
		
		listView.setOnItemClickListener(this);
		
		//-------- gestion caméra
        
		//openCamera();//démarrer la caméra voir onStart()
        
        //-------- composant contrôloant l'affichage de l'image "live" de la camera
        
        holder=surfaceView.getHolder();//Contrôleur SurfaceView
        
        holder.addCallback(this);//gestion des évènements  SurfaceHolder.CallBack
      
        //-deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        //-------- Listener Button
       
        captureButton.setOnClickListener(this);
        suivantButton.setOnClickListener(this);
        precedentButton.setOnClickListener(this);
        
        renameButton.setOnClickListener(this);
        
        
        zoomButton.setOnTouchListener(this);
   
        
      //-------- objet fileTrombi
      	fileTrombi=new FileTrombinoscope();
      		
      	//-------- Init Button
             
      	suivantButton.setEnabled(false);
      	precedentButton.setEnabled(false);


    }
    
    protected void onStart(){
    	super.onStart();
    	
    	// Démarrer la caméra ici 
    	//-permet restart de la caméra après onPause()
    	openCamera();
    	
    	camera.startSmoothZoom(zoom); // pour reprise sur onPause()
        
    }
    
    protected void onRestart(){
    	super.onRestart();
    }

    protected void onResume(){
    	super.onResume();
    }

    protected void onPause(){
    	super.onPause();
    	
    }

    protected void onStop()
    	{super.onStop();
    	this.closeCamera();
    }

    protected void onDestroy(){super.onDestroy();}
    
    protected void onRestoreInstanceState (Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState (Bundle outState){
    	super.onSaveInstanceState(outState);
    }
    
    /** __..._____________ Gestion des écouteurs sur Button __...__________ */
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch(v.getId()){
    	case R.id.button_capture:
    		camera.takePicture(null,null,this);
    		break;
    	
    	case R.id.button_suivant:
    		unTrombi=trombi.getNextPersonne();
    		afficheTrombi();
    		
    		suivantButton.setEnabled(trombi.isSuivant());
    		precedentButton.setEnabled(true);
    		break;
    		
    	case R.id.button_precedent:
    		unTrombi=trombi.getPrevPersonne();
    		afficheTrombi();
    		
    		precedentButton.setEnabled(trombi.isPrecedent());
    		suivantButton.setEnabled(true);
    		break;
    		
    	case R.id.button_rename:
    		this.fileTrombi.renameFilesTrombinoscope(' ','.');
    		AlertDialog.Builder alert;
    		alert=new AlertDialog.Builder(this);
    		alert.setTitle("RENOMMAGE FICHIERS" );
    		alert.setMessage("FIN");
    		
    		AlertDialog dialog=alert.create();
    		dialog.show();
    		
    		//message de fin?
    		
    	}
    	
	}
    

    public boolean onTouch(View v, MotionEvent event) {
    	
    	//analyser down et up
    
    	if (
    		event.getAction()!=MotionEvent.ACTION_MOVE &
    		event.getAction()!=MotionEvent.ACTION_DOWN){
    		return false;
    	}
    	
    	Camera.Parameters cp=camera.getParameters();
    	int largeurZoom=cp.getMaxZoom();
    		
    	//position relative du Touch
    	int largeurButton=v.getRight()-v.getLeft()-20;
    	
    	
    	int relativeTouch=largeurButton-(int)event.getX(0);//bord droit - position Touch
    	
    	if(relativeTouch<0){
    		relativeTouch=0;
    	}
    	
    	if(relativeTouch>largeurButton){
    		relativeTouch=largeurButton;
    	}
    	
    	
    	
    	float pourc=(float)relativeTouch/largeurButton;
    	zoom=(int)(largeurZoom*pourc);
    	
    	camera.startSmoothZoom(zoom);
    	
    	return true;
    	/* ancienne version
    	 * int largeurButton=v.getRight()-v.getLeft();
    	 
    	int largeurZoom=cp.getMaxZoom();
    	
    	int h=event.getHistorySize();
    	if(event.getHistorySize()<2) return false;
    	
    	float posX_p=event.getHistoricalX(0,h-1);
    	float posX_d=event.getX(0);
    	
    	if(posX_d>posX_p){zoom--;}
    	if(posX_d<posX_p){zoom++;}
    	
    	
    	
    	if(zoom>cp.getMaxZoom()){
			zoom=cp.getMaxZoom();
			return false;
		}
    	
    	if(zoom<0){zoom=0;return false;}
    	
    	camera.startSmoothZoom(zoom);
    	
    	return false;
    	*/
    	
    	
	}
  
    
    /** __..._____________ Gestion de la caméra __...__________ */
    
    private boolean openCamera(){
    	boolean qOpened = false;
    	  
        try {
            closeCamera();
            camera = Camera.open();
            Camera.Parameters cp=camera.getParameters();
            //Size s=getOptimalPreviewSize(cp.getSupportedPreviewSizes(),200,265);
           
            
            cp.setPictureSize(this.pictureCamera_size.w,pictureCamera_size.h);//352*440 ou 768x1024 512*640
            cp.setPreviewSize(picturePreview_size.w,picturePreview_size.h);
            
            List<Camera.Size> csp=cp.getSupportedPictureSizes();
            List<Camera.Size> csf=cp.getSupportedPreviewSizes();
        
            camera.setParameters(cp);
            
            
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
        
        // setCameraDisplayOrientation(this, 0, camera);
        return qOpened;   
    }
    
    private void closeCamera() {
    	// Surface will be destroyed when we return, so stop the preview.
        if (camera != null) {
            // Call stopPreview() to stop updating the preview surface.
            camera.stopPreview();
            camera.release();
            
            camera=null;
        }
    }
    
   
    /** Camera.pictureCallback. Sur capture de la photos. format jpeg */
    
    public void onPictureTaken(byte[] data,Camera camera){
    	
    	//afficher l'image dans un BitmapView
    	Bitmap pictureTaken=null;
    	
    	BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inPurgeable=true;
        o2.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(data, 0,data.length,o2);//récupère les car. de l'image ds o2
        	
        
        o2.inJustDecodeBounds=false;
    	pictureTaken = BitmapFactory.decodeByteArray(data, 0,data.length,o2);
    
    	Bitmap pictureBitmap;
    	pictureBitmap = Bitmap.createScaledBitmap(pictureTaken, pictureFile_size.w, pictureFile_size.h, false);
		//inutile car ok sur capture
    	//pictureBitmap=pictureTaken;
    	
    	imageView.setAdjustViewBounds(true);
    	imageView.setImageBitmap(pictureBitmap);
    	
    	
    	// Enregistrer l'image 
    	
    	if (unTrombi!=null && unTrombi[0]!="" && unTrombi[1]!="" && unTrombi[2]!=""){
    		fileTrombi.outputImageTrombinoscope(unTrombi, pictureBitmap, getApplicationContext());
    	}
    	
    	    	
        // Redémarrer la caméra
    	camera.startPreview();
    	
    }
    
    /** startSmoothZoom() */
    public void onZoomChange(int zoomValue, boolean stopped, Camera camera){
    	//ras
    }
        
    
    /** __..._____________ Gestion de SurfaceHolder.CallBack __...__________ */
    
     /** Indique à la caméra où afficher l'image */
    
        public void surfaceCreated(SurfaceHolder holder){
        	try {
                camera.setPreviewDisplay(holder);
                //camera.setDisplayOrientation(90);
     
                LayoutParams la=surfaceView.getLayoutParams();
                la.height=this.surfaceView_size.h;
                la.width=surfaceView_size.w;       
                surfaceView.setLayoutParams(la);
                
               
                
                camera.startPreview();
            } catch (IOException e) {
                Log.d(getString(R.string.app_name), "Error setting camera preview: " + e.getMessage());
            }
        	//Toast toast = Toast.makeText(getApplicationContext(),"create",Toast.LENGTH_SHORT);
        	
        }
        
       
        
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            //ras
        	camera.startPreview();
        }

        
        public void surfaceDestroyed(SurfaceHolder holder) {
            closeCamera();
        }

       
        /** __..._____________ Gestion Listener ListView __...__________ */
        
        public void onItemClick(AdapterView<?> ad, View v, int pos, long id) {
    		
        	TextView item=(TextView) v;
        	txt_groupe.setText(item.getText());
        	
        	trombi.openGroup(txt_groupe.getText().toString());
        	
        	unTrombi=trombi.getNextPersonne();
        	afficheTrombi();
        	
        	suivantButton.setEnabled(trombi.isSuivant());
        	precedentButton.setEnabled(trombi.isPrecedent());
        	
        	
    	}
		
      /** __..._____________ Méthodes internes __...__________ */
        
      /** Affichage nom et photo de la personne */
 
      private void afficheTrombi(){
    	  txt_personne.setText(unTrombi[trombi.iPrenom]+" "+unTrombi[trombi.iNom]);
        	
        Bitmap imaget=fileTrombi.getImageBitmapFile(unTrombi);
        	
        if (imaget!=null){
        	imageView.setImageBitmap(imaget);
        }
        else{
        	imageView.setImageResource(R.drawable.icon);
        }
        	
       }
        
        /** Pour info. non utilisé !! */
        
        private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.2;
            
            double targetRatio = (double) w / h;
            if (sizes == null)
                return null;

            Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;


            // Try to find an size match aspect ratio and size
            for (Size size : sizes) {
                Log.d("camera1", "Checking size " + size.width + "w " + size.height
                        + "h");
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the
            // requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }

		public class Sizex{
			public int w;
			public int h;
			public Sizex(int w, int h){
				this.w=w;this.h=h;
			}
		}
		
}
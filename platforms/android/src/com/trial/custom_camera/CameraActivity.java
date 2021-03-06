package com.trial.custom_camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends ActionBarActivity {
	
	private Camera cam;
	private CameraPreview camPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private FrameLayout mPreview;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//if(checkCameraHardware(this))
		cam = getCameraInstance();
		
		camPreview = new CameraPreview(this, cam);
		mPreview = (FrameLayout) findViewById(R.id.camera_preview);
		mPreview.addView(camPreview);
		
		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(
		    new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            // get an image from the camera
		            cam.takePicture(null, null, mPicture);
		        }
		    }
		);
		
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseCamera();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		resumeCamera();
	}
	


	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d("PictureCallback", "Error creating media file, check storage permissions: " );
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	            
	            MediaScannerConnection.scanFile(getApplicationContext(),
	                    new String[] { pictureFile.toString() }, null,
	                    new MediaScannerConnection.OnScanCompletedListener() {
	                public void onScanCompleted(String path, Uri uri) {
	                    Log.i("ExternalStorage", "Scanned " + path + ":");
	                    Log.i("ExternalStorage", "-> uri=" + uri);
	                }
	           });
	            
	            Log.d("PictureCallback", "File successfully written");
	        } catch (FileNotFoundException e) {
	            Log.d("PictureCallback", "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    Log.d("MyCameraApp", "timeStamp: " + timeStamp);
	    Log.d("MyCameraApp", "Dir: " + mediaStorageDir.getPath());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	
    private void releaseCamera(){
        if (cam != null){
        	cam.stopPreview();
        	cam.setPreviewCallback(null);
        	cam.release();        // release the camera for other applications
        	cam = null;
        	camPreview.getHolder().removeCallback(camPreview);
        	mPreview.removeView(camPreview);
        }
        
    }
    
	private void resumeCamera() {
		if(cam == null){
			cam = getCameraInstance();
			camPreview = new CameraPreview(this, cam);
			mPreview.addView(camPreview);
		}
	}
}

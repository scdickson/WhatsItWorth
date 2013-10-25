package cs307.wiw;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cs307.wiw.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.*;

public class Main extends Activity {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static String fileLocation = "";
	ProgressBar progressBar;
	TextView progressText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    if(savedInstanceState==null){
		    // create Intent to take a picture and return control to the calling application
		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
		    // create a file to save the image
		    File imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		    fileLocation = imageFile.getAbsolutePath();
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile)); 
	
		    // start the image capture Intent
		    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	    }else{
	    	fileLocation = savedInstanceState.getString("fileLocation");
	    }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putString("fileLocation", fileLocation);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	        	new ImageNetworkTask().execute(fileLocation);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "WIW");
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(0));
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else {
	        return null;
	    }
	    
	    return mediaFile;
	}
	
	private class ImageNetworkTask extends AsyncTask<String, Integer, String> {

		private InetAddress serverAddr;
		private Socket ioSocket;
		private int ioPort = 9999;
		
		@Override
		protected void onPreExecute(){
			progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		    progressText = (TextView) findViewById(R.id.textView1);
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setIndeterminate(false);
			progressBar.setProgress(0);
			progressText.setVisibility(View.VISIBLE);
						
		}
		
		@Override
		protected String doInBackground(String... fileLocationPath) {
			File imageFile = new File(fileLocationPath[0]);
			long imageFileSize = imageFile.length();
			BufferedInputStream imageBufferedInputStream = null;
			BufferedOutputStream socketBufferedOutputStream = null;
			BufferedInputStream socketBufferedInputStream = null;
			String cardName = "";
			
			try{
				//Setup connection
				serverAddr = InetAddress.getByName("data.cs.purdue.edu");
				ioSocket = new Socket(serverAddr, ioPort);
				
				//Send image to server
				imageBufferedInputStream = new BufferedInputStream(new FileInputStream(imageFile.getPath()));
				socketBufferedOutputStream = new BufferedOutputStream(ioSocket.getOutputStream());
				byte imageUploadBuffer[] = new byte[8192];
				int bytesRead;
				int totalRead = 0;
				while((bytesRead = imageBufferedInputStream.read(imageUploadBuffer, 0, 8192)) != -1){
					socketBufferedOutputStream.write(imageUploadBuffer, 0, bytesRead);
					totalRead+=bytesRead;
					publishProgress(0,(int) Math.floor((totalRead/imageFileSize)*100));
				}
				if(imageBufferedInputStream != null){
					imageBufferedInputStream.close();
				}
				if(socketBufferedOutputStream != null){
					socketBufferedOutputStream.close();
				}
				//Image sent
				publishProgress(1,0);
				
				//Get card name from server
				socketBufferedInputStream = new BufferedInputStream(ioSocket.getInputStream());
				byte cardNameBuffer[] = new byte[8192];
				socketBufferedInputStream.read(cardNameBuffer, 0, 8192);
				cardName = new String(cardNameBuffer,"UTF-8");
				if(socketBufferedInputStream != null){
					socketBufferedInputStream.close();
				}
				
				//Close socket
				ioSocket.close();
			}catch(Exception e){
				Log.e("ImageNetworkTask", "Upload Issue", e);
			}
			
			return cardName;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress){
			if(progress[0] == 0){
				progressBar.setProgress(progress[1]);
			}else{
				progressText.setText("Analyzing Image on Server");
			}
		}
		
		@Override
		protected void onCancelled(){
			progressText.setText("Task Cancelled!");
		}
		
		@Override
		protected void onPostExecute(String result){
			progressText.setText(result);
		}

	}
}

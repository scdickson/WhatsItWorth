package cs307.wiw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	TextView cardNameText;
	TextView cardPriceLowText;
	TextView cardPriceMedText;
	TextView cardPriceHighText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    progressBar = (ProgressBar) findViewById(R.id.progressBar);
	    progressText = (TextView) findViewById(R.id.progressTextView);
	    cardNameText = (TextView) findViewById(R.id.cardNameTextView);
	    cardPriceLowText = (TextView) findViewById(R.id.cardPriceLowTextView);
	    cardPriceMedText = (TextView) findViewById(R.id.cardPriceMedTextView);
	    cardPriceHighText = (TextView) findViewById(R.id.cardPriceHighTextView);
	    
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
	
	private class ImageNetworkTask extends AsyncTask<String, Integer, String[]> {

		private int ioPort = 9998;
		private String serverName = "data.cs.purdue.edu";
		
		@Override
		protected void onPreExecute(){
			progressBar.setVisibility(View.VISIBLE);
			progressText.setVisibility(View.VISIBLE);
						
		}
		
		@Override
		protected String[] doInBackground(String... fileLocationPath) {
			File imageFile = new File(fileLocationPath[0]);
			long imageFileSize = imageFile.length();
			String cardName = "";
			String[] results = new String[4];
			
			try{
				//Setup connection
				InetAddress serverAddr = InetAddress.getByName(serverName);
				Socket ioSocket = new Socket(serverAddr, ioPort);
				
				//Setup i/o
				FileInputStream fisFile = new FileInputStream(imageFile);
				DataOutputStream dosImage = new DataOutputStream(ioSocket.getOutputStream());
				DataInputStream disCard = new DataInputStream(ioSocket.getInputStream());
				
				//Setup buffers
				byte[] imageBuffer = new byte[(int)imageFileSize];
				byte[] cardNameBuffer = new byte[25];
				
				//Read from file
				fisFile.read(imageBuffer, 0, (int)imageFileSize);
				
				//Send file to server
				dosImage.writeInt((int)imageFileSize);
				dosImage.write(imageBuffer, 0, (int)imageFileSize);
				
				//Publish sent
				publishProgress(0);
				
				//Receive card name from server
				disCard.read(cardNameBuffer);
				cardName = new String(cardNameBuffer,"UTF-8");
				cardName = cardName.substring(0, cardName.indexOf("\n"));
				
				//close readers/writers
				dosImage.close();
				fisFile.close();
				disCard.close();

				//Close socket
				ioSocket.close();
			}catch(Exception e){
				Log.e("ImageNetworkTask", "Upload Issue", e);
			}
			
			results[0] = cardName;
			
			publishProgress(1);
			
			TCGScraper scrape = new TCGScraper();
			results[1] = "L: " + scrape.getLowPrice(cardName);
			results[2] = "M: " + scrape.getMedPrice(cardName);
			results[3] = "H: " + scrape.getHighPrice(cardName);
			return results;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress){
			if(progress[0] == 0){
				progressText.setText("Analyzing Image on Server");
			}else if(progress[0]==1){
				progressText.setText("Acquiring Current Pricing Information");
			}
		}
		
		@Override
		protected void onCancelled(){
			progressText.setText("Task Cancelled!");
		}
		
		@Override
		protected void onPostExecute(String[] results){
			cardNameText.setText(results[0]);
			cardPriceLowText.setText(results[1]);
			cardPriceMedText.setText(results[2]);
			cardPriceHighText.setText(results[3]);
			
			progressText.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			cardNameText.setVisibility(View.VISIBLE);
			cardPriceLowText.setVisibility(View.VISIBLE);
			cardPriceMedText.setVisibility(View.VISIBLE);
			cardPriceHighText.setVisibility(View.VISIBLE);
		}

	}
}

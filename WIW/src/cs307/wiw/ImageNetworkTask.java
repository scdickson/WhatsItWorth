package cs307.wiw;

import java.net.InetAddress;
import java.net.Socket;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ImageNetworkTask extends AsyncTask<Uri, Integer, String> {

	private InetAddress serverAddr;
	private Socket ioSocket;
	private int ioPort = 9999;
	
	@Override
	protected void onPreExecute(){
		try{
			serverAddr = InetAddress.getByName("data.cs.purdue.edu");
			ioSocket = new Socket(serverAddr, ioPort);
		}catch(Exception e){
			Log.e("ImageNetworkTask", "Connection Issue", e);
		}
	}
	
	@Override
	protected String doInBackground(Uri... fileUri) {
		return null;
	}

}

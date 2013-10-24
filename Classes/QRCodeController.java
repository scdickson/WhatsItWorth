
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.advansoft.database.DatabaseManager;
import com.advansoft.database.Exhibitor;
import com.advansoft.database.Fair;
import com.common.Common;
import com.common.Enum.STATUS;
import com.common.Messages;
import com.common.ui.ASBaseFragment;
import com.communication.HttpRequest;
import com.communication.Requester;
import com.communication.WebAttributes;
import com.communication.handlers.HandlerExhibitorDetail;
import com.ezfairs.exhibitors.ExhibitorDetailViewController;
import com.utils.NSNumber;
import com.zbar.CameraPreview;
import com.zbar.ZBarConstants;


public class QRCodeViewController extends ASBaseFragment implements Camera.PreviewCallback, ZBarConstants {

    private CameraPreview mPreview;
    private Camera mCamera;
    private ImageScanner mScanner;
    private Handler mAutoFocusHandler;
    private ImageButton buttonTorch;
    
    static {
        System.loadLibrary("iconv");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
        
        View v = super.onCreateView(inflater, container, savedInstanceState);
		if(v != null) {return v;}
		v =  this.inflateView(inflater,R.layout.qr_code_view_controller, container, false);
        
       
        mAutoFocusHandler = new Handler();
        // Create and configure the ImageScanner;
        setupScanner();
        FrameLayout frm = (FrameLayout)v.findViewById(R.id.layoutCamera);
        mPreview = new CameraPreview(v,getActivity(), this, autoFocusCB);
        frm.addView(mPreview);
        
        ImageButton button = (ImageButton)v.findViewById(R.id.buttonFavourite);
        button.setOnClickListener(actionFavourite);
        button = (ImageButton)v.findViewById(R.id.buttonGallery);
        button.setOnClickListener(actionGallery);
        buttonTorch = (ImageButton)v.findViewById(R.id.buttonFlash);
        buttonTorch.setOnClickListener(actionFlash);
        
        TextView textViewNav = (TextView)v.findViewById(R.id.navTitle);
        textViewNav.setText(Messages.kQRCodeScanner());
        
        return v;
	}
	
	public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = new int[]{Symbol.QRCODE};
        if (symbols != null) {
            mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                mScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }
    }
	 @Override
	public void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        mPreview.setCamera(mCamera);
        

	 }
	 
	 @Override
	 public void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
	 
	 private Bitmap decodeFile(File f){
		    try {
		        //Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

		        //The new size we want to scale to
		        final int REQUIRED_SIZE= 70;

		        //Find the correct scale value. It should be the power of 2.
		        int scale=1;
		        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
		            scale*=2;

		        //Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize=scale;
		        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		    } catch (FileNotFoundException e) {}
		    return null;
		}
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && data != null && data.getData() != null){
	        Uri _uri = data.getData();

	        if (_uri != null) {
	            //User had pick an image.
	            Cursor cursor = getActivity().getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
	            cursor.moveToFirst();
	            //Link to the image
	            final String imageFilePath = cursor.getString(0);
	            cursor.close();
	            
	            try 
	            {
	            	Bitmap bmp =  this.decodeFile(new File(imageFilePath));
		            Image barcode = new Image(bmp.getWidth(), bmp.getHeight(), "Y800");
		            
		            int bytes = bmp.getByteCount();
		          //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
		          //int bytes = b.getWidth()*b.getHeight()*4; 

		          ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		          bmp.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

		          byte[] array = buffer.array(); //Get
		          barcode.setData(array);
		          int result = mScanner.scanImage(barcode);
		            if (result != 0) {
		                SymbolSet syms = mScanner.getResults();
		                for (Symbol sym : syms) {
		                    String textString = sym.getData();
		                    if (!TextUtils.isEmpty(textString)) {	                    	
		                    	this.loadExhibitor(textString);
		                        break;
		                    }
		                }
		            }
		            
	                
	            }
	            catch(Exception e)
	            {
	                e.printStackTrace();
	            }
	            
	            
	         }
	    }
		super.onActivityResult(requestCode, resultCode, data);
			
	}
	 
	public void onFlash(boolean val) {
    	if(mCamera != null) {
	    	Camera.Parameters parameters = mCamera.getParameters();
	    	if(val)
	    		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
	    	else 
	    		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
	    	mCamera.setParameters(parameters);
    	}
	}
	    
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            SymbolSet syms = mScanner.getResults();
            for (Symbol sym : syms) {
                String textString = sym.getData();
                if (!TextUtils.isEmpty(textString)) {                	
                	this.loadExhibitor(textString);
                    break;
                }
            }
        }
    }
    
    private void loadExhibitor(String exhibitorID) {
    	Common.showLoader(getActivity());
		Requester request = new Requester(getActivity(), this);
		if(Common.isInteger(exhibitorID)) {
			request.fetchExhbitorDetailForExhibitor(Integer.parseInt(exhibitorID), new HandlerExhibitorDetail());
		}
		else {
			// TODO : Alert number is not valid
		}
    }
    
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };
	    
    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
    
    OnClickListener actionFavourite =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			
		}
	};
	
    OnClickListener actionGallery =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
			
		}
	};
    
    OnClickListener actionFlash =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters.getFlashMode().equalsIgnoreCase(Parameters.FLASH_MODE_TORCH)) {
		        buttonTorch.setImageDrawable(getResources().getDrawable(R.drawable.light_icon_small));
		        onFlash(false);
		    }
		    else {
		        buttonTorch.setImageDrawable(getResources().getDrawable(R.drawable.light_icon_small)); //ON
		        onFlash(true);
		    }
		}
	};
	
	@Override
	public void requestRecieved(Requester requester, HttpRequest httpRequest,STATUS status) {
		super.requestRecieved(requester, httpRequest, status);
		
		if(status == STATUS.SUCCESS) {
			JSONObject dic = httpRequest.getResponse();
			JSONArray list;
			try {
				list = dic.getJSONArray(WebAttributes.table_exhibitor);
				if(list.length() > 0) {
					JSONObject obj = list.getJSONObject(0);
					int exhibitorID = NSNumber.numberWithString(obj.getString(WebAttributes.exhibitor_exhibitor_id));
					int exhibitionID = NSNumber.numberWithString(obj.getString(WebAttributes.exhibitor_exhibition_id));
					Fair fair = DatabaseManager.sharedInstance(getActivity()).getExhibitionWithID(exhibitionID);
					Exhibitor exhibitor = DatabaseManager.sharedInstance(getActivity()).getExhibitorWithID(exhibitorID,fair);
					ExhibitorDetailViewController controller = new ExhibitorDetailViewController();
					Bundle bundle = new Bundle();
					bundle.putSerializable("Exhibitor",exhibitor);
					controller.setArguments(bundle);
					tabController.pushFragment(controller,true);
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		
		}
	}
}

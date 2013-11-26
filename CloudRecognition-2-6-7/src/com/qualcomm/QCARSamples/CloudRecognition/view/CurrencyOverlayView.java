package com.qualcomm.QCARSamples.CloudRecognition.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualcomm.QCARSamples.CloudRecognition.R;

/** Custom View with Book Overlay Data */
public class CurrencyOverlayView extends RelativeLayout
{
    public CurrencyOverlayView(Context context)
    {
        this(context, null);
    }

    public CurrencyOverlayView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CurrencyOverlayView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflateLayout(context);

    }
	
    /** Inflates the Custom View Layout */
    private void inflateLayout(Context context)
    {

        final LayoutInflater inflater = LayoutInflater.from(context);

        // Generates the layout for the view
        inflater.inflate(R.layout.bitmap_layout, this, true);
    }

    /** Sets Currency Name in View */
    public void setCurrencyName(String CurrencyName)
    {
    	Log.d("CurrencyOverlay Name", CurrencyName);
    	TextView tv = (TextView) findViewById(R.id.currency_name);
    	tv.setText(CurrencyName);
    }
    
    /**Sets Currency Price Low in View */
    public void setCurrencyPrice(String CurrencyPrice)
    {
    	TextView tv = (TextView) findViewById(R.id.currency_price);
        tv.setText(CurrencyPrice);
    }

}

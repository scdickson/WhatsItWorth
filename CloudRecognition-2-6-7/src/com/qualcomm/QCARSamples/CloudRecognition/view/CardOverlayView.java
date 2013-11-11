/*==============================================================================
Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

@file
    BookOverlayView.java

@brief
    Custom View to display the book overlay data

==============================================================================*/
package com.qualcomm.QCARSamples.CloudRecognition.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualcomm.QCARSamples.CloudRecognition.R;

/** Custom View with Book Overlay Data */
public class CardOverlayView extends RelativeLayout
{
    public CardOverlayView(Context context)
    {
        this(context, null);
    }


    public CardOverlayView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }


    public CardOverlayView(Context context, AttributeSet attrs, int defStyle)
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

    /** Sets Card Name in View */
    public void setCardName(String cardName)
    {
        TextView tv = (TextView) findViewById(R.id.card_name);
        tv.setText(cardName);
    }
    
    /**Sets Card Price Low in View */
    public void setCardPriceLow(String cardPriceLow)
    {
    	TextView tv = (TextView) findViewById(R.id.low_price);
    	tv.setText(cardPriceLow);
    }
    
    /**Sets Card Price Med. in View */
    public void setCardPriceMed(String cardPriceMed)
    {
    	TextView tv = (TextView) findViewById(R.id.med_price);
    	tv.setText(cardPriceMed);
    }
    
    /**Sets Card Price Hi. in View */
    public void setCardPriceHi(String cardPriceHi)
    {
    	TextView tv = (TextView) findViewById(R.id.high_price);
    	tv.setText(cardPriceHi);
    }

}

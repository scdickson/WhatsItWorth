/*==============================================================================
Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

==============================================================================*/
package com.qualcomm.QCARSamples.CloudRecognition.model;

import android.graphics.Bitmap;

/** A support class encapsulating the info for one book*/
public class Card
{
    private String name;
    private String priceLow;
    private String priceMed;
    private String priceHi;
    private Bitmap graphBitmap;

    public Card()
    {

    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPriceLow()
    {
    	return priceLow;
    }
    
    public void setPriceLow(String priceLow)
    {
    	this.priceLow = priceLow;
    }
    
    public String getPriceMed()
    {
    	return priceMed;
    }
    
    public void setPriceMed(String priceMed)
    {
    	this.priceMed = priceMed;
    }
    
    public String getPriceHi()
    {
    	return priceHi;
    }
    
    public void setPriceHi(String priceHi)
    {
    	this.priceHi = priceHi;
    }
    
    public Bitmap getGraph()
    {
    	return graphBitmap;
    }
    
    public void setGraph(Bitmap graphBitmap)
    {
    	this.graphBitmap = graphBitmap;
    }
}

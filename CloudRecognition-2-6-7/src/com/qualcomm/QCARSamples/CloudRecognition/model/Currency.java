package com.qualcomm.QCARSamples.CloudRecognition.model;

import android.graphics.Bitmap;

public class Currency
{
    private String name;
    private String price;
    private Bitmap graphBitmap;
    
    public Currency()
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
    
    public String getPrice()
    {
            return price;
    }
    
    public void setPrice (String price)
    {
            this.price = price;
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
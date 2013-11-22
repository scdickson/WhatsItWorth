package com.qualcomm.QCARSamples.CloudRecognition.model;

/** A support class encapsulating the info for one currency item*/
public class Currency
{
    private String name;
    private String price;

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
    
}
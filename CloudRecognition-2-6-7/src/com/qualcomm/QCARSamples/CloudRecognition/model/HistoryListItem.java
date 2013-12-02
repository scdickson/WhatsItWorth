package com.qualcomm.QCARSamples.CloudRecognition.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryListItem implements Serializable{
	private String name;
	private String type;
	private String lowPrice;
	private String medPrice;
	private String highPrice;
	
	private double low;
	private double medium;
	private double high;
	
	
	public HistoryListItem(String name, String type, String medPrice)
	{
		this.name = name;
		this.type = type;
		this.medPrice = medPrice;
		this.medium = Double.parseDouble(medPrice.substring(medPrice.indexOf("$")+1));
		
		this.low = 0;
		this.lowPrice = null;
		this.high = 0;
		this.highPrice = null;
		
	}
	
	public HistoryListItem(String name, String type, String lowPrice, String medPrice, String highPrice)
	{
		this.name = name;
		this.type = type;
		this.lowPrice = lowPrice;
		this.medPrice = medPrice;
		this.highPrice = highPrice;
		this.low = Double.parseDouble(lowPrice.substring(lowPrice.indexOf("$")+1));
		this.medium = Double.parseDouble(medPrice.substring(medPrice.indexOf("$")+1));
		this.high = Double.parseDouble(highPrice.substring(highPrice.indexOf("$")+1));
	
	}
	public String getName()
	{	
		return this.name;
	}
	public String getType()
	{
		return this.type;
	}
	
	/*Double Accessor Methods for price*/
	public double getLowDouble()
	{
		return this.low;
	}
	public double getMedDouble()
	{
		return this.medium;
	}
	public double getHighDouble()
	{
		return this.high;
	}
	/*String Accessor Methods for price*/
	public String getLowPriceString()
	{	
		return this.lowPrice;
	}
	public String getMedPriceString()
	{
		return this.medPrice;
	}
	public String getHighPriceString()
	{	
		return this.highPrice;
	}
	
}

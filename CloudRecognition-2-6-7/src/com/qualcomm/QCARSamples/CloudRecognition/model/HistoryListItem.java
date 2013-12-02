package com.qualcomm.QCARSamples.CloudRecognition.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryListItem implements Parcelable{
	private String name;
	private String type;
	private String lowPrice;
	private String medPrice;
	private String highPrice;
	
	private double low;
	private double medium;
	private double high;
	

	public HistoryListItem(String name, String type, String lowPrice, String medPrice, String highPrice)
	{
		this.name = name;
		this.type = type;
		this.lowPrice = lowPrice;
		this.medPrice = medPrice;
		this.highPrice = highPrice;
		this.low = Double.parseDouble(lowPrice);
		this.medium = Double.parseDouble(medPrice);
		this.high = Double.parseDouble(highPrice);
	
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}

package com.example.androidpong;
import java.io.*;
import java.net.*;

public class SimpleSocketConnection 
{
	private Socket s = null;
	private String netPath;
	private int port;
	private PrintWriter out;
	private boolean detailedErrorOn = false;
	
	public SimpleSocketConnection(String netPath, int port)
	{
		this.netPath = netPath;
		this.port = port;
	}
	
	public void connect()
	{
		Thread t = new Thread()
		{
			public void run()
			{
		try
		{
			s = new Socket(netPath, port);
			out = new PrintWriter(s.getOutputStream());
		}
		catch(Exception e)
		{
			if(detailedErrorOn)
			{
				e.printStackTrace();
			}
			else
			{
				System.out.println("Error establishing connection with " + netPath + " on port " + port);
				System.exit(0);
			}
		}}
		};
		t.start();
	}
	
	public void getStatus()
	{
		if(s != null)
		{
			System.out.println("Connected to " + s.getInetAddress() + " on port " + s.getPort());
		}
		else
		{
			System.out.println("No connection is established.");
		}
	}
	
	public void disconnect()
	{
		try
		{			
			out.close();
			s.close();
			s = null;
	
		}
		catch(Exception e)
		{
			if(detailedErrorOn)
			{
				e.printStackTrace();
			}
			else
			{
				System.out.println("Error disconnecting from " + netPath + " on port " + port);
				System.exit(0);
			}
		}
		
	}
	
	public void setDetailedErrors(boolean detailedErrorsOn)
	{
		detailedErrorOn = detailedErrorsOn;
	}
	
	public void send(String data)
	{
		try
		{
			out.println();
			out.println(data);
			out.flush();
		}
		catch(Exception e)
		{
			if(detailedErrorOn)
			{
				e.printStackTrace();
			}
			else
			{
				System.out.println("Error sending data to " + netPath + " on port " + port);
				System.exit(0);
			}
		}
	}

}

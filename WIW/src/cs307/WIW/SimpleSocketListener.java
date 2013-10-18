package com.example.androidpong;
import java.io.*;
import java.net.*;

public class SimpleSocketListener implements Runnable
{
	private ServerSocket s;
	private Socket clientSocket;
	private BufferedReader in;
	Draw d;
	Thread t;
	
	public SimpleSocketListener(int port)
	{
		try
		{
			s = new ServerSocket(port);
		}
		catch(Exception e)
		{
			System.out.println("Could not establish a Server Socket on port " + port);
			System.exit(0);
		}
	}
	
	public void listen()
	{
		t = new Thread(){
			public void run()
			{
				while(true)
				{
		try
		{	
			if(s!= null)
			{
			clientSocket = s.accept();
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			}
		}
		catch(Exception e)
		{
			//System.out.println("Could not accept incoming connections.");
			e.printStackTrace();
			continue;
		}}}
		};
		t.start();
		Thread s = new Thread(this);
		s.start();
	}
	
	public void getStatus()
	{
		System.out.println(s.getInetAddress() + " is listening on port " + s.getLocalPort());
	}
	
	public void stopListening()
	{
		try
		{
			clientSocket.close();
			in.close();
			s.close();
		}
		catch(Exception e)
		{
			System.out.println("Error stopping Server Socket.");
			System.exit(0);
		}
		
	}
	
	public void setDraw(Draw draw)
	{
		d = draw;
	}
	
	public void run()
	{
		while(true)
		{
		try
		{
			while(in == null)
			{
				//Null op
			}
			t.interrupt();
			while(in.readLine() != null)
			{
				//System.out.println("Remote Host: " + in.readLine());
				String line = in.readLine();
				d.redrawPlayer(line);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		}
		
	}

}

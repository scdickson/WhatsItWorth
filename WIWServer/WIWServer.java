//Sam Dickson
//What's It Worth Server
//19 October 2013

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class WIWServer
{
	private ArrayList<WIWServerThread> clientThreads;
	private ServerSocket ss;
	private boolean isRunning;
	
	public WIWServer()
	{
		clientThreads = new ArrayList<WIWServerThread>();

		try
		{
			ss = new ServerSocket(WIWConstants.SERVER_PORT);
			ss.setReuseAddress(WIWConstants.REUSE_ADDRESS);
		}
		catch(Exception e)
		{
			System.err.println(getDateTime() + "Fatal Error! Could not start WIWServer on port " + WIWConstants.SERVER_PORT + "...Shutting down...");
			System.exit(1);
		}
		
		isRunning = true;

		if(WIWConstants.VERBOSE_MODE)
		{
			System.err.println(getDateTime() + "WIWServer running on port " + WIWConstants.SERVER_PORT + " with " + WIWConstants.MAX_CLIENTS + " maximum clients.");
		}

		listen();
	}

	public static void main(String args[])
	{
		WIWServer server = new WIWServer();
	}

	public String getDateTime()
	{
		DateFormat df = new SimpleDateFormat(WIWConstants.DATE_TIME_FORMAT);
		Calendar cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}

	public void listen()
	{
		while(isRunning)
		{
			try
			{
				if(getNumThreads() < WIWConstants.MAX_CLIENTS)
				{
					addThread(new WIWServerThread(ss.accept()));
				}
			}
			catch(Exception e)
			{
				System.err.println(getDateTime() + "Error accepting connection on port " + WIWConstants.SERVER_PORT);
			}
		}
	}

	private void stopListening()
	{
		isRunning = false;
	}

	private void startListening()
	{
		isRunning = true;
		listen();
	}

	/* Synchronized methods to preserve integrity of data structure */

	private synchronized void addThread(WIWServerThread thread)
	{
		clientThreads.add(thread);
	}
	
	private synchronized int getNumThreads()
	{
		return clientThreads.size();
	}

	private synchronized void removeThread(WIWServerThread thread)
	{
		clientThreads.remove(thread);
		startListening();

	}
	
	/* Server thread. Processes request and returns result to client */

	private class WIWServerThread extends Thread 
	{
		Socket s;
		InputStream in;
		OutputStream out;

		public WIWServerThread(Socket s)
		{
			this.s = s;

			try
			{
				in = s.getInputStream();
				out = s.getOutputStream();
			}
			catch(Exception e)
			{
				System.err.println(getDateTime() + "Error accepting connection from " + s.getInetAddress());
				finish();
			}

			if(WIWConstants.VERBOSE_MODE)
			{
				try
				{
					System.err.println(getDateTime() + "Accepted connection from " + s.getInetAddress() + "\t(" + (getNumThreads() + 1) + "/" + WIWConstants.MAX_CLIENTS + ")");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(WIWConstants.DO_PING)
			{
				new WIWServerThreadPing(this).start();
			}
		}

		public void finish()
		{
			try
			{
				join();
				in.close();
				out.close();
				s.close();
				removeThread(this);

				if(WIWConstants.VERBOSE_MODE)
				{
					System.err.println(getDateTime() + "Server Thread handling " + s.getInetAddress() + " has completed.");
				}
			}
			catch(Exception e)
			{
				System.err.println(getDateTime() + "Error stopping Server Thread...");
			}
		}

		public synchronized InputStream getInputStream()
		{
			return in;
		}

		public synchronized Socket getSocket()
		{
			return s;
		}

		public void run()
		{
			//TODO...handle card request
		}

		private class WIWServerThreadPing extends Thread
		{
			WIWServerThread thread;
			InputStream in;
			Socket s;

			public WIWServerThreadPing(WIWServerThread thread)
			{
				this.thread = thread;
				in = thread.getInputStream();
				s = thread.getSocket();
			}

			public void run()
			{
				while(true)
				{
					try
					{
						if(in.read() == -1)
						{
							if(WIWConstants.VERBOSE_MODE)
							{
								System.err.println(getDateTime() + "Remote client " + s.getInetAddress() + " has disconnected.");
							}

							break;
						}

						sleep(WIWConstants.PING_TIMEOUT * 1000);
					}
					catch(Exception e)
					{
						System.err.println(getDateTime() + "Error pinging client...");
					}
				}

				try
				{
					thread.finish();
					join();
				}
				catch(Exception e)
				{
					System.err.println(getDateTime() + "Error stopping Server Ping Thread...");
					e.printStackTrace();
				}
			}
		}
	}
}

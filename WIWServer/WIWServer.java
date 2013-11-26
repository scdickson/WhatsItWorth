//Sam Dickson
//What's It Worth Server
//19 October 2013

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.image.*;
import javax.imageio.*;
import org.apache.commons.codec.binary.Base64;


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
		//DetectCard ben = new DetectCard();
		//System.err.println("THE ANSWER TO LIFE IS " + ben.run("Squire.jpg", "Camera.jpg"));
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

	}
	
	/* Server thread. Processes request and returns result to client */

	private class WIWServerThread extends Thread 
	{
		Socket s;
		InputStream in;
		OutputStream out;
		WIWServerThreadPing pingThread;

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
				pingThread = new WIWServerThreadPing(this);
				pingThread.start();
			}

			start();
		}

		public synchronized void finish()
		{
			if(!s.isClosed())
			{
				try
				{
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
			String fileName = null;

			try
			{
				/*DataInputStream dis = new DataInputStream(in);
				int length = dis.readInt();
				System.err.println("\t--Getting " + length + " bytes from client...");
				byte file[] = new byte[length];
				dis.readFully(file);
				System.err.println("\t--" + length + " bytes successfully received!");
				InputStream is = new ByteArrayInputStream(file);
				BufferedImage bi = ImageIO.read(is);
				fileName = System.currentTimeMillis() + ".jpg";
				ImageIO.write(bi, "jpg", new File(fileName));*/

				DataInputStream dis = new DataInputStream(in);
				String[] objectData = dis.readUTF().split(";");
				System.err.print("\t-> Received: " + objectData[1] + " of type ");
				itemCache DBRequest = new itemCache("192.168.1.12", 3306, "root", "kpcofgs");
				Type objectType = null;
				if(objectData[0].equalsIgnoreCase("C"))
				{
					objectType = Type.Card;
					System.err.println("CARD...");
				}
				else if(objectData[0].equalsIgnoreCase("M"))
				{
					objectType = Type.Currency;
					System.err.println("CURRENCY...");
				}
				else if(objectData[0].equalsIgnoreCase("S"))
				{
					objectType = Type.Stamp;
					System.err.println("STAMP...");
				}

				String result = "";
				System.err.println("\t-> Returning: (L/M/H)");

				for(String s : (DBRequest.getPrice(objectType, objectData[1])))
				{
					System.err.println("\t\t" + s);
					result += s + ";";
				}
				
				//Send price graph if object is card:
				if(objectType == Type.Card)
				{
					try
					{
						File f = new File("price_graphs/" + objectData[1] + ".png");
						System.err.println("\t-> Converting " + f.toString() + " for sending..."); 
						FileInputStream fis  = new FileInputStream(f);
						byte[] image_data = new byte[(int) f.length()];
						fis.read(image_data);
						String dataString = Base64.encodeBase64String(image_data);
						result += dataString;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

				System.err.println("\t-> Sending data to client...");
				DataOutputStream dos = new DataOutputStream(out);
				//PrintWriter writer = new PrintWriter(out);
				//writer.println(result);
				//writer.flush();
				dos.writeUTF(result);
				System.err.println("\t-> Data transfer complete (" + result.length() / 1000.0 + ") kb.");


				if(WIWConstants.DO_PING)
				{
					pingThread.finish();
				}
				else
				{
					finish();
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


		}

		private class WIWServerThreadPing extends Thread
		{
			WIWServerThread thread;
			InputStream in;
			Socket s;
			boolean pinging = true;

			public WIWServerThreadPing(WIWServerThread thread)
			{
				this.thread = thread;
				in = thread.getInputStream();
				s = thread.getSocket();
			}

			public synchronized void finish()
			{
				try
				{
					pinging = false;
					interrupt();
					thread.finish();
				}
				catch(Exception e)
				{
					System.err.println("Error stopping ping thread...");
				}
			}

			public void run()
			{
				while(pinging)
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
					catch(SocketException se)
					{
						//Perfectly Normal. Means WIWServerThread has finished.
					}
					catch(Exception e)
					{
						System.err.println(getDateTime() + "Error pinging client...");
						e.printStackTrace();
					}
				}

				try
				{
					finish();
				}
				catch(Exception e)
				{
					System.err.println(getDateTime() + "Error stopping Server Ping Thread...");
				}
			}
		}
	}
}

import java.net.*;
import java.io.*;

public class ItemServer
{
	public static final int PORT = 9990;

	ServerSocket ss;
	itemCache cache;

	public ItemServer()
	{
		try
		{
			cache = new itemCache("192.168.1.12", 3306, "root", "kpcofgs");
			ss = new ServerSocket(PORT);
		}
		catch(Exception e)
		{
			System.err.println("Fatal--Item Server could not start on port " + PORT);
			System.exit(0);
		}

		listen();
	}

	public void listen()
	{
		while(true)
		{
			try
			{
				Socket s = ss.accept();
				System.err.println("Item Server: Accepted connection from " + s.getInetAddress());
				DataInputStream in = new DataInputStream(s.getInputStream());
				String objectData = in.readUTF();
				cache.insertObject(Type.Card, objectData);
				System.err.println("Item Server: Added Card " + objectData);
				in.close();
				s.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{
		ItemServer is = new ItemServer();
	}
}

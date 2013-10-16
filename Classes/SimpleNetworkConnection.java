//Sam Dickson
//Simple Networking Class

import java.io.*;
import java.net.*;

public class SimpleNetworkConnection
{
  URL connection;
  InputStreamReader i;
  BufferedReader in;
  
  public SimpleNetworkConnection(String path, int port)
  {
    try
    {
      connection = new URL(path);
    }
    catch(Exception ex)
    {
      System.err.println("Error connecting to " + path + " on port " + port);
    }
  }
  
  public SimpleNetworkConnection(String path)
  {
    try
    {
      connection = new URL(path);
    }
    catch(Exception ex)
    {
      System.err.println("Error connecting to " + path);
    }
  }
  
  public String getSource()
  {
    String source = "";
    try
    {
    InputStreamReader i = new InputStreamReader(connection.openStream());
    BufferedReader in = new BufferedReader(i);
    String inputLine;
    while((inputLine = in.readLine()) != null)
    {
      source = source + inputLine;
    }
    in.close();
    }
    catch(Exception ex)
    {
      System.err.println("Error reading source of " + connection);
    }
    
    return source;
  }
  
}
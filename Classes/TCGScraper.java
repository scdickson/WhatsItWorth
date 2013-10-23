//Sam Dickson
//TCGScraper class

import java.net.*;
import java.io.*;
import java.util.*;

public class TCGScraper
{
  public static final String TCG_DB_URL = "http://store.tcgplayer.com/productcatalog/product/GlobalSearch";
  public static final String SEARCH_GAME_NAME = "SearchGameName=magic";
  public static final String IS_PRODUCT_NAME_EXACT = "IsProductNameExact=False";
  public static final String SEARCH_PRODUCT_NAME = "SearchProductName=";
  
  public static final String KEY_LOW = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#ff0000\">Low:</span> ";
  public static final String KEY_MED = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#0000ff\">Median:</span> ";
  public static final String KEY_HIGH = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#008000\">High:</span> ";
  
  //Test main method. Shows that case of search term doesn't matter.
  public static void main(String args[])
  {
    TCGScraper weboshi = new TCGScraper();
    System.out.println("Low: " + weboshi.getLowPrice("ANGEL OF SERENITY"));
    System.out.println("Med: " + weboshi.getMedPrice("angel of serenity"));
    System.out.println("High: " + weboshi.getHighPrice("AnGeL oF sErEnItY"));
  }
  
  
  public String getLowPrice(String cardName)
  {
    if(cardName == null || cardName.isEmpty())
    {
      return null;
    }
    
    String price = null;
    
    try
    {
      cardName = cardName.replace(" ", "+");
      String parameters = IS_PRODUCT_NAME_EXACT + "&" + SEARCH_PRODUCT_NAME + cardName + "&" + SEARCH_GAME_NAME;
      URL url = new URL(TCG_DB_URL);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(parameters);
      writer.flush();
      
      String line;
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while((line = in.readLine()) != null)
      {
        if(line.contains(KEY_LOW))
        {
          String tmp = line.substring(line.indexOf(KEY_LOW) + KEY_LOW.length());
          price = tmp.substring(0, tmp.indexOf("</span>"));
          break;
        }
      }
      writer.close();
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return price;
  }
  
  public String getMedPrice(String cardName)
  {
    if(cardName == null || cardName.isEmpty())
    {
      return null;
    }
    
    String price = null;
    
    try
    {
      cardName = cardName.replace(" ", "+");
      String parameters = IS_PRODUCT_NAME_EXACT + "&" + SEARCH_PRODUCT_NAME + cardName + "&" + SEARCH_GAME_NAME;
      URL url = new URL(TCG_DB_URL);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(parameters);
      writer.flush();
      
      String line;
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while((line = in.readLine()) != null)
      {
        if(line.contains(KEY_MED))
        {
          String tmp = line.substring(line.indexOf(KEY_MED) + KEY_MED.length());
          price = tmp.substring(0, tmp.indexOf("</span>"));
          break;
        }
      }
      writer.close();
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
    return price;
  }
  
  public String getHighPrice(String cardName)
  {
    if(cardName == null || cardName.isEmpty())
    {
      return null;
    }
    
    String price = null;
    
    try
    {
      cardName = cardName.replace(" ", "+");
      String parameters = IS_PRODUCT_NAME_EXACT + "&" + SEARCH_PRODUCT_NAME + cardName + "&" + SEARCH_GAME_NAME;
      URL url = new URL(TCG_DB_URL);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(parameters);
      writer.flush();
      
      String line;
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while((line = in.readLine()) != null)
      {
        if(line.contains(KEY_HIGH))
        {
          String tmp = line.substring(line.indexOf(KEY_HIGH) + KEY_HIGH.length());
          price = tmp.substring(0, tmp.indexOf("</span>"));
          break;
        }
      }
      writer.close();
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
    return price;
  }
 
}
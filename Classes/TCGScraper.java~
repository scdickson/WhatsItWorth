//Sam Dickson
//TCGScraper class

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TCGScraper
{ 
  public static void main(String args[])
  {
    TCGScraper weboshi = new TCGScraper();
    String prices[] = weboshi.getPrice(args[0]);
    for(String price : prices)
    {
      System.out.println(price);
    }
  }
  
  public String[] getPrice(String cardName)
  {
    return new TCGScraperThread(cardName).call();
  }
  
  private class TCGScraperThread implements Callable<String[]>
  {
    String cardName;
    
    public TCGScraperThread(String cardName)
    {
      this.cardName = cardName.replace(" ", "+");
    }
    
    public String[] call()
    {
      if(cardName == null || cardName.isEmpty())
      {
        return null;
      }
      
      String prices[] = new String[3];
      ExecutorService lowExecutor = Executors.newSingleThreadExecutor();
      ExecutorService medExecutor = Executors.newSingleThreadExecutor();
      ExecutorService highExecutor = Executors.newSingleThreadExecutor();

      try
      {
        Future lowFuture = lowExecutor.submit(new LowScraper(cardName));
        Future medFuture = medExecutor.submit(new MedScraper(cardName));
        Future highFuture = highExecutor.submit(new HighScraper(cardName));
        prices[0] = lowFuture.get().toString();
        prices[1] = medFuture.get().toString();
        prices[2] = highFuture.get().toString();
        lowExecutor.shutdown();
        medExecutor.shutdown();
        highExecutor.shutdown();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      
      
      return prices;
    }
    
    private class LowScraper implements Callable<String>
    {
      public static final String TCG_DB_URL = "http://store.tcgplayer.com/productcatalog/product/GlobalSearch";
      public static final String SEARCH_GAME_NAME = "SearchGameName=magic";
      public static final String IS_PRODUCT_NAME_EXACT = "IsProductNameExact=False";
      public static final String SEARCH_PRODUCT_NAME = "SearchProductName=";
      public static final String KEY_LOW = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#ff0000\">Low:</span> ";
      String cardName;
      
      public LowScraper(String cardName)
      {
        this.cardName = cardName;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
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
    
    private class MedScraper implements Callable<String>
    {
      public static final String TCG_DB_URL = "http://store.tcgplayer.com/productcatalog/product/GlobalSearch";
      public static final String SEARCH_GAME_NAME = "SearchGameName=magic";
      public static final String IS_PRODUCT_NAME_EXACT = "IsProductNameExact=False";
      public static final String SEARCH_PRODUCT_NAME = "SearchProductName=";
      public static final String KEY_MED = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#0000ff\">Median:</span> ";
      String cardName;
      
      public MedScraper(String cardName)
      {
        this.cardName = cardName;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
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
    
    private class HighScraper implements Callable<String>
    {
      public static final String TCG_DB_URL = "http://store.tcgplayer.com/productcatalog/product/GlobalSearch";
      public static final String SEARCH_GAME_NAME = "SearchGameName=magic";
      public static final String IS_PRODUCT_NAME_EXACT = "IsProductNameExact=False";
      public static final String SEARCH_PRODUCT_NAME = "SearchProductName=";
      public static final String KEY_HIGH = "<span style=\"display:block; padding-bottom:5px;\"><span style=\"color:#008000\">High:</span> ";
      String cardName;
      
      public HighScraper(String cardName)
      {
        this.cardName = cardName;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
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
  }
  
  
  
  
}

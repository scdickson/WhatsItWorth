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
      
      URL tcg_url = null;
      
      try
      {
        URL url = new URL("http://magiccards.info/query?q=!" + cardName + "&v=card&s=cname");
        String KEY_TCG = "http://partner.tcgplayer.com/x3/mchl.ashx?pk=MAGCINFO&sid=";
        URLConnection conn = url.openConnection();
        
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((line = in.readLine()) != null)
        {
          if(line.contains("<script type=\"text/javascript\" src=\"http://partner.tcgplayer.com/x3/mchl.ashx?pk=MAGCINFO&amp;sid="))
          {
            String tmp = line.substring(line.indexOf("sid=") + "sid=".length(), line.indexOf("\"></script>"));
            tcg_url = new URL(KEY_TCG + tmp);
            break;
          }
        }
      }
      catch(Exception e)
      {
      }
     
      String prices[] = new String[3];
      ExecutorService lowExecutor = Executors.newSingleThreadExecutor();
      ExecutorService medExecutor = Executors.newSingleThreadExecutor();
      ExecutorService highExecutor = Executors.newSingleThreadExecutor();

      try
      {
        Future lowFuture = lowExecutor.submit(new LowScraper(tcg_url));
        Future medFuture = medExecutor.submit(new MedScraper(tcg_url));
        Future highFuture = highExecutor.submit(new HighScraper(tcg_url));
        prices[0] = lowFuture.get().toString();
        prices[1] = medFuture.get().toString();
        prices[2] = highFuture.get().toString();
        lowExecutor.shutdown();
        medExecutor.shutdown();
        highExecutor.shutdown();
      }
      catch(Exception e)
      {
      }
      
      
      return prices;
    }
    
    private class LowScraper implements Callable<String>
    {
      public static final String KEY_LOW = "L: ";
      public static final String KEY_TCG = "http://partner.tcgplayer.com/x3/mchl.ashx?pk=MAGCINFO&sid=";
      URL tcgURL;
      
      public LowScraper(URL tcgURL)
      {
        this.tcgURL = tcgURL;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
          URLConnection conn = tcgURL.openConnection();     
          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line = "";
          line = in.readLine();
          line = line.substring(line.indexOf("L: ") + "L: ".length());
          line = line.substring(line.indexOf("$"));
          line = line.substring(0, line.indexOf("<"));
          price = line;
          in.close();
        }
        catch(Exception e)
        {
        }
        return price;
      }
    }
    
    private class MedScraper implements Callable<String>
    {
      public static final String KEY_MED = "M: ";
      public static final String KEY_TCG = "http://partner.tcgplayer.com/x3/mchl.ashx?pk=MAGCINFO&sid=";
     
      URL tcgURL;
      
      public MedScraper(URL tcgURL)
      {
        this.tcgURL = tcgURL;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
          URLConnection conn = tcgURL.openConnection();     
          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line = "";
          line = in.readLine();
          line = line.substring(line.indexOf("M: ") + "M: ".length());
          line = line.substring(line.indexOf("$"));
          line = line.substring(0, line.indexOf("<"));
          price = line;
          in.close();
        }
        catch(Exception e)
        {
        }
        return price;
      }
    }
    
    private class HighScraper implements Callable<String>
    {
      
      public static final String KEY_HIGH = "H: ";
      public static final String KEY_TCG = "http://partner.tcgplayer.com/x3/mchl.ashx?pk=MAGCINFO&sid=";
      URL tcgURL;
      
      public HighScraper(URL tcgURL)
      {
        this.tcgURL = tcgURL;
      }
      
      public String call()
      {
        String price = null;
        
        try
        {
          URLConnection conn = tcgURL.openConnection();     
          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line = "";
          line = in.readLine();
          line = line.substring(line.indexOf("H: ") + "H: ".length());
          line = line.substring(line.indexOf("$"));
          line = line.substring(0, line.indexOf("<"));
          price = line;
          in.close();
        }
        catch(Exception e)
        {
        }
        return price;
      }
    }
  }
  
  
  
  
}

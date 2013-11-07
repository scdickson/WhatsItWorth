//Sam Dickson
//4 Nov., 2013
//Currency Converter Class

import java.net.*;
import java.io.*;

public class CurrencyConverter
{
  public static final String API_KEY = "7e3cbe6fd3fdb0da134f8a215c9e6b6e5b5016ae";
  public static final String API_URL = "http://currency-api.appspot.com/api/";
  public static final String[] SUPPORTED_CURRENCY = {"AUD","CAD","CHF","DKK","EUR","GBP","HKD","JPY","MXN","NZD","PHP","SEK","SGD","THB","USD","ZAR"};
  
  public double convert(String source, String target, double amount)
  {
    source = source.toUpperCase();
    target = target.toUpperCase();
    
    boolean source_ok = false;
    boolean target_ok = false;
    
    for(String currency : SUPPORTED_CURRENCY)
    {
      if(currency.equals(source))
      {
        source_ok = true;
      }
      else if(currency.equals(target))
      {
        target_ok = true;
      }
      
      if(source_ok && target_ok)
      {
        break;
      }
    }
    
    if(source_ok && target_ok && amount >= 0)
    {
      try
      {
        URL url = new URL(API_URL + source + "/" + target + ".json?amount=" + amount + "&key=" + API_KEY);
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String tokens[] = in.readLine().split(", ");
        String raw_amount[] = tokens[4].split(": ");
        return Double.parseDouble(raw_amount[1]);
        
      }
      catch(Exception e){}
    }
    
    return -1;
  }
  
  public static void main(String args[])
  {
    if(args.length != 3)
    {
      System.err.println("Invalid arguments. Arguments are: Source Currency, Target Currency, Amount.\ne.g., CurrencyConverter JPY USD 10.00");
      System.err.println("\n-----Supported Currencies:-----\nAUD Australian Dollar\nCAD Canadian Dollar\nCHF Swiss Franc\nDKK Danish Krone\nEUR Euro\nGBP Pound Sterling\nHKD Hong Kong Dollar\nJPY Japanese Yen\nMXN Mexican Peso\nNZD New Zealand Dollar\nPHP Philippine Peso\nSEK Swedish Krona\nSGD Singapore Dollar\nTHB Thailand Baht\nUSD United States Dollar\nZAR South African Rand\n-------------------------------\n");
    }
    else
    {
      CurrencyConverter weboshi = new CurrencyConverter();
      if(args[0].equals(args[1]))
      {
        System.err.println("Try specifying different source and target currencies...");
      }
      else
      {
        try
        {
          double amount = weboshi.convert(args[0], args[1], Double.parseDouble(args[2]));
          
          if(amount >= 0)
          {
            System.out.println(Double.parseDouble(args[2]) + " " + args[0] + " = " + amount + " " + args[1]);
          }
          else
          {
            System.err.println("Unsupported currency type or unknown error.");
          }
        }
        catch(Exception e)
        {
          System.err.println("Check format of amount...");
        }
      }
    }
  }
}
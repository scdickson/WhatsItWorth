//Theo Reinke
//CS307 What's it Worth

//import java.io.*;
import java.util.*;
import java.text.*;


public class itemCache
{
  protected DBConnection conn = null;
  private ArrayList<String> results;
  final static long DAY_MILLIS = 86400000;
  
  public static void main(String[] args)
  {
    itemCache db = new itemCache("192.168.1.12", 3306, "root", "kpcofgs");
    Type mtgCard;  
    mtgCard = Type.Card;
    //db.insertObject(mtgCard, "Pack Rat");
    String[] prices = db.getPrice("Pack Rat");
    System.out.println("low: " + prices[0]);
    System.out.println("median: " + prices[1]);
    System.out.println("high: " + prices[2]);
  }
  
  public itemCache(String url, int port, String username, String password)
  {
   conn = new DBConnection(url, port, username, password);
   conn.connect("WIWDB");
   conn.detailedErrorOn(true);
   results = new ArrayList<String>();
  }
  
  private void insertObject(Type objectType, String name)
  {
    name = name.replace("'","\\'");
    //DATETIME: YYYY-HH-DD HH:MM:SS
    Date dateStamp = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //INSERT INTO itemCache (objectType, Name, last_Update)
    //VALUES (objectType, name, currentDate());
    String query = "INSERT INTO itemCache (objectType, Name, last_Update) VALUES (\'" + objectType + "\', \'" + name + "\', \'" + ft.format(dateStamp) + "\');";
    //System.out.println(query);
    results = conn.query(query);
  }
  
  public String[] getPrice(String name)
  {
    String[] prices = new String[3];
    //Query db for price of object and lastUpdate
    String query = "SELECT last_Update FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    //parse out LAST UPDATE: section from result
    String stamp = results.get(0);
    stamp = stamp.replace("LAST UPDATE: ","");
    //System.out.println(stamp);
    Date lastUpdate;
    try{
    	lastUpdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(stamp);
    } catch(ParseException e){
    	return prices;
    }
    
    //Date lastUpdate = new Date(stamp);
    Date curr = new Date();
    //if lastUpdate > 24 get new price and write to db
    Date temp = new Date(lastUpdate.getTime() + DAY_MILLIS);
    if(curr.after(temp)){
    	//get current prices
    	System.out.println("Calling scraper!");
	TCGScraper scraper = new TCGScraper();
    	prices = scraper.getPrice(name);
    	//insert prices and update timestamp
    	//UPDATE itemCache
    	//SET last_Update = 'curr', price_Low = 'prices[0]', price_Median = 'prices[1]', price_High = 'prices[2]'
    	//WHERE Name = 'name';
    	SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	query = "UPDATE itemCache SET last_Update = \'" + ft.format(curr) + "\', price_Low = \'" + prices[0] + "\', price_Median = \'" + prices[1] + "\', price_High = \'" + prices[2] + "\'";
    	query = query + " WHERE Name = \'" + name + "\';";
    	results = conn.query(query);
    } 
    //return price as String[]
    //Get low price
    query = "SELECT price_Low FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    String temp2 = results.get(0);
    prices[0] = temp2.replace("price_Low: ", "");
    //Get median price
    query = "SELECT price_Median FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    temp2 = results.get(0);
    prices[1] = temp2.replace("price_Median: ", "");
    //Get high price
    query = "SELECT price_High FROM itemCache WHERE name = \'" + name + "\'";
    results = conn.query(query);
    temp2 = results.get(0);
    prices[2] = temp2.replace("price_High: ", "");
    return prices;
  }

}

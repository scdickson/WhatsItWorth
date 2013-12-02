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
    Type curr;
    curr = Type.Currency;
    
    db.insertObject(curr, "CNY_1");
    
    //String[] prices = db.getPrice(mtgCard, "Staff of the Death Magus");
    //System.out.println("low: " + prices[0]);
    //System.out.println("median: " + prices[1]);
    //System.out.println("high: " + prices[2]);
  }
  
  public itemCache(String url, int port, String username, String password)
  {
   conn = new DBConnection(url, port, username, password);
   conn.connect("WIWDB");
   conn.detailedErrorOn(true);
   results = new ArrayList<String>();
  }
  
  public void insertObject(Type objectType, String name)
  {
    
    name = name.replace("'","\\'");
    String[] prices = new String[3];
    Calendar currTime = Calendar.getInstance();
    Calendar lateTime = Calendar.getInstance();
    lateTime.add(Calendar.MINUTE, 1);
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if(objectType == Type.Card){//Get prices
    	TCGScraper scraper = new TCGScraper();
    	prices = scraper.getPrice(name);
	MTGStockScraper graphScraper = new MTGStockScraper();
	String path = graphScraper.getPriceHistory(name);
    }
    if(objectType == Type.Currency){//Parse value from name
	String amt = name.substring(4,name.length());
	prices[0] = amt;
	prices[1] = amt;
	prices[2] = amt;
    }
    //INSERT INTO itemCache (objectType, Name, last_Update, price_Low, price_Median, price_High, cache_policy, graph_update)
    //VALUES (objectType, name, currentDate());
    String query = "INSERT INTO itemCache (objectType, Name, last_Update, price_Low, price_Median, price_High, cache_policy, graph_update) VALUES (\'" + objectType + "\', \'" + name + "\', \'" + ft.format(currTime.getTime()) + "\', \'" + prices[0] + "\', \'" + prices[1] + "\', \'" + prices[2] + "\', '0', \'" + ft.format(lateTime.getTime()) + "\');";
    results = conn.query(query);

  }
  
  public String[] getPrice(Type objectType, String name)
  {
    String[] prices = new String[3];
    //If currency convert here and return since no caching needs to be done
    String query;
    if(objectType == Type.Currency){
	CurrencyConverter cc = new CurrencyConverter();
	query = "SELECT price_Low FROM itemCache WHERE name = \'" + name + "\';";
	results = conn.query(query);
	String parsed = results.get(0);
	parsed = parsed.replace("PRICE LOW: ", "");
	double currAmount = Double.parseDouble(parsed);
	name = name.substring(0, 3);
	double convertedAmount = cc.convert(name, "USD", currAmount);
	DecimalFormat df = new DecimalFormat("#.##");
	parsed = "$" + String.valueOf(df.format(convertedAmount));
	prices[0] = parsed;
	prices[1] = parsed;
	prices[2] = parsed;
	return prices;
    }
   
    //Query db for price of object and lastUpdate
    query = "SELECT last_Update FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    //parse out LAST UPDATE: section from result
    String stamp = results.get(0);
    stamp = stamp.replace("LAST UPDATE: ","");
    Date lastUpdate;
    try{
    	lastUpdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(stamp);
    } catch(ParseException e){
    	return prices;
    }
    //Calendar priceTimeStamp = priceTimeStamp.setTime(lastUpdate);
    
    query = "SELECT graph_update FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    //parse out LAST UPDATE: section from result
    String graphStamp = results.get(0);
    graphStamp = graphStamp.replace("GRAPH UPDATE: ","");
    //System.out.println(stamp);
    Date lastGraphUpdate;
    Date curr = new Date();
    try{
	 lastGraphUpdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(graphStamp);
    } catch(ParseException e){
    	return prices;
    }


    //Flags
    //0 normal
    //1 update everytime
    //2 never
    int flag;
    query = "SELECT cache_policy FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    String resultString = results.get(0);
    resultString = resultString.replace("CACHE POLICY: ", "");
    flag = Integer.parseInt(resultString);
    //price_graphs/name.png
    //use MTGStockScraper.getPriceHistory
    //if one is over 24 hours update it, if both are over 24 hours, update older
    Date pricePlusDay = new Date(lastUpdate.getTime() + DAY_MILLIS);
    Date graphPlusDay = new Date(lastGraphUpdate.getTime() + DAY_MILLIS);
    //System.out.println("curr: " + curr.toString());
    //System.out.println("pricePlusDay: " + pricePlusDay.toString());
    //System.out.println("graphPlusDay: " + graphPlusDay.toString());
    if((curr.after(pricePlusDay) || curr.after(graphPlusDay) || flag == 1) && flag !=2){
    	//get current prices
	//Update older of prices and graph
	//System.out.println("lastUpdate: " + lastUpdate.toString());
	//System.out.println("lastGraphUpdate: " + lastGraphUpdate.toString());
	if(lastGraphUpdate.after(lastUpdate)){
		System.out.println("Updating prices!");
		TCGScraper scraper = new TCGScraper();
    		prices = scraper.getPrice(name);
		prices[0] = prices[0].replace("price_Low: ", "");
		prices[1] = prices[1].replace("price_Median: ", "");
		prices[2] = prices[2].replace("price_High: ", "");
    		//insert prices and update timestamp
    		//UPDATE itemCache
    		//SET last_Update = 'curr', price_Low = 'prices[0]', price_Median = 'prices[1]', price_High = 'prices[2]'
    		//WHERE Name = 'name';
    		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		query = "UPDATE itemCache SET last_Update = \'" + ft.format(curr) + "\', price_Low = \'" + prices[0] + "\', price_Median = \'" + prices[1] + "\', price_High = \'" + prices[2] + "\'";
    		query = query + " WHERE Name = \'" + name + "\';";
    		//System.out.println("QUERY: " + query);
		results = conn.query(query);
		return prices;

	}
	else{
		System.out.println("Updating graph!");
		if(objectType == Type.Card){
			MTGStockScraper graphScraper = new MTGStockScraper();
			String path = graphScraper.getPriceHistory(name);
		}
		else if(objectType == Type.Currency){
			CurrencyGraphScraper graphScraper = new CurrencyGraphScraper();
			String prefix = name.substring(0, name.indexOf("_"));
			graphScraper.getCurrencyGraph(prefix);
		}
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		query = "UPDATE itemCache SET graph_Update = \'" + ft.format(curr) + "\'";
		query = query + " WHERE Name = \'" + name + "\';";
		//System.out.println("QUERY: " + query);
		results = conn.query(query);


	}
    } 
    //return price as String[]
    //Get low price
    query = "SELECT price_Low FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    String temp2 = results.get(0);
    prices[0] = temp2.replace("PRICE LOW: ", "");
    //Get median price
    query = "SELECT price_Median FROM itemCache WHERE name = \'" + name + "\';";
    results = conn.query(query);
    temp2 = results.get(0);
    prices[1] = temp2.replace("PRICE MEDIAN: ", "");
    //Get high price
    query = "SELECT price_High FROM itemCache WHERE name = \'" + name + "\'";
    results = conn.query(query);
    temp2 = results.get(0);
    prices[2] = temp2.replace("PRICE HIGH: ", "");
    return prices;
  }

}

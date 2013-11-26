//Sam Dickson
//MTGStockScraper class

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.text.Format.*;
import com.xeiam.xchart.*;
import com.xeiam.xchart.StyleManager.*;

public class MTGStockScraper
{
  public static final String MTG_DB_URL = "http://www.mtgstocks.com/cards/search?utf8=%E2%9C%93&print%5Bcard%5D=";
  public static final String DATE_HDR = "<td class='center'>";
  public static final String LOW_HDR = "<td class='lowprice'>";
  public static final String MED_HDR = "<td class='avgprice'>";
  public static final String HIGH_HDR = "<td class='highprice'>";
  
  public static void main(String args[])
  {
    MTGStockScraper weboshi = new MTGStockScraper();
    weboshi.getPriceHistory(args[0]);
    
  }
    
  public String getPriceHistory(String cardName)
  {
    Chart chart = new ChartBuilder().width(800).height(600).theme(ChartTheme.Matlab).title(cardName + " Price History").xAxisTitle("Date").yAxisTitle("Price").build();
    chart.getStyleManager().setPlotGridLinesVisible(true);
    chart.getStyleManager().setLegendVisible(false);
    chart.getStyleManager().setChartTitleVisible(false);
    chart.getStyleManager().setAxisTitlesVisible(false);
    chart.getStyleManager().setAxisTickLabelsFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    Collection<Date> xData = new ArrayList<Date>();
    Collection<Number> lowData = new ArrayList<Number>();
    Collection<Number> medData = new ArrayList<Number>();
    Collection<Number> highData = new ArrayList<Number>();
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    try
    {
      URL url = new URL(MTG_DB_URL + cardName);
      URLConnection conn = url.openConnection();
      String line = "";
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while((line = in.readLine()) != null)
      {
        if(line.equals("<table class='table center-table table-condensed'>"))
        {
          //Skip ahead to first relevant row (ignore column headers)
          in.readLine();
          while(!line.equals("<tr>"))
          {
            line = in.readLine();
          }
          //End skip
          
          while(!line.equals("</table>"))
          {
            boolean eof = false;
            
            while(!line.contains(DATE_HDR))
            {
              if(line.equals("</table>"))
              {
                eof = true;
                break;
              }
              
              line = in.readLine();
            }

            if(eof)
            {
              break;
            }
            
            Date tmp = sdf.parse(line.substring(line.indexOf(DATE_HDR)+DATE_HDR.length()+1,line.indexOf("</td>")));
            xData.add(tmp);
            
            while(!line.contains(LOW_HDR))
            {
              line = in.readLine();
            }
            
            lowData.add(Double.parseDouble(line.substring(line.indexOf(LOW_HDR)+LOW_HDR.length()+2,line.indexOf("</td>"))));
            
            while(!line.contains(MED_HDR))
            {
              line = in.readLine();
            }
            
            medData.add(Double.parseDouble(line.substring(line.indexOf(MED_HDR)+MED_HDR.length()+2,line.indexOf("</td>"))));
            
            while(!line.contains(HIGH_HDR))
            {
              line = in.readLine();
            }
            
	    Double d = (Double.parseDouble(line.substring(line.indexOf(HIGH_HDR)+HIGH_HDR.length()+1,line.indexOf("</td>"))));
	    highData.add(d);
            
          }
          
          break;
        }
      }
      
      Series lowSeries = chart.addDateSeries("Low Price", xData, lowData);
      lowSeries.setLineStyle(new BasicStroke(13));
      lowSeries.setLineColor(SeriesColor.RED);
      Series medSeries = chart.addDateSeries("Medium Price", xData, medData);
      medSeries.setLineStyle(SeriesLineStyle.SOLID);
      medSeries.setLineStyle(new BasicStroke(13));
      medSeries.setLineColor(SeriesColor.BLUE);
      Series highSeries = chart.addDateSeries("High Price", xData, highData);
      highSeries.setLineStyle(SeriesLineStyle.SOLID);
      highSeries.setLineStyle(new BasicStroke(13));
      highSeries.setLineColor(SeriesColor.GREEN);
      String filePath = "price_graphs/" + cardName + ".png";
      BitmapEncoder.savePNG(chart, filePath);
      return filePath;

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    return null;
  }
  
  
  
  
  
}

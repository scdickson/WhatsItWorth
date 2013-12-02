import java.net.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class CurrencyGraphScraper
{
	public static final String url = "http://tools.currenciesdirect.net/currency-chart/currencychart.ajax.php";
	public static final String image_url = "http://tools.currenciesdirect.net/currency-chart/temp_images/chart_image.png?rand=";

	public static void main(String args[])
	{
		CurrencyGraphScraper cgs = new CurrencyGraphScraper();
		cgs.getCurrencyGraph(args[0]);
	}

	public void getCurrencyGraph(String currency)
	{
		try
		{
			String params = "xfrom=" + currency + "&xto=USD&xperiod=1_month";
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(params);
			writer.flush();
			
			String line;
			String ret = null;

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while((line = in.readLine()) != null)
			{
				if(line.contains(image_url))
				{
					ret = line.substring(line.indexOf(image_url), line.indexOf("alt=")-2);
				}
			}

			if(ret != null)
			{
				URL tmp = new URL(ret);
				BufferedImage image = ImageIO.read(tmp);
				File output = new File("price_graphs/" + currency + ".png");
				ImageIO.write(image, "png", output);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}

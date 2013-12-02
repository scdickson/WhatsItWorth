package com.qualcomm.QCARSamples.CloudRecognition.view;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import com.qualcomm.QCARSamples.CloudRecognition.R;
import com.qualcomm.QCARSamples.CloudRecognition.model.HistoryListItem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HistoryListActivity extends Activity {
	TextView totalTV;
	Context context;
	ListView history_list;
	ArrayList<HistoryListItem> historyList = new ArrayList<HistoryListItem>(); 

	private class ListAdapter extends BaseAdapter
	{
		LayoutInflater rowInflater;
		
		
		
		@Override
		public int getCount() {
			return historyList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return historyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			rowInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = rowInflater.inflate(R.layout.row_layout, parent, false);
			LinearLayout cedric = (LinearLayout) rowView.findViewById(R.id.cedric);
			cedric.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					Toast.makeText(context, "HELLO", 1000);
				}
			});
			
			TextView nameTV = (TextView)rowView.findViewById(R.id.item_name);
			TextView priceTV = (TextView)rowView.findViewById(R.id.item_price);
			HistoryListItem hli = historyList.get(position);
			nameTV.setText(hli.getName());
			if (hli.getType().equals("m"))
			{
				priceTV.setText(Html.fromHtml("<font color=\"#8888FF\">" +  hli.getMedPriceString() + "</font>"));
			}
			else
			{
				priceTV.setText(Html.fromHtml("<font color=\"#FF8888\">" + hli.getLowPriceString() + " | " + "</font><font color=\"#8888FF\">" + hli.getMedPriceString() + " | " + "</font><font color=\"#88FF88\">" + hli.getHighPriceString() + "</font>"));
			}
			return rowView;
		}
		
		
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.list_activity_layout);
        
        history_list = (ListView)findViewById(R.id.list);
        totalTV = (TextView)findViewById(R.id.totalTextView);
        displayList(); //call to display the list
        displayTotal(); //call to display the total
    }

    public void displayList()
    {
    	if (historyList.isEmpty())
    	{
    		System.out.print("The list is empty");
    		//maybe make a dialogbox with explanation to the user?
    	}
    	
    	//Iterator<HistoryListItem> it = historyList.iterator();
    	
    	/*Get Intent*/
    	Intent in = getIntent();
    	ArrayList<HistoryListItem> HList = (ArrayList<HistoryListItem>) in.getSerializableExtra("historyList");
    	if (HList.isEmpty())
    	{
    		Log.d("ListTest","HList is Empty");
    	}
    	historyList.addAll(HList);
    	if (historyList.isEmpty())
    	{
    		Log.d("ListTest","historyList is Empty");
    	}
    	ListAdapter adapter = new ListAdapter();
    	history_list.setAdapter(adapter);
    	
  
    }
    public void displayTotal()
    {
    	Iterator<HistoryListItem> it = historyList.iterator();
    	double runningTotal = 0;
    	boolean filledList = false;
    	String totalString;
        while (it.hasNext())
        {
        	filledList = true;
        	HistoryListItem obj = it.next();
        	runningTotal += obj.getMedDouble();
        		
        }
        if (filledList == false)
        {
        	totalTV.setText("No History Total to Display");
        	return;
        }
        DecimalFormat df = new DecimalFormat("###.##");
        totalTV.setText("Total: $" + df.format(runningTotal));
    }
    
}
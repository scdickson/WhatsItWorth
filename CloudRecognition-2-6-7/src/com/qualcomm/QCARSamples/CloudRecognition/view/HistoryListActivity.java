package com.qualcomm.QCARSamples.CloudRecognition.view;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import com.qualcomm.QCARSamples.CloudRecognition.R;
import com.qualcomm.QCARSamples.CloudRecognition.model.HistoryListItem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryListActivity extends Activity {
	TextView totalTV;
	ListView history_list;
	ArrayList<HistoryListItem> historyList = new ArrayList<HistoryListItem>(); 
	
	public void addToHistory(HistoryListItem item)
	{
		historyList.add(item);
		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);
        
        history_list = (ListView)findViewById(R.id.list);
        totalTV = (TextView)findViewById(R.id.totalTextView);
        displayList(); //call to display the list
        displayTotal(); //call to display the total
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.history_list, menu);
        return true;
    }
    */
    public void displayList()
    {
    	if (historyList.isEmpty())
    	{
    		System.out.print("The list is empty");
    		//maybe make a dialogbox with explanation to the user?
    	}
    	
    	//Iterator<HistoryListItem> it = historyList.iterator();
    	
    	/*Get Intent*/
    	Intent in = this.getIntent();
    	ArrayList<HistoryListItem> HList = new ArrayList<HistoryListItem>();
    	HList = in.getParcelableArrayListExtra("historyList");
    	
    	
    	ArrayAdapter<HistoryListItem> adapter = new ArrayAdapter<HistoryListItem>(this, android.R.layout.simple_list_item_1, HList);
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
        totalString = Double.toString(runningTotal);
        totalTV.setText("Total: " + totalString);
    }
    
}
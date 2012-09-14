package com.datapath.telepath;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

/*
 * Shows list of possible regions based on gps coordinates
 * They are passed in as a json Object that has json Objects inside it. Geez...
 */

public class SelectRegionActivity extends PsyborgListActivity{
	
	Gson gson = new Gson();
	
	Intent toRegionOverview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		Intent intent = getIntent();
		String jsonRegionList = intent.getStringExtra("regionKey");
		
		String[] regionNames = {"No Regions Available"};
		
		if(jsonRegionList.length()>4){
			RegionList regionList = gson.fromJson(jsonRegionList, RegionList.class);

			regionNames = new String[regionList.validRegions.size()];

			for (int i = 0; i<regionList.validRegions.size();i++){
				//what was I doing here? I don't get why a region class is needed. This is awful.
				//I think I didn't know how to use gson for collections
				Region region = gson.fromJson((String) regionList.validRegions.get(i), Region.class);
				regionNames[i]=region.regionName;
			}
		}
		
		
 
		setListAdapter(new ArrayAdapter<String>(this, R.layout.select_region,regionNames));
 
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		toRegionOverview = new Intent(this, OverviewActivity.class);
 
		listView.setOnItemClickListener(new RegionItemClickListener(toRegionOverview, this));
 
	}
	class RegionItemClickListener implements OnItemClickListener{
		Intent intent;
		Context context;

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int whatIsThisParameter,
				long alsoThisOneIzConfused) {
			
	    	String MESSAGE_KEY = "desiredRegion";
	    	String desiredRegion = (String) ((TextView) view).getText();
	    	
	    	SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
	    	SharedPreferences.Editor prefsEditor = myPrefs.edit();
	    	prefsEditor.putString("currentRegion", desiredRegion.replace(" ", "_"));
	    	prefsEditor.commit();
	    	
	    	intent.putExtra(MESSAGE_KEY, desiredRegion);
	    	startActivity(intent);
			
		}
		public RegionItemClickListener(Intent intent, Context context){
			this.intent=intent;
			this.context = context;
		}
		
	}
	
}

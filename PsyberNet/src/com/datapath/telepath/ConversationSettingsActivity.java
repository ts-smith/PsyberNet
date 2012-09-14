package com.datapath.telepath;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.datapath.telepath.ConversationFragment.UpdateSound;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
 * Currently Ui for selecting filter items 
 */

public class ConversationSettingsActivity extends Activity {

	SharedPreferences myPrefs;
	ListView filterView;
	String regionTopicsString;
	String[] regionTopicsArray;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_settings);
		
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);

		regionTopicsString = myPrefs.getString("regionTopics", "");

		if (regionTopicsString == ""){
			regionTopicsArray = new String[] {};
		}
		else{
			//this is not really a good way to do things, might want to use a static hashSet populated at ConversationActivity's main update loop
			
			String[] tempArray = regionTopicsString.split(",");
			
			HashSet<String> topicSet = new HashSet<String>();
			
			topicSet.addAll(Arrays.asList(tempArray));
			
			tempArray = new String[topicSet.size()];
			
			
			regionTopicsArray = topicSet.toArray(tempArray);
		}
		


		filterView = (ListView)findViewById(R.id.conversation_settings_filter_view);

		filterView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, regionTopicsArray));
		filterView.setItemsCanFocus(false);
		filterView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		//maybe should have two listviews, and the commit button be a footer
		//each listview could be for the different list of settings
		
		//maybe one listview for filter, and the other area more hard coded, or a scroll view
		//other area would be for other settings, like sounds or ?
		//other settings could be from menu

	}
	//exit activity and apply filter
	public void conversationSettingsCommitTopic(View view){

		
		SparseBooleanArray checked = filterView.getCheckedItemPositions();
		
		Set<String> filterList = new HashSet<String>();
		
		for (int i = 0; i < regionTopicsArray.length; i++) {
			
			
			boolean isChecked = checked.get(i);

			if (isChecked){
				filterList.add((String) filterView.getItemAtPosition(i));
			}
		} 
		ConversationFilter.topicFilter = filterList;
		
		ConversationFilter.appliedRegion = myPrefs.getString("currentRegion", "RegionInitializationError");
		
		//prevent complete depopulation
		ConversationFilter.idFilter=-1;
		ConversationFilter.lockSet=false;
		
		ConversationFragment.sound = UpdateSound.FilterSet;
		ConversationFragment.updateListView(true);
		
		
		
		//may not want to keep this
		finish();
	}

}

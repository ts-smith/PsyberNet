package com.datapath.telepath;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

public class ViewInboxActivity extends PsyborgListActivity {

	ArrayList<String> names = null;
	Gson gson = new Gson();
	Intent toPmConversation;

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList("names", names);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

		if (savedInstanceState == null){

			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(3);

			String url = "http://telepathwebserver.appspot.com/getPmList";

			parameters.add(new BasicNameValuePair("user", myPrefs.getString("userName", null)));//should never be null

			parameters.add(new BasicNameValuePair("password", myPrefs.getString("password", null)));//ditto

			String response = WebInterface.postToUrl(url, parameters, true);

			
			
			if (response != null){
				names = gson.fromJson(response, ArrayList.class);
			}

		}
		else{
			names = savedInstanceState.getStringArrayList("names");
		}


		setListAdapter(new ArrayAdapter<String>(this, R.layout.view_inbox_activity, names));
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		toPmConversation = new Intent(this, PmConversationActivity.class);

		listView.setOnItemClickListener(new PmListItemClickListener(toPmConversation, this));
	}


	class PmListItemClickListener implements OnItemClickListener{
		Intent intent;
		Context context;

		public PmListItemClickListener(Intent intent, Context context){
			this.intent=intent;
			this.context = context;
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int arg2,
				long arg3) {
			// Auto-generated method stub, don't know what those variables are

			String target = ((TextView) view).getText().toString();

			intent.putExtra("target", target);

			startActivity(intent);


		}
		
	}
	 
}

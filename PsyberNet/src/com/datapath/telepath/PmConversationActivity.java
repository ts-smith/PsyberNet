package com.datapath.telepath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.google.gson.Gson;

//show conversation between user and selected person
public class PmConversationActivity extends PsyborgActivity{
	Gson gson = new Gson();

	String jsonConversation = null;

	SharedPreferences myPrefs;

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("jsonConversation", jsonConversation);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pm_conversation_layout);

		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

		String requester = myPrefs.getString("userName", "error");

		if (savedInstanceState == null){

			Intent intent = getIntent();


			String target = intent.getStringExtra("target");

			String passwordHash = myPrefs.getString("password", "error");

			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(3);

			parameters.add(new BasicNameValuePair("requester", requester));
			parameters.add(new BasicNameValuePair("target", target));
			parameters.add(new BasicNameValuePair("password", passwordHash));

			String url = "http://telepathwebserver.appspot.com/getPmConversation";

			jsonConversation = WebInterface.postToUrl(url, parameters, true);


		}

		else{
			jsonConversation = savedInstanceState.getString("jsonConversation");
		}
		ArrayList<ArrayList<String>> pmMessageList = gson.fromJson(jsonConversation, ArrayList.class);

		for (int i = 0; i < pmMessageList.size(); i++) {
			ArrayList<String> message = pmMessageList.get(i);
			String sentString = message.get(2);

			//keep getting parseException, I don't understand
			/*
				SimpleDateFormat parserSDF=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
				sent = parserSDF.parse(sentString);

				SimpleDateFormat displayFormat = new SimpleDateFormat("YY/MM/dd HH:mm");
			 */
			message.set(2, sentString.substring(4, 20));

		}

		LayoutInflater inflater = getLayoutInflater();  
		PmConversationAdapter pmAdapter = new PmConversationAdapter(pmMessageList, inflater, requester);

		ListView listView = (ListView) findViewById(R.id.pm_conversation_listview);
		listView.setAdapter(pmAdapter);

	}
}

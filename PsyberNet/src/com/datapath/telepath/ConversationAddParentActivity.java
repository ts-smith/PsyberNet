package com.datapath.telepath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

	/*Dialog for adding a parent node to the current region
	 */


public class ConversationAddParentActivity extends PsyborgActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversations_add_parent);
	}
	//on "send" pressed
	public void conversationsAddParentSend(View view){

		EditText contentView =(EditText) findViewById(R.id.conversationAddParentContent);
		String message = contentView.getText().toString();

		EditText topicView = (EditText) findViewById(R.id.conversationAddParentTopic);
		String topic = topicView.getText().toString();


		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		NameValuePair content = new BasicNameValuePair("content", message);
		parameters.add(content);

		NameValuePair topicPair = new BasicNameValuePair("topic", topic);
		parameters.add(topicPair);

		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		String userName = myPrefs.getString("userName", MainActivity.makeAnonTag());//<-shouldn't happen
		NameValuePair poster = new BasicNameValuePair("poster", userName);
		parameters.add(poster);

		Date now = new Date();
		SimpleDateFormat displayFormat = new SimpleDateFormat("E kk:mm:ss");
		String postTime = displayFormat.format(now);
		NameValuePair date = new BasicNameValuePair("date", postTime);
		parameters.add(date);

		String selectedRegion = myPrefs.getString("currentRegion", "RegionInitializationError");
		NameValuePair region = new BasicNameValuePair("region", selectedRegion);
		parameters.add(region);

		if (!userName.startsWith("Anon-")){

			String passwordHash = myPrefs.getString("password", null);
			NameValuePair passwordNumPair = new BasicNameValuePair("password", passwordHash);
			parameters.add(passwordNumPair);
		}


		String url = ("http://telepathwebserver.appspot.com/postParentToConversations");
		WebInterface.postToUrl(url, parameters, false);


		finish();

	}
}

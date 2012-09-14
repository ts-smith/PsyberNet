package com.datapath.telepath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/*
 * Dialog to add reply to the end of the chain which launched the activity.
 * Sends conversation id to notify place in server.
 */

public class ConversationAddReplyActivity extends PsyborgActivity {

	String regionString;
	String posterString;
	String idNumString;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_add_reply_activity);

		Intent intent = getIntent();

		regionString = intent.getStringExtra("region");
		posterString = intent.getStringExtra("poster");
		idNumString = intent.getStringExtra("idNum");
		
		EditText messageView = (EditText) findViewById(R.id.conversation_add_reply_content);
		
		messageView.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	public void conversationsAddReplySend(View view){


		LinearLayout parent = (LinearLayout) view.getParent();
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

		NameValuePair regionPair = new BasicNameValuePair("region", regionString);
		parameters.add(regionPair);

		NameValuePair posterPair = new BasicNameValuePair("poster", posterString);
		parameters.add(posterPair);

		String contentString = ((EditText)parent.findViewById(R.id.conversation_add_reply_content)).getText().toString();
		NameValuePair contentPair = new BasicNameValuePair("content", contentString);
		parameters.add(contentPair);

		//may want date to be found server side
		Date now = new Date();
		SimpleDateFormat displayFormat = new SimpleDateFormat("E kk:mm:ss");
		String postTime = displayFormat.format(now);
		NameValuePair date = new BasicNameValuePair("date", postTime);
		parameters.add(date);

		NameValuePair idNumPair = new BasicNameValuePair("idNum", idNumString);
		parameters.add(idNumPair);


		if (!posterString.startsWith("Anon-")){

			SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
			String passwordHash = myPrefs.getString("password", null);
			NameValuePair passwordNumPair = new BasicNameValuePair("password", passwordHash);
			parameters.add(passwordNumPair);
		}




		String url = ("http://telepathwebserver.appspot.com/replyToConversations");

		
		
		WebInterface.postToUrl(url, parameters, false);
		
		finish();			
	}
}

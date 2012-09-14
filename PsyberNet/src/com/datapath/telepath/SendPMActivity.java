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
import android.widget.Toast;

/*
 * dialog for sending pm to a user, could probably be launched from the pmConversation items, 
 * as opposed to just the region conversationItems as it is now.
 */

public class SendPMActivity extends PsyborgActivity{
	String sender;
	String receiver;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pm_activity);

		Intent intent = getIntent();

		sender = intent.getStringExtra("sender");
		receiver = intent.getStringExtra("receiver");

		setTitle("Send message to "+receiver);
	}
	public void pmSend(View view){
		View parent = (View) view.getParent();

		final String content = ((EditText)parent.findViewById(R.id.pm_content)).getText().toString();

		new Thread(new Runnable(){
			@Override
			public void run(){
				ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

				parameters.add(new BasicNameValuePair("receiver", receiver));

				parameters.add(new BasicNameValuePair("sender", sender));

				parameters.add(new BasicNameValuePair("message", content));
				
				SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
				String passwordHash = myPrefs.getString("password", null);
				NameValuePair passwordNumPair = new BasicNameValuePair("password", passwordHash);
				parameters.add(passwordNumPair);





				String url = ("http://telepathwebserver.appspot.com/postPm");
				
				String response = WebInterface.postToUrl(url, parameters, true);
				
				
				if(response.equals("success")){
					//somehow show that message has been added, and show conversation
					
					finish();//for now
				}
				else{
					SendPMActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run(){
							
							Toast.makeText(SendPMActivity.this, "There has been an error posting", Toast.LENGTH_SHORT).show();

						}
					});
					
				}
			}
		}).start();
		


		
	}

}

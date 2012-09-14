package com.datapath.telepath;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

//container for ConversationFragment. Used for setRetainInstance of Fragments


public class ConversationActivity extends PsyborgFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_conversation);


	}
	//on "+" pressed
	public void conversationAddParentButton(View view){
		Intent intent = new Intent(this, ConversationAddParentActivity.class);
		startActivity(intent);
	}
	//on settings Pressed
	public void conversationSettings(View view){
		Intent intent = new Intent(this, ConversationSettingsActivity.class);
		startActivity(intent);
	}
}

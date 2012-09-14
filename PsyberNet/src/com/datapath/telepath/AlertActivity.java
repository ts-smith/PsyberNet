package com.datapath.telepath;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class AlertActivity extends PsyborgActivity{
	
	/*
	 * Used for displaying messages, either for the user or debugging.
	 * 
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alert_dialog);
		
		TextView text = (TextView) findViewById(R.id.alert_text);
		
		Intent intent = getIntent();
		
		text.setText(intent.getStringExtra("message"));
		
		boolean red = intent.getBooleanExtra("red", false);
		
		if (red){
			text.setTextColor(Color.RED);
		}
	}

}

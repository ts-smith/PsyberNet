package com.datapath.telepath;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.datapath.telepath.Overview.*;
//contains overview fragments. used to setRetainInstance to true for less httprequests on orientation change.
public class OverviewActivity extends PsyborgFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.overview_activity);

		
	}
}

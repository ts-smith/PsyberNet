package com.datapath.telepath;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.datapath.telepath.R;
import com.datapath.telepath.R.id;
import com.datapath.telepath.R.layout;
import com.google.gson.Gson;

//shows over view for bulletins and conversations for each region
public class OverviewFragment extends Fragment {
	Gson gson = new Gson();

	Overview overview = null;
	String website = null;
	OverviewConversationItem[] overviewConversationItems;
	OverviewBulletinItem[] overviewBulletinItems;
	
	String urlBase = "http://telepathwebserver.appspot.com";
	String overviewPage = "/regionOverview";
	String regionQuery;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);


		Intent intent = getActivity().getIntent();
		String desiredRegion = intent.getStringExtra("desiredRegion");
		
		String queryFormat = "?region=";

		regionQuery = desiredRegion.replace(" ", "_");

		String url = urlBase+overviewPage+queryFormat+regionQuery;

		String jsonOverview = WebInterface.getUrl(url);

		overview = gson.fromJson(jsonOverview, Overview.class);
		
		
		overviewConversationItems = new OverviewConversationItem[overview.conversationsContent.size()];
		
		for (int i = 0; i < overviewConversationItems.length; i++) {
			OverviewConversationItem item = new OverviewConversationItem();
			
			ArrayList entry = ((ArrayList)overview.conversationsContent.get(i));
			
			item.parent=(String) entry.get(0);
			item.time=(String) entry.get(1);
			item.topic=(String) entry.get(2);
			item.textPreview=(String) entry.get(3);
			
			overviewConversationItems[i]=item;
		}
		
		overviewBulletinItems = new OverviewBulletinItem[overview.bulletinsContent.size()];
		
		for (int i = 0; i < overviewBulletinItems.length; i++) {
			OverviewBulletinItem item = new OverviewBulletinItem();
			
			ArrayList entry = ((ArrayList)overview.bulletinsContent.get(i));
			
			item.parent=(String) entry.get(0);
			item.time=(String) entry.get(1);
			item.topic=(String) entry.get(2);
			item.textPreview=(String) entry.get(3);
			
			overviewBulletinItems[i]=item;
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.overview_fragment, container, false);

		setRetainInstance(true); 
		
		if(overview!=null){
			
			OverviewConversationAdapter conversationAdapter = new OverviewConversationAdapter(getActivity(), 
	                R.layout.overview_conversation_row, overviewConversationItems);
			
			

			ListView conversationList =  (ListView)view.findViewById(R.id.overviewConversationList); 

			View conversationHeader = getLayoutInflater(savedInstanceState).inflate(R.layout.overview_conversation_header, null);

			conversationList.addHeaderView(conversationHeader);
			
			conversationHeader.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					goToConversations(regionQuery);
				}
				
			});
			
			
			conversationList.setAdapter(conversationAdapter);
			
			//conversationList.setOnItemClickListener(new OverviewClickListener("conversation"));
			
			
			OverviewBulletinAdapter bulletinAdapter = new OverviewBulletinAdapter(getActivity(), 
	                R.layout.overview_bulletin_row, overviewBulletinItems);
			
			

			ListView bulletinList =  (ListView)view.findViewById(R.id.overviewBulletinList); 

			View bulletinHeader = getLayoutInflater(savedInstanceState).inflate(R.layout.overview_bulletin_header, null);

			bulletinList.addHeaderView(bulletinHeader);
			
			bulletinHeader.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					goToBulletins(regionQuery);
				}
				
			});
			
			bulletinList.setAdapter(conversationAdapter);
			
			//bulletinList.setOnItemClickListener(new OverviewClickListener("bulletin"));
			
			return view;
		}
		else{
			Toast.makeText(getActivity(), "There has been a connection error", Toast.LENGTH_LONG).show();
			return view;
		}
	}
	private void goToConversations(String regionQuery){
		
		Intent intent = new Intent(getActivity(), ConversationActivity.class);
		
		intent.putExtra("desiredRegion", regionQuery);
		
    	startActivity(intent);
    	
	}
	private void goToBulletins(String regionQuery){
		Toast.makeText(getActivity(), regionQuery, 2000).show();
	}
	
	/* don't think I used this, wanted to have a way to get a preview of the item or something
	
	//This is probably broken!!!!!!!!!!!!!!!!!!!!!!!!
	class OverviewClickListener implements OnItemClickListener{
		
		String type;
		
		public OverviewClickListener(String type){
			if (type =="conversation"){
				this.type = "conversation";
			}else{
				this.type = "overview";
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//this whole method won't work
			int viewType;
			if (type=="conversation") {
				viewType = R.id.overviewConversationContent;
			}else{
				viewType = R.id.overviewBulletinContent;
			}
			String extendedContent = ((TextView)arg1.findViewById(viewType)).getText().toString();
			int length = extendedContent.split(" ").length*500;
			
			
			Toast.makeText(getActivity(), extendedContent, length).show();
		}
	}
	*/
}
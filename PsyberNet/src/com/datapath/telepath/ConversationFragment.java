package com.datapath.telepath;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.datapath.telepath.ConversationFilter.LockStatus;
import com.google.gson.Gson;

/*
 * Object for handling conversation logic for any given region. 
 * Implemented as fragment so that some network calls aren't done on configurationChange
 */

public class ConversationFragment extends Fragment{

	static Gson gson = new Gson();

	static Date lastUpdate;
	SimpleDateFormat displayFormat = new SimpleDateFormat("E kk:mm:ss");
	SimpleDateFormat compareFormat = new SimpleDateFormat("yyDDDHHmmss");

	static ArrayList<ArrayList> conversations = null;
	static ArrayList<ConversationItem> conversationItems = new ArrayList<ConversationItem>();
	static ConversationAdapter conversationAdapter;

	ListView listView;

	static SharedPreferences myPrefs;
	String userName;

	boolean pollingActive = true;

	String urlBase = "http://telepathwebserver.appspot.com";
	String writeConversationsPage = "/writeConversations";
	String regionQueryValue;

	static String jsonConversations;

	static String regionTopics;

	Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	static MediaPlayer mMediaPlayer = new MediaPlayer();
	static AudioManager audioManager;

	static UpdateSound sound = UpdateSound.None;

	//may want to make more extensive use of this;
	static boolean firstCreated = true;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		compareFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		myPrefs = getActivity().getSharedPreferences("myPrefs", getActivity().MODE_WORLD_READABLE);
		userName = myPrefs.getString("userName", "Anon");

		regionQueryValue = getActivity().getIntent().getStringExtra("desiredRegion");






		regionTopics = myPrefs.getString("regionTopics", "");



		if (!regionQueryValue.equals(ConversationFilter.appliedRegion)){
			ConversationFilter.appliedRegion=null;
			ConversationFilter.idFilter=-1;
		}


		getActivity().setTitle(regionQueryValue.replace("_", " "));



		try {
			mMediaPlayer.setDataSource(getActivity(), soundUri);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);


	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		setRetainInstance(true);

		View view = inflater.inflate(R.layout.conversation_layout, container, false);

		conversationAdapter = new ConversationAdapter(conversationItems, getActivity(), inflater, regionQueryValue);

		listView = (ListView) view.findViewById(R.id.conversationListView);
		listView.setAdapter(conversationAdapter);

		return view;
	}
	@Override
	public void onResume(){
		super.onResume();

		String queryFormat = "?region=";

		final String tempUrl = urlBase+writeConversationsPage+queryFormat+regionQueryValue;


		pollingActive=true;//probably unnecessary

		//poll server/refreshUi
		//there may be some inefficiencies or redundancies here
		new Thread(new Runnable(){
			@Override
			public void run() {
				//probably unnecessary
				while(pollingActive){
					int waitTime = 2300;

					String queryUrl = tempUrl+"&lastUpdate=";
					if(lastUpdate==null){
						queryUrl+="-1";
					}
					else{
						queryUrl+=compareFormat.format(lastUpdate);
					}




					jsonConversations = WebInterface.getUrl(queryUrl);


					//easy way to check if anything was returned
					if(jsonConversations.length()>1){

						if (sound == UpdateSound.None){
							sound = UpdateSound.MessageNotification;
						}


						myPrefs.edit().putString("jsonConversations", jsonConversations).commit();


						waitTime += 300;
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run(){
								updateListView(false);

							}
						});
					}

					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		}).start();


	}
	//again probably unnecessary
	@Override
	public void onPause(){
		super.onPause();
		pollingActive=false;
	}
	//refresh ui with new information after something has changed
	public static void updateListView(boolean manualTrigger){
		
		if (!manualTrigger){
			lastUpdate=new Date();
			conversations=gson.fromJson(jsonConversations, ArrayList.class);
		}

		if(!firstCreated){
			if(sound == UpdateSound.MessageNotification){
				sound = UpdateSound.None;
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
					mMediaPlayer.setLooping(false);//necessary?
					try {
						mMediaPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mMediaPlayer.start();
				}
			}
			else if (sound == UpdateSound.FilterSet){
				sound = UpdateSound.None;
				//play filter sound
			}
			else if (sound == UpdateSound.LockSet){
				sound = UpdateSound.None;
				//play lock sound
			}

		}
		else{
			firstCreated = false;
		}



		String tempTopics = "";

		ArrayList<ConversationItem> updatedList = new ArrayList<ConversationItem>();


		for (ArrayList<Object> conversationRow:conversations) {

			//for creating filter
			String topic = (String)conversationRow.get(2);
			//idNum will be needed for lock
			//not going to stop once locked item is found to keep topic list updated
			String idNum = (String)conversationRow.get(4);
			int idNumInt = Integer.parseInt(idNum);


			if(tempTopics==""){
				tempTopics+=topic;
			}else{
				tempTopics+=","+topic;
			}




			LockStatus lockStatus = ConversationFilter.getLockStatus(idNumInt);

			if (lockStatus == LockStatus.LOCK_ACCEPTED || lockStatus == LockStatus.NOT_SET){
				if (ConversationFilter.acceptsTopic(topic)){

					ConversationItem parent;
					String poster = (String)conversationRow.get(0);
					String date = (String)conversationRow.get(1);
					String content = (String)conversationRow.get(3);

					parent = new ConversationItem(idNum, poster, date, topic, content);
					updatedList.add(parent);

					ArrayList<ArrayList<String>> replies = (ArrayList<ArrayList<String>>) conversationRow.get(5);

					for (ArrayList<String> reply:replies){

						ConversationItem replyItem;

						String replyPoster = (String) reply.get(0);
						String replyDate = (String)reply.get(2);
						String replyContent = (String)reply.get(1);

						replyItem = new ConversationItem(idNum, replyPoster, replyDate, null, replyContent);
						updatedList.add(replyItem);

					}

					ConversationItem responseButton = new ConversationItem(idNum, null, null, null, null); 
					updatedList.add(responseButton);
				}
			}

		}

		conversationItems.clear();
		conversationAdapter.notifyDataSetChanged();
		//doing the clear above may not be necessary
		conversationItems.addAll(updatedList);
		conversationAdapter.notifyDataSetChanged();


		if(tempTopics != regionTopics && ConversationFilter.lockSet==false/*make sure notSet to keep topics available*/){

			//with regionTopics now static, use of a preference may be unnecessary
			myPrefs.edit().putString("regionTopics", tempTopics).commit();
			//probably redundant
			regionTopics = tempTopics;
		}
	}

	public static enum UpdateSound{
		None,
		MessageNotification,
		LockSet,
		FilterSet;
	}
}
//left over from days as an Activity, retained for local reference
/*
//on "+" pressed
	public void conversationAddParentButton(View view){
		Intent intent = new Intent(getActivity(), ConversationAddParentActivity.class);
		startActivity(intent);
	}
	//on settings Pressed
	public void conversationSettings(View view){
		Intent intent = new Intent(getActivity(), ConversationSettingsActivity.class);
		startActivity(intent);
	}
 */
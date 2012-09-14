package com.datapath.telepath;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
//class to add menu to all classes
public abstract class PsyborgActivity extends Activity{
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.psyborg_menu, menu);//menu xml
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.view_inbox:
	            goToInbox();
	            return true;
	        case R.id.view_sound_settings:
	        	goToSoundSettings();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void goToInbox(){
		SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		String name = myPrefs.getString("userName", MainActivity.makeAnonTag());
		
		if (!name.startsWith("Anon-")){
			Intent intent = new Intent(this, ViewInboxActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(this, "Must be signed in to access inbox", Toast.LENGTH_LONG).show();
		}
		
	}
	public void goToSoundSettings(){
		
	}

}

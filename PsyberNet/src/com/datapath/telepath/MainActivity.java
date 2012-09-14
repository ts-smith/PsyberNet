package com.datapath.telepath;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Opening ui for app
 * 
 * Handles logic for user name, gets gps coordinates, dispatches sign in and select region activities.
 */



//do the fragment thing here with retain instance state instead of bundles probably,
//because somehow onPause seems to be locking things up
public class MainActivity extends PsyborgActivity {

	static String userName;
	static SharedPreferences myPrefs;
	static TextView textView;

	LocationManager locationManager;
	LocationListener locationListener;
	Location location;

	double xCoord;
	double yCoord;

	private ProgressBar mProgress;

	String urlBase = "http://telepathwebserver.appspot.com";
	String candidatesPage = "/findcandidateregions";
	String coordQuery;

	boolean showProgressBar = false;

	boolean signedIn = false;

	SimpleDateFormat compareFormat = new SimpleDateFormat("yyDDDHHmmss");


	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean("showProgressBar", showProgressBar);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		showProgressBar = savedInstanceState.getBoolean("showProgressBar");

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);		

		signedIn = myPrefs.getBoolean("signedIn", false);

		toggleLoggedOnUi(signedIn);



		//gui name changed to this onResume
		userName = myPrefs.getString("userName", makeAnonTag());

		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();

		mProgress = (ProgressBar) findViewById(R.id.progressBar);




	}

	@Override
	public void onResume(){
		super.onResume();
		if (showProgressBar){
			mProgress.setVisibility(View.VISIBLE);
		}else{
			mProgress.setVisibility(View.GONE);
		}

		textView = (TextView) findViewById(R.id.userName);
		textView.setText(userName);

	}//redundant, should figure out which one is necessary to make progress bar work with minimal code.
	@Override
	protected void onPause(){
		super.onPause();

		showProgressBar = false;
		mProgress.setVisibility(View.GONE);

	}
	//called on find regions press
	public void findRegions(View view){
		locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		mProgress.setVisibility(View.VISIBLE);
		showProgressBar = true;
		Toast.makeText( getApplicationContext(),
				"Finding location, this may take a moment",
				Toast.LENGTH_LONG).show();
		//passes chain off to onLocationChanged
	}
	//called on find regions press (top one)
	public void findRegionsInside(View view){
		/*   //For fast debugging
		Button button = (Button) view;		
		button.setText("dBug fx l8r");
		new Thread(new Runnable(){
			@Override
			public void run(){
				String urlToUseTemporarily = urlBase+"/findcandidateregions?xCoord=-121.94546626880765&yCoord=37.321728598326445";
				String jsonRegionList=WebInterface.getUrl(urlToUseTemporarily);
				selectRegion(jsonRegionList);
			}
		}).start();*/

		locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		mProgress.setVisibility(View.VISIBLE);
		showProgressBar = true;
		Toast.makeText( getApplicationContext(),
				"Finding location, this may take a moment",
				Toast.LENGTH_LONG).show();
		//passes chain off to onLocationChanged
	}
	static void refreshName(){
		userName = myPrefs.getString("userName", makeAnonTag());
		textView.setText(userName);
	}

	//called on location found
	public void selectRegion(String jsonRegionList){


		Intent intent = new Intent(this, SelectRegionActivity.class);

		String MESSAGE_KEY = "regionKey";
		String regionList = jsonRegionList; 

		intent.putExtra(MESSAGE_KEY, regionList);
		startActivity(intent);
	}
	//gets regionList and passes to selection activity
	public class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location loc){
			xCoord = loc.getLongitude();
			yCoord = loc.getLatitude();

			locationManager.removeUpdates(locationListener);

			coordQuery = "?xCoord="+xCoord+"&"+"yCoord="+yCoord;

			new Thread(new Runnable(){
				@Override
				public void run(){
					String regionUrl = urlBase+candidatesPage+coordQuery;

					String jsonRegionList=WebInterface.getUrl(regionUrl);

					selectRegion(jsonRegionList);
				}
			}).start();



		}

		@Override
		public void onProviderDisabled(String provider){
			Toast.makeText( getApplicationContext(),
					"Gps Disabled",
					Toast.LENGTH_SHORT ).show();
		}

		@Override
		public void onProviderEnabled(String provider){
			Toast.makeText( getApplicationContext(),
					"Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras){
		}
	}
	//called on Sign Up pressed
	public void launchSignUpActivity(View view){
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	//called on Sign In pressed
	public void signIn(View view) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		EditText accountName = (EditText) findViewById(R.id.account_sign_in_field);
		String enteredName = accountName.getText().toString();

		if (enteredName.length()>3&&enteredName.length()<20){

			if (!enteredName.startsWith("Anon-")){
				Matcher nameMatch = SecurityUtils.p.matcher(enteredName);

				if (nameMatch.matches()){

					EditText passwordView = (EditText) findViewById(R.id.account_sign_in_password);

					String password = passwordView.getText().toString();

					if (password.length()<20&&password.length()>3){
						String validateUrl = urlBase+"/signIn";


						ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(6);

						NameValuePair namePair = new BasicNameValuePair("accountName", enteredName);
						parameters.add(namePair);

						String saltedPassword = password+"Arachnid";

						MessageDigest md = null;
						try {
							md = MessageDigest.getInstance("SHA-256");
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						try {
							md.update(saltedPassword.getBytes("UTF-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						byte[] digest = md.digest();

						String hexDigest = SecurityUtils.bin2hex(digest);

						NameValuePair passwordPair = new BasicNameValuePair("password", hexDigest);
						parameters.add(passwordPair);


						String response = WebInterface.postToUrl(validateUrl, parameters, true);

						if (response.equals("success")){

							myPrefs.edit().putBoolean("signedIn", true).commit();
							myPrefs.edit().putString("userName", enteredName).commit();
							myPrefs.edit().putString("password", hexDigest).commit();

							signedIn = true;
							
							textView.setText(enteredName);

							toggleLoggedOnUi(true);

							

						}
						else{
							Intent alert = new Intent(this, AlertActivity.class);
							alert.putExtra("message", "Sign-In failed");
							startActivity(alert);
						}
					}
					else{
						passwordView.setText(null);
						passwordView.setHint("Password must be between 3 and 20 characters");
					}

				}
				else{
					Toast.makeText(this, "Name must contain only Alphanumeric characters or \"-\" and \"_\"", Toast.LENGTH_LONG).show();
				}
			}
			else{
				accountName.setText(null);
				accountName.setHint("Cannot spoof Anon-Syntax");
			}

		}
		else{

			accountName.setText(null);
			accountName.setHint("Name must be within 4 and 19 characters");
		}





	}
	//called on Sign Out pressed
	public void signOut(View view){
		myPrefs.edit().putBoolean("signedIn", false).commit();
		signedIn = false;
		myPrefs.edit().putString("userName", makeAnonTag()).commit();
		refreshName();
		myPrefs.edit().putString("password", null).commit();
		toggleLoggedOnUi(false);
	}
	public static String makeAnonTag(){
		Random random = new Random();
		String appendCode="";
		String pool = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i<5;i++){
			int r = random.nextInt(pool.length());
			appendCode+=pool.charAt(r);
		}
		return "Anon-"+appendCode;
	}
	//switch between ui when logged in or out
	private void toggleLoggedOnUi(boolean loggedOn){
		View[] signInFields = {
				findViewById(R.id.account_sign_up_button),
				findViewById(R.id.account_sign_in_field),
				findViewById(R.id.account_sign_in_password),
				findViewById(R.id.account_sign_in_button)
		};
		View signOutButton = findViewById(R.id.account_sign_out_button);


		if (loggedOn){

			for (View view : signInFields){
				view.setVisibility(View.GONE);
			}
			signOutButton.setVisibility(View.VISIBLE);
		}
		else{
			for (View view: signInFields){
				view.setVisibility(View.VISIBLE);
			}
			signOutButton.setVisibility(View.GONE);
		}
	}
}

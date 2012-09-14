package com.datapath.telepath;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends PsyborgActivity{
	
	//may want to figure out good way to thread website-read so ui doesn't lock, ?(potentially helps mitigate connection based problems, probably not)
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sign_up_activity);
	}

	//Sanitizing here isn't safe, an attack could still be done directly on the server without the app.
	//done for convenience
	public void signUpSubmit(View view){
		TextView nameView = (TextView) findViewById(R.id.sign_up_name_entry);
		TextView passwordView = (TextView) findViewById(R.id.sign_up_password);
		TextView passwordConfrimView = (TextView) findViewById(R.id.sign_up_password_confirm);
		
		String name = nameView.getText().toString();
		
		Intent alert = new Intent(this, AlertActivity.class);
		if (name.length()>3&&name.length()<20){
			if (!name.startsWith("Anon-")){
				Matcher nameMatch = SecurityUtils.p.matcher(name);
				
				if (nameMatch.matches()){
					String password = passwordView.getText().toString();
					String confirm = passwordConfrimView.getText().toString();
					
					if (password.equals(confirm)){
						if (password.length()>3&&password.length()<20){
							String urlBase = "http://telepathwebserver.appspot.com";
							String signUp = urlBase+="/signUp";
							
							
							ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
							
							NameValuePair namePair = new BasicNameValuePair("accountName", name);
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
													
							
							String response = WebInterface.postToUrl(signUp, parameters, true);
							
							if (response.equals("success")){
								
								SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
								
								myPrefs.edit().putBoolean("signedIn", true).commit();
								myPrefs.edit().putString("userName", name).commit();
								myPrefs.edit().putString("password", hexDigest).commit();
								
								Intent restartUi = new Intent(this, MainActivity.class);
								restartUi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(restartUi);
							}
							else if(response.startsWith("NT")){
								alert.putExtra("message", "Name Taken");
								alert.putExtra("red", true);
								
								startActivity(alert);
							}
							
							
							
							
							
						}
						else{
							alert.putExtra("message", "Password must be between 3 and 20 characters");
							startActivity(alert);
						}
					}else{
						alert.putExtra("message", "Passwords don't match");
						startActivity(alert);
					}
				}
				else{
					alert.putExtra("message", "Name must be composed of alphanumeric characters, \"-\"s, or \"_\".");
					startActivity(alert);
				}
			}
			else{
				alert.putExtra("message", "Don't spoof the Anon-syntax, nice try");
				startActivity(alert);
			}
		}
		else{
			alert.putExtra("message", "Name must be between 3 and 20 characters long");
			startActivity(alert);
		}
	}
}
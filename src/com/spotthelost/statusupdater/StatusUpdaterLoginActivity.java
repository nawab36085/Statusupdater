package com.spotthelost.statusupdater;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusUpdaterLoginActivity extends Activity {
	String TAG = "StatusUpdaterLoginActivity";
	TextView titleTextView = null;
	TextView phoneNumberTextView = null;
	EditText phoneNumberEditText = null;
	TextView passwordTextView = null;
	EditText passwordEditText = null;
	Button loginButton = null;
	ImageButton newUserImageButton = null;
	ImageButton verificationImageButton = null;
	TextView loggingTextView = null;
	ProgressBar loginProgress = null;
	OnClickListener loginButtonListener = null;
	private Handler mHandler = null;
	String phoneNumber = null;
	String password = null;
	public static final String PREFS_NAME = "statusUpdaterPref";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statusupdaterlogin);
		final SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //String appstatus = settings.getString("appstatus", "locked");
        int loginStatus = settings.getInt("loginStatus", 0);
        String savedPhoneNumber = settings.getString("phoneNumber", null);
        String savedPassword = settings.getString("password", null);
        if(loginStatus == 1 && savedPhoneNumber != null && savedPassword != null)
        {
        	Log.v(TAG, "starting updater activity");
			//start Application Unlock Activity now
			Intent StatusUpdaterIntent = new Intent(getApplicationContext(), StatusUpdaterActivity.class);
			//LockImageDoneIntent.putExtra(null, lock);
			startActivity(StatusUpdaterIntent);
			finish();
			return;
        }
		//To Do. Create registration form with phone number, password and email.
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);
		passwordTextView = (TextView) findViewById(R.id.passwordTextView);
		phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        newUserImageButton = (ImageButton) findViewById(R.id.newUserImageButton);
        verificationImageButton = (ImageButton) findViewById(R.id.verificationImageButton);        
        loggingTextView = (TextView) findViewById(R.id.loggingTextView);
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);
        
        titleTextView.setText(R.string.login);
        loginButton.setText(R.string.login);
        loggingTextView.setVisibility(View.GONE);
        loginProgress.setVisibility(View.GONE);
        
        loginButtonListener = new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0.getId() == loginButton.getId())
				{
					Editable phoneNumberText = null;
					Editable passwordText = null;
					phoneNumberText = phoneNumberEditText.getText();
					passwordText = passwordEditText.getText();
					if(phoneNumberText == null || passwordText == null)
					{
						if(phoneNumberText == null)
						{
							
						}
						else if(passwordText == null)
						{
							
						}							
					}
					else
					{
						if(phoneNumberText.length() >= 0)
						{
							phoneNumber = phoneNumberText.toString();
							if(passwordText.length() >= 0)
							{
								password = passwordText.toString();

								phoneNumberTextView.setVisibility(View.GONE);
								phoneNumberEditText.setVisibility(View.GONE);
								passwordTextView.setVisibility(View.GONE);
								passwordEditText.setVisibility(View.GONE);
								loginButton.setVisibility(View.GONE);
								newUserImageButton.setVisibility(View.GONE);
								verificationImageButton.setVisibility(View.GONE);
								loggingTextView.setText(R.string.loggingdot);
								loggingTextView.setVisibility(View.VISIBLE);
						        loginProgress.setVisibility(View.VISIBLE);
								
								new Thread(new Runnable(){

									public void run() {
										// TODO Auto-generated method stub
										String httpResult = null;
										try {
							                  HttpClient client = new DefaultHttpClient();  
							                  String postURL = "http://spotthelost.com/uploads/login.php";
							                  HttpPost post = new HttpPost(postURL); 
						                      List<NameValuePair> params = new ArrayList<NameValuePair>();
						                      params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
						                      params.add(new BasicNameValuePair("password", password));
						                      //params.add(new BasicNameValuePair("email", "nawab36085@gmail.com"));
						                      //params.add(new BasicNameValuePair("removePhoneNumbers", null));
						                      UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
						                      post.setEntity(ent);
						                      HttpResponse responsePOST = client.execute(post);  
						                      HttpEntity resEntity = responsePOST.getEntity();  
						                      if (resEntity != null) 
						                      {  
						                    	  httpResult = EntityUtils.toString(resEntity);
						                    	  Log.i(TAG,httpResult);
						                          //Log.i("RESPONSE",EntityUtils.toString(resEntity));
						                      }
						                      else
						                      {
						                      	Log.i("RESPONSE","response is null");
						                      }
							              } catch (Exception e) {
							                  e.printStackTrace();
							              }
							              Message msg = Message.obtain();
							              msg.what = 1;
							              msg.obj = httpResult;
							              msg.setTarget(mHandler);
							              msg.sendToTarget();
									}
									
								}).start();
								mHandler = new Handler(){
						        	public void handleMessage(Message msg) 
						            { 
						        		if(msg.what == 1)
						        		{
						        			//updatingTextView.setVisibility(View.GONE);
						        			
						        	        if(msg.obj != null)
						        	        {
						        	        	String updateResult = (String)msg.obj;
						        	        	String status = updateResult.substring(1, 8);
						        	        	if(status.equals("success"))
						        	        	{
						        	        		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
						        	               
						        	    			SharedPreferences.Editor editor = settings.edit();
						        
						        	    	        editor.putInt("loginStatus", 1);
						        	    	        editor.putString("phoneNumber", phoneNumber);
						        	    	        editor.putString("password", password);
						        	    	        editor.commit();
						        	    	        
						        	        		loggingTextView.setText(R.string.loginsuccess);
						        	            	Log.v(TAG, "starting updater activity");
						        	    			//start Application Unlock Activity now
						        	    			Intent StatusUpdaterIntent = new Intent(getApplicationContext(), StatusUpdaterActivity.class);
						        	    			//LockImageDoneIntent.putExtra(null, lock);
						        	    			startActivity(StatusUpdaterIntent);
						        	    			finish();
						        	    			return;
						        	        	}
						        	        	else if(status.equals("failure"))
						        	        	{
						        	        		loggingTextView.setText(R.string.loginfailed);
						        	        	}
						        	        	else
						        	        	{
						        	        		loggingTextView.setText(R.string.loginfailed);
						        	        	}
						        	        	loginProgress.setVisibility(View.GONE);
						        	        	//mBitmap.recycle();//Nawab 29.08.2011

						        	        }
						        	        else
						        	        {
						        	        	//Toast error message.
						        	        }
						        		}
						            }
								};
							}
							else
							{
								
							}
						}
						else
						{
							
						}
					}
				}
				else if(arg0.getId() == newUserImageButton.getId())
				{
					Log.v(TAG, "starting registration activity");
					//start Application Unlock Activity now
					Intent StatusUpdaterRegistrationIntent = new Intent(getApplicationContext(), StatusUpdaterRegistrationActivity.class);
					//LockImageDoneIntent.putExtra(null, lock);
					startActivity(StatusUpdaterRegistrationIntent);
					//finish();
					return;
				}
				else if(arg0.getId() == verificationImageButton.getId())
				{
					Log.v(TAG, "starting verification activity");
					//start Application Unlock Activity now
					Intent StatusUpdaterVerificationIntent = new Intent(getApplicationContext(), StatusUpdaterVerificationActivity.class);
					//LockImageDoneIntent.putExtra(null, lock);
					startActivity(StatusUpdaterVerificationIntent);
					//finish();
					return;
				}
			}
        	
        };
        loginButton.setOnClickListener(loginButtonListener);
        newUserImageButton.setOnClickListener(loginButtonListener);
        verificationImageButton.setOnClickListener(loginButtonListener);
	}

}

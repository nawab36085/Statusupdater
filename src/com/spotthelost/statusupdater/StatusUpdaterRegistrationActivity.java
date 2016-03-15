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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusUpdaterRegistrationActivity extends Activity {
	String TAG = "StatusUpdaterRegistrationActivity";
	TextView phoneNumberTextView = null;
	EditText phoneNumberEditText = null;
	TextView passwordTextView = null;
	EditText passwordEditText = null;
	TextView rePasswordTextView = null;
	EditText rePasswordEditText = null;
	Button registerButton = null;
	TextView registeringTextView = null;
	ProgressBar registerProgress = null;
	OnClickListener registerButtonListener = null;
	private Handler mHandler = null;
	String phoneNumber = null;
	String password = null;
	String rePassword = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statusupdaterregistration);
		//To Do. Create registration form with phone number, password and email.
		phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);
		passwordTextView = (TextView) findViewById(R.id.passwordTextView);
		rePasswordTextView = (TextView) findViewById(R.id.rePasswordTextView);
		phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        registeringTextView = (TextView) findViewById(R.id.registeringTextView);
        registerProgress = (ProgressBar) findViewById(R.id.registerProgress);
        registeringTextView.setVisibility(View.GONE);
        registerProgress.setVisibility(View.GONE);
        
        registerButtonListener = new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0.getId() == registerButton.getId())
				{
					Editable phoneNumberText = null;
					Editable passwordText = null;
					Editable rePasswordText = null;
					phoneNumberText = phoneNumberEditText.getText();
					passwordText = passwordEditText.getText();
					rePasswordText = rePasswordEditText.getText();
					if(phoneNumberText == null || passwordText == null || rePasswordText == null)
					{
						if(phoneNumberText == null)
						{
							
						}
						else if(passwordText == null)
						{
							
						}
						else if(rePasswordText == null)
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
								if(rePasswordText.length() >= 0)
								{
									rePassword = rePasswordText.toString();
									if(rePassword.equals(password))
									{
										phoneNumberTextView.setVisibility(View.GONE);
										phoneNumberEditText.setVisibility(View.GONE);
										passwordTextView.setVisibility(View.GONE);
										passwordEditText.setVisibility(View.GONE);
										rePasswordTextView.setVisibility(View.GONE);
										rePasswordEditText.setVisibility(View.GONE);
										registerButton.setVisibility(View.GONE);

								        registeringTextView.setVisibility(View.VISIBLE);
								        registerProgress.setVisibility(View.VISIBLE);
										
										new Thread(new Runnable(){

											public void run() {
												// TODO Auto-generated method stub
												String httpResult = null;
												try {
									                  HttpClient client = new DefaultHttpClient();  
									                  String postURL = "http://spotthelost.com/uploads/register.php";
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
								        			registerProgress.setVisibility(View.GONE);
								        	        if(msg.obj != null)
								        	        {
								        	        	String updateResult = (String)msg.obj;
								        	        	String status = updateResult.substring(1, 8);
								        	        	if(status.equals("success"))
								        	        	{
								        	        		registeringTextView.setText(R.string.registersuccess);
								        	        	}
								        	        	else if(status.equals("failure"))
								        	        	{
								        	        		registeringTextView.setText(R.string.registerfailed);
								        	        	}
								        	        	else
								        	        	{
								        	        		registeringTextView.setText(R.string.registerfailed);
								        	        	}
								        	        	//mBitmap.recycle();//Nawab 29.08.2011

								        	        }
								        	        else
								        	        {
								        	        	//Toast error message.
								        	        	registeringTextView.setText(R.string.registerfailed);
								        	        }
								        		}
								            }
										};

									}
									else
									{
										//Toast password do nto match.
									}
								}
								else
								{
									
								}
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
			}
        	
        };
        registerButton.setOnClickListener(registerButtonListener);
        
	}

}

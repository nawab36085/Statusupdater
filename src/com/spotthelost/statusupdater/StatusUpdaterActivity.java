package com.spotthelost.statusupdater;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.spotthelost.statusupdater.MyLocation.LocationResult;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StatusUpdaterActivity extends Activity {
	String TAG = "StatusUpdaterActivity";
	int IMAGE_PICK = 5;
	int CAMERA_SHOT = 6;
	Uri imageUri = null;
	String filepath = null;
	String myAddress = null;
	String publishLocation = null;
	TextView statusMessageTextView = null;
	EditText statusMessageEditText = null;
	ImageView imageImageView = null;
	Button fromGalleryButton = null;
	Button fromCameraButton = null;
	Button updateButton = null;
	TextView updatingTextView = null;
	ProgressBar updateProgress = null;
	OnClickListener AllButtonListener = null;
	LocationResult locationResult = null;
	private Handler mHandler = null;
	public static final String PREFS_NAME = "statusUpdaterPref";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AllButtonListener = new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
					
	        	if(v.getId() == fromGalleryButton.getId())
	        	{
					Log.v(TAG, "starting gallery");
					//start  gallery now
					chooseFromGallery();
	        	}
	        	else if(v.getId() == fromCameraButton.getId())
	        	{
					Log.v(TAG, "starting camera");
					//start camera now
					captureFromCamera();
	        	}
				else if(v.getId() == updateButton.getId())
				{
					statusMessageTextView.setVisibility(View.GONE);
					statusMessageEditText.setVisibility(View.GONE);
					imageImageView.setVisibility(View.GONE);
					fromGalleryButton.setVisibility(View.GONE);
					fromCameraButton.setVisibility(View.GONE);
					updateButton.setVisibility(View.GONE);
					updatingTextView.setVisibility(View.VISIBLE);
					updateProgress.setVisibility(View.VISIBLE);
					locationResult = new LocationResult(){

						@Override
						public void gotLocation(Location location) {
							// TODO Auto-generated method stub
							if(location != null)
							{
								Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
						       try {
						    	   List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						    	  
						    	   if(addresses != null) {
						    	    Address returnedAddress = addresses.get(0);
						    	    StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
						    	    for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
						    	     strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
						    	    }
						    	    myAddress = strReturnedAddress.toString();
						    	    Log.i(TAG,myAddress);
						    	   }
						    	   else{
						    		   Log.i(TAG,"Cannot get address");
						    	   }
						    	  } catch (IOException e) {
						    	   // TODO Auto-generated catch block
						    	   e.printStackTrace();
						    	   Log.i(TAG,"Cannnot get address");
						    	  }
							}
							else
							{
								Log.i(TAG,"location is null");
							}
						}
						
					};
					MyLocation myLocation = new MyLocation();					
					myLocation.getLocation(getApplicationContext(), locationResult);
					new Thread(new Runnable(){

						public void run() {
							// TODO Auto-generated method stub
							String httpResult = null;
							Log.v(TAG, "updating the status");
							Editable text = null;
							String statusMessage = null;
							text = statusMessageEditText.getText();
							if(text != null)
							{
								if(text.length() >= 0)
								{
									statusMessage = text.toString();
								}
							}


							//start uploading to server now
							//if(filepath != null)
								
							try 
							{
						         HttpClient client = new DefaultHttpClient();  
						         String postURL = "http://spotthelost.com/uploads/uploader.php";
						         HttpPost post = new HttpPost(postURL); 
							     
							     MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
							     if(filepath != null)
							     {
							    	 File file = new File(filepath);
							    	 FileBody bin = new FileBody(file);
							    	 reqEntity.addPart("file", bin);
							     }
							     reqEntity.addPart("phoneNumber", new StringBody("+919886055152", "text/plain", Charset.forName("UTF-8")));
							     reqEntity.addPart("password", new StringBody("simynawab", "text/plain", Charset.forName("UTF-8")));
							     if(statusMessage != null)
							     {
							    	 reqEntity.addPart("statusmessage", new StringBody(statusMessage, "text/plain", Charset.forName("UTF-8"))); 
							     }
							     if(myAddress != null)
							     {
							    	 reqEntity.addPart("location", new StringBody(myAddress, "text/plain", Charset.forName("UTF-8")));
							     }
							     if(publishLocation != null)
							     {
							    	 reqEntity.addPart("publishlocation", new StringBody(publishLocation, "text/plain", Charset.forName("UTF-8")));
							     }
							     post.setEntity(reqEntity);  
							     HttpResponse response = client.execute(post);  
							     HttpEntity resEntity = response.getEntity();  
							     if (resEntity != null) 
							     {
							    	 httpResult = EntityUtils.toString(resEntity);
							    	 Log.i(TAG,httpResult);
							     }
							} 
							catch (Exception e) 
							{
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
			        			updateProgress.setVisibility(View.GONE);
			        	        if(msg.obj != null)
			        	        {
			        	        	String updateResult = (String)msg.obj;
			        	        	String status = updateResult.substring(1, 8);
			        	        	updatingTextView.setText(updateResult);
			        	        	/*
			        	        	if(status.equals("success"))
			        	        	{
			        	        		updatingTextView.setText(R.string.updatesuccess);
			        	        	}
			        	        	else if(status.equals("failure"))
			        	        	{
			        	        		updatingTextView.setText(R.string.updatefailed);
			        	        	}
			        	        	else
			        	        	{
			        	        		updatingTextView.setText(R.string.updatefailed);
			        	        	}*/
			        	        	//mBitmap.recycle();//Nawab 29.08.2011

			        	        }
			        	        else
			        	        {
			        	        	//Toast error message.
			        	        	updatingTextView.setText(R.string.updatefailed);
			        	        }
			        		}
			            }
					};
				}
			}
        	
        };      

        statusMessageTextView = (TextView) findViewById(R.id.statusMessageTextView);
        statusMessageEditText = (EditText) findViewById(R.id.statusMessageEditText);
        imageImageView = (ImageView) findViewById(R.id.imageImageView);
        fromGalleryButton = (Button) findViewById(R.id.fromGalleryButton);
        fromCameraButton = (Button) findViewById(R.id.fromCameraButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        updatingTextView = (TextView) findViewById(R.id.updatingTextView);
        updateProgress = (ProgressBar) findViewById(R.id.updateProgress); 
        fromGalleryButton.setOnClickListener(AllButtonListener);
        fromCameraButton.setOnClickListener(AllButtonListener);
        updateButton.setOnClickListener(AllButtonListener);
        updatingTextView.setVisibility(View.GONE);
        updateProgress.setVisibility(View.GONE);
        imageImageView.setVisibility(View.GONE);
    }
    
 /*   @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.onCreate(null);
	}
*/
	public void chooseFromGallery()
    {
    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        //Uri uri =Uri.parse("content://contacts/");
        //intent.setData(uri);
        startActivityForResult(intent, IMAGE_PICK);
    }
    public void captureFromCamera()
    {
    	//define the file-name to save photo taken by Camera activity
	    String fileName = "new-photo-name.jpg";
	    //create parameters for Intent with filename
	    ContentValues values = new ContentValues();
	    values.put(MediaStore.Images.Media.TITLE, fileName);
	    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
	    //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
	    imageUri = getContentResolver().insert(
	    		MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	    //create new Intent
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	    startActivityForResult(intent, CAMERA_SHOT);
    }
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri,
		null, // Which columns to return
		null, // WHERE clause; which rows to return (all rows)
		null, // WHERE clause selection arguments (none)
		null); // Order-by clause (ascending by name)
		if(cursor != null && cursor.moveToFirst())
		{
			int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);			
			if(dataColumn >= 0)
			{
				String filepath = cursor.getString(dataColumn);
				return filepath;
			}
			else
				return null;
		}
		else
			return null;
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if (requestCode == CAMERA_SHOT || requestCode == IMAGE_PICK) 
    	{
    	    if (resultCode == RESULT_OK) 
    	    {
    	    	String file = null;
    	    	Uri uri = null;
    	    	if (requestCode == CAMERA_SHOT)
    	    	{
    	    		uri = imageUri;
    	    	}
    	    	else
    	    	{
    	    		file = data.getDataString();
                	uri = Uri.parse(file);
    	    	}
    	        //use imageUri here to access the image
            	filepath = getRealPathFromURI(uri);
	    		Log.e(TAG, "filepath="+filepath);
            	if(filepath == null)
            	{
                	// todo define resid for invalid image.
                	Toast toast = Toast.makeText(getApplicationContext(), "Invalid image", Toast.LENGTH_SHORT);
                	toast.show();
                	return;
            	}
            	else
            	{
            		Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            		imageImageView.setVisibility(View.VISIBLE);
            		imageImageView.setImageBitmap(scaledBitmap);
            	}
    	    }else 
    	    {
    	        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
    	        return;
    	    }
    	}

    }

}
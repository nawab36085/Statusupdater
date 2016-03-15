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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StatusUpdaterFriendsListActivity extends Activity {
	final int PICK_CONTACT = 5;
	String phoneNumber = null;
	String name = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String phonenumber = getMyPhoneNumber();
        if(phonenumber != null)
        {
        	Log.i("RESPONSE","my phone number is null");
        }
        else
        {
        	Log.i("RESPONSE","my phone number is " + phonenumber);
        }
        chooseContact();
	}
	private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
            getSystemService(Context.TELEPHONY_SERVICE); 
        return mTelephonyMgr.getLine1Number();
    }
    private void chooseContact()
    {
    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	startActivityForResult(intent, PICK_CONTACT);
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
      super.onActivityResult(reqCode, resultCode, data);

      switch (reqCode) {
        case (PICK_CONTACT) :
          if (resultCode == Activity.RESULT_OK) {
        	  getContactInfo(data);
              Log.i("RESPONSE",phoneNumber);

              try {
                  HttpClient client = new DefaultHttpClient();  
                  String postURL = "http://spotthelost.com/android/save-status.php";
                  HttpPost post = new HttpPost(postURL); 
                      List<NameValuePair> params = new ArrayList<NameValuePair>();
                      params.add(new BasicNameValuePair("myPhoneNumber", "+919886055152"));
                      params.add(new BasicNameValuePair("password", "simynawab"));
                      //params.add(new BasicNameValuePair("email", "nawab36085@gmail.com"));
                      params.add(new BasicNameValuePair("addPhoneNumbers", phoneNumber));
                      //params.add(new BasicNameValuePair("removePhoneNumbers", null));
                      UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
                      post.setEntity(ent);
                      HttpResponse responsePOST = client.execute(post);  
                      HttpEntity resEntity = responsePOST.getEntity();  
                      if (resEntity != null) {    
                          Log.i("RESPONSE",EntityUtils.toString(resEntity));
                      }
                      else
                      {
                      	Log.i("RESPONSE","response is null");
                      }
              } catch (Exception e) {
                  e.printStackTrace();
              }
            }
          break;
      }
	}
    protected void getContactInfo(Intent intent)
    {

       Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);      
       while (cursor.moveToNext()) 
       {           
           String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
           name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 

           String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

           if ( hasPhone.equalsIgnoreCase("1"))
               hasPhone = "true";
           else
               hasPhone = "false" ;

           if (Boolean.parseBoolean(hasPhone)) 
           {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
            while (phones.moveToNext()) 
            {
              phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();
           }

           /*// Find Email Addresses
           Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
           while (emails.moveToNext()) 
           {
            emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
           }
           emails.close();

        Cursor address = getContentResolver().query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                    null, null);
        while (address.moveToNext()) 
        { 
          // These are all private class variables, don't forget to create them.
          poBox      = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
          street     = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
          city       = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
          state      = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
          postalCode = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
          country    = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
          type       = address.getString(address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
        }  //address.moveToNext()   */
      }  //while (cursor.moveToNext())        
       cursor.close();
    }//getContactInfo
}

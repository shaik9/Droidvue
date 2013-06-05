package com.hugo.droidapplication;

import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class AuthenticationAcitivity extends Activity {
 public static String TAG = "AuthenticationAcitivity";
 private final static String NETWORK_ERROR = "Network error.";
 private ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		validateDevice();
		//	startActivity(new Intent(this,HomeActivity.class));
	}
	
private void validateDevice() {
		// TODO Auto-generated method stub
		new ValidateDeviceAsyncTask().execute();
	}


private class ValidateDeviceAsyncTask extends AsyncTask<String, Void, ResponseObj> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
			if(mProgressDialog!= null){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mProgressDialog = new ProgressDialog(AuthenticationAcitivity.this,ProgressDialog.THEME_HOLO_DARK);
			//mProgressDialog.setTitle("Droidvue ");
			mProgressDialog.setMessage("Authenticating Details...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();	
		}

		@Override
		protected ResponseObj doInBackground(String... params) {
			Log.d(TAG, "doInBackground");
			ResponseObj resObj = new ResponseObj();
			if (Utilities.isNetworkAvailable(getApplicationContext())) {
				HashMap<String, String> map = new HashMap<String, String>();
				String androidId = "efa4c629924f8139";
				//String androidId = Settings.Secure.getString(
				//		getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
				map.put("TagURL", "mediadevices/"+androidId);
				resObj= Utilities.callExternalApiGetMethod(
						getApplicationContext(), map);
				return resObj;
				}
				else {
				resObj.setFailResponse(100, NETWORK_ERROR);
				return resObj;
			}
		}

		@Override
		protected void onPostExecute(ResponseObj resObj) {
			super.onPostExecute(resObj);
			Log.d(TAG, "onPostExecute");
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if(resObj.getStatusCode()==200){
     				//need to save the details of result for further use
			     	//{
					//  "deviceId": 1,
					//  "clientId": 55,
					//  "clientType": "Normal",
					//  "clientTypeId": 20
					//}
				//Intent i = new Intent(AuthenticationAcitivity.this,HomeActivity.class);
				finish();
				startActivity(new Intent(AuthenticationAcitivity.this,HomeActivity.class));
				
			}
			else {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationAcitivity.this,AlertDialog.THEME_HOLO_DARK);
				// Add the buttons
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				           public void onClick(DialogInterface dialog, int id) {
				              AuthenticationAcitivity.this.finish();
				           }
				       });

				AlertDialog dialog =builder.create();
				dialog.setMessage(resObj.getsErrorMessage());
				dialog.show();
			}			
		}
	}
}

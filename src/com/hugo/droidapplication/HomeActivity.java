package com.hugo.droidapplication;

import java.util.HashMap;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {

	private static final String TAG = "HomeActivity";
	public final static String PREFS_FILE = "PREFS_FILE";
	private final static String GETMOVIEDETAILSBYCATEGORY_ACTION = "GET_MOVIE_DETAILS_BY_CATEGORY";
	private final static String GETMOVIEDETAILSBYSEARCH_ACTION = "GET_MOVIE_DETAILS_BY_SEARCH";
	private final static String GETDEVICEID_ACTION = "GET_MOVIE_DETAILS_BY_SEARCH";
	private final static String NETWORK_ERROR = "Network error.";
	private final static String INVALID_REQUEST = "INVALID_REQUEST";

	private SharedPreferences mPrefs; 
	private ProgressDialog mProgressDialog;
	private TextView mMenuName;
	private ListView mlistView;
	private Editor mPrefsEditor;
	private EditText mSearchEt;
	private Button mSearchBtn;
	String category = "";
	int totalPageCount;
	int pageNumber;
	private String mActionName; 
	String androidId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		Log.d(TAG, "onCreate");
        
		mPrefs = getSharedPreferences(HomeActivity.PREFS_FILE, 0);
		mPrefsEditor = mPrefs.edit();
		
		//if(!validateDevice())
		//	finish();
		
		mPrefsEditor.putString(getString(R.string.menu_name),
				getString(R.string.movies));//getString(R.string.main_menu)); // set menu name
		mPrefsEditor.commit();
        
		mMenuName = (TextView) findViewById(R.id.tv_menuname);
		mSearchEt = (EditText) findViewById(R.id.search_et);
		mSearchBtn = (Button) findViewById(R.id.search_btn);
		mlistView = (ListView) findViewById(R.id.listView);

		mlistView.setDivider(null);
		mlistView.setSelector(R.color.listgrad);
		mlistView.setAdapter(new ArrayAdapter<String>(HomeActivity.this,
				R.layout.lv_item, R.id.list_content, MenuData.mMap
						.get(getString(R.string.movies))));//.get(getString(R.string.main_menu))));
        
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listview, View arg1,
					int position, long arg3) {
				mActionName = GETMOVIEDETAILSBYCATEGORY_ACTION;
				if(mSearchEt.getText().toString().length()!=0){
					mSearchEt.setText("");
				}
				mMenuName.setText(MenuData.mMap.get("Movies")[position]);
					category = (String) MenuData.movCateg.get(position);
					mPrefsEditor.putString(getString(R.string.menu_name), category);
					mPrefsEditor.commit();
					pageNumber = 0;
					getDetails();
					return;
			}
		});
		mlistView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> listview, View arg1,
					int position, long arg3) {

				// TODO Auto-generated method stub
				mActionName = GETMOVIEDETAILSBYCATEGORY_ACTION;
				if(mSearchEt.getText().toString().length()!=0){
					mSearchEt.setText("");
				}
				mMenuName.setText(MenuData.mMap.get("Movies")[position]);
					category = (String) MenuData.movCateg.get(position);
					mPrefsEditor.putString(getString(R.string.menu_name), category);
					mPrefsEditor.commit();
					pageNumber = 0;
					getDetails();
					return;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		((Button) findViewById(R.id.lf_button))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (pageNumber > 0 && (category.length() != 0))
						{
							pageNumber = pageNumber - 1; 
							getDetails();
						}
							
					}
				});
		((Button) findViewById(R.id.rt_button))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (pageNumber < totalPageCount - 1
								&& (category.length() != 0))
						{
							pageNumber = pageNumber + 1;
							getDetails();
						}
							
					}
				});
	}
	
	public void onSearchButtonClick(View v) {
		mActionName = GETMOVIEDETAILSBYSEARCH_ACTION;
		pageNumber = 0;
		category = mSearchEt.getText().toString();
		mMenuName.setText("Search Movies");
		getDetails();
		
		
	}
		

	public void getDetails() {
		Log.d(TAG, "getDetails");
		try {

			new GetDetailsAsynTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class GetDetailsAsynTask extends AsyncTask<String, Void, ResponseObj> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
			if(mProgressDialog!= null){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mProgressDialog = new ProgressDialog(HomeActivity.this,ProgressDialog.THEME_HOLO_DARK);
			//mProgressDialog.setTitle("Droidvue ");
			mProgressDialog.setMessage("Retrieving Details...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();	
		}

		@Override
		protected ResponseObj doInBackground(String... params) {
			Log.d(TAG, "doInBackground");
			ResponseObj resObj = new ResponseObj();
			if (Utilities.isNetworkAvailable(getApplicationContext())) {
				if(mActionName.equalsIgnoreCase(GETMOVIEDETAILSBYCATEGORY_ACTION)){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TagURL", "assets");
				map.put("filterType", category);
				map.put("pageNo", pageNumber+"");

				resObj= Utilities.callExternalApiGetMethod(
						getApplicationContext(), map);
				return resObj;
				}
				else if(mActionName.equalsIgnoreCase(GETMOVIEDETAILSBYSEARCH_ACTION)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("TagURL", "assets");
					map.put("filterType", category);
					map.put("pageNo", pageNumber+"");

					resObj = Utilities.callExternalApiGetMethod(
							getApplicationContext(), map);
					return resObj;
					}
				else if(mActionName.equalsIgnoreCase(GETDEVICEID_ACTION)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("TagURL", "mediadevices/efa4c629924f8139");
				//	map.put("TagURL", "mediadevices/"+androidId);
					resObj =  Utilities.callExternalApiGetMethod(
							getApplicationContext(), map);
					return resObj;
					}
				else{
					
					resObj.setFailResponse(100, INVALID_REQUEST);
					return resObj;
				}
			} else {
				resObj.setFailResponse(100, NETWORK_ERROR);
				return resObj;
			}
		}

		@Override
		protected void onPostExecute(ResponseObj resObj) {
			super.onPostExecute(resObj);
			Log.d(TAG, "onPostExecute");
			if(resObj.getStatusCode()==200){
     				updateDetails(resObj.getsResponse());
     				if (mProgressDialog.isShowing()) {
    					mProgressDialog.dismiss();
    				}
			}
			else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,AlertDialog.THEME_HOLO_DARK);
				// Add the buttons
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { //R.string.ok
				           public void onClick(DialogInterface dialog, int id) {
				              //doing nothing
				           }
				       });

				AlertDialog dialog =builder.create();
				dialog.setMessage(resObj.getsErrorMessage());
				/*TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);*/
				dialog.show();
			}			
		}
	}
	public void updateDetails(String result) {
		Log.d(TAG, "updateDetails" + result);
		if (result != null) {
			final GridViewData gvDataObj = MovieEngine
					.parseMovieDetails(result);
			totalPageCount = gvDataObj.getPageCount();
			pageNumber = gvDataObj.getPageNumber();
		    TextView page_no = (TextView)findViewById(R.id.home_page_no);
		    page_no.setText((pageNumber+1)+"/"+totalPageCount);
			final GridView gridView = (GridView) findViewById(R.id.grid_view);
			gridView.setAdapter(new CustomGridViewAdapter(gvDataObj
					.getMovieListObj(), this));
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> listview, View imageVw,
						int position, long arg3) {
					Intent intent = new Intent(HomeActivity.this,
							MovieDetailsActivity.class);
					MovieObj movObj = gvDataObj.getMovieListObj().get(position);
					intent.putExtra("MediaId", movObj.getId() + "");
					intent.putExtra("EventId", movObj.getEventId() + "");
					startActivity(intent);
				}
			});
			gridView.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(arg1!=null){
					arg1.startAnimation(AnimationUtils.loadAnimation(
							HomeActivity.this, R.anim.zoom_selection));
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			findViewById(R.id.srch_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.frag_rel_layout).setVisibility(View.VISIBLE);
			
			Button left_btn = (Button) findViewById(R.id.lf_button);
			Button right_btn = (Button) findViewById(R.id.rt_button);
			if(pageNumber==0 && pageNumber==totalPageCount - 1)
			{
				left_btn.setVisibility(View.INVISIBLE);
				right_btn.setVisibility(View.INVISIBLE);
			}
			else if(pageNumber==0)
			{
				left_btn.setVisibility(View.INVISIBLE);
				right_btn.setVisibility(View.VISIBLE);
			}
			else if(pageNumber==totalPageCount - 1)
			{
				left_btn.setVisibility(View.VISIBLE);
				right_btn.setVisibility(View.INVISIBLE);
			}
			else if((left_btn.getVisibility()==View.INVISIBLE) && (right_btn.getVisibility()==View.INVISIBLE))
			{
				left_btn.setVisibility(View.VISIBLE);
				right_btn.setVisibility(View.VISIBLE);
			}			
			else if(left_btn.getVisibility()==View.INVISIBLE)
			{
				left_btn.setVisibility(View.VISIBLE);
			}
			else if(right_btn.getVisibility()==View.INVISIBLE)
			{
				right_btn.setVisibility(View.VISIBLE);
			}

		}
	}
   /* private boolean validateDevice(){
    	
    	androidId = Settings.Secure.getString(HomeActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
    	mPrefsEditor.putString("ANDROID_ID", androidId);
    	String json="";
		mActionName = GETDEVICEID_ACTION;
		try {
			json = new GetDetailsAsynTask().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(json.length()!=0){
			JSONObject deviceDtls;
			try {
				deviceDtls = new JSONObject(json);
				mPrefsEditor.putString("deviceId",deviceDtls.getString("deviceId"));
				mPrefsEditor.putString("clientId",deviceDtls.getString("clientId"));
				mPrefsEditor.putString("clientType",deviceDtls.getString("clientType"));
				mPrefsEditor.putString("clientTypeId",deviceDtls.getString("clientTypeId"));
				mPrefsEditor.commit();
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
    }*/

}
